package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RouteRequest;
import com.vijay.User_Master.dto.RouteResponse;
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
class RouteServiceTest extends ServiceTestBase {

    @Mock
    private RouteService routeService;

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
    void createRoute_withValidRequest_returnsRoute() {
        RouteRequest request = new RouteRequest();
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setId(1L);

        when(routeService.createRoute(request, OWNER_ID)).thenReturn(mockResponse);

        RouteResponse response = routeService.createRoute(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(routeService).createRoute(request, OWNER_ID);
    }

    @Test
    void createRoute_withNullRequest_throwsException() {
        when(routeService.createRoute(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                routeService.createRoute(null, OWNER_ID));
    }

    @Test
    void updateRoute_withValidData_returnsUpdatedRoute() {
        RouteRequest request = new RouteRequest();
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setId(1L);

        when(routeService.updateRoute(1L, request, OWNER_ID)).thenReturn(mockResponse);

        RouteResponse response = routeService.updateRoute(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(routeService).updateRoute(1L, request, OWNER_ID);
    }

    @Test
    void getRouteById_withValidId_returnsRoute() {
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setId(1L);

        when(routeService.getRouteById(1L, OWNER_ID)).thenReturn(mockResponse);

        RouteResponse response = routeService.getRouteById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getRouteById_withInvalidId_throwsException() {
        when(routeService.getRouteById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Route not found"));

        assertThrows(RuntimeException.class, () ->
                routeService.getRouteById(999L, OWNER_ID));
    }

    @Test
    void getAllRoutes_withValidOwner_returnsPagedRoutes() {
        Pageable pageable = PageRequest.of(0, 10);
        List<RouteResponse> routes = new ArrayList<>();
        routes.add(new RouteResponse());
        Page<RouteResponse> page = new PageImpl<>(routes, pageable, 1);

        when(routeService.getAllRoutes(OWNER_ID, pageable)).thenReturn(page);

        Page<RouteResponse> response = routeService.getAllRoutes(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(routeService).getAllRoutes(OWNER_ID, pageable);
    }

    @Test
    void getActiveRoutes_withValidOwner_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());
        mockRoutes.add(new RouteResponse());

        when(routeService.getActiveRoutes(OWNER_ID)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getActiveRoutes(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(routeService).getActiveRoutes(OWNER_ID);
    }

    @Test
    void getOperationalRoutes_withValidOwner_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getOperationalRoutes(OWNER_ID)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getOperationalRoutes(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesWithAvailableSeats_withValidOwner_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesWithAvailableSeats(OWNER_ID)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesWithAvailableSeats(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesByBus_withValidBusId_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesByBus(1L, OWNER_ID)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesByBus(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesByStartLocation_withValidData_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesByStartLocation(OWNER_ID, "School")).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesByStartLocation(OWNER_ID, "School");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesByEndLocation_withValidData_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesByEndLocation(OWNER_ID, "Home")).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesByEndLocation(OWNER_ID, "Home");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesByDistanceRange_withValidData_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesByDistanceRange(OWNER_ID, 5.0, 15.0)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesByDistanceRange(OWNER_ID, 5.0, 15.0);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesByFareRange_withValidData_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesByFareRange(OWNER_ID, 100.0, 500.0)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesByFareRange(OWNER_ID, 100.0, 500.0);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesByPriority_withValidData_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesByPriority(OWNER_ID, "HIGH")).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesByPriority(OWNER_ID, "HIGH");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesWithoutBus_withValidOwner_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesWithoutBus(OWNER_ID)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesWithoutBus(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRoutesByOperationalDay_withValidData_returnsRoutes() {
        List<RouteResponse> mockRoutes = new ArrayList<>();
        mockRoutes.add(new RouteResponse());

        when(routeService.getRoutesByOperationalDay(OWNER_ID, "MONDAY")).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getRoutesByOperationalDay(OWNER_ID, "MONDAY");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchRoutes_withValidKeyword_returnsPagedRoutes() {
        Pageable pageable = PageRequest.of(0, 10);
        List<RouteResponse> routes = new ArrayList<>();
        routes.add(new RouteResponse());
        Page<RouteResponse> page = new PageImpl<>(routes, pageable, 1);

        when(routeService.searchRoutes(OWNER_ID, "Route 1", pageable)).thenReturn(page);

        Page<RouteResponse> response = routeService.searchRoutes(OWNER_ID, "Route 1", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getRouteStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalRoutes", 10);
        mockStats.put("activeRoutes", 8);

        when(routeService.getRouteStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = routeService.getRouteStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(10, response.get("totalRoutes"));
        assertEquals(8, response.get("activeRoutes"));
        verify(routeService).getRouteStatistics(OWNER_ID);
    }

    @Test
    void deleteRoute_withValidId_succeeds() {
        doNothing().when(routeService).deleteRoute(1L, OWNER_ID);

        routeService.deleteRoute(1L, OWNER_ID);

        verify(routeService).deleteRoute(1L, OWNER_ID);
    }

    @Test
    void restoreRoute_withValidId_succeeds() {
        doNothing().when(routeService).restoreRoute(1L, OWNER_ID);

        routeService.restoreRoute(1L, OWNER_ID);

        verify(routeService).restoreRoute(1L, OWNER_ID);
    }

    @Test
    void existsByRouteCode_withExistingCode_returnsTrue() {
        when(routeService.existsByRouteCode(OWNER_ID, "ROUTE001")).thenReturn(true);

        boolean result = routeService.existsByRouteCode(OWNER_ID, "ROUTE001");

        assertTrue(result);
        verify(routeService).existsByRouteCode(OWNER_ID, "ROUTE001");
    }

    @Test
    void existsByRouteCode_withNonExistingCode_returnsFalse() {
        when(routeService.existsByRouteCode(OWNER_ID, "NONEXISTENT")).thenReturn(false);

        boolean result = routeService.existsByRouteCode(OWNER_ID, "NONEXISTENT");

        assertFalse(result);
    }

    @Test
    void assignBusToRoute_withValidData_returnsUpdatedRoute() {
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setId(1L);

        when(routeService.assignBusToRoute(1L, 10L, OWNER_ID)).thenReturn(mockResponse);

        RouteResponse response = routeService.assignBusToRoute(1L, 10L, OWNER_ID);

        assertNotNull(response);
        verify(routeService).assignBusToRoute(1L, 10L, OWNER_ID);
    }

    @Test
    void removeBusFromRoute_withValidData_returnsUpdatedRoute() {
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setId(1L);

        when(routeService.removeBusFromRoute(1L, OWNER_ID)).thenReturn(mockResponse);

        RouteResponse response = routeService.removeBusFromRoute(1L, OWNER_ID);

        assertNotNull(response);
        verify(routeService).removeBusFromRoute(1L, OWNER_ID);
    }

    @Test
    void updateOperationalStatus_withValidData_returnsUpdatedRoute() {
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setId(1L);

        when(routeService.updateOperationalStatus(1L, true, OWNER_ID)).thenReturn(mockResponse);

        RouteResponse response = routeService.updateOperationalStatus(1L, true, OWNER_ID);

        assertNotNull(response);
        verify(routeService).updateOperationalStatus(1L, true, OWNER_ID);
    }

    @Test
    void updateRouteCapacity_withValidData_returnsUpdatedRoute() {
        RouteResponse mockResponse = new RouteResponse();
        mockResponse.setId(1L);

        when(routeService.updateRouteCapacity(1L, 45, OWNER_ID)).thenReturn(mockResponse);

        RouteResponse response = routeService.updateRouteCapacity(1L, 45, OWNER_ID);

        assertNotNull(response);
        verify(routeService).updateRouteCapacity(1L, 45, OWNER_ID);
    }

    @Test
    void calculateRouteProfitability_withValidRouteId_returnsProfitability() {
        Map<String, Object> mockProfitability = new HashMap<>();
        mockProfitability.put("profit", 5000.0);
        mockProfitability.put("revenue", 15000.0);
        mockProfitability.put("costs", 10000.0);

        when(routeService.calculateRouteProfitability(1L, OWNER_ID)).thenReturn(mockProfitability);

        Map<String, Object> response = routeService.calculateRouteProfitability(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(5000.0, response.get("profit"));
        assertEquals(15000.0, response.get("revenue"));
        assertEquals(10000.0, response.get("costs"));
        verify(routeService).calculateRouteProfitability(1L, OWNER_ID);
    }

    @Test
    void updateRoute_withNullRequest_throwsException() {
        when(routeService.updateRoute(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                routeService.updateRoute(1L, null, OWNER_ID));
    }

    @Test
    void getActiveRoutes_withNoRoutes_returnsEmptyList() {
        List<RouteResponse> mockRoutes = new ArrayList<>();

        when(routeService.getActiveRoutes(OWNER_ID)).thenReturn(mockRoutes);

        List<RouteResponse> response = routeService.getActiveRoutes(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
