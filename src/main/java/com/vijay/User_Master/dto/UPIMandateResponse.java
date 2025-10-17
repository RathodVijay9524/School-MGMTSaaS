package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for UPI mandate response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UPIMandateResponse {

    private String mandateId;
    private String status;
    private String upiId;
    private Long amount;
    private Long maxAmount;
    private String startDate;
    private String expiryDate;
    private String description;
    private String subscriptionId;
    private String createdAt;
    private String updatedAt;
}
