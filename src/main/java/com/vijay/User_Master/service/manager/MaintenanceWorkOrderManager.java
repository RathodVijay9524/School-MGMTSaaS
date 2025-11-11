package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.chat.AiToolProvider;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class MaintenanceWorkOrderManager implements AiToolProvider {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class WOState {
        private Long ticketId;
        private String title;
        private String description;
        private String stage; // CREATED, APPROVED, ASSIGNED, COMPLETED, CANCELLED
        private Double costEstimate;
        private Long assigneeUserId;
        private Boolean approved;
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

    private void persistState(AgentRun run, WOState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private WOState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return WOState.builder().build();
            return mapper.readValue(run.getStateJson(), WOState.class);
        } catch (Exception e) { return WOState.builder().build(); }
    }

    @Tool(name = "maintenanceStart", description = "Start a maintenance work order. Inputs: title, description, costEstimate?. Returns runId.")
    public String start(String title, String description, Double costEstimate) {
        if (title == null || title.isBlank()) return "Title is required";
        if (description == null || description.isBlank()) return "Description is required";
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("MaintenanceWorkOrder")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("create_ticket")
                .build();
        agentRunRepository.save(run);

        WOState state = WOState.builder()
                .ticketId(System.currentTimeMillis())
                .title(title)
                .description(description)
                .stage("CREATED")
                .costEstimate(costEstimate != null ? costEstimate : 0.0)
                .approved(false)
                .completed(false)
                .timestamps(new HashMap<>())
                .build();
        state.getTimestamps().put("CREATED", LocalDateTime.now().toString());
        persistState(run, state, "create_ticket", "RUNNING");
        saveStep(run, "create_ticket", Map.of("title", title), Map.of("ticketId", state.getTicketId()), "OK", null);
        return runId;
    }

    @Tool(name = "maintenanceApprove", description = "Approve the work order. Inputs: runId, approverUserId. Returns status.")
    public String approve(String runId, Long approverUserId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        WOState state = readState(run);
        if (Boolean.TRUE.equals(state.getApproved())) return "Already approved";
        state.setApproved(true);
        state.setStage("APPROVED");
        state.getTimestamps().put("APPROVED", LocalDateTime.now().toString());
        persistState(run, state, "approve", "RUNNING");
        saveStep(run, "approve", Map.of("approver", approverUserId), Map.of("approved", true), "OK", null);
        return "Approved";
    }

    @Tool(name = "maintenanceAssign", description = "Assign to technician. Inputs: runId, assigneeUserId. Returns status.")
    public String assign(String runId, Long assigneeUserId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        WOState state = readState(run);
        if (!Boolean.TRUE.equals(state.getApproved())) return "Cannot assign: not approved";
        if (state.getAssigneeUserId() != null) return "Already assigned";
        state.setAssigneeUserId(assigneeUserId);
        state.setStage("ASSIGNED");
        state.getTimestamps().put("ASSIGNED", LocalDateTime.now().toString());
        persistState(run, state, "assign", "RUNNING");
        saveStep(run, "assign", Map.of("assigneeUserId", assigneeUserId), Map.of("ok", true), "OK", null);
        return "Assigned";
    }

    @Tool(name = "maintenanceComplete", description = "Complete the work order. Inputs: runId. Returns status.")
    public String complete(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        WOState state = readState(run);
        if (Boolean.TRUE.equals(state.getCompleted())) return "Already completed";
        if (!"ASSIGNED".equalsIgnoreCase(state.getStage()) && !Boolean.TRUE.equals(state.getApproved())) {
            return "Cannot complete: not in progress";
        }
        state.setCompleted(true);
        state.setStage("COMPLETED");
        state.getTimestamps().put("COMPLETED", LocalDateTime.now().toString());
        persistState(run, state, "complete", "COMPLETED");
        saveStep(run, "complete", Map.of(), Map.of("completed", true), "OK", null);
        return "Completed";
    }

    @Tool(name = "maintenanceGetRunState", description = "Get work order run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
