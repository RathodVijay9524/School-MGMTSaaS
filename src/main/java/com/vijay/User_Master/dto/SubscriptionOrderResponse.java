package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription order response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionOrderResponse {

    private String orderId;
    private String amount;
    private String currency;
    private String keyId; // Razorpay key for frontend
    private String receipt;
    private String status;
    private String planName;
    private String schoolName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String notes;
    
    // Additional Razorpay response fields
    private String createdAt;
    private String updatedAt;
}
