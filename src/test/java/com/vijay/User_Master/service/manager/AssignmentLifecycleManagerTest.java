package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.dto.PeerReviewAssignmentRequest;
import com.vijay.User_Master.dto.PeerReviewAssignmentResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.PeerReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssignmentLifecycleManagerTest extends ManagerTestBase {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private PeerReviewService peerReviewService;

    @Mock
    private AIGradingService aiGradingService;

    @InjectMocks
    private AssignmentLifecycleManager manager;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupRepositoryMocks(agentRunRepository, agentStepRepository);
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void startAssignmentLifecycle_initializesState() throws Exception {
        PeerReviewAssignmentResponse mockResponse = new PeerReviewAssignmentResponse();
        when(peerReviewService.assignPeerReviews(any(PeerReviewAssignmentRequest.class), any()))
                .thenReturn(mockResponse);

        String runId = manager.startAssignmentLifecycle(100L, 3, 50L, true, false, true);

        assertNotNull(runId);
        assertFalse(runId.isBlank());
        verify(agentRunRepository, atLeast(1)).save(any(AgentRun.class));
        verify(agentStepRepository).save(any());
    }

    @Test
    void startAssignmentLifecycle_returnsValidRunId() throws Exception {
        PeerReviewAssignmentResponse mockResponse = new PeerReviewAssignmentResponse();
        when(peerReviewService.assignPeerReviews(any(PeerReviewAssignmentRequest.class), any()))
                .thenReturn(mockResponse);

        String runId = manager.startAssignmentLifecycle(100L, 3, 50L, true, false, true);

        assertTrue(runId.length() > 0);
        assertTrue(runId.matches("[a-f0-9]+"));
    }

    @Test
    void collectSubmissions_addsSubmissionsToState() throws Exception {
        String runId = "run-collect";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .submissionIds(new ArrayList<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.collectSubmissions(runId, "1, 2, 3");

        assertTrue(result.contains("Collected"));
        verify(agentRunRepository).save(any(AgentRun.class));
    }

    @Test
    void collectSubmissions_handlesEmptySubmissions() throws Exception {
        String runId = "run-empty";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .submissionIds(new ArrayList<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.collectSubmissions(runId, "");

        assertTrue(result.contains("Collected"));
    }

    @Test
    void collectSubmissions_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String result = manager.collectSubmissions("invalid", "1,2");

        assertEquals("Invalid runId", result);
    }

    @Test
    void aiGradeBatch_gradesSubmissions() throws Exception {
        String runId = "run-grade";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .submissionIds(List.of(1L, 2L, 3L))
                .rubricId(50L)
                .gradedCount(0)
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        List<AIGradingResponse> mockGrades = new ArrayList<>();
        mockGrades.add(new AIGradingResponse());
        mockGrades.add(new AIGradingResponse());
        mockGrades.add(new AIGradingResponse());
        when(aiGradingService.batchGradeSubmissions(any(), any(), any()))
                .thenReturn(mockGrades);

        String result = manager.aiGradeBatch(runId);

        assertTrue(result.contains("Graded"));
        verify(aiGradingService).batchGradeSubmissions(any(), any(), any());
    }

    @Test
    void aiGradeBatch_handlesNoSubmissions() throws Exception {
        String runId = "run-no-grade";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .submissionIds(new ArrayList<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.aiGradeBatch(runId);

        assertEquals("No submissions to grade", result);
    }

    @Test
    void aiGradeBatch_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String result = manager.aiGradeBatch("invalid");

        assertEquals("Invalid runId", result);
    }

    @Test
    void teacherReviewGate_setsWaitingState() throws Exception {
        String runId = "run-gate";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .gateWaiting(false)
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.teacherReviewGate(runId);

        assertEquals("Teacher review gate set to WAITING", result);
        verify(agentRunRepository).save(any(AgentRun.class));
    }

    @Test
    void teacherReviewGate_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String result = manager.teacherReviewGate("invalid");

        assertEquals("Invalid runId", result);
    }

    @Test
    void publishGrades_marksCompleted() throws Exception {
        String runId = "run-publish";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .completed(false)
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.publishGrades(runId);

        assertEquals("Assignment lifecycle completed", result);
        verify(agentRunRepository).save(any(AgentRun.class));
    }

    @Test
    void publishGrades_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String result = manager.publishGrades("invalid");

        assertEquals("Invalid runId", result);
    }

    @Test
    void getRunState_returnsStateJson() throws Exception {
        String runId = "run-state";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .completed(true)
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.getRunState(runId);

        assertNotNull(result);
        assertTrue(result.contains("100"));
    }

    @Test
    void getRunState_returnsInvalidMessageForNonexistentRun() {
        when(agentRunRepository.findByRunId("nonexistent")).thenReturn(Optional.empty());

        String result = manager.getRunState("nonexistent");

        assertEquals("Invalid runId", result);
    }

    @Test
    void collectSubmissions_parsesMultipleSubmissionIds() throws Exception {
        String runId = "run-parse";
        AssignmentLifecycleManager.AssignmentState state = AssignmentLifecycleManager.AssignmentState.builder()
                .assignmentId(100L)
                .submissionIds(new ArrayList<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.collectSubmissions(runId, "10, 20, 30, 40");

        assertTrue(result.contains("Collected"));
        verify(agentRunRepository).save(any(AgentRun.class));
    }
}
