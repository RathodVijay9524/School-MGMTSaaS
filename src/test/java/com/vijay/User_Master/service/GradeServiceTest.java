package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.GradeRequest;
import com.vijay.User_Master.dto.GradeResponse;
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
class GradeServiceTest extends ServiceTestBase {

    @Mock
    private GradeService gradeService;

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
    void getAllGrades_withValidPageable_returnsPagedGrades() {
        Pageable pageable = PageRequest.of(0, 10);
        List<GradeResponse> grades = new ArrayList<>();
        grades.add(new GradeResponse());
        Page<GradeResponse> page = new PageImpl<>(grades, pageable, 1);

        when(gradeService.getAllGrades(pageable)).thenReturn(page);

        Page<GradeResponse> response = gradeService.getAllGrades(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(gradeService).getAllGrades(pageable);
    }

    @Test
    void createGrade_withValidRequest_returnsGrade() {
        GradeRequest request = new GradeRequest();
        GradeResponse mockResponse = new GradeResponse();
        mockResponse.setId(1L);

        when(gradeService.createGrade(request)).thenReturn(mockResponse);

        GradeResponse response = gradeService.createGrade(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void createGrade_withNullRequest_throwsException() {
        when(gradeService.createGrade(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                gradeService.createGrade(null));
    }

    @Test
    void updateGrade_withValidData_returnsUpdatedGrade() {
        GradeRequest request = new GradeRequest();
        GradeResponse mockResponse = new GradeResponse();
        mockResponse.setId(1L);

        when(gradeService.updateGrade(1L, request)).thenReturn(mockResponse);

        GradeResponse response = gradeService.updateGrade(1L, request);

        assertNotNull(response);
        verify(gradeService).updateGrade(1L, request);
    }

    @Test
    void getGradeById_withValidId_returnsGrade() {
        GradeResponse mockResponse = new GradeResponse();
        mockResponse.setId(1L);

        when(gradeService.getGradeById(1L)).thenReturn(mockResponse);

        GradeResponse response = gradeService.getGradeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getGradeById_withInvalidId_throwsException() {
        when(gradeService.getGradeById(999L))
                .thenThrow(new RuntimeException("Grade not found"));

        assertThrows(RuntimeException.class, () ->
                gradeService.getGradeById(999L));
    }

    @Test
    void getGradesByStudent_withValidStudentId_returnsPagedGrades() {
        Pageable pageable = PageRequest.of(0, 10);
        List<GradeResponse> grades = new ArrayList<>();
        grades.add(new GradeResponse());
        Page<GradeResponse> page = new PageImpl<>(grades, pageable, 1);

        when(gradeService.getGradesByStudent(100L, pageable)).thenReturn(page);

        Page<GradeResponse> response = gradeService.getGradesByStudent(100L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getGradesByStudentAndSubject_withValidIds_returnsGrades() {
        List<GradeResponse> mockGrades = new ArrayList<>();
        mockGrades.add(new GradeResponse());

        when(gradeService.getGradesByStudentAndSubject(100L, 1L)).thenReturn(mockGrades);

        List<GradeResponse> response = gradeService.getGradesByStudentAndSubject(100L, 1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getGradesByStudentAndSemester_withValidData_returnsGrades() {
        List<GradeResponse> mockGrades = new ArrayList<>();
        mockGrades.add(new GradeResponse());

        when(gradeService.getGradesByStudentAndSemester(100L, "SPRING-2024")).thenReturn(mockGrades);

        List<GradeResponse> response = gradeService.getGradesByStudentAndSemester(100L, "SPRING-2024");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPublishedGrades_withValidStudentId_returnsPublishedGrades() {
        List<GradeResponse> mockGrades = new ArrayList<>();
        mockGrades.add(new GradeResponse());

        when(gradeService.getPublishedGrades(100L)).thenReturn(mockGrades);

        List<GradeResponse> response = gradeService.getPublishedGrades(100L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPublishedGrades_withNoPublishedGrades_returnsEmptyList() {
        List<GradeResponse> mockGrades = new ArrayList<>();

        when(gradeService.getPublishedGrades(100L)).thenReturn(mockGrades);

        List<GradeResponse> response = gradeService.getPublishedGrades(100L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void calculateStudentGPA_withValidStudentId_returnsGPA() {
        when(gradeService.calculateStudentGPA(100L)).thenReturn(3.8);

        Double gpa = gradeService.calculateStudentGPA(100L);

        assertNotNull(gpa);
        assertEquals(3.8, gpa);
        assertTrue(gpa >= 0.0 && gpa <= 4.0);
    }

    @Test
    void calculateSubjectAverage_withValidIds_returnsAverage() {
        when(gradeService.calculateSubjectAverage(100L, 1L)).thenReturn(85.5);

        Double average = gradeService.calculateSubjectAverage(100L, 1L);

        assertNotNull(average);
        assertEquals(85.5, average);
    }

    @Test
    void getFailingGrades_withValidStudentId_returnsFailingGrades() {
        List<GradeResponse> mockGrades = new ArrayList<>();
        mockGrades.add(new GradeResponse());

        when(gradeService.getFailingGrades(100L)).thenReturn(mockGrades);

        List<GradeResponse> response = gradeService.getFailingGrades(100L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getFailingGrades_withNoFailingGrades_returnsEmptyList() {
        List<GradeResponse> mockGrades = new ArrayList<>();

        when(gradeService.getFailingGrades(100L)).thenReturn(mockGrades);

        List<GradeResponse> response = gradeService.getFailingGrades(100L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void publishGrade_withValidId_succeeds() {
        doNothing().when(gradeService).publishGrade(1L);

        gradeService.publishGrade(1L);

        verify(gradeService).publishGrade(1L);
    }

    @Test
    void deleteGrade_withValidId_succeeds() {
        doNothing().when(gradeService).deleteGrade(1L);

        gradeService.deleteGrade(1L);

        verify(gradeService).deleteGrade(1L);
    }

    @Test
    void updateGrade_withNullRequest_throwsException() {
        when(gradeService.updateGrade(1L, null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                gradeService.updateGrade(1L, null));
    }
}
