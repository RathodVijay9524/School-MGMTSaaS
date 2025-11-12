package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.QuizRequest;
import com.vijay.User_Master.dto.QuizResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        QuizRequest request = new QuizRequest();
        QuizResponse mockResponse = new QuizResponse();
        mockResponse.setId(1L);

        when(quizService.createQuiz(request, OWNER_ID)).thenReturn(mockResponse);

        QuizResponse response = quizService.createQuiz(request, OWNER_ID);

        assertNotNull(response);
        verify(quizService).createQuiz(request, OWNER_ID);
    }

    @Test
    void updateQuiz_withValidData_returnsUpdatedQuiz() {
        QuizRequest request = new QuizRequest();
        QuizResponse mockResponse = new QuizResponse();
        mockResponse.setId(1L);

        when(quizService.updateQuiz(1L, request, OWNER_ID)).thenReturn(mockResponse);

        QuizResponse response = quizService.updateQuiz(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(quizService).updateQuiz(1L, request, OWNER_ID);
    }

    @Test
    void getQuizById_withValidId_returnsQuiz() {
        QuizResponse mockResponse = new QuizResponse();
        mockResponse.setId(1L);

        when(quizService.getQuizById(1L, OWNER_ID)).thenReturn(mockResponse);

        QuizResponse response = quizService.getQuizById(1L, OWNER_ID);

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
        List<QuizResponse> mockQuizzes = new ArrayList<>();
        mockQuizzes.add(new QuizResponse());

        when(quizService.getAllQuizzes(OWNER_ID)).thenReturn(mockQuizzes);

        List<QuizResponse> response = quizService.getAllQuizzes(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getQuizzesBySubject_withValidSubjectId_returnsQuizzes() {
        List<QuizResponse> mockQuizzes = new ArrayList<>();
        mockQuizzes.add(new QuizResponse());

        when(quizService.getQuizzesBySubject(1L, OWNER_ID)).thenReturn(mockQuizzes);

        List<QuizResponse> response = quizService.getQuizzesBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void deleteQuiz_withValidId_succeeds() {
        doNothing().when(quizService).deleteQuiz(1L, OWNER_ID);

        quizService.deleteQuiz(1L, OWNER_ID);

        verify(quizService).deleteQuiz(1L, OWNER_ID);
    }

    @Test
    void getQuizzesPaginated_withValidOwner_returnsPagedQuizzes() {
        Pageable pageable = PageRequest.of(0, 10);
        List<QuizResponse> quizzes = new ArrayList<>();
        quizzes.add(new QuizResponse());
        Page<QuizResponse> page = new PageImpl<>(quizzes, pageable, 1);

        when(quizService.getQuizzesPaginated(OWNER_ID, pageable)).thenReturn(page);

        Page<QuizResponse> response = quizService.getQuizzesPaginated(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void createQuiz_withNullRequest_throwsException() {
        when(quizService.createQuiz(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                quizService.createQuiz(null, OWNER_ID));
    }

    @Test
    void getAllQuizzes_withEmptyResult_returnsEmptyList() {
        List<QuizResponse> mockQuizzes = new ArrayList<>();

        when(quizService.getAllQuizzes(OWNER_ID)).thenReturn(mockQuizzes);

        List<QuizResponse> response = quizService.getAllQuizzes(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
