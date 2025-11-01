package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.repository.FeeRepository;
import com.vijay.User_Master.service.FeeService;
import com.vijay.User_Master.service.SchoolNotificationService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class FeeRecoveryManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final FeeRepository feeRepository;
    private final FeeService feeService;
    private final SchoolNotificationService schoolNotificationService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class FeeRecoveryState {
        private Long studentId; // optional focus
        private List<Long> feeIds; // targeted fees
        private String stage; // DETECTED, REMINDER_T1, REMINDER_T2, FINAL_NOTICE, PLAN_DECIDED, ESCALATED, COMPLETED
        private Map<String, String> timestamps; // stage->iso ts
        private Boolean installmentPlan;
        private Integer installmentCount;
        private Double waiverAmount;
        private String waiverReason;
        private Double totalCollected;
        private Boolean escalated;
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

    private void persistState(AgentRun run, FeeRecoveryState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private FeeRecoveryState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return FeeRecoveryState.builder().build();
            return mapper.readValue(run.getStateJson(), FeeRecoveryState.class);
        } catch (Exception e) { return FeeRecoveryState.builder().build(); }
    }

    @Tool(description = "Start Fee Recovery. Inputs: studentId (optional). Returns runId.")
    public String startFeeRecovery(Long studentId) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("FeeRecovery")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("detect_overdue")
                .build();
        FeeRecoveryState state = FeeRecoveryState.builder()
                .studentId(studentId)
                .feeIds(new ArrayList<>())
                .stage("DETECTED")
                .timestamps(new HashMap<>())
                .installmentPlan(false)
                .installmentCount(0)
                .waiverAmount(0.0)
                .waiverReason(null)
                .totalCollected(0.0)
                .escalated(false)
                .build();
        agentRunRepository.save(run);

        // detect overdue or pending fees
        List<Fee> targetFees;
        if (studentId != null) {
            targetFees = feeRepository.findByOwner_IdAndStudent_IdAndIsDeletedFalse(ownerId, studentId);
            targetFees.removeIf(f -> !(f.getPaymentStatus() == Fee.PaymentStatus.PENDING || f.getPaymentStatus() == Fee.PaymentStatus.PARTIAL));
        } else {
            targetFees = feeRepository.findOverdueFees(LocalDate.now());
        }
        List<Long> ids = new ArrayList<>();
        for (Fee f : targetFees) ids.add(f.getId());
        state.setFeeIds(ids);
        state.getTimestamps().put("DETECTED", LocalDateTime.now().toString());
        persistState(run, state, "detect_overdue", "RUNNING");
        saveStep(run, "detect_overdue", Map.of("studentId", studentId), Map.of("feeCount", ids.size()), "OK", null);
        return runId;
    }

    @Tool(description = "Send reminder. Inputs: runId, stage (T1|T2|FINAL). Returns status.")
    public String sendReminder(String runId, String stage) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        FeeRecoveryState state = readState(run);
        if (state.getFeeIds() == null || state.getFeeIds().isEmpty()) return "No fees to remind";
        int reminded = 0;
        for (Long feeId : state.getFeeIds()) {
            try {
                Fee fee = feeRepository.findById(feeId).orElse(null);
                if (fee == null || fee.getStudent() == null) continue;
                if ("T1".equalsIgnoreCase(stage)) {
                    schoolNotificationService.sendFeeReminder(fee.getStudent().getId(), feeId, 7);
                } else if ("T2".equalsIgnoreCase(stage)) {
                    schoolNotificationService.sendFeeReminder(fee.getStudent().getId(), feeId, 3);
                } else {
                    schoolNotificationService.sendFeeOverdueNotice(fee.getStudent().getId(), feeId);
                }
                reminded++;
            } catch (Exception ignored) {}
        }
        String node = switch (stage == null ? "" : stage.toUpperCase()) {
            case "T1" -> "send_reminder_t1";
            case "T2" -> "send_reminder_t2";
            default -> "final_notice";
        };
        state.setStage(stage != null ? stage.toUpperCase() : "FINAL_NOTICE");
        state.getTimestamps().put(state.getStage(), LocalDateTime.now().toString());
        persistState(run, state, node, "RUNNING");
        saveStep(run, node, Map.of("fees", state.getFeeIds()), Map.of("reminded", reminded), "OK", null);
        return "Reminders sent: " + reminded;
    }

    @Tool(description = "Decide plan or waiver. Inputs: runId, installmentPlan (true/false), installmentCount, waiverAmount, waiverReason. Returns status.")
    public String decidePlan(String runId, Boolean installmentPlan, Integer installmentCount, Double waiverAmount, String waiverReason) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        FeeRecoveryState state = readState(run);
        state.setInstallmentPlan(Boolean.TRUE.equals(installmentPlan));
        state.setInstallmentCount(installmentCount != null ? installmentCount : 0);
        state.setWaiverAmount(waiverAmount != null ? waiverAmount : 0.0);
        state.setWaiverReason(waiverReason);
        state.setStage("PLAN_DECIDED");
        state.getTimestamps().put("PLAN_DECIDED", LocalDateTime.now().toString());
        persistState(run, state, "plan_decision", "RUNNING");
        saveStep(run, "plan_decision", Map.of("installments", installmentCount, "waiver", waiverAmount), Map.of("ok", true), "OK", null);
        return "Plan recorded";
    }

    @Tool(description = "Mark payment against a fee. Inputs: runId, feeId, amount, method, transactionId. Returns status.")
    public String markPayment(String runId, Long feeId, Double amount, String method, String transactionId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        FeeRecoveryState state = readState(run);
        try {
            feeService.recordPayment(feeId, amount, method, transactionId);
            state.setTotalCollected((state.getTotalCollected() == null ? 0.0 : state.getTotalCollected()) + (amount != null ? amount : 0.0));
            persistState(run, state, "mark_payment", "RUNNING");
            saveStep(run, "mark_payment", Map.of("feeId", feeId, "amount", amount), Map.of("totalCollected", state.getTotalCollected()), "OK", null);
            // send receipt
            try { 
                Fee fee = feeRepository.findById(feeId).orElse(null);
                if (fee != null && fee.getStudent() != null) {
                    schoolNotificationService.sendFeePaymentReceipt(fee.getStudent().getId(), feeId);
                }
            } catch (Exception ignored) {}
            return "Payment recorded";
        } catch (Exception e) {
            saveStep(run, "mark_payment", Map.of("feeId", feeId, "amount", amount), Map.of(), "ERROR", e.getMessage());
            return "Failed to record payment: "+e.getMessage();
        }
    }

    @Tool(name = "feesGetRunState", description = "Get Fee Recovery run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
