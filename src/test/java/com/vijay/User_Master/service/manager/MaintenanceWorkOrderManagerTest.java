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
class MaintenanceWorkOrderManagerTest {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @InjectMocks
    private MaintenanceWorkOrderManager manager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void start_createsWorkOrderState() throws Exception {
        long ownerId = 123L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);
            when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String runId = manager.start("Fix AC", "AC not cooling", 1500.0);

            assertTrue(runId != null && !runId.isBlank());
            verify(agentStepRepository).save(any(AgentStep.class));

            AgentRun saved = agentRunRepository.save(AgentRun.builder().runId(runId).build());
            MaintenanceWorkOrderManager.WOState state = mapper.readValue(saved.getStateJson(), MaintenanceWorkOrderManager.WOState.class);
            assertEquals("Fix AC", state.getTitle());
            assertEquals("AC not cooling", state.getDescription());
            assertEquals(Double.valueOf(1500.0), state.getCostEstimate());
            assertEquals("CREATED", state.getStage());
            assertTrue(state.getTimestamps().containsKey("CREATED"));
        }
    }

    @Test
    void start_requiresTitleAndDescription() {
        assertEquals("Title is required", manager.start(" ", "desc", 0.0));
        assertEquals("Description is required", manager.start("Fix", " ", 0.0));
        verifyNoInteractions(agentRunRepository, agentStepRepository);
    }

    @Test
    void approve_updatesStateOnce() throws Exception {
        String runId = "run-approve";
        MaintenanceWorkOrderManager.WOState state = MaintenanceWorkOrderManager.WOState.builder()
                .approved(false)
                .stage("CREATED")
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = manager.approve(runId, 77L);

        assertEquals("Approved", result);
        MaintenanceWorkOrderManager.WOState saved = mapper.readValue(run.getStateJson(), MaintenanceWorkOrderManager.WOState.class);
        assertTrue(Boolean.TRUE.equals(saved.getApproved()));
        assertEquals("APPROVED", saved.getStage());
        verify(agentStepRepository).save(any(AgentStep.class));

        assertEquals("Already approved", manager.approve(runId, 77L));
    }

    @Test
    void assign_requiresApproval() throws Exception {
        String runId = "run-assign";
        MaintenanceWorkOrderManager.WOState state = MaintenanceWorkOrderManager.WOState.builder()
                .approved(false)
                .stage("CREATED")
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));

        assertEquals("Cannot assign: not approved", manager.assign(runId, 88L));
    }

    @Test
    void assign_setsAssigneeWhenApproved() throws Exception {
        String runId = "run-assign-ok";
        MaintenanceWorkOrderManager.WOState state = MaintenanceWorkOrderManager.WOState.builder()
                .approved(true)
                .stage("APPROVED")
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.assign(runId, 99L);

        assertEquals("Assigned", response);
        MaintenanceWorkOrderManager.WOState saved = mapper.readValue(run.getStateJson(), MaintenanceWorkOrderManager.WOState.class);
        assertEquals(Long.valueOf(99L), saved.getAssigneeUserId());
        assertEquals("ASSIGNED", saved.getStage());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void complete_checksStageAndMarksDone() throws Exception {
        String runId = "run-complete";
        MaintenanceWorkOrderManager.WOState state = MaintenanceWorkOrderManager.WOState.builder()
                .approved(true)
                .stage("ASSIGNED")
                .completed(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();
        when(agentRunRepository.findByRunId(runId)).thenReturn(java.util.Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String res = manager.complete(runId);

        assertEquals("Completed", res);
        MaintenanceWorkOrderManager.WOState saved = mapper.readValue(run.getStateJson(), MaintenanceWorkOrderManager.WOState.class);
        assertTrue(Boolean.TRUE.equals(saved.getCompleted()));
        assertEquals("COMPLETED", saved.getStage());
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

