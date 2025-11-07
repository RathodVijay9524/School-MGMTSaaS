package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.EventResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.EventService;
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
public class EventTripOrchestrationManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final EventService eventService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class EventState {
        private Long eventId;
        private String audience; // display
        private Boolean requiresRegistration;
        private Integer expectedParticipants;
        private Integer registeredCount;
        private Integer rosterCount;
        private Boolean dispatched;
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

    private void persistState(AgentRun run, EventState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private EventState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return EventState.builder().build();
            return mapper.readValue(run.getStateJson(), EventState.class);
        } catch (Exception e) { return EventState.builder().build(); }
    }

    @Tool(name = "eventsStartOrchestration", description = "Start Event/Trip orchestration. Inputs: eventId. Returns runId.")
    public String startOrchestration(Long eventId) {
        if (eventId == null || eventId <= 0) return "eventId is required";
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("EventTripOrchestration")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("announce_event")
                .build();
        agentRunRepository.save(run);

        EventState state = EventState.builder()
                .eventId(eventId)
                .registeredCount(0)
                .rosterCount(0)
                .dispatched(false)
                .completed(false)
                .build();

        try {
            EventResponse er = eventService.getEventById(eventId, ownerId);
            state.setAudience(er.getAudience() != null ? er.getAudience().name() : null);
            state.setRequiresRegistration(er.isRequiresRegistration());
            state.setExpectedParticipants(er.getExpectedParticipants());
            persistState(run, state, "announce_event", "RUNNING");
            saveStep(run, "announce_event", Map.of("eventId", eventId), er, "OK", null);
        } catch (Exception e) {
            saveStep(run, "announce_event", Map.of("eventId", eventId), Map.of(), "ERROR", e.getMessage());
        }

        return runId;
    }

    @Tool(name = "eventsOpenRegistration", description = "Open registration and record current registered count. Inputs: runId. Returns count.")
    public String openRegistration(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        EventState state = readState(run);
        try {
            EventResponse er = eventService.getEventById(state.getEventId(), run.getOwnerId());
            int registered = er.getRegisteredParticipants() != null ? er.getRegisteredParticipants() : 0;
            state.setRegisteredCount(registered);
            persistState(run, state, "open_registration", "RUNNING");
            saveStep(run, "open_registration", Map.of(), Map.of("registered", registered), "OK", null);
            return "Registered: " + registered;
        } catch (Exception e) {
            saveStep(run, "open_registration", Map.of(), Map.of(), "ERROR", e.getMessage());
            return "Failed: " + e.getMessage();
        }
    }

    @Tool(name = "eventsBuildRoster", description = "Build roster based on expected vs registered. Inputs: runId, targetCount?. Returns roster count.")
    public String buildRoster(String runId, Integer targetCount) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        EventState state = readState(run);
        int expect = state.getExpectedParticipants() != null ? state.getExpectedParticipants() : 0;
        int registered = state.getRegisteredCount() != null ? state.getRegisteredCount() : 0;
        if (targetCount != null && targetCount < 0) return "targetCount must be >= 0";
        int goal = targetCount != null ? targetCount : (registered > 0 ? registered : expect);
        state.setRosterCount(goal);
        persistState(run, state, "build_roster", "RUNNING");
        saveStep(run, "build_roster", Map.of("target", targetCount), Map.of("roster", goal), "OK", null);
        return "Roster count: " + goal;
    }

    @Tool(name = "eventsDispatch", description = "Mark event/trip as dispatched (logistics ready). Inputs: runId.")
    public String dispatch(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        EventState state = readState(run);
        if (Boolean.TRUE.equals(state.getDispatched())) return "Already dispatched";
        state.setDispatched(true);
        persistState(run, state, "dispatch", "RUNNING");
        saveStep(run, "dispatch", Map.of(), Map.of("dispatched", true), "OK", null);
        return "Dispatched";
    }

    @Tool(name = "eventsPostReport", description = "Generate simple post-event report and complete run. Inputs: runId.")
    public String postReport(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        EventState state = readState(run);
        if (Boolean.TRUE.equals(state.getCompleted())) return "Already completed";
        Map<String, Object> report = Map.of(
                "eventId", state.getEventId(),
                "registered", state.getRegisteredCount(),
                "roster", state.getRosterCount(),
                "dispatched", state.getDispatched()
        );
        state.setCompleted(true);
        persistState(run, state, "post_report", "COMPLETED");
        saveStep(run, "post_report", Map.of(), report, "OK", null);
        return "Event/trip orchestration completed";
    }

    @Tool(name = "eventsGetRunState", description = "Get Event/Trip orchestration run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
