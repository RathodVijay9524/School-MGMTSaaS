package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AttendanceStatistics;
import com.vijay.User_Master.dto.AttendanceRequest;
import com.vijay.User_Master.dto.AttendanceResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttendanceServiceTest extends ServiceTestBase {

    @Mock
    private AttendanceService attendanceService;

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
    void getAllAttendance_withValidPageable_returnsPagedAttendance() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AttendanceResponse> attendanceList = new ArrayList<>();
        attendanceList.add(new AttendanceResponse());
        Page<AttendanceResponse> page = new PageImpl<>(attendanceList, pageable, 1);

        when(attendanceService.getAllAttendance(pageable)).thenReturn(page);

        Page<AttendanceResponse> response = attendanceService.getAllAttendance(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(attendanceService).getAllAttendance(pageable);
    }

    @Test
    void markAttendance_withValidRequest_returnsAttendance() {
        AttendanceRequest request = new AttendanceRequest();
        AttendanceResponse mockResponse = new AttendanceResponse();
        mockResponse.setId(1L);

        when(attendanceService.markAttendance(request)).thenReturn(mockResponse);

        AttendanceResponse response = attendanceService.markAttendance(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(attendanceService).markAttendance(request);
    }

    @Test
    void markAttendance_withNullRequest_throwsException() {
        when(attendanceService.markAttendance(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                attendanceService.markAttendance(null));
    }

    @Test
    void markBulkAttendance_withValidRequests_returnsAttendanceList() {
        List<AttendanceRequest> requests = new ArrayList<>();
        requests.add(new AttendanceRequest());
        List<AttendanceResponse> mockResponses = new ArrayList<>();
        mockResponses.add(new AttendanceResponse());

        when(attendanceService.markBulkAttendance(requests)).thenReturn(mockResponses);

        List<AttendanceResponse> response = attendanceService.markBulkAttendance(requests);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(attendanceService).markBulkAttendance(requests);
    }

    @Test
    void updateAttendance_withValidData_returnsUpdatedAttendance() {
        AttendanceRequest request = new AttendanceRequest();
        AttendanceResponse mockResponse = new AttendanceResponse();
        mockResponse.setId(1L);

        when(attendanceService.updateAttendance(1L, request)).thenReturn(mockResponse);

        AttendanceResponse response = attendanceService.updateAttendance(1L, request);

        assertNotNull(response);
        verify(attendanceService).updateAttendance(1L, request);
    }

    @Test
    void getAttendanceById_withValidId_returnsAttendance() {
        AttendanceResponse mockResponse = new AttendanceResponse();
        mockResponse.setId(1L);

        when(attendanceService.getAttendanceById(1L)).thenReturn(mockResponse);

        AttendanceResponse response = attendanceService.getAttendanceById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAttendanceById_withInvalidId_throwsException() {
        when(attendanceService.getAttendanceById(999L))
                .thenThrow(new RuntimeException("Attendance not found"));

        assertThrows(RuntimeException.class, () ->
                attendanceService.getAttendanceById(999L));
    }

    @Test
    void getAttendanceByStudent_withValidStudentId_returnsPagedAttendance() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AttendanceResponse> attendanceList = new ArrayList<>();
        attendanceList.add(new AttendanceResponse());
        Page<AttendanceResponse> page = new PageImpl<>(attendanceList, pageable, 1);

        when(attendanceService.getAttendanceByStudent(100L, pageable)).thenReturn(page);

        Page<AttendanceResponse> response = attendanceService.getAttendanceByStudent(100L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getAttendanceByStudentAndDateRange_withValidData_returnsAttendanceList() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<AttendanceResponse> mockAttendance = new ArrayList<>();
        mockAttendance.add(new AttendanceResponse());

        when(attendanceService.getAttendanceByStudentAndDateRange(100L, startDate, endDate))
                .thenReturn(mockAttendance);

        List<AttendanceResponse> response = attendanceService.getAttendanceByStudentAndDateRange(100L, startDate, endDate);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAttendanceByClassAndDate_withValidData_returnsAttendanceList() {
        List<AttendanceResponse> mockAttendance = new ArrayList<>();
        mockAttendance.add(new AttendanceResponse());

        when(attendanceService.getAttendanceByClassAndDate(1L, LocalDate.now()))
                .thenReturn(mockAttendance);

        List<AttendanceResponse> response = attendanceService.getAttendanceByClassAndDate(1L, LocalDate.now());

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAbsentStudents_withValidData_returnsAbsentStudents() {
        List<AttendanceResponse> mockAttendance = new ArrayList<>();
        mockAttendance.add(new AttendanceResponse());

        when(attendanceService.getAbsentStudents(1L, LocalDate.now()))
                .thenReturn(mockAttendance);

        List<AttendanceResponse> response = attendanceService.getAbsentStudents(1L, LocalDate.now());

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void calculateAttendancePercentage_withValidStudentId_returnsPercentage() {
        when(attendanceService.calculateAttendancePercentage(100L)).thenReturn(85.5);

        Double percentage = attendanceService.calculateAttendancePercentage(100L);

        assertNotNull(percentage);
        assertEquals(85.5, percentage);
    }

    @Test
    void calculateAttendancePercentageInRange_withValidData_returnsPercentage() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        when(attendanceService.calculateAttendancePercentageInRange(100L, startDate, endDate))
                .thenReturn(92.0);

        Double percentage = attendanceService.calculateAttendancePercentageInRange(100L, startDate, endDate);

        assertNotNull(percentage);
        assertEquals(92.0, percentage);
    }

    @Test
    void getClassAttendanceStatistics_withValidClassId_returnsStatistics() {
        AttendanceStatistics mockStats = new AttendanceStatistics();

        when(attendanceService.getClassAttendanceStatistics(1L, LocalDate.now()))
                .thenReturn(mockStats);

        AttendanceStatistics response = attendanceService.getClassAttendanceStatistics(1L, LocalDate.now());

        assertNotNull(response);
        verify(attendanceService).getClassAttendanceStatistics(1L, LocalDate.now());
    }

    @Test
    void isAttendanceMarked_withValidData_returnsBoolean() {
        when(attendanceService.isAttendanceMarked(100L, LocalDate.now(), "MORNING"))
                .thenReturn(true);

        boolean result = attendanceService.isAttendanceMarked(100L, LocalDate.now(), "MORNING");

        assertTrue(result);
        verify(attendanceService).isAttendanceMarked(100L, LocalDate.now(), "MORNING");
    }

    @Test
    void deleteAttendance_withValidId_succeeds() {
        doNothing().when(attendanceService).deleteAttendance(1L);

        attendanceService.deleteAttendance(1L);

        verify(attendanceService).deleteAttendance(1L);
    }

    @Test
    void getMonthlyAttendanceStatistics_withValidData_returnsStatisticsList() {
        List<AttendanceStatistics> mockStats = new ArrayList<>();
        mockStats.add(new AttendanceStatistics());

        when(attendanceService.getMonthlyAttendanceStatistics(1L, 2024, 1))
                .thenReturn(mockStats);

        List<AttendanceStatistics> response = attendanceService.getMonthlyAttendanceStatistics(1L, 2024, 1);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(attendanceService).getMonthlyAttendanceStatistics(1L, 2024, 1);
    }

    @Test
    void markBulkAttendance_withEmptyList_returnsEmptyList() {
        List<AttendanceRequest> requests = new ArrayList<>();
        List<AttendanceResponse> mockResponses = new ArrayList<>();

        when(attendanceService.markBulkAttendance(requests)).thenReturn(mockResponses);

        List<AttendanceResponse> response = attendanceService.markBulkAttendance(requests);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAttendanceByClassAndDateRange_withValidData_returnsAttendanceList() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<AttendanceResponse> mockAttendance = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockAttendance.add(new AttendanceResponse());
        }

        when(attendanceService.getAttendanceByClassAndDateRange(1L, startDate, endDate))
                .thenReturn(mockAttendance);

        List<AttendanceResponse> response = attendanceService.getAttendanceByClassAndDateRange(1L, startDate, endDate);

        assertNotNull(response);
        assertEquals(5, response.size());
    }
}
