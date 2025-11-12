package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.TransferCertificateRequest;
import com.vijay.User_Master.dto.TransferCertificateResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.entity.TransferCertificate;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.TransferCertificateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferCertificateOrchestrationManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private TransferCertificateService tcService;

    @InjectMocks
    private TransferCertificateOrchestrationManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void start_generatesTcAndInitializesState() throws Exception {
        long ownerId = 555L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        TransferCertificateResponse generated = TransferCertificateResponse.builder()
                .id(1001L)
                .tcNumber("TC-2024-001")
                .build();
        when(tcService.generateTC(any(TransferCertificateRequest.class))).thenReturn(generated);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.start(777L, 888L, "TRANSFER", "Moving to another city", "2024-03-10", "2023-2024", "Good", "All the best");

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            AgentRun saved = agentRunRepository.save(AgentRun.builder().runId(runId).build());
            TransferCertificateOrchestrationManager.TCState state = mapper.readValue(saved.getStateJson(), TransferCertificateOrchestrationManager.TCState.class);
            assertEquals(777L, state.getStudentId());
            assertEquals(Long.valueOf(1001L), state.getTcId());
            assertEquals("TC-2024-001", state.getTcNumber());
            assertEquals("GENERATED", state.getStage());
            assertTrue(state.getTimestamps().containsKey("GENERATED"));
        }
    }

    @Test
    void approve_transitionsToApproved() throws Exception {
        String runId = "run-approve";
        TransferCertificateOrchestrationManager.TCState state = TransferCertificateOrchestrationManager.TCState.builder()
                .tcId(2002L)
                .stage("GENERATED")
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(tcService.approveTC(2002L, 3003L)).thenReturn(TransferCertificateResponse.builder().id(2002L).build());

        String result = manager.approve(runId, 3003L);

        assertEquals("Approved", result);
        TransferCertificateOrchestrationManager.TCState saved = mapper.readValue(run.getStateJson(), TransferCertificateOrchestrationManager.TCState.class);
        assertEquals("APPROVED", saved.getStage());
        assertTrue(saved.getTimestamps().containsKey("APPROVED"));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void issue_updatesStageToIssued() throws Exception {
        String runId = "run-issue";
        TransferCertificateOrchestrationManager.TCState state = TransferCertificateOrchestrationManager.TCState.builder()
                .tcId(4004L)
                .stage("APPROVED")
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(tcService.issueTC(4004L)).thenReturn(TransferCertificateResponse.builder().id(4004L).status(TransferCertificate.TCStatus.ISSUED).build());

        String response = manager.issue(runId);

        assertEquals("Issued", response);
        TransferCertificateOrchestrationManager.TCState saved = mapper.readValue(run.getStateJson(), TransferCertificateOrchestrationManager.TCState.class);
        assertEquals("ISSUED", saved.getStage());
        assertTrue(saved.getTimestamps().containsKey("ISSUED"));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void generatePdf_savesUrlAndStage() throws Exception {
        String runId = "run-pdf";
        TransferCertificateOrchestrationManager.TCState state = TransferCertificateOrchestrationManager.TCState.builder()
                .tcId(5005L)
                .stage("ISSUED")
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(tcService.generateTCPDF(5005L)).thenReturn("https://example.com/tc.pdf");

        String url = manager.generatePdf(runId);

        assertEquals("https://example.com/tc.pdf", url);
        TransferCertificateOrchestrationManager.TCState saved = mapper.readValue(run.getStateJson(), TransferCertificateOrchestrationManager.TCState.class);
        assertEquals("PDF_READY", saved.getStage());
        assertEquals("https://example.com/tc.pdf", saved.getPdfUrl());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void finish_marksCompleted() throws Exception {
        String runId = "run-finish";
        TransferCertificateOrchestrationManager.TCState state = TransferCertificateOrchestrationManager.TCState.builder()
                .stage("PDF_READY")
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.finish(runId);

        assertEquals("Completed", result);
        TransferCertificateOrchestrationManager.TCState saved = mapper.readValue(run.getStateJson(), TransferCertificateOrchestrationManager.TCState.class);
        assertEquals("COMPLETED", saved.getStage());
        assertTrue(saved.getTimestamps().containsKey("COMPLETED"));
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

