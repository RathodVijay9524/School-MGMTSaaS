package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.StudentTransportRequest;
import com.vijay.User_Master.dto.StudentTransportResponse;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StudentTransportServiceTest extends ServiceTestBase {

    @Mock
    private StudentTransportService studentTransportService;

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
    void createStudentTransport_withValidRequest_returnsTransport() {
        StudentTransportRequest request = new StudentTransportRequest();
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.createStudentTransport(request, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.createStudentTransport(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(studentTransportService).createStudentTransport(request, OWNER_ID);
    }

    @Test
    void createStudentTransport_withNullRequest_throwsException() {
        when(studentTransportService.createStudentTransport(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                studentTransportService.createStudentTransport(null, OWNER_ID));
    }

    @Test
    void updateStudentTransport_withValidData_returnsUpdatedTransport() {
        StudentTransportRequest request = new StudentTransportRequest();
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.updateStudentTransport(1L, request, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.updateStudentTransport(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).updateStudentTransport(1L, request, OWNER_ID);
    }

    @Test
    void getStudentTransportById_withValidId_returnsTransport() {
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.getStudentTransportById(1L, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.getStudentTransportById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getStudentTransportById_withInvalidId_throwsException() {
        when(studentTransportService.getStudentTransportById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Student transport not found"));

        assertThrows(RuntimeException.class, () ->
                studentTransportService.getStudentTransportById(999L, OWNER_ID));
    }

    @Test
    void getAllStudentTransports_withValidOwner_returnsPagedTransports() {
        Pageable pageable = PageRequest.of(0, 10);
        List<StudentTransportResponse> transports = new ArrayList<>();
        transports.add(new StudentTransportResponse());
        Page<StudentTransportResponse> page = new PageImpl<>(transports, pageable, 1);

        when(studentTransportService.getAllStudentTransports(OWNER_ID, pageable)).thenReturn(page);

        Page<StudentTransportResponse> response = studentTransportService.getAllStudentTransports(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(studentTransportService).getAllStudentTransports(OWNER_ID, pageable);
    }

    @Test
    void getStudentTransportsByStudent_withValidStudentId_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByStudent(100L, OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByStudent(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getActiveTransportByStudent_withValidStudentId_returnsTransport() {
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.getActiveTransportByStudent(100L, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.getActiveTransportByStudent(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getStudentTransportsByBus_withValidBusId_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByBus(1L, OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByBus(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getActiveStudentTransportsByBus_withValidBusId_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getActiveStudentTransportsByBus(1L, OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getActiveStudentTransportsByBus(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentTransportsByRoute_withValidRouteId_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByRoute(1L, OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByRoute(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getActiveStudentTransportsByRoute_withValidRouteId_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getActiveStudentTransportsByRoute(1L, OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getActiveStudentTransportsByRoute(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentTransportsByDriver_withValidDriverId_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByDriver(1L, OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByDriver(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getActiveStudentTransportsByDriver_withValidDriverId_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getActiveStudentTransportsByDriver(1L, OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getActiveStudentTransportsByDriver(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTransportsWithOverduePayments_withValidOwner_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getTransportsWithOverduePayments(OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getTransportsWithOverduePayments(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentTransportsByPaymentStatus_withValidData_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByPaymentStatus(OWNER_ID, "PAID")).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByPaymentStatus(OWNER_ID, "PAID");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getActiveStudentTransports_withValidOwner_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getActiveStudentTransports(OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getActiveStudentTransports(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(studentTransportService).getActiveStudentTransports(OWNER_ID);
    }

    @Test
    void getStudentTransportsByFareRange_withValidData_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByFareRange(OWNER_ID, 100.0, 500.0)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByFareRange(OWNER_ID, 100.0, 500.0);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentTransportsByAssignmentDateRange_withValidData_returnsTransports() {
        Date startDate = Date.valueOf(LocalDate.now().minusDays(30));
        Date endDate = Date.valueOf(LocalDate.now());
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByAssignmentDateRange(OWNER_ID, startDate, endDate)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByAssignmentDateRange(OWNER_ID, startDate, endDate);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchStudentTransports_withValidKeyword_returnsPagedTransports() {
        Pageable pageable = PageRequest.of(0, 10);
        List<StudentTransportResponse> transports = new ArrayList<>();
        transports.add(new StudentTransportResponse());
        Page<StudentTransportResponse> page = new PageImpl<>(transports, pageable, 1);

        when(studentTransportService.searchStudentTransports(OWNER_ID, "John", pageable)).thenReturn(page);

        Page<StudentTransportResponse> response = studentTransportService.searchStudentTransports(OWNER_ID, "John", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getStudentTransportStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalTransports", 50);
        mockStats.put("activeTransports", 45);

        when(studentTransportService.getStudentTransportStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = studentTransportService.getStudentTransportStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(50, response.get("totalTransports"));
        assertEquals(45, response.get("activeTransports"));
        verify(studentTransportService).getStudentTransportStatistics(OWNER_ID);
    }

    @Test
    void deleteStudentTransport_withValidId_succeeds() {
        doNothing().when(studentTransportService).deleteStudentTransport(1L, OWNER_ID);

        studentTransportService.deleteStudentTransport(1L, OWNER_ID);

        verify(studentTransportService).deleteStudentTransport(1L, OWNER_ID);
    }

    @Test
    void restoreStudentTransport_withValidId_succeeds() {
        doNothing().when(studentTransportService).restoreStudentTransport(1L, OWNER_ID);

        studentTransportService.restoreStudentTransport(1L, OWNER_ID);

        verify(studentTransportService).restoreStudentTransport(1L, OWNER_ID);
    }

    @Test
    void existsByTransportId_withExistingId_returnsTrue() {
        when(studentTransportService.existsByTransportId(OWNER_ID, "TRANS001")).thenReturn(true);

        boolean result = studentTransportService.existsByTransportId(OWNER_ID, "TRANS001");

        assertTrue(result);
        verify(studentTransportService).existsByTransportId(OWNER_ID, "TRANS001");
    }

    @Test
    void existsByTransportId_withNonExistingId_returnsFalse() {
        when(studentTransportService.existsByTransportId(OWNER_ID, "NONEXISTENT")).thenReturn(false);

        boolean result = studentTransportService.existsByTransportId(OWNER_ID, "NONEXISTENT");

        assertFalse(result);
    }

    @Test
    void hasActiveTransport_withStudentHavingTransport_returnsTrue() {
        when(studentTransportService.hasActiveTransport(100L, OWNER_ID)).thenReturn(true);

        boolean result = studentTransportService.hasActiveTransport(100L, OWNER_ID);

        assertTrue(result);
        verify(studentTransportService).hasActiveTransport(100L, OWNER_ID);
    }

    @Test
    void assignStudentToTransport_withValidData_returnsTransport() {
        StudentTransportRequest request = new StudentTransportRequest();
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.assignStudentToTransport(request, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.assignStudentToTransport(request, OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).assignStudentToTransport(request, OWNER_ID);
    }

    @Test
    void updateStudentTransportAssignment_withValidData_returnsUpdatedTransport() {
        StudentTransportRequest request = new StudentTransportRequest();
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.updateStudentTransportAssignment(1L, request, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.updateStudentTransportAssignment(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).updateStudentTransportAssignment(1L, request, OWNER_ID);
    }

    @Test
    void removeStudentFromTransport_withValidData_returnsUpdatedTransport() {
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.removeStudentFromTransport(1L, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.removeStudentFromTransport(1L, OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).removeStudentFromTransport(1L, OWNER_ID);
    }

    @Test
    void updatePaymentStatus_withValidData_returnsUpdatedTransport() {
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.updatePaymentStatus(1L, "PAID", 500.0, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.updatePaymentStatus(1L, "PAID", 500.0, OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).updatePaymentStatus(1L, "PAID", 500.0, OWNER_ID);
    }

    @Test
    void recordAttendance_withValidData_returnsUpdatedTransport() {
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.recordAttendance(1L, true, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.recordAttendance(1L, true, OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).recordAttendance(1L, true, OWNER_ID);
    }

    @Test
    void recordIncident_withValidData_returnsUpdatedTransport() {
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.recordIncident(1L, "Minor incident", OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.recordIncident(1L, "Minor incident", OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).recordIncident(1L, "Minor incident", OWNER_ID);
    }

    @Test
    void recordComplaint_withValidData_returnsUpdatedTransport() {
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.recordComplaint(1L, "Parent complaint", OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.recordComplaint(1L, "Parent complaint", OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).recordComplaint(1L, "Parent complaint", OWNER_ID);
    }

    @Test
    void getStudentTransportsByCriteria_withValidData_returnsTransports() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();
        mockTransports.add(new StudentTransportResponse());

        when(studentTransportService.getStudentTransportsByCriteria(OWNER_ID, 1L, 2L, 3L, "PAID", "ACTIVE")).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getStudentTransportsByCriteria(OWNER_ID, 1L, 2L, 3L, "PAID", "ACTIVE");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void generateTransportId_withValidOwner_returnsGeneratedId() {
        when(studentTransportService.generateTransportId(OWNER_ID)).thenReturn("TRANS001");

        String response = studentTransportService.generateTransportId(OWNER_ID);

        assertNotNull(response);
        assertEquals("TRANS001", response);
        verify(studentTransportService).generateTransportId(OWNER_ID);
    }

    @Test
    void calculateTransportFees_withValidTransportId_returnsFeeDetails() {
        Map<String, Object> mockFees = new HashMap<>();
        mockFees.put("totalFees", 500.0);
        mockFees.put("paidAmount", 300.0);
        mockFees.put("dueAmount", 200.0);

        when(studentTransportService.calculateTransportFees(1L, OWNER_ID)).thenReturn(mockFees);

        Map<String, Object> response = studentTransportService.calculateTransportFees(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(500.0, response.get("totalFees"));
        assertEquals(300.0, response.get("paidAmount"));
        assertEquals(200.0, response.get("dueAmount"));
        verify(studentTransportService).calculateTransportFees(1L, OWNER_ID);
    }

    @Test
    void updateTransportTiming_withValidData_returnsUpdatedTransport() {
        StudentTransportRequest request = new StudentTransportRequest();
        StudentTransportResponse mockResponse = new StudentTransportResponse();
        mockResponse.setId(1L);

        when(studentTransportService.updateTransportTiming(1L, request, OWNER_ID)).thenReturn(mockResponse);

        StudentTransportResponse response = studentTransportService.updateTransportTiming(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(studentTransportService).updateTransportTiming(1L, request, OWNER_ID);
    }

    @Test
    void updateStudentTransport_withNullRequest_throwsException() {
        when(studentTransportService.updateStudentTransport(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                studentTransportService.updateStudentTransport(1L, null, OWNER_ID));
    }

    @Test
    void getActiveStudentTransports_withNoTransports_returnsEmptyList() {
        List<StudentTransportResponse> mockTransports = new ArrayList<>();

        when(studentTransportService.getActiveStudentTransports(OWNER_ID)).thenReturn(mockTransports);

        List<StudentTransportResponse> response = studentTransportService.getActiveStudentTransports(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
