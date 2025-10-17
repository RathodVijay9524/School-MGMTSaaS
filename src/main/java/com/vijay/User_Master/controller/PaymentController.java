package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.service.RazorpayPaymentService;
import com.vijay.User_Master.service.SubscriptionService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Payment Gateway operations
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Gateway", description = "APIs for payment processing and subscription management")
public class PaymentController {

    private final RazorpayPaymentService razorpayPaymentService;
    private final SubscriptionService subscriptionService;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // ==================== SUBSCRIPTION MANAGEMENT ====================

    /**
     * Create a new subscription
     */
    @PostMapping("/subscriptions")
    @Operation(summary = "Create subscription", description = "Create a new subscription for the school")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createSubscription(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Creating subscription for plan: {}", request.getPlan());
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.createSubscription(request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    /**
     * Get all subscriptions for owner
     */
    @GetMapping("/subscriptions")
    @Operation(summary = "Get subscriptions", description = "Get all subscriptions for the owner")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getAllSubscriptions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdOn") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting all subscriptions - page: {}, size: {}", page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        if (sortDir.equalsIgnoreCase("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        }
        Page<SubscriptionResponse> response = subscriptionService.getAllSubscriptions(ownerId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get active subscription
     */
    @GetMapping("/subscriptions/active")
    @Operation(summary = "Get active subscription", description = "Get the currently active subscription")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getActiveSubscription() {
        log.info("Getting active subscription");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.getActiveSubscription(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get subscription by ID
     */
    @GetMapping("/subscriptions/{id}")
    @Operation(summary = "Get subscription by ID", description = "Get a specific subscription by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id) {
        log.info("Getting subscription: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.getSubscriptionById(id, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Cancel subscription
     */
    @PostMapping("/subscriptions/{id}/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancel an active subscription")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> cancelSubscription(@PathVariable Long id) {
        log.info("Cancelling subscription: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.cancelSubscription(id, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Renew subscription
     */
    @PostMapping("/subscriptions/{id}/renew")
    @Operation(summary = "Renew subscription", description = "Renew an expired subscription")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> renewSubscription(@PathVariable Long id) {
        log.info("Renewing subscription: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.renewSubscription(id, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Upgrade subscription plan
     */
    @PostMapping("/subscriptions/{id}/upgrade")
    @Operation(summary = "Upgrade subscription", description = "Upgrade subscription to a higher plan")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> upgradeSubscription(
            @PathVariable Long id,
            @RequestParam String newPlan) {
        log.info("Upgrading subscription: {} to plan: {}", id, newPlan);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.upgradeSubscription(id, 
                com.vijay.User_Master.entity.Subscription.SubscriptionPlan.valueOf(newPlan), ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    // ==================== PAYMENT PROCESSING ====================

    /**
     * Create payment order for subscription
     */
    @PostMapping("/create-order")
    @Operation(summary = "Create payment order", description = "Create a payment order for subscription")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createPaymentOrder(@Valid @RequestBody SubscriptionOrderRequest request) {
        log.info("Creating payment order for subscription: {}", request.getSubscriptionId());
        SubscriptionOrderResponse response = razorpayPaymentService.createSubscriptionOrder(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Verify payment and activate subscription
     */
    @PostMapping("/verify")
    @Operation(summary = "Verify payment", description = "Verify payment and activate subscription")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        log.info("Verifying payment for order: {}", request.getOrderId());
        
        boolean isValid = razorpayPaymentService.verifyPayment(
            request.getOrderId(), 
            request.getPaymentId(), 
            request.getSignature()
        );
        
        if (isValid) {
            // Activate subscription
            SubscriptionResponse subscriptionResponse = subscriptionService.activateSubscription(request.getSubscriptionId());
            
            // Generate receipt
            String receiptUrl = razorpayPaymentService.generateReceipt(request.getPaymentId());
            
            PaymentVerificationResponse response = PaymentVerificationResponse.builder()
                .success(true)
                .message("Payment successful! Subscription activated.")
                .receiptUrl(receiptUrl)
                .subscriptionId(request.getSubscriptionId().toString())
                .planName(subscriptionResponse.getPlanName())
                .startDate(subscriptionResponse.getStartDate())
                .endDate(subscriptionResponse.getEndDate())
                .nextBillingDate(subscriptionResponse.getNextBillingDate())
                .paymentId(request.getPaymentId())
                .orderId(request.getOrderId())
                .amount(subscriptionResponse.getPlanPrice().toString())
                .currency("INR")
                .paymentMethod(request.getPaymentMethod())
                .schoolName(subscriptionResponse.getSchoolName())
                .customerName(subscriptionResponse.getOwnerName())
                .customerEmail(subscriptionResponse.getOwnerName() + "@school.com") // This should come from user profile
                .autoRenew(subscriptionResponse.isAutoRenew())
                .features(subscriptionResponse.getFeaturesSummary())
                .build();
            
            return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
        } else {
            PaymentVerificationResponse response = PaymentVerificationResponse.builder()
                .success(false)
                .message("Payment verification failed")
                .build();
            return ExceptionUtil.createBuildResponse(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get payment details
     */
    @GetMapping("/details/{paymentId}")
    @Operation(summary = "Get payment details", description = "Get details of a specific payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String paymentId) {
        log.info("Getting payment details: {}", paymentId);
        PaymentResponse response = razorpayPaymentService.getPaymentDetails(paymentId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Process refund
     */
    @PostMapping("/refund")
    @Operation(summary = "Process refund", description = "Process a refund for a payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> processRefund(
            @RequestParam String paymentId,
            @RequestParam Long amount,
            @RequestParam String reason) {
        log.info("Processing refund for payment: {} amount: {}", paymentId, amount);
        RefundResponse response = razorpayPaymentService.refundPayment(paymentId, amount, reason);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    // ==================== UPI AUTOPAY ====================

    /**
     * Setup UPI AutoPay
     */
    @PostMapping("/subscriptions/{id}/upi-autopay")
    @Operation(summary = "Setup UPI AutoPay", description = "Setup UPI AutoPay for recurring payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> setupUPIAutoPay(
            @PathVariable Long id,
            @RequestParam String upiId) {
        log.info("Setting up UPI AutoPay for subscription: {} with UPI ID: {}", id, upiId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.setupUPIAutoPay(id, upiId, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Cancel UPI AutoPay
     */
    @PostMapping("/subscriptions/{id}/cancel-upi-autopay")
    @Operation(summary = "Cancel UPI AutoPay", description = "Cancel UPI AutoPay for recurring payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> cancelUPIAutoPay(@PathVariable Long id) {
        log.info("Cancelling UPI AutoPay for subscription: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionResponse response = subscriptionService.cancelUPIAutoPay(id, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    // ==================== WEBHOOK HANDLING ====================

    /**
     * Handle Razorpay webhook events
     */
    @PostMapping("/webhook")
    @Operation(summary = "Handle webhook", description = "Handle Razorpay webhook events")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        
        log.info("Received webhook event");
        
        try {
            // Verify webhook signature
            // TODO: Implement proper Razorpay signature verification
            String expectedSignature = "mock_signature_" + payload.hashCode();
            
            if (expectedSignature.equals(signature)) {
                // Parse event type from payload
                org.json.JSONObject event = new org.json.JSONObject(payload);
                String eventType = event.getString("event");
                
                // Handle the event
                razorpayPaymentService.handleWebhookEvent(eventType, payload);
                
                return ResponseEntity.ok("Webhook processed successfully");
            } else {
                log.warn("Invalid webhook signature");
                return ResponseEntity.badRequest().body("Invalid signature");
            }
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            return ResponseEntity.status(500).body("Webhook processing failed");
        }
    }

    // ==================== STATISTICS & ANALYTICS ====================

    /**
     * Get subscription statistics
     */
    @GetMapping("/subscriptions/statistics")
    @Operation(summary = "Get subscription statistics", description = "Get subscription statistics for the owner")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getSubscriptionStatistics() {
        log.info("Getting subscription statistics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubscriptionStatisticsResponse response = subscriptionService.getSubscriptionStatistics(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get payment statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get payment statistics", description = "Get payment statistics for the owner")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getPaymentStatistics() {
        log.info("Getting payment statistics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        PaymentStatisticsResponse response = razorpayPaymentService.getPaymentStatistics(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get feature usage
     */
    @GetMapping("/subscriptions/feature-usage")
    @Operation(summary = "Get feature usage", description = "Get feature usage statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getFeatureUsage() {
        log.info("Getting feature usage");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        FeatureUsageResponse response = subscriptionService.getFeatureUsage(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Check if feature can be used
     */
    @GetMapping("/subscriptions/can-use-feature/{featureName}")
    @Operation(summary = "Check feature access", description = "Check if a specific feature can be used")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> canUseFeature(@PathVariable String featureName) {
        log.info("Checking if feature can be used: {}", featureName);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        boolean canUse = subscriptionService.canUseFeature(ownerId, featureName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("featureName", featureName);
        response.put("canUse", canUse);
        response.put("message", canUse ? "Feature is available" : "Feature is not available in current plan");
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    // ==================== SUBSCRIPTION PLANS ====================

    /**
     * Get available subscription plans
     */
    @GetMapping("/plans")
    @Operation(summary = "Get subscription plans", description = "Get all available subscription plans")
    public ResponseEntity<?> getSubscriptionPlans() {
        log.info("Getting subscription plans");
        
        Map<String, Object> plans = new HashMap<>();
        
        for (com.vijay.User_Master.entity.Subscription.SubscriptionPlan plan : 
             com.vijay.User_Master.entity.Subscription.SubscriptionPlan.values()) {
            
            Map<String, Object> planDetails = new HashMap<>();
            planDetails.put("name", plan.name());
            planDetails.put("description", plan.getDescription());
            planDetails.put("price", plan.getPrice());
            planDetails.put("maxStudents", plan.getMaxStudents());
            planDetails.put("maxTeachers", plan.getMaxTeachers());
            planDetails.put("aiQueriesPerDay", plan.getAiQueriesPerDay());
            planDetails.put("features", Map.of(
                "aiChatbot", plan.isAiChatbot(),
                "smsIntegration", plan.isSmsIntegration(),
                "whatsappIntegration", plan.isWhatsappIntegration(),
                "advancedAnalytics", plan.isAdvancedAnalytics(),
                "customFields", plan.isCustomFields(),
                "transportHostel", plan.isTransportHostel()
            ));
            
            plans.put(plan.name(), planDetails);
        }
        
        return ExceptionUtil.createBuildResponse(plans, HttpStatus.OK);
    }
}
