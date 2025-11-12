package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.LearningInteractionRequest;
import com.vijay.User_Master.dto.SkillMasteryResponse;
import com.vijay.User_Master.service.AcademicTutoringService;
import com.vijay.User_Master.service.AdaptiveLearningService;
import com.vijay.User_Master.service.AIGradingService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvancedTutorAgentManagerTest {

    @Mock
    private AcademicTutoringService academicTutoringService;

    @Mock
    private AdaptiveLearningService adaptiveLearningService;

    @Mock
    private AIGradingService aiGradingService;

    @InjectMocks
    private AdvancedTutorAgentManager manager;

    @Test
    void runTutor_updatesMasteryFromAdaptiveServiceAndStopsEarly() {
        long ownerId = 42L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);

            when(academicTutoringService.explainConcept(anyString(), anyString(), anyString()))
                    .thenReturn("Concept explanation");
            when(academicTutoringService.generatePracticeProblems(anyString(), anyString(), anyString(), eq(1)))
                    .thenReturn("Practice question");

            SkillMasteryResponse masteryResponse = SkillMasteryResponse.builder()
                    .masteryLevel(92.5)
                    .build();
            when(adaptiveLearningService.recordInteraction(any(LearningInteractionRequest.class), eq(ownerId)))
                    .thenReturn(masteryResponse);

            String summary = manager.runTutor(7L, "fractions", "Grade 7", 5);

            assertTrue(summary.contains("iterations=1"));
            assertTrue(summary.contains("mastery=92.5%"));

            ArgumentCaptor<LearningInteractionRequest> requestCaptor = ArgumentCaptor.forClass(LearningInteractionRequest.class);
            verify(adaptiveLearningService).recordInteraction(requestCaptor.capture(), eq(ownerId));
            LearningInteractionRequest captured = requestCaptor.getValue();
            assertEquals(7L, captured.getStudentId());
            assertEquals("fractions", captured.getSkillKey());
            assertEquals("MEDIUM", captured.getDifficulty());
            assertEquals("CORRECT", captured.getOutcome());
        }

        verifyNoInteractions(aiGradingService);
    }

    @Test
    void runTutor_usesFallbackMasteryIncrementWhenAdaptiveServiceFails() {
        long ownerId = 84L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);

            when(academicTutoringService.explainConcept(anyString(), anyString(), anyString()))
                    .thenReturn("Explain");
            when(academicTutoringService.generatePracticeProblems(anyString(), anyString(), anyString(), eq(1)))
                    .thenReturn("Question");

            when(adaptiveLearningService.recordInteraction(any(LearningInteractionRequest.class), eq(ownerId)))
                    .thenThrow(new RuntimeException("service down"));

            String summary = manager.runTutor(11L, "algebra", "Grade 8", 1);

            assertTrue(summary.contains("iterations=1"));
            assertTrue(summary.contains("mastery=20.0%"));
        }

        verifyNoInteractions(aiGradingService);
    }
}

