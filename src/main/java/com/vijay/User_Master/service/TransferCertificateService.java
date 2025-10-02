package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.TransferCertificateRequest;
import com.vijay.User_Master.dto.TransferCertificateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransferCertificateService {

    /**
     * Generate TC automatically for a student
     */
    TransferCertificateResponse generateTC(TransferCertificateRequest request);
    
    /**
     * Get TC by ID
     */
    TransferCertificateResponse getTCById(Long id);
    
    /**
     * Get TC by student ID
     */
    TransferCertificateResponse getTCByStudentId(Long studentId);
    
    /**
     * Get TC by TC number
     */
    TransferCertificateResponse getTCByNumber(String tcNumber);
    
    /**
     * Get all TCs with pagination
     */
    Page<TransferCertificateResponse> getAllTCs(Pageable pageable);
    
    /**
     * Get pending approval TCs
     */
    List<TransferCertificateResponse> getPendingApprovals();
    
    /**
     * Approve TC
     */
    TransferCertificateResponse approveTC(Long id, Long approvedByUserId);
    
    /**
     * Issue TC (final step)
     */
    TransferCertificateResponse issueTC(Long id);
    
    /**
     * Generate TC PDF
     */
    String generateTCPDF(Long id);
    
    /**
     * Cancel TC
     */
    void cancelTC(Long id, String reason);
    
    /**
     * Delete TC
     */
    void deleteTC(Long id);
}

