package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.TransferCertificate;
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
public interface TransferCertificateRepository extends JpaRepository<TransferCertificate, Long> {

    Optional<TransferCertificate> findByTcNumber(String tcNumber);
    
    Optional<TransferCertificate> findByStudent_Id(Long studentId);
    
    boolean existsByStudent_Id(Long studentId);
    
    boolean existsByTcNumber(String tcNumber);
    
    // Find by status
    Page<TransferCertificate> findByStatusAndIsDeletedFalse(TransferCertificate.TCStatus status, Pageable pageable);
    
    // Find by issue date range
    List<TransferCertificate> findByIssueDateBetweenAndIsDeletedFalse(LocalDate startDate, LocalDate endDate);
    
    // Find by reason for leaving
    List<TransferCertificate> findByReasonForLeavingAndIsDeletedFalse(TransferCertificate.ReasonForLeaving reason);
    
    // Find pending approvals
    @Query("SELECT tc FROM TransferCertificate tc WHERE tc.status = 'PENDING_APPROVAL' AND tc.isDeleted = false")
    List<TransferCertificate> findPendingApprovals();
    
    // Find issued TCs
    @Query("SELECT tc FROM TransferCertificate tc WHERE tc.status = 'ISSUED' AND tc.isDeleted = false")
    Page<TransferCertificate> findIssuedTCs(Pageable pageable);
    
    // Find by academic year
    List<TransferCertificate> findByAcademicYearOfLeavingAndIsDeletedFalse(String academicYear);
}

