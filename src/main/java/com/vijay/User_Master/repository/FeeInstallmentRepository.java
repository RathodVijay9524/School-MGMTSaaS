package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.FeeInstallment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FeeInstallment entity
 */
@Repository
public interface FeeInstallmentRepository extends JpaRepository<FeeInstallment, Long> {

    // Find by ID with owner check (Multi-tenant safe)
    Optional<FeeInstallment> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
    
    // Find all installments for a fee (Multi-tenant safe)
    List<FeeInstallment> findByFee_IdAndOwner_IdAndIsDeletedFalseOrderByInstallmentNumberAsc(Long feeId, Long ownerId);
    
    // Find installments by status
    List<FeeInstallment> findByFee_IdAndStatusAndOwner_IdAndIsDeletedFalse(
        Long feeId, FeeInstallment.InstallmentStatus status, Long ownerId
    );
    
    // Find pending installments for a fee
    @Query("SELECT fi FROM FeeInstallment fi WHERE fi.fee.id = :feeId " +
           "AND fi.status = 'PENDING' AND fi.owner.id = :ownerId AND fi.isDeleted = false " +
           "ORDER BY fi.installmentNumber ASC")
    List<FeeInstallment> findPendingInstallmentsByFee(@Param("feeId") Long feeId, @Param("ownerId") Long ownerId);
    
    // Find overdue installments for a fee
    @Query("SELECT fi FROM FeeInstallment fi WHERE fi.fee.id = :feeId " +
           "AND fi.status = 'PENDING' AND fi.dueDate < :currentDate " +
           "AND fi.owner.id = :ownerId AND fi.isDeleted = false " +
           "ORDER BY fi.dueDate ASC")
    List<FeeInstallment> findOverdueInstallmentsByFee(
        @Param("feeId") Long feeId, 
        @Param("currentDate") LocalDate currentDate, 
        @Param("ownerId") Long ownerId
    );
    
    // Find all overdue installments for owner (for dashboard)
    @Query("SELECT fi FROM FeeInstallment fi WHERE fi.status = 'PENDING' " +
           "AND fi.dueDate < :currentDate AND fi.owner.id = :ownerId AND fi.isDeleted = false " +
           "ORDER BY fi.dueDate ASC")
    List<FeeInstallment> findAllOverdueInstallmentsByOwner(
        @Param("currentDate") LocalDate currentDate, 
        @Param("ownerId") Long ownerId
    );
    
    // Find installments due in a date range
    @Query("SELECT fi FROM FeeInstallment fi WHERE fi.dueDate BETWEEN :startDate AND :endDate " +
           "AND fi.owner.id = :ownerId AND fi.isDeleted = false " +
           "ORDER BY fi.dueDate ASC")
    List<FeeInstallment> findInstallmentsDueInRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("ownerId") Long ownerId
    );
    
    // Find installments for a student (via fee.student)
    @Query("SELECT fi FROM FeeInstallment fi WHERE fi.fee.student.id = :studentId " +
           "AND fi.owner.id = :ownerId AND fi.isDeleted = false " +
           "ORDER BY fi.dueDate DESC")
    Page<FeeInstallment> findByStudentId(
        @Param("studentId") Long studentId, 
        @Param("ownerId") Long ownerId,
        Pageable pageable
    );
    
    // Count pending installments for a fee
    @Query("SELECT COUNT(fi) FROM FeeInstallment fi WHERE fi.fee.id = :feeId " +
           "AND fi.status = 'PENDING' AND fi.owner.id = :ownerId AND fi.isDeleted = false")
    long countPendingInstallmentsByFee(@Param("feeId") Long feeId, @Param("ownerId") Long ownerId);
    
    // Count paid installments for a fee
    @Query("SELECT COUNT(fi) FROM FeeInstallment fi WHERE fi.fee.id = :feeId " +
           "AND fi.status = 'PAID' AND fi.owner.id = :ownerId AND fi.isDeleted = false")
    long countPaidInstallmentsByFee(@Param("feeId") Long feeId, @Param("ownerId") Long ownerId);
    
    // Sum of paid installment amounts for a fee
    @Query("SELECT COALESCE(SUM(fi.amount), 0.0) FROM FeeInstallment fi WHERE fi.fee.id = :feeId " +
           "AND fi.status = 'PAID' AND fi.owner.id = :ownerId AND fi.isDeleted = false")
    Double sumPaidAmountsByFee(@Param("feeId") Long feeId, @Param("ownerId") Long ownerId);
    
    // Find next due installment for a fee
    @Query("SELECT fi FROM FeeInstallment fi WHERE fi.fee.id = :feeId " +
           "AND fi.status = 'PENDING' AND fi.owner.id = :ownerId AND fi.isDeleted = false " +
           "ORDER BY fi.installmentNumber ASC")
    Optional<FeeInstallment> findNextDueInstallment(@Param("feeId") Long feeId, @Param("ownerId") Long ownerId);
    
    // Delete all installments for a fee (soft delete should be handled at service level)
    void deleteByFee_Id(Long feeId);
}

