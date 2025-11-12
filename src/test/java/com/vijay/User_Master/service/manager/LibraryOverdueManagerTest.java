package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.BookIssueResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.BookIssueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryOverdueManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private BookIssueService bookIssueService;

    @InjectMocks
    private LibraryOverdueManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void startOverdueRun_populatesStateWithOverdueAndDueToday() throws Exception {
        long ownerId = 444L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        BookIssueResponse overdue1 = BookIssueResponse.builder().id(10L).build();
        BookIssueResponse overdue2 = BookIssueResponse.builder().id(11L).build();
        BookIssueResponse dueToday = BookIssueResponse.builder().id(20L).build();

        when(bookIssueService.getOverdueBooks(ownerId)).thenReturn(List.of(overdue1, overdue2));
        when(bookIssueService.getBooksDueToday(ownerId)).thenReturn(List.of(dueToday));

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.startOverdueRun(true, 5);

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            AgentRun saved = agentRunRepository.save(AgentRun.builder().runId(runId).build());
            LibraryOverdueManager.LibraryOverdueState state = mapper.readValue(saved.getStateJson(), LibraryOverdueManager.LibraryOverdueState.class);
            assertEquals(List.of(10L, 11L), state.getOverdueIssueIds());
            assertEquals(List.of(20L), state.getDueTodayIssueIds());
            assertEquals(Integer.valueOf(5), state.getAutoExtendDays());
        }
    }

    @Test
    void autoExtendDueToday_callsRenewAndTracksExtended() throws Exception {
        String runId = "run-auto-extend";
        LibraryOverdueManager.LibraryOverdueState state = LibraryOverdueManager.LibraryOverdueState.builder()
                .dueTodayIssueIds(List.of(101L, 102L))
                .extendedIssueIds(new java.util.ArrayList<>())
                .autoExtendDays(3)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .ownerId(555L)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookIssueResponse renewed = BookIssueResponse.builder()
                .id(101L)
                .status(com.vijay.User_Master.entity.BookIssue.IssueStatus.RENEWED)
                .build();
        when(bookIssueService.renewBook(101L, 3, 555L)).thenReturn(renewed);
        when(bookIssueService.renewBook(102L, 3, 555L)).thenReturn(null);

        String result = manager.autoExtendDueToday(runId, null);

        assertEquals("Auto-extended: 1", result);
        LibraryOverdueManager.LibraryOverdueState saved = mapper.readValue(run.getStateJson(), LibraryOverdueManager.LibraryOverdueState.class);
        assertEquals(List.of(101L), saved.getExtendedIssueIds());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void applyFines_accumulatesTotals() throws Exception {
        String runId = "run-fines";
        LibraryOverdueManager.LibraryOverdueState state = LibraryOverdueManager.LibraryOverdueState.builder()
                .overdueIssueIds(List.of(200L, 201L))
                .calculatedFines(new java.util.HashMap<>())
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .ownerId(600L)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(bookIssueService.calculateFine(200L, 600L)).thenReturn(10.0);
        when(bookIssueService.calculateFine(201L, 600L)).thenReturn(5.5);

        String response = manager.applyFines(runId);

        assertEquals("Calculated total fines: 15.5", response);
        LibraryOverdueManager.LibraryOverdueState saved = mapper.readValue(run.getStateJson(), LibraryOverdueManager.LibraryOverdueState.class);
        assertEquals(Map.of(200L, 10.0, 201L, 5.5), saved.getCalculatedFines());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void notifyBorrowers_marksNotificationsSent() throws Exception {
        String runId = "run-notify";
        LibraryOverdueManager.LibraryOverdueState state = LibraryOverdueManager.LibraryOverdueState.builder()
                .notificationsSent(false)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.notifyBorrowers(runId);

        assertEquals("Notifications sent", result);
        LibraryOverdueManager.LibraryOverdueState saved = mapper.readValue(run.getStateJson(), LibraryOverdueManager.LibraryOverdueState.class);
        assertTrue(Boolean.TRUE.equals(saved.getNotificationsSent()));
        verify(bookIssueService).sendOverdueNotifications();
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void finishRun_setsCompletedFlag() throws Exception {
        String runId = "run-finish";
        LibraryOverdueManager.LibraryOverdueState state = LibraryOverdueManager.LibraryOverdueState.builder()
                .completed(false)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.finishRun(runId);

        assertEquals("Library overdue process completed", response);
        LibraryOverdueManager.LibraryOverdueState saved = mapper.readValue(run.getStateJson(), LibraryOverdueManager.LibraryOverdueState.class);
        assertTrue(Boolean.TRUE.equals(saved.getCompleted()));
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

