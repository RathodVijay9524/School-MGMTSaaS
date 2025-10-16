package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Driver entity
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Find drivers by owner
    Page<Driver> findByOwnerIdAndIsDeletedFalseOrderByFirstNameAsc(Long ownerId, Pageable pageable);
    
    // Find active drivers by owner
    List<Driver> findByOwnerIdAndEmploymentStatusAndIsDeletedFalseOrderByFirstNameAsc(Long ownerId, String employmentStatus);
    
    // Find available drivers
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.employmentStatus = 'ACTIVE' AND d.isAvailable = true AND d.isDeleted = false ORDER BY d.firstName ASC")
    List<Driver> findAvailableDrivers(@Param("ownerId") Long ownerId);
    
    // Find drivers by bus
    List<Driver> findByBusIdAndIsDeletedFalseOrderByFirstNameAsc(Long busId);
    
    // Find drivers by route
    List<Driver> findByRouteIdAndIsDeletedFalseOrderByFirstNameAsc(Long routeId);
    
    // Find driver by employee ID
    Optional<Driver> findByEmployeeIdAndOwnerIdAndIsDeletedFalse(String employeeId, Long ownerId);
    
    // Find driver by license number
    Optional<Driver> findByLicenseNumberAndOwnerIdAndIsDeletedFalse(String licenseNumber, Long ownerId);
    
    // Find drivers with expiring licenses
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.licenseExpiryDate <= :expiryDate AND d.isDeleted = false ORDER BY d.licenseExpiryDate ASC")
    List<Driver> findDriversWithExpiringLicenses(@Param("ownerId") Long ownerId, @Param("expiryDate") Date expiryDate);
    
    // Find drivers with expired licenses
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.licenseExpiryDate < CURRENT_DATE AND d.isDeleted = false ORDER BY d.licenseExpiryDate ASC")
    List<Driver> findDriversWithExpiredLicenses(@Param("ownerId") Long ownerId);
    
    // Find drivers with expiring medical certificates
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.medicalCertificateExpiry <= :expiryDate AND d.isDeleted = false ORDER BY d.medicalCertificateExpiry ASC")
    List<Driver> findDriversWithExpiringMedicalCertificates(@Param("ownerId") Long ownerId, @Param("expiryDate") Date expiryDate);
    
    // Find drivers with expired medical certificates
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.medicalCertificateExpiry < CURRENT_DATE AND d.isDeleted = false ORDER BY d.medicalCertificateExpiry ASC")
    List<Driver> findDriversWithExpiredMedicalCertificates(@Param("ownerId") Long ownerId);
    
    // Find drivers by license type
    List<Driver> findByOwnerIdAndLicenseTypeAndIsDeletedFalseOrderByFirstNameAsc(Long ownerId, String licenseType);
    
    // Find drivers by experience level
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.yearsOfExperience BETWEEN :minExperience AND :maxExperience AND d.isDeleted = false ORDER BY d.yearsOfExperience DESC")
    List<Driver> findDriversByExperienceRange(@Param("ownerId") Long ownerId, @Param("minExperience") Integer minExperience, @Param("maxExperience") Integer maxExperience);
    
    // Find drivers by performance rating
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.performanceRating >= :minRating AND d.isDeleted = false ORDER BY d.performanceRating DESC")
    List<Driver> findDriversByPerformanceRating(@Param("ownerId") Long ownerId, @Param("minRating") Double minRating);
    
    // Find drivers with accidents
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.accidentsCount > 0 AND d.isDeleted = false ORDER BY d.accidentsCount DESC")
    List<Driver> findDriversWithAccidents(@Param("ownerId") Long ownerId);
    
    // Find drivers with violations
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.violationsCount > 0 AND d.isDeleted = false ORDER BY d.violationsCount DESC")
    List<Driver> findDriversWithViolations(@Param("ownerId") Long ownerId);
    
    // Find drivers on duty
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.isOnDuty = true AND d.isDeleted = false ORDER BY d.firstName ASC")
    List<Driver> findDriversOnDuty(@Param("ownerId") Long ownerId);
    
    // Find drivers without bus assignment
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.bus IS NULL AND d.employmentStatus = 'ACTIVE' AND d.isDeleted = false ORDER BY d.firstName ASC")
    List<Driver> findDriversWithoutBus(@Param("ownerId") Long ownerId);
    
    // Find drivers without route assignment
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.route IS NULL AND d.employmentStatus = 'ACTIVE' AND d.isDeleted = false ORDER BY d.firstName ASC")
    List<Driver> findDriversWithoutRoute(@Param("ownerId") Long ownerId);
    
    // Count drivers by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count active drivers by owner
    long countByOwnerIdAndEmploymentStatusAndIsDeletedFalse(Long ownerId, String employmentStatus);
    
    // Count available drivers by owner
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.owner.id = :ownerId AND d.employmentStatus = 'ACTIVE' AND d.isAvailable = true AND d.isDeleted = false")
    long countAvailableDrivers(@Param("ownerId") Long ownerId);
    
    // Count drivers on duty by owner
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.owner.id = :ownerId AND d.isOnDuty = true AND d.isDeleted = false")
    long countDriversOnDuty(@Param("ownerId") Long ownerId);
    
    // Get driver statistics
    @Query("SELECT COUNT(d) as totalDrivers, " +
           "COUNT(CASE WHEN d.employmentStatus = 'ACTIVE' THEN 1 END) as activeDrivers, " +
           "COUNT(CASE WHEN d.isAvailable = true THEN 1 END) as availableDrivers, " +
           "COUNT(CASE WHEN d.isOnDuty = true THEN 1 END) as driversOnDuty, " +
           "COUNT(CASE WHEN d.licenseExpiryDate < CURRENT_DATE THEN 1 END) as expiredLicenses, " +
           "COUNT(CASE WHEN d.medicalCertificateExpiry < CURRENT_DATE THEN 1 END) as expiredMedical, " +
           "AVG(d.performanceRating) as averagePerformance, " +
           "AVG(d.yearsOfExperience) as averageExperience " +
           "FROM Driver d WHERE d.owner.id = :ownerId AND d.isDeleted = false")
    Object[] getDriverStatistics(@Param("ownerId") Long ownerId);
    
    // Search drivers by keyword
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND " +
           "(LOWER(d.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.licenseNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND d.isDeleted = false")
    Page<Driver> searchDrivers(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Find drivers by salary range
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.salary BETWEEN :minSalary AND :maxSalary AND d.isDeleted = false ORDER BY d.salary ASC")
    List<Driver> findDriversBySalaryRange(@Param("ownerId") Long ownerId, @Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary);
    
    // Find drivers by age range
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.age BETWEEN :minAge AND :maxAge AND d.isDeleted = false ORDER BY d.age ASC")
    List<Driver> findDriversByAgeRange(@Param("ownerId") Long ownerId, @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    // Find drivers by gender
    List<Driver> findByOwnerIdAndGenderAndIsDeletedFalseOrderByFirstNameAsc(Long ownerId, String gender);
    
    // Find drivers needing training
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.nextTrainingDue <= CURRENT_DATE AND d.isDeleted = false ORDER BY d.nextTrainingDue ASC")
    List<Driver> findDriversNeedingTraining(@Param("ownerId") Long ownerId);
    
    // Find top performing drivers
    @Query("SELECT d FROM Driver d WHERE d.owner.id = :ownerId AND d.performanceRating >= 4.0 AND d.isDeleted = false ORDER BY d.performanceRating DESC, d.totalTripsCompleted DESC")
    List<Driver> findTopPerformingDrivers(@Param("ownerId") Long ownerId);
    
    // Check if employee ID exists for owner
    boolean existsByEmployeeIdAndOwnerIdAndIsDeletedFalse(String employeeId, Long ownerId);
    
    // Check if license number exists for owner
    boolean existsByLicenseNumberAndOwnerIdAndIsDeletedFalse(String licenseNumber, Long ownerId);
    
    // Soft delete by owner
    @Query("UPDATE Driver d SET d.isDeleted = true WHERE d.id = :id AND d.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Find drivers by bus and status
    List<Driver> findByBusIdAndEmploymentStatusAndIsDeletedFalseOrderByFirstNameAsc(Long busId, String employmentStatus);
    
    // Find drivers by route and status
    List<Driver> findByRouteIdAndEmploymentStatusAndIsDeletedFalseOrderByFirstNameAsc(Long routeId, String employmentStatus);
}
