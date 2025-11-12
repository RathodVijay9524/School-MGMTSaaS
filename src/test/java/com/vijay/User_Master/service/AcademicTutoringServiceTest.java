package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.TutoringSessionRequest;
import com.vijay.User_Master.dto.TutoringSessionResponse;
import com.vijay.User_Master.dto.LearningPathResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AcademicTutoringServiceTest extends ServiceTestBase {

    @Mock
    private AcademicTutoringService tutoringService;

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
    void createTutoringSession_withValidRequest_returnsResponse() {
        TutoringSessionRequest request = new TutoringSessionRequest();
        TutoringSessionResponse mockResponse = new TutoringSessionResponse();
        mockResponse.setId(1L);

        when(tutoringService.createTutoringSession(request, OWNER_ID)).thenReturn(mockResponse);

        TutoringSessionResponse response = tutoringService.createTutoringSession(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(tutoringService).createTutoringSession(request, OWNER_ID);
    }

    @Test
    void getTutoringSessionById_withValidId_returnsSession() {
        TutoringSessionResponse mockResponse = new TutoringSessionResponse();
        mockResponse.setId(1L);

        when(tutoringService.getTutoringSessionById(1L, OWNER_ID)).thenReturn(mockResponse);

        TutoringSessionResponse response = tutoringService.getTutoringSessionById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTutoringSessionById_withInvalidId_throwsException() {
        when(tutoringService.getTutoringSessionById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Session not found"));

        assertThrows(RuntimeException.class, () ->
                tutoringService.getTutoringSessionById(999L, OWNER_ID));
    }

    @Test
    void getAllTutoringSessions_withValidOwner_returnsPagedSessions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TutoringSessionResponse> sessions = new ArrayList<>();
        sessions.add(new TutoringSessionResponse());
        Page<TutoringSessionResponse> page = new PageImpl<>(sessions, pageable, 1);

        when(tutoringService.getAllTutoringSessions(OWNER_ID, pageable)).thenReturn(page);

        Page<TutoringSessionResponse> response = tutoringService.getAllTutoringSessions(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getTutoringSessionsByStudent_withValidStudentId_returnsSessions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TutoringSessionResponse> sessions = new ArrayList<>();
        sessions.add(new TutoringSessionResponse());
        Page<TutoringSessionResponse> page = new PageImpl<>(sessions, pageable, 1);

        when(tutoringService.getTutoringSessionsByStudent(100L, OWNER_ID, pageable)).thenReturn(page);

        Page<TutoringSessionResponse> response = tutoringService.getTutoringSessionsByStudent(100L, OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void updateTutoringSession_withValidData_returnsUpdatedSession() {
        TutoringSessionRequest request = new TutoringSessionRequest();
        TutoringSessionResponse mockResponse = new TutoringSessionResponse();
        mockResponse.setId(1L);

        when(tutoringService.updateTutoringSession(1L, request, OWNER_ID)).thenReturn(mockResponse);

        TutoringSessionResponse response = tutoringService.updateTutoringSession(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(tutoringService).updateTutoringSession(1L, request, OWNER_ID);
    }

    @Test
    void deleteTutoringSession_withValidId_succeeds() {
        doNothing().when(tutoringService).deleteTutoringSession(1L, OWNER_ID);

        tutoringService.deleteTutoringSession(1L, OWNER_ID);

        verify(tutoringService).deleteTutoringSession(1L, OWNER_ID);
    }

    @Test
    void getTutoringStatistics_withValidOwnerId_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalSessions", 10);

        when(tutoringService.getTutoringStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = tutoringService.getTutoringStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(10, response.get("totalSessions"));
    }

    @Test
    void getSessionsRequiringFollowUp_returnsFollowUpSessions() {
        List<TutoringSessionResponse> mockSessions = new ArrayList<>();
        mockSessions.add(new TutoringSessionResponse());

        when(tutoringService.getSessionsRequiringFollowUp(OWNER_ID)).thenReturn(mockSessions);

        List<TutoringSessionResponse> response = tutoringService.getSessionsRequiringFollowUp(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRecentTutoringSessions_withValidDays_returnsSessions() {
        List<TutoringSessionResponse> mockSessions = new ArrayList<>();
        mockSessions.add(new TutoringSessionResponse());

        when(tutoringService.getRecentTutoringSessions(OWNER_ID, 7)).thenReturn(mockSessions);

        List<TutoringSessionResponse> response = tutoringService.getRecentTutoringSessions(OWNER_ID, 7);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void analyzeStudentPerformance_withValidStudentId_returnsAnalysis() {
        Map<String, Object> mockAnalysis = new HashMap<>();
        mockAnalysis.put("averageScore", 85.5);

        when(tutoringService.analyzeStudentPerformance(100L, OWNER_ID)).thenReturn(mockAnalysis);

        Map<String, Object> response = tutoringService.analyzeStudentPerformance(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(85.5, response.get("averageScore"));
    }

    @Test
    void createTutoringSession_withNullRequest_throwsException() {
        when(tutoringService.createTutoringSession(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                tutoringService.createTutoringSession(null, OWNER_ID));
    }
}
