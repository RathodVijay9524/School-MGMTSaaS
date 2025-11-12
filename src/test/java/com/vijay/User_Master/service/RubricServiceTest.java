package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RubricRequest;
import com.vijay.User_Master.dto.RubricResponse;
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
class RubricServiceTest extends ServiceTestBase {

    @Mock
    private RubricService rubricService;

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
    void createRubric_withValidRequest_returnsRubric() {
        RubricRequest request = new RubricRequest();
        RubricResponse mockResponse = new RubricResponse();
        mockResponse.setId(1L);

        when(rubricService.createRubric(request, OWNER_ID)).thenReturn(mockResponse);

        RubricResponse response = rubricService.createRubric(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(rubricService).createRubric(request, OWNER_ID);
    }

    @Test
    void createRubric_withNullRequest_throwsException() {
        when(rubricService.createRubric(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                rubricService.createRubric(null, OWNER_ID));
    }

    @Test
    void updateRubric_withValidData_returnsUpdatedRubric() {
        RubricRequest request = new RubricRequest();
        RubricResponse mockResponse = new RubricResponse();
        mockResponse.setId(1L);

        when(rubricService.updateRubric(1L, request, OWNER_ID)).thenReturn(mockResponse);

        RubricResponse response = rubricService.updateRubric(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(rubricService).updateRubric(1L, request, OWNER_ID);
    }

    @Test
    void getRubricById_withValidId_returnsRubric() {
        RubricResponse mockResponse = new RubricResponse();
        mockResponse.setId(1L);

        when(rubricService.getRubricById(1L, OWNER_ID)).thenReturn(mockResponse);

        RubricResponse response = rubricService.getRubricById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getRubricById_withInvalidId_throwsException() {
        when(rubricService.getRubricById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Rubric not found"));

        assertThrows(RuntimeException.class, () ->
                rubricService.getRubricById(999L, OWNER_ID));
    }

    @Test
    void getAllRubrics_withValidOwner_returnsRubrics() {
        List<RubricResponse> mockRubrics = new ArrayList<>();
        mockRubrics.add(new RubricResponse());
        mockRubrics.add(new RubricResponse());

        when(rubricService.getAllRubrics(OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getAllRubrics(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(rubricService).getAllRubrics(OWNER_ID);
    }

    @Test
    void getAllRubrics_withEmptyResult_returnsEmptyList() {
        List<RubricResponse> mockRubrics = new ArrayList<>();

        when(rubricService.getAllRubrics(OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getAllRubrics(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getRubricsBySubject_withValidSubjectId_returnsRubrics() {
        List<RubricResponse> mockRubrics = new ArrayList<>();
        mockRubrics.add(new RubricResponse());

        when(rubricService.getRubricsBySubject(1L, OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getRubricsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRubricsBySubject_withNoRubrics_returnsEmptyList() {
        List<RubricResponse> mockRubrics = new ArrayList<>();

        when(rubricService.getRubricsBySubject(1L, OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getRubricsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getRubricsByType_withValidType_returnsRubrics() {
        List<RubricResponse> mockRubrics = new ArrayList<>();
        mockRubrics.add(new RubricResponse());

        when(rubricService.getRubricsByType("ASSIGNMENT", OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getRubricsByType("ASSIGNMENT", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRubricsByType_withNoRubrics_returnsEmptyList() {
        List<RubricResponse> mockRubrics = new ArrayList<>();

        when(rubricService.getRubricsByType("PROJECT", OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getRubricsByType("PROJECT", OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deleteRubric_withValidId_succeeds() {
        doNothing().when(rubricService).deleteRubric(1L, OWNER_ID);

        rubricService.deleteRubric(1L, OWNER_ID);

        verify(rubricService).deleteRubric(1L, OWNER_ID);
    }

    @Test
    void searchRubrics_withValidKeyword_returnsRubrics() {
        List<RubricResponse> mockRubrics = new ArrayList<>();
        mockRubrics.add(new RubricResponse());

        when(rubricService.searchRubrics("essay", OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.searchRubrics("essay", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(rubricService).searchRubrics("essay", OWNER_ID);
    }

    @Test
    void searchRubrics_withNoMatches_returnsEmptyList() {
        List<RubricResponse> mockRubrics = new ArrayList<>();

        when(rubricService.searchRubrics("nonexistent", OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.searchRubrics("nonexistent", OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void updateRubric_withNullRequest_throwsException() {
        when(rubricService.updateRubric(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                rubricService.updateRubric(1L, null, OWNER_ID));
    }

    @Test
    void getRubricsBySubject_multipleRubrics_returnsAllRubrics() {
        List<RubricResponse> mockRubrics = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockRubrics.add(new RubricResponse());
        }

        when(rubricService.getRubricsBySubject(1L, OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getRubricsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(5, response.size());
    }

    @Test
    void getAllRubrics_multipleRubrics_returnsAllRubrics() {
        List<RubricResponse> mockRubrics = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mockRubrics.add(new RubricResponse());
        }

        when(rubricService.getAllRubrics(OWNER_ID)).thenReturn(mockRubrics);

        List<RubricResponse> response = rubricService.getAllRubrics(OWNER_ID);

        assertNotNull(response);
        assertEquals(3, response.size());
    }
}
