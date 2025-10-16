package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RouteRequest;
import com.vijay.User_Master.dto.RouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Route management
 */
public interface RouteService {

    /**
     * Create a new route
     */
    RouteResponse createRoute(RouteRequest request, Long ownerId);

    /**
     * Update an existing route
     */
    RouteResponse updateRoute(Long id, RouteRequest request, Long ownerId);

    /**
     * Get route by ID
     */
    RouteResponse getRouteById(Long id, Long ownerId);

    /**
     * Get all routes for owner with pagination
     */
    Page<RouteResponse> getAllRoutes(Long ownerId, Pageable pageable);

    /**
     * Get active routes for owner
     */
    List<RouteResponse> getActiveRoutes(Long ownerId);

    /**
     * Get operational routes for owner
     */
    List<RouteResponse> getOperationalRoutes(Long ownerId);

    /**
     * Get routes with available seats
     */
    List<RouteResponse> getRoutesWithAvailableSeats(Long ownerId);

    /**
     * Get routes by bus
     */
    List<RouteResponse> getRoutesByBus(Long busId, Long ownerId);

    /**
     * Get routes by start location
     */
    List<RouteResponse> getRoutesByStartLocation(Long ownerId, String startLocation);

    /**
     * Get routes by end location
     */
    List<RouteResponse> getRoutesByEndLocation(Long ownerId, String endLocation);

    /**
     * Get routes by distance range
     */
    List<RouteResponse> getRoutesByDistanceRange(Long ownerId, Double minDistance, Double maxDistance);

    /**
     * Get routes by fare range
     */
    List<RouteResponse> getRoutesByFareRange(Long ownerId, Double minFare, Double maxFare);

    /**
     * Get routes by priority
     */
    List<RouteResponse> getRoutesByPriority(Long ownerId, String priority);

    /**
     * Get routes without bus assignment
     */
    List<RouteResponse> getRoutesWithoutBus(Long ownerId);

    /**
     * Get routes by operational day
     */
    List<RouteResponse> getRoutesByOperationalDay(Long ownerId, String day);

    /**
     * Get profitable routes
     */
    List<RouteResponse> getProfitableRoutes(Long ownerId);

    /**
     * Get loss-making routes
     */
    List<RouteResponse> getLossMakingRoutes(Long ownerId);

    /**
     * Search routes by keyword
     */
    Page<RouteResponse> searchRoutes(Long ownerId, String keyword, Pageable pageable);

    /**
     * Get route statistics
     */
    Map<String, Object> getRouteStatistics(Long ownerId);

    /**
     * Delete route (soft delete)
     */
    void deleteRoute(Long id, Long ownerId);

    /**
     * Restore route (undo soft delete)
     */
    void restoreRoute(Long id, Long ownerId);

    /**
     * Check if route code exists for owner
     */
    boolean existsByRouteCode(Long ownerId, String routeCode);

    /**
     * Assign bus to route
     */
    RouteResponse assignBusToRoute(Long routeId, Long busId, Long ownerId);

    /**
     * Remove bus from route
     */
    RouteResponse removeBusFromRoute(Long routeId, Long ownerId);

    /**
     * Update route operational status
     */
    RouteResponse updateOperationalStatus(Long routeId, boolean isOperational, Long ownerId);

    /**
     * Update route capacity
     */
    RouteResponse updateRouteCapacity(Long routeId, Integer newCapacity, Long ownerId);

    /**
     * Calculate route profitability
     */
    Map<String, Object> calculateRouteProfitability(Long routeId, Long ownerId);
}
