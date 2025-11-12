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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableOrchestrationManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @InjectMocks
    private TimetableOrchestrationManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void start_initializesState() throws Exception {
        long ownerId = 101L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.start("2024-2025", "Semester 1", "1,2,3", null);

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            AgentRun saved = agentRunRepository.save(AgentRun.builder().runId(runId).build());
            TimetableOrchestrationManager.TTState state = mapper.readValue(saved.getStateJson(), TimetableOrchestrationManager.TTState.class);
            assertEquals("2024-2025", state.getAcademicYear());
            assertEquals("Semester 1", state.getSemester());
            assertEquals(java.util.List.of(1L, 2L, 3L), state.getClassIds());
            assertTrue(state.getTimestamps().containsKey("STARTED"));
        }
    }

    @Test
    void generateDraft_calculatesSlotsBasedOnClasses() throws Exception {
        String runId = "run-draft";
        TimetableOrchestrationManager.TTState state = TimetableOrchestrationManager.TTState.builder()
                .classIds(java.util.List.of(1L, 2L))
                .draftSlots(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.generateDraft(runId);

        assertEquals("Draft slots: 60", result);
        TimetableOrchestrationManager.TTState saved = mapper.readValue(run.getStateJson(), TimetableOrchestrationManager.TTState.class);
        assertEquals(Integer.valueOf(60), saved.getDraftSlots());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void resolveConflicts_reducesConflictCount() throws Exception {
        String runId = "run-conflicts";
        TimetableOrchestrationManager.TTState state = TimetableOrchestrationManager.TTState.builder()
                .draftSlots(80)
                .conflictCount(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.resolveConflicts(runId, 2);

        assertEquals("Conflicts remaining: 2", response);
        TimetableOrchestrationManager.TTState saved = mapper.readValue(run.getStateJson(), TimetableOrchestrationManager.TTState.class);
        assertEquals(Integer.valueOf(2), saved.getConflictCount());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void finalizeTimetable_requiresZeroConflicts() throws Exception {
        String runId = "run-finalize";
        TimetableOrchestrationManager.TTState state = TimetableOrchestrationManager.TTState.builder()
                .conflictCount(1)
                .finalized(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));

        assertEquals("Cannot finalize: conflicts present", manager.finalizeTimetable(runId));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void finalizeTimetable_marksFinalizedWhenNoConflicts() throws Exception {
        String runId = "run-finalize-ok";
        TimetableOrchestrationManager.TTState state = TimetableOrchestrationManager.TTState.builder()
                .conflictCount(0)
                .finalized(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.finalizeTimetable(runId);

        assertEquals("Finalized", result);
        TimetableOrchestrationManager.TTState saved = mapper.readValue(run.getStateJson(), TimetableOrchestrationManager.TTState.class);
        assertTrue(Boolean.TRUE.equals(saved.getFinalized()));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void publish_requiresFinalized() throws Exception {
        String runId = "run-publish";
        TimetableOrchestrationManager.TTState state = TimetableOrchestrationManager.TTState.builder()
                .finalized(false)
                .published(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));

        assertEquals("Cannot publish: not finalized", manager.publish(runId));
    }

    @Test
    void publish_marksPublishedAfterFinalization() throws Exception {
        String runId = "run-publish-ok";
        TimetableOrchestrationManager.TTState state = TimetableOrchestrationManager.TTState.builder()
                .finalized(true)
                .published(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String res = manager.publish(runId);

        assertEquals("Published", res);
        TimetableOrchestrationManager.TTState saved = mapper.readValue(run.getStateJson(), TimetableOrchestrationManager.TTState.class);
        assertTrue(Boolean.TRUE.equals(saved.getPublished()));
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

