package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Hostel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {

    // Find hostels by owner
    List<Hostel> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);

    Page<Hostel> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Pageable pageable);

    // Find hostel by ID and owner
    Optional<Hostel> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);

    // Find hostel by code within owner's scope
    Optional<Hostel> findByHostelCodeAndOwnerIdAndIsDeletedFalse(String hostelCode, Long ownerId);

    // Find active hostels
    List<Hostel> findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByHostelName(Long ownerId);

    // Search hostels by name
    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false " +
           "AND (LOWER(h.hostelName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(h.hostelCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Hostel> searchHostels(@Param("ownerId") Long ownerId, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Find hostels with available beds
    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false " +
           "AND h.isActive = true AND h.availableBeds > 0")
    List<Hostel> findHostelsWithAvailableBeds(@Param("ownerId") Long ownerId);

    // Get hostel statistics
    @Query("SELECT COUNT(h) FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false")
    Long countHostelsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(h) FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false AND h.isActive = true")
    Long countActiveHostelsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(h.totalBeds) FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false")
    Long sumTotalBedsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(h.occupiedBeds) FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false")
    Long sumOccupiedBedsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(h.availableBeds) FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false")
    Long sumAvailableBedsByOwner(@Param("ownerId") Long ownerId);

    // Find hostels by amenities
    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false " +
           "AND h.isActive = true AND (h.messAvailable = :messAvailable OR h.wifiAvailable = :wifiAvailable " +
           "OR h.laundryAvailable = :laundryAvailable OR h.gymAvailable = :gymAvailable)")
    List<Hostel> findHostelsByAmenities(@Param("ownerId") Long ownerId, 
                                       @Param("messAvailable") Boolean messAvailable,
                                       @Param("wifiAvailable") Boolean wifiAvailable,
                                       @Param("laundryAvailable") Boolean laundryAvailable,
                                       @Param("gymAvailable") Boolean gymAvailable);

    // Get occupancy statistics
    @Query("SELECT h.hostelName, h.occupiedBeds, h.totalBeds, " +
           "CASE WHEN h.totalBeds > 0 THEN (h.occupiedBeds * 100.0 / h.totalBeds) ELSE 0 END " +
           "FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false")
    List<Object[]> getOccupancyStatistics(@Param("ownerId") Long ownerId);

    // Find hostels by fee range
    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false " +
           "AND h.isActive = true AND h.feesPerMonth BETWEEN :minFee AND :maxFee")
    List<Hostel> findHostelsByFeeRange(@Param("ownerId") Long ownerId, 
                                      @Param("minFee") Double minFee, 
                                      @Param("maxFee") Double maxFee);

    // Check if hostel code exists for owner
    boolean existsByHostelCodeAndOwnerIdAndIsDeletedFalse(String hostelCode, Long ownerId);

    // Find hostels by warden
    List<Hostel> findByWardenNameContainingIgnoreCaseAndOwnerIdAndIsDeletedFalse(String wardenName, Long ownerId);

    // Get recent hostels
    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId AND h.isDeleted = false " +
           "ORDER BY h.createdOn DESC")
    List<Hostel> findRecentHostels(@Param("ownerId") Long ownerId, Pageable pageable);
}
