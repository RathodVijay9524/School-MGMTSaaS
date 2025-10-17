package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for UPI mandate creation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UPIMandateRequest {

    private String customerId;
    private String upiId; // user@paytm, user@okaxis
    private Long amount; // Amount in paise
    private Long maxAmount; // Maximum amount per transaction
    private String startDate; // ISO date string
    private String expiryDate; // ISO date string
    private String subscriptionId;
    private String description;
}
