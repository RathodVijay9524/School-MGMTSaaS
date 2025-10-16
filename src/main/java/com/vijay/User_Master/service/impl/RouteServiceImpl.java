package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.RouteRequest;
import com.vijay.User_Master.dto.RouteResponse;
import com.vijay.User_Master.entity.Route;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.RouteRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.RouteService;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for Route management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    @Override
    public RouteResponse createRoute(RouteRequest request, Long ownerId) {
        log.info("Creating new route for owner: {}", ownerId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        
        Route route = Route.builder()
                .routeName(request.getRouteName())
                .routeCode(request.getRouteCode())
                .description(request.getDescription())
                .startLocation(request.getStartLocation())
                .endLocation(request.getEndLocation())
                .totalDistance(request.getTotalDistance())
                .estimatedTravelTime(request.getEstimatedTravelTime())
                .routeFare(request.getRouteFare())
                .isActive(request.getIsActive())
                .isOperational(request.getIsOperational())
                .notes(request.getNotes())
                .owner(owner)
                .build();
        
        Route savedRoute = routeRepository.save(route);
        log.info("Route created successfully with ID: {}", savedRoute.getId());
        
        return mapToResponse(savedRoute);
    }

    @Override
    public RouteResponse updateRoute(Long id, RouteRequest request, Long ownerId) {
        log.info("Updating route with ID: {} for owner: {}", id, ownerId);
        
        Route route = routeRepository.findById(id)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        
        route.setRouteName(request.getRouteName());
        route.setRouteCode(request.getRouteCode());
        route.setDescription(request.getDescription());
        route.setStartLocation(request.getStartLocation());
        route.setEndLocation(request.getEndLocation());
        route.setTotalDistance(request.getTotalDistance());
        route.setEstimatedTravelTime(request.getEstimatedTravelTime());
        route.setRouteFare(request.getRouteFare());
        route.setIsActive(request.getIsActive());
        route.setIsOperational(request.getIsOperational());
        route.setNotes(request.getNotes());
        
        Route updatedRoute = routeRepository.save(route);
        log.info("Route updated successfully with ID: {}", updatedRoute.getId());
        
        return mapToResponse(updatedRoute);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long id, Long ownerId) {
        log.info("Getting route with ID: {} for owner: {}", id, ownerId);
        
        Route route = routeRepository.findById(id)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        
        return mapToResponse(route);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RouteResponse> getAllRoutes(Long ownerId, Pageable pageable) {
        log.info("Getting all routes for owner: {} with pagination", ownerId);
        
        Page<Route> routes = routeRepository.findByOwnerIdAndIsDeletedFalseOrderByRouteNameAsc(ownerId, pageable);
        
        List<RouteResponse> responses = routes.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, routes.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getActiveRoutes(Long ownerId) {
        log.info("Getting active routes for owner: {}", ownerId);
        
        List<Route> routes = routeRepository.findByOwnerIdAndIsActiveTrueAndIsOperationalTrueAndIsDeletedFalseOrderByRouteNameAsc(ownerId);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getOperationalRoutes(Long ownerId) {
        log.info("Getting operational routes for owner: {}", ownerId);
        
        List<Route> routes = routeRepository.findOperationalRoutes(ownerId);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesWithAvailableSeats(Long ownerId) {
        log.info("Getting routes with available seats for owner: {}", ownerId);
        
        List<Route> routes = routeRepository.findRoutesWithAvailableSeats(ownerId);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByBus(Long busId, Long ownerId) {
        log.info("Getting routes by bus: {} for owner: {}", busId, ownerId);
        
        List<Route> routes = routeRepository.findByBusIdAndIsDeletedFalseOrderByRouteNameAsc(busId);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByStartLocation(Long ownerId, String startLocation) {
        log.info("Getting routes by start location: {} for owner: {}", startLocation, ownerId);
        
        List<Route> routes = routeRepository.findByOwnerIdAndStartLocationContainingIgnoreCaseAndIsDeletedFalseOrderByRouteNameAsc(ownerId, startLocation);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByEndLocation(Long ownerId, String endLocation) {
        log.info("Getting routes by end location: {} for owner: {}", endLocation, ownerId);
        
        List<Route> routes = routeRepository.findByOwnerIdAndEndLocationContainingIgnoreCaseAndIsDeletedFalseOrderByRouteNameAsc(ownerId, endLocation);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByDistanceRange(Long ownerId, Double minDistance, Double maxDistance) {
        log.info("Getting routes by distance range: {}-{} for owner: {}", minDistance, maxDistance, ownerId);
        
        List<Route> routes = routeRepository.findRoutesByDistanceRange(ownerId, minDistance, maxDistance);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByFareRange(Long ownerId, Double minFare, Double maxFare) {
        log.info("Getting routes by fare range: {}-{} for owner: {}", minFare, maxFare, ownerId);
        
        List<Route> routes = routeRepository.findRoutesByFareRange(ownerId, minFare, maxFare);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByPriority(Long ownerId, String priority) {
        log.info("Getting routes by priority: {} for owner: {}", priority, ownerId);
        
        List<Route> routes = routeRepository.findByOwnerIdAndPriorityAndIsDeletedFalseOrderByRouteNameAsc(ownerId, priority);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesWithoutBus(Long ownerId) {
        log.info("Getting routes without bus for owner: {}", ownerId);
        
        List<Route> routes = routeRepository.findRoutesWithoutBus(ownerId);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesByOperationalDay(Long ownerId, String day) {
        log.info("Getting routes by operational day: {} for owner: {}", day, ownerId);
        
        List<Route> routes = routeRepository.findRoutesByOperationalDay(ownerId, day);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getProfitableRoutes(Long ownerId) {
        log.info("Getting profitable routes for owner: {}", ownerId);
        
        List<Route> routes = routeRepository.findProfitableRoutes(ownerId);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getLossMakingRoutes(Long ownerId) {
        log.info("Getting loss-making routes for owner: {}", ownerId);
        
        List<Route> routes = routeRepository.findLossMakingRoutes(ownerId);
        
        return routes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RouteResponse> searchRoutes(Long ownerId, String keyword, Pageable pageable) {
        log.info("Searching routes with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Route> routes = routeRepository.searchRoutes(ownerId, keyword, pageable);
        
        List<RouteResponse> responses = routes.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, routes.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRouteStatistics(Long ownerId) {
        log.info("Getting route statistics for owner: {}", ownerId);
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            Long totalRoutes = routeRepository.countByOwnerIdAndIsDeletedFalse(ownerId);
            Long activeRoutes = routeRepository.countByOwnerIdAndIsActiveTrueAndIsOperationalTrueAndIsDeletedFalse(ownerId);
            // Get operational routes count from the statistics query
            Object[] statsArray = routeRepository.getRouteStatistics(ownerId);
            Long operationalRoutes = statsArray != null && statsArray.length > 2 ? (Long) statsArray[2] : 0L;
            
            stats.put("totalRoutes", totalRoutes);
            stats.put("activeRoutes", activeRoutes);
            stats.put("operationalRoutes", operationalRoutes);
            
            return stats;
        } catch (Exception e) {
            log.error("Error getting route statistics: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public void deleteRoute(Long id, Long ownerId) {
        log.info("Deleting route with ID: {} for owner: {}", id, ownerId);
        
        Route route = routeRepository.findById(id)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        
        route.setIsDeleted(true);
        routeRepository.save(route);
        
        log.info("Route deleted successfully with ID: {}", id);
    }

    @Override
    public void restoreRoute(Long id, Long ownerId) {
        log.info("Restoring route with ID: {} for owner: {}", id, ownerId);
        
        Route route = routeRepository.findById(id)
                .filter(r -> r.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        
        route.setIsDeleted(false);
        routeRepository.save(route);
        
        log.info("Route restored successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRouteCode(Long ownerId, String routeCode) {
        return routeRepository.existsByRouteCodeAndOwnerIdAndIsDeletedFalse(routeCode, ownerId);
    }

    @Override
    public RouteResponse assignBusToRoute(Long routeId, Long busId, Long ownerId) {
        log.info("Assigning bus {} to route {} for owner: {}", busId, routeId, ownerId);
        
        Route route = routeRepository.findById(routeId)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));
        
        // TODO: Implement bus assignment logic when Bus entity is available
        
        return mapToResponse(route);
    }

    @Override
    public RouteResponse removeBusFromRoute(Long routeId, Long ownerId) {
        log.info("Removing bus from route {} for owner: {}", routeId, ownerId);
        
        Route route = routeRepository.findById(routeId)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));
        
        // TODO: Implement bus removal logic when Bus entity is available
        
        return mapToResponse(route);
    }

    @Override
    public RouteResponse updateOperationalStatus(Long routeId, boolean isOperational, Long ownerId) {
        log.info("Updating operational status for route {} to {} for owner: {}", routeId, isOperational, ownerId);
        
        Route route = routeRepository.findById(routeId)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));
        
        route.setIsOperational(isOperational);
        
        Route updatedRoute = routeRepository.save(route);
        
        return mapToResponse(updatedRoute);
    }

    @Override
    public RouteResponse updateRouteCapacity(Long routeId, Integer newCapacity, Long ownerId) {
        log.info("Updating capacity for route {} to {} for owner: {}", routeId, newCapacity, ownerId);
        
        Route route = routeRepository.findById(routeId)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));
        
        route.setMaxStudentsPerTrip(newCapacity);
        
        Route updatedRoute = routeRepository.save(route);
        
        return mapToResponse(updatedRoute);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calculateRouteProfitability(Long routeId, Long ownerId) {
        log.info("Calculating profitability for route {} for owner: {}", routeId, ownerId);
        
        Route route = routeRepository.findById(routeId)
                .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));
        
        Map<String, Object> profitability = new HashMap<>();
        profitability.put("routeId", routeId);
        profitability.put("routeName", route.getRouteName());
        // Calculate revenue and cost
        Double totalRevenue = route.getRouteFare() * (route.getCurrentStudents() != null ? route.getCurrentStudents() : 0);
        Double totalCost = route.getTotalCostPerTrip() != null ? route.getTotalCostPerTrip() : 0.0;
        Double profit = totalRevenue - totalCost;
        profitability.put("totalRevenue", totalRevenue);
        profitability.put("totalCost", totalCost);
        profitability.put("profitability", profit);
        
        return profitability;
    }

    /**
     * Map Route entity to RouteResponse DTO
     */
    private RouteResponse mapToResponse(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .routeName(route.getRouteName())
                .routeCode(route.getRouteCode())
                .startLocation(route.getStartLocation())
                .endLocation(route.getEndLocation())
                .totalDistance(route.getTotalDistance())
                .estimatedTravelTime(route.getEstimatedTravelTime())
                .routeFare(route.getRouteFare())
                .isActive(route.getIsActive())
                .notes(route.getNotes())
                .createdOn(route.getCreatedOn() != null ? new java.sql.Date(route.getCreatedOn().getTime()) : null)
                .updatedOn(route.getUpdatedOn() != null ? new java.sql.Date(route.getUpdatedOn().getTime()) : null)
                .build();
    }
}

