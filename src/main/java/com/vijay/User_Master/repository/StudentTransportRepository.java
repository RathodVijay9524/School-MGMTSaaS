package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.StudentTransport;
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
 * Repository for StudentTransport entity
 */
@Repository
public interface StudentTransportRepository extends JpaRepository<StudentTransport, Long> {

    // Find student transports by owner
    Page<StudentTransport> findByOwnerIdAndIsDeletedFalseOrderByTransportIdAsc(Long ownerId, Pageable pageable);
    
    // Find student transport by student
    List<StudentTransport> findByStudentIdAndIsDeletedFalseOrderByAssignmentDateDesc(Long studentId);
    
    // Find active student transport by student
    @Query("SELECT st FROM StudentTransport st WHERE st.student.id = :studentId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false")
    Optional<StudentTransport> findActiveTransportByStudent(@Param("studentId") Long studentId);
    
    // Find student transports by bus
    List<StudentTransport> findByBusIdAndIsDeletedFalseOrderByTransportIdAsc(Long busId);
    
    // Find active student transports by bus
    @Query("SELECT st FROM StudentTransport st WHERE st.bus.id = :busId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false ORDER BY st.transportId ASC")
    List<StudentTransport> findActiveTransportsByBus(@Param("busId") Long busId);
    
    // Find student transports by route
    List<StudentTransport> findByRouteIdAndIsDeletedFalseOrderByTransportIdAsc(Long routeId);
    
    // Find active student transports by route
    @Query("SELECT st FROM StudentTransport st WHERE st.route.id = :routeId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false ORDER BY st.transportId ASC")
    List<StudentTransport> findActiveTransportsByRoute(@Param("routeId") Long routeId);
    
    // Find student transports by driver
    List<StudentTransport> findByDriverIdAndIsDeletedFalseOrderByTransportIdAsc(Long driverId);
    
    // Find active student transports by driver
    @Query("SELECT st FROM StudentTransport st WHERE st.driver.id = :driverId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false ORDER BY st.transportId ASC")
    List<StudentTransport> findActiveTransportsByDriver(@Param("driverId") Long driverId);
    
    // Find student transports by pickup stop
    List<StudentTransport> findByPickupStopIdAndIsDeletedFalseOrderByTransportIdAsc(Long pickupStopId);
    
    // Find student transports by drop stop
    List<StudentTransport> findByDropStopIdAndIsDeletedFalseOrderByTransportIdAsc(Long dropStopId);
    
    // Find student transport by transport ID
    Optional<StudentTransport> findByTransportIdAndOwnerIdAndIsDeletedFalse(String transportId, Long ownerId);
    
    // Find student transports with overdue payments
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.nextPaymentDue < CURRENT_DATE AND st.paymentStatus != 'PAID' AND st.isDeleted = false ORDER BY st.nextPaymentDue ASC")
    List<StudentTransport> findTransportsWithOverduePayments(@Param("ownerId") Long ownerId);
    
    // Find student transports by payment status
    List<StudentTransport> findByOwnerIdAndPaymentStatusAndIsDeletedFalseOrderByTransportIdAsc(Long ownerId, String paymentStatus);
    
    // Find student transports by assignment status
    List<StudentTransport> findByOwnerIdAndAssignmentStatusAndIsDeletedFalseOrderByTransportIdAsc(Long ownerId, String assignmentStatus);
    
    // Find active student transports
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false ORDER BY st.transportId ASC")
    List<StudentTransport> findActiveTransports(@Param("ownerId") Long ownerId);
    
    // Find student transports by fare range
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.monthlyFee BETWEEN :minFare AND :maxFare AND st.isDeleted = false ORDER BY st.monthlyFee ASC")
    List<StudentTransport> findTransportsByFareRange(@Param("ownerId") Long ownerId, @Param("minFare") Double minFare, @Param("maxFare") Double maxFare);
    
