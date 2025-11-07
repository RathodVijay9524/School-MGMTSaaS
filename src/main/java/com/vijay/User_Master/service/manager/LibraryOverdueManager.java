package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.BookIssueResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.BookIssueService;
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
public class LibraryOverdueManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final BookIssueService bookIssueService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class LibraryOverdueState {
        private List<Long> overdueIssueIds;
        private List<Long> dueTodayIssueIds;
        private List<Long> extendedIssueIds;
        private List<Long> notifiedIssueIds;
        private Map<Long, Double> calculatedFines; // issueId -> fine
        private Integer autoExtendDays;
        private Boolean notificationsSent;
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

    private void persistState(AgentRun run, LibraryOverdueState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private LibraryOverdueState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return LibraryOverdueState.builder().build();
            return mapper.readValue(run.getStateJson(), LibraryOverdueState.class);
        } catch (Exception e) { return LibraryOverdueState.builder().build(); }
    }

    @Tool(name = "libraryStartOverdueRun", description = "Start Library Overdue run. Inputs: includeDueToday (Boolean), autoExtendDays (Integer). Returns runId.")
    public String startOverdueRun(Boolean includeDueToday, Integer autoExtendDays) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("LibraryOverdue")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("scan_overdue")
                .build();
        agentRunRepository.save(run);

        LibraryOverdueState state = LibraryOverdueState.builder()
                .overdueIssueIds(new ArrayList<>())
                .dueTodayIssueIds(new ArrayList<>())
                .extendedIssueIds(new ArrayList<>())
                .notifiedIssueIds(new ArrayList<>())
                .calculatedFines(new HashMap<>())
                .autoExtendDays(autoExtendDays != null ? autoExtendDays : 7)
                .notificationsSent(false)
                .completed(false)
                .build();

        try {
            List<BookIssueResponse> overdue = bookIssueService.getOverdueBooks(ownerId);
            List<Long> overdueIds = new ArrayList<>();
            for (BookIssueResponse r : overdue) { if (r.getId() != null) overdueIds.add(r.getId()); }
            state.setOverdueIssueIds(overdueIds);

            if (includeDueToday != null && includeDueToday) {
                List<BookIssueResponse> dueToday = bookIssueService.getBooksDueToday(ownerId);
                List<Long> dueTodayIds = new ArrayList<>();
                for (BookIssueResponse r : dueToday) { if (r.getId() != null) dueTodayIds.add(r.getId()); }
                state.setDueTodayIssueIds(dueTodayIds);
            }

            persistState(run, state, "scan_overdue", "RUNNING");
            saveStep(run, "scan_overdue", Map.of("includeDueToday", includeDueToday), Map.of(
                    "overdueCount", state.getOverdueIssueIds().size(),
                    "dueTodayCount", state.getDueTodayIssueIds().size()
            ), "OK", null);
        } catch (Exception e) {
            saveStep(run, "scan_overdue", Map.of("includeDueToday", includeDueToday), Map.of(), "ERROR", e.getMessage());
        }

        return runId;
    }

    @Tool(name = "libraryAutoExtendDueToday", description = "Auto-extend due-today issues. Inputs: runId, days? Uses state.autoExtendDays if null. Returns extended count.")
    public String autoExtendDueToday(String runId, Integer days) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        LibraryOverdueState state = readState(run);
        Long ownerId = run.getOwnerId();
        int extended = 0;
        int d = (days != null ? days : (state.getAutoExtendDays() != null ? state.getAutoExtendDays() : 7));
        if (state.getDueTodayIssueIds() != null) {
            for (Long id : state.getDueTodayIssueIds()) {
                try {
                    BookIssueResponse resp = bookIssueService.renewBook(id, d, ownerId);
                    if (resp != null && Objects.equals(resp.getStatus().name(), "RENEWED")) {
                        state.getExtendedIssueIds().add(id);
                        extended++;
                    }
                } catch (Exception ex) {
                    // ignore non-eligible renewals
                }
            }
        }
        persistState(run, state, "auto_extend_due_today", "RUNNING");
        saveStep(run, "auto_extend_due_today", Map.of("days", d), Map.of("extended", extended), "OK", null);
        return "Auto-extended: " + extended;
    }

    @Tool(name = "libraryApplyFines", description = "Calculate fines for overdue issues in state. Inputs: runId. Returns total fines sum.")
    public String applyFines(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        LibraryOverdueState state = readState(run);
        Long ownerId = run.getOwnerId();
        double total = 0.0;
        if (state.getOverdueIssueIds() != null) {
            for (Long id : state.getOverdueIssueIds()) {
                try {
                    Double fine = bookIssueService.calculateFine(id, ownerId);
                    if (fine != null && fine > 0) {
                        state.getCalculatedFines().put(id, fine);
                        total += fine;
                    }
                } catch (Exception ex) {
                    // ignore calculation errors per issue
                }
            }
        }
        persistState(run, state, "apply_fines", "RUNNING");
        saveStep(run, "apply_fines", Map.of(), Map.of("totalFine", total, "fineCount", state.getCalculatedFines().size()), "OK", null);
        return "Calculated total fines: " + total;
    }

    @Tool(name = "libraryNotifyBorrowers", description = "Send overdue notifications using existing service. Inputs: runId. Returns status.")
    public String notifyBorrowers(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        LibraryOverdueState state = readState(run);
        try {
            bookIssueService.sendOverdueNotifications();
            state.setNotificationsSent(true);
            persistState(run, state, "notify_borrowers", "RUNNING");
            saveStep(run, "notify_borrowers", Map.of(), Map.of("sent", true), "OK", null);
            return "Notifications sent";
        } catch (Exception e) {
            saveStep(run, "notify_borrowers", Map.of(), Map.of(), "ERROR", e.getMessage());
            return "Failed to send notifications: " + e.getMessage();
        }
    }

    @Tool(name = "libraryFinishRun", description = "Mark Library Overdue run as completed. Inputs: runId.")
    public String finishRun(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        LibraryOverdueState state = readState(run);
        state.setCompleted(true);
        persistState(run, state, "completed", "COMPLETED");
        saveStep(run, "completed", Map.of(), Map.of("completed", true), "OK", null);
        return "Library overdue process completed";
    }

    @Tool(name = "libraryGetRunState", description = "Get Library Overdue run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
