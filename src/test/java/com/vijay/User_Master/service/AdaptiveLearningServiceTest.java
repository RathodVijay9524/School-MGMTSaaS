package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AdaptiveRecommendationResponse;
import com.vijay.User_Master.dto.LearningInteractionRequest;
import com.vijay.User_Master.dto.SkillMasteryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdaptiveLearningServiceTest extends ServiceTestBase {

    @Mock
    private AdaptiveLearningService adaptiveService;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void getNextModule_withValidStudentAndSubject_returnsRecommendation() {
        AdaptiveRecommendationResponse mockResponse = new AdaptiveRecommendationResponse();
        mockResponse.setModuleId(1L);

        when(adaptiveService.getNextModule(100L, 1L, OWNER_ID)).thenReturn(mockResponse);

        AdaptiveRecommendationResponse response = adaptiveService.getNextModule(100L, 1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getModuleId());
        verify(adaptiveService).getNextModule(100L, 1L, OWNER_ID);
    }

    @Test
    void recordInteraction_withValidRequest_returnsSkillMastery() {
        LearningInteractionRequest request = new LearningInteractionRequest();
        SkillMasteryResponse mockResponse = new SkillMasteryResponse();
        mockResponse.setSkillKey("algebra");

        when(adaptiveService.recordInteraction(request, OWNER_ID)).thenReturn(mockResponse);

        SkillMasteryResponse response = adaptiveService.recordInteraction(request, OWNER_ID);

        assertNotNull(response);
        assertEquals("algebra", response.getSkillKey());
    }

    @Test
    void getReviewQueue_withValidStudent_returnsReviewModules() {
        List<AdaptiveRecommendationResponse> mockQueue = new ArrayList<>();
        mockQueue.add(new AdaptiveRecommendationResponse());

        when(adaptiveService.getReviewQueue(100L, OWNER_ID)).thenReturn(mockQueue);

        List<AdaptiveRecommendationResponse> response = adaptiveService.getReviewQueue(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getReviewQueue_withEmptyQueue_returnsEmptyList() {
        List<AdaptiveRecommendationResponse> mockQueue = new ArrayList<>();

        when(adaptiveService.getReviewQueue(100L, OWNER_ID)).thenReturn(mockQueue);

        List<AdaptiveRecommendationResponse> response = adaptiveService.getReviewQueue(100L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void canAccessModule_withMetPrerequisites_returnsTrue() {
        when(adaptiveService.canAccessModule(100L, 1L)).thenReturn(true);

        boolean response = adaptiveService.canAccessModule(100L, 1L);

        assertTrue(response);
    }

    @Test
    void canAccessModule_withUnmetPrerequisites_returnsFalse() {
        when(adaptiveService.canAccessModule(100L, 1L)).thenReturn(false);

        boolean response = adaptiveService.canAccessModule(100L, 1L);

        assertFalse(response);
    }

    @Test
    void getDiagnosticAssessment_withValidStudent_returnsAssessment() {
        List<AdaptiveRecommendationResponse> mockAssessment = new ArrayList<>();
        mockAssessment.add(new AdaptiveRecommendationResponse());

        when(adaptiveService.getDiagnosticAssessment(100L, 1L, OWNER_ID)).thenReturn(mockAssessment);

        List<AdaptiveRecommendationResponse> response = adaptiveService.getDiagnosticAssessment(100L, 1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRemedialContent_withValidSkill_returnsContent() {
        List<AdaptiveRecommendationResponse> mockContent = new ArrayList<>();
        mockContent.add(new AdaptiveRecommendationResponse());

        when(adaptiveService.getRemedialContent(100L, "algebra", OWNER_ID)).thenReturn(mockContent);

        List<AdaptiveRecommendationResponse> response = adaptiveService.getRemedialContent(100L, "algebra", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentMastery_withValidStudent_returnsMastery() {
        List<SkillMasteryResponse> mockMastery = new ArrayList<>();
        mockMastery.add(new SkillMasteryResponse());

        when(adaptiveService.getStudentMastery(100L, 1L, OWNER_ID)).thenReturn(mockMastery);

        List<SkillMasteryResponse> response = adaptiveService.getStudentMastery(100L, 1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getMasteryHeatmap_withValidSubject_returnsHeatmap() {
        Map<String, Object> mockHeatmap = new HashMap<>();
        mockHeatmap.put("totalSkills", 10);

        when(adaptiveService.getMasteryHeatmap(1L, OWNER_ID)).thenReturn(mockHeatmap);

        Map<String, Object> response = adaptiveService.getMasteryHeatmap(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(10, response.get("totalSkills"));
    }

    @Test
    void getVelocityTrends_withValidStudent_returnsTrends() {
        Map<String, Object> mockTrends = new HashMap<>();
        mockTrends.put("averageVelocity", 0.85);

        when(adaptiveService.getVelocityTrends(100L, OWNER_ID)).thenReturn(mockTrends);

        Map<String, Object> response = adaptiveService.getVelocityTrends(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(0.85, response.get("averageVelocity"));
    }

    @Test
    void adjustMastery_withValidData_returnsAdjustedMastery() {
        SkillMasteryResponse mockResponse = new SkillMasteryResponse();
        mockResponse.setMasteryLevel(0.9);

        when(adaptiveService.adjustMastery(100L, "algebra", 0.9, "teacher override", OWNER_ID))
                .thenReturn(mockResponse);

        SkillMasteryResponse response = adaptiveService.adjustMastery(100L, "algebra", 0.9, "teacher override", OWNER_ID);

        assertNotNull(response);
        assertEquals(0.9, response.getMasteryLevel());
    }

    @Test
    void resetSkillMastery_withValidData_succeeds() {
        doNothing().when(adaptiveService).resetSkillMastery(100L, "algebra", OWNER_ID);

        adaptiveService.resetSkillMastery(100L, "algebra", OWNER_ID);

        verify(adaptiveService).resetSkillMastery(100L, "algebra", OWNER_ID);
    }

    @Test
    void getSkillsNeedingAttention_withValidStudent_returnsSkills() {
        List<SkillMasteryResponse> mockSkills = new ArrayList<>();
        mockSkills.add(new SkillMasteryResponse());

        when(adaptiveService.getSkillsNeedingAttention(100L, OWNER_ID)).thenReturn(mockSkills);

        List<SkillMasteryResponse> response = adaptiveService.getSkillsNeedingAttention(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getMasteredSkills_withValidStudent_returnsMasteredSkills() {
        List<SkillMasteryResponse> mockSkills = new ArrayList<>();
        mockSkills.add(new SkillMasteryResponse());

        when(adaptiveService.getMasteredSkills(100L, OWNER_ID)).thenReturn(mockSkills);

        List<SkillMasteryResponse> response = adaptiveService.getMasteredSkills(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }
}
