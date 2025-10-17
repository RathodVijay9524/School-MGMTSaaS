package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vijay.User_Master.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for Subscription response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long id;
    private Long ownerId;
    private String ownerName;
    private String schoolName;
    
    // Plan Details
    private Subscription.SubscriptionPlan plan;
    private String planName;
    private String planDescription;
    private BigDecimal planPrice;
    private Subscription.SubscriptionStatus status;
    private String statusDisplay;
    
    // Dates
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private LocalDate trialEndDate;
    
    // Payment Details
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySubscriptionId;
    private String razorpayMandateId;
    private BigDecimal amount;
    private String currency;
    
    // UPI Details
    private String upiId;
    private String upiMandateId;
    @JsonProperty("upiAutoPayEnabled")
    private boolean upiAutoPayEnabled;
    private LocalDate upiMandateExpiry;
    
    // Billing
    private Integer billingCycle;
    @JsonProperty("autoRenew")
    private boolean autoRenew;
    private Integer maxStudents;
    private Integer maxTeachers;
    private Subscription.PaymentMethod paymentMethod;
    private String paymentMethodDisplay;
    
    // Features
    @JsonProperty("aiChatbotEnabled")
    private boolean aiChatbotEnabled;
    @JsonProperty("smsIntegrationEnabled")
    private boolean smsIntegrationEnabled;
    @JsonProperty("whatsappIntegrationEnabled")
    private boolean whatsappIntegrationEnabled;
    @JsonProperty("advancedAnalyticsEnabled")
    private boolean advancedAnalyticsEnabled;
    @JsonProperty("customFieldsEnabled")
    private boolean customFieldsEnabled;
    @JsonProperty("transportManagementEnabled")
    private boolean transportManagementEnabled;
    @JsonProperty("hostelManagementEnabled")
    private boolean hostelManagementEnabled;
    
    // Usage Tracking
    private Integer aiChatbotQueriesUsed;
    private Integer smsSent;
    private Integer whatsappMessagesSent;
    private Integer aiQueriesPerDay;
    private Integer aiQueriesRemaining;
    
    // Computed Fields
    @JsonProperty("isActive")
    private boolean isActive;
    @JsonProperty("isExpired")
    private boolean isExpired;
    @JsonProperty("isTrialActive")
    private boolean isTrialActive;
    private Integer daysUntilExpiry;
    private String expiryStatus; // e.g., "Expires in 15 days", "Expired 5 days ago"
    
    // Audit Fields
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;
    
    // Additional computed fields
    private String featuresSummary; // e.g., "AI Chatbot, SMS Integration, Advanced Analytics"
    private String paymentHistory;
    private BigDecimal totalPaid;
    private Integer totalPayments;
    private LocalDateTime lastPaymentDate;
}
