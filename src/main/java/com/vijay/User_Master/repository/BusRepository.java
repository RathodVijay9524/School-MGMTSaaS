package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Bus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Bus entity
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    // Find buses by owner
    Page<Bus> findByOwnerIdAndIsDeletedFalseOrderByBusNumberAsc(Long ownerId, Pageable pageable);
    
    // Find active buses by owner
    List<Bus> findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByBusNumberAsc(Long ownerId);
    
    // Find available buses (not under maintenance and active)
    List<Bus> findByOwnerIdAndIsActiveTrueAndIsAvailableTrueAndIsUnderMaintenanceFalseAndIsDeletedFalseOrderByBusNumberAsc(Long ownerId);
    
    // Find buses by registration number
    Optional<Bus> findByRegistrationNumberAndOwnerIdAndIsDeletedFalse(String registrationNumber, Long ownerId);
    
    // Find buses by bus number
    Optional<Bus> findByBusNumberAndOwnerIdAndIsDeletedFalse(String busNumber, Long ownerId);
    
    // Find buses under maintenance
    List<Bus> findByOwnerIdAndIsUnderMaintenanceTrueAndIsDeletedFalseOrderByBusNumberAsc(Long ownerId);
    
    // Find buses with available seats
    @Query("SELECT b FROM Bus b WHERE b.owner.id = :ownerId AND b.isActive = true AND b.availableSeats > 0 AND b.isDeleted = false ORDER BY b.availableSeats DESC")
    List<Bus> findBusesWithAvailableSeats(@Param("ownerId") Long ownerId);
    
    // Find buses needing service
    @Query("SELECT b FROM Bus b WHERE b.owner.id = :ownerId AND b.nextServiceDate <= CURRENT_DATE AND b.isDeleted = false ORDER BY b.nextServiceDate ASC")
    List<Bus> findBusesNeedingService(@Param("ownerId") Long ownerId);
    
    // Find buses with expired documents
    @Query("SELECT b FROM Bus b WHERE b.owner.id = :ownerId AND " +
           "(b.insuranceExpiryDate <= CURRENT_DATE OR b.fitnessExpiryDate <= CURRENT_DATE OR " +
           "b.pollutionExpiryDate <= CURRENT_DATE OR b.permitExpiryDate <= CURRENT_DATE) AND b.isDeleted = false")
    List<Bus> findBusesWithExpiredDocuments(@Param("ownerId") Long ownerId);
    
    // Find buses by fuel type
    List<Bus> findByOwnerIdAndFuelTypeAndIsActiveTrueAndIsDeletedFalseOrderByBusNumberAsc(Long ownerId, String fuelType);
    
    // Find buses by make
    List<Bus> findByOwnerIdAndMakeAndIsActiveTrueAndIsDeletedFalseOrderByBusNumberAsc(Long ownerId, String make);
    
    // Count buses by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count active buses by owner
    long countByOwnerIdAndIsActiveTrueAndIsDeletedFalse(Long ownerId);
    
    // Count available buses by owner
    long countByOwnerIdAndIsActiveTrueAndIsAvailableTrueAndIsDeletedFalse(Long ownerId);
    
    // Count buses under maintenance by owner
    long countByOwnerIdAndIsUnderMaintenanceTrueAndIsDeletedFalse(Long ownerId);
    
    // Get bus statistics
    @Query("SELECT COUNT(b) as totalBuses, " +
           "COUNT(CASE WHEN b.isActive = true THEN 1 END) as activeBuses, " +
           "COUNT(CASE WHEN b.isUnderMaintenance = true THEN 1 END) as maintenanceBuses, " +
           "COUNT(CASE WHEN b.availableSeats > 0 THEN 1 END) as availableBuses, " +
           "SUM(b.capacity) as totalCapacity, " +
           "SUM(b.currentStudents) as totalStudents, " +
           "AVG(b.currentStudents * 100.0 / b.capacity) as averageOccupancy " +
           "FROM Bus b WHERE b.owner.id = :ownerId AND b.isDeleted = false")
    Object[] getBusStatistics(@Param("ownerId") Long ownerId);
    
    // Search buses by keyword
    @Query("SELECT b FROM Bus b WHERE b.owner.id = :ownerId AND " +
           "(LOWER(b.busNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.busName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.registrationNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.make) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.model) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND b.isDeleted = false")
    Page<Bus> searchBuses(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Find buses by capacity range
    @Query("SELECT b FROM Bus b WHERE b.owner.id = :ownerId AND b.capacity BETWEEN :minCapacity AND :maxCapacity AND b.isDeleted = false ORDER BY b.capacity ASC")
    List<Bus> findBusesByCapacityRange(@Param("ownerId") Long ownerId, @Param("minCapacity") Integer minCapacity, @Param("maxCapacity") Integer maxCapacity);
    
    // Find buses by year of manufacture
    List<Bus> findByOwnerIdAndYearOfManufactureAndIsDeletedFalseOrderByBusNumberAsc(Long ownerId, Integer year);
    
    // Find buses by year range
    @Query("SELECT b FROM Bus b WHERE b.owner.id = :ownerId AND b.yearOfManufacture BETWEEN :startYear AND :endYear AND b.isDeleted = false ORDER BY b.yearOfManufacture DESC")
    List<Bus> findBusesByYearRange(@Param("ownerId") Long ownerId, @Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
    
    // Check if bus number exists for owner
    boolean existsByBusNumberAndOwnerIdAndIsDeletedFalse(String busNumber, Long ownerId);
    
    // Check if registration number exists for owner
    boolean existsByRegistrationNumberAndOwnerIdAndIsDeletedFalse(String registrationNumber, Long ownerId);
    
    // Soft delete by owner
    @Query("UPDATE Bus b SET b.isDeleted = true WHERE b.id = :id AND b.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
}
