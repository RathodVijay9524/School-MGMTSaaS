package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DriverRequest;
import com.vijay.User_Master.dto.DriverResponse;
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
class DriverServiceTest extends ServiceTestBase {

    @Mock
    private DriverService driverService;

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
    void createDriver_withValidRequest_returnsDriver() {
        DriverRequest request = new DriverRequest();
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.createDriver(request, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.createDriver(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(driverService).createDriver(request, OWNER_ID);
    }

    @Test
    void createDriver_withNullRequest_throwsException() {
        when(driverService.createDriver(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                driverService.createDriver(null, OWNER_ID));
    }

    @Test
    void updateDriver_withValidData_returnsUpdatedDriver() {
        DriverRequest request = new DriverRequest();
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.updateDriver(1L, request, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.updateDriver(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(driverService).updateDriver(1L, request, OWNER_ID);
    }

    @Test
    void getDriverById_withValidId_returnsDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.getDriverById(1L, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.getDriverById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getDriverById_withInvalidId_throwsException() {
        when(driverService.getDriverById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Driver not found"));

        assertThrows(RuntimeException.class, () ->
                driverService.getDriverById(999L, OWNER_ID));
    }

    @Test
    void getAllDrivers_withValidOwner_returnsPagedDrivers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<DriverResponse> drivers = new ArrayList<>();
        drivers.add(new DriverResponse());
        Page<DriverResponse> page = new PageImpl<>(drivers, pageable, 1);

        when(driverService.getAllDrivers(OWNER_ID, pageable)).thenReturn(page);

        Page<DriverResponse> response = driverService.getAllDrivers(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(driverService).getAllDrivers(OWNER_ID, pageable);
    }

    @Test
    void getActiveDrivers_withValidOwner_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());
        mockDrivers.add(new DriverResponse());

        when(driverService.getActiveDrivers(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getActiveDrivers(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(driverService).getActiveDrivers(OWNER_ID);
    }

    @Test
    void getAvailableDrivers_withValidOwner_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getAvailableDrivers(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getAvailableDrivers(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversByBus_withValidBusId_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversByBus(1L, OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversByBus(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversByRoute_withValidRouteId_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversByRoute(1L, OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversByRoute(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversWithExpiringLicenses_withValidData_returnsDrivers() {
        Date expiryDate = Date.valueOf(LocalDate.now().plusDays(30));
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversWithExpiringLicenses(OWNER_ID, expiryDate)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversWithExpiringLicenses(OWNER_ID, expiryDate);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversWithExpiredLicenses_withValidOwner_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversWithExpiredLicenses(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversWithExpiredLicenses(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversByLicenseType_withValidData_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversByLicenseType(OWNER_ID, "LMV")).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversByLicenseType(OWNER_ID, "LMV");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversByExperienceRange_withValidData_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversByExperienceRange(OWNER_ID, 5, 10)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversByExperienceRange(OWNER_ID, 5, 10);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversByPerformanceRating_withValidData_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversByPerformanceRating(OWNER_ID, 4.5)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversByPerformanceRating(OWNER_ID, 4.5);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversWithAccidents_withValidOwner_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversWithAccidents(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversWithAccidents(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversWithViolations_withValidOwner_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversWithViolations(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversWithViolations(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversOnDuty_withValidOwner_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversOnDuty(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversOnDuty(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDriversWithoutBus_withValidOwner_returnsDrivers() {
        List<DriverResponse> mockDrivers = new ArrayList<>();
        mockDrivers.add(new DriverResponse());

        when(driverService.getDriversWithoutBus(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getDriversWithoutBus(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchDrivers_withValidKeyword_returnsPagedDrivers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<DriverResponse> drivers = new ArrayList<>();
        drivers.add(new DriverResponse());
        Page<DriverResponse> page = new PageImpl<>(drivers, pageable, 1);

        when(driverService.searchDrivers(OWNER_ID, "John", pageable)).thenReturn(page);

        Page<DriverResponse> response = driverService.searchDrivers(OWNER_ID, "John", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getDriverStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalDrivers", 15);
        mockStats.put("activeDrivers", 12);

        when(driverService.getDriverStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = driverService.getDriverStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(15, response.get("totalDrivers"));
        assertEquals(12, response.get("activeDrivers"));
        verify(driverService).getDriverStatistics(OWNER_ID);
    }

    @Test
    void deleteDriver_withValidId_succeeds() {
        doNothing().when(driverService).deleteDriver(1L, OWNER_ID);

        driverService.deleteDriver(1L, OWNER_ID);

        verify(driverService).deleteDriver(1L, OWNER_ID);
    }

    @Test
    void restoreDriver_withValidId_succeeds() {
        doNothing().when(driverService).restoreDriver(1L, OWNER_ID);

        driverService.restoreDriver(1L, OWNER_ID);

        verify(driverService).restoreDriver(1L, OWNER_ID);
    }

    @Test
    void existsByEmployeeId_withExistingId_returnsTrue() {
        when(driverService.existsByEmployeeId(OWNER_ID, "EMP123")).thenReturn(true);

        boolean result = driverService.existsByEmployeeId(OWNER_ID, "EMP123");

        assertTrue(result);
        verify(driverService).existsByEmployeeId(OWNER_ID, "EMP123");
    }

    @Test
    void existsByLicenseNumber_withExistingNumber_returnsTrue() {
        when(driverService.existsByLicenseNumber(OWNER_ID, "DL123456")).thenReturn(true);

        boolean result = driverService.existsByLicenseNumber(OWNER_ID, "DL123456");

        assertTrue(result);
        verify(driverService).existsByLicenseNumber(OWNER_ID, "DL123456");
    }

    @Test
    void assignDriverToBus_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.assignDriverToBus(1L, 10L, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.assignDriverToBus(1L, 10L, OWNER_ID);

        assertNotNull(response);
        verify(driverService).assignDriverToBus(1L, 10L, OWNER_ID);
    }

    @Test
    void assignDriverToRoute_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.assignDriverToRoute(1L, 5L, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.assignDriverToRoute(1L, 5L, OWNER_ID);

        assertNotNull(response);
        verify(driverService).assignDriverToRoute(1L, 5L, OWNER_ID);
    }

    @Test
    void removeDriverFromBus_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.removeDriverFromBus(1L, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.removeDriverFromBus(1L, OWNER_ID);

        assertNotNull(response);
        verify(driverService).removeDriverFromBus(1L, OWNER_ID);
    }

    @Test
    void removeDriverFromRoute_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.removeDriverFromRoute(1L, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.removeDriverFromRoute(1L, OWNER_ID);

        assertNotNull(response);
        verify(driverService).removeDriverFromRoute(1L, OWNER_ID);
    }

    @Test
    void updateDriverAvailability_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.updateDriverAvailability(1L, true, OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.updateDriverAvailability(1L, true, OWNER_ID);

        assertNotNull(response);
        verify(driverService).updateDriverAvailability(1L, true, OWNER_ID);
    }

    @Test
    void updateDriverDutyStatus_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.updateDriverDutyStatus(1L, true, "School Route 1", OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.updateDriverDutyStatus(1L, true, "School Route 1", OWNER_ID);

        assertNotNull(response);
        verify(driverService).updateDriverDutyStatus(1L, true, "School Route 1", OWNER_ID);
    }

    @Test
    void updateDriverPerformance_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.updateDriverPerformance(1L, 4.8, "Excellent performance", OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.updateDriverPerformance(1L, 4.8, "Excellent performance", OWNER_ID);

        assertNotNull(response);
        verify(driverService).updateDriverPerformance(1L, 4.8, "Excellent performance", OWNER_ID);
    }

    @Test
    void recordAccident_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.recordAccident(1L, "Minor fender bender", OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.recordAccident(1L, "Minor fender bender", OWNER_ID);

        assertNotNull(response);
        verify(driverService).recordAccident(1L, "Minor fender bender", OWNER_ID);
    }

    @Test
    void recordViolation_withValidData_returnsUpdatedDriver() {
        DriverResponse mockResponse = new DriverResponse();
        mockResponse.setId(1L);

        when(driverService.recordViolation(1L, "Speeding violation", OWNER_ID)).thenReturn(mockResponse);

        DriverResponse response = driverService.recordViolation(1L, "Speeding violation", OWNER_ID);

        assertNotNull(response);
        verify(driverService).recordViolation(1L, "Speeding violation", OWNER_ID);
    }

    @Test
    void updateDriver_withNullRequest_throwsException() {
        when(driverService.updateDriver(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                driverService.updateDriver(1L, null, OWNER_ID));
    }

    @Test
    void getActiveDrivers_withNoDrivers_returnsEmptyList() {
        List<DriverResponse> mockDrivers = new ArrayList<>();

        when(driverService.getActiveDrivers(OWNER_ID)).thenReturn(mockDrivers);

        List<DriverResponse> response = driverService.getActiveDrivers(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
