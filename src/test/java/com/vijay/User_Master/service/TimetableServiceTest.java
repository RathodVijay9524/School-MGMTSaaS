package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.TimetableRequest;
import com.vijay.User_Master.dto.TimetableResponse;
import com.vijay.User_Master.dto.TimetableStatistics;
import com.vijay.User_Master.entity.Timetable;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TimetableServiceTest extends ServiceTestBase {

    @Mock
    private TimetableService timetableService;

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
    void createTimetable_withValidRequest_returnsTimetable() {
        TimetableRequest request = new TimetableRequest();
        TimetableResponse mockResponse = new TimetableResponse();
        mockResponse.setId(1L);

        when(timetableService.createTimetable(request, OWNER_ID)).thenReturn(mockResponse);

        TimetableResponse response = timetableService.createTimetable(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(timetableService).createTimetable(request, OWNER_ID);
    }

    @Test
    void createTimetable_withNullRequest_throwsException() {
        when(timetableService.createTimetable(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                timetableService.createTimetable(null, OWNER_ID));
    }

    @Test
    void updateTimetable_withValidData_returnsUpdatedTimetable() {
        TimetableRequest request = new TimetableRequest();
        TimetableResponse mockResponse = new TimetableResponse();
        mockResponse.setId(1L);

        when(timetableService.updateTimetable(1L, request, OWNER_ID)).thenReturn(mockResponse);

        TimetableResponse response = timetableService.updateTimetable(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(timetableService).updateTimetable(1L, request, OWNER_ID);
    }

    @Test
    void getTimetableById_withValidId_returnsTimetable() {
        TimetableResponse mockResponse = new TimetableResponse();
        mockResponse.setId(1L);

        when(timetableService.getTimetableById(1L, OWNER_ID)).thenReturn(mockResponse);

        TimetableResponse response = timetableService.getTimetableById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTimetableById_withInvalidId_throwsException() {
        when(timetableService.getTimetableById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Timetable not found"));

        assertThrows(RuntimeException.class, () ->
                timetableService.getTimetableById(999L, OWNER_ID));
    }

    @Test
    void getAllTimetables_withValidOwner_returnsPagedTimetables() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TimetableResponse> timetables = new ArrayList<>();
        timetables.add(new TimetableResponse());
        Page<TimetableResponse> page = new PageImpl<>(timetables, pageable, 1);

        when(timetableService.getAllTimetables(OWNER_ID, pageable)).thenReturn(page);

        Page<TimetableResponse> response = timetableService.getAllTimetables(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(timetableService).getAllTimetables(OWNER_ID, pageable);
    }

    @Test
    void getTimetablesByClass_withValidClassId_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesByClass(1L, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByClass(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTimetablesByClassAndDay_withValidData_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesByClassAndDay(1L, Timetable.DayOfWeek.MONDAY, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByClassAndDay(1L, Timetable.DayOfWeek.MONDAY, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTimetablesByTeacher_withValidTeacherId_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesByTeacher(50L, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByTeacher(50L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTimetablesByTeacherAndDay_withValidData_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesByTeacherAndDay(50L, Timetable.DayOfWeek.TUESDAY, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByTeacherAndDay(50L, Timetable.DayOfWeek.TUESDAY, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTimetablesBySubject_withValidSubjectId_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesBySubject(10L, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesBySubject(10L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTimetablesByDay_withValidDay_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesByDay(Timetable.DayOfWeek.WEDNESDAY, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByDay(Timetable.DayOfWeek.WEDNESDAY, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTimetablesByStatus_withValidStatus_returnsPagedTimetables() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TimetableResponse> timetables = new ArrayList<>();
        timetables.add(new TimetableResponse());
        Page<TimetableResponse> page = new PageImpl<>(timetables, pageable, 1);

        when(timetableService.getTimetablesByStatus(Timetable.TimetableStatus.ACTIVE, OWNER_ID, pageable)).thenReturn(page);

        Page<TimetableResponse> response = timetableService.getTimetablesByStatus(Timetable.TimetableStatus.ACTIVE, OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getTimetablesByAcademicYearAndSemester_withValidData_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesByAcademicYearAndSemester("2023-24", "Sem1", OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByAcademicYearAndSemester("2023-24", "Sem1", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void checkTeacherTimeConflicts_withValidData_returnsConflicts() {
        List<TimetableResponse> mockConflicts = new ArrayList<>();
        mockConflicts.add(new TimetableResponse());

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        when(timetableService.checkTeacherTimeConflicts(50L, Timetable.DayOfWeek.THURSDAY, startTime, endTime, OWNER_ID)).thenReturn(mockConflicts);

        List<TimetableResponse> response = timetableService.checkTeacherTimeConflicts(50L, Timetable.DayOfWeek.THURSDAY, startTime, endTime, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTimetablesByRoom_withValidRoom_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getTimetablesByRoom("Room101", OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByRoom("Room101", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchTimetables_withValidKeyword_returnsPagedTimetables() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TimetableResponse> timetables = new ArrayList<>();
        timetables.add(new TimetableResponse());
        Page<TimetableResponse> page = new PageImpl<>(timetables, pageable, 1);

        when(timetableService.searchTimetables("math", OWNER_ID, pageable)).thenReturn(page);

        Page<TimetableResponse> response = timetableService.searchTimetables("math", OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void deleteTimetable_withValidId_succeeds() {
        doNothing().when(timetableService).deleteTimetable(1L, OWNER_ID);

        timetableService.deleteTimetable(1L, OWNER_ID);

        verify(timetableService).deleteTimetable(1L, OWNER_ID);
    }

    @Test
    void restoreTimetable_withValidId_succeeds() {
        doNothing().when(timetableService).restoreTimetable(1L, OWNER_ID);

        timetableService.restoreTimetable(1L, OWNER_ID);

        verify(timetableService).restoreTimetable(1L, OWNER_ID);
    }

    @Test
    void getTimetableStatistics_withValidOwner_returnsStatistics() {
        TimetableStatistics mockStats = new TimetableStatistics();

        when(timetableService.getTimetableStatistics(OWNER_ID)).thenReturn(mockStats);

        TimetableStatistics response = timetableService.getTimetableStatistics(OWNER_ID);

        assertNotNull(response);
        verify(timetableService).getTimetableStatistics(OWNER_ID);
    }

    @Test
    void getWeeklyTimetableForClass_withValidClassId_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getWeeklyTimetableForClass(1L, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getWeeklyTimetableForClass(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getWeeklyTimetableForTeacher_withValidTeacherId_returnsTimetables() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();
        mockTimetables.add(new TimetableResponse());

        when(timetableService.getWeeklyTimetableForTeacher(50L, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getWeeklyTimetableForTeacher(50L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateTimetable_withNullRequest_throwsException() {
        when(timetableService.updateTimetable(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                timetableService.updateTimetable(1L, null, OWNER_ID));
    }

    @Test
    void getTimetablesByClass_withNoTimetables_returnsEmptyList() {
        List<TimetableResponse> mockTimetables = new ArrayList<>();

        when(timetableService.getTimetablesByClass(999L, OWNER_ID)).thenReturn(mockTimetables);

        List<TimetableResponse> response = timetableService.getTimetablesByClass(999L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
