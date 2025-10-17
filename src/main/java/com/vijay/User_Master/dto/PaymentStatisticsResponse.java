package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for payment statistics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatisticsResponse {
    private Long totalPayments;
    private String totalAmount;
    private String currency;
    private Long successfulPayments;
    private Long failedPayments;
    private Long refundedPayments;
    private String successRate;
    private String averageTransactionAmount;
    private String monthlyRevenue;
    private String yearlyRevenue;
}
