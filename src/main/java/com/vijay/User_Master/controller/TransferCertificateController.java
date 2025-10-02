package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.TransferCertificateRequest;
import com.vijay.User_Master.dto.TransferCertificateResponse;
import com.vijay.User_Master.service.TransferCertificateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Transfer Certificate Management REST Controller
 * Automatically generates TC with student's complete academic record
 */
@RestController
@RequestMapping("/api/v1/tc")
@AllArgsConstructor
@Slf4j
public class TransferCertificateController {

    private final TransferCertificateService tcService;

    /**
     * Generate Transfer Certificate (Automatic Data Collection)
     * - Automatically fetches attendance percentage
     * - Automatically calculates GPA
     * - Checks fee clearance
     * - Checks library clearance
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> generateTC(@Valid @RequestBody TransferCertificateRequest request) {
        log.info("Generating TC for student ID: {}", request.getStudentId());
        TransferCertificateResponse response = tcService.generateTC(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    /**
     * Approve TC (Principal/Admin approval)
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> approveTC(
            @PathVariable Long id,
            @RequestParam Long approvedByUserId) {
        log.info("Approving TC with ID: {}", id);
        TransferCertificateResponse response = tcService.approveTC(id, approvedByUserId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Issue TC (Final step - marks student as TRANSFERRED)
     */
    @PatchMapping("/{id}/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> issueTC(@PathVariable Long id) {
        log.info("Issuing TC with ID: {}", id);
        TransferCertificateResponse response = tcService.issueTC(id);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get TC by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getTCById(@PathVariable Long id) {
        TransferCertificateResponse response = tcService.getTCById(id);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get TC by student ID
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getTCByStudentId(@PathVariable Long studentId) {
        TransferCertificateResponse response = tcService.getTCByStudentId(studentId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get TC by TC number
     */
    @GetMapping("/number/{tcNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getTCByNumber(@PathVariable String tcNumber) {
        TransferCertificateResponse response = tcService.getTCByNumber(tcNumber);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get all issued TCs
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getAllTCs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<TransferCertificateResponse> response = tcService.getAllTCs(pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get pending approval TCs
     */
    @GetMapping("/pending-approvals")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getPendingApprovals() {
        List<TransferCertificateResponse> response = tcService.getPendingApprovals();
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Generate TC PDF (Printable version)
     */
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> generateTCPDF(@PathVariable Long id) {
        log.info("Generating TC PDF for ID: {}", id);
        String pdfUrl = tcService.generateTCPDF(id);
        return ExceptionUtil.createBuildResponse(new PDFResponse(id, pdfUrl), HttpStatus.OK);
    }

    /**
     * Cancel TC
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> cancelTC(
            @PathVariable Long id,
            @RequestParam String reason) {
        log.info("Cancelling TC with ID: {}", id);
        tcService.cancelTC(id, reason);
        return ExceptionUtil.createBuildResponseMessage("TC cancelled successfully", HttpStatus.OK);
    }

    /**
     * Delete TC
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTC(@PathVariable Long id) {
        tcService.deleteTC(id);
        return ExceptionUtil.createBuildResponseMessage("TC deleted successfully", HttpStatus.OK);
    }

    // Helper response classes
    private record PDFResponse(Long id, String pdfUrl) {}
}

