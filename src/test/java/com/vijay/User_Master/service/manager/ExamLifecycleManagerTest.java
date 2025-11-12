package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.ExamService;
import com.vijay.User_Master.service.GradeService;
import com.vijay.User_Master.service.SchoolNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamLifecycleManagerTest {

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

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void startExamLifecycle_initializesStateAndSendsNotification() throws Exception {
        Long examId = 777L;
        Long classId = 88L;
        Long subjectId = 99L;
        Long rubricId = 111L;
        Long ownerId = 222L;

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);

            String runId = manager.startExamLifecycle(examId, classId, subjectId, rubricId);

            assertNotNull(runId);
            assertFalse(runId.isBlank());
            verify(schoolNotificationService).sendExamScheduleNotification(examId);
            verify(agentStepRepository, atLeastOnce()).save(any(AgentStep.class));

            ArgumentCaptor<AgentRun> captor = ArgumentCaptor.forClass(AgentRun.class);
            verify(agentRunRepository, atLeast(1)).save(captor.capture());
            AgentRun savedRun = captor.getValue();
            ExamLifecycleManager.ExamState state = mapper.readValue(savedRun.getStateJson(), ExamLifecycleManager.ExamState.class);
            assertEquals(examId, state.getExamId());
            assertEquals(classId, state.getClassId());
            assertEquals(subjectId, state.getSubjectId());
            assertEquals(rubricId, state.getRubricId());
            assertTrue(state.getScheduleNotified());
            assertNotNull(state.getTimestamps());
        }
    }

    @Test
    void collectSubmissions_addsIdsAndPersistsState() throws Exception {
        String runId = "run-submissions";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(1L)
                .submissionIds(new java.util.ArrayList<>())
                .gradedCount(0)
                .resultsPublished(false)
                .scheduleNotified(false)
                .reminderSent(false)
                .parentsNotified(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();

        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = manager.collectSubmissions(runId, "1, 2");

        assertEquals("Collected submissions: 2", response);
        ExamLifecycleManager.ExamState saved = mapper.readValue(run.getStateJson(), ExamLifecycleManager.ExamState.class);
        assertEquals(List.of(1L, 2L), saved.getSubmissionIds());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void aiGradeBatch_updatesGradedCount() throws Exception {
        String runId = "run-grading";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(5L)
                .rubricId(6L)
                .submissionIds(new java.util.ArrayList<>(List.of(10L, 11L)))
                .gradedCount(0)
                .resultsPublished(false)
                .scheduleNotified(true)
                .reminderSent(true)
                .parentsNotified(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .ownerId(999L)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();

        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<AIGradingResponse> gradingResponses = List.of(
                AIGradingResponse.builder().submissionId(10L).build(),
                AIGradingResponse.builder().submissionId(11L).build()
        );
        when(aiGradingService.batchGradeSubmissions(eq(state.getSubmissionIds()), eq(state.getRubricId()), eq(run.getOwnerId())))
                .thenReturn(gradingResponses);

        String result = manager.aiGradeBatch(runId);

        assertEquals("Graded submissions: 2", result);
        ExamLifecycleManager.ExamState saved = mapper.readValue(run.getStateJson(), ExamLifecycleManager.ExamState.class);
        assertEquals(2, saved.getGradedCount());
        verify(agentStepRepository).save(any(AgentStep.class));
    }

    @Test
    void notifyParents_sendsNotificationsAndMarksState() throws Exception {
        String runId = "run-notify";
        ExamLifecycleManager.ExamState state = ExamLifecycleManager.ExamState.builder()
                .examId(321L)
                .submissionIds(new java.util.ArrayList<>())
                .gradedCount(2)
                .resultsPublished(true)
                .scheduleNotified(true)
                .reminderSent(true)
                .parentsNotified(false)
                
                .build();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .build();

        when(agentRunRepository.findByRunId(runId)).thenReturn(Optional.of(run));
        when(agentRunRepository.save(any(AgentRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(schoolNotificationService).sendExamResultNotification(eq(state.getExamId()), anyLong());

        String response = manager.notifyParents(runId, "10,20");

        assertEquals("Parents notified: 2", response);
        ExamLifecycleManager.ExamState saved = mapper.readValue(run.getStateJson(), ExamLifecycleManager.ExamState.class);
        assertTrue(saved.getParentsNotified());
        verify(schoolNotificationService).sendExamResultNotification(state.getExamId(), 10L);
        verify(schoolNotificationService).sendExamResultNotification(state.getExamId(), 20L);
        verify(agentStepRepository).save(any(AgentStep.class));
    }
}

