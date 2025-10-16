package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DriverRequest;
import com.vijay.User_Master.dto.DriverResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Service interface for Driver management
 */
public interface DriverService {

    /**
     * Create a new driver
     */
    DriverResponse createDriver(DriverRequest request, Long ownerId);

    /**
     * Update an existing driver
     */
    DriverResponse updateDriver(Long id, DriverRequest request, Long ownerId);

    /**
     * Get driver by ID
     */
    DriverResponse getDriverById(Long id, Long ownerId);

    /**
     * Get all drivers for owner with pagination
     */
    Page<DriverResponse> getAllDrivers(Long ownerId, Pageable pageable);

    /**
     * Get active drivers for owner
     */
    List<DriverResponse> getActiveDrivers(Long ownerId);

    /**
     * Get available drivers
     */
    List<DriverResponse> getAvailableDrivers(Long ownerId);

    /**
     * Get drivers by bus
     */
    List<DriverResponse> getDriversByBus(Long busId, Long ownerId);

    /**
     * Get drivers by route
     */
    List<DriverResponse> getDriversByRoute(Long routeId, Long ownerId);

    /**
     * Get drivers with expiring licenses
     */
    List<DriverResponse> getDriversWithExpiringLicenses(Long ownerId, Date expiryDate);

    /**
     * Get drivers with expired licenses
     */
    List<DriverResponse> getDriversWithExpiredLicenses(Long ownerId);

    /**
     * Get drivers with expiring medical certificates
     */
    List<DriverResponse> getDriversWithExpiringMedicalCertificates(Long ownerId, Date expiryDate);

    /**
     * Get drivers with expired medical certificates
     */
    List<DriverResponse> getDriversWithExpiredMedicalCertificates(Long ownerId);

    /**
     * Get drivers by license type
     */
    List<DriverResponse> getDriversByLicenseType(Long ownerId, String licenseType);

    /**
     * Get drivers by experience range
     */
    List<DriverResponse> getDriversByExperienceRange(Long ownerId, Integer minExperience, Integer maxExperience);

    /**
     * Get drivers by performance rating
     */
    List<DriverResponse> getDriversByPerformanceRating(Long ownerId, Double minRating);

    /**
     * Get drivers with accidents
     */
    List<DriverResponse> getDriversWithAccidents(Long ownerId);

    /**
     * Get drivers with violations
     */
    List<DriverResponse> getDriversWithViolations(Long ownerId);

    /**
     * Get drivers on duty
     */
    List<DriverResponse> getDriversOnDuty(Long ownerId);

    /**
     * Get drivers without bus assignment
     */
    List<DriverResponse> getDriversWithoutBus(Long ownerId);

    /**
     * Get drivers without route assignment
     */
    List<DriverResponse> getDriversWithoutRoute(Long ownerId);

    /**
     * Get drivers by salary range
     */
    List<DriverResponse> getDriversBySalaryRange(Long ownerId, Double minSalary, Double maxSalary);

    /**
     * Get drivers by age range
     */
    List<DriverResponse> getDriversByAgeRange(Long ownerId, Integer minAge, Integer maxAge);

    /**
     * Get drivers by gender
     */
    List<DriverResponse> getDriversByGender(Long ownerId, String gender);

    /**
     * Get drivers needing training
     */
    List<DriverResponse> getDriversNeedingTraining(Long ownerId);

    /**
     * Get top performing drivers
     */
    List<DriverResponse> getTopPerformingDrivers(Long ownerId);

    /**
     * Search drivers by keyword
     */
    Page<DriverResponse> searchDrivers(Long ownerId, String keyword, Pageable pageable);

    /**
     * Get driver statistics
     */
    Map<String, Object> getDriverStatistics(Long ownerId);

    /**
     * Delete driver (soft delete)
     */
    void deleteDriver(Long id, Long ownerId);

    /**
     * Restore driver (undo soft delete)
     */
    void restoreDriver(Long id, Long ownerId);

    /**
     * Check if employee ID exists for owner
     */
    boolean existsByEmployeeId(Long ownerId, String employeeId);

    /**
     * Check if license number exists for owner
     */
    boolean existsByLicenseNumber(Long ownerId, String licenseNumber);

    /**
     * Assign driver to bus
     */
    DriverResponse assignDriverToBus(Long driverId, Long busId, Long ownerId);

    /**
     * Assign driver to route
     */
    DriverResponse assignDriverToRoute(Long driverId, Long routeId, Long ownerId);

    /**
     * Remove driver from bus
     */
    DriverResponse removeDriverFromBus(Long driverId, Long ownerId);

    /**
     * Remove driver from route
     */
    DriverResponse removeDriverFromRoute(Long driverId, Long ownerId);

    /**
     * Update driver availability
     */
    DriverResponse updateDriverAvailability(Long driverId, boolean isAvailable, Long ownerId);

    /**
     * Update driver duty status
     */
    DriverResponse updateDriverDutyStatus(Long driverId, boolean isOnDuty, String location, Long ownerId);

    /**
     * Update driver performance rating
     */
    DriverResponse updateDriverPerformance(Long driverId, Double performanceRating, String review, Long ownerId);

    /**
     * Record driver accident
     */
    DriverResponse recordAccident(Long driverId, String accidentDetails, Long ownerId);

    /**
     * Record driver violation
     */
    DriverResponse recordViolation(Long driverId, String violationDetails, Long ownerId);
}
