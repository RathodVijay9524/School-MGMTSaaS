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
 * DTO for creating subscription payment order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionOrderRequest {

    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;

    @NotNull(message = "Plan is required")
    private Subscription.SubscriptionPlan plan;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name must not exceed 100 characters")
    private String planName;

    @NotBlank(message = "School name is required")
    @Size(max = 200, message = "School name must not exceed 200 characters")
    private String schoolName;

    @NotNull(message = "Payment method is required")
    private Subscription.PaymentMethod paymentMethod;

    @Min(value = 1, message = "Billing cycle must be at least 1 month")
    @Max(value = 12, message = "Billing cycle cannot exceed 12 months")
    @Builder.Default
    private Integer billingCycle = 1; // Default to monthly

    @Builder.Default
    private boolean autoRenew = true;

    // UPI specific fields
    @Size(max = 100, message = "UPI ID must not exceed 100 characters")
    private String upiId; // user@paytm, user@okaxis

    @Builder.Default
    private boolean upiAutoPayEnabled = false;

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

    // Additional notes
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
