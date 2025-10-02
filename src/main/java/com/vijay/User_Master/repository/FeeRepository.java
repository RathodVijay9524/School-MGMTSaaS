package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Fee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    // Find by student
    Page<Fee> findByStudent_IdAndIsDeletedFalse(Long studentId, Pageable pageable);
    
    // Find by payment status
    Page<Fee> findByPaymentStatusAndIsDeletedFalse(Fee.PaymentStatus status, Pageable pageable);
    
    // Find by fee type
    List<Fee> findByFeeTypeAndIsDeletedFalse(Fee.FeeType feeType);
    
    // Find pending fees
    @Query("SELECT f FROM Fee f WHERE f.student.id = :studentId AND " +
           "f.paymentStatus IN ('PENDING', 'PARTIAL') AND f.isDeleted = false")
    List<Fee> findPendingFees(@Param("studentId") Long studentId);
    
    // Find overdue fees
    @Query("SELECT f FROM Fee f WHERE f.dueDate < :currentDate AND " +
           "f.paymentStatus IN ('PENDING', 'PARTIAL') AND f.isDeleted = false")
    List<Fee> findOverdueFees(@Param("currentDate") LocalDate currentDate);
    
    // Find by academic year
    List<Fee> findByAcademicYearAndIsDeletedFalse(String academicYear);
    
    // Find by payment method
    List<Fee> findByPaymentMethodAndIsDeletedFalse(Fee.PaymentMethod paymentMethod);
    
    // Calculate total fees collected
    @Query("SELECT SUM(f.paidAmount) FROM Fee f WHERE f.paymentStatus = 'PAID' AND f.isDeleted = false")
    Double calculateTotalFeesCollected();
    
    // Calculate total pending fees
    @Query("SELECT SUM(f.balanceAmount) FROM Fee f WHERE f.paymentStatus IN ('PENDING', 'PARTIAL') AND f.isDeleted = false")
    Double calculateTotalPendingFees();
    
    // Find fees by date range
    List<Fee> findByPaymentDateBetweenAndIsDeletedFalse(LocalDate startDate, LocalDate endDate);
    
    // Get student's fee summary
    @Query("SELECT f FROM Fee f WHERE f.student.id = :studentId AND " +
           "f.academicYear = :academicYear AND f.isDeleted = false")
    List<Fee> getStudentFeeSummary(@Param("studentId") Long studentId, @Param("academicYear") String academicYear);
    
    // Multi-tenancy: Find by business owner
    Page<Fee> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    @Query("SELECT f FROM Fee f WHERE f.owner.id = :ownerId AND " +
           "f.paymentStatus IN ('PENDING', 'PARTIAL') AND f.isDeleted = false")
    Page<Fee> findPendingFeesByOwner(@Param("ownerId") Long ownerId, Pageable pageable);
    
    @Query("SELECT SUM(f.paidAmount) FROM Fee f WHERE f.owner.id = :ownerId AND " +
           "f.paymentStatus = 'PAID' AND f.isDeleted = false")
    Double calculateTotalFeesCollectedByOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT SUM(f.balanceAmount) FROM Fee f WHERE f.owner.id = :ownerId AND " +
           "f.paymentStatus IN ('PENDING', 'PARTIAL') AND f.isDeleted = false")
    Double calculateTotalPendingFeesByOwner(@Param("ownerId") Long ownerId);
}

