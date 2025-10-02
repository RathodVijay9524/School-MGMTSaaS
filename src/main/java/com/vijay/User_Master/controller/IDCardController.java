package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.IDCardResponse;
import com.vijay.User_Master.service.IDCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * ID Card Management REST Controller
 * Automatic ID card generation for students and teachers
 */
@RestController
@RequestMapping("/api/v1/idcards")
@AllArgsConstructor
@Slf4j
public class IDCardController {

    private final IDCardService idCardService;

    /**
     * Generate ID card automatically for student
     * - Auto-generates card number (ID-STU-YYYY-XXXX)
     * - Auto-fills all student information
     * - Generates QR code and barcode
     * - Sets validity period (1 year default)
     */
    @PostMapping("/generate/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> generateStudentIDCard(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        log.info("Generating ID card for student ID: {}", studentId);
        IDCardResponse response = idCardService.generateStudentIDCard(studentId, expiryDate);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    /**
     * Generate ID card automatically for teacher
     * - Auto-generates card number (ID-TCH-YYYY-XXXX)
     * - Auto-fills all teacher information
     * - Generates QR code and barcode
     * - Sets validity period (3 years default)
     */
    @PostMapping("/generate/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> generateTeacherIDCard(
            @PathVariable Long teacherId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        log.info("Generating ID card for teacher ID: {}", teacherId);
        IDCardResponse response = idCardService.generateTeacherIDCard(teacherId, expiryDate);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    /**
     * Get ID card by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getIDCardById(@PathVariable Long id) {
        IDCardResponse response = idCardService.getIDCardById(id);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get active ID card for student
     */
    @GetMapping("/student/{studentId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getActiveStudentCard(@PathVariable Long studentId) {
        IDCardResponse response = idCardService.getActiveStudentCard(studentId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get all ID cards
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getAllIDCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<IDCardResponse> response = idCardService.getAllIDCards(pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Report ID card as lost
     */
    @PatchMapping("/{id}/report-lost")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'TEACHER')")
    public ResponseEntity<?> reportLost(
            @PathVariable Long id,
            @RequestParam String reason) {
        log.info("Reporting ID card {} as lost", id);
        IDCardResponse response = idCardService.reportLost(id, reason);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Reissue ID card (for lost/damaged cards)
     * - Marks old card as REPLACED
     * - Generates new card automatically
     * - Charges replacement fee
     */
    @PostMapping("/{oldCardId}/reissue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> reissueIDCard(
            @PathVariable Long oldCardId,
            @RequestParam(defaultValue = "100.0") Double replacementFee) {
        log.info("Reissuing ID card for old card ID: {}", oldCardId);
        IDCardResponse response = idCardService.reissueIDCard(oldCardId, replacementFee);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    /**
     * Generate QR code for ID card
     */
    @GetMapping("/{id}/qrcode")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> generateQRCode(@PathVariable Long id) {
        String qrCode = idCardService.generateQRCode(id);
        return ExceptionUtil.createBuildResponse(new QRCodeResponse(id, qrCode), HttpStatus.OK);
    }

    // Helper response classes
    private record QRCodeResponse(Long cardId, String qrCodeData) {}
}

