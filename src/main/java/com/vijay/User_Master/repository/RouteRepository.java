package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Route entity
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    // Find routes by owner
    Page<Route> findByOwnerIdAndIsDeletedFalseOrderByRouteNameAsc(Long ownerId, Pageable pageable);
    
    // Find active routes by owner
    List<Route> findByOwnerIdAndIsActiveTrueAndIsOperationalTrueAndIsDeletedFalseOrderByRouteNameAsc(Long ownerId);
    
    // Find routes by bus
    List<Route> findByBusIdAndIsDeletedFalseOrderByRouteNameAsc(Long busId);
    
    // Find route by route code
    Optional<Route> findByRouteCodeAndOwnerIdAndIsDeletedFalse(String routeCode, Long ownerId);
    
    // Find routes with available seats
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND r.isActive = true AND r.isOperational = true AND r.availableSeats > 0 AND r.isDeleted = false ORDER BY r.availableSeats DESC")
    List<Route> findRoutesWithAvailableSeats(@Param("ownerId") Long ownerId);
    
    // Find routes by start location
    List<Route> findByOwnerIdAndStartLocationContainingIgnoreCaseAndIsDeletedFalseOrderByRouteNameAsc(Long ownerId, String startLocation);
    
    // Find routes by end location
    List<Route> findByOwnerIdAndEndLocationContainingIgnoreCaseAndIsDeletedFalseOrderByRouteNameAsc(Long ownerId, String endLocation);
    
    // Find routes by distance range
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND r.totalDistance BETWEEN :minDistance AND :maxDistance AND r.isDeleted = false ORDER BY r.totalDistance ASC")
    List<Route> findRoutesByDistanceRange(@Param("ownerId") Long ownerId, @Param("minDistance") Double minDistance, @Param("maxDistance") Double maxDistance);
    
    // Find routes by fare range
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND r.routeFare BETWEEN :minFare AND :maxFare AND r.isDeleted = false ORDER BY r.routeFare ASC")
    List<Route> findRoutesByFareRange(@Param("ownerId") Long ownerId, @Param("minFare") Double minFare, @Param("maxFare") Double maxFare);
    
    // Find routes by priority
    List<Route> findByOwnerIdAndPriorityAndIsDeletedFalseOrderByRouteNameAsc(Long ownerId, String priority);
    
    // Find routes without bus assignment
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND r.bus IS NULL AND r.isDeleted = false ORDER BY r.routeName ASC")
    List<Route> findRoutesWithoutBus(@Param("ownerId") Long ownerId);
    
    // Find operational routes
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND r.isActive = true AND r.isOperational = true AND r.bus IS NOT NULL AND r.bus.isActive = true AND r.isDeleted = false ORDER BY r.routeName ASC")
    List<Route> findOperationalRoutes(@Param("ownerId") Long ownerId);
    
    // Count routes by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count active routes by owner
    long countByOwnerIdAndIsActiveTrueAndIsOperationalTrueAndIsDeletedFalse(Long ownerId);
    
    // Count routes with available seats
    @Query("SELECT COUNT(r) FROM Route r WHERE r.owner.id = :ownerId AND r.availableSeats > 0 AND r.isDeleted = false")
    long countRoutesWithAvailableSeats(@Param("ownerId") Long ownerId);
    
    // Get route statistics
    @Query("SELECT COUNT(r) as totalRoutes, " +
           "COUNT(CASE WHEN r.isActive = true THEN 1 END) as activeRoutes, " +
           "COUNT(CASE WHEN r.isOperational = true THEN 1 END) as operationalRoutes, " +
           "COUNT(CASE WHEN r.availableSeats > 0 THEN 1 END) as routesWithSeats, " +
           "SUM(r.totalDistance) as totalDistance, " +
           "SUM(r.currentStudents) as totalStudents, " +
           "AVG(r.routeFare) as averageFare " +
           "FROM Route r WHERE r.owner.id = :ownerId AND r.isDeleted = false")
    Object[] getRouteStatistics(@Param("ownerId") Long ownerId);
    
    // Search routes by keyword
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND " +
           "(LOWER(r.routeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.routeCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.startLocation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.endLocation) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND r.isDeleted = false")
    Page<Route> searchRoutes(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Find routes by operational days
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND r.operationalDays LIKE %:day% AND r.isDeleted = false ORDER BY r.routeName ASC")
    List<Route> findRoutesByOperationalDay(@Param("ownerId") Long ownerId, @Param("day") String day);
    
    // Find profitable routes
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND " +
           "(r.routeFare * r.currentStudents) > r.totalCostPerTrip AND r.isDeleted = false ORDER BY " +
           "((r.routeFare * r.currentStudents) - r.totalCostPerTrip) DESC")
    List<Route> findProfitableRoutes(@Param("ownerId") Long ownerId);
    
    // Find loss-making routes
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId AND " +
           "(r.routeFare * r.currentStudents) < r.totalCostPerTrip AND r.isDeleted = false ORDER BY " +
           "(r.totalCostPerTrip - (r.routeFare * r.currentStudents)) DESC")
    List<Route> findLossMakingRoutes(@Param("ownerId") Long ownerId);
    
    // Check if route code exists for owner
    boolean existsByRouteCodeAndOwnerIdAndIsDeletedFalse(String routeCode, Long ownerId);
    
    // Soft delete by owner
    @Query("UPDATE Route r SET r.isDeleted = true WHERE r.id = :id AND r.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Find routes by bus and status
    List<Route> findByBusIdAndIsActiveTrueAndIsOperationalTrueAndIsDeletedFalseOrderByRouteNameAsc(Long busId);
}
