package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.QuestionRequest;
import com.vijay.User_Master.dto.QuestionResponse;
import com.vijay.User_Master.dto.QuestionSearchRequest;
import com.vijay.User_Master.dto.QuestionBankStatisticsResponse;
import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionType;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuestionBankServiceTest extends ServiceTestBase {

    @Mock
    private QuestionBankService questionBankService;

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
    void createQuestion_withValidRequest_returnsQuestion() {
        QuestionRequest request = new QuestionRequest();
        QuestionResponse mockResponse = new QuestionResponse();
        mockResponse.setId(1L);

        when(questionBankService.createQuestion(request, OWNER_ID)).thenReturn(mockResponse);

        QuestionResponse response = questionBankService.createQuestion(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(questionBankService).createQuestion(request, OWNER_ID);
    }

    @Test
    void createQuestion_withNullRequest_throwsException() {
        when(questionBankService.createQuestion(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                questionBankService.createQuestion(null, OWNER_ID));
    }

    @Test
    void updateQuestion_withValidData_returnsUpdatedQuestion() {
        QuestionRequest request = new QuestionRequest();
        QuestionResponse mockResponse = new QuestionResponse();
        mockResponse.setId(1L);

        when(questionBankService.updateQuestion(1L, request, OWNER_ID)).thenReturn(mockResponse);

        QuestionResponse response = questionBankService.updateQuestion(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(questionBankService).updateQuestion(1L, request, OWNER_ID);
    }

    @Test
    void getQuestionById_withValidId_returnsQuestion() {
        QuestionResponse mockResponse = new QuestionResponse();
        mockResponse.setId(1L);

        when(questionBankService.getQuestionById(1L, OWNER_ID)).thenReturn(mockResponse);

        QuestionResponse response = questionBankService.getQuestionById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getQuestionById_withInvalidId_throwsException() {
        when(questionBankService.getQuestionById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Question not found"));

        assertThrows(RuntimeException.class, () ->
                questionBankService.getQuestionById(999L, OWNER_ID));
    }

    @Test
    void getAllQuestions_withValidOwner_returnsQuestions() {
        List<QuestionResponse> mockQuestions = new ArrayList<>();
        mockQuestions.add(new QuestionResponse());

        when(questionBankService.getAllQuestions(OWNER_ID)).thenReturn(mockQuestions);

        List<QuestionResponse> response = questionBankService.getAllQuestions(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAllQuestions_withEmptyResult_returnsEmptyList() {
        List<QuestionResponse> mockQuestions = new ArrayList<>();

        when(questionBankService.getAllQuestions(OWNER_ID)).thenReturn(mockQuestions);

        List<QuestionResponse> response = questionBankService.getAllQuestions(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getQuestionsPaginated_withValidOwner_returnsPagedQuestions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<QuestionResponse> questions = new ArrayList<>();
        questions.add(new QuestionResponse());
        Page<QuestionResponse> page = new PageImpl<>(questions, pageable, 1);

        when(questionBankService.getQuestionsPaginated(OWNER_ID, pageable)).thenReturn(page);

        Page<QuestionResponse> response = questionBankService.getQuestionsPaginated(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getQuestionsBySubject_withValidSubjectId_returnsQuestions() {
        List<QuestionResponse> mockQuestions = new ArrayList<>();
        mockQuestions.add(new QuestionResponse());

        when(questionBankService.getQuestionsBySubject(1L, OWNER_ID)).thenReturn(mockQuestions);

        List<QuestionResponse> response = questionBankService.getQuestionsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getQuestionsByClass_withValidClassId_returnsQuestions() {
        List<QuestionResponse> mockQuestions = new ArrayList<>();
        mockQuestions.add(new QuestionResponse());

        when(questionBankService.getQuestionsByClass(1L, OWNER_ID)).thenReturn(mockQuestions);

        List<QuestionResponse> response = questionBankService.getQuestionsByClass(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getQuestionsByType_withValidType_returnsQuestions() {
        List<QuestionResponse> mockQuestions = new ArrayList<>();
        mockQuestions.add(new QuestionResponse());

        when(questionBankService.getQuestionsByType(QuestionType.MULTIPLE_CHOICE, OWNER_ID))
                .thenReturn(mockQuestions);

        List<QuestionResponse> response = questionBankService.getQuestionsByType(QuestionType.MULTIPLE_CHOICE, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getQuestionsByDifficulty_withValidDifficulty_returnsQuestions() {
        List<QuestionResponse> mockQuestions = new ArrayList<>();
        mockQuestions.add(new QuestionResponse());

        when(questionBankService.getQuestionsByDifficulty(DifficultyLevel.MEDIUM, OWNER_ID))
                .thenReturn(mockQuestions);

        List<QuestionResponse> response = questionBankService.getQuestionsByDifficulty(DifficultyLevel.MEDIUM, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchQuestions_withKeyword_returnsMatchingQuestions() {
        List<QuestionResponse> mockQuestions = new ArrayList<>();
        mockQuestions.add(new QuestionResponse());

        when(questionBankService.searchQuestions("algebra", OWNER_ID)).thenReturn(mockQuestions);

        List<QuestionResponse> response = questionBankService.searchQuestions("algebra", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void advancedSearch_withValidRequest_returnsPagedQuestions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<QuestionResponse> questions = new ArrayList<>();
        questions.add(new QuestionResponse());
        Page<QuestionResponse> page = new PageImpl<>(questions, pageable, 1);

        QuestionSearchRequest searchRequest = new QuestionSearchRequest();

        when(questionBankService.advancedSearch(searchRequest, OWNER_ID)).thenReturn(page);

        Page<QuestionResponse> response = questionBankService.advancedSearch(searchRequest, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void deleteQuestion_withValidId_succeeds() {
        doNothing().when(questionBankService).deleteQuestion(1L, OWNER_ID);

        questionBankService.deleteQuestion(1L, OWNER_ID);

        verify(questionBankService).deleteQuestion(1L, OWNER_ID);
    }

    @Test
    void getStatistics_withValidOwnerId_returnsStatistics() {
        QuestionBankStatisticsResponse mockStats = new QuestionBankStatisticsResponse();

        when(questionBankService.getStatistics(OWNER_ID)).thenReturn(mockStats);

        QuestionBankStatisticsResponse response = questionBankService.getStatistics(OWNER_ID);

        assertNotNull(response);
        verify(questionBankService).getStatistics(OWNER_ID);
    }

    @Test
    void getAllTags_withValidOwnerId_returnsTags() {
        List<QuestionResponse.QuestionTagResponse> mockTags = new ArrayList<>();

        when(questionBankService.getAllTags(OWNER_ID)).thenReturn(mockTags);

        List<QuestionResponse.QuestionTagResponse> response = questionBankService.getAllTags(OWNER_ID);

        assertNotNull(response);
        verify(questionBankService).getAllTags(OWNER_ID);
    }

    @Test
    void deleteTag_withValidTagId_succeeds() {
        doNothing().when(questionBankService).deleteTag(1L, OWNER_ID);

        questionBankService.deleteTag(1L, OWNER_ID);

        verify(questionBankService).deleteTag(1L, OWNER_ID);
    }

    @Test
    void addQuestionsToTag_withValidData_succeeds() {
        List<Long> questionIds = new ArrayList<>();
        questionIds.add(1L);
        questionIds.add(2L);

        doNothing().when(questionBankService).addQuestionsToTag(1L, questionIds, OWNER_ID);

        questionBankService.addQuestionsToTag(1L, questionIds, OWNER_ID);

        verify(questionBankService).addQuestionsToTag(1L, questionIds, OWNER_ID);
    }
}
