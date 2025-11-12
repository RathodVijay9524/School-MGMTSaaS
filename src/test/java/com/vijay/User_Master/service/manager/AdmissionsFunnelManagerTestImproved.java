package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.dto.WorkerResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.repository.FeeRepository;
import com.vijay.User_Master.service.SchoolNotificationService;
import com.vijay.User_Master.service.impl.WorkerUserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdmissionsFunnelManagerTestImproved extends ManagerTestBase {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private SchoolNotificationService schoolNotificationService;

    @Mock
    private WorkerUserServiceImpl workerUserService;

    @Mock
    private FeeRepository feeRepository;

    @InjectMocks
    private AdmissionsFunnelManager manager;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupRepositoryMocks(agentRunRepository, agentStepRepository);
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void startAdmissions_initializesState() throws Exception {
        String runId = manager.startAdmissions("Alice", "alice@example.com", "Grade 5", "Parent P", "parent@example.com");

        assertNotNull(runId);
        assertFalse(runId.isBlank());
        verify(agentRunRepository, atLeast(1)).save(any(AgentRun.class));
        verify(agentStepRepository).save(any());
    }

    @Test
    void submitDocuments_verifiesDocsWhenProvided() throws Exception {
        String runId = "run-docs";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .documentIds(new java.util.ArrayList<>())
                .docsVerified(false)
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String response = manager.submitDocuments(runId, "1, 2");

        assertEquals("Documents verified", response);
        verify(agentRunRepository).findByRunId(runId);
        verify(agentRunRepository).save(any(AgentRun.class));
    }

    @Test
    void submitDocuments_returnsMessageWhenNoDocuments() throws Exception {
        String runId = "run-no-docs";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .documentIds(new java.util.ArrayList<>())
                .docsVerified(false)
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String response = manager.submitDocuments(runId, "");

        assertEquals("No documents received", response);
    }

    @Test
    void submitDocuments_returnsInvalidRunIdMessage() throws Exception {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String response = manager.submitDocuments("invalid", "1,2");

        assertEquals("Invalid runId", response);
    }

    @Test
    void scheduleInterview_setsInterviewSlot() throws Exception {
        String runId = "run-interview";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.scheduleInterview(runId, "2025-11-20 10:00 AM");

        assertEquals("Interview scheduled for 2025-11-20 10:00 AM", result);
        verify(agentRunRepository).save(any(AgentRun.class));
    }

    @Test
    void submitInterviewFeedback_recordsScore() throws Exception {
        String runId = "run-feedback";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.submitInterviewFeedback(runId, 85.0, "Good performance");

        assertEquals("Interview feedback recorded", result);
        verify(agentRunRepository).save(any(AgentRun.class));
    }

    @Test
    void finalDecision_approvesApplicant() throws Exception {
        String runId = "run-decision";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .decision("PENDING")
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.finalDecision(runId, true);

        assertEquals("Decision set: APPROVED", result);
        verify(agentRunRepository).save(any(AgentRun.class));
    }

    @Test
    void finalDecision_rejectsApplicant() throws Exception {
        String runId = "run-reject";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .decision("PENDING")
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.finalDecision(runId, false);

        assertEquals("Decision set: REJECTED", result);
    }

    @Test
    void initiateFee_createsStudentAndFeeWhenApproved() throws Exception {
        String runId = "run-fee";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .applicantName("Bob")
                .applicantEmail("bob@example.com")
                .parentEmail("parent@example.com")
                .decision("APPROVED")
                .feePaid(false)
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        WorkerResponse createdWorkerResponse = WorkerResponse.builder().id(700L).build();
        when(workerUserService.create(any())).thenReturn(createdWorkerResponse);

        Fee savedFee = Fee.builder()
                .id(555L)
                .student(Worker.builder().id(700L).build())
                .owner(User.builder().id(OWNER_ID).build())
                .build();
        when(feeRepository.save(any(Fee.class))).thenReturn(savedFee);

        String response = manager.initiateFee(runId, 25000.0);

        assertTrue(response.contains("Fee created"));
        verify(workerUserService).create(any());
        verify(feeRepository).save(any(Fee.class));
    }

    @Test
    void initiateFee_failsWhenNotApproved() throws Exception {
        String runId = "run-fee-fail";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .decision("PENDING")
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String response = manager.initiateFee(runId, 25000.0);

        assertEquals("Cannot initiate fee unless APPROVED", response);
    }

    @Test
    void markPaymentCaptured_updatesFeeStatus() throws Exception {
        String runId = "run-pay";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .feeId(600L)
                .feePaid(false)
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        Fee fee = Fee.builder()
                .id(600L)
                .student(Worker.builder().id(123L).build())
                .owner(User.builder().id(OWNER_ID).build())
                .totalAmount(25000.0)
                .paidAmount(0.0)
                .balanceAmount(25000.0)
                .paymentStatus(Fee.PaymentStatus.PENDING)
                .build();
        when(feeRepository.findById(600L)).thenReturn(Optional.of(fee));
        when(feeRepository.save(any(Fee.class))).thenReturn(fee);

        String result = manager.markPaymentCaptured(runId, "TX123", "ONLINE");

        assertTrue(result.contains("Payment captured"));
        verify(feeRepository).findById(600L);
        verify(feeRepository).save(any(Fee.class));
    }

    @Test
    void onboardStudent_createsStudentWhenNotExists() throws Exception {
        String runId = "run-onboard";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .applicantName("Charlie")
                .applicantEmail("charlie@example.com")
                .parentEmail("parent@example.com")
                .feePaid(true)
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        WorkerResponse workerResponse = WorkerResponse.builder().id(800L).build();
        when(workerUserService.create(any())).thenReturn(workerResponse);

        String result = manager.onboardStudent(runId);

        assertTrue(result.contains("Onboarding complete"));
        verify(workerUserService).create(any());
    }

    @Test
    void getRunState_returnsStateJson() throws Exception {
        String runId = "run-state";
        AdmissionsFunnelManager.AdmissionsState state = AdmissionsFunnelManager.AdmissionsState.builder()
                .applicantName("Diana")
                .decision("APPROVED")
                .build();
        
        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.getRunState(runId);

        assertNotNull(result);
        assertTrue(result.contains("Diana"));
        assertTrue(result.contains("APPROVED"));
    }

    @Test
    void getRunState_returnsInvalidMessageForNonexistentRun() {
        when(agentRunRepository.findByRunId("nonexistent")).thenReturn(Optional.empty());

        String result = manager.getRunState("nonexistent");

        assertEquals("Invalid runId", result);
    }
}
