package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vijay.User_Master.entity.Subscription;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating/updating subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotNull(message = "Plan is required")
    private Subscription.SubscriptionPlan plan;

    @Min(value = 1, message = "Billing cycle must be at least 1 month")
    @Max(value = 12, message = "Billing cycle cannot exceed 12 months")
    @Builder.Default
    private Integer billingCycle = 1; // Default to monthly

    @Builder.Default
    private boolean autoRenew = true;

    // UPI AutoPay settings
    @Size(max = 100, message = "UPI ID must not exceed 100 characters")
    private String upiId; // user@paytm, user@okaxis

    @Builder.Default
    private boolean upiAutoPayEnabled = false;

    // Trial settings
    @Builder.Default
    private boolean startTrial = false;

    @Min(value = 1, message = "Trial days must be at least 1")
    @Max(value = 30, message = "Trial days cannot exceed 30")
    @Builder.Default
    private Integer trialDays = 14; // Default 14 days trial

    // Customer details
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String customerEmail;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String customerPhone;

    // School details
    @NotBlank(message = "School name is required")
    @Size(max = 200, message = "School name must not exceed 200 characters")
    private String schoolName;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    // Payment preferences
    private Subscription.PaymentMethod preferredPaymentMethod;
}
