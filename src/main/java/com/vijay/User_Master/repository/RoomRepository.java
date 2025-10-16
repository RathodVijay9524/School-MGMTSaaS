package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Room;
import com.vijay.User_Master.entity.Room.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Find rooms by hostel
    List<Room> findByHostelIdAndIsDeletedFalseOrderByFloorNumberAscRoomNumberAsc(Long hostelId);

    Page<Room> findByHostelIdAndIsDeletedFalseOrderByFloorNumberAscRoomNumberAsc(Long hostelId, Pageable pageable);

    // Find room by ID and isDeleted false
    Optional<Room> findByIdAndIsDeletedFalse(Long id);

    // Find room by number within hostel
    Optional<Room> findByRoomNumberAndHostelIdAndIsDeletedFalse(String roomNumber, Long hostelId);

    // Find available rooms
    List<Room> findByHostelIdAndIsAvailableTrueAndIsActiveTrueAndIsDeletedFalseOrderByRoomNumberAsc(Long hostelId);

    // Find rooms with available beds
    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false " +
           "AND r.isActive = true AND r.availableBeds > 0 ORDER BY r.roomNumber")
    List<Room> findRoomsWithAvailableBeds(@Param("hostelId") Long hostelId);

    // Find rooms by type
    List<Room> findByHostelIdAndRoomTypeAndIsDeletedFalseOrderByRoomNumberAsc(Long hostelId, RoomType roomType);

    // Find rooms by floor
    List<Room> findByHostelIdAndFloorNumberAndIsDeletedFalseOrderByRoomNumberAsc(Long hostelId, Integer floorNumber);

    // Search rooms
    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false " +
           "AND LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Room> searchRooms(@Param("hostelId") Long hostelId, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Get room statistics
    @Query("SELECT COUNT(r) FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false")
    Long countRoomsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false AND r.isActive = true")
    Long countActiveRoomsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT SUM(r.capacity) FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false")
    Long sumCapacityByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT SUM(r.occupiedBeds) FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false")
    Long sumOccupiedBedsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT SUM(r.availableBeds) FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false")
    Long sumAvailableBedsByHostel(@Param("hostelId") Long hostelId);

    // Get occupancy by room type
    @Query("SELECT r.roomType, COUNT(r), SUM(r.occupiedBeds), SUM(r.capacity), " +
           "CASE WHEN SUM(r.capacity) > 0 THEN (SUM(r.occupiedBeds) * 100.0 / SUM(r.capacity)) ELSE 0 END " +
           "FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false " +
           "GROUP BY r.roomType")
    List<Object[]> getOccupancyByRoomType(@Param("hostelId") Long hostelId);

    // Get occupancy by floor
    @Query("SELECT r.floorNumber, COUNT(r), SUM(r.occupiedBeds), SUM(r.capacity), " +
           "CASE WHEN SUM(r.capacity) > 0 THEN (SUM(r.occupiedBeds) * 100.0 / SUM(r.capacity)) ELSE 0 END " +
           "FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false " +
           "GROUP BY r.floorNumber ORDER BY r.floorNumber")
    List<Object[]> getOccupancyByFloor(@Param("hostelId") Long hostelId);

    // Find rooms by amenities
    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false " +
           "AND r.isActive = true AND (r.acAvailable = :acAvailable OR r.attachedBathroom = :attachedBathroom " +
           "OR r.balconyAvailable = :balconyAvailable OR r.furnitureIncluded = :furnitureIncluded)")
    List<Room> findRoomsByAmenities(@Param("hostelId") Long hostelId,
                                   @Param("acAvailable") Boolean acAvailable,
                                   @Param("attachedBathroom") Boolean attachedBathroom,
                                   @Param("balconyAvailable") Boolean balconyAvailable,
                                   @Param("furnitureIncluded") Boolean furnitureIncluded);

    // Find rooms by fee range
    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false " +
           "AND r.isActive = true AND r.roomFeesPerMonth BETWEEN :minFee AND :maxFee")
    List<Room> findRoomsByFeeRange(@Param("hostelId") Long hostelId,
                                  @Param("minFee") Double minFee,
                                  @Param("maxFee") Double maxFee);

    // Check if room number exists in hostel
    boolean existsByRoomNumberAndHostelIdAndIsDeletedFalse(String roomNumber, Long hostelId);

    // Find rooms by owner (through hostel)
    @Query("SELECT r FROM Room r WHERE r.hostel.owner.id = :ownerId AND r.isDeleted = false " +
           "ORDER BY r.hostel.hostelName, r.roomNumber")
    List<Room> findByOwnerId(@Param("ownerId") Long ownerId);

    // Get room utilization statistics
    @Query("SELECT r.roomNumber, r.roomType, r.capacity, r.occupiedBeds, r.availableBeds, " +
           "CASE WHEN r.capacity > 0 THEN (r.occupiedBeds * 100.0 / r.capacity) ELSE 0 END " +
           "FROM Room r WHERE r.hostel.id = :hostelId AND r.isDeleted = false " +
           "ORDER BY r.roomNumber")
    List<Object[]> getRoomUtilizationStatistics(@Param("hostelId") Long hostelId);
}
