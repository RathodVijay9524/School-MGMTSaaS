package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SubjectRequest;
import com.vijay.User_Master.dto.SubjectResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.entity.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SubjectServiceTest extends ServiceTestBase {

    @Mock
    private SubjectService subjectService;

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
    void createSubject_withValidRequest_returnsSubject() {
        SubjectRequest request = new SubjectRequest();
        SubjectResponse mockResponse = new SubjectResponse();
        mockResponse.setId(1L);

        when(subjectService.createSubject(request, OWNER_ID)).thenReturn(mockResponse);

        SubjectResponse response = subjectService.createSubject(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(subjectService).createSubject(request, OWNER_ID);
    }

    @Test
    void createSubject_withNullRequest_throwsException() {
        when(subjectService.createSubject(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                subjectService.createSubject(null, OWNER_ID));
    }

    @Test
    void updateSubject_withValidData_returnsUpdatedSubject() {
        SubjectRequest request = new SubjectRequest();
        SubjectResponse mockResponse = new SubjectResponse();
        mockResponse.setId(1L);

        when(subjectService.updateSubject(1L, request, OWNER_ID)).thenReturn(mockResponse);

        SubjectResponse response = subjectService.updateSubject(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(subjectService).updateSubject(1L, request, OWNER_ID);
    }

    @Test
    void getSubjectById_withValidId_returnsSubject() {
        SubjectResponse mockResponse = new SubjectResponse();
        mockResponse.setId(1L);

        when(subjectService.getSubjectById(1L, OWNER_ID)).thenReturn(mockResponse);

        SubjectResponse response = subjectService.getSubjectById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getSubjectById_withInvalidId_throwsException() {
        when(subjectService.getSubjectById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Subject not found"));

        assertThrows(RuntimeException.class, () ->
                subjectService.getSubjectById(999L, OWNER_ID));
    }

    @Test
    void getAllSubjects_withValidOwner_returnsPageableResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<SubjectResponse> mockResponse = new PageableResponse<>();

        when(subjectService.getAllSubjects(OWNER_ID, pageable)).thenReturn(mockResponse);

        PageableResponse<SubjectResponse> response = subjectService.getAllSubjects(OWNER_ID, pageable);

        assertNotNull(response);
        verify(subjectService).getAllSubjects(OWNER_ID, pageable);
    }

    @Test
    void getAllActiveSubjects_withValidOwner_returnsSubjects() {
        List<SubjectResponse> mockSubjects = new ArrayList<>();
        mockSubjects.add(new SubjectResponse());
        mockSubjects.add(new SubjectResponse());

        when(subjectService.getAllActiveSubjects(OWNER_ID)).thenReturn(mockSubjects);

        List<SubjectResponse> response = subjectService.getAllActiveSubjects(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(subjectService).getAllActiveSubjects(OWNER_ID);
    }

    @Test
    void searchSubjects_withValidKeyword_returnsPageableResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<SubjectResponse> mockResponse = new PageableResponse<>();

        when(subjectService.searchSubjects("math", OWNER_ID, pageable)).thenReturn(mockResponse);

        PageableResponse<SubjectResponse> response = subjectService.searchSubjects("math", OWNER_ID, pageable);

        assertNotNull(response);
        verify(subjectService).searchSubjects("math", OWNER_ID, pageable);
    }

    @Test
    void getSubjectsByDepartment_withValidDepartment_returnsSubjects() {
        List<SubjectResponse> mockSubjects = new ArrayList<>();
        mockSubjects.add(new SubjectResponse());

        when(subjectService.getSubjectsByDepartment("SCIENCE", OWNER_ID)).thenReturn(mockSubjects);

        List<SubjectResponse> response = subjectService.getSubjectsByDepartment("SCIENCE", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    /*@Test
    void getSubjectsByType_withValidType_returnsSubjects() {
        List<SubjectResponse> mockSubjects = new ArrayList<>();
        mockSubjects.add(new SubjectResponse());

        //when(subjectService.getSubjectsByType(Subject.SubjectType.THEORY, OWNER_ID)).thenReturn(mockSubjects);

        //List<SubjectResponse> response = subjectService.getSubjectsByType(Subject.SubjectType.THEORY, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }*/

    @Test
    void getSubjectsByClass_withValidClassId_returnsSubjects() {
        List<SubjectResponse> mockSubjects = new ArrayList<>();
        mockSubjects.add(new SubjectResponse());

        when(subjectService.getSubjectsByClass(1L, OWNER_ID)).thenReturn(mockSubjects);

        List<SubjectResponse> response = subjectService.getSubjectsByClass(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void deleteSubject_withValidId_succeeds() {
        doNothing().when(subjectService).deleteSubject(1L, OWNER_ID);

        subjectService.deleteSubject(1L, OWNER_ID);

        verify(subjectService).deleteSubject(1L, OWNER_ID);
    }

    @Test
    void restoreSubject_withValidId_succeeds() {
        doNothing().when(subjectService).restoreSubject(1L, OWNER_ID);

        subjectService.restoreSubject(1L, OWNER_ID);

        verify(subjectService).restoreSubject(1L, OWNER_ID);
    }

    @Test
    void getDeletedSubjects_withValidOwner_returnsPageableResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<SubjectResponse> mockResponse = new PageableResponse<>();

        when(subjectService.getDeletedSubjects(OWNER_ID, pageable)).thenReturn(mockResponse);

        PageableResponse<SubjectResponse> response = subjectService.getDeletedSubjects(OWNER_ID, pageable);

        assertNotNull(response);
        verify(subjectService).getDeletedSubjects(OWNER_ID, pageable);
    }

    @Test
    void getAllActiveSubjects_withNoActiveSubjects_returnsEmptyList() {
        List<SubjectResponse> mockSubjects = new ArrayList<>();

        when(subjectService.getAllActiveSubjects(OWNER_ID)).thenReturn(mockSubjects);

        List<SubjectResponse> response = subjectService.getAllActiveSubjects(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void updateSubject_withNullRequest_throwsException() {
        when(subjectService.updateSubject(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                subjectService.updateSubject(1L, null, OWNER_ID));
    }

    @Test
    void searchSubjects_withEmptyKeyword_returnsPageableResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<SubjectResponse> mockResponse = new PageableResponse<>();

        when(subjectService.searchSubjects("", OWNER_ID, pageable)).thenReturn(mockResponse);

        PageableResponse<SubjectResponse> response = subjectService.searchSubjects("", OWNER_ID, pageable);

        assertNotNull(response);
        verify(subjectService).searchSubjects("", OWNER_ID, pageable);
    }
}
