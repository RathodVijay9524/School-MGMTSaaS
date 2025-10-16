package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Bed;
import com.vijay.User_Master.entity.Bed.BedType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

    // Find beds by room
    List<Bed> findByRoomIdAndIsDeletedFalseOrderByBedNumberAsc(Long roomId);

    Page<Bed> findByRoomIdAndIsDeletedFalseOrderByBedNumberAsc(Long roomId, Pageable pageable);

    // Find bed by ID and isDeleted false
    Optional<Bed> findByIdAndIsDeletedFalse(Long id);

    // Find bed by number within room
    Optional<Bed> findByBedNumberAndRoomIdAndIsDeletedFalse(String bedNumber, Long roomId);

    // Find available beds
    List<Bed> findByRoomIdAndIsAvailableTrueAndIsOccupiedFalseAndIsUnderMaintenanceFalseAndIsActiveTrueAndIsDeletedFalseOrderByBedNumberAsc(Long roomId);

    // Find available beds in hostel
    @Query("SELECT b FROM Bed b WHERE b.room.hostel.id = :hostelId AND b.isDeleted = false " +
           "AND b.isAvailable = true AND b.isOccupied = false AND b.isUnderMaintenance = false " +
           "AND b.isActive = true ORDER BY b.room.roomNumber, b.bedNumber")
    List<Bed> findAvailableBedsInHostel(@Param("hostelId") Long hostelId);

    // Find occupied beds
    List<Bed> findByRoomIdAndIsOccupiedTrueAndIsDeletedFalseOrderByBedNumberAsc(Long roomId);

    // Find beds under maintenance
    List<Bed> findByRoomIdAndIsUnderMaintenanceTrueAndIsDeletedFalseOrderByBedNumberAsc(Long roomId);

    // Find beds by type
    List<Bed> findByRoomIdAndBedTypeAndIsDeletedFalseOrderByBedNumberAsc(Long roomId, BedType bedType);

    // Search beds
    @Query("SELECT b FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false " +
           "AND LOWER(b.bedNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Bed> searchBeds(@Param("roomId") Long roomId, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Get bed statistics
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false")
    Long countBedsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false AND b.isActive = true")
    Long countActiveBedsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false AND b.isOccupied = true")
    Long countOccupiedBedsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false AND b.isAvailable = true")
    Long countAvailableBedsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false AND b.isUnderMaintenance = true")
    Long countBedsUnderMaintenanceByRoom(@Param("roomId") Long roomId);

    // Get bed statistics by hostel
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.hostel.id = :hostelId AND b.isDeleted = false")
    Long countBedsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.hostel.id = :hostelId AND b.isDeleted = false AND b.isOccupied = true")
    Long countOccupiedBedsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.hostel.id = :hostelId AND b.isDeleted = false AND b.isAvailable = true")
    Long countAvailableBedsByHostel(@Param("hostelId") Long hostelId);

    // Get bed utilization by type
    @Query("SELECT b.bedType, COUNT(b), SUM(CASE WHEN b.isOccupied THEN 1 ELSE 0 END), " +
           "CASE WHEN COUNT(b) > 0 THEN (SUM(CASE WHEN b.isOccupied THEN 1 ELSE 0 END) * 100.0 / COUNT(b)) ELSE 0 END " +
           "FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false " +
           "GROUP BY b.bedType")
    List<Object[]> getBedUtilizationByType(@Param("roomId") Long roomId);

    // Find beds needing maintenance
    @Query("SELECT b FROM Bed b WHERE b.room.hostel.owner.id = :ownerId AND b.isDeleted = false " +
           "AND b.nextMaintenanceDate <= :date ORDER BY b.nextMaintenanceDate")
    List<Bed> findBedsNeedingMaintenance(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);

    // Find beds by maintenance status
    @Query("SELECT b FROM Bed b WHERE b.room.hostel.owner.id = :ownerId AND b.isDeleted = false " +
           "AND b.isUnderMaintenance = :underMaintenance ORDER BY b.room.hostel.hostelName, b.room.roomNumber, b.bedNumber")
    List<Bed> findBedsByMaintenanceStatus(@Param("ownerId") Long ownerId, @Param("underMaintenance") Boolean underMaintenance);

    // Find beds by fee range
    @Query("SELECT b FROM Bed b WHERE b.room.hostel.id = :hostelId AND b.isDeleted = false " +
           "AND b.isActive = true AND b.bedFeesPerMonth BETWEEN :minFee AND :maxFee")
    List<Bed> findBedsByFeeRange(@Param("hostelId") Long hostelId,
                                @Param("minFee") Double minFee,
                                @Param("maxFee") Double maxFee);

    // Check if bed number exists in room
    boolean existsByBedNumberAndRoomIdAndIsDeletedFalse(String bedNumber, Long roomId);

    // Find beds by owner (through room and hostel)
    @Query("SELECT b FROM Bed b WHERE b.room.hostel.owner.id = :ownerId AND b.isDeleted = false " +
           "ORDER BY b.room.hostel.hostelName, b.room.roomNumber, b.bedNumber")
    List<Bed> findByOwnerId(@Param("ownerId") Long ownerId);

    // Get bed status summary
    @Query("SELECT b.room.roomNumber, b.bedNumber, b.bedType, b.isOccupied, b.isAvailable, " +
           "b.isUnderMaintenance, b.bedFeesPerMonth " +
           "FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false " +
           "ORDER BY b.bedNumber")
    List<Object[]> getBedStatusSummary(@Param("roomId") Long roomId);

    // Find next available bed in room
    @Query("SELECT b FROM Bed b WHERE b.room.id = :roomId AND b.isDeleted = false " +
           "AND b.isAvailable = true AND b.isOccupied = false AND b.isUnderMaintenance = false " +
           "AND b.isActive = true ORDER BY b.bedNumber LIMIT 1")
    Optional<Bed> findNextAvailableBed(@Param("roomId") Long roomId);

    // Update bed status
    @Query("UPDATE Bed b SET b.isOccupied = :isOccupied, b.isAvailable = :isAvailable " +
           "WHERE b.id = :bedId AND b.owner.id = :ownerId")
    void updateBedStatus(@Param("bedId") Long bedId, @Param("ownerId") Long ownerId, 
                        @Param("isOccupied") Boolean isOccupied, @Param("isAvailable") Boolean isAvailable);
}
