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
public class HostelAllocationManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class HostelState {
        private List<Long> hostelIds;
        private Map<Long, Integer> hostelCapacity; // hostelId -> remaining capacity
        private List<Long> candidateStudentIds;
        private Map<Long, Long> studentToHostel; // studentId -> hostelId
        private List<Long> unassigned;
        private Boolean completed;
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

    private void persistState(AgentRun run, HostelState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private HostelState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return HostelState.builder().build();
            return mapper.readValue(run.getStateJson(), HostelState.class);
        } catch (Exception e) { return HostelState.builder().build(); }
    }

    @Tool(name = "hostelStartAllocation", description = "Start hostel allocation. Inputs: hostelIdsCsv, capacitiesCsv(opt, comma-separated capacities matching hostelIds). Returns runId.")
    public String startAllocation(String hostelIdsCsv, String capacitiesCsv) {
        if (hostelIdsCsv == null || hostelIdsCsv.isBlank()) return "hostelIdsCsv is required";
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("HostelAllocation")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("init_hostels")
                .build();
        agentRunRepository.save(run);

        List<Long> hostelIds = new ArrayList<>();
        if (hostelIdsCsv != null && !hostelIdsCsv.isBlank()) {
            for (String s : hostelIdsCsv.split(",")) {
                try { hostelIds.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        if (hostelIds.isEmpty()) return "No valid hostel IDs provided";
        Map<Long, Integer> caps = new LinkedHashMap<>();
        if (capacitiesCsv != null && !capacitiesCsv.isBlank()) {
            String[] capArr = capacitiesCsv.split(",");
            for (int i = 0; i < hostelIds.size(); i++) {
                int cap = 50;
                if (i < capArr.length) {
                    try { cap = Integer.parseInt(capArr[i].trim()); } catch (NumberFormatException ignored) {}
                }
                caps.put(hostelIds.get(i), cap);
            }
        } else {
            for (Long id : hostelIds) caps.put(id, 50);
        }
        HostelState state = HostelState.builder()
                .hostelIds(hostelIds)
                .hostelCapacity(caps)
                .candidateStudentIds(new ArrayList<>())
                .studentToHostel(new LinkedHashMap<>())
                .unassigned(new ArrayList<>())
                .completed(false)
                .build();
        persistState(run, state, "init_hostels", "RUNNING");
        saveStep(run, "init_hostels", Map.of("hostels", hostelIds, "caps", caps), Map.of("ok", true), "OK", null);
        return runId;
    }

    @Tool(name = "hostelIngestStudents", description = "Ingest candidate students. Inputs: runId, studentIdsCsv. Returns total candidates.")
    public String ingestStudents(String runId, String studentIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        if (studentIdsCsv == null || studentIdsCsv.isBlank()) return "studentIdsCsv is required";
        HostelState state = readState(run);
        List<Long> ids = state.getCandidateStudentIds() != null ? state.getCandidateStudentIds() : new ArrayList<>();
        if (studentIdsCsv != null && !studentIdsCsv.isBlank()) {
            for (String s : studentIdsCsv.split(",")) {
                try { ids.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        state.setCandidateStudentIds(ids);
        persistState(run, state, "ingest_students", "RUNNING");
        saveStep(run, "ingest_students", Map.of("count", ids.size()), Map.of("total", ids.size()), "OK", null);
        return "Candidates: " + ids.size();
    }

    @Tool(name = "hostelAssignByCapacity", description = "Assign students to hostels by remaining capacity. Inputs: runId. Returns assignment summary.")
    public String assignByCapacity(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        HostelState state = readState(run);
        if (Boolean.TRUE.equals(state.getCompleted())) return "Already completed";
        Map<Long, Integer> avail = state.getHostelCapacity() != null ? new LinkedHashMap<>(state.getHostelCapacity()) : new LinkedHashMap<>();
        if (state.getStudentToHostel() == null) state.setStudentToHostel(new LinkedHashMap<>());
        int assigned = 0;
        List<Long> unassigned = new ArrayList<>();
        List<Long> candidates = state.getCandidateStudentIds() != null ? state.getCandidateStudentIds() : Collections.emptyList();
        if (candidates.isEmpty()) return "No candidates to assign";
        for (Long sid : candidates) {
            if (state.getStudentToHostel().containsKey(sid)) {
                continue; // already assigned
            }
            boolean placed = false;
            for (Map.Entry<Long, Integer> en : avail.entrySet()) {
                if (en.getValue() != null && en.getValue() > 0) {
                    state.getStudentToHostel().put(sid, en.getKey());
                    en.setValue(en.getValue() - 1);
                    assigned++;
                    placed = true;
                    break;
                }
            }
            if (!placed) unassigned.add(sid);
        }
        state.setUnassigned(unassigned);
        state.setHostelCapacity(avail);
        persistState(run, state, "assign_capacity", "RUNNING");
        Map<String, Object> out = Map.of("assigned", assigned, "unassigned", unassigned.size());
        saveStep(run, "assign_capacity", Map.of("candidates", candidates.size()), out, "OK", null);
        return "Assigned: " + assigned + ", Unassigned: " + unassigned.size();
    }

    @Tool(name = "hostelFinishAllocation", description = "Finish allocation. Inputs: runId. Returns status.")
    public String finishAllocation(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        HostelState state = readState(run);
        if (Boolean.TRUE.equals(state.getCompleted())) return "Already completed";
        state.setCompleted(true);
        persistState(run, state, "finish", "COMPLETED");
        saveStep(run, "finish", Map.of(), Map.of("completed", true, "unassigned", state.getUnassigned() != null ? state.getUnassigned().size() : 0), "OK", null);
        return "Completed";
    }

    @Tool(name = "hostelGetRunState", description = "Get hostel allocation run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
