package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.ExamService;
import com.vijay.User_Master.service.GradeService;
import com.vijay.User_Master.service.SchoolNotificationService;
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
public class ExamLifecycleManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final ExamService examService;
    private final GradeService gradeService;
    private final AIGradingService aiGradingService;
    private final SchoolNotificationService schoolNotificationService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class ExamState {
        private Long examId;
        private Long classId;
        private Long subjectId;
        private Long rubricId;
        private List<Long> submissionIds;
        private Integer gradedCount;
        private Boolean resultsPublished;
        private Boolean scheduleNotified;
        private Boolean reminderSent;
        private Boolean parentsNotified;
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
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize step: {}", e.getMessage());
        }
    }

    private void persistState(AgentRun run, ExamState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private ExamState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return ExamState.builder().build();
            return mapper.readValue(run.getStateJson(), ExamState.class);
        } catch (Exception e) { return ExamState.builder().build(); }
    }

    // ============== Tools / Orchestration ==============

    @Tool(description = "Start Exam lifecycle run. Inputs: examId (Long), classId (Long optional), subjectId (Long optional), rubricId (Long optional). Returns runId.")
    public String startExamLifecycle(Long examId, Long classId, Long subjectId, Long rubricId) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("ExamLifecycle")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("plan_schedule")
                .build();
        ExamState state = ExamState.builder()
                .examId(examId)
                .classId(classId)
                .subjectId(subjectId)
                .rubricId(rubricId)
                .submissionIds(new ArrayList<>())
                .gradedCount(0)
                .resultsPublished(false)
                .scheduleNotified(false)
                .reminderSent(false)
                .parentsNotified(false)
                .build();
        agentRunRepository.save(run);
        persistState(run, state, "plan_schedule", "RUNNING");
        // notify schedule immediately
        try { schoolNotificationService.sendExamScheduleNotification(examId); state.setScheduleNotified(true);} catch (Exception ignored) {}
        persistState(run, state, "notify_students_parents", "RUNNING");
        saveStep(run, "notify_students_parents", Map.of("examId", examId), Map.of("scheduleNotified", state.getScheduleNotified()), "OK", null);
        return runId;
    }

    @Tool(description = "Send exam reminder (1 day before). Inputs: runId.")
    public String sendExamReminder(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ExamState state = readState(run);
        try { schoolNotificationService.sendExamReminder(state.getExamId()); state.setReminderSent(true);} catch (Exception e) { saveStep(run, "send_reminder", Map.of(), Map.of(), "ERROR", e.getMessage()); return "Failed to send reminder: "+e.getMessage(); }
        persistState(run, state, "conduct_exam_window", "WAITING");
        saveStep(run, "send_reminder", Map.of("examId", state.getExamId()), Map.of("reminderSent", true), "OK", null);
        return "Reminder sent";
    }

    @Tool(description = "Collect submissions for grading. Inputs: runId, submissionIds (comma-separated). Returns count.")
    public String collectSubmissions(String runId, String submissionIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ExamState state = readState(run);
        List<Long> ids = new ArrayList<>();
        if (submissionIdsCsv != null && !submissionIdsCsv.isBlank()) {
            for (String p : submissionIdsCsv.split(",")) { try { ids.add(Long.parseLong(p.trim())); } catch (Exception ignored) {} }
        }
        if (state.getSubmissionIds() == null) state.setSubmissionIds(new ArrayList<>());
        state.getSubmissionIds().addAll(ids);
        persistState(run, state, "collect_submissions", "RUNNING");
        saveStep(run, "collect_submissions", Map.of("submissionIds", ids), Map.of("total", state.getSubmissionIds().size()), "OK", null);
        return "Collected submissions: "+ state.getSubmissionIds().size();
    }

    @Tool(description = "AI grade batch submissions. Inputs: runId. Uses rubricId if present. Returns graded count.")
    public String aiGradeBatch(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ExamState state = readState(run);
        if (state.getSubmissionIds() == null || state.getSubmissionIds().isEmpty()) return "No submissions to grade";
        try {
            List<AIGradingResponse> list = aiGradingService.batchGradeSubmissions(state.getSubmissionIds(), state.getRubricId(), run.getOwnerId());
            int count = list != null ? list.size() : 0;
            state.setGradedCount(count);
            persistState(run, state, "grade_batch", "RUNNING");
            saveStep(run, "grade_batch", Map.of("submissions", state.getSubmissionIds()), Map.of("gradedCount", count), "OK", null);
            return "Graded submissions: "+count;
        } catch (Exception e) {
            saveStep(run, "grade_batch", Map.of("submissions", state.getSubmissionIds()), Map.of(), "ERROR", e.getMessage());
            return "Failed to grade: "+e.getMessage();
        }
    }

    @Tool(description = "Publish exam results via ExamService. Inputs: runId. Returns status.")
    public String publishResults(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ExamState state = readState(run);
        try {
            examService.publishExamResults(state.getExamId(), run.getOwnerId());
            state.setResultsPublished(true);
            persistState(run, state, "publish_results", "RUNNING");
            saveStep(run, "publish_results", Map.of("examId", state.getExamId()), Map.of("published", true), "OK", null);
            return "Results published";
        } catch (Exception e) {
            saveStep(run, "publish_results", Map.of("examId", state.getExamId()), Map.of(), "ERROR", e.getMessage());
            return "Failed to publish: "+e.getMessage();
        }
    }

    @Tool(description = "Notify parents about results. Inputs: runId, studentIdsCsv (optional). If not provided, assumes class-level distribution managed elsewhere.")
    public String notifyParents(String runId, String studentIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        ExamState state = readState(run);
        List<Long> studentIds = new ArrayList<>();
        if (studentIdsCsv != null && !studentIdsCsv.isBlank()) {
            for (String s : studentIdsCsv.split(",")) { try { studentIds.add(Long.parseLong(s.trim())); } catch (Exception ignored) {} }
        }
        int notified = 0;
        for (Long sid : studentIds) {
            try { schoolNotificationService.sendExamResultNotification(state.getExamId(), sid); notified++; } catch (Exception ignored) {}
        }
        state.setParentsNotified(notified > 0);
        persistState(run, state, "notify_parents", "RUNNING");
        saveStep(run, "notify_parents", Map.of("studentCount", studentIds.size()), Map.of("notified", notified), "OK", null);
        return "Parents notified: "+notified;
    }

    @Tool(name = "examsGetRunState", description = "Get Exam lifecycle run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
