package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.DriverRequest;
import com.vijay.User_Master.dto.DriverResponse;
import com.vijay.User_Master.service.DriverService;
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
 * REST Controller for Driver management
 */
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {

    private final DriverService driverService;

    /**
     * Create a new driver
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createDriver(@RequestBody DriverRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.createDriver(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating driver: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update an existing driver
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody DriverRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.updateDriver(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating driver: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get driver by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getDriverById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.getDriverById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting driver: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get all drivers with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DriverResponse> response = driverService.getAllDrivers(ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get active drivers
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getActiveDrivers() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getActiveDrivers(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting active drivers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get available drivers
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAvailableDrivers() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getAvailableDrivers(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting available drivers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by bus
     */
    @GetMapping("/by-bus/{busId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversByBus(@PathVariable Long busId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversByBus(busId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by route
     */
    @GetMapping("/by-route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversByRoute(@PathVariable Long routeId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversByRoute(routeId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers with expiring licenses
     */
    @GetMapping("/expiring-licenses")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversWithExpiringLicenses() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversWithExpiringLicenses(ownerId, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers with expiring licenses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers with expired licenses
     */
    @GetMapping("/expired-licenses")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversWithExpiredLicenses() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversWithExpiredLicenses(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers with expired licenses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by license type
     */
    @GetMapping("/by-license-type/{licenseType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversByLicenseType(@PathVariable String licenseType) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversByLicenseType(ownerId, licenseType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by license type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by experience range
     */
    @GetMapping("/by-experience")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversByExperienceRange(
            @RequestParam Integer minExperience,
            @RequestParam Integer maxExperience) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversByExperienceRange(ownerId, minExperience, maxExperience);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by experience range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by performance rating
     */
    @GetMapping("/by-performance/{minRating}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversByPerformanceRating(@PathVariable Double minRating) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversByPerformanceRating(ownerId, minRating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by performance rating: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers with accidents
     */
    @GetMapping("/with-accidents")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversWithAccidents() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversWithAccidents(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers with accidents: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers with violations
     */
    @GetMapping("/with-violations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversWithViolations() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversWithViolations(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers with violations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers on duty
     */
    @GetMapping("/on-duty")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversOnDuty() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversOnDuty(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers on duty: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers without bus assignment
     */
    @GetMapping("/without-bus")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversWithoutBus() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversWithoutBus(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers without bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers without route assignment
     */
    @GetMapping("/without-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversWithoutRoute() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversWithoutRoute(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers without route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by salary range
     */
    @GetMapping("/by-salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversBySalaryRange(ownerId, minSalary, maxSalary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by salary range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by age range
     */
    @GetMapping("/by-age")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversByAgeRange(ownerId, minAge, maxAge);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by age range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers by gender
     */
    @GetMapping("/by-gender/{gender}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversByGender(@PathVariable String gender) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversByGender(ownerId, gender);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers by gender: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get drivers needing training
     */
    @GetMapping("/needing-training")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriversNeedingTraining() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getDriversNeedingTraining(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting drivers needing training: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get top performing drivers
     */
    @GetMapping("/top-performing")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getTopPerformingDrivers() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<DriverResponse> response = driverService.getTopPerformingDrivers(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting top performing drivers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Search drivers
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> searchDrivers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DriverResponse> response = driverService.searchDrivers(ownerId, keyword, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching drivers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get driver statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getDriverStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = driverService.getDriverStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting driver statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete driver
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            driverService.deleteDriver(id, ownerId);
            return ResponseEntity.ok("Driver deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting driver: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Restore driver
     */
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> restoreDriver(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            driverService.restoreDriver(id, ownerId);
            return ResponseEntity.ok("Driver restored successfully");
        } catch (Exception e) {
            log.error("Error restoring driver: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Check if employee ID exists
     */
    @GetMapping("/exists/employee-id/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> existsByEmployeeId(@PathVariable String employeeId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            boolean exists = driverService.existsByEmployeeId(ownerId, employeeId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking employee ID existence: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Check if license number exists
     */
    @GetMapping("/exists/license-number/{licenseNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> existsByLicenseNumber(@PathVariable String licenseNumber) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            boolean exists = driverService.existsByLicenseNumber(ownerId, licenseNumber);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking license number existence: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Assign driver to bus
     */
    @PostMapping("/{driverId}/assign-bus/{busId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> assignDriverToBus(@PathVariable Long driverId, @PathVariable Long busId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.assignDriverToBus(driverId, busId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error assigning driver to bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Assign driver to route
     */
    @PostMapping("/{driverId}/assign-route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> assignDriverToRoute(@PathVariable Long driverId, @PathVariable Long routeId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.assignDriverToRoute(driverId, routeId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error assigning driver to route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Remove driver from bus
     */
    @PostMapping("/{driverId}/remove-bus")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> removeDriverFromBus(@PathVariable Long driverId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.removeDriverFromBus(driverId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing driver from bus: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Remove driver from route
     */
    @PostMapping("/{driverId}/remove-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> removeDriverFromRoute(@PathVariable Long driverId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.removeDriverFromRoute(driverId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing driver from route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update driver availability
     */
    @PutMapping("/{driverId}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateDriverAvailability(
            @PathVariable Long driverId,
            @RequestParam boolean isAvailable) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.updateDriverAvailability(driverId, isAvailable, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating driver availability: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update driver duty status
     */
    @PutMapping("/{driverId}/duty-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateDriverDutyStatus(
            @PathVariable Long driverId,
            @RequestParam boolean isOnDuty,
            @RequestParam(required = false) String location) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.updateDriverDutyStatus(driverId, isOnDuty, location, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating driver duty status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update driver performance rating
     */
    @PutMapping("/{driverId}/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateDriverPerformance(
            @PathVariable Long driverId,
            @RequestParam Double performanceRating,
            @RequestParam(required = false) String review) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.updateDriverPerformance(driverId, performanceRating, review, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating driver performance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Record driver accident
     */
    @PostMapping("/{driverId}/record-accident")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> recordAccident(
            @PathVariable Long driverId,
            @RequestParam String accidentDetails) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.recordAccident(driverId, accidentDetails, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error recording driver accident: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Record driver violation
     */
    @PostMapping("/{driverId}/record-violation")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> recordViolation(
            @PathVariable Long driverId,
            @RequestParam String violationDetails) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            DriverResponse response = driverService.recordViolation(driverId, violationDetails, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error recording driver violation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
