package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceReconciliationManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @InjectMocks
    private AttendanceReconciliationManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void start_initializesRunAndState() throws Exception {
        long ownerId = 55L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);

            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.start("2025-01-01", "2025-01-05", "10,20");

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            ArgumentCaptor<AgentRun> runCaptor = ArgumentCaptor.forClass(AgentRun.class);
            verify(agentRunRepository, atLeast(1)).save(runCaptor.capture());
            AgentRun saved = runCaptor.getValue();
            AttendanceReconciliationManager.ReconState state = mapper.readValue(saved.getStateJson(), AttendanceReconciliationManager.ReconState.class);
            assertEquals(LocalDate.parse("2025-01-01"), state.getDateFrom());
            assertEquals(LocalDate.parse("2025-01-05"), state.getDateTo());
            assertEquals(List.of(10L, 20L), state.getClassIds());
            assertEquals(0, state.getMissingCount());
            assertEquals(0, state.getCorrectedCount());
        }
    }

    @Test
    void start_returnsErrorWhenDateRangeInvalid() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(77L);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String response = manager.start("2025-02-01", "2025-01-01", null);

            assertEquals("dateTo must be >= dateFrom", response);
            verify(agentStepRepository, never()).save(any(AgentStep.class));
        }
    }

    @Test
    void detect_updatesMissingCountAndPersistsState() throws Exception {
        String runId = "run-detect";
        AttendanceReconciliationManager.ReconState state = AttendanceReconciliationManager.ReconState.builder()
                .dateFrom(LocalDate.parse("2025-01-01"))
                .dateTo(LocalDate.parse("2025-01-05"))
                .classIds(List.of(1L, 2L))
                .missingCount(0)
                .correctedCount(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.detect(runId);

        assertEquals("Missing records: 10", result);
        AttendanceReconciliationManager.ReconState saved = mapper.readValue(run.getStateJson(), AttendanceReconciliationManager.ReconState.class);
        assertEquals(10, saved.getMissingCount());
        assertTrue(saved.getTimestamps().containsKey("DETECTED"));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void ingestCorrections_accumulatesCounts() throws Exception {
        String runId = "run-ingest";
        AttendanceReconciliationManager.ReconState state = AttendanceReconciliationManager.ReconState.builder()
                .correctedCount(3)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.ingestCorrections(runId, 4);

        assertEquals("Corrected total: 7", response);
        AttendanceReconciliationManager.ReconState saved = mapper.readValue(run.getStateJson(), AttendanceReconciliationManager.ReconState.class);
        assertEquals(7, saved.getCorrectedCount());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void lock_setsLockedFlagAndCompletesRun() throws Exception {
        String runId = "run-lock";
        AttendanceReconciliationManager.ReconState state = AttendanceReconciliationManager.ReconState.builder()
                .locked(false)
                .correctedCount(5)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.lock(runId);

        assertEquals("Locked", result);
        AttendanceReconciliationManager.ReconState saved = mapper.readValue(run.getStateJson(), AttendanceReconciliationManager.ReconState.class);
        assertTrue(Boolean.TRUE.equals(saved.getLocked()));
        assertTrue(saved.getTimestamps().containsKey("LOCKED"));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void detect_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("missing")).thenReturn(Optional.empty());
        assertEquals("Invalid runId", manager.detect("missing"));
    }
}

