package com.vijay.User_Master.controller;

import com.vijay.User_Master.service.manager.AtRiskStudentAgentManager;
import com.vijay.User_Master.service.manager.AdvancedTutorAgentManager;
import com.vijay.User_Master.service.manager.PeerReviewAgentManager;
import com.vijay.User_Master.service.manager.AdmissionsFunnelManager;
import com.vijay.User_Master.service.manager.ExamLifecycleManager;
import com.vijay.User_Master.service.manager.FeeRecoveryManager;
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
    private final ExamLifecycleManager examLifecycleManager;
    private final FeeRecoveryManager feeRecoveryManager;

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

    // ===================== EXAM LIFECYCLE =====================

    @PostMapping("/exams/start")
    public String examStart(
            @RequestParam Long examId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long rubricId) {
        return examLifecycleManager.startExamLifecycle(examId, classId, subjectId, rubricId);
    }

    @PostMapping("/exams/reminder")
    public String examReminder(@RequestParam String runId) {
        return examLifecycleManager.sendExamReminder(runId);
    }

    @PostMapping("/exams/collect-submissions")
    public String examCollectSubmissions(
            @RequestParam String runId,
            @RequestParam String submissionIdsCsv) {
        return examLifecycleManager.collectSubmissions(runId, submissionIdsCsv);
    }

    @PostMapping("/exams/grade-batch")
    public String examGradeBatch(@RequestParam String runId) {
        return examLifecycleManager.aiGradeBatch(runId);
    }

    @PostMapping("/exams/publish")
    public String examPublish(@RequestParam String runId) {
        return examLifecycleManager.publishResults(runId);
    }

    @PostMapping("/exams/notify-parents")
    public String examNotifyParents(
            @RequestParam String runId,
            @RequestParam(required = false) String studentIdsCsv) {
        return examLifecycleManager.notifyParents(runId, studentIdsCsv);
    }

    @GetMapping("/exams/state")
    public String examGetState(@RequestParam String runId) {
        return examLifecycleManager.getRunState(runId);
    }

    // ===================== FEE RECOVERY =====================

    @PostMapping("/fees/recovery/start")
    public String feesStart(@RequestParam(required = false) Long studentId) {
        return feeRecoveryManager.startFeeRecovery(studentId);
    }

    @PostMapping("/fees/recovery/reminder")
    public String feesSendReminder(@RequestParam String runId, @RequestParam String stage) {
        return feeRecoveryManager.sendReminder(runId, stage);
    }

    @PostMapping("/fees/recovery/plan")
    public String feesDecidePlan(
            @RequestParam String runId,
            @RequestParam(required = false) Boolean installmentPlan,
            @RequestParam(required = false) Integer installmentCount,
            @RequestParam(required = false) Double waiverAmount,
            @RequestParam(required = false) String waiverReason) {
        return feeRecoveryManager.decidePlan(runId, installmentPlan, installmentCount, waiverAmount, waiverReason);
    }

    @PostMapping("/fees/recovery/mark-payment")
    public String feesMarkPayment(
            @RequestParam String runId,
            @RequestParam Long feeId,
            @RequestParam Double amount,
            @RequestParam String method,
            @RequestParam(required = false) String transactionId) {
        return feeRecoveryManager.markPayment(runId, feeId, amount, method, transactionId);
    }

    @GetMapping("/fees/recovery/state")
    public String feesGetState(@RequestParam String runId) {
        return feeRecoveryManager.getRunState(runId);
    }
}
