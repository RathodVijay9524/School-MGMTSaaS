package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.dto.WorkerRequest;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.repository.FeeRepository;
import com.vijay.User_Master.service.SchoolNotificationService;
import com.vijay.User_Master.service.impl.WorkerUserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
@Slf4j
public class AdmissionsFunnelManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final SchoolNotificationService schoolNotificationService;
    private final WorkerUserServiceImpl workerUserService;
    private final FeeRepository feeRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class AdmissionsState {
        private String applicantName;
        private String applicantEmail;
        private String gradeApplied;
        private String parentName;
        private String parentEmail;
        private List<Long> documentIds;
        private boolean docsVerified;
        private String interviewSlot; // ISO string or human readable
        private Double interviewScore;
        private String decision; // APPROVED/REJECTED/PENDING
        private Long feeInvoiceId;
        private Boolean feePaid;
        private Long createdStudentId;
        private Long feeId;
    }

    private String newRunId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

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

    private void persistState(AgentRun run, AdmissionsState state, String currentNode, String status) {
        try {
            run.setStateJson(mapper.writeValueAsString(state));
        } catch (JsonProcessingException e) {
            run.setStateJson(null);
        }
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private AdmissionsState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return AdmissionsState.builder().build();
            return mapper.readValue(run.getStateJson(), AdmissionsState.class);
        } catch (Exception e) {
            return AdmissionsState.builder().build();
        }
    }

    @Tool(description = "Start Admissions run. Inputs: applicantName, applicantEmail, gradeApplied, parentName, parentEmail. Returns runId.")
    public String startAdmissions(String applicantName, String applicantEmail, String gradeApplied, String parentName, String parentEmail) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("AdmissionsFunnel")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("collect_application")
                .build();
        AdmissionsState state = AdmissionsState.builder()
                .applicantName(applicantName)
                .applicantEmail(applicantEmail)
                .gradeApplied(gradeApplied)
                .parentName(parentName)
                .parentEmail(parentEmail)
                .documentIds(new ArrayList<>())
                .docsVerified(false)
                .decision("PENDING")
                .feePaid(false)
                .build();
        agentRunRepository.save(run);
        persistState(run, state, "collect_application", "RUNNING");
        saveStep(run, "collect_application",
                Map.of("applicantName", applicantName, "applicantEmail", applicantEmail, "gradeApplied", gradeApplied),
                Map.of("message", "Application received"), "OK", null);
        // notify applicant/parent
        // Optional: send acknowledgement email in your notification system (placeholder call removed)
        return runId;
    }

    @Tool(description = "Submit documents for an admissions run. Inputs: runId, documentIds (comma-separated Longs). Returns status string.")
    public String submitDocuments(String runId, String documentIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        List<Long> ids = new ArrayList<>();
        if (documentIdsCsv != null && !documentIdsCsv.isBlank()) {
            for (String p : documentIdsCsv.split(",")) {
                try { ids.add(Long.parseLong(p.trim())); } catch (Exception ignored) {}
            }
        }
        if (state.getDocumentIds() == null) state.setDocumentIds(new ArrayList<>());
        state.getDocumentIds().addAll(ids);
        // simple verification heuristic
        state.setDocsVerified(!state.getDocumentIds().isEmpty());
        persistState(run, state, "verify_documents", state.isDocsVerified() ? "RUNNING" : "WAITING");
        saveStep(run, "verify_documents", Map.of("documentIds", ids), Map.of("verified", state.isDocsVerified()), "OK", null);
        return state.isDocsVerified() ? "Documents verified" : "No documents received";
    }

    @Tool(description = "Schedule interview. Inputs: runId, slot (string). Returns status string.")
    public String scheduleInterview(String runId, String slot) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        state.setInterviewSlot(slot);
        persistState(run, state, "schedule_interview", "WAITING");
        saveStep(run, "schedule_interview", Map.of("slot", slot), Map.of("status", "scheduled"), "OK", null);
        return "Interview scheduled for " + slot;
    }

    @Tool(description = "Submit interview feedback. Inputs: runId, score (0-100), notes. Returns status string.")
    public String submitInterviewFeedback(String runId, Double score, String notes) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        state.setInterviewScore(score != null ? score : 0.0);
        persistState(run, state, "record_interview_feedback", "RUNNING");
        saveStep(run, "record_interview_feedback", Map.of("score", score, "notes", notes), Map.of("accepted", score != null && score >= 60), "OK", null);
        return "Interview feedback recorded";
    }

    @Tool(description = "Final decision. Inputs: runId, approved (true/false). Returns status string.")
    public String finalDecision(String runId, Boolean approved) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        state.setDecision(Boolean.TRUE.equals(approved) ? "APPROVED" : "REJECTED");
        persistState(run, state, "decision_committee_review", "RUNNING");
        saveStep(run, "decision_committee_review", Map.of("approved", approved), Map.of("decision", state.getDecision()), "OK", null);
        return "Decision set: " + state.getDecision();
    }

    @Tool(description = "Initiate fee. Inputs: runId, amount (Double). Returns status string.")
    public String initiateFee(String runId, Double amount) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        if (!"APPROVED".equals(state.getDecision())) return "Cannot initiate fee unless APPROVED";
        if (amount == null || amount <= 0) return "Amount must be > 0";

        // Ensure student exists before fee
        if (state.getCreatedStudentId() == null) {
            // Create a basic student record using WorkerUserServiceImpl
            String username = (state.getApplicantEmail() != null && !state.getApplicantEmail().isBlank())
                    ? state.getApplicantEmail()
                    : ("student_" + UUID.randomUUID().toString().substring(0,8));
            String safePassword = UUID.randomUUID().toString().substring(0, 12);
            WorkerRequest req = WorkerRequest.builder()
                    .name(state.getApplicantName())
                    .username(username)
                    .email(state.getApplicantEmail())
                    .password(safePassword)
                    .roles(new HashSet<>(Collections.singleton("ROLE_STUDENT")))
                    .parentEmail(state.getParentEmail())
                    .build();
            var created = workerUserService.create(req);
            state.setCreatedStudentId(created.getId());
            saveStep(run, "onboard_student(create)", Map.of("username", username), Map.of("studentId", created.getId()), "OK", null);
        }

        // Create Fee entity for ADMISSION type
        try {
            Worker studentRef = Worker.builder().id(state.getCreatedStudentId()).build();
            Fee fee = Fee.builder()
                    .student(studentRef)
                    .feeType(Fee.FeeType.ADMISSION)
                    .feeCategory("Admission Fee")
                    .totalAmount(amount)
                    .paidAmount(0.0)
                    .discountAmount(0.0)
                    .balanceAmount(amount)
                    .paymentStatus(Fee.PaymentStatus.PENDING)
                    .dueDate(LocalDate.now().plusDays(7))
                    .owner(User.builder().id(run.getOwnerId()).build())
                    .build();
            Fee saved = feeRepository.save(fee);
            state.setFeeId(saved.getId());
            persistState(run, state, "collect_enrollment_fee", "WAITING");
            saveStep(run, "collect_enrollment_fee", Map.of("amount", amount), Map.of("feeId", saved.getId()), "OK", null);
            return "Fee created with ID: " + saved.getId();
        } catch (Exception e) {
            saveStep(run, "collect_enrollment_fee", Map.of("amount", amount), Map.of(), "ERROR", e.getMessage());
            return "Failed to create fee: " + e.getMessage();
        }
    }

    @Tool(description = "Mark payment captured. Inputs: runId, transactionId, method(ONLINE/CASH/...). Returns status string.")
    public String markPaymentCaptured(String runId, String transactionId, String method) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        if (state.getFeeId() == null) return "No fee created yet";
        try {
            Fee fee = feeRepository.findById(state.getFeeId()).orElse(null);
            if (fee == null) return "Fee not found";
            fee.setPaidAmount(fee.getTotalAmount());
            fee.setBalanceAmount(0.0);
            fee.setPaymentStatus(Fee.PaymentStatus.PAID);
            fee.setPaymentDate(LocalDate.now());
            fee.setPaymentMethod(method != null ? Fee.PaymentMethod.valueOf(method) : Fee.PaymentMethod.ONLINE);
            fee.setTransactionId(transactionId != null ? transactionId : ("TXN-" + UUID.randomUUID().toString().substring(0,8)));
            feeRepository.save(fee);

            state.setFeePaid(true);
            persistState(run, state, "collect_enrollment_fee", "RUNNING");
            saveStep(run, "payment_captured", Map.of("transactionId", fee.getTransactionId()), Map.of("feePaid", true), "OK", null);
            return "Payment captured for feeId=" + state.getFeeId();
        } catch (Exception e) {
            saveStep(run, "payment_captured", Map.of("transactionId", transactionId), Map.of(), "ERROR", e.getMessage());
            return "Failed to capture payment: " + e.getMessage();
        }
    }

    @Tool(description = "Onboard student (create Worker if not created yet). Inputs: runId. Returns status string.")
    public String onboardStudent(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        if (state.getCreatedStudentId() == null) {
            String username = (state.getApplicantEmail() != null && !state.getApplicantEmail().isBlank())
                    ? state.getApplicantEmail()
                    : ("student_" + UUID.randomUUID().toString().substring(0,8));
            String safePassword = UUID.randomUUID().toString().substring(0, 12);
            WorkerRequest req = WorkerRequest.builder()
                    .name(state.getApplicantName())
                    .username(username)
                    .email(state.getApplicantEmail())
                    .password(safePassword)
                    .roles(new HashSet<>(Collections.singleton("ROLE_STUDENT")))
                    .parentEmail(state.getParentEmail())
                    .build();
            var created = workerUserService.create(req);
            state.setCreatedStudentId(created.getId());
            saveStep(run, "onboard_student(create)", Map.of("username", username), Map.of("studentId", created.getId()), "OK", null);
        }

        // If fee is already paid, we can complete the run; else keep RUNNING until payment
        String newStatus = Boolean.TRUE.equals(state.getFeePaid()) ? "COMPLETED" : "RUNNING";
        persistState(run, state, "onboard_student", newStatus);
        return Boolean.TRUE.equals(state.getFeePaid())
                ? ("Onboarding complete. Student ID: " + state.getCreatedStudentId())
                : ("Student created with ID: " + state.getCreatedStudentId() + ". Awaiting fee payment to complete.");
    }

    @Tool(description = "Onboard student and set class. Inputs: runId, classId (Long). Returns status string.")
    public String onboardStudentWithClass(String runId, Long classId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        AdmissionsState state = readState(run);
        // Ensure student exists
        if (state.getCreatedStudentId() == null) {
            String username = (state.getApplicantEmail() != null && !state.getApplicantEmail().isBlank())
                    ? state.getApplicantEmail()
                    : ("student_" + UUID.randomUUID().toString().substring(0,8));
            String safePassword = UUID.randomUUID().toString().substring(0, 12);
            WorkerRequest req = WorkerRequest.builder()
                    .name(state.getApplicantName())
                    .username(username)
                    .email(state.getApplicantEmail())
                    .password(safePassword)
                    .roles(new HashSet<>(Collections.singleton("ROLE_STUDENT")))
                    .parentEmail(state.getParentEmail())
                    .currentClassId(classId)
                    .build();
            var created = workerUserService.create(req);
            state.setCreatedStudentId(created.getId());
            saveStep(run, "onboard_student(create)", Map.of("username", username, "classId", classId), Map.of("studentId", created.getId()), "OK", null);
        } else {
            // Update class for existing student
            try {
                WorkerRequest updateReq = WorkerRequest.builder().currentClassId(classId).build();
                workerUserService.update(state.getCreatedStudentId(), updateReq);
                saveStep(run, "onboard_student(update_class)", Map.of("classId", classId), Map.of("studentId", state.getCreatedStudentId()), "OK", null);
            } catch (Exception e) {
                saveStep(run, "onboard_student(update_class)", Map.of("classId", classId), Map.of(), "ERROR", e.getMessage());
                return "Failed to set class: " + e.getMessage();
            }
        }

        String newStatus = Boolean.TRUE.equals(state.getFeePaid()) ? "COMPLETED" : "RUNNING";
        persistState(run, state, "onboard_student", newStatus);
        return Boolean.TRUE.equals(state.getFeePaid())
                ? ("Onboarding complete. Student ID: " + state.getCreatedStudentId())
                : ("Student created with ID: " + state.getCreatedStudentId() + ". Class set. Awaiting fee payment to complete.");
    }

    @Tool(description = "Get run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
