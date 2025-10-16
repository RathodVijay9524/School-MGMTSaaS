package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.entity.FeeInstallment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for FeeInstallment response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeInstallmentResponse {

    private Long id;
    private Long feeId;
    private Integer installmentNumber;
    private Double amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private LocalDateTime paidAt;
    private FeeInstallment.InstallmentStatus status;
    private Fee.PaymentMethod paymentMethod;
    private String transactionId;
    private String receiptNumber;
    private String remarks;
    private Double lateFeeAmount;
    private Integer daysOverdue;
    private boolean isLatePayment;
    private Double discountAmount;
    private boolean isWaived;
    private String waiverReason;
    private Long collectedByUserId;
    private String collectedByUserName;
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;
    
    // Computed fields
    private boolean isOverdue;
    private boolean isPaid;
    private boolean isPending;
    private String statusDisplay;
    private String paymentMethodDisplay;
    private Double netAmount;  // amount - discountAmount + lateFeeAmount
}

