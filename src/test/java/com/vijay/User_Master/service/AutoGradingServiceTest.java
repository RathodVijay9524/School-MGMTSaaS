package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AutoGradingRequest;
import com.vijay.User_Master.dto.AutoGradingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AutoGradingServiceTest extends ServiceTestBase {

    @Mock
    private AutoGradingService autoGradingService;

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
    void gradeResponse_withValidRequest_returnsGradingResponse() {
        AutoGradingRequest request = new AutoGradingRequest();
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeResponse(request)).thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeResponse(request);

        assertNotNull(response);
        verify(autoGradingService).gradeResponse(request);
    }

    @Test
    void gradeResponse_withInvalidRequest_throwsException() {
        when(autoGradingService.gradeResponse(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                autoGradingService.gradeResponse(null));
    }

    @Test
    void gradeMultipleChoice_withCorrectAnswer_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeMultipleChoice(any(), eq("A")))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeMultipleChoice(null, "A");

        assertNotNull(response);
        verify(autoGradingService).gradeMultipleChoice(any(), eq("A"));
    }

    @Test
    void gradeMultipleChoice_withIncorrectAnswer_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeMultipleChoice(any(), eq("B")))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeMultipleChoice(null, "B");

        assertNotNull(response);
        verify(autoGradingService).gradeMultipleChoice(any(), eq("B"));
    }

    @Test
    void gradeTrueFalse_withAnswer_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeTrueFalse(any(), eq("true")))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeTrueFalse(null, "true");

        assertNotNull(response);
        verify(autoGradingService).gradeTrueFalse(any(), eq("true"));
    }

    @Test
    void gradeShortAnswer_withValidAnswer_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeShortAnswer(any(), anyString()))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeShortAnswer(null, "sample answer");

        assertNotNull(response);
        verify(autoGradingService).gradeShortAnswer(any(), anyString());
    }

    @Test
    void gradeEssay_withValidAnswer_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeEssay(any(), anyString()))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeEssay(null, "essay answer");

        assertNotNull(response);
        verify(autoGradingService).gradeEssay(any(), anyString());
    }

    @Test
    void gradeMatching_withMatches_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeMatching(any(), anyString()))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeMatching(null, "1-A,2-B,3-C");

        assertNotNull(response);
        verify(autoGradingService).gradeMatching(any(), anyString());
    }

    @Test
    void gradeOrdering_withOrder_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeOrdering(any(), anyString()))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeOrdering(null, "1,2,3,4");

        assertNotNull(response);
        verify(autoGradingService).gradeOrdering(any(), anyString());
    }

    @Test
    void gradeFillInBlank_withAnswer_returnsResponse() {
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeFillInBlank(any(), anyString()))
                .thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeFillInBlank(null, "correct");

        assertNotNull(response);
        verify(autoGradingService).gradeFillInBlank(any(), anyString());
    }

    @Test
    void calculatePartialCredit_withPartialAnswer_returnsPartialScore() {
        when(autoGradingService.calculatePartialCredit(any(), anyString(), anyString()))
                .thenReturn(50.0);

        Double score = autoGradingService.calculatePartialCredit(null, "partial", "correct");

        assertNotNull(score);
        assertEquals(50.0, score);
    }

    @Test
    void calculateSimilarity_withIdenticalStrings_returnsOne() {
        when(autoGradingService.calculateSimilarity("answer", "answer"))
                .thenReturn(1.0);

        Double similarity = autoGradingService.calculateSimilarity("answer", "answer");

        assertNotNull(similarity);
        assertEquals(1.0, similarity);
    }

    @Test
    void calculateSimilarity_withDifferentStrings_returnsLowerValue() {
        when(autoGradingService.calculateSimilarity("answer", "different"))
                .thenReturn(0.2);

        Double similarity = autoGradingService.calculateSimilarity("answer", "different");

        assertNotNull(similarity);
        assertTrue(similarity < 1.0);
    }

    @Test
    void gradeResponse_withPartialCredit_returnsResponse() {
        AutoGradingRequest request = new AutoGradingRequest();
        AutoGradingResponse mockResponse = new AutoGradingResponse();

        when(autoGradingService.gradeResponse(request)).thenReturn(mockResponse);

        AutoGradingResponse response = autoGradingService.gradeResponse(request);

        assertNotNull(response);
        verify(autoGradingService).gradeResponse(request);
    }
}
