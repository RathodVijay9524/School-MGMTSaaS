package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.dto.RouteResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.BusService;
import com.vijay.User_Master.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransportRouteAllocationManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private RouteService routeService;

    @Mock
    private BusService busService;

    @InjectMocks
    private TransportRouteAllocationManager manager;

    @Test
    void startAllocation_initializesRunAndState() {
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String runId = manager.startAllocation("1,2", "10", 999L);

        assertTrue(runId != null && !runId.isBlank());
        ArgumentCaptor<AgentRun> runCaptor = ArgumentCaptor.forClass(AgentRun.class);
        verify(agentRunRepository, atLeast(1)).save(runCaptor.capture());
        AgentRun saved = runCaptor.getValue();
        assertEquals("TransportRouteAllocation", saved.getAgentName());
        assertEquals("ingest", saved.getCurrentNode());

        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void assignByCapacity_assignsStudentsToAvailableRoutes() throws Exception {
        String runId = "run123";
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .status("RUNNING")
                .stateJson(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(TransportRouteAllocationManager.TransportState.builder()
                        .candidateStudentIds(List.of(1L, 2L))
                        .routeIds(List.of(11L))
                        .studentToRoute(new java.util.LinkedHashMap<>())
                        .busIds(Collections.emptyList())
                        .unassignedStudents(Collections.emptyList())
                        .completed(false)
                        .build()))
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        RouteResponse response = new RouteResponse();
        response.setId(11L);
        response.setAvailableSeats(1);
        when(routeService.getRouteById(eq(11L), any())).thenReturn(response);

        String result = manager.assignByCapacity(runId);

        assertEquals("Assigned: 1, Unassigned: 1", result);
        verify(agentRunRepository, atLeast(1)).save(any(AgentRun.class));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void assignByCapacity_returnsErrorWhenRunNotFound() {
        when(agentRunRepository.findByRunId("missing")).thenReturn(Optional.empty());

        String response = manager.assignByCapacity("missing");

        assertEquals("Invalid runId", response);
        verify(agentStepRepository, never()).save(any(AgentStep.class));
    }

    @Test
    void getRunState_returnsSerializedState() {
        AgentRun run = AgentRun.builder().runId("abc").stateJson("{}" ).build();
        when(agentRunRepository.findByRunId("abc")).thenReturn(Optional.of(run));

        String state = manager.getRunState("abc");
        assertEquals("{}", state);
    }

    @Test
    void getRunState_returnsInvalidMessageWhenMissing() {
        when(agentRunRepository.findByRunId("zzz")).thenReturn(Optional.empty());
        assertEquals("Invalid runId", manager.getRunState("zzz"));
    }
}

