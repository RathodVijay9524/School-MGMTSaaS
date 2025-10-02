package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.FeeRequest;
import com.vijay.User_Master.dto.FeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface FeeService {

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
}

