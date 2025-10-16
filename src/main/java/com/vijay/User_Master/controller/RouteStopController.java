package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.RouteStopRequest;
import com.vijay.User_Master.dto.RouteStopResponse;
import com.vijay.User_Master.entity.RouteStop;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.RouteStopRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.RouteRepository;
import com.vijay.User_Master.entity.Route;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Route Stop management
 */
@RestController
@RequestMapping("/api/v1/route-stops")
@RequiredArgsConstructor
@Slf4j
public class RouteStopController {

    private final RouteStopRepository routeStopRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;

    /**
     * Create a new route stop
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createRouteStop(@RequestBody RouteStopRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
            
            // Find the route
            Route route = routeRepository.findById(request.getRouteId())
                    .filter(r -> r.getOwner().getId().equals(ownerId) && !r.getIsDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));
            
            RouteStop routeStop = RouteStop.builder()
                    .stopName(request.getStopName())
                    .stopCode(request.getStopCode())
                    .address(request.getAddress())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .pickupTime(request.getPickupTime())
                    .dropTime(request.getDropTime())
                    .returnPickupTime(request.getReturnPickupTime())
                    .returnDropTime(request.getReturnDropTime())
                    .stopSequence(request.getStopSequence())
                    .distanceFromSchool(request.getDistanceFromSchool())
                    .estimatedTravelTimeFromSchool(request.getEstimatedTravelTimeFromSchool())
                    .landmarks(request.getLandmarks())
                    .contactPerson(request.getContactPerson())
                    .contactPhone(request.getContactPhone())
                    .isPickupPoint(request.getIsPickupPoint())
                    .isDropPoint(request.getIsDropPoint())
                    .isReturnPickupPoint(request.getIsReturnPickupPoint())
                    .isReturnDropPoint(request.getIsReturnDropPoint())
                    .isActive(request.getIsActive())
                    .isSafeLocation(request.getIsSafeLocation())
                    .hasShelter(request.getHasShelter())
                    .hasLighting(request.getHasLighting())
                    .parkingAvailable(request.getParkingAvailable())
                    .notes(request.getNotes())
                    .route(route)
                    .owner(owner)
                    .build();
            
            RouteStop savedRouteStop = routeStopRepository.save(routeStop);
            RouteStopResponse response = mapToResponse(savedRouteStop);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating route stop: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update an existing route stop
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateRouteStop(@PathVariable Long id, @RequestBody RouteStopRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            
            RouteStop routeStop = routeStopRepository.findById(id)
                    .filter(rs -> rs.getOwner().getId().equals(ownerId) && !rs.getIsDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("RouteStop", "id", id));
            
            routeStop.setStopName(request.getStopName());
            routeStop.setStopCode(request.getStopCode());
            routeStop.setAddress(request.getAddress());
            routeStop.setLatitude(request.getLatitude());
            routeStop.setLongitude(request.getLongitude());
            routeStop.setPickupTime(request.getPickupTime());
            routeStop.setDropTime(request.getDropTime());
            routeStop.setReturnPickupTime(request.getReturnPickupTime());
            routeStop.setReturnDropTime(request.getReturnDropTime());
            routeStop.setStopSequence(request.getStopSequence());
            routeStop.setDistanceFromSchool(request.getDistanceFromSchool());
            routeStop.setEstimatedTravelTimeFromSchool(request.getEstimatedTravelTimeFromSchool());
            routeStop.setLandmarks(request.getLandmarks());
            routeStop.setContactPerson(request.getContactPerson());
            routeStop.setContactPhone(request.getContactPhone());
            routeStop.setIsPickupPoint(request.getIsPickupPoint());
            routeStop.setIsDropPoint(request.getIsDropPoint());
            routeStop.setIsReturnPickupPoint(request.getIsReturnPickupPoint());
            routeStop.setIsReturnDropPoint(request.getIsReturnDropPoint());
            routeStop.setIsActive(request.getIsActive());
            routeStop.setIsSafeLocation(request.getIsSafeLocation());
            routeStop.setHasShelter(request.getHasShelter());
            routeStop.setHasLighting(request.getHasLighting());
            routeStop.setParkingAvailable(request.getParkingAvailable());
            routeStop.setNotes(request.getNotes());
            
            RouteStop updatedRouteStop = routeStopRepository.save(routeStop);
            RouteStopResponse response = mapToResponse(updatedRouteStop);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating route stop: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get route stop by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getRouteStopById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            
            RouteStop routeStop = routeStopRepository.findById(id)
                    .filter(rs -> rs.getOwner().getId().equals(ownerId) && !rs.getIsDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("RouteStop", "id", id));
            
            RouteStopResponse response = mapToResponse(routeStop);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting route stop: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get all route stops
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAllRouteStops() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            
            List<RouteStop> routeStops = routeStopRepository.findByOwnerIdAndIsDeletedFalseOrderByStopNameAsc(ownerId);
            
            List<RouteStopResponse> responses = routeStops.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error getting route stops: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get active route stops
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getActiveRouteStops() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            
            List<RouteStop> routeStops = routeStopRepository.findByOwnerIdAndIsDeletedFalseOrderByStopNameAsc(ownerId)
                    .stream()
                    .filter(rs -> rs.getIsActive())
                    .collect(Collectors.toList());
            
            List<RouteStopResponse> responses = routeStops.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error getting active route stops: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get route stops by route
     */
    @GetMapping("/by-route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getRouteStopsByRoute(@PathVariable Long routeId) {
        try {
            List<RouteStop> routeStops = routeStopRepository.findByRouteIdAndIsDeletedFalseOrderByStopSequenceAsc(routeId);
            
            List<RouteStopResponse> responses = routeStops.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error getting route stops by route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get route stop statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getRouteStopStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            
            Map<String, Object> stats = new HashMap<>();
            
            Long totalStops = routeStopRepository.countByOwnerIdAndIsDeletedFalse(ownerId);
            Long activeStops = routeStopRepository.findByOwnerIdAndIsDeletedFalseOrderByStopNameAsc(ownerId)
                    .stream()
                    .mapToLong(rs -> rs.getIsActive() ? 1L : 0L)
                    .sum();
            
            stats.put("totalStops", totalStops);
            stats.put("activeStops", activeStops);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting route stop statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete route stop
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRouteStop(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            
            RouteStop routeStop = routeStopRepository.findById(id)
                    .filter(rs -> rs.getOwner().getId().equals(ownerId) && !rs.getIsDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("RouteStop", "id", id));
            
            routeStop.setIsDeleted(true);
            routeStopRepository.save(routeStop);
            
            return ResponseEntity.ok("Route stop deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting route stop: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Map RouteStop entity to RouteStopResponse DTO
     */
    private RouteStopResponse mapToResponse(RouteStop routeStop) {
        return RouteStopResponse.builder()
                .id(routeStop.getId())
                .stopName(routeStop.getStopName())
                .stopCode(routeStop.getStopCode())
                .address(routeStop.getAddress())
                .latitude(routeStop.getLatitude())
                .longitude(routeStop.getLongitude())
                .pickupTime(routeStop.getPickupTime())
                .dropTime(routeStop.getDropTime())
                .returnPickupTime(routeStop.getReturnPickupTime())
                .returnDropTime(routeStop.getReturnDropTime())
                .stopSequence(routeStop.getStopSequence())
                .distanceFromSchool(routeStop.getDistanceFromSchool())
                .estimatedTravelTimeFromSchool(routeStop.getEstimatedTravelTimeFromSchool())
                .landmarks(routeStop.getLandmarks())
                .contactPerson(routeStop.getContactPerson())
                .contactPhone(routeStop.getContactPhone())
                .isPickupPoint(routeStop.getIsPickupPoint())
                .isDropPoint(routeStop.getIsDropPoint())
                .isReturnPickupPoint(routeStop.getIsReturnPickupPoint())
                .isReturnDropPoint(routeStop.getIsReturnDropPoint())
                .isActive(routeStop.getIsActive())
                .isSafeLocation(routeStop.getIsSafeLocation())
                .hasShelter(routeStop.getHasShelter())
                .hasLighting(routeStop.getHasLighting())
                .parkingAvailable(routeStop.getParkingAvailable())
                .notes(routeStop.getNotes())
                .createdOn(routeStop.getCreatedOn() != null ? new java.sql.Date(routeStop.getCreatedOn().getTime()) : null)
                .updatedOn(routeStop.getUpdatedOn() != null ? new java.sql.Date(routeStop.getUpdatedOn().getTime()) : null)
                .build();
    }
}
