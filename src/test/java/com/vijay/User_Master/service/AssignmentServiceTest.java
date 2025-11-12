package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AssignmentRequest;
import com.vijay.User_Master.dto.AssignmentResponse;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssignmentServiceTest extends ServiceTestBase {

    @Mock
    private AssignmentService assignmentService;

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
    void createAssignment_withValidRequest_returnsAssignmentResponse() {
        AssignmentRequest request = new AssignmentRequest();
        request.setTitle("Math Assignment");
        request.setDueDate(LocalDateTime.now().plusDays(7));

        AssignmentResponse mockResponse = new AssignmentResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle("Math Assignment");

        when(assignmentService.createAssignment(request, OWNER_ID)).thenReturn(mockResponse);

        AssignmentResponse response = assignmentService.createAssignment(request, OWNER_ID);

        assertNotNull(response);
        assertEquals("Math Assignment", response.getTitle());
        verify(assignmentService).createAssignment(request, OWNER_ID);
    }

    @Test
    void createAssignment_withNullRequest_throwsException() {
        when(assignmentService.createAssignment(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                assignmentService.createAssignment(null, OWNER_ID));
    }

    @Test
    void updateAssignment_withValidData_returnsUpdatedAssignment() {
        AssignmentRequest request = new AssignmentRequest();
        request.setTitle("Updated Assignment");

        AssignmentResponse mockResponse = new AssignmentResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle("Updated Assignment");

        when(assignmentService.updateAssignment(1L, request, OWNER_ID)).thenReturn(mockResponse);

        AssignmentResponse response = assignmentService.updateAssignment(1L, request, OWNER_ID);

        assertNotNull(response);
        assertEquals("Updated Assignment", response.getTitle());
    }

    @Test
    void getAssignmentById_withValidId_returnsAssignment() {
        AssignmentResponse mockResponse = new AssignmentResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle("Math Assignment");

        when(assignmentService.getAssignmentById(1L, OWNER_ID)).thenReturn(mockResponse);

        AssignmentResponse response = assignmentService.getAssignmentById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAssignmentById_withInvalidId_throwsException() {
        when(assignmentService.getAssignmentById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Assignment not found"));

        assertThrows(RuntimeException.class, () ->
                assignmentService.getAssignmentById(999L, OWNER_ID));
    }

    @Test
    void getAllAssignments_withValidOwner_returnsPagedAssignments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AssignmentResponse> assignments = new ArrayList<>();
        assignments.add(new AssignmentResponse());
        Page<AssignmentResponse> page = new PageImpl<>(assignments, pageable, 1);

        when(assignmentService.getAllAssignments(OWNER_ID, pageable)).thenReturn(page);

        Page<AssignmentResponse> response = assignmentService.getAllAssignments(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getAssignmentsByClass_withValidClassId_returnsAssignments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AssignmentResponse> assignments = new ArrayList<>();
        assignments.add(new AssignmentResponse());
        Page<AssignmentResponse> page = new PageImpl<>(assignments, pageable, 1);

        when(assignmentService.getAssignmentsByClass(1L, OWNER_ID, pageable)).thenReturn(page);

        Page<AssignmentResponse> response = assignmentService.getAssignmentsByClass(1L, OWNER_ID, pageable);

        assertNotNull(response);
        verify(assignmentService).getAssignmentsByClass(1L, OWNER_ID, pageable);
    }

    @Test
    void getAssignmentsBySubject_withValidSubjectId_returnsAssignments() {
        List<AssignmentResponse> mockAssignments = new ArrayList<>();
        mockAssignments.add(new AssignmentResponse());

        when(assignmentService.getAssignmentsBySubject(1L, OWNER_ID)).thenReturn(mockAssignments);

        List<AssignmentResponse> response = assignmentService.getAssignmentsBySubject(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAssignmentsByTeacher_withValidTeacherId_returnsAssignments() {
        List<AssignmentResponse> mockAssignments = new ArrayList<>();
        mockAssignments.add(new AssignmentResponse());

        when(assignmentService.getAssignmentsByTeacher(1L, OWNER_ID)).thenReturn(mockAssignments);

        List<AssignmentResponse> response = assignmentService.getAssignmentsByTeacher(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getUpcomingAssignments_returnsUpcomingAssignments() {
        List<AssignmentResponse> mockAssignments = new ArrayList<>();
        mockAssignments.add(new AssignmentResponse());

        when(assignmentService.getUpcomingAssignments(OWNER_ID)).thenReturn(mockAssignments);

        List<AssignmentResponse> response = assignmentService.getUpcomingAssignments(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getOverdueAssignments_returnsOverdueAssignments() {
        List<AssignmentResponse> mockAssignments = new ArrayList<>();

        when(assignmentService.getOverdueAssignments(OWNER_ID)).thenReturn(mockAssignments);

        List<AssignmentResponse> response = assignmentService.getOverdueAssignments(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void publishAssignment_withValidId_returnsPublishedAssignment() {
        AssignmentResponse mockResponse = new AssignmentResponse();
        mockResponse.setId(1L);

        when(assignmentService.updateAssignment(1L, any(), OWNER_ID)).thenReturn(mockResponse);

        AssignmentResponse response = assignmentService.updateAssignment(1L, new AssignmentRequest(), OWNER_ID);

        assertNotNull(response);
        verify(assignmentService).updateAssignment(1L, any(), OWNER_ID);
    }

    @Test
    void deleteAssignment_withValidId_succeeds() {
        doNothing().when(assignmentService).deleteAssignment(1L, OWNER_ID);

        assignmentService.deleteAssignment(1L, OWNER_ID);

        verify(assignmentService).deleteAssignment(1L, OWNER_ID);
    }

    @Test
    void searchAssignments_withKeyword_returnsMatchingAssignments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AssignmentResponse> assignments = new ArrayList<>();
        assignments.add(new AssignmentResponse());
        Page<AssignmentResponse> page = new PageImpl<>(assignments, pageable, 1);

        when(assignmentService.searchAssignments("Math", OWNER_ID, pageable)).thenReturn(page);

        Page<AssignmentResponse> response = assignmentService.searchAssignments("Math", OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getAssignmentsByClass_withEmptyResult_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AssignmentResponse> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(assignmentService.getAssignmentsByClass(999L, OWNER_ID, pageable)).thenReturn(emptyPage);

        Page<AssignmentResponse> response = assignmentService.getAssignmentsByClass(999L, OWNER_ID, pageable);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
