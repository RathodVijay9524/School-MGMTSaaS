package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription statistics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionStatisticsResponse {
    private Long totalSubscriptions;
    private Long activeSubscriptions;
    private Long expiredSubscriptions;
    private Long trialSubscriptions;
    private String totalRevenue;
    private String monthlyRevenue;
    private String yearlyRevenue;
    private String currency;
    private String averageSubscriptionValue;
    private String renewalRate;
    private String churnRate;
}
