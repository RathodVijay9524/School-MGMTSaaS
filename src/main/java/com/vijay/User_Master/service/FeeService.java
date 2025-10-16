package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.FeeInstallmentResponse;
import com.vijay.User_Master.dto.FeeRequest;
import com.vijay.User_Master.dto.FeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeeService {

    Page<FeeResponse> getAllFees(Pageable pageable);
    
    FeeResponse createFee(FeeRequest request);
    
    FeeResponse updateFee(Long id, FeeRequest request);
    
    FeeResponse getFeeById(Long id);
    
    Page<FeeResponse> getFeesByStudent(Long studentId, Pageable pageable);
    
    Page<FeeResponse> getFeesByPaymentStatus(String status, Pageable pageable);
    
    List<FeeResponse> getPendingFees(Long studentId);
    
    List<FeeResponse> getOverdueFees();
    
    FeeResponse recordPayment(Long feeId, Double amount, String paymentMethod, String transactionId);
    
    List<FeeResponse> getStudentFeeSummary(Long studentId, String academicYear);
    
    Double calculateTotalFeesCollected();
    
    Double calculateTotalPendingFees();
    
    void deleteFee(Long id);
    
    // ==================== INSTALLMENT METHODS ====================
    
    /**
     * Get all installments for a specific fee
     */
    Page<FeeInstallmentResponse> getFeeInstallments(Long feeId, Pageable pageable);
    
    /**
     * Get a specific installment by ID
     */
    FeeInstallmentResponse getInstallmentById(Long installmentId);
    
    /**
     * Pay a specific installment
     */
    FeeInstallmentResponse payInstallment(Long installmentId, String paymentMethod, String transactionId, String remarks);
    
    /**
     * Get all overdue installments
     */
    Page<FeeInstallmentResponse> getOverdueInstallments(Pageable pageable);
    
    /**
     * Get pending installments for a student
     */
    Page<FeeInstallmentResponse> getStudentPendingInstallments(Long studentId, Pageable pageable);
    
    /**
     * Get installments due within a date range
     */
    Page<FeeInstallmentResponse> getInstallmentsDueInRange(String startDate, String endDate, Pageable pageable);
    
    /**
     * Get next pending installment for a fee
     */
    FeeInstallmentResponse getNextPendingInstallment(Long feeId);
}

