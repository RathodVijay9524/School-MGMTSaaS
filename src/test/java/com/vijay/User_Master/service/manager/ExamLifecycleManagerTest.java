package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.ExamService;
import com.vijay.User_Master.service.GradeService;
import com.vijay.User_Master.service.SchoolNotificationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExamLifecycleManagerTest extends ManagerTestBase {

    @Mock
    private AgentRunRepository agentRunRepository;

    @Mock
    private AgentStepRepository agentStepRepository;

    @Mock
    private ExamService examService;

    @Mock
    private GradeService gradeService;

    @Mock
    private AIGradingService aiGradingService;

    @Mock
    private SchoolNotificationService schoolNotificationService;

    @InjectMocks
    private ExamLifecycleManager manager;

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
    void startExamLifecycle_initializesState() throws Exception {
        String runId = manager.startExamLifecycle(100L, 5L, 10L, 50L);

        assertNotNull(runId);
        assertFalse(runId.isBlank());
        verify(agentRunRepository, atLeast(1)).save(any(AgentRun.class));
        verify(agentStepRepository).save(any());
    }

    @Test
    void startExamLifecycle_returnsValidRunId() throws Exception {
        String runId = manager.startExamLifecycle(100L, 5L, 10L, 50L);

        assertTrue(runId.length() > 0);
        assertTrue(runId.matches("[a-f0-9]+"));
    }

    @Test
    void startExamLifecycle_notifiesSchedule() throws Exception {
        String runId = manager.startExamLifecycle(100L, 5L, 10L, 50L);

        assertNotNull(runId);
        verify(schoolNotificationService).sendExamScheduleNotification(100L);
    }

    @Test
    void sendExamReminder_sendsReminder() throws Exception {
        String runId = "run-reminder";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .reminderSent(false)
                .timestamps(new HashMap<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.sendExamReminder(runId);

        assertEquals("Reminder sent", result);
        verify(schoolNotificationService).sendExamReminder(100L);
    }

    @Test
    void sendExamReminder_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String result = manager.sendExamReminder("invalid");

        assertEquals("Invalid runId", result);
    }

    @Test
    void collectSubmissions_addsSubmissionsToState() throws Exception {
        String runId = "run-collect";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .submissionIds(new ArrayList<>())
                .timestamps(new HashMap<>())
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
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .submissionIds(new ArrayList<>())
                .timestamps(new HashMap<>())
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
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .submissionIds(List.of(1L, 2L, 3L))
                .rubricId(50L)
                .gradedCount(0)
                .timestamps(new HashMap<>())
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
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .submissionIds(new ArrayList<>())
                .timestamps(new HashMap<>())
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
    void publishResults_publishesExamResults() throws Exception {
        String runId = "run-publish";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .resultsPublished(false)
                .timestamps(new HashMap<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.publishResults(runId);

        assertEquals("Results published", result);
        verify(examService).publishExamResults(100L, OWNER_ID);
    }

    @Test
    void publishResults_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String result = manager.publishResults("invalid");

        assertEquals("Invalid runId", result);
    }

    @Test
    void notifyParents_notifiesStudents() throws Exception {
        String runId = "run-notify";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .parentsNotified(false)
                .timestamps(new HashMap<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.notifyParents(runId, "1, 2, 3");

        assertTrue(result.contains("Parents notified"));
        verify(schoolNotificationService, atLeast(1)).sendExamResultNotification(any(), any());
    }

    @Test
    void notifyParents_handlesEmptyStudentList() throws Exception {
        String runId = "run-notify-empty";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .parentsNotified(false)
                .timestamps(new HashMap<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.notifyParents(runId, "");

        assertTrue(result.contains("Parents notified"));
    }

    @Test
    void notifyParents_returnsInvalidRunIdMessage() {
        when(agentRunRepository.findByRunId("invalid")).thenReturn(Optional.empty());

        String result = manager.notifyParents("invalid", "1,2");

        assertEquals("Invalid runId", result);
    }

    @Test
    void getRunState_returnsStateJson() throws Exception {
        String runId = "run-state";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .resultsPublished(true)
                .timestamps(new HashMap<>())
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
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(100L)
                .submissionIds(new ArrayList<>())
                .timestamps(new HashMap<>())
                .build();

        AgentRun run = createTestAgentRun(runId, state);
        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));

        String result = manager.collectSubmissions(runId, "10, 20, 30, 40");

        assertTrue(result.contains("Collected"));
        verify(agentRunRepository).save(any(AgentRun.class));
    }
}
