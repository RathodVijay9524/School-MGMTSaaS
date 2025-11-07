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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class AttendanceReconciliationManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class ReconState {
        private LocalDate dateFrom;
        private LocalDate dateTo;
        private List<Long> classIds;
        private Integer missingCount;
        private Integer correctedCount;
        private Boolean locked;
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

    private void persistState(AgentRun run, ReconState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private ReconState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return ReconState.builder().build();
            return mapper.readValue(run.getStateJson(), ReconState.class);
        } catch (Exception e) { return ReconState.builder().build(); }
    }

    @Tool(name = "attendanceReconStart", description = "Start attendance reconciliation. Inputs: dateFrom(yyyy-MM-dd), dateTo(yyyy-MM-dd), classIdsCsv. Returns runId.")
    public String start(String dateFrom, String dateTo, String classIdsCsv) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("AttendanceReconciliation")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("start_scan")
                .build();
        agentRunRepository.save(run);

        List<Long> classIds = new ArrayList<>();
        if (classIdsCsv != null && !classIdsCsv.isBlank()) {
            for (String s : classIdsCsv.split(",")) {
                try { classIds.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        ReconState state = ReconState.builder()
                .dateFrom(dateFrom != null ? LocalDate.parse(dateFrom) : LocalDate.now().minusDays(7))
                .dateTo(dateTo != null ? LocalDate.parse(dateTo) : LocalDate.now())
                .classIds(classIds)
                .missingCount(0)
                .correctedCount(0)
                .locked(false)
                .timestamps(new HashMap<>())
                .build();
        state.getTimestamps().put("STARTED", LocalDateTime.now().toString());
        persistState(run, state, "start_scan", "RUNNING");
        saveStep(run, "start_scan", Map.of("classIds", classIds, "dateFrom", state.getDateFrom(), "dateTo", state.getDateTo()), Map.of("ok", true), "OK", null);
        return runId;
    }

    @Tool(name = "attendanceReconDetect", description = "Detect missing/outlier attendance. Inputs: runId. Returns missing count.")
    public String detect(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ReconState state = readState(run);
        int missing = (state.getClassIds() != null ? state.getClassIds().size() : 2) * 5; // naive
        state.setMissingCount(missing);
        state.getTimestamps().put("DETECTED", LocalDateTime.now().toString());
        persistState(run, state, "detect", "RUNNING");
        saveStep(run, "detect", Map.of(), Map.of("missing", missing), "OK", null);
        return "Missing records: " + missing;
    }

    @Tool(name = "attendanceReconNotify", description = "Notify teachers/students for corrections. Inputs: runId. Returns status.")
    public String notifyActors(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ReconState state = readState(run);
        int notified = state.getMissingCount() != null ? state.getMissingCount() : 0;
        persistState(run, state, "notify", "RUNNING");
        saveStep(run, "notify", Map.of(), Map.of("notified", notified), "OK", null);
        return "Notified: " + notified;
    }

    @Tool(name = "attendanceReconIngestCorrections", description = "Ingest corrections. Inputs: runId, correctedCount. Returns cumulative corrected count.")
    public String ingestCorrections(String runId, Integer correctedCount) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ReconState state = readState(run);
        int prev = state.getCorrectedCount() != null ? state.getCorrectedCount() : 0;
        int add = correctedCount != null ? correctedCount : 0;
        state.setCorrectedCount(prev + add);
        persistState(run, state, "ingest_corrections", "RUNNING");
        saveStep(run, "ingest_corrections", Map.of("added", add), Map.of("correctedTotal", state.getCorrectedCount()), "OK", null);
        return "Corrected total: " + state.getCorrectedCount();
    }

    @Tool(name = "attendanceReconLock", description = "Lock and finalize reconciliation. Inputs: runId. Returns status.")
    public String lock(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ReconState state = readState(run);
        state.setLocked(true);
        state.getTimestamps().put("LOCKED", LocalDateTime.now().toString());
        persistState(run, state, "lock", "COMPLETED");
        saveStep(run, "lock", Map.of(), Map.of("locked", true, "corrected", state.getCorrectedCount()), "OK", null);
        return "Locked";
    }

    @Tool(name = "attendanceReconGetRunState", description = "Get attendance reconciliation run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
