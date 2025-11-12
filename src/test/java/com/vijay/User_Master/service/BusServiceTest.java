package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.BusRequest;
import com.vijay.User_Master.dto.BusResponse;
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
class BusServiceTest extends ServiceTestBase {

    @Mock
    private BusService busService;

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
    void createBus_withValidRequest_returnsBus() {
        BusRequest request = new BusRequest();
        BusResponse mockResponse = new BusResponse();
        mockResponse.setId(1L);

        when(busService.createBus(request, OWNER_ID)).thenReturn(mockResponse);

        BusResponse response = busService.createBus(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(busService).createBus(request, OWNER_ID);
    }

    @Test
    void createBus_withNullRequest_throwsException() {
        when(busService.createBus(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                busService.createBus(null, OWNER_ID));
    }

    @Test
    void updateBus_withValidData_returnsUpdatedBus() {
        BusRequest request = new BusRequest();
        BusResponse mockResponse = new BusResponse();
        mockResponse.setId(1L);

        when(busService.updateBus(1L, request, OWNER_ID)).thenReturn(mockResponse);

        BusResponse response = busService.updateBus(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(busService).updateBus(1L, request, OWNER_ID);
    }

    @Test
    void getBusById_withValidId_returnsBus() {
        BusResponse mockResponse = new BusResponse();
        mockResponse.setId(1L);

        when(busService.getBusById(1L, OWNER_ID)).thenReturn(mockResponse);

        BusResponse response = busService.getBusById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getBusById_withInvalidId_throwsException() {
        when(busService.getBusById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Bus not found"));

        assertThrows(RuntimeException.class, () ->
                busService.getBusById(999L, OWNER_ID));
    }

    @Test
    void getAllBuses_withValidOwner_returnsPagedBuses() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BusResponse> buses = new ArrayList<>();
        buses.add(new BusResponse());
        Page<BusResponse> page = new PageImpl<>(buses, pageable, 1);

        when(busService.getAllBuses(OWNER_ID, pageable)).thenReturn(page);

        Page<BusResponse> response = busService.getAllBuses(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(busService).getAllBuses(OWNER_ID, pageable);
    }

    @Test
    void getActiveBuses_withValidOwner_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());
        mockBuses.add(new BusResponse());

        when(busService.getActiveBuses(OWNER_ID)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getActiveBuses(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(busService).getActiveBuses(OWNER_ID);
    }

    @Test
    void getAvailableBuses_withValidOwner_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getAvailableBuses(OWNER_ID)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getAvailableBuses(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesWithAvailableSeats_withValidOwner_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesWithAvailableSeats(OWNER_ID)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesWithAvailableSeats(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesNeedingService_withValidOwner_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesNeedingService(OWNER_ID)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesNeedingService(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesWithExpiredDocuments_withValidOwner_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesWithExpiredDocuments(OWNER_ID)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesWithExpiredDocuments(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesByFuelType_withValidData_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesByFuelType(OWNER_ID, "DIESEL")).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesByFuelType(OWNER_ID, "DIESEL");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesByMake_withValidData_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesByMake(OWNER_ID, "TATA")).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesByMake(OWNER_ID, "TATA");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesByCapacityRange_withValidData_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesByCapacityRange(OWNER_ID, 30, 50)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesByCapacityRange(OWNER_ID, 30, 50);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesByYearRange_withValidData_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesByYearRange(OWNER_ID, 2020, 2023)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesByYearRange(OWNER_ID, 2020, 2023);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBusesUnderMaintenance_withValidOwner_returnsBuses() {
        List<BusResponse> mockBuses = new ArrayList<>();
        mockBuses.add(new BusResponse());

        when(busService.getBusesUnderMaintenance(OWNER_ID)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getBusesUnderMaintenance(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchBuses_withValidKeyword_returnsPagedBuses() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BusResponse> buses = new ArrayList<>();
        buses.add(new BusResponse());
        Page<BusResponse> page = new PageImpl<>(buses, pageable, 1);

        when(busService.searchBuses(OWNER_ID, "TATA", pageable)).thenReturn(page);

        Page<BusResponse> response = busService.searchBuses(OWNER_ID, "TATA", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getBusStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalBuses", 10);
        mockStats.put("activeBuses", 8);

        when(busService.getBusStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = busService.getBusStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(10, response.get("totalBuses"));
        assertEquals(8, response.get("activeBuses"));
        verify(busService).getBusStatistics(OWNER_ID);
    }

    @Test
    void deleteBus_withValidId_succeeds() {
        doNothing().when(busService).deleteBus(1L, OWNER_ID);

        busService.deleteBus(1L, OWNER_ID);

        verify(busService).deleteBus(1L, OWNER_ID);
    }

    @Test
    void restoreBus_withValidId_succeeds() {
        doNothing().when(busService).restoreBus(1L, OWNER_ID);

        busService.restoreBus(1L, OWNER_ID);

        verify(busService).restoreBus(1L, OWNER_ID);
    }

    @Test
    void existsByBusNumber_withExistingNumber_returnsTrue() {
        when(busService.existsByBusNumber(OWNER_ID, "MH12AB1234")).thenReturn(true);

        boolean result = busService.existsByBusNumber(OWNER_ID, "MH12AB1234");

        assertTrue(result);
        verify(busService).existsByBusNumber(OWNER_ID, "MH12AB1234");
    }

    @Test
    void existsByBusNumber_withNonExistingNumber_returnsFalse() {
        when(busService.existsByBusNumber(OWNER_ID, "NONEXISTENT")).thenReturn(false);

        boolean result = busService.existsByBusNumber(OWNER_ID, "NONEXISTENT");

        assertFalse(result);
    }

    @Test
    void existsByRegistrationNumber_withExistingNumber_returnsTrue() {
        when(busService.existsByRegistrationNumber(OWNER_ID, "REG123456")).thenReturn(true);

        boolean result = busService.existsByRegistrationNumber(OWNER_ID, "REG123456");

        assertTrue(result);
        verify(busService).existsByRegistrationNumber(OWNER_ID, "REG123456");
    }

    @Test
    void assignBusToRoute_withValidData_returnsUpdatedBus() {
        BusResponse mockResponse = new BusResponse();
        mockResponse.setId(1L);

        when(busService.assignBusToRoute(1L, 10L, OWNER_ID)).thenReturn(mockResponse);

        BusResponse response = busService.assignBusToRoute(1L, 10L, OWNER_ID);

        assertNotNull(response);
        verify(busService).assignBusToRoute(1L, 10L, OWNER_ID);
    }

    @Test
    void removeBusFromRoute_withValidData_returnsUpdatedBus() {
        BusResponse mockResponse = new BusResponse();
        mockResponse.setId(1L);

        when(busService.removeBusFromRoute(1L, OWNER_ID)).thenReturn(mockResponse);

        BusResponse response = busService.removeBusFromRoute(1L, OWNER_ID);

        assertNotNull(response);
        verify(busService).removeBusFromRoute(1L, OWNER_ID);
    }

    @Test
    void updateMaintenanceStatus_withValidData_returnsUpdatedBus() {
        BusResponse mockResponse = new BusResponse();
        mockResponse.setId(1L);

        when(busService.updateMaintenanceStatus(1L, true, "Regular service", OWNER_ID)).thenReturn(mockResponse);

        BusResponse response = busService.updateMaintenanceStatus(1L, true, "Regular service", OWNER_ID);

        assertNotNull(response);
        verify(busService).updateMaintenanceStatus(1L, true, "Regular service", OWNER_ID);
    }

    @Test
    void updateBusCapacity_withValidData_returnsUpdatedBus() {
        BusResponse mockResponse = new BusResponse();
        mockResponse.setId(1L);

        when(busService.updateBusCapacity(1L, 45, OWNER_ID)).thenReturn(mockResponse);

        BusResponse response = busService.updateBusCapacity(1L, 45, OWNER_ID);

        assertNotNull(response);
        verify(busService).updateBusCapacity(1L, 45, OWNER_ID);
    }

    @Test
    void updateBus_withNullRequest_throwsException() {
        when(busService.updateBus(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                busService.updateBus(1L, null, OWNER_ID));
    }

    @Test
    void getActiveBuses_withNoBuses_returnsEmptyList() {
        List<BusResponse> mockBuses = new ArrayList<>();

        when(busService.getActiveBuses(OWNER_ID)).thenReturn(mockBuses);

        List<BusResponse> response = busService.getActiveBuses(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
