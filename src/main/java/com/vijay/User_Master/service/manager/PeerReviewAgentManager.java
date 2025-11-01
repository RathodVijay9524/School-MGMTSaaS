package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.AIGradingRequest;
import com.vijay.User_Master.dto.PeerReviewAssignmentRequest;
import com.vijay.User_Master.dto.PeerReviewAssignmentResponse;
import com.vijay.User_Master.dto.PeerReviewResponse;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.PeerReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrates Peer Review workflow: distribute -> collect/verify -> auto grade -> synthesis.
 * Uses existing PeerReviewService and AIGradingService. No LangGraph4j dependency.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PeerReviewAgentManager {

    private final PeerReviewService peerReviewService;
    private final AIGradingService aiGradingService;

    public String runPeerReviewWorkflow(Long assignmentId,
                                        int reviewsPerSubmission,
                                        int waitDays,
                                        double lazyThreshold,
                                        boolean autoGrade,
                                        Long rubricId) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        if (assignmentId == null) return "assignmentId is required";

        // Node 1: distribute_assignments
        PeerReviewAssignmentRequest assignReq = PeerReviewAssignmentRequest.builder()
                .assignmentId(assignmentId)
                .reviewsPerSubmission(reviewsPerSubmission)
                .rubricId(rubricId)
                .randomAssignment(Boolean.TRUE)
                .anonymousReview(Boolean.TRUE)
                .allowSelfReview(Boolean.FALSE)
                .build();
        PeerReviewAssignmentResponse assignment = null;
        try {
            assignment = peerReviewService.assignPeerReviews(assignReq, ownerId);
        } catch (Exception e) {
            log.warn("assignPeerReviews failed: {}", e.getMessage());
        }

        // Node 2: collect_and_verify (no real wait; just fetch and analyze)
        List<PeerReviewResponse> reviews = Collections.emptyList();
        try {
            reviews = peerReviewService.getPeerReviewsByAssignment(assignmentId, ownerId);
        } catch (Exception e) {
            log.warn("getPeerReviewsByAssignment failed: {}", e.getMessage());
        }

        // Identify lazy reviewers by heuristic on comments length; compute lazy ratio per reviewer
        Map<Long, List<PeerReviewResponse>> byReviewer = reviews.stream()
                .filter(r -> r.getReviewerId() != null)
                .collect(Collectors.groupingBy(PeerReviewResponse::getReviewerId));
        Set<Long> lazyReviewers = new HashSet<>();
        for (Map.Entry<Long, List<PeerReviewResponse>> e : byReviewer.entrySet()) {
            long total = e.getValue().size();
            long lazy = e.getValue().stream()
                    .filter(r -> {
                        String c = r.getReviewComments();
                        if (c == null) return true;
                        String trimmed = c.trim();
                        if (trimmed.length() < 10) return true;
                        int words = trimmed.split("\\s+").length;
                        return words <= 2; // one/two word reviews
                    })
                    .count();
            double ratio = total > 0 ? (double) lazy / (double) total : 0.0;
            if (ratio >= lazyThreshold) lazyReviewers.add(e.getKey());
        }

        // Node 3: auto_grade_essays
        int gradedCount = 0;
        if (autoGrade) {
            // Prefer batch grading if available
            Set<Long> submissionIds = reviews.stream()
                    .map(PeerReviewResponse::getSubmissionId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (!submissionIds.isEmpty()) {
                try {
                    var list = aiGradingService.batchGradeSubmissions(new ArrayList<>(submissionIds), rubricId, ownerId);
                    gradedCount = list != null ? list.size() : 0;
                } catch (Exception e) {
                    log.warn("batchGradeSubmissions failed: {}", e.getMessage());
                    // Fallback to single calls
                    for (Long sid : submissionIds) {
                        try {
                            aiGradingService.gradeSubmission(AIGradingRequest.builder().submissionId(sid).rubricId(rubricId).build(), ownerId);
                            gradedCount++;
                        } catch (Exception ex) {
                            log.warn("gradeSubmission failed for {}: {}", sid, ex.getMessage());
                        }
                    }
                }
            }
        }

        // Node 4: final_grade_synthesis
        int totalReviews = reviews.size();
        int totalLazy = lazyReviewers.size();
        int totalAssignments = assignment != null && assignment.getTotalSubmissions() != null ? assignment.getTotalSubmissions() : 0;
        String report = String.format(
                "Peer Review Workflow Complete:\n- Assignment ID: %d\n- Reviews per submission: %d\n- Total submissions: %d\n- Reviews collected: %d\n- Lazy reviewers flagged: %d\n- AI auto-graded essays: %d",
                assignmentId, reviewsPerSubmission, totalAssignments, totalReviews, totalLazy, gradedCount);
        return report;
    }

    @Tool(description = "Run Peer Review workflow. Inputs: assignmentId (Long), reviewsPerSubmission (Integer, default 3), waitDays (Integer, default 2), lazyThreshold (Double, default 0.2), autoGrade (Boolean, default true), rubricId (Long, optional). Returns a synthesis report string.")
    public String runPeerReviewTool(Long assignmentId, Integer reviewsPerSubmission, Integer waitDays, Double lazyThreshold, Boolean autoGrade, Long rubricId) {
        int rps = reviewsPerSubmission != null ? reviewsPerSubmission : 3;
        int wd = waitDays != null ? waitDays : 2;
        double lt = lazyThreshold != null ? lazyThreshold : 0.2;
        boolean ag = autoGrade == null || autoGrade;
        return runPeerReviewWorkflow(assignmentId, rps, wd, lt, ag, rubricId);
    }
}
