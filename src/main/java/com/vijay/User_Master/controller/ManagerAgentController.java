package com.vijay.User_Master.controller;

import com.vijay.User_Master.service.manager.AtRiskStudentAgentManager;
import com.vijay.User_Master.service.manager.AdvancedTutorAgentManager;
import com.vijay.User_Master.service.manager.PeerReviewAgentManager;
import com.vijay.User_Master.service.manager.AdmissionsFunnelManager;
import com.vijay.User_Master.service.manager.ExamLifecycleManager;
import com.vijay.User_Master.service.manager.FeeRecoveryManager;
import com.vijay.User_Master.service.manager.AssignmentLifecycleManager;
import com.vijay.User_Master.service.manager.LibraryOverdueManager;
import com.vijay.User_Master.service.manager.EventTripOrchestrationManager;
import com.vijay.User_Master.service.manager.TransportRouteAllocationManager;
import com.vijay.User_Master.service.manager.TransferCertificateOrchestrationManager;
import com.vijay.User_Master.service.manager.TimetableOrchestrationManager;
import com.vijay.User_Master.service.manager.AttendanceReconciliationManager;
import com.vijay.User_Master.service.manager.NotificationCampaignManager;
import com.vijay.User_Master.service.manager.IDCardIssuanceManager;
import com.vijay.User_Master.service.manager.HostelAllocationManager;
import com.vijay.User_Master.service.manager.MaintenanceWorkOrderManager;
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
    private final AssignmentLifecycleManager assignmentLifecycleManager;
    private final LibraryOverdueManager libraryOverdueManager;
    private final EventTripOrchestrationManager eventTripOrchestrationManager;
    private final TransportRouteAllocationManager transportRouteAllocationManager;
    private final TransferCertificateOrchestrationManager transferCertificateOrchestrationManager;
    private final TimetableOrchestrationManager timetableOrchestrationManager;
    private final AttendanceReconciliationManager attendanceReconciliationManager;
    private final NotificationCampaignManager notificationCampaignManager;
    private final IDCardIssuanceManager idCardIssuanceManager;
    private final HostelAllocationManager hostelAllocationManager;
    private final MaintenanceWorkOrderManager maintenanceWorkOrderManager;

   /* @PostMapping("/run/at-risk-student-analysis")
    public String runAtRiskAnalysis(
            @RequestParam Long classId,
            @RequestParam(defaultValue = "80") int attendanceThreshold,
            @RequestParam Long subjectId) {
        return atRiskStudentAgentManager.runAtRiskAnalysis(classId, attendanceThreshold, subjectId);
    }*/

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

    // ===================== ASSIGNMENT LIFECYCLE + CHEATING REVIEW =====================

    @PostMapping("/assignments/start")
    public String assignmentStart(
            @RequestParam Long assignmentId,
            @RequestParam(defaultValue = "3") Integer reviewsPerSubmission,
            @RequestParam(required = false) Long rubricId,
            @RequestParam(required = false, defaultValue = "true") Boolean randomAssignment,
            @RequestParam(required = false, defaultValue = "false") Boolean allowSelfReview,
            @RequestParam(required = false, defaultValue = "true") Boolean anonymousReview) {
        return assignmentLifecycleManager.startAssignmentLifecycle(assignmentId, reviewsPerSubmission, rubricId, randomAssignment, allowSelfReview, anonymousReview);
    }

    @PostMapping("/assignments/collect-submissions")
    public String assignmentCollectSubmissions(@RequestParam String runId, @RequestParam String submissionIdsCsv) {
        return assignmentLifecycleManager.collectSubmissions(runId, submissionIdsCsv);
    }

    @PostMapping("/assignments/grade-batch")
    public String assignmentGradeBatch(@RequestParam String runId) {
        return assignmentLifecycleManager.aiGradeBatch(runId);
    }

    @PostMapping("/assignments/teacher-gate")
    public String assignmentTeacherGate(@RequestParam String runId) {
        return assignmentLifecycleManager.teacherReviewGate(runId);
    }

    @PostMapping("/assignments/publish")
    public String assignmentPublish(@RequestParam String runId) {
        return assignmentLifecycleManager.publishGrades(runId);
    }

    @GetMapping("/assignments/state")
    public String assignmentGetState(@RequestParam String runId) {
        return assignmentLifecycleManager.getRunState(runId);
    }

    // ===================== LIBRARY OVERDUE + AUTO-EXTEND =====================

    @PostMapping("/library/overdue/start")
    public String libraryOverdueStart(
            @RequestParam(required = false, defaultValue = "true") Boolean includeDueToday,
            @RequestParam(required = false) Integer autoExtendDays) {
        return libraryOverdueManager.startOverdueRun(includeDueToday, autoExtendDays);
    }

    @PostMapping("/library/overdue/auto-extend")
    public String libraryOverdueAutoExtend(
            @RequestParam String runId,
            @RequestParam(required = false) Integer days) {
        return libraryOverdueManager.autoExtendDueToday(runId, days);
    }

    @PostMapping("/library/overdue/apply-fines")
    public String libraryOverdueApplyFines(@RequestParam String runId) {
        return libraryOverdueManager.applyFines(runId);
    }

    @PostMapping("/library/overdue/notify")
    public String libraryOverdueNotify(@RequestParam String runId) {
        return libraryOverdueManager.notifyBorrowers(runId);
    }

    @PostMapping("/library/overdue/finish")
    public String libraryOverdueFinish(@RequestParam String runId) {
        return libraryOverdueManager.finishRun(runId);
    }

    @GetMapping("/library/overdue/state")
    public String libraryOverdueState(@RequestParam String runId) {
        return libraryOverdueManager.getRunState(runId);
    }

    // ===================== EVENT/TRIP ORCHESTRATION =====================

    @PostMapping("/events/orch/start")
    public String eventsStart(@RequestParam Long eventId) {
        return eventTripOrchestrationManager.startOrchestration(eventId);
    }

    @PostMapping("/events/orch/open-registration")
    public String eventsOpenRegistration(@RequestParam String runId) {
        return eventTripOrchestrationManager.openRegistration(runId);
    }

    @PostMapping("/events/orch/build-roster")
    public String eventsBuildRoster(@RequestParam String runId, @RequestParam(required = false) Integer targetCount) {
        return eventTripOrchestrationManager.buildRoster(runId, targetCount);
    }

    @PostMapping("/events/orch/dispatch")
    public String eventsDispatch(@RequestParam String runId) {
        return eventTripOrchestrationManager.dispatch(runId);
    }

    @PostMapping("/events/orch/post-report")
    public String eventsPostReport(@RequestParam String runId) {
        return eventTripOrchestrationManager.postReport(runId);
    }

    @GetMapping("/events/orch/state")
    public String eventsGetState(@RequestParam String runId) {
        return eventTripOrchestrationManager.getRunState(runId);
    }

    // ===================== TRANSPORT ROUTE ALLOCATION =====================

    @PostMapping("/transport/allocation/start")
    public String transportStart(
            @RequestParam(required = false) String routeIdsCsv,
            @RequestParam(required = false) String busIdsCsv) {
        return transportRouteAllocationManager.startAllocation(routeIdsCsv, busIdsCsv, null);
    }

    @PostMapping("/transport/allocation/ingest-students")
    public String transportIngestStudents(@RequestParam String runId, @RequestParam String studentIdsCsv) {
        return transportRouteAllocationManager.ingestStudents(runId, studentIdsCsv);
    }

    @PostMapping("/transport/allocation/assign-by-capacity")
    public String transportAssignByCapacity(@RequestParam String runId) {
        return transportRouteAllocationManager.assignByCapacity(runId);
    }

    @PostMapping("/transport/allocation/finish")
    public String transportFinish(@RequestParam String runId) {
        return transportRouteAllocationManager.finishAllocation(runId);
    }

    @GetMapping("/transport/allocation/state")
    public String transportState(@RequestParam String runId) {
        return transportRouteAllocationManager.getRunState(runId);
    }

    // ===================== TRANSFER CERTIFICATE ORCHESTRATION =====================

    @PostMapping("/tc/start")
    public String tcStart(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long issuedByUserId,
            @RequestParam(required = false) String reasonForLeaving,
            @RequestParam(required = false) String reasonDetails,
            @RequestParam(required = false) String lastAttendanceDate,
            @RequestParam(required = false) String academicYearOfLeaving,
            @RequestParam(required = false) String conduct,
            @RequestParam(required = false) String generalRemarks
    ) {
        return transferCertificateOrchestrationManager.start(
                studentId, issuedByUserId, reasonForLeaving, reasonDetails,
                lastAttendanceDate, academicYearOfLeaving, conduct, generalRemarks);
    }

    @PostMapping("/tc/approve")
    public String tcApprove(@RequestParam String runId, @RequestParam Long approvedByUserId) {
        return transferCertificateOrchestrationManager.approve(runId, approvedByUserId);
    }

    @PostMapping("/tc/issue")
    public String tcIssue(@RequestParam String runId) {
        return transferCertificateOrchestrationManager.issue(runId);
    }

    @PostMapping("/tc/generate-pdf")
    public String tcGeneratePdf(@RequestParam String runId) {
        return transferCertificateOrchestrationManager.generatePdf(runId);
    }

    @PostMapping("/tc/finish")
    public String tcFinish(@RequestParam String runId) {
        return transferCertificateOrchestrationManager.finish(runId);
    }

    @GetMapping("/tc/state")
    public String tcState(@RequestParam String runId) {
        return transferCertificateOrchestrationManager.getRunState(runId);
    }

    // ===================== TIMETABLE ORCHESTRATION =====================

    @PostMapping("/timetable/start")
    public String timetableStart(
            @RequestParam String academicYear,
            @RequestParam String semester,
            @RequestParam(required = false) String classIdsCsv
    ) {
        return timetableOrchestrationManager.start(academicYear, semester, classIdsCsv);
    }

    @PostMapping("/timetable/generate-draft")
    public String timetableGenerateDraft(@RequestParam String runId) {
        return timetableOrchestrationManager.generateDraft(runId);
    }

    @PostMapping("/timetable/resolve-conflicts")
    public String timetableResolveConflicts(@RequestParam String runId, @RequestParam(required = false) Integer iterations) {
        return timetableOrchestrationManager.resolveConflicts(runId, iterations);
    }

    @PostMapping("/timetable/finalize")
    public String timetableFinalize(@RequestParam String runId) {
        return timetableOrchestrationManager.finalizeTimetable(runId);
    }

    @PostMapping("/timetable/publish")
    public String timetablePublish(@RequestParam String runId) {
        return timetableOrchestrationManager.publish(runId);
    }

    @GetMapping("/timetable/state")
    public String timetableState(@RequestParam String runId) {
        return timetableOrchestrationManager.getRunState(runId);
    }

    // ===================== ATTENDANCE RECONCILIATION =====================

    @PostMapping("/attendance-recon/start")
    public String attendanceReconStart(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String classIdsCsv
    ) {
        return attendanceReconciliationManager.start(dateFrom, dateTo, classIdsCsv);
    }

    @PostMapping("/attendance-recon/detect")
    public String attendanceReconDetect(@RequestParam String runId) {
        return attendanceReconciliationManager.detect(runId);
    }

    @PostMapping("/attendance-recon/notify")
    public String attendanceReconNotify(@RequestParam String runId) {
        return attendanceReconciliationManager.notifyActors(runId);
    }

    @PostMapping("/attendance-recon/ingest-corrections")
    public String attendanceReconIngestCorrections(@RequestParam String runId, @RequestParam(required = false) Integer correctedCount) {
        return attendanceReconciliationManager.ingestCorrections(runId, correctedCount);
    }

    @PostMapping("/attendance-recon/lock")
    public String attendanceReconLock(@RequestParam String runId) {
        return attendanceReconciliationManager.lock(runId);
    }

    @GetMapping("/attendance-recon/state")
    public String attendanceReconState(@RequestParam String runId) {
        return attendanceReconciliationManager.getRunState(runId);
    }

    // ===================== NOTIFICATION CAMPAIGNS =====================

    @PostMapping("/notifications/campaign/start")
    public String notifStart(@RequestParam String campaignName,
                             @RequestParam String channel,
                             @RequestParam String audienceType) {
        return notificationCampaignManager.start(campaignName, channel, audienceType);
    }

    @PostMapping("/notifications/campaign/select-audience")
    public String notifSelectAudience(@RequestParam String runId,
                                      @RequestParam(required = false) String classIdsCsv,
                                      @RequestParam(required = false) String studentIdsCsv) {
        return notificationCampaignManager.selectAudience(runId, classIdsCsv, studentIdsCsv);
    }

    @PostMapping("/notifications/campaign/schedule")
    public String notifSchedule(@RequestParam String runId,
                                @RequestParam(required = false) String scheduledAt) {
        return notificationCampaignManager.schedule(runId, scheduledAt);
    }

    @PostMapping("/notifications/campaign/send")
    public String notifSend(@RequestParam String runId) {
        return notificationCampaignManager.send(runId);
    }

    @GetMapping("/notifications/campaign/stats")
    public String notifStats(@RequestParam String runId) {
        return notificationCampaignManager.stats(runId);
    }

    @GetMapping("/notifications/campaign/state")
    public String notifState(@RequestParam String runId) {
        return notificationCampaignManager.getRunState(runId);
    }

    // ===================== ID CARD BATCH ISSUANCE =====================

    @PostMapping("/idcards/start")
    public String idcStart(@RequestParam String batchName) {
        return idCardIssuanceManager.start(batchName);
    }

    @PostMapping("/idcards/ingest-students")
    public String idcIngest(@RequestParam String runId, @RequestParam String studentIdsCsv) {
        return idCardIssuanceManager.ingestStudents(runId, studentIdsCsv);
    }

    @PostMapping("/idcards/render")
    public String idcRender(@RequestParam String runId) {
        return idCardIssuanceManager.render(runId);
    }

    @PostMapping("/idcards/print")
    public String idcPrint(@RequestParam String runId, @RequestParam(required = false) Integer batchSize) {
        return idCardIssuanceManager.print(runId, batchSize);
    }

    @PostMapping("/idcards/distribute")
    public String idcDistribute(@RequestParam String runId) {
        return idCardIssuanceManager.distribute(runId);
    }

    @GetMapping("/idcards/state")
    public String idcState(@RequestParam String runId) {
        return idCardIssuanceManager.getRunState(runId);
    }

    // ===================== HOSTEL/ROOM ALLOCATION =====================

    @PostMapping("/hostel/allocation/start")
    public String hostelStart(
            @RequestParam String hostelIdsCsv,
            @RequestParam(required = false) String capacitiesCsv) {
        return hostelAllocationManager.startAllocation(hostelIdsCsv, capacitiesCsv);
    }

    @PostMapping("/hostel/allocation/ingest-students")
    public String hostelIngest(@RequestParam String runId, @RequestParam String studentIdsCsv) {
        return hostelAllocationManager.ingestStudents(runId, studentIdsCsv);
    }

    @PostMapping("/hostel/allocation/assign-by-capacity")
    public String hostelAssign(@RequestParam String runId) {
        return hostelAllocationManager.assignByCapacity(runId);
    }

    @PostMapping("/hostel/allocation/finish")
    public String hostelFinish(@RequestParam String runId) {
        return hostelAllocationManager.finishAllocation(runId);
    }

    @GetMapping("/hostel/allocation/state")
    public String hostelState(@RequestParam String runId) {
        return hostelAllocationManager.getRunState(runId);
    }

    // ===================== MAINTENANCE WORK ORDERS =====================

    @PostMapping("/maintenance/start")
    public String maintenanceStart(@RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam(required = false) Double costEstimate) {
        return maintenanceWorkOrderManager.start(title, description, costEstimate);
    }

    @PostMapping("/maintenance/approve")
    public String maintenanceApprove(@RequestParam String runId,
                                     @RequestParam Long approverUserId) {
        return maintenanceWorkOrderManager.approve(runId, approverUserId);
    }

    @PostMapping("/maintenance/assign")
    public String maintenanceAssign(@RequestParam String runId,
                                    @RequestParam Long assigneeUserId) {
        return maintenanceWorkOrderManager.assign(runId, assigneeUserId);
    }

    @PostMapping("/maintenance/complete")
    public String maintenanceComplete(@RequestParam String runId) {
        return maintenanceWorkOrderManager.complete(runId);
    }

    @GetMapping("/maintenance/state")
    public String maintenanceState(@RequestParam String runId) {
        return maintenanceWorkOrderManager.getRunState(runId);
    }
}
