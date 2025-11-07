package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.TransferCertificateRequest;
import com.vijay.User_Master.dto.TransferCertificateResponse;
import com.vijay.User_Master.entity.TransferCertificate;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.TransferCertificateService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class TransferCertificateOrchestrationManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;
    private final TransferCertificateService tcService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class TCState {
        private Long studentId;
        private Long tcId;
        private String tcNumber;
        private String stage; // GENERATED, APPROVED, ISSUED, PDF_READY, COMPLETED
        private Map<String, String> timestamps;
        private String pdfUrl;
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

    private void persistState(AgentRun run, TCState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private TCState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return TCState.builder().build();
            return mapper.readValue(run.getStateJson(), TCState.class);
        } catch (Exception e) { return TCState.builder().build(); }
    }

    @Tool(name = "tcStart", description = "Start TC orchestration. Inputs: studentId, issuedByUserId, reason, details, lastAttendanceDate, academicYear, conduct, remarks. Returns runId.")
    public String start(
            Long studentId,
            Long issuedByUserId,
            String reasonForLeaving,
            String reasonDetails,
            String lastAttendanceDate,
            String academicYearOfLeaving,
            String conduct,
            String generalRemarks
    ) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("TransferCertificateOrchestration")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("generate_tc")
                .build();
        agentRunRepository.save(run);

        TCState state = TCState.builder()
                .studentId(studentId)
                .timestamps(new HashMap<>())
                .stage("STARTED")
                .build();

        try {
            TransferCertificateRequest req = new TransferCertificateRequest();
            req.setStudentId(studentId);
            req.setIssuedByUserId(issuedByUserId);
            // Map string to enum with safe defaults
            TransferCertificate.ReasonForLeaving rfl = null;
            if (reasonForLeaving != null && !reasonForLeaving.isBlank()) {
                try { rfl = TransferCertificate.ReasonForLeaving.valueOf(reasonForLeaving.trim().toUpperCase()); } catch (IllegalArgumentException ignored) { rfl = TransferCertificate.ReasonForLeaving.OTHER; }
            } else {
                rfl = TransferCertificate.ReasonForLeaving.OTHER;
            }
            req.setReasonForLeaving(rfl);
            req.setReasonDetails(reasonDetails);
            req.setLastAttendanceDate(lastAttendanceDate != null ? LocalDate.parse(lastAttendanceDate) : LocalDate.now());
            req.setAcademicYearOfLeaving(academicYearOfLeaving);
            TransferCertificate.ConductRating cr = null;
            if (conduct != null && !conduct.isBlank()) {
                try { cr = TransferCertificate.ConductRating.valueOf(conduct.trim().toUpperCase()); } catch (IllegalArgumentException ignored) { cr = TransferCertificate.ConductRating.GOOD; }
            } else {
                cr = TransferCertificate.ConductRating.GOOD;
            }
            req.setConduct(cr);
            req.setGeneralRemarks(generalRemarks);

            TransferCertificateResponse resp = tcService.generateTC(req);
            state.setTcId(resp.getId());
            state.setTcNumber(resp.getTcNumber());
            state.setStage("GENERATED");
            state.getTimestamps().put("GENERATED", LocalDateTime.now().toString());
            persistState(run, state, "generate_tc", "RUNNING");
            saveStep(run, "generate_tc", req, resp, "OK", null);
        } catch (Exception e) {
            saveStep(run, "generate_tc", Map.of("studentId", studentId), Map.of(), "ERROR", e.getMessage());
        }
        return runId;
    }

    @Tool(name = "tcApprove", description = "Approve TC. Inputs: runId, approvedByUserId. Returns status.")
    public String approve(String runId, Long approvedByUserId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TCState state = readState(run);
        if (state.getTcId() == null) return "TC not generated";
        try {
            TransferCertificateResponse resp = tcService.approveTC(state.getTcId(), approvedByUserId);
            state.setStage("APPROVED");
            state.getTimestamps().put("APPROVED", LocalDateTime.now().toString());
            persistState(run, state, "approve_tc", "RUNNING");
            saveStep(run, "approve_tc", Map.of("tcId", state.getTcId(), "approvedBy", approvedByUserId), resp, "OK", null);
            return "Approved";
        } catch (Exception e) {
            saveStep(run, "approve_tc", Map.of("tcId", state.getTcId(), "approvedBy", approvedByUserId), Map.of(), "ERROR", e.getMessage());
            return "Failed: " + e.getMessage();
        }
    }

    @Tool(name = "tcIssue", description = "Issue TC. Inputs: runId. Returns status.")
    public String issue(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TCState state = readState(run);
        if (state.getTcId() == null) return "TC not generated";
        try {
            TransferCertificateResponse resp = tcService.issueTC(state.getTcId());
            state.setStage("ISSUED");
            state.getTimestamps().put("ISSUED", LocalDateTime.now().toString());
            persistState(run, state, "issue_tc", "RUNNING");
            saveStep(run, "issue_tc", Map.of("tcId", state.getTcId()), resp, "OK", null);
            return "Issued";
        } catch (Exception e) {
            saveStep(run, "issue_tc", Map.of("tcId", state.getTcId()), Map.of(), "ERROR", e.getMessage());
            return "Failed: " + e.getMessage();
        }
    }

    @Tool(name = "tcGeneratePdf", description = "Generate PDF for TC. Inputs: runId. Returns pdfUrl.")
    public String generatePdf(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TCState state = readState(run);
        if (state.getTcId() == null) return "TC not generated";
        try {
            String url = tcService.generateTCPDF(state.getTcId());
            state.setPdfUrl(url);
            state.setStage("PDF_READY");
            state.getTimestamps().put("PDF_READY", LocalDateTime.now().toString());
            persistState(run, state, "generate_pdf", "RUNNING");
            saveStep(run, "generate_pdf", Map.of("tcId", state.getTcId()), Map.of("pdfUrl", url), "OK", null);
            return url;
        } catch (Exception e) {
            saveStep(run, "generate_pdf", Map.of("tcId", state.getTcId()), Map.of(), "ERROR", e.getMessage());
            return "Failed: " + e.getMessage();
        }
    }

    @Tool(name = "tcFinish", description = "Finish TC orchestration. Inputs: runId. Returns status.")
    public String finish(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        TCState state = readState(run);
        state.setStage("COMPLETED");
        state.getTimestamps().put("COMPLETED", LocalDateTime.now().toString());
        persistState(run, state, "finish", "COMPLETED");
        saveStep(run, "finish", Map.of(), Map.of("stage", state.getStage()), "OK", null);
        return "Completed";
    }

    @Tool(name = "tcGetRunState", description = "Get TC orchestration run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
