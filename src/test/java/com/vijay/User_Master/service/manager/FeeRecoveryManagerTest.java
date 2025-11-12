package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.repository.FeeRepository;
import com.vijay.User_Master.service.FeeService;
import com.vijay.User_Master.service.SchoolNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeRecoveryManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private FeeRepository feeRepository;

    @Mock
    private FeeService feeService;

    @Mock
    private SchoolNotificationService schoolNotificationService;

    @InjectMocks
    private FeeRecoveryManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void startFeeRecovery_detectsPendingFeesForStudent() throws Exception {
        long ownerId = 901L;
        long studentId = 321L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        Fee pendingFee = Fee.builder()
                .id(10L)
                .paymentStatus(Fee.PaymentStatus.PENDING)
                .build();
        Fee partialFee = Fee.builder()
                .id(11L)
                .paymentStatus(Fee.PaymentStatus.PARTIAL)
                .build();
        Fee paidFee = Fee.builder()
                .id(12L)
                .paymentStatus(Fee.PaymentStatus.PAID)
                .build();

        when(feeRepository.findByOwner_IdAndStudent_IdAndIsDeletedFalse(ownerId, studentId))
                .thenReturn(List.of(pendingFee, partialFee, paidFee));

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.startFeeRecovery(studentId, ownerId);

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            ArgumentCaptor<AgentRun> runs = ArgumentCaptor.forClass(AgentRun.class);
            verify(agentRunRepository, atLeast(1)).save(runs.capture());
            AgentRun saved = runs.getValue();
            FeeRecoveryManager.FeeRecoveryState state = mapper.readValue(saved.getStateJson(), FeeRecoveryManager.FeeRecoveryState.class);
            assertEquals(List.of(10L, 11L), state.getFeeIds());
            assertEquals("DETECTED", state.getStage());
            assertTrue(state.getTimestamps().containsKey("DETECTED"));
        }
    }

    @Test
    void sendReminder_dispatchesNotificationsAndUpdatesStage() throws Exception {
        String runId = "run-reminder";
        FeeRecoveryManager.FeeRecoveryState state = FeeRecoveryManager.FeeRecoveryState.builder()
                .feeIds(List.of(101L))
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();

        Worker student = Worker.builder().id(555L).build();
        Fee fee = Fee.builder().id(101L).student(student).build();

        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(feeRepository.findById(101L)).thenReturn(Optional.of(fee));

        String result = manager.sendReminder(runId, "T1");

        assertEquals("Reminders sent: 1", result);
        verify(schoolNotificationService).sendFeeReminder(555L, 101L, 7);
        FeeRecoveryManager.FeeRecoveryState saved = mapper.readValue(run.getStateJson(), FeeRecoveryManager.FeeRecoveryState.class);
        assertEquals("T1", saved.getStage());
        assertTrue(saved.getTimestamps().containsKey("T1"));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void decidePlan_recordsInstallmentAndWaiverDetails() throws Exception {
        String runId = "run-plan";
        FeeRecoveryManager.FeeRecoveryState state = FeeRecoveryManager.FeeRecoveryState.builder()
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.decidePlan(runId, true, 6, 500.0, "Scholarship");

        assertEquals("Plan recorded", response);
        FeeRecoveryManager.FeeRecoveryState saved = mapper.readValue(run.getStateJson(), FeeRecoveryManager.FeeRecoveryState.class);
        assertTrue(Boolean.TRUE.equals(saved.getInstallmentPlan()));
        assertEquals(6, saved.getInstallmentCount());
        assertEquals(500.0, saved.getWaiverAmount());
        assertEquals("Scholarship", saved.getWaiverReason());
        assertEquals("PLAN_DECIDED", saved.getStage());
        assertTrue(saved.getTimestamps().containsKey("PLAN_DECIDED"));
    }

    @Test
    void markPayment_addsToTotalCollectedAndSendsReceipt() throws Exception {
        String runId = "run-pay";
        FeeRecoveryManager.FeeRecoveryState state = FeeRecoveryManager.FeeRecoveryState.builder()
                .totalCollected(1000.0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        Worker student = Worker.builder().id(700L).build();
        Fee fee = Fee.builder().id(22L).student(student).build();

        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(feeRepository.findById(22L)).thenReturn(Optional.of(fee));

        String result = manager.markPayment(runId, 22L, 250.0, "ONLINE", "txn-1");

        assertEquals("Payment recorded", result);
        verify(feeService).recordPayment(22L, 250.0, "ONLINE", "txn-1");
        verify(schoolNotificationService).sendFeePaymentReceipt(700L, 22L);
        FeeRecoveryManager.FeeRecoveryState saved = mapper.readValue(run.getStateJson(), FeeRecoveryManager.FeeRecoveryState.class);
        assertEquals(1250.0, saved.getTotalCollected());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void markPayment_returnsErrorWhenFeeServiceFails() {
        String runId = "run-pay-fail";
        FeeRecoveryManager.FeeRecoveryState state = FeeRecoveryManager.FeeRecoveryState.builder()
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson("{}")
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        doThrow(new RuntimeException("service down")).when(feeService).recordPayment(30L, 100.0, "CASH", "txn-2");

        String response = manager.markPayment(runId, 30L, 100.0, "CASH", "txn-2");

        assertEquals("Failed to record payment: service down", response);
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

