package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.PeerReviewAssignmentRequest;
import com.vijay.User_Master.dto.PeerReviewAssignmentResponse;
import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.PeerReviewService;
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
public class AssignmentLifecycleManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final PeerReviewService peerReviewService;
    private final AIGradingService aiGradingService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class AssignmentState {
        private Long assignmentId;
        private Integer reviewsPerSubmission;
        private Long rubricId;
        private Boolean randomAssignment;
        private Boolean allowSelfReview;
        private Boolean anonymousReview;
        private List<Long> submissionIds;
        private Integer gradedCount;
        private Boolean gateWaiting;
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
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize step: {}", e.getMessage());
        }
    }

    private void persistState(AgentRun run, AssignmentState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private AssignmentState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return AssignmentState.builder().build();
            return mapper.readValue(run.getStateJson(), AssignmentState.class);
        } catch (Exception e) { return AssignmentState.builder().build(); }
    }

    @Tool(description = "Start Assignment lifecycle. Inputs: assignmentId, reviewsPerSubmission, rubricId?, randomAssignment?, allowSelfReview?, anonymousReview?. Returns runId.")
    public String startAssignmentLifecycle(Long assignmentId, Integer reviewsPerSubmission, Long rubricId, Boolean randomAssignment, Boolean allowSelfReview, Boolean anonymousReview) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("AssignmentLifecycle")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("publish_assignment")
                .build();
        AssignmentState state = AssignmentState.builder()
                .assignmentId(assignmentId)
                .reviewsPerSubmission(reviewsPerSubmission != null ? reviewsPerSubmission : 3)
                .rubricId(rubricId)
                .randomAssignment(randomAssignment != null ? randomAssignment : true)
                .allowSelfReview(allowSelfReview != null ? allowSelfReview : false)
                .anonymousReview(anonymousReview != null ? anonymousReview : true)
                .submissionIds(new ArrayList<>())
                .gradedCount(0)
                .gateWaiting(false)
                .completed(false)
                .build();
        agentRunRepository.save(run);

        // Assign peer reviews immediately
        try {
            PeerReviewAssignmentRequest req = new PeerReviewAssignmentRequest();
            req.setAssignmentId(assignmentId);
            req.setReviewsPerSubmission(state.getReviewsPerSubmission());
            req.setRubricId(rubricId);
            req.setRandomAssignment(state.getRandomAssignment());
            req.setAllowSelfReview(state.getAllowSelfReview());
            req.setAnonymousReview(state.getAnonymousReview());
            PeerReviewAssignmentResponse resp = peerReviewService.assignPeerReviews(req, ownerId);
            persistState(run, state, "peer_review_round", "RUNNING");
            saveStep(run, "peer_review_round", req, resp, "OK", null);
        } catch (Exception e) {
            saveStep(run, "peer_review_round", Map.of("assignmentId", assignmentId), Map.of(), "ERROR", e.getMessage());
        }
        return runId;
    }

    @Tool(name = "assignmentsCollectSubmissions", description = "Collect submissions for assignment. Inputs: runId, submissionIdsCsv. Returns total count.")
    public String collectSubmissions(String runId, String submissionIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AssignmentState state = readState(run);
        List<Long> ids = new ArrayList<>();
        if (submissionIdsCsv != null && !submissionIdsCsv.isBlank()) {
            for (String p : submissionIdsCsv.split(",")) { try { ids.add(Long.parseLong(p.trim())); } catch (Exception ignored) {} }
        }
        if (state.getSubmissionIds() == null) state.setSubmissionIds(new ArrayList<>());
        state.getSubmissionIds().addAll(ids);
        persistState(run, state, "collect_submissions", "RUNNING");
        saveStep(run, "collect_submissions", Map.of("submissions", ids), Map.of("total", state.getSubmissionIds().size()), "OK", null);
        return "Collected submissions: " + state.getSubmissionIds().size();
    }

    @Tool(name = "assignmentsAiGradeBatch", description = "AI grade submissions (includes cheating/plagiarism checks handled inside service). Inputs: runId. Returns graded count.")
    public String aiGradeBatch(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AssignmentState state = readState(run);
        if (state.getSubmissionIds() == null || state.getSubmissionIds().isEmpty()) return "No submissions to grade";
        try {
            List<AIGradingResponse> list = aiGradingService.batchGradeSubmissions(state.getSubmissionIds(), state.getRubricId(), run.getOwnerId());
            int count = list != null ? list.size() : 0;
            state.setGradedCount(count);
            persistState(run, state, "ai_feedback_generation", "RUNNING");
            saveStep(run, "ai_feedback_generation", Map.of("submissions", state.getSubmissionIds()), Map.of("gradedCount", count), "OK", null);
            return "Graded submissions: "+count;
        } catch (Exception e) {
            saveStep(run, "ai_feedback_generation", Map.of("submissions", state.getSubmissionIds()), Map.of(), "ERROR", e.getMessage());
            return "Failed to grade: "+e.getMessage();
        }
    }

    @Tool(description = "Teacher review gate. Inputs: runId. Marks gate as WAITING for manual review.")
    public String teacherReviewGate(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AssignmentState state = readState(run);
        state.setGateWaiting(true);
        persistState(run, state, "teacher_review_gate", "WAITING");
        saveStep(run, "teacher_review_gate", Map.of(), Map.of("waiting", true), "OK", null);
        return "Teacher review gate set to WAITING";
    }

    @Tool(description = "Publish grades and finish flow (assumes teacher review done externally). Inputs: runId.")
    public String publishGrades(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AssignmentState state = readState(run);
        state.setCompleted(true);
        persistState(run, state, "publish_grades", "COMPLETED");
        saveStep(run, "publish_grades", Map.of(), Map.of("completed", true), "OK", null);
        return "Assignment lifecycle completed";
    }

    @Tool(name = "assignmentsGetRunState", description = "Get Assignment lifecycle run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
