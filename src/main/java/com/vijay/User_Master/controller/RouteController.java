package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.RouteRequest;
import com.vijay.User_Master.dto.RouteResponse;
import com.vijay.User_Master.service.RouteService;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Route management
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private final RouteService routeService;

    /**
     * Create a new route
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createRoute(@RequestBody RouteRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            RouteResponse response = routeService.createRoute(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update an existing route
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody RouteRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            RouteResponse response = routeService.updateRoute(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get route by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getRouteById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            RouteResponse response = routeService.getRouteById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get all routes with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAllRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "routeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<RouteResponse> response = routeService.getAllRoutes(ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get active routes
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getActiveRoutes() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<RouteResponse> response = routeService.getActiveRoutes(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting active routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get operational routes
     */
    @GetMapping("/operational")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getOperationalRoutes() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<RouteResponse> response = routeService.getOperationalRoutes(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting operational routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get routes with available seats
     */
    @GetMapping("/available-seats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getRoutesWithAvailableSeats() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<RouteResponse> response = routeService.getRoutesWithAvailableSeats(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting routes with available seats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get routes by bus
     */
    @GetMapping("/by-bus/{busId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getRoutesByBus(@PathVariable Long busId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<RouteResponse> response = routeService.getRoutesByBus(busId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting routes by bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get route statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getRouteStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = routeService.getRouteStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting route statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Search routes
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> searchRoutes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<RouteResponse> response = routeService.searchRoutes(ownerId, keyword, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete route
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            routeService.deleteRoute(id, ownerId);
            return ResponseEntity.ok("Route deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Check if route code exists
     */
    @GetMapping("/exists/route-code/{routeCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> existsByRouteCode(@PathVariable String routeCode) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            boolean exists = routeService.existsByRouteCode(ownerId, routeCode);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking route code existence: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update operational status
     */
    @PutMapping("/{routeId}/operational-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateOperationalStatus(
            @PathVariable Long routeId,
            @RequestParam boolean isOperational) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            RouteResponse response = routeService.updateOperationalStatus(routeId, isOperational, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating operational status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

