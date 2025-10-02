package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.service.SMSService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * SMS Notification Controller
 * Send instant SMS notifications to parents and students
 * 
 * Works in MOCK mode by default
 * Configure Twilio to enable real SMS sending
 */
@RestController
@RequestMapping("/api/v1/sms")
@AllArgsConstructor
@Slf4j
public class SMSController {

    private final SMSService smsService;

    /**
     * Send custom SMS to a phone number
     */
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendSMS(
            @RequestParam String phoneNumber,
            @RequestParam String message) {
        log.info("Sending SMS to: {}", phoneNumber);
        boolean sent = smsService.sendSMS(phoneNumber, message);
        return ExceptionUtil.createBuildResponse(
            new SMSResponse(phoneNumber, sent), HttpStatus.OK);
    }

    /**
     * Send bulk SMS to multiple numbers
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendBulkSMS(
            @RequestBody List<String> phoneNumbers,
            @RequestParam String message) {
        log.info("Sending bulk SMS to {} recipients", phoneNumbers.size());
        smsService.sendBulkSMS(phoneNumbers, message);
        return ExceptionUtil.createBuildResponseMessage(
            "Bulk SMS sent to " + phoneNumbers.size() + " recipients", HttpStatus.OK);
    }

    /**
     * Send attendance SMS to parent
     */
    @PostMapping("/attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendAttendanceSMS(
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String status) {
        log.info("Sending attendance SMS for student ID: {}", studentId);
        smsService.sendAttendanceSMS(studentId, date, status);
        return ExceptionUtil.createBuildResponseMessage(
            "Attendance SMS sent successfully", HttpStatus.OK);
    }

    /**
     * Send fee reminder SMS
     */
    @PostMapping("/fee-reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendFeeReminderSMS(
            @RequestParam Long studentId,
            @RequestParam Long feeId,
            @RequestParam Double amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        log.info("Sending fee reminder SMS for student ID: {}", studentId);
        smsService.sendFeeReminderSMS(studentId, feeId, amount, dueDate);
        return ExceptionUtil.createBuildResponseMessage(
            "Fee reminder SMS sent successfully", HttpStatus.OK);
    }

    /**
     * Send exam reminder SMS
     */
    @PostMapping("/exam-reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendExamReminderSMS(
            @RequestParam Long studentId,
            @RequestParam Long examId) {
        log.info("Sending exam reminder SMS for student ID: {}", studentId);
        smsService.sendExamReminderSMS(studentId, examId);
        return ExceptionUtil.createBuildResponseMessage(
            "Exam reminder SMS sent successfully", HttpStatus.OK);
    }

    /**
     * Send low attendance warning SMS
     */
    @PostMapping("/low-attendance-warning")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendLowAttendanceWarningSMS(
            @RequestParam Long studentId,
            @RequestParam Double percentage) {
        log.info("Sending low attendance warning SMS for student ID: {}", studentId);
        smsService.sendLowAttendanceWarningSMS(studentId, percentage);
        return ExceptionUtil.createBuildResponseMessage(
            "Low attendance warning SMS sent", HttpStatus.OK);
    }

    /**
     * Send grade published SMS
     */
    @PostMapping("/grade-published")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendGradePublishedSMS(
            @RequestParam Long studentId,
            @RequestParam String subject,
            @RequestParam Double percentage,
            @RequestParam String grade) {
        log.info("Sending grade published SMS for student ID: {}", studentId);
        smsService.sendGradePublishedSMS(studentId, subject, percentage, grade);
        return ExceptionUtil.createBuildResponseMessage(
            "Grade SMS sent successfully", HttpStatus.OK);
    }

    /**
     * Send emergency SMS to all parents
     */
    @PostMapping("/emergency")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendEmergencySMSToAll(@RequestParam String message) {
        log.info("Sending emergency SMS to all parents");
        smsService.sendEmergencySMSToAll(message);
        return ExceptionUtil.createBuildResponseMessage(
            "Emergency SMS sent to all parents", HttpStatus.OK);
    }

    /**
     * Send OTP for verification
     */
    @PostMapping("/send-otp")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendOTP(
            @RequestParam String phoneNumber,
            @RequestParam String otp) {
        log.info("Sending OTP to: {}", phoneNumber);
        boolean sent = smsService.sendOTP(phoneNumber, otp);
        return ExceptionUtil.createBuildResponse(
            new SMSResponse(phoneNumber, sent), HttpStatus.OK);
    }

    /**
     * Send welcome SMS to new student
     */
    @PostMapping("/welcome")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendWelcomeSMS(@RequestParam Long studentId) {
        log.info("Sending welcome SMS for student ID: {}", studentId);
        smsService.sendWelcomeSMS(studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Welcome SMS sent successfully", HttpStatus.OK);
    }

    // Helper response class
    private record SMSResponse(String phoneNumber, boolean sent) {}
}

