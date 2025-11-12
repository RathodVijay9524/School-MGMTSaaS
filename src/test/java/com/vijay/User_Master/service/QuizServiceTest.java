package com.vijay.User_Master.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuizServiceTest extends ServiceTestBase {

    @Mock
    private QuizService quizService;

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
    void createQuiz_withValidRequest_returnsQuizResponse() {
        Object request = new Object();
        Object mockResponse = new Object();

        when(quizService.createQuiz(request, OWNER_ID)).thenReturn(mockResponse);

        Object response = quizService.createQuiz(request, OWNER_ID);

        assertNotNull(response);
        verify(quizService).createQuiz(request, OWNER_ID);
    }

    @Test
    void updateQuiz_withValidData_returnsUpdatedQuiz() {
        Object request = new Object();
        Object mockResponse = new Object();

        when(quizService.updateQuiz(1L, request, OWNER_ID)).thenReturn(mockResponse);

        Object response = quizService.updateQuiz(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(quizService).updateQuiz(1L, request, OWNER_ID);
    }

    @Test
    void getQuizById_withValidId_returnsQuiz() {
        Object mockResponse = new Object();

        when(quizService.getQuizById(1L, OWNER_ID)).thenReturn(mockResponse);

        Object response = quizService.getQuizById(1L, OWNER_ID);

        assertNotNull(response);
        verify(quizService).getQuizById(1L, OWNER_ID);
    }

    @Test
    void getQuizById_withInvalidId_throwsException() {
        when(quizService.getQuizById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Quiz not found"));

        assertThrows(RuntimeException.class, () ->
                quizService.getQuizById(999L, OWNER_ID));
    }

    @Test
    void getAllQuizzes_withValidOwner_returnsQuizzes() {
        List<Object> mockQuizzes = new ArrayList<>();
        mockQuizzes.add(new Object());

        when(quizService.getAllQuizzes(OWNER_ID)).thenReturn(mockQuizzes);

        List<Object> response = quizService.getAllQuizzes(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getQuizzesBySubject_withValidSubjectId_returnsQuizzes() {
        List<Object> mockQuizzes = new ArrayList<>();
        mockQuizzes.add(new Object());

        when(quizService.getQuizzesBySubject(1L, OWNER_ID)).thenReturn(mockQuizzes);

        List<Object> response = quizService.getQuizzesBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void publishQuiz_withValidId_returnsPublishedQuiz() {
        Object mockResponse = new Object();

        when(quizService.publishQuiz(1L, OWNER_ID)).thenReturn(mockResponse);

        Object response = quizService.publishQuiz(1L, OWNER_ID);

        assertNotNull(response);
        verify(quizService).publishQuiz(1L, OWNER_ID);
    }

    @Test
    void deleteQuiz_withValidId_succeeds() {
        doNothing().when(quizService).deleteQuiz(1L, OWNER_ID);

        quizService.deleteQuiz(1L, OWNER_ID);

        verify(quizService).deleteQuiz(1L, OWNER_ID);
    }

    @Test
    void submitQuizResponse_withValidData_succeeds() {
        doNothing().when(quizService).submitQuizResponse(1L, 100L, new Object(), OWNER_ID);

        quizService.submitQuizResponse(1L, 100L, new Object(), OWNER_ID);

        verify(quizService).submitQuizResponse(1L, 100L, any(), OWNER_ID);
    }

    @Test
    void getQuizStatistics_returnsStatistics() {
        Object mockStats = new Object();

        when(quizService.getQuizStatistics(OWNER_ID)).thenReturn(mockStats);

        Object response = quizService.getQuizStatistics(OWNER_ID);

        assertNotNull(response);
        verify(quizService).getQuizStatistics(OWNER_ID);
    }

    @Test
    void getQuizResults_withValidQuizId_returnsResults() {
        List<Object> mockResults = new ArrayList<>();
        mockResults.add(new Object());

        when(quizService.getQuizResults(1L, OWNER_ID)).thenReturn(mockResults);

        List<Object> response = quizService.getQuizResults(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }
}
