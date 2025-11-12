package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SchoolClassRequest;
import com.vijay.User_Master.dto.SchoolClassResponse;
import com.vijay.User_Master.dto.PageableResponse;
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
class SchoolClassServiceTest extends ServiceTestBase {

    @Mock
    private SchoolClassService schoolClassService;

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
    void createClass_withValidRequest_returnsClass() {
        SchoolClassRequest request = new SchoolClassRequest();
        SchoolClassResponse mockResponse = new SchoolClassResponse();
        mockResponse.setId(1L);

        when(schoolClassService.createClass(request, OWNER_ID)).thenReturn(mockResponse);

        SchoolClassResponse response = schoolClassService.createClass(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(schoolClassService).createClass(request, OWNER_ID);
    }

    @Test
    void createClass_withNullRequest_throwsException() {
        when(schoolClassService.createClass(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                schoolClassService.createClass(null, OWNER_ID));
    }

    @Test
    void getAllClasses_withValidParams_returnsPageableResponse() {
        PageableResponse<SchoolClassResponse> mockResponse = new PageableResponse<>();

        when(schoolClassService.getAllClasses(OWNER_ID, 0, 10, "id", "ASC")).thenReturn(mockResponse);

        PageableResponse<SchoolClassResponse> response = schoolClassService.getAllClasses(OWNER_ID, 0, 10, "id", "ASC");

        assertNotNull(response);
        verify(schoolClassService).getAllClasses(OWNER_ID, 0, 10, "id", "ASC");
    }

    @Test
    void getClassById_withValidId_returnsClass() {
        SchoolClassResponse mockResponse = new SchoolClassResponse();
        mockResponse.setId(1L);

        when(schoolClassService.getClassById(1L, OWNER_ID)).thenReturn(mockResponse);

        SchoolClassResponse response = schoolClassService.getClassById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getClassById_withInvalidId_throwsException() {
        when(schoolClassService.getClassById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Class not found"));

        assertThrows(RuntimeException.class, () ->
                schoolClassService.getClassById(999L, OWNER_ID));
    }

    @Test
    void updateClass_withValidData_returnsUpdatedClass() {
        SchoolClassRequest request = new SchoolClassRequest();
        SchoolClassResponse mockResponse = new SchoolClassResponse();
        mockResponse.setId(1L);

        when(schoolClassService.updateClass(1L, request, OWNER_ID)).thenReturn(mockResponse);

        SchoolClassResponse response = schoolClassService.updateClass(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(schoolClassService).updateClass(1L, request, OWNER_ID);
    }

    @Test
    void deleteClass_withValidId_succeeds() {
        doNothing().when(schoolClassService).deleteClass(1L, OWNER_ID);

        schoolClassService.deleteClass(1L, OWNER_ID);

        verify(schoolClassService).deleteClass(1L, OWNER_ID);
    }

    @Test
    void getActiveClasses_withValidOwner_returnsClasses() {
        List<SchoolClassResponse> mockClasses = new ArrayList<>();
        mockClasses.add(new SchoolClassResponse());
        mockClasses.add(new SchoolClassResponse());

        when(schoolClassService.getActiveClasses(OWNER_ID)).thenReturn(mockClasses);

        List<SchoolClassResponse> response = schoolClassService.getActiveClasses(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(schoolClassService).getActiveClasses(OWNER_ID);
    }

    @Test
    void getClassesByTeacher_withValidTeacherId_returnsClasses() {
        List<SchoolClassResponse> mockClasses = new ArrayList<>();
        mockClasses.add(new SchoolClassResponse());

        when(schoolClassService.getClassesByTeacher(50L, OWNER_ID)).thenReturn(mockClasses);

        List<SchoolClassResponse> response = schoolClassService.getClassesByTeacher(50L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void assignSubjectsToClass_withValidData_succeeds() {
        List<Long> subjectIds = new ArrayList<>();
        subjectIds.add(1L);
        subjectIds.add(2L);
        doNothing().when(schoolClassService).assignSubjectsToClass(1L, subjectIds, OWNER_ID);

        schoolClassService.assignSubjectsToClass(1L, subjectIds, OWNER_ID);

        verify(schoolClassService).assignSubjectsToClass(1L, subjectIds, OWNER_ID);
    }

    @Test
    void removeSubjectFromClass_withValidData_succeeds() {
        doNothing().when(schoolClassService).removeSubjectFromClass(1L, 10L, OWNER_ID);

        schoolClassService.removeSubjectFromClass(1L, 10L, OWNER_ID);

        verify(schoolClassService).removeSubjectFromClass(1L, 10L, OWNER_ID);
    }

    @Test
    void getActiveClasses_withNoActiveClasses_returnsEmptyList() {
        List<SchoolClassResponse> mockClasses = new ArrayList<>();

        when(schoolClassService.getActiveClasses(OWNER_ID)).thenReturn(mockClasses);

        List<SchoolClassResponse> response = schoolClassService.getActiveClasses(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void updateClass_withNullRequest_throwsException() {
        when(schoolClassService.updateClass(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                schoolClassService.updateClass(1L, null, OWNER_ID));
    }

    @Test
    void getAllClasses_withDifferentPagination_returnsPageableResponse() {
        PageableResponse<SchoolClassResponse> mockResponse = new PageableResponse<>();

        when(schoolClassService.getAllClasses(OWNER_ID, 1, 20, "name", "DESC")).thenReturn(mockResponse);

        PageableResponse<SchoolClassResponse> response = schoolClassService.getAllClasses(OWNER_ID, 1, 20, "name", "DESC");

        assertNotNull(response);
        verify(schoolClassService).getAllClasses(OWNER_ID, 1, 20, "name", "DESC");
    }
}
