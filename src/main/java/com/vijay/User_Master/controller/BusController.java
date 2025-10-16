package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.BusRequest;
import com.vijay.User_Master.dto.BusResponse;
import com.vijay.User_Master.service.BusService;
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
 * REST Controller for Bus management
 */
@RestController
@RequestMapping("/api/v1/buses")
@RequiredArgsConstructor
@Slf4j
public class BusController {

    private final BusService busService;

    /**
     * Create a new bus
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createBus(@RequestBody BusRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            BusResponse response = busService.createBus(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update an existing bus
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateBus(@PathVariable Long id, @RequestBody BusRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            BusResponse response = busService.updateBus(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get bus by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getBusById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            BusResponse response = busService.getBusById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get all buses with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAllBuses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BusResponse> response = busService.getAllBuses(ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get active buses
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getActiveBuses() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getActiveBuses(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting active buses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get available buses
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAvailableBuses() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getAvailableBuses(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting available buses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses with available seats
     */
    @GetMapping("/available-seats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getBusesWithAvailableSeats() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesWithAvailableSeats(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses with available seats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses needing service
     */
    @GetMapping("/needing-service")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusesNeedingService() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesNeedingService(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses needing service: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses with expired documents
     */
    @GetMapping("/expired-documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusesWithExpiredDocuments() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesWithExpiredDocuments(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses with expired documents: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses by fuel type
     */
    @GetMapping("/by-fuel-type/{fuelType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusesByFuelType(@PathVariable String fuelType) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesByFuelType(ownerId, fuelType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses by fuel type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses by make
     */
    @GetMapping("/by-make/{make}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusesByMake(@PathVariable String make) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesByMake(ownerId, make);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses by make: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses by capacity range
     */
    @GetMapping("/by-capacity")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusesByCapacityRange(
            @RequestParam Integer minCapacity,
            @RequestParam Integer maxCapacity) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesByCapacityRange(ownerId, minCapacity, maxCapacity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses by capacity range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses by year range
     */
    @GetMapping("/by-year")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusesByYearRange(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesByYearRange(ownerId, startYear, endYear);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses by year range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get buses under maintenance
     */
    @GetMapping("/under-maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusesUnderMaintenance() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<BusResponse> response = busService.getBusesUnderMaintenance(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting buses under maintenance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Search buses
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> searchBuses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BusResponse> response = busService.searchBuses(ownerId, keyword, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching buses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get bus statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getBusStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = busService.getBusStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting bus statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete bus
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBus(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            busService.deleteBus(id, ownerId);
            return ResponseEntity.ok("Bus deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Restore bus
     */
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> restoreBus(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            busService.restoreBus(id, ownerId);
            return ResponseEntity.ok("Bus restored successfully");
        } catch (Exception e) {
            log.error("Error restoring bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Check if bus number exists
     */
    @GetMapping("/exists/bus-number/{busNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> existsByBusNumber(@PathVariable String busNumber) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            boolean exists = busService.existsByBusNumber(ownerId, busNumber);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking bus number existence: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Check if registration number exists
     */
    @GetMapping("/exists/registration/{registrationNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> existsByRegistrationNumber(@PathVariable String registrationNumber) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            boolean exists = busService.existsByRegistrationNumber(ownerId, registrationNumber);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking registration number existence: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Assign bus to route
     */
    @PostMapping("/{busId}/assign-route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> assignBusToRoute(@PathVariable Long busId, @PathVariable Long routeId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            BusResponse response = busService.assignBusToRoute(busId, routeId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error assigning bus to route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Remove bus from route
     */
    @PostMapping("/{busId}/remove-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> removeBusFromRoute(@PathVariable Long busId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            BusResponse response = busService.removeBusFromRoute(busId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing bus from route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update maintenance status
     */
    @PutMapping("/{busId}/maintenance-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateMaintenanceStatus(
            @PathVariable Long busId,
            @RequestParam boolean isUnderMaintenance,
            @RequestParam(required = false) String reason) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            BusResponse response = busService.updateMaintenanceStatus(busId, isUnderMaintenance, reason, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating maintenance status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update bus capacity
     */
    @PutMapping("/{busId}/capacity")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateBusCapacity(
            @PathVariable Long busId,
            @RequestParam Integer newCapacity) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            BusResponse response = busService.updateBusCapacity(busId, newCapacity, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating bus capacity: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
