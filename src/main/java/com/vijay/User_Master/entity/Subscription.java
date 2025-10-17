package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
@EntityListeners(AuditingEntityListener.class)
public class Subscription extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optimistic Locking
    @Version
    private Long version;

    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // School owner

    // Subscription Plan Details
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan; // BASIC, PROFESSIONAL, PREMIUM, ENTERPRISE

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.PENDING_PAYMENT; // ACTIVE, EXPIRED, CANCELLED, PENDING_PAYMENT

    // Subscription Dates
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private LocalDate trialEndDate;

    // Payment Details
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySubscriptionId; // For recurring payments
    private String razorpayMandateId; // For UPI AutoPay

    private BigDecimal amount;
    @Builder.Default
    private String currency = "INR";

    // UPI AutoPay Details
    private String upiId; // user@paytm, user@okaxis
    private String upiMandateId;
    @Builder.Default
    private boolean upiAutoPayEnabled = false;
    private LocalDate upiMandateExpiry;

    // Billing Configuration
    private Integer billingCycle; // 1 = monthly, 3 = quarterly, 12 = annual
    @Builder.Default
    private boolean autoRenew = true;
    private Integer maxStudents; // Based on plan
    private Integer maxTeachers; // Based on plan

    // Payment Method Used
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // UPI, CREDIT_CARD, NET_BANKING, WALLET, UPI_AUTOPAY

    // Features Included
    @Builder.Default
    private boolean aiChatbotEnabled = false;
    @Builder.Default
    private boolean smsIntegrationEnabled = false;
    @Builder.Default
    private boolean whatsappIntegrationEnabled = false;
    @Builder.Default
    private boolean advancedAnalyticsEnabled = false;
    @Builder.Default
    private boolean customFieldsEnabled = false;
    @Builder.Default
    private boolean transportManagementEnabled = false;
    @Builder.Default
    private boolean hostelManagementEnabled = false;

    // Usage Tracking
    @Builder.Default
    private Integer aiChatbotQueriesUsed = 0;
    @Builder.Default
    private Integer smsSent = 0;
    @Builder.Default
    private Integer whatsappMessagesSent = 0;

    // Payment History
    @Column(length = 1000)
    private String paymentHistory; // JSON string of payment history

    // Soft Delete
    @Builder.Default
    private boolean isDeleted = false;

    // Subscription Plans Enum
    public enum SubscriptionPlan {
        BASIC(2999, "Basic Plan", 100, 10, 50, false, false, false, false, false, false),
        PROFESSIONAL(8999, "Professional Plan", 500, 25, 200, true, true, false, true, true, false),
        PREMIUM(19999, "Premium Plan", 1500, 50, 500, true, true, true, true, true, true),
        ENTERPRISE(49999, "Enterprise Plan", -1, -1, -1, true, true, true, true, true, true); // -1 = unlimited

        private final BigDecimal price;
        private final String description;
        private final Integer maxStudents;
        private final Integer maxTeachers;
        private final Integer aiQueriesPerDay;
        private final boolean aiChatbot;
        private final boolean smsIntegration;
        private final boolean whatsappIntegration;
        private final boolean advancedAnalytics;
        private final boolean customFields;
        private final boolean transportHostel;

        SubscriptionPlan(int price, String description, Integer maxStudents, Integer maxTeachers, 
                        Integer aiQueriesPerDay, boolean aiChatbot, boolean smsIntegration, 
                        boolean whatsappIntegration, boolean advancedAnalytics, 
                        boolean customFields, boolean transportHostel) {
            this.price = BigDecimal.valueOf(price);
            this.description = description;
            this.maxStudents = maxStudents;
            this.maxTeachers = maxTeachers;
            this.aiQueriesPerDay = aiQueriesPerDay;
            this.aiChatbot = aiChatbot;
            this.smsIntegration = smsIntegration;
            this.whatsappIntegration = whatsappIntegration;
            this.advancedAnalytics = advancedAnalytics;
            this.customFields = customFields;
            this.transportHostel = transportHostel;
        }

        public BigDecimal getPrice() { return price; }
        public String getDescription() { return description; }
        public Integer getMaxStudents() { return maxStudents; }
        public Integer getMaxTeachers() { return maxTeachers; }
        public Integer getAiQueriesPerDay() { return aiQueriesPerDay; }
        public boolean isAiChatbot() { return aiChatbot; }
        public boolean isSmsIntegration() { return smsIntegration; }
        public boolean isWhatsappIntegration() { return whatsappIntegration; }
        public boolean isAdvancedAnalytics() { return advancedAnalytics; }
        public boolean isCustomFields() { return customFields; }
        public boolean isTransportHostel() { return transportHostel; }
    }

    public enum SubscriptionStatus {
        ACTIVE, EXPIRED, CANCELLED, PENDING_PAYMENT, TRIAL, SUSPENDED
    }

    public enum PaymentMethod {
        UPI, CREDIT_CARD, NET_BANKING, WALLET, UPI_AUTOPAY, DEBIT_CARD
    }

    // Helper Methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && 
               (endDate == null || endDate.isAfter(LocalDate.now()));
    }

    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    public boolean isTrialActive() {
        return status == SubscriptionStatus.TRIAL && 
               trialEndDate != null && trialEndDate.isAfter(LocalDate.now());
    }

    public int getDaysUntilExpiry() {
        if (endDate == null) return -1; // Never expires
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    public boolean canUpgrade(SubscriptionPlan newPlan) {
        return newPlan.getPrice().compareTo(this.plan.getPrice()) > 0;
    }

    public BigDecimal getUpgradePrice(SubscriptionPlan newPlan) {
        return newPlan.getPrice().subtract(this.plan.getPrice());
    }

    public void activateSubscription() {
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = LocalDate.now();
        this.endDate = this.startDate.plusMonths(this.billingCycle);
        this.nextBillingDate = this.endDate;
        
        // Set features based on plan
        this.aiChatbotEnabled = this.plan.isAiChatbot();
        this.smsIntegrationEnabled = this.plan.isSmsIntegration();
        this.whatsappIntegrationEnabled = this.plan.isWhatsappIntegration();
        this.advancedAnalyticsEnabled = this.plan.isAdvancedAnalytics();
        this.customFieldsEnabled = this.plan.isCustomFields();
        this.transportManagementEnabled = this.plan.isTransportHostel();
        this.hostelManagementEnabled = this.plan.isTransportHostel();
        
        // Set limits
        this.maxStudents = this.plan.getMaxStudents();
        this.maxTeachers = this.plan.getMaxTeachers();
    }

    public void cancelSubscription() {
        this.status = SubscriptionStatus.CANCELLED;
        this.autoRenew = false;
        this.endDate = LocalDate.now();
    }

    public void renewSubscription() {
        if (this.status == SubscriptionStatus.ACTIVE) {
            this.endDate = this.endDate.plusMonths(this.billingCycle);
            this.nextBillingDate = this.endDate;
        }
    }
}
