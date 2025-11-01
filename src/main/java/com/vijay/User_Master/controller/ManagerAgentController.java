package com.vijay.User_Master.controller;

import com.vijay.User_Master.service.manager.AtRiskStudentAgentManager;
import com.vijay.User_Master.service.manager.AdvancedTutorAgentManager;
import com.vijay.User_Master.service.manager.PeerReviewAgentManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager-agents")
@RequiredArgsConstructor
public class ManagerAgentController {

    private final AtRiskStudentAgentManager atRiskStudentAgentManager;
    private final AdvancedTutorAgentManager advancedTutorAgentManager;
    private final PeerReviewAgentManager peerReviewAgentManager;

    @PostMapping("/run/at-risk-student-analysis")
    public String runAtRiskAnalysis(
            @RequestParam Long classId,
            @RequestParam(defaultValue = "80") int attendanceThreshold,
            @RequestParam Long subjectId) {
        return atRiskStudentAgentManager.runAtRiskAnalysis(classId, attendanceThreshold, subjectId);
    }

    @PostMapping("/run/adaptive-tutor")
    public String runAdaptiveTutor(
            @RequestParam Long studentId,
            @RequestParam String skillKey,
            @RequestParam String gradeLevel,
            @RequestParam(defaultValue = "3") int maxLoops) {
        return advancedTutorAgentManager.runTutor(studentId, skillKey, gradeLevel, maxLoops);
    }

    @PostMapping("/run/peer-review")
    public String runPeerReview(
            @RequestParam Long assignmentId,
            @RequestParam(defaultValue = "3") int reviewsPerSubmission,
            @RequestParam(defaultValue = "2") int waitDays,
            @RequestParam(defaultValue = "0.2") double lazyThreshold,
            @RequestParam(defaultValue = "true") boolean autoGrade,
            @RequestParam(required = false) Long rubricId) {
        return peerReviewAgentManager.runPeerReviewWorkflow(
                assignmentId, reviewsPerSubmission, waitDays, lazyThreshold, autoGrade, rubricId);
    }
}
