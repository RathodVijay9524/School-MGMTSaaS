package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RouteStop entity
 */
@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    // Find stops by route
    List<RouteStop> findByRouteIdAndIsDeletedFalseOrderByStopSequenceAsc(Long routeId);
    
    // Find active stops by route
    List<RouteStop> findByRouteIdAndIsActiveTrueAndIsDeletedFalseOrderByStopSequenceAsc(Long routeId);
    
    // Find stops by owner
    List<RouteStop> findByOwnerIdAndIsDeletedFalseOrderByStopNameAsc(Long ownerId);
    
    // Find stop by stop code and route
    Optional<RouteStop> findByStopCodeAndRouteIdAndIsDeletedFalse(String stopCode, Long routeId);
    
    // Find pickup points by route
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.isPickupPoint = true AND rs.isActive = true AND rs.isDeleted = false ORDER BY rs.stopSequence ASC")
    List<RouteStop> findPickupPointsByRoute(@Param("routeId") Long routeId);
    
    // Find drop points by route
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.isDropPoint = true AND rs.isActive = true AND rs.isDeleted = false ORDER BY rs.stopSequence ASC")
    List<RouteStop> findDropPointsByRoute(@Param("routeId") Long routeId);
    
    // Find return pickup points by route
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.isReturnPickupPoint = true AND rs.isActive = true AND rs.isDeleted = false ORDER BY rs.stopSequence ASC")
    List<RouteStop> findReturnPickupPointsByRoute(@Param("routeId") Long routeId);
    
    // Find return drop points by route
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.isReturnDropPoint = true AND rs.isActive = true AND rs.isDeleted = false ORDER BY rs.stopSequence ASC")
    List<RouteStop> findReturnDropPointsByRoute(@Param("routeId") Long routeId);
    
    // Find stops by location (within radius)
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.owner.id = :ownerId AND " +
           "rs.latitude IS NOT NULL AND rs.longitude IS NOT NULL AND " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(rs.latitude)) * " +
           "cos(radians(rs.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(rs.latitude))) <= :radiusKm AND rs.isDeleted = false")
    List<RouteStop> findStopsNearLocation(@Param("ownerId") Long ownerId, @Param("latitude") Double latitude, 
                                        @Param("longitude") Double longitude, @Param("radiusKm") Double radiusKm);
    
    // Find stops by area/city
    List<RouteStop> findByOwnerIdAndAddressContainingIgnoreCaseAndIsDeletedFalseOrderByStopNameAsc(Long ownerId, String area);
    
    // Find stops with facilities
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.owner.id = :ownerId AND " +
           "(rs.hasShelter = true OR rs.hasLighting = true OR rs.parkingAvailable = true) AND rs.isDeleted = false")
    List<RouteStop> findStopsWithFacilities(@Param("ownerId") Long ownerId);
    
    // Find safe stops only
    List<RouteStop> findByOwnerIdAndIsSafeLocationTrueAndIsDeletedFalseOrderByStopNameAsc(Long ownerId);
    
    // Find stops with coordinates
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.owner.id = :ownerId AND rs.latitude IS NOT NULL AND rs.longitude IS NOT NULL AND rs.isDeleted = false")
    List<RouteStop> findStopsWithCoordinates(@Param("ownerId") Long ownerId);
    
    // Find stops without coordinates
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.owner.id = :ownerId AND (rs.latitude IS NULL OR rs.longitude IS NULL) AND rs.isDeleted = false")
    List<RouteStop> findStopsWithoutCoordinates(@Param("ownerId") Long ownerId);
    
    // Count stops by route
    long countByRouteIdAndIsDeletedFalse(Long routeId);
    
    // Count active stops by route
    long countByRouteIdAndIsActiveTrueAndIsDeletedFalse(Long routeId);
    
    // Count stops by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count pickup points by route
    @Query("SELECT COUNT(rs) FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.isPickupPoint = true AND rs.isDeleted = false")
    long countPickupPointsByRoute(@Param("routeId") Long routeId);
    
    // Count drop points by route
    @Query("SELECT COUNT(rs) FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.isDropPoint = true AND rs.isDeleted = false")
    long countDropPointsByRoute(@Param("routeId") Long routeId);
    
    // Get next stop sequence number for route
    @Query("SELECT COALESCE(MAX(rs.stopSequence), 0) + 1 FROM RouteStop rs WHERE rs.route.id = :routeId AND rs.isDeleted = false")
    Integer getNextStopSequence(@Param("routeId") Long routeId);
    
    // Search stops by keyword
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.owner.id = :ownerId AND " +
           "(LOWER(rs.stopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(rs.stopCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(rs.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(rs.landmarks) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND rs.isDeleted = false")
    List<RouteStop> searchStops(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);
    
    // Find stops by distance from school
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.owner.id = :ownerId AND rs.distanceFromSchool BETWEEN :minDistance AND :maxDistance AND rs.isDeleted = false ORDER BY rs.distanceFromSchool ASC")
    List<RouteStop> findStopsByDistanceRange(@Param("ownerId") Long ownerId, @Param("minDistance") Double minDistance, @Param("maxDistance") Double maxDistance);
    
    // Check if stop code exists for route
    boolean existsByStopCodeAndRouteIdAndIsDeletedFalse(String stopCode, Long routeId);
    
    // Soft delete by owner
    @Query("UPDATE RouteStop rs SET rs.isDeleted = true WHERE rs.id = :id AND rs.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Find stops by route and type
    @Query("SELECT rs FROM RouteStop rs WHERE rs.route.id = :routeId AND " +
           "(:isPickup IS NULL OR rs.isPickupPoint = :isPickup) AND " +
           "(:isDrop IS NULL OR rs.isDropPoint = :isDrop) AND " +
           "rs.isDeleted = false ORDER BY rs.stopSequence ASC")
    List<RouteStop> findStopsByType(@Param("routeId") Long routeId, @Param("isPickup") Boolean isPickup, @Param("isDrop") Boolean isDrop);
}
