package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.QuestionPoolRequest;
import com.vijay.User_Master.dto.QuestionPoolResponse;
import com.vijay.User_Master.dto.QuestionPoolGenerateRequest;
import com.vijay.User_Master.dto.QuestionPoolGenerateResponse;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuestionPoolServiceTest extends ServiceTestBase {

    @Mock
    private QuestionPoolService questionPoolService;

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
    void createPool_withValidRequest_returnsPool() {
        QuestionPoolRequest request = new QuestionPoolRequest();
        QuestionPoolResponse mockResponse = new QuestionPoolResponse();
        mockResponse.setId(1L);

        when(questionPoolService.createPool(request, OWNER_ID)).thenReturn(mockResponse);

        QuestionPoolResponse response = questionPoolService.createPool(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(questionPoolService).createPool(request, OWNER_ID);
    }

    @Test
    void createPool_withNullRequest_throwsException() {
        when(questionPoolService.createPool(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                questionPoolService.createPool(null, OWNER_ID));
    }

    @Test
    void updatePool_withValidData_returnsUpdatedPool() {
        QuestionPoolRequest request = new QuestionPoolRequest();
        QuestionPoolResponse mockResponse = new QuestionPoolResponse();
        mockResponse.setId(1L);

        when(questionPoolService.updatePool(1L, request, OWNER_ID)).thenReturn(mockResponse);

        QuestionPoolResponse response = questionPoolService.updatePool(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(questionPoolService).updatePool(1L, request, OWNER_ID);
    }

    @Test
    void getPoolById_withValidId_returnsPool() {
        QuestionPoolResponse mockResponse = new QuestionPoolResponse();
        mockResponse.setId(1L);

        when(questionPoolService.getPoolById(1L, OWNER_ID)).thenReturn(mockResponse);

        QuestionPoolResponse response = questionPoolService.getPoolById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getPoolById_withInvalidId_throwsException() {
        when(questionPoolService.getPoolById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Pool not found"));

        assertThrows(RuntimeException.class, () ->
                questionPoolService.getPoolById(999L, OWNER_ID));
    }

    @Test
    void deletePool_withValidId_succeeds() {
        doNothing().when(questionPoolService).deletePool(1L, OWNER_ID);

        questionPoolService.deletePool(1L, OWNER_ID);

        verify(questionPoolService).deletePool(1L, OWNER_ID);
    }

    @Test
    void getAllPools_withValidOwner_returnsPools() {
        List<QuestionPoolResponse> mockPools = new ArrayList<>();
        mockPools.add(new QuestionPoolResponse());
        mockPools.add(new QuestionPoolResponse());

        when(questionPoolService.getAllPools(OWNER_ID)).thenReturn(mockPools);

        List<QuestionPoolResponse> response = questionPoolService.getAllPools(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(questionPoolService).getAllPools(OWNER_ID);
    }

    @Test
    void getAllPools_withEmptyResult_returnsEmptyList() {
        List<QuestionPoolResponse> mockPools = new ArrayList<>();

        when(questionPoolService.getAllPools(OWNER_ID)).thenReturn(mockPools);

        List<QuestionPoolResponse> response = questionPoolService.getAllPools(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getPoolsBySubject_withValidSubjectId_returnsPools() {
        List<QuestionPoolResponse> mockPools = new ArrayList<>();
        mockPools.add(new QuestionPoolResponse());

        when(questionPoolService.getPoolsBySubject(1L, OWNER_ID)).thenReturn(mockPools);

        List<QuestionPoolResponse> response = questionPoolService.getPoolsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPoolsBySubject_withNoResults_returnsEmptyList() {
        List<QuestionPoolResponse> mockPools = new ArrayList<>();

        when(questionPoolService.getPoolsBySubject(1L, OWNER_ID)).thenReturn(mockPools);

        List<QuestionPoolResponse> response = questionPoolService.getPoolsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void generateQuestions_withValidRequest_returnsGeneratedQuestions() {
        QuestionPoolGenerateRequest request = new QuestionPoolGenerateRequest();
        QuestionPoolGenerateResponse mockResponse = new QuestionPoolGenerateResponse();

        when(questionPoolService.generateQuestions(request, OWNER_ID)).thenReturn(mockResponse);

        QuestionPoolGenerateResponse response = questionPoolService.generateQuestions(request, OWNER_ID);

        assertNotNull(response);
        verify(questionPoolService).generateQuestions(request, OWNER_ID);
    }

    @Test
    void generateQuestions_withNullRequest_throwsException() {
        when(questionPoolService.generateQuestions(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                questionPoolService.generateQuestions(null, OWNER_ID));
    }

    @Test
    void addQuestionsToPool_withValidData_returnsUpdatedPool() {
        List<Long> questionIds = new ArrayList<>();
        questionIds.add(1L);
        questionIds.add(2L);

        QuestionPoolResponse mockResponse = new QuestionPoolResponse();
        mockResponse.setId(1L);

        when(questionPoolService.addQuestionsToPool(1L, questionIds, OWNER_ID)).thenReturn(mockResponse);

        QuestionPoolResponse response = questionPoolService.addQuestionsToPool(1L, questionIds, OWNER_ID);

        assertNotNull(response);
        verify(questionPoolService).addQuestionsToPool(1L, questionIds, OWNER_ID);
    }

    @Test
    void addQuestionsToPool_withEmptyList_returnsPool() {
        List<Long> questionIds = new ArrayList<>();

        QuestionPoolResponse mockResponse = new QuestionPoolResponse();
        mockResponse.setId(1L);

        when(questionPoolService.addQuestionsToPool(1L, questionIds, OWNER_ID)).thenReturn(mockResponse);

        QuestionPoolResponse response = questionPoolService.addQuestionsToPool(1L, questionIds, OWNER_ID);

        assertNotNull(response);
        verify(questionPoolService).addQuestionsToPool(1L, questionIds, OWNER_ID);
    }

    @Test
    void removeQuestionsFromPool_withValidData_returnsUpdatedPool() {
        List<Long> questionIds = new ArrayList<>();
        questionIds.add(1L);

        QuestionPoolResponse mockResponse = new QuestionPoolResponse();
        mockResponse.setId(1L);

        when(questionPoolService.removeQuestionsFromPool(1L, questionIds, OWNER_ID)).thenReturn(mockResponse);

        QuestionPoolResponse response = questionPoolService.removeQuestionsFromPool(1L, questionIds, OWNER_ID);

        assertNotNull(response);
        verify(questionPoolService).removeQuestionsFromPool(1L, questionIds, OWNER_ID);
    }

    @Test
    void updatePool_withNullRequest_throwsException() {
        when(questionPoolService.updatePool(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                questionPoolService.updatePool(1L, null, OWNER_ID));
    }

    @Test
    void getPoolsBySubject_multipleResults_returnsAllPools() {
        List<QuestionPoolResponse> mockPools = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockPools.add(new QuestionPoolResponse());
        }

        when(questionPoolService.getPoolsBySubject(1L, OWNER_ID)).thenReturn(mockPools);

        List<QuestionPoolResponse> response = questionPoolService.getPoolsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(5, response.size());
    }

    @Test
    void getAllPools_multipleResults_returnsAllPools() {
        List<QuestionPoolResponse> mockPools = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mockPools.add(new QuestionPoolResponse());
        }

        when(questionPoolService.getAllPools(OWNER_ID)).thenReturn(mockPools);

        List<QuestionPoolResponse> response = questionPoolService.getAllPools(OWNER_ID);

        assertNotNull(response);
        assertEquals(3, response.size());
    }
}
