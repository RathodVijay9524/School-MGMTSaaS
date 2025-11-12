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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IDCardIssuanceManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @InjectMocks
    private IDCardIssuanceManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void start_createsRunAndInitialState() throws Exception {
        long ownerId = 501L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.start("Batch-A");

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            ArgumentCaptor<AgentRun> runCaptor = ArgumentCaptor.forClass(AgentRun.class);
            verify(agentRunRepository, atLeast(1)).save(runCaptor.capture());
            AgentRun saved = runCaptor.getValue();
            IDCardIssuanceManager.IDState state = mapper.readValue(saved.getStateJson(), IDCardIssuanceManager.IDState.class);
            assertEquals("Batch-A", state.getBatchName());
            assertEquals(0, state.getRendered());
            assertEquals(0, state.getPrinted());
            assertEquals(0, state.getDistributed());
            assertTrue(state.getTimestamps().containsKey("STARTED"));
        }
    }

    @Test
    void start_requiresBatchName() {
        assertEquals("batchName is required", manager.start(" "));
        verifyNoInteractions(agentRunRepository, agentStepRepository);
    }

    @Test
    void ingestStudents_appendsCandidates() throws Exception {
        String runId = "run-ingest";
        IDCardIssuanceManager.IDState state = IDCardIssuanceManager.IDState.builder()
                .studentIds(new java.util.ArrayList<>(java.util.List.of(1L)))
                .rendered(0)
                .printed(0)
                .distributed(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.ingestStudents(runId, "2,3");

        assertEquals("Candidates: 3", result);
        IDCardIssuanceManager.IDState saved = mapper.readValue(run.getStateJson(), IDCardIssuanceManager.IDState.class);
        assertEquals(java.util.List.of(1L, 2L, 3L), saved.getStudentIds());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void render_setsRenderedCountOnce() throws Exception {
        String runId = "run-render";
        IDCardIssuanceManager.IDState state = IDCardIssuanceManager.IDState.builder()
                .studentIds(java.util.List.of(1L, 2L))
                .rendered(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.render(runId);

        assertEquals("Rendered: 2", response);
        IDCardIssuanceManager.IDState saved = mapper.readValue(run.getStateJson(), IDCardIssuanceManager.IDState.class);
        assertEquals(2, saved.getRendered());
        verify(agentStepRepository).save(any(AgentStep.class));

        // calling again should early exit
        assertEquals("Already rendered: 2", manager.render(runId));
    }

    @Test
    void print_requiresRenderBeforePrinting() throws Exception {
        String runId = "run-print";
        IDCardIssuanceManager.IDState state = IDCardIssuanceManager.IDState.builder()
                .studentIds(java.util.List.of(1L))
                .rendered(0)
                .printed(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));

        assertEquals("Nothing to print: not rendered", manager.print(runId, 10));
    }

    @Test
    void print_setsPrintedCount() throws Exception {
        String runId = "run-print2";
        IDCardIssuanceManager.IDState state = IDCardIssuanceManager.IDState.builder()
                .studentIds(java.util.List.of(1L, 2L, 3L))
                .rendered(3)
                .printed(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.print(runId, 2);

        assertEquals("Printed: 3", response);
        IDCardIssuanceManager.IDState saved = mapper.readValue(run.getStateJson(), IDCardIssuanceManager.IDState.class);
        assertEquals(3, saved.getPrinted());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void distribute_completesWhenPrinted() throws Exception {
        String runId = "run-distribute";
        IDCardIssuanceManager.IDState state = IDCardIssuanceManager.IDState.builder()
                .studentIds(java.util.List.of(1L))
                .rendered(1)
                .printed(1)
                .distributed(0)
                .completed(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.distribute(runId);

        assertEquals("Distributed: 1", result);
        IDCardIssuanceManager.IDState saved = mapper.readValue(run.getStateJson(), IDCardIssuanceManager.IDState.class);
        assertEquals(1, saved.getDistributed());
        assertTrue(Boolean.TRUE.equals(saved.getCompleted()));
        assertTrue(saved.getTimestamps().containsKey("COMPLETED"));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void distribute_requiresPrintedCards() {
        String runId = "run-distribute-fail";
        IDCardIssuanceManager.IDState state = IDCardIssuanceManager.IDState.builder()
                .printed(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson("{}")
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));

        assertEquals("Nothing to distribute: not printed", manager.distribute(runId));
    }
}

