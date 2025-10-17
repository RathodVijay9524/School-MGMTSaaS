package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.Subscription;

import java.util.List;

/**
 * Service interface for Razorpay payment operations
 */
public interface RazorpayPaymentService {

    /**
     * Create a payment order for subscription
     */
    SubscriptionOrderResponse createSubscriptionOrder(SubscriptionOrderRequest request);

    /**
     * Verify payment signature and process payment
     */
    boolean verifyPayment(String orderId, String paymentId, String signature);

    /**
     * Create UPI mandate for recurring payments
     */
    UPIMandateResponse createUPIMandate(UPIMandateRequest request);

    /**
     * Charge subscription using UPI AutoPay
     */
    PaymentResponse chargeSubscription(String subscriptionId);

    /**
     * Cancel UPI mandate
     */
    boolean cancelUPIMandate(String mandateId);

    /**
     * Get payment details by payment ID
     */
    PaymentResponse getPaymentDetails(String paymentId);

    /**
     * Refund payment
     */
    RefundResponse refundPayment(String paymentId, Long amount, String reason);

    /**
     * Get refund details
     */
    RefundResponse getRefundDetails(String refundId);

    /**
     * Handle Razorpay webhook events
     */
    void handleWebhookEvent(String eventType, String payload);

    /**
     * Generate payment receipt
     */
    String generateReceipt(String paymentId);

    /**
     * Get payment statistics
     */
    PaymentStatisticsResponse getPaymentStatistics(Long ownerId);
}
