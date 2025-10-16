package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.HostelResident;
import com.vijay.User_Master.entity.HostelResident.ResidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HostelResidentRepository extends JpaRepository<HostelResident, Long> {

    // Find residents by hostel
    List<HostelResident> findByHostelIdAndIsDeletedFalseOrderByCheckInDateDesc(Long hostelId);

    Page<HostelResident> findByHostelIdAndIsDeletedFalseOrderByCheckInDateDesc(Long hostelId, Pageable pageable);

    // Find resident by ID within owner's scope
    Optional<HostelResident> findByResidentIdAndOwnerIdAndIsDeletedFalse(String residentId, Long ownerId);

    // Find active residents
    List<HostelResident> findByHostelIdAndIsActiveTrueAndStatusAndIsDeletedFalseOrderByCheckInDateDesc(Long hostelId, ResidentStatus status);

    // Find residents by room
    List<HostelResident> findByRoomIdAndIsDeletedFalseOrderByCheckInDateDesc(Long roomId);

    // Find residents by bed
    Optional<HostelResident> findByBedIdAndIsActiveTrueAndIsDeletedFalse(Long bedId);

    // Find residents by student
    List<HostelResident> findByStudentIdAndIsDeletedFalseOrderByCheckInDateDesc(Long studentId);

    // Find current residents (active and checked in)
    @Query("SELECT hr FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE' AND hr.checkOutDate IS NULL")
    List<HostelResident> findCurrentResidents(@Param("hostelId") Long hostelId);

    // Find residents with overdue fees
    @Query("SELECT hr FROM HostelResident hr WHERE hr.owner.id = :ownerId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE' AND hr.checkOutDate IS NULL " +
           "AND hr.nextFeeDueDate < :currentDate AND hr.outstandingFees > 0")
    List<HostelResident> findResidentsWithOverdueFees(@Param("ownerId") Long ownerId, @Param("currentDate") LocalDate currentDate);

    // Find residents by status
    List<HostelResident> findByHostelIdAndStatusAndIsDeletedFalseOrderByCheckInDateDesc(Long hostelId, ResidentStatus status);

    // Search residents
    @Query("SELECT hr FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND (LOWER(hr.student.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(hr.residentId) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<HostelResident> searchResidents(@Param("hostelId") Long hostelId, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Get resident statistics
    @Query("SELECT COUNT(hr) FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false")
    Long countResidentsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT COUNT(hr) FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false AND hr.isActive = true")
    Long countActiveResidentsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT COUNT(hr) FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false AND hr.status = 'ACTIVE'")
    Long countCurrentResidentsByHostel(@Param("hostelId") Long hostelId);

    // Get residents by check-in date range
    @Query("SELECT hr FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.checkInDate BETWEEN :startDate AND :endDate ORDER BY hr.checkInDate DESC")
    List<HostelResident> findResidentsByCheckInDateRange(@Param("hostelId") Long hostelId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // Get residents by check-out date range
    @Query("SELECT hr FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.checkOutDate BETWEEN :startDate AND :endDate ORDER BY hr.checkOutDate DESC")
    List<HostelResident> findResidentsByCheckOutDateRange(@Param("hostelId") Long hostelId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    // Get fee collection statistics
    @Query("SELECT SUM(hr.outstandingFees), SUM(hr.lateFeeCharges), SUM(hr.penaltyCharges) " +
           "FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE'")
    List<Object[]> getFeeCollectionStatistics(@Param("hostelId") Long hostelId);

    // Get residents by services subscribed
    @Query("SELECT hr FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE' " +
           "AND (hr.messSubscribed = :messSubscribed OR hr.laundrySubscribed = :laundrySubscribed " +
           "OR hr.wifiSubscribed = :wifiSubscribed OR hr.gymSubscribed = :gymSubscribed)")
    List<HostelResident> findResidentsByServices(@Param("hostelId") Long hostelId,
                                                @Param("messSubscribed") Boolean messSubscribed,
                                                @Param("laundrySubscribed") Boolean laundrySubscribed,
                                                @Param("wifiSubscribed") Boolean wifiSubscribed,
                                                @Param("gymSubscribed") Boolean gymSubscribed);

    // Get residents due for check-out
    @Query("SELECT hr FROM HostelResident hr WHERE hr.owner.id = :ownerId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE' " +
           "AND hr.expectedCheckOutDate BETWEEN :startDate AND :endDate")
    List<HostelResident> findResidentsDueForCheckOut(@Param("ownerId") Long ownerId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    // Get monthly revenue statistics
    @Query("SELECT SUM(hr.monthlyFees), SUM(hr.messFees), SUM(hr.securityDeposit) " +
           "FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE'")
    List<Object[]> getMonthlyRevenueStatistics(@Param("hostelId") Long hostelId);

    // Get resident demographics
    @Query("SELECT hr.student.name, COUNT(hr) " +
           "FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE' " +
           "GROUP BY hr.student.name")
    List<Object[]> getResidentDemographics(@Param("hostelId") Long hostelId);

    // Find residents by room type
    @Query("SELECT hr FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.status = 'ACTIVE' " +
           "AND hr.room.roomType = :roomType")
    List<HostelResident> findResidentsByRoomType(@Param("hostelId") Long hostelId, @Param("roomType") String roomType);

    // Get residents by owner
    @Query("SELECT hr FROM HostelResident hr WHERE hr.owner.id = :ownerId AND hr.isDeleted = false " +
           "ORDER BY hr.hostel.hostelName, hr.room.roomNumber, hr.bed.bedNumber")
    List<HostelResident> findByOwnerId(@Param("ownerId") Long ownerId);

    // Check if resident ID exists for owner
    boolean existsByResidentIdAndOwnerIdAndIsDeletedFalse(String residentId, Long ownerId);

    // Get recent residents
    @Query("SELECT hr FROM HostelResident hr WHERE hr.owner.id = :ownerId AND hr.isDeleted = false " +
           "ORDER BY hr.createdOn DESC")
    List<HostelResident> findRecentResidents(@Param("ownerId") Long ownerId, Pageable pageable);

    // Get residents with security deposit not paid
    @Query("SELECT hr FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.isActive = true AND hr.securityDepositPaid = false")
    List<HostelResident> findResidentsWithUnpaidSecurityDeposit(@Param("hostelId") Long hostelId);

    // Get average stay duration
    @Query("SELECT AVG(DATEDIFF(hr.checkOutDate, hr.checkInDate)) " +
           "FROM HostelResident hr WHERE hr.hostel.id = :hostelId AND hr.isDeleted = false " +
           "AND hr.checkOutDate IS NOT NULL")
    Double getAverageStayDuration(@Param("hostelId") Long hostelId);
}
