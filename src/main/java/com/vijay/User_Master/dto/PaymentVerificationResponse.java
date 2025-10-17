package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for payment verification response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponse {

    private boolean success;
    private String message;
    private String receiptUrl;
    private String subscriptionId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private String paymentId;
    private String orderId;
    private String amount;
    private String currency;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    
    // Additional details
    private String schoolName;
    private String customerName;
    private String customerEmail;
    private boolean autoRenew;
    private String features; // JSON string of enabled features
}
