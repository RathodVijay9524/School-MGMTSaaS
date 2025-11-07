package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class IDCardIssuanceManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class IDState {
        private String batchName;
        private List<Long> studentIds;
        private Integer rendered; // count
        private Integer printed;  // count
        private Integer distributed; // count
        private Boolean completed;
        private Map<String, String> timestamps;
    }

    private String newRunId() { return UUID.randomUUID().toString().replace("-", ""); }

    private void saveStep(AgentRun run, String node, Object input, Object output, String status, String error) {
        try {
            AgentStep step = AgentStep.builder()
                    .agentRun(run)
                    .nodeName(node)
                    .inputJson(input != null ? mapper.writeValueAsString(input) : null)
                    .outputJson(output != null ? mapper.writeValueAsString(output) : null)
                    .status(status)
                    .error(error)
                    .startedAt(LocalDateTime.now())
                    .finishedAt(LocalDateTime.now())
                    .build();
            agentStepRepository.save(step);
        } catch (Exception e) {
            log.warn("Failed to serialize step: {}", e.getMessage());
        }
    }

    private void persistState(AgentRun run, IDState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private IDState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return IDState.builder().build();
            return mapper.readValue(run.getStateJson(), IDState.class);
        } catch (Exception e) { return IDState.builder().build(); }
    }

    @Tool(name = "idcardsStartBatch", description = "Start ID card issuance batch. Inputs: batchName. Returns runId.")
    public String start(String batchName) {
        if (batchName == null || batchName.isBlank()) return "batchName is required";
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("IDCardIssuance")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("start_batch")
                .build();
        agentRunRepository.save(run);

        IDState state = IDState.builder()
                .batchName(batchName)
                .studentIds(new ArrayList<>())
                .rendered(0)
                .printed(0)
                .distributed(0)
                .completed(false)
                .timestamps(new HashMap<>())
                .build();
        state.getTimestamps().put("STARTED", LocalDateTime.now().toString());
        persistState(run, state, "start_batch", "RUNNING");
        saveStep(run, "start_batch", Map.of("batchName", batchName), Map.of("ok", true), "OK", null);
        return runId;
    }

    @Tool(name = "idcardsIngestStudents", description = "Ingest student IDs for the batch. Inputs: runId, studentIdsCsv. Returns total candidates.")
    public String ingestStudents(String runId, String studentIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        if (studentIdsCsv == null || studentIdsCsv.isBlank()) return "studentIdsCsv is required";
        IDState state = readState(run);
        List<Long> ids = state.getStudentIds() != null ? state.getStudentIds() : new ArrayList<>();
        if (studentIdsCsv != null && !studentIdsCsv.isBlank()) {
            for (String s : studentIdsCsv.split(",")) {
                try { ids.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        state.setStudentIds(ids);
        persistState(run, state, "ingest_students", "RUNNING");
        saveStep(run, "ingest_students", Map.of("count", ids.size()), Map.of("total", ids.size()), "OK", null);
        return "Candidates: " + ids.size();
    }

    @Tool(name = "idcardsRender", description = "Render ID cards (simulate). Inputs: runId. Returns rendered count.")
    public String render(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        IDState state = readState(run);
        if (state.getRendered() != null && state.getRendered() > 0) return "Already rendered: " + state.getRendered();
        int count = state.getStudentIds() != null ? state.getStudentIds().size() : 0;
        state.setRendered(count);
        persistState(run, state, "render", "RUNNING");
        saveStep(run, "render", Map.of(), Map.of("rendered", count), "OK", null);
        return "Rendered: " + count;
    }

    @Tool(name = "idcardsPrint", description = "Print ID cards in batches. Inputs: runId, batchSize?. Returns printed count.")
    public String print(String runId, Integer batchSize) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        IDState state = readState(run);
        if (state.getRendered() == null || state.getRendered() == 0) return "Nothing to print: not rendered";
        if (state.getPrinted() != null && state.getPrinted() > 0) return "Already printed: " + state.getPrinted();
        int total = state.getRendered() != null ? state.getRendered() : (state.getStudentIds() != null ? state.getStudentIds().size() : 0);
        int size = batchSize != null && batchSize > 0 ? batchSize : 50;
        int printed = Math.min(total, total); // simulate fully printed
        state.setPrinted(printed);
        persistState(run, state, "print", "RUNNING");
        saveStep(run, "print", Map.of("batchSize", size), Map.of("printed", printed), "OK", null);
        return "Printed: " + printed;
    }

    @Tool(name = "idcardsDistribute", description = "Distribute ID cards. Inputs: runId. Returns distributed count.")
    public String distribute(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        IDState state = readState(run);
        if (state.getDistributed() != null && state.getDistributed() > 0) return "Already distributed: " + state.getDistributed();
        if (state.getPrinted() == null || state.getPrinted() == 0) return "Nothing to distribute: not printed";
        int distributed = state.getPrinted() != null ? state.getPrinted() : 0;
        state.setDistributed(distributed);
        state.setCompleted(true);
        state.getTimestamps().put("COMPLETED", LocalDateTime.now().toString());
        persistState(run, state, "distribute", "COMPLETED");
        saveStep(run, "distribute", Map.of(), Map.of("distributed", distributed), "OK", null);
        return "Distributed: " + distributed;
    }

    @Tool(name = "idcardsGetRunState", description = "Get ID card issuance run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
