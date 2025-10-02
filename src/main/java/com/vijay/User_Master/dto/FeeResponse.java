package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Fee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeeResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Fee.FeeType feeType;
    private String feeCategory;
    private Double totalAmount;
    private Double paidAmount;
    private Double discountAmount;
    private Double balanceAmount;
    private Fee.PaymentStatus paymentStatus;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Fee.PaymentMethod paymentMethod;
    private String transactionId;
    private String receiptNumber;
    private String academicYear;
    private String semester;
    private String remarks;
    private String lateFeeReason;
    private Double lateFeeAmount;
    private boolean isWaived;
    private String waiverReason;
    private Long collectedByUserId;
    private String collectedByUsername;
    
    // Computed fields
    private String feeTypeDisplay;
    private String paymentStatusDisplay;
    private String paymentMethodDisplay;
    private boolean isOverdue;
    private Integer daysOverdue;
    private Double paymentPercentage;
}

