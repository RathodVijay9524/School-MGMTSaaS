package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.service.WhatsAppService;
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
 * WhatsApp Notification Controller
 * Send WhatsApp messages to parents with rich media support
 * 
 * Works in MOCK mode by default
 * Configure Twilio WhatsApp Business API to enable real messaging
 */
@RestController
@RequestMapping("/api/v1/whatsapp")
@AllArgsConstructor
@Slf4j
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    /**
     * Send custom WhatsApp message
     */
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendWhatsAppMessage(
            @RequestParam String phoneNumber,
            @RequestParam String message) {
        log.info("Sending WhatsApp message to: {}", phoneNumber);
        boolean sent = whatsAppService.sendWhatsAppMessage(phoneNumber, message);
        return ExceptionUtil.createBuildResponse(
            new WhatsAppResponse(phoneNumber, sent), HttpStatus.OK);
    }

    /**
     * Send WhatsApp message with media (PDF, Image, etc.)
     */
    @PostMapping("/send-with-media")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendWhatsAppWithMedia(
            @RequestParam String phoneNumber,
            @RequestParam String message,
            @RequestParam String mediaUrl) {
        log.info("Sending WhatsApp with media to: {}", phoneNumber);
        boolean sent = whatsAppService.sendWhatsAppMessageWithMedia(phoneNumber, message, mediaUrl);
        return ExceptionUtil.createBuildResponse(
            new WhatsAppResponse(phoneNumber, sent), HttpStatus.OK);
    }

    /**
     * Send bulk WhatsApp messages
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendBulkWhatsApp(
            @RequestBody List<String> phoneNumbers,
            @RequestParam String message) {
        log.info("Sending bulk WhatsApp to {} recipients", phoneNumbers.size());
        whatsAppService.sendBulkWhatsAppMessages(phoneNumbers, message);
        return ExceptionUtil.createBuildResponseMessage(
            "Bulk WhatsApp sent to " + phoneNumbers.size() + " recipients", HttpStatus.OK);
    }

    /**
     * Send attendance WhatsApp notification
     */
    @PostMapping("/attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendAttendanceWhatsApp(
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String status) {
        log.info("Sending attendance WhatsApp for student ID: {}", studentId);
        whatsAppService.sendAttendanceWhatsApp(studentId, date, status);
        return ExceptionUtil.createBuildResponseMessage(
            "Attendance WhatsApp sent successfully", HttpStatus.OK);
    }

    /**
     * Send fee reminder via WhatsApp
     */
    @PostMapping("/fee-reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendFeeReminderWhatsApp(
            @RequestParam Long studentId,
            @RequestParam Long feeId) {
        log.info("Sending fee reminder WhatsApp for student ID: {}", studentId);
        whatsAppService.sendFeeReminderWhatsApp(studentId, feeId);
        return ExceptionUtil.createBuildResponseMessage(
            "Fee reminder WhatsApp sent", HttpStatus.OK);
    }

    /**
     * Send report card via WhatsApp with PDF
     */
    @PostMapping("/report-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendReportCardWhatsApp(
            @RequestParam Long studentId,
            @RequestParam String semester,
            @RequestParam String pdfUrl) {
        log.info("Sending report card WhatsApp for student ID: {}", studentId);
        whatsAppService.sendReportCardWhatsApp(studentId, semester, pdfUrl);
        return ExceptionUtil.createBuildResponseMessage(
            "Report card sent via WhatsApp", HttpStatus.OK);
    }

    /**
     * Send exam admit card via WhatsApp
     */
    @PostMapping("/admit-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendAdmitCardWhatsApp(
            @RequestParam Long studentId,
            @RequestParam Long examId,
            @RequestParam String admitCardUrl) {
        log.info("Sending admit card WhatsApp for student ID: {}", studentId);
        whatsAppService.sendAdmitCardWhatsApp(studentId, examId, admitCardUrl);
        return ExceptionUtil.createBuildResponseMessage(
            "Admit card sent via WhatsApp", HttpStatus.OK);
    }

    /**
     * Send ID card via WhatsApp
     */
    @PostMapping("/id-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendIDCardWhatsApp(
            @RequestParam Long studentId,
            @RequestParam String idCardPdfUrl) {
        log.info("Sending ID card WhatsApp for student ID: {}", studentId);
        whatsAppService.sendIDCardWhatsApp(studentId, idCardPdfUrl);
        return ExceptionUtil.createBuildResponseMessage(
            "ID card sent via WhatsApp", HttpStatus.OK);
    }

    /**
     * Send transfer certificate via WhatsApp
     */
    @PostMapping("/transfer-certificate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendTCWhatsApp(
            @RequestParam Long studentId,
            @RequestParam String tcPdfUrl) {
        log.info("Sending TC WhatsApp for student ID: {}", studentId);
        whatsAppService.sendTCWhatsApp(studentId, tcPdfUrl);
        return ExceptionUtil.createBuildResponseMessage(
            "Transfer certificate sent via WhatsApp", HttpStatus.OK);
    }

    /**
     * Send event invitation via WhatsApp
     */
    @PostMapping("/event-invitation")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendEventInvitationWhatsApp(@RequestParam Long eventId) {
        log.info("Sending event invitation WhatsApp for event ID: {}", eventId);
        whatsAppService.sendEventInvitationWhatsApp(eventId);
        return ExceptionUtil.createBuildResponseMessage(
            "Event invitations sent via WhatsApp", HttpStatus.OK);
    }

    /**
     * Send emergency WhatsApp to all parents
     */
    @PostMapping("/emergency")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendEmergencyWhatsAppToAll(@RequestParam String message) {
        log.info("Sending emergency WhatsApp to all parents");
        whatsAppService.sendEmergencyWhatsAppToAll(message);
        return ExceptionUtil.createBuildResponseMessage(
            "Emergency WhatsApp sent to all parents", HttpStatus.OK);
    }

    /**
     * Send welcome WhatsApp to new student
     */
    @PostMapping("/welcome")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendWelcomeWhatsApp(@RequestParam Long studentId) {
        log.info("Sending welcome WhatsApp for student ID: {}", studentId);
        whatsAppService.sendWelcomeWhatsApp(studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Welcome WhatsApp sent", HttpStatus.OK);
    }

    /**
     * Send birthday wishes via WhatsApp
     */
    @PostMapping("/birthday")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendBirthdayWhatsApp(@RequestParam Long studentId) {
        log.info("Sending birthday WhatsApp for student ID: {}", studentId);
        whatsAppService.sendBirthdayWhatsApp(studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Birthday wishes sent via WhatsApp", HttpStatus.OK);
    }

    // Helper response class
    private record WhatsAppResponse(String phoneNumber, boolean sent) {}
    private record SMSResponse(String phoneNumber, boolean sent) {}
}