    // Find student transports by assignment date range
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.assignmentDate BETWEEN :startDate AND :endDate AND st.isDeleted = false ORDER BY st.assignmentDate DESC")
    List<StudentTransport> findTransportsByAssignmentDateRange(@Param("ownerId") Long ownerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Find student transports by effective date range
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND " +
           "((st.effectiveFrom <= :endDate AND (st.effectiveUntil IS NULL OR st.effectiveUntil >= :startDate)) OR " +
           "(st.effectiveFrom <= :endDate AND st.effectiveUntil >= :startDate)) AND st.isDeleted = false ORDER BY st.effectiveFrom ASC")
    List<StudentTransport> findTransportsByEffectiveDateRange(@Param("ownerId") Long ownerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Count student transports by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count active student transports by owner
    @Query("SELECT COUNT(st) FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false")
    long countActiveTransports(@Param("ownerId") Long ownerId);
    
    // Count student transports by bus
    long countByBusIdAndIsDeletedFalse(Long busId);
    
    // Count active student transports by bus
    @Query("SELECT COUNT(st) FROM StudentTransport st WHERE st.bus.id = :busId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false")
    long countActiveTransportsByBus(@Param("busId") Long busId);
    
    // Count student transports by route
    long countByRouteIdAndIsDeletedFalse(Long routeId);
    
    // Count active student transports by route
    @Query("SELECT COUNT(st) FROM StudentTransport st WHERE st.route.id = :routeId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false")
    long countActiveTransportsByRoute(@Param("routeId") Long routeId);
    
    // Count student transports by payment status
    long countByOwnerIdAndPaymentStatusAndIsDeletedFalse(Long ownerId, String paymentStatus);
    
    // Get student transport statistics
    @Query("SELECT COUNT(st) as totalTransports, " +
           "COUNT(CASE WHEN st.isActive = true THEN 1 END) as activeTransports, " +
           "COUNT(CASE WHEN st.paymentStatus = 'PAID' THEN 1 END) as paidTransports, " +
           "COUNT(CASE WHEN st.paymentStatus = 'OVERDUE' THEN 1 END) as overdueTransports, " +
           "COUNT(CASE WHEN st.nextPaymentDue < CURRENT_DATE THEN 1 END) as overduePayments, " +
           "SUM(st.monthlyFee) as totalMonthlyRevenue, " +
           "SUM(st.outstandingAmount) as totalOutstanding, " +
           "AVG(st.attendanceRate) as averageAttendance " +
           "FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.isDeleted = false")
    Object[] getTransportStatistics(@Param("ownerId") Long ownerId);
    
    // Search student transports by keyword
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND " +
           "(LOWER(st.transportId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(st.student.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(st.parentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(st.parentPhone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND st.isDeleted = false")
    Page<StudentTransport> searchTransports(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Find student transports by attendance rate
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.attendanceRate >= :minAttendance AND st.isDeleted = false ORDER BY st.attendanceRate DESC")
    List<StudentTransport> findTransportsByAttendanceRate(@Param("ownerId") Long ownerId, @Param("minAttendance") Double minAttendance);
    
    // Find student transports with low attendance
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.attendanceRate < :threshold AND st.isDeleted = false ORDER BY st.attendanceRate ASC")
    List<StudentTransport> findTransportsWithLowAttendance(@Param("ownerId") Long ownerId, @Param("threshold") Double threshold);
    
    // Find student transports with incidents
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.incidentsCount > 0 AND st.isDeleted = false ORDER BY st.incidentsCount DESC")
    List<StudentTransport> findTransportsWithIncidents(@Param("ownerId") Long ownerId);
    
    // Find student transports with complaints
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND st.complaintsCount > 0 AND st.isDeleted = false ORDER BY st.complaintsCount DESC")
    List<StudentTransport> findTransportsWithComplaints(@Param("ownerId") Long ownerId);
    
    // Find student transports by parent phone
    List<StudentTransport> findByOwnerIdAndParentPhoneAndIsDeletedFalseOrderByTransportIdAsc(Long ownerId, String parentPhone);
    
    // Find student transports by parent email
    List<StudentTransport> findByOwnerIdAndParentEmailAndIsDeletedFalseOrderByTransportIdAsc(Long ownerId, String parentEmail);
    
    // Check if transport ID exists for owner
    boolean existsByTransportIdAndOwnerIdAndIsDeletedFalse(String transportId, Long ownerId);
    
    // Check if student has active transport
    @Query("SELECT COUNT(st) > 0 FROM StudentTransport st WHERE st.student.id = :studentId AND st.isActive = true AND st.assignmentStatus = 'ACTIVE' AND st.isDeleted = false")
    boolean existsActiveTransportByStudent(@Param("studentId") Long studentId);
    
    // Soft delete by owner
    @Query("UPDATE StudentTransport st SET st.isDeleted = true WHERE st.id = :id AND st.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Find student transports by multiple criteria
    @Query("SELECT st FROM StudentTransport st WHERE st.owner.id = :ownerId AND " +
           "(:busId IS NULL OR st.bus.id = :busId) AND " +
           "(:routeId IS NULL OR st.route.id = :routeId) AND " +
           "(:driverId IS NULL OR st.driver.id = :driverId) AND " +
           "(:paymentStatus IS NULL OR st.paymentStatus = :paymentStatus) AND " +
           "(:assignmentStatus IS NULL OR st.assignmentStatus = :assignmentStatus) AND " +
           "st.isDeleted = false ORDER BY st.transportId ASC")
    List<StudentTransport> findTransportsByCriteria(@Param("ownerId") Long ownerId, 
                                                   @Param("busId") Long busId, 
                                                   @Param("routeId") Long routeId, 
                                                   @Param("driverId") Long driverId, 
                                                   @Param("paymentStatus") String paymentStatus, 
                                                   @Param("assignmentStatus") String assignmentStatus);
}
