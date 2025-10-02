package com.vijay.User_Master.service;

import java.time.LocalDate;
import java.util.List;

/**
 * WhatsApp Service Interface
 * Handles WhatsApp notifications via Twilio WhatsApp Business API
 */
public interface WhatsAppService {

    /**
     * Send WhatsApp message to a single number
     */
    boolean sendWhatsAppMessage(String phoneNumber, String message);
    
    /**
     * Send WhatsApp message with media (image, PDF, etc.)
     */
    boolean sendWhatsAppMessageWithMedia(String phoneNumber, String message, String mediaUrl);
    
    /**
     * Send bulk WhatsApp messages
     */
    void sendBulkWhatsAppMessages(List<String> phoneNumbers, String message);
    
    /**
     * Send attendance WhatsApp notification
     */
    void sendAttendanceWhatsApp(Long studentId, LocalDate date, String status);
    
    /**
     * Send fee reminder via WhatsApp
     */
    void sendFeeReminderWhatsApp(Long studentId, Long feeId);
    
    /**
     * Send report card via WhatsApp (with PDF)
     */
    void sendReportCardWhatsApp(Long studentId, String semester, String pdfUrl);
    
    /**
     * Send exam admit card via WhatsApp
     */
    void sendAdmitCardWhatsApp(Long studentId, Long examId, String admitCardUrl);
    
    /**
     * Send ID card via WhatsApp
     */
    void sendIDCardWhatsApp(Long studentId, String idCardPdfUrl);
    
    /**
     * Send transfer certificate via WhatsApp
     */
    void sendTCWhatsApp(Long studentId, String tcPdfUrl);
    
    /**
     * Send event invitation via WhatsApp
     */
    void sendEventInvitationWhatsApp(Long eventId);
    
    /**
     * Send emergency alert via WhatsApp
     */
    void sendEmergencyWhatsAppToAll(String message);
    
    /**
     * Send welcome message to new student
     */
    void sendWelcomeWhatsApp(Long studentId);
    
    /**
     * Send birthday wishes via WhatsApp
     */
    void sendBirthdayWhatsApp(Long studentId);
}

