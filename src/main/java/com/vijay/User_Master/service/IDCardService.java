package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.IDCardRequest;
import com.vijay.User_Master.dto.IDCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IDCardService {

    /**
     * Generate ID card automatically for student
     */
    IDCardResponse generateStudentIDCard(Long studentId, LocalDate expiryDate);
    
    /**
     * Generate ID card automatically for teacher
     */
    IDCardResponse generateTeacherIDCard(Long teacherId, LocalDate expiryDate);
    
    /**
     * Create ID card with custom details
     */
    IDCardResponse createIDCard(IDCardRequest request);
    
    /**
     * Get ID card by ID
     */
    IDCardResponse getIDCardById(Long id);
    
    /**
     * Get ID card by card number
     */
    IDCardResponse getIDCardByNumber(String cardNumber);
    
    /**
     * Get active ID card for student
     */
    IDCardResponse getActiveStudentCard(Long studentId);
    
    /**
     * Get active ID card for teacher
     */
    IDCardResponse getActiveTeacherCard(Long teacherId);
    
    /**
     * Get all ID cards
     */
    Page<IDCardResponse> getAllIDCards(Pageable pageable);
    
    /**
     * Get expired cards
     */
    List<IDCardResponse> getExpiredCards();
    
    /**
     * Get cards expiring soon (within 30 days)
     */
    List<IDCardResponse> getCardsExpiringSoon();
    
    /**
     * Report card as lost
     */
    IDCardResponse reportLost(Long cardId, String reason);
    
    /**
     * Report card as damaged
     */
    IDCardResponse reportDamaged(Long cardId, String reason);
    
    /**
     * Reissue ID card (for lost/damaged)
     */
    IDCardResponse reissueIDCard(Long oldCardId, Double replacementFee);
    
    /**
     * Generate ID card PDF
     */
    String generateIDCardPDF(Long id);
    
    /**
     * Generate QR code for ID card
     */
    String generateQRCode(Long id);
    
    /**
     * Cancel ID card
     */
    void cancelIDCard(Long id);
}

