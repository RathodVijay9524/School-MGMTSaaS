package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.BookMaintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository for BookMaintenance entity
 */
@Repository
public interface BookMaintenanceRepository extends JpaRepository<BookMaintenance, Long> {

    // Find maintenance records for a specific book
    Page<BookMaintenance> findByBook_IdAndOwner_IdAndIsDeletedFalse(
        Long bookId, Long ownerId, Pageable pageable);

    // Find by maintenance type
    Page<BookMaintenance> findByMaintenanceTypeAndOwner_IdAndIsDeletedFalse(
        BookMaintenance.MaintenanceType type, Long ownerId, Pageable pageable);

    // Find by status
    Page<BookMaintenance> findByStatusAndOwner_IdAndIsDeletedFalse(
        BookMaintenance.MaintenanceStatus status, Long ownerId, Pageable pageable);

    // Find overdue maintenance (status not completed/cancelled and past estimated completion)
    @Query("SELECT bm FROM BookMaintenance bm WHERE bm.owner.id = :ownerId " +
           "AND bm.isDeleted = false " +
           "AND bm.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "AND FUNCTION('DATE_ADD', bm.maintenanceDate, bm.estimatedDays) < CURRENT_DATE")
    Page<BookMaintenance> findOverdueMaintenanceByOwner(
        @Param("ownerId") Long ownerId, Pageable pageable);

    // Find maintenance scheduled in a date range
    @Query("SELECT bm FROM BookMaintenance bm WHERE bm.owner.id = :ownerId " +
           "AND bm.isDeleted = false " +
           "AND bm.maintenanceDate BETWEEN :startDate AND :endDate")
    Page<BookMaintenance> findScheduledMaintenanceInRange(
        @Param("ownerId") Long ownerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable);

    // Find all maintenance for owner
    Page<BookMaintenance> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);

    // Find specific maintenance by ID
    Optional<BookMaintenance> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);

    // Count pending maintenance by owner
    @Query("SELECT COUNT(bm) FROM BookMaintenance bm WHERE bm.owner.id = :ownerId " +
           "AND bm.isDeleted = false " +
           "AND bm.status IN ('SCHEDULED', 'IN_PROGRESS', 'PENDING_APPROVAL')")
    Long countPendingMaintenanceByOwner(@Param("ownerId") Long ownerId);

    // Total maintenance cost by owner
    @Query("SELECT COALESCE(SUM(bm.maintenanceCost), 0.0) FROM BookMaintenance bm " +
           "WHERE bm.owner.id = :ownerId " +
           "AND bm.isDeleted = false " +
           "AND bm.status = 'COMPLETED'")
    Double getTotalMaintenanceCostByOwner(@Param("ownerId") Long ownerId);
}

