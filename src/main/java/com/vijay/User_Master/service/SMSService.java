package com.vijay.User_Master.service;

import java.time.LocalDate;
import java.util.List;

/**
 * SMS Service Interface
 * Handles SMS notifications via Twilio
 */
public interface SMSService {

    /**
     * Send SMS to a single phone number
     */
    boolean sendSMS(String phoneNumber, String message);
    
    /**
     * Send bulk SMS to multiple phone numbers
     */
    void sendBulkSMS(List<String> phoneNumbers, String message);
    
    /**
     * Send attendance SMS to parent
     */
    void sendAttendanceSMS(Long studentId, LocalDate date, String status);
    
    /**
     * Send fee reminder SMS
     */
    void sendFeeReminderSMS(Long studentId, Long feeId, Double amount, LocalDate dueDate);
    
    /**
     * Send exam reminder SMS
     */
    void sendExamReminderSMS(Long studentId, Long examId);
    
    /**
     * Send low attendance warning SMS
     */
    void sendLowAttendanceWarningSMS(Long studentId, Double percentage);
    
    /**
     * Send grade published SMS
     */
    void sendGradePublishedSMS(Long studentId, String subject, Double percentage, String grade);
    
    /**
     * Send emergency SMS to all parents
     */
    void sendEmergencySMSToAll(String message);
    
    /**
     * Send OTP for verification
     */
    boolean sendOTP(String phoneNumber, String otp);
    
    /**
     * Send welcome SMS to new student
     */
    void sendWelcomeSMS(Long studentId);
}

