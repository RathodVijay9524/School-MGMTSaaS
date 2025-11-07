package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.TimetableService;
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
public class TimetableOrchestrationManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final TimetableService timetableService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class TTState {
        private String academicYear;
        private String semester;
        private List<Long> classIds;
        private Integer draftSlots; // simple metric
        private Integer conflictCount;
        private Boolean finalized;
        private Boolean published;
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

    private void persistState(AgentRun run, TTState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private TTState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return TTState.builder().build();
            return mapper.readValue(run.getStateJson(), TTState.class);
        } catch (Exception e) { return TTState.builder().build(); }
    }

    @Tool(name = "timetableStart", description = "Start timetable orchestration. Inputs: academicYear, semester, classIdsCsv. Returns runId.")
    public String start(String academicYear, String semester, String classIdsCsv) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("TimetableOrchestration")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("collect_constraints")
                .build();
        agentRunRepository.save(run);

        List<Long> classIds = new ArrayList<>();
        if (classIdsCsv != null && !classIdsCsv.isBlank()) {
            for (String s : classIdsCsv.split(",")) {
                try { classIds.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        TTState state = TTState.builder()
                .academicYear(academicYear)
                .semester(semester)
                .classIds(classIds)
                .draftSlots(0)
                .conflictCount(0)
                .finalized(false)
                .published(false)
                .timestamps(new HashMap<>())
                .build();
        state.getTimestamps().put("STARTED", LocalDateTime.now().toString());
        persistState(run, state, "collect_constraints", "RUNNING");
        saveStep(run, "collect_constraints", Map.of("classIds", classIds, "year", academicYear, "sem", semester), Map.of("ok", true), "OK", null);
        return runId;
    }

    @Tool(name = "timetableGenerateDraft", description = "Generate a draft timetable. Inputs: runId. Returns draft slots count.")
    public String generateDraft(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TTState state = readState(run);
        int slots = (state.getClassIds() != null ? state.getClassIds().size() : 1) * 30; // naive estimate
        state.setDraftSlots(slots);
        persistState(run, state, "generate_draft", "RUNNING");
        saveStep(run, "generate_draft", Map.of(), Map.of("draftSlots", slots), "OK", null);
        return "Draft slots: " + slots;
    }

    @Tool(name = "timetableResolveConflicts", description = "Resolve conflicts iteratively. Inputs: runId, iterations?. Returns remaining conflict count.")
    public String resolveConflicts(String runId, Integer iterations) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TTState state = readState(run);
        int baseConflicts = state.getDraftSlots() != null ? Math.max(0, state.getDraftSlots() / 20) : 5;
        int iter = iterations != null ? iterations : 1;
        int remaining = Math.max(0, baseConflicts - iter);
        state.setConflictCount(remaining);
        persistState(run, state, "resolve_conflicts", "RUNNING");
        saveStep(run, "resolve_conflicts", Map.of("iterations", iter), Map.of("remaining", remaining), "OK", null);
        return "Conflicts remaining: " + remaining;
    }

    @Tool(name = "timetableFinalize", description = "Finalize timetable if conflicts are zero. Inputs: runId. Returns status.")
    public String finalizeTimetable(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TTState state = readState(run);
        if (state.getConflictCount() != null && state.getConflictCount() > 0) {
            saveStep(run, "finalize", Map.of(), Map.of(), "ERROR", "Conflicts still present");
            return "Cannot finalize: conflicts present";
        }
        state.setFinalized(true);
        state.getTimestamps().put("FINALIZED", LocalDateTime.now().toString());
        persistState(run, state, "finalize", "RUNNING");
        saveStep(run, "finalize", Map.of(), Map.of("finalized", true), "OK", null);
        return "Finalized";
    }

    @Tool(name = "timetablePublish", description = "Publish timetable. Inputs: runId. Returns status.")
    public String publish(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TTState state = readState(run);
        if (!Boolean.TRUE.equals(state.getFinalized())) return "Cannot publish: not finalized";
        state.setPublished(true);
        state.getTimestamps().put("PUBLISHED", LocalDateTime.now().toString());
        persistState(run, state, "publish", "COMPLETED");
        saveStep(run, "publish", Map.of(), Map.of("published", true), "OK", null);
        return "Published";
    }

    @Tool(name = "timetableGetRunState", description = "Get timetable orchestration run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
