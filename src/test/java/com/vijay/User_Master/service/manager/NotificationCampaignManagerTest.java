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
class NotificationCampaignManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @InjectMocks
    private NotificationCampaignManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void start_createsInitialCampaignState() throws Exception {
        long ownerId = 222L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.start("Fee Reminder", "email", "class", null);

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            ArgumentCaptor<AgentRun> captor = ArgumentCaptor.forClass(AgentRun.class);
            verify(agentRunRepository, atLeast(1)).save(captor.capture());
            AgentRun saved = captor.getValue();
            NotificationCampaignManager.CampaignState state = mapper.readValue(saved.getStateJson(), NotificationCampaignManager.CampaignState.class);
            assertEquals("Fee Reminder", state.getCampaignName());
            assertEquals("EMAIL", state.getChannel());
            assertEquals("CLASS", state.getAudienceType());
            assertEquals(0, state.getTargetCount());
            assertTrue(state.getTimestamps().containsKey("STARTED"));
        }
    }

    @Test
    void selectAudience_updatesTargetCount() throws Exception {
        String runId = "run-audience";
        NotificationCampaignManager.CampaignState state = NotificationCampaignManager.CampaignState.builder()
                .classIds(new java.util.ArrayList<>(java.util.List.of(1L)))
                .studentIds(new java.util.ArrayList<>(java.util.List.of(10L)))
                .targetCount(0)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.selectAudience(runId, "2", "11,12");

        assertEquals("Target audience: 62", response);
        NotificationCampaignManager.CampaignState saved = mapper.readValue(run.getStateJson(), NotificationCampaignManager.CampaignState.class);
        assertEquals(java.util.List.of(1L, 2L), saved.getClassIds());
        assertEquals(java.util.List.of(10L, 11L, 12L), saved.getStudentIds());
        assertEquals(Integer.valueOf(62), saved.getTargetCount());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void schedule_setsScheduledTimestampOnce() throws Exception {
        String runId = "run-schedule";
        NotificationCampaignManager.CampaignState state = NotificationCampaignManager.CampaignState.builder()
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.schedule(runId, "2025-01-01T10:00:00");

        assertEquals("Scheduled at: 2025-01-01T10:00:00", result);
        NotificationCampaignManager.CampaignState saved = mapper.readValue(run.getStateJson(), NotificationCampaignManager.CampaignState.class);
        assertEquals("2025-01-01T10:00:00", saved.getScheduledAt());
        assertTrue(saved.getTimestamps().containsKey("SCHEDULED"));
        verify(agentStepRepository).save(any(AgentStep.class));

        assertEquals("Already scheduled at: 2025-01-01T10:00:00", manager.schedule(runId, "2025-01-02"));
    }

    @Test
    void send_marksCompletionAndCountsResults() throws Exception {
        String runId = "run-send";
        NotificationCampaignManager.CampaignState state = NotificationCampaignManager.CampaignState.builder()
                .targetCount(100)
                .sentCount(0)
                .failedCount(0)
                .completed(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String summary = manager.send(runId);

        assertEquals("Sent: 95, Failed: 5", summary);
        NotificationCampaignManager.CampaignState saved = mapper.readValue(run.getStateJson(), NotificationCampaignManager.CampaignState.class);
        assertEquals(Integer.valueOf(95), saved.getSentCount());
        assertEquals(Integer.valueOf(5), saved.getFailedCount());
        assertTrue(Boolean.TRUE.equals(saved.getCompleted()));
        assertTrue(saved.getTimestamps().containsKey("SENT"));
        verify(agentStepRepository).save(any(AgentStep.class));

        assertEquals("Already sent: sent=95", manager.send(runId));
    }

    @Test
    void stats_returnsSummaryJson() throws Exception {
        String runId = "run-stats";
        NotificationCampaignManager.CampaignState state = NotificationCampaignManager.CampaignState.builder()
                .campaignName("Exam Reminder")
                .channel("SMS")
                .audienceType("ALL")
                .targetCount(200)
                .sentCount(190)
                .failedCount(10)
                .completed(true)
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("COMPLETED")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));

        String stats = manager.stats(runId);

        assertTrue(stats.contains("\"campaignName\":\"Exam Reminder\""));
        assertTrue(stats.contains("\"sent\":190"));
    }
}

