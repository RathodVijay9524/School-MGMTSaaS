package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.chat.AiToolProvider;
import com.vijay.User_Master.dto.RouteResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.BusService;
import com.vijay.User_Master.service.RouteService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TransportRouteAllocationManager implements AiToolProvider {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final RouteService routeService;
    private final BusService busService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class TransportState {
        private List<Long> candidateStudentIds;
        private List<Long> routeIds;
        private List<Long> busIds;
        private Map<Long, Long> studentToRoute; // studentId -> routeId
        private List<Long> unassignedStudents;
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

    private void persistState(AgentRun run, TransportState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private TransportState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return TransportState.builder().build();
            return mapper.readValue(run.getStateJson(), TransportState.class);
        } catch (Exception e) { return TransportState.builder().build(); }
    }

    @Tool(name = "transportStartAllocation", description = "Start transport route allocation. Inputs: routeIdsCsv?, busIdsCsv?, ownerId(optional). Returns runId.")
    public String startAllocation(String routeIdsCsv, String busIdsCsv, Long ownerId) {
        Long resolvedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("TransportRouteAllocation")
                .ownerId(resolvedOwnerId)
                .status("RUNNING")
                .currentNode("ingest")
                .build();
        agentRunRepository.save(run);

        TransportState state = TransportState.builder()
                .candidateStudentIds(new ArrayList<>())
                .routeIds(csvToLongs(routeIdsCsv))
                .busIds(csvToLongs(busIdsCsv))
                .studentToRoute(new LinkedHashMap<>())
                .unassignedStudents(new ArrayList<>())
                .completed(false)
                .build();
        persistState(run, state, "ingest", "RUNNING");
        saveStep(run, "start_allocation", Map.of("routes", state.getRouteIds(), "buses", state.getBusIds()), Map.of(), "OK", null);
        return runId;
    }

    // Backward-compatible overload used by controllers calling with two arguments
    public String startAllocation(String routeIdsCsv, String busIdsCsv) {
        return startAllocation(routeIdsCsv, busIdsCsv, (Long) null);
    }

    @Tool(name = "transportIngestStudents", description = "Add candidate students. Inputs: runId, studentIdsCsv. Returns total candidates.")
    public String ingestStudents(String runId, String studentIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        if (studentIdsCsv == null || studentIdsCsv.isBlank()) return "studentIdsCsv is required";
        TransportState state = readState(run);
        List<Long> ids = csvToLongs(studentIdsCsv);
        if (state.getCandidateStudentIds() == null) state.setCandidateStudentIds(new ArrayList<>());
        state.getCandidateStudentIds().addAll(ids);
        persistState(run, state, "ingest_students", "RUNNING");
        saveStep(run, "ingest_students", Map.of("students", ids), Map.of("total", state.getCandidateStudentIds().size()), "OK", null);
        return "Candidates: " + state.getCandidateStudentIds().size();
    }

    @Tool(name = "transportAssignByCapacity", description = "Assign students to routes by available seats. Inputs: runId. Returns assigned count.")
    public String assignByCapacity(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TransportState state = readState(run);
        if (Boolean.TRUE.equals(state.getCompleted())) return "Already completed";
        Long ownerId = run.getOwnerId();

        // Fetch target routes
        List<RouteResponse> routes;
        try {
            if (state.getRouteIds() != null && !state.getRouteIds().isEmpty()) {
                routes = state.getRouteIds().stream().map(rid -> {
                    try { return routeService.getRouteById(rid, ownerId); } catch (Exception e) { return null; }
                }).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
            } else {
                routes = routeService.getActiveRoutes(ownerId);
            }
        } catch (Exception e) {
            saveStep(run, "assign_by_capacity", Map.of(), Map.of(), "ERROR", e.getMessage());
            return "Failed to fetch routes: " + e.getMessage();
        }

        // Track mutable available seats map (routeId -> available)
        Map<Long, Integer> avail = new LinkedHashMap<>();
        for (RouteResponse rr : routes) {
            Integer a = rr.getAvailableSeats() != null ? rr.getAvailableSeats() : 0;
            avail.put(rr.getId(), a);
        }

        int assigned = 0;
        List<Long> unassigned = new ArrayList<>();
        List<Long> candidates = state.getCandidateStudentIds() != null ? state.getCandidateStudentIds() : Collections.emptyList();
        if (candidates.isEmpty()) return "No candidates to assign";
        for (Long sid : candidates) {
            if (state.getStudentToRoute().containsKey(sid)) {
                continue; // already assigned
            }
            boolean placed = false;
            for (Map.Entry<Long, Integer> en : avail.entrySet()) {
                if (en.getValue() != null && en.getValue() > 0) {
                    state.getStudentToRoute().put(sid, en.getKey());
                    en.setValue(en.getValue() - 1);
                    assigned++;
                    placed = true;
                    break;
                }
            }
            if (!placed) unassigned.add(sid);
        }
        state.setUnassignedStudents(unassigned);
        persistState(run, state, "assign_by_capacity", "RUNNING");
        saveStep(run, "assign_by_capacity", Map.of(), Map.of("assigned", assigned, "unassigned", unassigned.size()), "OK", null);
        return "Assigned: " + assigned + ", Unassigned: " + unassigned.size();
    }

    @Tool(name = "transportFinishAllocation", description = "Mark allocation as completed. Inputs: runId.")
    public String finishAllocation(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TransportState state = readState(run);
        if (Boolean.TRUE.equals(state.getCompleted())) return "Already completed";
        state.setCompleted(true);
        persistState(run, state, "completed", "COMPLETED");
        saveStep(run, "completed", Map.of(), Map.of("completed", true), "OK", null);
        return "Transport allocation completed";
    }

    @Tool(name = "transportGetRunState", description = "Get Transport allocation run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }

    private List<Long> csvToLongs(String csv) {
        List<Long> ids = new ArrayList<>();
        if (csv == null || csv.isBlank()) return ids;
        for (String p : csv.split(",")) { try { ids.add(Long.parseLong(p.trim())); } catch (Exception ignored) {} }
        return ids;
    }
}
