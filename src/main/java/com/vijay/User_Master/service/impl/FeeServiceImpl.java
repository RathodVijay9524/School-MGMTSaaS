package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.FeeRequest;
import com.vijay.User_Master.dto.FeeResponse;
import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.FeeRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.FeeService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
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
    public FeeResponse createFee(FeeRequest request) {
        log.info("Creating fee for student ID: {}", request.getStudentId());
        
        Worker student = workerRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        
        User collectedBy = null;
        if (request.getCollectedByUserId() != null) {
            collectedBy = userRepository.findById(request.getCollectedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getCollectedByUserId()));
        }
        
        Double paidAmount = request.getPaidAmount() != null ? request.getPaidAmount() : 0.0;
        Double discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0;
        Double balanceAmount = request.getTotalAmount() - paidAmount - discountAmount;
        
        Fee.PaymentStatus paymentStatus = calculatePaymentStatus(balanceAmount, request.getDueDate());
        String receiptNumber = generateReceiptNumber();
        
        // Get the current logged-in user as owner
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
            .orElseThrow(() -> new RuntimeException("Owner user not found"));
        
        Fee fee = Fee.builder()
            .student(student)
            .totalAmount(request.getTotalAmount())
            .paidAmount(paidAmount)
            .discountAmount(discountAmount)
            .balanceAmount(balanceAmount)
            .dueDate(request.getDueDate())
            .paymentStatus(paymentStatus)
            .receiptNumber(receiptNumber)
            .paymentMethod(request.getPaymentMethod())
            .paymentDate(request.getPaymentDate())
            .collectedBy(collectedBy)
            .remarks(request.getRemarks())
            .owner(owner) // Set the owner for multi-tenancy
            .build();
        Fee savedFee = feeRepository.save(fee);
        
        // Update student fee balance
        updateStudentFeeBalance(student.getId());
        
        return mapToResponse(savedFee);
    }

    @Override
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
    public FeeResponse getFeeById(Long id) {
        Fee fee = feeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", id));
        return mapToResponse(fee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeeResponse> getFeesByStudent(Long studentId, Pageable pageable) {
        return feeRepository.findByStudent_IdAndIsDeletedFalse(studentId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeeResponse> getFeesByPaymentStatus(String status, Pageable pageable) {
        Fee.PaymentStatus paymentStatus = Fee.PaymentStatus.valueOf(status.toUpperCase());
        return feeRepository.findByPaymentStatusAndIsDeletedFalse(paymentStatus, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponse> getPendingFees(Long studentId) {
        return feeRepository.findPendingFees(studentId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponse> getOverdueFees() {
        return feeRepository.findOverdueFees(LocalDate.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponse> getStudentFeeSummary(Long studentId, String academicYear) {
        return feeRepository.getStudentFeeSummary(studentId, academicYear).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalFeesCollected() {
        Double total = feeRepository.calculateTotalFeesCollected();
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalPendingFees() {
        Double total = feeRepository.calculateTotalPendingFees();
        return total != null ? total : 0.0;
    }

    @Override
    public FeeResponse updateFee(Long id, FeeRequest request) {
        Fee fee = feeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", id));
        
        fee.setTotalAmount(request.getTotalAmount());
        fee.setDueDate(request.getDueDate());
        fee.setRemarks(request.getRemarks());
        
        Fee updated = feeRepository.save(fee);
        return mapToResponse(updated);
    }

    @Override
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
            .feeTypeDisplay(fee.getFeeType().toString())
            .paymentStatusDisplay(fee.getPaymentStatus().toString())
            .paymentMethodDisplay(fee.getPaymentMethod() != null ? fee.getPaymentMethod().toString() : null)
            .isOverdue(isOverdue)
            .daysOverdue(daysOverdue)
            .paymentPercentage(paymentPercentage)
            .build();
    }
    
    private Long getCurrentOwnerId() {
        // Get the logged-in user ID for multi-tenancy
        return 1L; // For now, using karina's ID. In real implementation, get from security context
    }
}

