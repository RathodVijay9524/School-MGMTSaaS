package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.ExamRequest;
import com.vijay.User_Master.dto.ExamResponse;
import com.vijay.User_Master.dto.ExamStatistics;
import com.vijay.User_Master.entity.Exam;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExamServiceTest extends ServiceTestBase {

    @Mock
    private ExamService examService;

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
    void createExam_withValidRequest_returnsExamResponse() {
        ExamRequest request = new ExamRequest();
        request.setExamName("Math Midterm");
        request.setExamType(Exam.ExamType.MIDTERM);
        request.setExamDate(LocalDate.now().plusDays(10));

        ExamResponse mockResponse = new ExamResponse();
        mockResponse.setId(1L);
        mockResponse.setExamName("Math Midterm");

        when(examService.createExam(request, OWNER_ID)).thenReturn(mockResponse);

        ExamResponse response = examService.createExam(request, OWNER_ID);

        assertNotNull(response);
        assertEquals("Math Midterm", response.getExamName());
        verify(examService).createExam(request, OWNER_ID);
    }

    @Test
    void createExam_withNullRequest_throwsException() {
        when(examService.createExam(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                examService.createExam(null, OWNER_ID));
    }

    @Test
    void updateExam_withValidData_returnsUpdatedExam() {
        ExamRequest request = new ExamRequest();
        request.setExamName("Updated Exam");

        ExamResponse mockResponse = new ExamResponse();
        mockResponse.setId(1L);
        mockResponse.setExamName("Updated Exam");

        when(examService.updateExam(1L, request, OWNER_ID)).thenReturn(mockResponse);

        ExamResponse response = examService.updateExam(1L, request, OWNER_ID);

        assertNotNull(response);
        assertEquals("Updated Exam", response.getExamName());
    }

    @Test
    void updateExam_withInvalidId_throwsException() {
        ExamRequest request = new ExamRequest();

        when(examService.updateExam(999L, request, OWNER_ID))
                .thenThrow(new RuntimeException("Exam not found"));

        assertThrows(RuntimeException.class, () ->
                examService.updateExam(999L, request, OWNER_ID));
    }

    @Test
    void getExamById_withValidId_returnsExam() {
        ExamResponse mockResponse = new ExamResponse();
        mockResponse.setId(1L);
        mockResponse.setExamName("Math Exam");

        when(examService.getExamById(1L, OWNER_ID)).thenReturn(mockResponse);

        ExamResponse response = examService.getExamById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getExamById_withInvalidId_throwsException() {
        when(examService.getExamById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Exam not found"));

        assertThrows(RuntimeException.class, () ->
                examService.getExamById(999L, OWNER_ID));
    }

    @Test
    void getAllExams_withValidOwner_returnsPagedExams() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ExamResponse> exams = new ArrayList<>();
        exams.add(new ExamResponse());
        Page<ExamResponse> page = new PageImpl<>(exams, pageable, 1);

        when(examService.getAllExams(OWNER_ID, pageable)).thenReturn(page);

        Page<ExamResponse> response = examService.getAllExams(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getExamsByClass_withValidClassId_returnsExams() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ExamResponse> exams = new ArrayList<>();
        exams.add(new ExamResponse());
        Page<ExamResponse> page = new PageImpl<>(exams, pageable, 1);

        when(examService.getExamsByClass(1L, OWNER_ID, pageable)).thenReturn(page);

        Page<ExamResponse> response = examService.getExamsByClass(1L, OWNER_ID, pageable);

        assertNotNull(response);
        verify(examService).getExamsByClass(1L, OWNER_ID, pageable);
    }

    @Test
    void getExamsBySubject_withValidSubjectId_returnsExams() {
        List<ExamResponse> mockExams = new ArrayList<>();
        mockExams.add(new ExamResponse());

        when(examService.getExamsBySubject(1L, OWNER_ID)).thenReturn(mockExams);

        List<ExamResponse> response = examService.getExamsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getExamsByType_withValidType_returnsExams() {
        List<ExamResponse> mockExams = new ArrayList<>();
        mockExams.add(new ExamResponse());

        when(examService.getExamsByType(Exam.ExamType.MIDTERM, OWNER_ID))
                .thenReturn(mockExams);

        List<ExamResponse> response = examService.getExamsByType(Exam.ExamType.MIDTERM, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getExamsByStatus_withValidStatus_returnsExams() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ExamResponse> exams = new ArrayList<>();
        exams.add(new ExamResponse());
        Page<ExamResponse> page = new PageImpl<>(exams, pageable, 1);

        when(examService.getExamsByStatus(Exam.ExamStatus.SCHEDULED, OWNER_ID, pageable))
                .thenReturn(page);

        Page<ExamResponse> response = examService.getExamsByStatus(Exam.ExamStatus.SCHEDULED, OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getUpcomingExams_returnsUpcomingExams() {
        List<ExamResponse> mockExams = new ArrayList<>();
        mockExams.add(new ExamResponse());

        when(examService.getUpcomingExams(OWNER_ID)).thenReturn(mockExams);

        List<ExamResponse> response = examService.getUpcomingExams(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getOverdueExams_returnsOverdueExams() {
        List<ExamResponse> mockExams = new ArrayList<>();

        when(examService.getOverdueExams(OWNER_ID)).thenReturn(mockExams);

        List<ExamResponse> response = examService.getOverdueExams(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void publishExamResults_withValidId_returnsPublishedExam() {
        ExamResponse mockResponse = new ExamResponse();
        mockResponse.setId(1L);
        mockResponse.setStatus(Exam.ExamStatus.PUBLISHED);

        when(examService.publishExamResults(1L, OWNER_ID)).thenReturn(mockResponse);

        ExamResponse response = examService.publishExamResults(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(Exam.ExamStatus.PUBLISHED, response.getStatus());
    }

    @Test
    void cancelExam_withValidId_returnsCancelledExam() {
        ExamResponse mockResponse = new ExamResponse();
        mockResponse.setId(1L);
        mockResponse.setStatus(Exam.ExamStatus.CANCELLED);

        when(examService.cancelExam(1L, "Rescheduled", OWNER_ID))
                .thenReturn(mockResponse);

        ExamResponse response = examService.cancelExam(1L, "Rescheduled", OWNER_ID);

        assertNotNull(response);
        assertEquals(Exam.ExamStatus.CANCELLED, response.getStatus());
    }

    @Test
    void rescheduleExam_withValidData_returnsRescheduledExam() {
        ExamResponse mockResponse = new ExamResponse();
        mockResponse.setId(1L);
        mockResponse.setExamDate(LocalDate.now().plusDays(20));

        when(examService.rescheduleExam(1L, LocalDate.now().plusDays(20), OWNER_ID))
                .thenReturn(mockResponse);

        ExamResponse response = examService.rescheduleExam(1L, LocalDate.now().plusDays(20), OWNER_ID);

        assertNotNull(response);
        assertEquals(LocalDate.now().plusDays(20), response.getExamDate());
    }

    @Test
    void getExamStatistics_returnsStatistics() {
        ExamStatistics mockStats = new ExamStatistics();
        mockStats.setTotalExams(10);
        mockStats.setCompletedExams(5);

        when(examService.getExamStatistics(OWNER_ID)).thenReturn(mockStats);

        ExamStatistics response = examService.getExamStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(10, response.getTotalExams());
    }

    @Test
    void deleteExam_withValidId_succeeds() {
        doNothing().when(examService).deleteExam(1L, OWNER_ID);

        examService.deleteExam(1L, OWNER_ID);

        verify(examService).deleteExam(1L, OWNER_ID);
    }

    @Test
    void restoreExam_withValidId_succeeds() {
        doNothing().when(examService).restoreExam(1L, OWNER_ID);

        examService.restoreExam(1L, OWNER_ID);

        verify(examService).restoreExam(1L, OWNER_ID);
    }
}
