package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.EventResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.EventService;
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
class EventTripOrchestrationManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventTripOrchestrationManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void startOrchestration_populatesStateFromEventService() throws Exception {
        long ownerId = 222L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        EventResponse response = EventResponse.builder()
                .id(999L)
                .audience(EventResponse.builder().build().getAudience())
                .expectedParticipants(120)
                .requiresRegistration(true)
                .build();
        when(eventService.getEventById(999L, ownerId)).thenReturn(response);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.startOrchestration(999L);

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            AgentRun saved = agentRunRepository.save(AgentRun.builder().runId(runId).build());
            EventTripOrchestrationManager.EventState state = mapper.readValue(saved.getStateJson(), EventTripOrchestrationManager.EventState.class);
            assertEquals(Long.valueOf(999L), state.getEventId());
            assertEquals(Integer.valueOf(120), state.getExpectedParticipants());
        }
    }

    @Test
    void openRegistration_recordsRegisteredCount() throws Exception {
        String runId = "run-reg";
        EventTripOrchestrationManager.EventState state = EventTripOrchestrationManager.EventState.builder()
                .eventId(500L)
                .registeredCount(0)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .ownerId(777L)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(eventService.getEventById(500L, 777L)).thenReturn(EventResponse.builder().registeredParticipants(45).build());

        String result = manager.openRegistration(runId);

        assertEquals("Registered: 45", result);
        EventTripOrchestrationManager.EventState saved = mapper.readValue(run.getStateJson(), EventTripOrchestrationManager.EventState.class);
        assertEquals(Integer.valueOf(45), saved.getRegisteredCount());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void buildRoster_calculatesGoalFromRegistered() throws Exception {
        String runId = "run-roster";
        EventTripOrchestrationManager.EventState state = EventTripOrchestrationManager.EventState.builder()
                .expectedParticipants(100)
                .registeredCount(80)
                .rosterCount(0)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.buildRoster(runId, null);

        assertEquals("Roster count: 80", response);
        EventTripOrchestrationManager.EventState saved = mapper.readValue(run.getStateJson(), EventTripOrchestrationManager.EventState.class);
        assertEquals(Integer.valueOf(80), saved.getRosterCount());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void dispatch_flagsState() throws Exception {
        String runId = "run-dispatch";
        EventTripOrchestrationManager.EventState state = EventTripOrchestrationManager.EventState.builder()
                .dispatched(false)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.dispatch(runId);

        assertEquals("Dispatched", result);
        EventTripOrchestrationManager.EventState saved = mapper.readValue(run.getStateJson(), EventTripOrchestrationManager.EventState.class);
        assertTrue(Boolean.TRUE.equals(saved.getDispatched()));
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void postReport_marksCompletedAndCreatesReport() throws Exception {
        String runId = "run-report";
        EventTripOrchestrationManager.EventState state = EventTripOrchestrationManager.EventState.builder()
                .eventId(123L)
                .registeredCount(70)
                .rosterCount(65)
                .dispatched(true)
                .completed(false)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.postReport(runId);

        assertEquals("Event/trip orchestration completed", result);
        EventTripOrchestrationManager.EventState saved = mapper.readValue(run.getStateJson(), EventTripOrchestrationManager.EventState.class);
        assertTrue(Boolean.TRUE.equals(saved.getCompleted()));
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

