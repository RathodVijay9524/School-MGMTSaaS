package com.vijay.User_Master.controller;

import com.vijay.User_Master.service.manager.AtRiskStudentAgentManager;
import com.vijay.User_Master.service.manager.AdvancedTutorAgentManager;
import com.vijay.User_Master.service.manager.PeerReviewAgentManager;
import com.vijay.User_Master.service.manager.AdmissionsFunnelManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/manager-agents")
@RequiredArgsConstructor
public class ManagerAgentController {

    private final AtRiskStudentAgentManager atRiskStudentAgentManager;
    private final AdvancedTutorAgentManager advancedTutorAgentManager;
    private final PeerReviewAgentManager peerReviewAgentManager;
    private final AdmissionsFunnelManager admissionsFunnelManager;

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

    // ===================== ADMISSIONS FUNNEL =====================

    @PostMapping("/admissions/start")
    public String admissionsStart(
            @RequestParam String applicantName,
            @RequestParam String applicantEmail,
            @RequestParam String gradeApplied,
            @RequestParam(required = false) String parentName,
            @RequestParam(required = false) String parentEmail) {
        return admissionsFunnelManager.startAdmissions(applicantName, applicantEmail, gradeApplied, parentName, parentEmail);
    }

    @PostMapping("/admissions/documents")
    public String admissionsDocuments(
            @RequestParam String runId,
            @RequestParam String documentIdsCsv) {
        return admissionsFunnelManager.submitDocuments(runId, documentIdsCsv);
    }

    @PostMapping("/admissions/schedule-interview")
    public String admissionsScheduleInterview(
            @RequestParam String runId,
            @RequestParam String slot) {
        return admissionsFunnelManager.scheduleInterview(runId, slot);
    }

    @PostMapping("/admissions/submit-interview")
    public String admissionsSubmitInterview(
            @RequestParam String runId,
            @RequestParam Double score,
            @RequestParam(required = false) String notes) {
        return admissionsFunnelManager.submitInterviewFeedback(runId, score, notes);
    }

    @PostMapping("/admissions/decision")
    public String admissionsDecision(
            @RequestParam String runId,
            @RequestParam Boolean approved) {
        return admissionsFunnelManager.finalDecision(runId, approved);
    }

    @PostMapping("/admissions/initiate-fee")
    public String admissionsInitiateFee(
            @RequestParam String runId,
            @RequestParam Double amount) {
        return admissionsFunnelManager.initiateFee(runId, amount);
    }

    @PostMapping("/admissions/mark-payment")
    public String admissionsMarkPayment(
            @RequestParam String runId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false, defaultValue = "ONLINE") String method) {
        return admissionsFunnelManager.markPaymentCaptured(runId, transactionId, method);
    }

    @PostMapping("/admissions/onboard")
    public String admissionsOnboard(
            @RequestParam String runId) {
        return admissionsFunnelManager.onboardStudent(runId);
    }

    @PostMapping("/admissions/onboard-with-class")
    public String admissionsOnboardWithClass(
            @RequestParam String runId,
            @RequestParam Long classId) {
        return admissionsFunnelManager.onboardStudentWithClass(runId, classId);
    }

    @GetMapping("/admissions/state")
    public String admissionsGetState(@RequestParam String runId) {
        return admissionsFunnelManager.getRunState(runId);
    }
}
