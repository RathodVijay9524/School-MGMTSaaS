package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SubscriptionRequest;
import com.vijay.User_Master.dto.SubscriptionResponse;
import com.vijay.User_Master.dto.SubscriptionStatisticsResponse;
import com.vijay.User_Master.dto.FeatureUsageResponse;
import com.vijay.User_Master.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Subscription management
 */
public interface SubscriptionService {

    /**
     * Create a new subscription
     */
    SubscriptionResponse createSubscription(SubscriptionRequest request, Long ownerId);

    /**
     * Update an existing subscription
     */
    SubscriptionResponse updateSubscription(Long id, SubscriptionRequest request, Long ownerId);

    /**
     * Get subscription by ID
     */
    SubscriptionResponse getSubscriptionById(Long id, Long ownerId);

    /**
     * Get all subscriptions for owner
     */
    Page<SubscriptionResponse> getAllSubscriptions(Long ownerId, Pageable pageable);

    /**
     * Get active subscription for owner
     */
    SubscriptionResponse getActiveSubscription(Long ownerId);

    /**
     * Cancel subscription
     */
    SubscriptionResponse cancelSubscription(Long id, Long ownerId);

    /**
     * Renew subscription
     */
    SubscriptionResponse renewSubscription(Long id, Long ownerId);

    /**
     * Upgrade subscription plan
     */
    SubscriptionResponse upgradeSubscription(Long id, Subscription.SubscriptionPlan newPlan, Long ownerId);

    /**
     * Activate subscription after payment
     */
    SubscriptionResponse activateSubscription(Long subscriptionId);

    /**
     * Get subscriptions expiring soon
     */
    Page<SubscriptionResponse> getSubscriptionsExpiringSoon(Long ownerId, int days, Pageable pageable);

    /**
     * Get expired subscriptions
     */
    Page<SubscriptionResponse> getExpiredSubscriptions(Long ownerId, Pageable pageable);

    /**
     * Get trial subscriptions expiring soon
     */
    Page<SubscriptionResponse> getTrialSubscriptionsExpiringSoon(Long ownerId, int days, Pageable pageable);

    /**
     * Get subscription statistics
     */
    SubscriptionStatisticsResponse getSubscriptionStatistics(Long ownerId);

    /**
     * Check if owner can use a feature
     */
    boolean canUseFeature(Long ownerId, String featureName);

    /**
     * Track feature usage
     */
    void trackFeatureUsage(Long ownerId, String featureName);

    /**
     * Get feature usage statistics
     */
    FeatureUsageResponse getFeatureUsage(Long ownerId);

    /**
     * Setup UPI AutoPay
     */
    SubscriptionResponse setupUPIAutoPay(Long subscriptionId, String upiId, Long ownerId);

    /**
     * Cancel UPI AutoPay
     */
    SubscriptionResponse cancelUPIAutoPay(Long subscriptionId, Long ownerId);

    /**
     * Process subscription renewal
     */
    void processSubscriptionRenewal(Long subscriptionId);

    /**
     * Get subscription by Razorpay order ID
     */
    SubscriptionResponse getSubscriptionByOrderId(String orderId);

    /**
     * Get subscription by Razorpay payment ID
     */
    SubscriptionResponse getSubscriptionByPaymentId(String paymentId);
}
