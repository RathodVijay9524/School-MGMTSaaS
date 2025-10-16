package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.FeeInstallmentResponse;
import com.vijay.User_Master.dto.FeeRequest;
import com.vijay.User_Master.dto.FeeResponse;
import com.vijay.User_Master.service.FeeService;
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
 * Fee Management REST Controller
 */
@RestController
@RequestMapping("/api/v1/fees")
@AllArgsConstructor
@Slf4j
public class FeeController {

    private final FeeService feeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getAllFees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        log.info("Fetching all fees - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<FeeResponse> response = feeService.getAllFees(pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createFee(@Valid @RequestBody FeeRequest request) {
        log.info("Creating fee for student ID: {}", request.getStudentId());
        FeeResponse response = feeService.createFee(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateFee(@PathVariable Long id, @Valid @RequestBody FeeRequest request) {
        FeeResponse response = feeService.updateFee(id, request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getFeeById(@PathVariable Long id) {
        FeeResponse response = feeService.getFeeById(id);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getFeesByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<FeeResponse> response = feeService.getFeesByStudent(studentId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getFeesByPaymentStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeeResponse> response = feeService.getFeesByPaymentStatus(status, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getPendingFees(@PathVariable Long studentId) {
        List<FeeResponse> response = feeService.getPendingFees(studentId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getOverdueFees() {
        List<FeeResponse> response = feeService.getOverdueFees();
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PostMapping("/{feeId}/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> recordPayment(
            @PathVariable Long feeId,
            @RequestParam Double amount,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String transactionId) {
        log.info("Recording payment for fee ID: {}", feeId);
        FeeResponse response = feeService.recordPayment(feeId, amount, paymentMethod, transactionId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/summary/{academicYear}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getStudentFeeSummary(
            @PathVariable Long studentId,
            @PathVariable String academicYear) {
        List<FeeResponse> response = feeService.getStudentFeeSummary(studentId, academicYear);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/total-collected")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getTotalFeesCollected() {
        Double total = feeService.calculateTotalFeesCollected();
        return ExceptionUtil.createBuildResponse(new TotalResponse("collected", total), HttpStatus.OK);
    }

    @GetMapping("/total-pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getTotalPendingFees() {
        Double total = feeService.calculateTotalPendingFees();
        return ExceptionUtil.createBuildResponse(new TotalResponse("pending", total), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
        return ExceptionUtil.createBuildResponseMessage("Fee deleted successfully", HttpStatus.OK);
    }

    // ==================== INSTALLMENT ENDPOINTS ====================

    /**
     * Get all installments for a specific fee
     */
    @GetMapping("/{feeId}/installments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getFeeInstallments(
            @PathVariable Long feeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching installments for fee ID: {}", feeId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("installmentNumber").ascending());
        Page<FeeInstallmentResponse> response = feeService.getFeeInstallments(feeId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get a specific installment by ID
     */
    @GetMapping("/{feeId}/installments/{installmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getInstallmentById(
            @PathVariable Long feeId,
            @PathVariable Long installmentId) {
        log.info("Fetching installment ID: {} for fee ID: {}", installmentId, feeId);
        FeeInstallmentResponse response = feeService.getInstallmentById(installmentId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Pay a specific installment
     */
    @PostMapping("/{feeId}/installments/{installmentId}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> payInstallment(
            @PathVariable Long feeId,
            @PathVariable Long installmentId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String remarks) {
        log.info("Recording payment for installment ID: {} of fee ID: {}", installmentId, feeId);
        FeeInstallmentResponse response = feeService.payInstallment(
            installmentId, paymentMethod, transactionId, remarks);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get all overdue installments across all fees
     */
    @GetMapping("/installments/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getOverdueInstallments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching all overdue installments");
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<FeeInstallmentResponse> response = feeService.getOverdueInstallments(pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get pending installments for a specific student
     */
    @GetMapping("/student/{studentId}/installments/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getStudentPendingInstallments(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching pending installments for student ID: {}", studentId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<FeeInstallmentResponse> response = feeService.getStudentPendingInstallments(studentId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get installments due within a specific date range
     */
    @GetMapping("/installments/due")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getInstallmentsDueInRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching installments due between {} and {}", startDate, endDate);
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<FeeInstallmentResponse> response = feeService.getInstallmentsDueInRange(
            startDate, endDate, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get next pending installment for a fee
     */
    @GetMapping("/{feeId}/installments/next")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getNextPendingInstallment(@PathVariable Long feeId) {
        log.info("Fetching next pending installment for fee ID: {}", feeId);
        FeeInstallmentResponse response = feeService.getNextPendingInstallment(feeId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    // Helper response classes
    private record TotalResponse(String type, Double amount) {}
}

