package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.FeeRequest;
import com.vijay.User_Master.dto.FeeResponse;
import com.vijay.User_Master.dto.FeeInstallmentResponse;
import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.entity.FeeInstallment;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.BusinessRuleViolationException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import org.springframework.ai.tool.annotation.Tool;
import com.vijay.User_Master.repository.FeeRepository;
import com.vijay.User_Master.repository.FeeInstallmentRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.FeeService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final FeeInstallmentRepository feeInstallmentRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getAllFees", description = "Get all fees with pagination")
    public Page<FeeResponse> getAllFees(Pageable pageable) {
        log.info("Fetching all fees with pagination");
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        // Use owner-based query
        Page<Fee> feePage = feeRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        
        return feePage.map(this::mapToResponse);
    }

    @Override
    @Tool(name = "createFee", description = "Create a new fee record for a student")
    public FeeResponse createFee(FeeRequest request) {
        log.info("Creating fee for student ID: {}", request.getStudentId());
        log.info("Fee request data - feeCategory: '{}', feeType: '{}', totalAmount: {}", 
                request.getFeeCategory(), request.getFeeType(), request.getTotalAmount());
        
        Worker student = workerRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        
        // NEW: Get parent/guardian if provided
        Worker parent = null;
        if (request.getParentId() != null) {
            parent = workerRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent", "id", request.getParentId()));
        }
        
        User collectedBy = null;
        if (request.getCollectedByUserId() != null) {
            collectedBy = userRepository.findById(request.getCollectedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getCollectedByUserId()));
        }
        
        Double paidAmount = request.getPaidAmount() != null ? request.getPaidAmount() : 0.0;
        Double discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0;
        Double balanceAmount = request.getTotalAmount() - paidAmount - discountAmount;
        
        log.info("Fee calculation - Total: {}, Paid: {}, Discount: {}, Balance: {}", 
                request.getTotalAmount(), paidAmount, discountAmount, balanceAmount);
        
        Fee.PaymentStatus paymentStatus = calculatePaymentStatus(balanceAmount, request.getDueDate());
        String receiptNumber = generateReceiptNumber();
        
        // Get the current logged-in user as owner
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
            .orElseThrow(() -> new RuntimeException("Owner user not found"));
        
        Fee fee = Fee.builder()
            .student(student)
            .feeType(request.getFeeType())
            .feeCategory(request.getFeeCategory())
            .totalAmount(request.getTotalAmount())
            .paidAmount(paidAmount)
            .discountAmount(discountAmount)
            .balanceAmount(balanceAmount)
            .dueDate(request.getDueDate())
            .paymentStatus(paymentStatus)
            .receiptNumber(receiptNumber)
            .paymentMethod(request.getPaymentMethod())
            .paymentDate(request.getPaymentDate())
            .transactionId(request.getTransactionId())
            .academicYear(request.getAcademicYear())
            .semester(request.getSemester())
            .lateFeeAmount(request.getLateFeeAmount())
            .lateFeeReason(request.getLateFeeReason())
            .isWaived(request.isWaived())
            .waiverReason(request.getWaiverReason())
            .collectedBy(collectedBy)
            .remarks(request.getRemarks())
            // NEW FIELDS - Parent/Guardian
            .parent(parent)
            // NEW FIELDS - Installment Support
            .installmentAllowed(request.isInstallmentAllowed())
            .paymentPlanType(request.getPaymentPlanType())
            .totalInstallments(request.getTotalInstallments())
            .paidInstallments(0) // Initially zero
            .installmentAmount(request.getInstallmentAmount())
            .owner(owner) // Set the owner for multi-tenancy
            .build();
        Fee savedFee = feeRepository.save(fee);
        
        // NEW: Create installments if payment plan is enabled
        if (request.isInstallmentAllowed() && request.getTotalInstallments() != null && request.getTotalInstallments() > 0) {
            createInstallmentsForFee(savedFee, request, owner);
        }
        
        // Update student fee balance
        updateStudentFeeBalance(student.getId());
        
        return mapToResponse(savedFee);
    }

    @Override
    @Tool(name = "recordFeePayment", description = "Record fee payment for a student")
    public FeeResponse recordPayment(Long feeId, Double amount, String paymentMethod, String transactionId) {
        log.info("Recording payment of ₹{} for fee ID: {}", amount, feeId);
        
        Fee fee = feeRepository.findById(feeId)
            .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", feeId));
        
        if (amount > fee.getBalanceAmount()) {
            throw new BadApiRequestException("Payment amount exceeds balance amount");
        }
        
        Double newPaidAmount = fee.getPaidAmount() + amount;
        Double newBalance = fee.getBalanceAmount() - amount;
        
        fee.setPaidAmount(newPaidAmount);
        fee.setBalanceAmount(newBalance);
        fee.setPaymentDate(LocalDate.now());
        fee.setPaymentMethod(Fee.PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        fee.setTransactionId(transactionId);
        
        // Update payment status
        if (newBalance == 0) {
            fee.setPaymentStatus(Fee.PaymentStatus.PAID);
        } else if (newBalance < fee.getTotalAmount()) {
            fee.setPaymentStatus(Fee.PaymentStatus.PARTIAL);
        }
        
        Fee updated = feeRepository.save(fee);
        
        // Update student fee balance
        updateStudentFeeBalance(fee.getStudent().getId());
        
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getFeeById", description = "Get fee details by fee ID")
    public FeeResponse getFeeById(Long id) {
        Fee fee = feeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", id));
        return mapToResponse(fee);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getFeesByStudent", description = "Get all fees for a specific student")
    public Page<FeeResponse> getFeesByStudent(Long studentId, Pageable pageable) {
        return feeRepository.findByStudent_IdAndIsDeletedFalse(studentId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getFeesByPaymentStatus", description = "Get fees by payment status (PAID, PENDING, OVERDUE, PARTIAL)")
    public Page<FeeResponse> getFeesByPaymentStatus(String status, Pageable pageable) {
        Fee.PaymentStatus paymentStatus = Fee.PaymentStatus.valueOf(status.toUpperCase());
        return feeRepository.findByPaymentStatusAndIsDeletedFalse(paymentStatus, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getPendingFees", description = "Get all pending fees for a student")
    public List<FeeResponse> getPendingFees(Long studentId) {
        return feeRepository.findPendingFees(studentId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getOverdueFees", description = "Get all overdue fees across all students")
    public List<FeeResponse> getOverdueFees() {
        return feeRepository.findOverdueFees(LocalDate.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getStudentFeeSummary", description = "Get fee summary for a student in specific academic year")
    public List<FeeResponse> getStudentFeeSummary(Long studentId, String academicYear) {
        return feeRepository.getStudentFeeSummary(studentId, academicYear).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(description = "Calculate total fees collected across all students")
    public Double calculateTotalFeesCollected() {
        Double total = feeRepository.calculateTotalFeesCollected();
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "calculateTotalPendingFees", description = "Calculate total pending fees across all students")
    public Double calculateTotalPendingFees() {
        Double total = feeRepository.calculateTotalPendingFees();
        return total != null ? total : 0.0;
    }

    @Override
    @Tool(name = "updateFee", description = "Update fee details including total amount, due date and remarks")
    public FeeResponse updateFee(Long id, FeeRequest request) {
        Fee fee = feeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", id));
        
        // Update all editable fields
        fee.setFeeType(request.getFeeType());
        fee.setFeeCategory(request.getFeeCategory());
        fee.setTotalAmount(request.getTotalAmount());
        fee.setPaidAmount(request.getPaidAmount() != null ? request.getPaidAmount() : 0.0);
        fee.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0);
        fee.setDueDate(request.getDueDate());
        fee.setPaymentDate(request.getPaymentDate());
        fee.setPaymentMethod(request.getPaymentMethod());
        fee.setTransactionId(request.getTransactionId());
        fee.setAcademicYear(request.getAcademicYear());
        fee.setSemester(request.getSemester());
        fee.setRemarks(request.getRemarks());
        fee.setLateFeeAmount(request.getLateFeeAmount());
        fee.setLateFeeReason(request.getLateFeeReason());
        fee.setWaived(request.isWaived());
        fee.setWaiverReason(request.getWaiverReason());
        
        // Recalculate balance and payment status
        Double balanceAmount = fee.getTotalAmount() - fee.getPaidAmount() - fee.getDiscountAmount();
        log.info("Update fee calculation - Total: {}, Paid: {}, Discount: {}, Balance: {}", 
                fee.getTotalAmount(), fee.getPaidAmount(), fee.getDiscountAmount(), balanceAmount);
        fee.setBalanceAmount(balanceAmount);
        fee.setPaymentStatus(calculatePaymentStatus(balanceAmount, fee.getDueDate()));
        
        Fee updated = feeRepository.save(fee);
        return mapToResponse(updated);
    }

    @Override
    @Tool(name = "deleteFee", description = "Delete fee record permanently")
    public void deleteFee(Long id) {
        Fee fee = feeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", id));
        feeRepository.delete(fee);
    }

    // Helper Methods
    
    private Fee.PaymentStatus calculatePaymentStatus(Double balanceAmount, LocalDate dueDate) {
        if (balanceAmount == 0) {
            return Fee.PaymentStatus.PAID;
        } else if (dueDate.isBefore(LocalDate.now())) {
            return Fee.PaymentStatus.OVERDUE;
        } else if (balanceAmount > 0) {
            return Fee.PaymentStatus.PENDING;
        }
        return Fee.PaymentStatus.PENDING;
    }
    
    private String generateReceiptNumber() {
        return "REC-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void updateStudentFeeBalance(Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student != null) {
            List<Fee> fees = feeRepository.findByStudent_IdAndIsDeletedFalse(studentId, Pageable.unpaged()).getContent();
            Double totalFees = fees.stream().mapToDouble(Fee::getTotalAmount).sum();
            Double totalPaid = fees.stream().mapToDouble(Fee::getPaidAmount).sum();
            Double balance = totalFees - totalPaid;
            
            student.setTotalFees(totalFees);
            student.setFeesPaid(totalPaid);
            student.setFeesBalance(balance);
            workerRepository.save(student);
        }
    }
    
    private FeeResponse mapToResponse(Fee fee) {
        boolean isOverdue = fee.getDueDate() != null && fee.getDueDate().isBefore(LocalDate.now()) 
            && fee.getPaymentStatus() != Fee.PaymentStatus.PAID;
        Integer daysOverdue = isOverdue 
            ? (int) ChronoUnit.DAYS.between(fee.getDueDate(), LocalDate.now()) 
            : 0;
        Double paymentPercentage = (fee.getPaidAmount() / fee.getTotalAmount()) * 100.0;
        
        // Load installments manually if payment plan is enabled (handle lazy loading)
        List<FeeInstallmentResponse> installmentResponses = null;
        log.debug("mapToResponse: Fee ID={}, isInstallmentAllowed={}, hasOwner={}", 
            fee.getId(), fee.isInstallmentAllowed(), fee.getOwner() != null);
        
        if (fee.isInstallmentAllowed() && fee.getOwner() != null) {
            List<FeeInstallment> installments = feeInstallmentRepository
                .findByFee_IdAndOwner_IdAndIsDeletedFalseOrderByInstallmentNumberAsc(fee.getId(), fee.getOwner().getId());
            log.info("Loaded {} installments for fee ID: {}", 
                installments != null ? installments.size() : 0, fee.getId());
            
            if (installments != null && !installments.isEmpty()) {
                installmentResponses = installments.stream()
                    .map(this::convertInstallmentToResponse)
                    .collect(Collectors.toList());
                log.info("Converted {} installments to response DTOs", installmentResponses.size());
            }
        }
        
        return FeeResponse.builder()
            .id(fee.getId())
            .studentId(fee.getStudent().getId())
            .studentName(fee.getStudent().getName())
            .admissionNumber(fee.getStudent().getUsername())
            .feeType(fee.getFeeType())
            .feeCategory(fee.getFeeCategory())
            .totalAmount(fee.getTotalAmount())
            .paidAmount(fee.getPaidAmount())
            .discountAmount(fee.getDiscountAmount())
            .balanceAmount(fee.getBalanceAmount())
            .paymentStatus(fee.getPaymentStatus())
            .dueDate(fee.getDueDate())
            .paymentDate(fee.getPaymentDate())
            .paymentMethod(fee.getPaymentMethod())
            .transactionId(fee.getTransactionId())
            .receiptNumber(fee.getReceiptNumber())
            .academicYear(fee.getAcademicYear())
            .semester(fee.getSemester())
            .remarks(fee.getRemarks())
            .lateFeeReason(fee.getLateFeeReason())
            .lateFeeAmount(fee.getLateFeeAmount())
            .isWaived(fee.isWaived())
            .waiverReason(fee.getWaiverReason())
            .collectedByUserId(fee.getCollectedBy() != null ? fee.getCollectedBy().getId() : null)
            .collectedByUsername(fee.getCollectedBy() != null ? fee.getCollectedBy().getUsername() : null)
            // NEW FIELDS - Parent/Guardian
            .parentId(fee.getParent() != null ? fee.getParent().getId() : null)
            .parentName(fee.getParent() != null ? fee.getParent().getName() : null)
            .parentContact(fee.getParent() != null ? fee.getParent().getPhoNo() : null)
            // NEW FIELDS - Installment Support
            .installmentAllowed(fee.isInstallmentAllowed())
            .paymentPlanType(fee.getPaymentPlanType())
            .totalInstallments(fee.getTotalInstallments())
            .paidInstallments(fee.getPaidInstallments())
            .nextInstallmentDueDate(fee.getNextInstallmentDueDate())
            .installmentAmount(fee.getInstallmentAmount())
            .installments(installmentResponses)
            // Computed fields
            .feeTypeDisplay(fee.getFeeType().toString())
            .paymentStatusDisplay(fee.getPaymentStatus().toString())
            .paymentMethodDisplay(fee.getPaymentMethod() != null ? fee.getPaymentMethod().toString() : null)
            .paymentPlanTypeDisplay(fee.getPaymentPlanType() != null ? fee.getPaymentPlanType().toString() : "FULL_PAYMENT")
            .isOverdue(isOverdue)
            .daysOverdue(daysOverdue)
            .paymentPercentage(paymentPercentage)
            .installmentsRemaining(fee.getTotalInstallments() != null && fee.getPaidInstallments() != null ? 
                fee.getTotalInstallments() - fee.getPaidInstallments() : 0)
            .hasOutstandingInstallments(fee.isInstallmentAllowed() && fee.getPaidInstallments() != null && 
                fee.getTotalInstallments() != null && fee.getPaidInstallments() < fee.getTotalInstallments())
            .build();
    }
    
    /**
     * Create installments for a fee based on payment plan
     */
    private void createInstallmentsForFee(Fee fee, FeeRequest request, User owner) {
        LocalDate startDate = fee.getDueDate();
        int totalInstallments = request.getTotalInstallments();
        Double amountPerInstallment = request.getInstallmentAmount() != null ? 
            request.getInstallmentAmount() : 
            fee.getTotalAmount() / totalInstallments;
        
        log.info("Creating {} installments for fee ID: {}, Amount per installment: {}", 
            totalInstallments, fee.getId(), amountPerInstallment);
        
        for (int i = 1; i <= totalInstallments; i++) {
            LocalDate dueDate = calculateInstallmentDueDate(startDate, i, fee.getPaymentPlanType());
            
            FeeInstallment installment = FeeInstallment.builder()
                .fee(fee)
                .installmentNumber(i)
                .amount(amountPerInstallment)
                .dueDate(dueDate)
                .status(FeeInstallment.InstallmentStatus.PENDING)
                .owner(owner)
                .build();
            
            feeInstallmentRepository.save(installment);
        }
        
        // Update nextInstallmentDueDate to the first installment's due date
        fee.setNextInstallmentDueDate(calculateInstallmentDueDate(startDate, 1, fee.getPaymentPlanType()));
        feeRepository.save(fee);
        
        log.info("Successfully created {} installments for fee ID: {}", totalInstallments, fee.getId());
    }
    
    /**
     * Calculate installment due date based on payment plan type
     */
    private LocalDate calculateInstallmentDueDate(LocalDate startDate, int installmentNumber, Fee.PaymentPlanType planType) {
        if (planType == null) {
            planType = Fee.PaymentPlanType.MONTHLY; // Default to monthly
        }
        
        switch (planType) {
            case MONTHLY:
                return startDate.plusMonths((long) installmentNumber - 1);
            case QUARTERLY:
                return startDate.plusMonths((long) (installmentNumber - 1) * 3);
            case SEMI_ANNUAL:
                return startDate.plusMonths((long) (installmentNumber - 1) * 6);
            case ANNUAL:
                return startDate.plusYears((long) installmentNumber - 1);
            case CUSTOM:
                // For custom, default to monthly for now
                return startDate.plusMonths((long) installmentNumber - 1);
            default:
                return startDate.plusMonths((long) installmentNumber - 1);
        }
    }
    
    /**
     * Convert FeeInstallment entity to FeeInstallmentResponse DTO
     */
    private FeeInstallmentResponse convertInstallmentToResponse(FeeInstallment installment) {
        Double netAmount = installment.getAmount();
        if (installment.getDiscountAmount() != null) {
            netAmount -= installment.getDiscountAmount();
        }
        if (installment.getLateFeeAmount() != null) {
            netAmount += installment.getLateFeeAmount();
        }
        
        return FeeInstallmentResponse.builder()
            .id(installment.getId())
            .feeId(installment.getFee().getId())
            .installmentNumber(installment.getInstallmentNumber())
            .amount(installment.getAmount())
            .dueDate(installment.getDueDate())
            .paidDate(installment.getPaidDate())
            .paidAt(installment.getPaidAt())
            .status(installment.getStatus())
            .paymentMethod(installment.getPaymentMethod())
            .transactionId(installment.getTransactionId())
            .receiptNumber(installment.getReceiptNumber())
            .remarks(installment.getRemarks())
            .lateFeeAmount(installment.getLateFeeAmount())
            .daysOverdue(installment.getDaysOverdue())
            .isLatePayment(installment.isLatePayment())
            .discountAmount(installment.getDiscountAmount())
            .isWaived(installment.isWaived())
            .waiverReason(installment.getWaiverReason())
            .collectedByUserId(installment.getCollectedBy() != null ? installment.getCollectedBy().getId() : null)
            .collectedByUserName(installment.getCollectedBy() != null ? installment.getCollectedBy().getName() : null)
            .isDeleted(installment.isDeleted())
            .createdOn(installment.getCreatedOn())
            .updatedOn(installment.getUpdatedOn())
            // Computed fields
            .isOverdue(installment.isOverdue())
            .isPaid(installment.getStatus() == FeeInstallment.InstallmentStatus.PAID)
            .isPending(installment.getStatus() == FeeInstallment.InstallmentStatus.PENDING)
            .statusDisplay(installment.getStatus() != null ? installment.getStatus().name() : "PENDING")
            .paymentMethodDisplay(installment.getPaymentMethod() != null ? installment.getPaymentMethod().name() : "N/A")
            .netAmount(netAmount)
            .build();
    }
    
    // ==================== INSTALLMENT SERVICE METHODS ====================
    
    @Override
    @Transactional(readOnly = true)
    public Page<FeeInstallmentResponse> getFeeInstallments(Long feeId, Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        List<FeeInstallment> allInstallments = feeInstallmentRepository
            .findByFee_IdAndOwner_IdAndIsDeletedFalseOrderByInstallmentNumberAsc(feeId, ownerId);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allInstallments.size());
        
        List<FeeInstallmentResponse> responses = allInstallments.subList(start, end).stream()
            .map(this::convertInstallmentToResponse)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            responses, pageable, allInstallments.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public FeeInstallmentResponse getInstallmentById(Long installmentId) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        FeeInstallment installment = feeInstallmentRepository
            .findByIdAndOwner_IdAndIsDeletedFalse(installmentId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Installment", "id", installmentId));
        return convertInstallmentToResponse(installment);
    }
    
    @Override
    @Transactional
    public FeeInstallmentResponse payInstallment(Long installmentId, String paymentMethod, 
                                                  String transactionId, String remarks) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        User collectedBy = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        
        FeeInstallment installment = feeInstallmentRepository
            .findByIdAndOwner_IdAndIsDeletedFalse(installmentId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Installment", "id", installmentId));
        
        if (installment.getStatus() == FeeInstallment.InstallmentStatus.PAID) {
            throw new BusinessRuleViolationException("Installment is already paid");
        }
        
        // Mark installment as paid
        installment.setPaidDate(LocalDate.now());
        installment.setPaidAt(LocalDateTime.now());
        installment.setStatus(FeeInstallment.InstallmentStatus.PAID);
        installment.setPaymentMethod(Fee.PaymentMethod.valueOf(paymentMethod));
        installment.setTransactionId(transactionId);
        installment.setCollectedBy(collectedBy);
        installment.setRemarks(remarks);
        
        // Calculate late fee if overdue
        if (installment.getDueDate().isBefore(LocalDate.now())) {
            installment.setLatePayment(true);
            installment.setDaysOverdue(installment.calculateDaysOverdue());
        }
        
        FeeInstallment savedInstallment = feeInstallmentRepository.save(installment);
        
        // Update parent fee
        Fee fee = installment.getFee();
        fee.setPaidAmount(fee.getPaidAmount() + installment.getAmount());
        fee.setBalanceAmount(fee.getBalanceAmount() - installment.getAmount());
        fee.setPaidInstallments(fee.getPaidInstallments() + 1);
        
        // Update fee status
        if (fee.getBalanceAmount() <= 0.001) {
            fee.setPaymentStatus(Fee.PaymentStatus.PAID);
        } else if (fee.getPaidAmount() > 0) {
            fee.setPaymentStatus(Fee.PaymentStatus.PARTIAL);
        }
        
        // Update next installment due date
        FeeInstallment nextInstallment = feeInstallmentRepository
            .findNextDueInstallment(fee.getId(), ownerId)
            .orElse(null);
        
        if (nextInstallment != null) {
            fee.setNextInstallmentDueDate(nextInstallment.getDueDate());
        } else {
            fee.setNextInstallmentDueDate(null);
        }
        
        feeRepository.save(fee);
        
        // Update student fee balance
        updateStudentFeeBalance(fee.getStudent().getId());
        
        log.info("Installment ID {} paid successfully. Fee ID: {}, Amount: {}", 
            installmentId, fee.getId(), installment.getAmount());
        
        return convertInstallmentToResponse(savedInstallment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FeeInstallmentResponse> getOverdueInstallments(Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        List<FeeInstallment> overdueInstallments = feeInstallmentRepository
            .findAllOverdueInstallmentsByOwner(LocalDate.now(), ownerId);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), overdueInstallments.size());
        
        List<FeeInstallmentResponse> responses = overdueInstallments.subList(start, end).stream()
            .map(this::convertInstallmentToResponse)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            responses, pageable, overdueInstallments.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FeeInstallmentResponse> getStudentPendingInstallments(Long studentId, Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        // Get all fees for the student
        List<Fee> studentFees = feeRepository.findByStudent_IdAndIsDeletedFalse(
            studentId, Pageable.unpaged()).getContent();
        
        List<FeeInstallment> pendingInstallments = new ArrayList<>();
        for (Fee fee : studentFees) {
            List<FeeInstallment> feeInstallments = feeInstallmentRepository
                .findByFee_IdAndStatusAndOwner_IdAndIsDeletedFalse(
                    fee.getId(), FeeInstallment.InstallmentStatus.PENDING, ownerId);
            pendingInstallments.addAll(feeInstallments);
        }
        
        // Sort by due date
        pendingInstallments.sort(Comparator.comparing(FeeInstallment::getDueDate));
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), pendingInstallments.size());
        
        List<FeeInstallmentResponse> responses = pendingInstallments.subList(start, end).stream()
            .map(this::convertInstallmentToResponse)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            responses, pageable, pendingInstallments.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FeeInstallmentResponse> getInstallmentsDueInRange(String startDate, String endDate, Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        List<FeeInstallment> installments = feeInstallmentRepository
            .findInstallmentsDueInRange(start, end, ownerId);
        
        int pageStart = (int) pageable.getOffset();
        int pageEnd = Math.min((pageStart + pageable.getPageSize()), installments.size());
        
        List<FeeInstallmentResponse> responses = installments.subList(pageStart, pageEnd).stream()
            .map(this::convertInstallmentToResponse)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            responses, pageable, installments.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public FeeInstallmentResponse getNextPendingInstallment(Long feeId) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        FeeInstallment nextInstallment = feeInstallmentRepository
            .findNextDueInstallment(feeId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Installment", "pending for fee", feeId));
        
        return convertInstallmentToResponse(nextInstallment);
    }
}

