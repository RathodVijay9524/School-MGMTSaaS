package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.entity.FeeInstallment;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for FeeInstallment creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeInstallmentRequest {

    @NotNull(message = "Fee ID is required")
    private Long feeId;
    
    @NotNull(message = "Installment number is required")
    @Min(value = 1, message = "Installment number must be at least 1")
    private Integer installmentNumber;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    private LocalDate paidDate;
    
    private FeeInstallment.InstallmentStatus status;
    
    private Fee.PaymentMethod paymentMethod;
    
    @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    private String transactionId;
    
    @Size(max = 100, message = "Receipt number cannot exceed 100 characters")
    private String receiptNumber;
    
    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    private String remarks;
    
    @DecimalMin(value = "0.0", message = "Late fee amount cannot be negative")
    private Double lateFeeAmount;
    
    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private Double discountAmount;
    
    private boolean isWaived;
    
    @Size(max = 500, message = "Waiver reason cannot exceed 500 characters")
    private String waiverReason;
}

