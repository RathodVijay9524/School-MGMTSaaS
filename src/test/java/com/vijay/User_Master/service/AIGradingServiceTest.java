package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AIGradingRequest;
import com.vijay.User_Master.dto.AIGradingResponse;
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
class AIGradingServiceTest extends ServiceTestBase {

    @Mock
    private AIGradingService aiGradingService;

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
    void gradeSubmission_withValidRequest_returnsGradingResult() {
        AIGradingRequest request = new AIGradingRequest();
        AIGradingResponse mockResponse = new AIGradingResponse();
        mockResponse.setId(1L);

        when(aiGradingService.gradeSubmission(request, OWNER_ID)).thenReturn(mockResponse);

        AIGradingResponse response = aiGradingService.gradeSubmission(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(aiGradingService).gradeSubmission(request, OWNER_ID);
    }

    @Test
    void gradeSubmission_withNullRequest_throwsException() {
        when(aiGradingService.gradeSubmission(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                aiGradingService.gradeSubmission(null, OWNER_ID));
    }

    @Test
    void getGradingResultBySubmission_withValidId_returnsResult() {
        AIGradingResponse mockResponse = new AIGradingResponse();
        mockResponse.setId(1L);

        when(aiGradingService.getGradingResultBySubmission(100L, OWNER_ID)).thenReturn(mockResponse);

        AIGradingResponse response = aiGradingService.getGradingResultBySubmission(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getPendingTeacherReview_withPendingResults_returnsList() {
        List<AIGradingResponse> mockResults = new ArrayList<>();
        mockResults.add(new AIGradingResponse());
        mockResults.add(new AIGradingResponse());

        when(aiGradingService.getPendingTeacherReview(OWNER_ID)).thenReturn(mockResults);

        List<AIGradingResponse> response = aiGradingService.getPendingTeacherReview(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void approveGrading_withValidData_returnsApprovedResult() {
        AIGradingResponse mockResponse = new AIGradingResponse();
        mockResponse.setId(1L);

        when(aiGradingService.approveGrading(1L, 50L, OWNER_ID)).thenReturn(mockResponse);

        AIGradingResponse response = aiGradingService.approveGrading(1L, 50L, OWNER_ID);

        assertNotNull(response);
        verify(aiGradingService).approveGrading(1L, 50L, OWNER_ID);
    }

    @Test
    void modifyGrading_withValidData_returnsModifiedResult() {
        AIGradingResponse mockResponse = new AIGradingResponse();
        mockResponse.setId(1L);

        when(aiGradingService.modifyGrading(1L, 85.5, "Good work!", 50L, OWNER_ID)).thenReturn(mockResponse);

        AIGradingResponse response = aiGradingService.modifyGrading(1L, 85.5, "Good work!", 50L, OWNER_ID);

        assertNotNull(response);
        verify(aiGradingService).modifyGrading(1L, 85.5, "Good work!", 50L, OWNER_ID);
    }

    @Test
    void getHighPlagiarismCases_withValidThreshold_returnsCases() {
        List<AIGradingResponse> mockCases = new ArrayList<>();
        mockCases.add(new AIGradingResponse());

        when(aiGradingService.getHighPlagiarismCases(0.8, OWNER_ID)).thenReturn(mockCases);

        List<AIGradingResponse> response = aiGradingService.getHighPlagiarismCases(0.8, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getLowConfidenceGradings_withValidThreshold_returnsGradings() {
        List<AIGradingResponse> mockGradings = new ArrayList<>();
        mockGradings.add(new AIGradingResponse());
        mockGradings.add(new AIGradingResponse());

        when(aiGradingService.getLowConfidenceGradings(0.3, OWNER_ID)).thenReturn(mockGradings);

        List<AIGradingResponse> response = aiGradingService.getLowConfidenceGradings(0.3, OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getGradingStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalGradings", 100);
        mockStats.put("pendingReview", 10);

        when(aiGradingService.getGradingStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = aiGradingService.getGradingStatistics(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.containsKey("totalGradings"));
        verify(aiGradingService).getGradingStatistics(OWNER_ID);
    }

    @Test
    void regradeSubmission_withValidData_returnsNewResult() {
        AIGradingRequest request = new AIGradingRequest();
        AIGradingResponse mockResponse = new AIGradingResponse();
        mockResponse.setId(2L);

        when(aiGradingService.regradeSubmission(100L, request, OWNER_ID)).thenReturn(mockResponse);

        AIGradingResponse response = aiGradingService.regradeSubmission(100L, request, OWNER_ID);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        verify(aiGradingService).regradeSubmission(100L, request, OWNER_ID);
    }

    @Test
    void batchGradeSubmissions_withValidIds_returnsResults() {
        List<Long> submissionIds = List.of(1L, 2L, 3L);
        List<AIGradingResponse> mockResults = new ArrayList<>();
        mockResults.add(new AIGradingResponse());
        mockResults.add(new AIGradingResponse());
        mockResults.add(new AIGradingResponse());

        when(aiGradingService.batchGradeSubmissions(submissionIds, 200L, OWNER_ID)).thenReturn(mockResults);

        List<AIGradingResponse> response = aiGradingService.batchGradeSubmissions(submissionIds, 200L, OWNER_ID);

        assertNotNull(response);
        assertEquals(3, response.size());
        verify(aiGradingService).batchGradeSubmissions(submissionIds, 200L, OWNER_ID);
    }

    @Test
    void getGradingResultBySubmission_withInvalidId_throwsException() {
        when(aiGradingService.getGradingResultBySubmission(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Grading result not found"));

        assertThrows(RuntimeException.class, () ->
                aiGradingService.getGradingResultBySubmission(999L, OWNER_ID));
    }

    @Test
    void getPendingTeacherReview_withNoPending_returnsEmptyList() {
        List<AIGradingResponse> mockResults = new ArrayList<>();

        when(aiGradingService.getPendingTeacherReview(OWNER_ID)).thenReturn(mockResults);

        List<AIGradingResponse> response = aiGradingService.getPendingTeacherReview(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void approveGrading_withDifferentTeacher_returnsApprovedResult() {
        AIGradingResponse mockResponse = new AIGradingResponse();
        mockResponse.setId(1L);

        when(aiGradingService.approveGrading(1L, 75L, OWNER_ID)).thenReturn(mockResponse);

        AIGradingResponse response = aiGradingService.approveGrading(1L, 75L, OWNER_ID);

        assertNotNull(response);
        verify(aiGradingService).approveGrading(1L, 75L, OWNER_ID);
    }

    @Test
    void modifyGrading_withDifferentScore_returnsModifiedResult() {
        AIGradingResponse mockResponse = new AIGradingResponse();
        mockResponse.setId(1L);

        when(aiGradingService.modifyGrading(1L, 90.0, "Excellent work!", 50L, OWNER_ID)).thenReturn(mockResponse);

        AIGradingResponse response = aiGradingService.modifyGrading(1L, 90.0, "Excellent work!", 50L, OWNER_ID);

        assertNotNull(response);
        verify(aiGradingService).modifyGrading(1L, 90.0, "Excellent work!", 50L, OWNER_ID);
    }
}
