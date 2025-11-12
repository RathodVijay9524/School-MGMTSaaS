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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HostelAllocationManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @InjectMocks
    private HostelAllocationManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void startAllocation_initializesStateWithCapacities() throws Exception {
        long ownerId = 999L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.startAllocation("1,2", "10,20");

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            ArgumentCaptor<AgentRun> runCaptor = ArgumentCaptor.forClass(AgentRun.class);
            verify(agentRunRepository, atLeast(1)).save(runCaptor.capture());
            AgentRun saved = runCaptor.getValue();
            HostelAllocationManager.HostelState state = mapper.readValue(saved.getStateJson(), HostelAllocationManager.HostelState.class);
            assertEquals(List.of(1L, 2L), state.getHostelIds());
            assertEquals(Integer.valueOf(10), state.getHostelCapacity().get(1L));
            assertEquals(Integer.valueOf(20), state.getHostelCapacity().get(2L));
            assertTrue(state.getCandidateStudentIds().isEmpty());
        }
    }

    @Test
    void startAllocation_requiresHostelIds() {
        String response = manager.startAllocation(null, "10");
        assertEquals("hostelIdsCsv is required", response);
        verifyNoInteractions(agentRunRepository, agentStepRepository);
    }

    @Test
    void ingestStudents_appendsCandidates() throws Exception {
        String runId = "run-ingest";
        HostelAllocationManager.HostelState state = HostelAllocationManager.HostelState.builder()
                .candidateStudentIds(new java.util.ArrayList<>(List.of(1L)))
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.ingestStudents(runId, "2,3");

        assertEquals("Candidates: 3", result);
        HostelAllocationManager.HostelState saved = mapper.readValue(run.getStateJson(), HostelAllocationManager.HostelState.class);
        assertEquals(List.of(1L, 2L, 3L), saved.getCandidateStudentIds());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void assignByCapacity_assignsUntilHostelsFull() throws Exception {
        String runId = "run-assign";
        HostelAllocationManager.HostelState state = HostelAllocationManager.HostelState.builder()
                .hostelIds(List.of(10L, 20L))
                .hostelCapacity(new LinkedHashMap<>(java.util.Map.of(10L, 1, 20L, 1)))
                .candidateStudentIds(List.of(100L, 101L, 102L))
                .studentToHostel(new LinkedHashMap<>())
                .unassigned(new java.util.ArrayList<>())
                .completed(false)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.assignByCapacity(runId);

        assertEquals("Assigned: 2, Unassigned: 1", response);
        HostelAllocationManager.HostelState saved = mapper.readValue(run.getStateJson(), HostelAllocationManager.HostelState.class);
        assertEquals(2, saved.getStudentToHostel().size());
        assertEquals(List.of(102L), saved.getUnassigned());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void finishAllocation_marksCompleted() throws Exception {
        String runId = "run-finish";
        HostelAllocationManager.HostelState state = HostelAllocationManager.HostelState.builder()
                .completed(false)
                .unassigned(new java.util.ArrayList<>())
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.finishAllocation(runId);

        assertEquals("Completed", result);
        HostelAllocationManager.HostelState saved = mapper.readValue(run.getStateJson(), HostelAllocationManager.HostelState.class);
        assertTrue(Boolean.TRUE.equals(saved.getCompleted()));
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

