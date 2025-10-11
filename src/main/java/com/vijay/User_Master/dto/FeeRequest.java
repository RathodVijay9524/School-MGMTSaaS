package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Fee;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeeRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Fee type is required")
    private Fee.FeeType feeType;
    
    @NotBlank(message = "Fee category is required")
    private String feeCategory;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;
    
    @PositiveOrZero(message = "Paid amount must be zero or positive")
    private Double paidAmount;
    
    @PositiveOrZero(message = "Discount amount must be zero or positive")
    private Double discountAmount;
    
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
    
    private LocalDate paymentDate;
    
    private Fee.PaymentMethod paymentMethod;
    
    private String transactionId;
    
    @NotBlank(message = "Academic year is required")
    private String academicYear;
    
    private String semester;
    
    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    private String remarks;
    
    private Double lateFeeAmount;
    
    private String lateFeeReason;
    
    private boolean isWaived;
    
    private String waiverReason;
    
    private Long collectedByUserId;
}

