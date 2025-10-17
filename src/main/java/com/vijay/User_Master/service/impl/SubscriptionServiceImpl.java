package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.Subscription;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.SubscriptionRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Subscription management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Override
    public SubscriptionResponse createSubscription(SubscriptionRequest request, Long ownerId) {
        log.info("Creating subscription for owner: {}", ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        // Check if owner already has an active subscription
        Subscription existingSubscription = subscriptionRepository
                .findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Subscription.SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (existingSubscription != null) {
            log.warn("Owner {} already has an active subscription", ownerId);
            throw new IllegalArgumentException("Owner already has an active subscription");
        }

        Subscription subscription = Subscription.builder()
                .owner(owner)
                .plan(request.getPlan())
                .billingCycle(request.getBillingCycle())
                .autoRenew(request.isAutoRenew())
                .upiId(request.getUpiId())
                .upiAutoPayEnabled(request.isUpiAutoPayEnabled())
                .amount(request.getPlan().getPrice())
                .currency("INR")
                .status(request.isStartTrial() ? Subscription.SubscriptionStatus.TRIAL : Subscription.SubscriptionStatus.PENDING_PAYMENT)
                .build();

        // Set trial period if requested
        if (request.isStartTrial()) {
            subscription.setTrialEndDate(LocalDate.now().plusDays(request.getTrialDays()));
            subscription.setEndDate(subscription.getTrialEndDate());
        }

        // Set features based on plan
        subscription.setAiChatbotEnabled(request.getPlan().isAiChatbot());
        subscription.setSmsIntegrationEnabled(request.getPlan().isSmsIntegration());
        subscription.setWhatsappIntegrationEnabled(request.getPlan().isWhatsappIntegration());
        subscription.setAdvancedAnalyticsEnabled(request.getPlan().isAdvancedAnalytics());
        subscription.setCustomFieldsEnabled(request.getPlan().isCustomFields());
        subscription.setTransportManagementEnabled(request.getPlan().isTransportHostel());
        subscription.setHostelManagementEnabled(request.getPlan().isTransportHostel());

        // Set limits
        subscription.setMaxStudents(request.getPlan().getMaxStudents());
        subscription.setMaxTeachers(request.getPlan().getMaxTeachers());

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription created successfully: {}", savedSubscription.getId());

        return convertToResponse(savedSubscription);
    }

    @Override
    public SubscriptionResponse updateSubscription(Long id, SubscriptionRequest request, Long ownerId) {
        log.info("Updating subscription: {} for owner: {}", id, ownerId);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));

        if (!subscription.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Subscription does not belong to owner");
        }

        if (subscription.isDeleted()) {
            throw new IllegalArgumentException("Subscription is deleted");
        }

        // Update subscription details
        subscription.setPlan(request.getPlan());
        subscription.setBillingCycle(request.getBillingCycle());
        subscription.setAutoRenew(request.isAutoRenew());
        subscription.setUpiId(request.getUpiId());
        subscription.setUpiAutoPayEnabled(request.isUpiAutoPayEnabled());
        subscription.setAmount(request.getPlan().getPrice());

        // Update features based on new plan
        subscription.setAiChatbotEnabled(request.getPlan().isAiChatbot());
        subscription.setSmsIntegrationEnabled(request.getPlan().isSmsIntegration());
        subscription.setWhatsappIntegrationEnabled(request.getPlan().isWhatsappIntegration());
        subscription.setAdvancedAnalyticsEnabled(request.getPlan().isAdvancedAnalytics());
        subscription.setCustomFieldsEnabled(request.getPlan().isCustomFields());
        subscription.setTransportManagementEnabled(request.getPlan().isTransportHostel());
        subscription.setHostelManagementEnabled(request.getPlan().isTransportHostel());

        // Update limits
        subscription.setMaxStudents(request.getPlan().getMaxStudents());
        subscription.setMaxTeachers(request.getPlan().getMaxTeachers());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription updated successfully: {}", updatedSubscription.getId());

        return convertToResponse(updatedSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(Long id, Long ownerId) {
        log.info("Getting subscription: {} for owner: {}", id, ownerId);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));

        if (!subscription.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Subscription does not belong to owner");
        }

        if (subscription.isDeleted()) {
            throw new IllegalArgumentException("Subscription is deleted");
        }

        return convertToResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getAllSubscriptions(Long ownerId, Pageable pageable) {
        log.info("Getting all subscriptions for owner: {}", ownerId);

        Page<Subscription> subscriptions = subscriptionRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);

        return subscriptions.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getActiveSubscription(Long ownerId) {
        log.info("Getting active subscription for owner: {}", ownerId);

        Subscription subscription = subscriptionRepository
                .findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Subscription.SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (subscription == null) {
            return null;
        }

        return convertToResponse(subscription);
    }

    @Override
    public SubscriptionResponse cancelSubscription(Long id, Long ownerId) {
        log.info("Cancelling subscription: {} for owner: {}", id, ownerId);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));

        if (!subscription.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Subscription does not belong to owner");
        }

        if (subscription.isDeleted()) {
            throw new IllegalArgumentException("Subscription is deleted");
        }

        subscription.cancelSubscription();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription cancelled successfully: {}", updatedSubscription.getId());

        return convertToResponse(updatedSubscription);
    }

    @Override
    public SubscriptionResponse renewSubscription(Long id, Long ownerId) {
        log.info("Renewing subscription: {} for owner: {}", id, ownerId);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));

        if (!subscription.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Subscription does not belong to owner");
        }

        if (subscription.isDeleted()) {
            throw new IllegalArgumentException("Subscription is deleted");
        }

        subscription.renewSubscription();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription renewed successfully: {}", updatedSubscription.getId());

        return convertToResponse(updatedSubscription);
    }

    @Override
    public SubscriptionResponse upgradeSubscription(Long id, Subscription.SubscriptionPlan newPlan, Long ownerId) {
        log.info("Upgrading subscription: {} to plan: {} for owner: {}", id, newPlan, ownerId);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));

        if (!subscription.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Subscription does not belong to owner");
        }

        if (subscription.isDeleted()) {
            throw new IllegalArgumentException("Subscription is deleted");
        }

        if (!subscription.canUpgrade(newPlan)) {
            throw new IllegalArgumentException("Cannot upgrade to a lower or same plan");
        }

        // Calculate upgrade price
        BigDecimal upgradePrice = subscription.getUpgradePrice(newPlan);
        subscription.setAmount(subscription.getAmount().add(upgradePrice));

        // Update plan and features
        subscription.setPlan(newPlan);
        subscription.setAiChatbotEnabled(newPlan.isAiChatbot());
        subscription.setSmsIntegrationEnabled(newPlan.isSmsIntegration());
        subscription.setWhatsappIntegrationEnabled(newPlan.isWhatsappIntegration());
        subscription.setAdvancedAnalyticsEnabled(newPlan.isAdvancedAnalytics());
        subscription.setCustomFieldsEnabled(newPlan.isCustomFields());
        subscription.setTransportManagementEnabled(newPlan.isTransportHostel());
        subscription.setHostelManagementEnabled(newPlan.isTransportHostel());

        // Update limits
        subscription.setMaxStudents(newPlan.getMaxStudents());
        subscription.setMaxTeachers(newPlan.getMaxTeachers());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription upgraded successfully: {}", updatedSubscription.getId());

        return convertToResponse(updatedSubscription);
    }

    @Override
    public SubscriptionResponse activateSubscription(Long subscriptionId) {
        log.info("Activating subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));

        subscription.activateSubscription();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription activated successfully: {}", updatedSubscription.getId());

        return convertToResponse(updatedSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getSubscriptionsExpiringSoon(Long ownerId, int days, Pageable pageable) {
        log.info("Getting subscriptions expiring soon for owner: {} within {} days", ownerId, days);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(days);

        Page<Subscription> subscriptions = subscriptionRepository
                .findSubscriptionsExpiringSoon(ownerId, startDate, endDate, pageable);

        return subscriptions.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getExpiredSubscriptions(Long ownerId, Pageable pageable) {
        log.info("Getting expired subscriptions for owner: {}", ownerId);

        Page<Subscription> subscriptions = subscriptionRepository.findExpiredSubscriptions(ownerId, pageable);

        return subscriptions.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getTrialSubscriptionsExpiringSoon(Long ownerId, int days, Pageable pageable) {
        log.info("Getting trial subscriptions expiring soon for owner: {} within {} days", ownerId, days);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(days);

        Page<Subscription> subscriptions = subscriptionRepository
                .findTrialSubscriptionsExpiringSoon(ownerId, startDate, endDate, pageable);

        return subscriptions.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionStatisticsResponse getSubscriptionStatistics(Long ownerId) {
        log.info("Getting subscription statistics for owner: {}", ownerId);

        // This is a simplified implementation
        // In production, you would aggregate data from the database
        return SubscriptionStatisticsResponse.builder()
                .totalSubscriptions(10L)
                .activeSubscriptions(8L)
                .expiredSubscriptions(2L)
                .trialSubscriptions(1L)
                .totalRevenue("89990")
                .monthlyRevenue("8999")
                .yearlyRevenue("107988")
                .currency("INR")
                .averageSubscriptionValue("8999")
                .renewalRate("90.0%")
                .churnRate("10.0%")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUseFeature(Long ownerId, String featureName) {
        log.info("Checking if owner: {} can use feature: {}", ownerId, featureName);

        Subscription activeSubscription = subscriptionRepository
                .findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Subscription.SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (activeSubscription == null) {
            return false;
        }

        switch (featureName.toLowerCase()) {
            case "ai_chatbot":
                return activeSubscription.isAiChatbotEnabled();
            case "sms_integration":
                return activeSubscription.isSmsIntegrationEnabled();
            case "whatsapp_integration":
                return activeSubscription.isWhatsappIntegrationEnabled();
            case "advanced_analytics":
                return activeSubscription.isAdvancedAnalyticsEnabled();
            case "custom_fields":
                return activeSubscription.isCustomFieldsEnabled();
            case "transport_management":
                return activeSubscription.isTransportManagementEnabled();
            case "hostel_management":
                return activeSubscription.isHostelManagementEnabled();
            default:
                return false;
        }
    }

    @Override
    public void trackFeatureUsage(Long ownerId, String featureName) {
        log.info("Tracking feature usage for owner: {} feature: {}", ownerId, featureName);

        Subscription activeSubscription = subscriptionRepository
                .findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Subscription.SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (activeSubscription == null) {
            return;
        }

        switch (featureName.toLowerCase()) {
            case "ai_chatbot":
                activeSubscription.setAiChatbotQueriesUsed(
                        activeSubscription.getAiChatbotQueriesUsed() + 1);
                break;
            case "sms_integration":
                activeSubscription.setSmsSent(activeSubscription.getSmsSent() + 1);
                break;
            case "whatsapp_integration":
                activeSubscription.setWhatsappMessagesSent(
                        activeSubscription.getWhatsappMessagesSent() + 1);
                break;
        }

        subscriptionRepository.save(activeSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public FeatureUsageResponse getFeatureUsage(Long ownerId) {
        log.info("Getting feature usage for owner: {}", ownerId);

        Subscription activeSubscription = subscriptionRepository
                .findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Subscription.SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (activeSubscription == null) {
            return FeatureUsageResponse.builder()
                    .aiChatbotQueriesUsed(0)
                    .aiChatbotQueriesRemaining(0)
                    .smsSent(0)
                    .whatsappMessagesSent(0)
                    .maxStudents(0)
                    .currentStudents(0)
                    .maxTeachers(0)
                    .currentTeachers(0)
                    .usageSummary("No active subscription")
                    .build();
        }

        int queriesRemaining = activeSubscription.getPlan().getAiQueriesPerDay() - 
                              activeSubscription.getAiChatbotQueriesUsed();

        return FeatureUsageResponse.builder()
                .aiChatbotQueriesUsed(activeSubscription.getAiChatbotQueriesUsed())
                .aiChatbotQueriesRemaining(Math.max(0, queriesRemaining))
                .smsSent(activeSubscription.getSmsSent())
                .whatsappMessagesSent(activeSubscription.getWhatsappMessagesSent())
                .maxStudents(activeSubscription.getMaxStudents())
                .currentStudents(0) // This would be calculated from actual student count
                .maxTeachers(activeSubscription.getMaxTeachers())
                .currentTeachers(0) // This would be calculated from actual teacher count
                .usageSummary(String.format("AI Queries: %d/%d, SMS: %d, WhatsApp: %d",
                        activeSubscription.getAiChatbotQueriesUsed(),
                        activeSubscription.getPlan().getAiQueriesPerDay(),
                        activeSubscription.getSmsSent(),
                        activeSubscription.getWhatsappMessagesSent()))
                .build();
    }

    @Override
    public SubscriptionResponse setupUPIAutoPay(Long subscriptionId, String upiId, Long ownerId) {
        log.info("Setting up UPI AutoPay for subscription: {} with UPI ID: {}", subscriptionId, upiId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));

        if (!subscription.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Subscription does not belong to owner");
        }

        subscription.setUpiId(upiId);
        subscription.setUpiAutoPayEnabled(true);
        subscription.setUpiMandateExpiry(LocalDate.now().plusYears(1)); // Default 1 year mandate

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        log.info("UPI AutoPay setup successfully for subscription: {}", updatedSubscription.getId());

        return convertToResponse(updatedSubscription);
    }

    @Override
    public SubscriptionResponse cancelUPIAutoPay(Long subscriptionId, Long ownerId) {
        log.info("Cancelling UPI AutoPay for subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));

        if (!subscription.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Subscription does not belong to owner");
        }

        subscription.setUpiAutoPayEnabled(false);
        subscription.setUpiMandateId(null);
        subscription.setUpiMandateExpiry(null);

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        log.info("UPI AutoPay cancelled successfully for subscription: {}", updatedSubscription.getId());

        return convertToResponse(updatedSubscription);
    }

    @Override
    public void processSubscriptionRenewal(Long subscriptionId) {
        log.info("Processing subscription renewal: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));

        if (subscription.isAutoRenew() && subscription.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
            subscription.renewSubscription();
            subscriptionRepository.save(subscription);
            log.info("Subscription renewed successfully: {}", subscriptionId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionByOrderId(String orderId) {
        log.info("Getting subscription by order ID: {}", orderId);

        Subscription subscription = subscriptionRepository
                .findByRazorpayOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "razorpayOrderId", orderId));

        return convertToResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionByPaymentId(String paymentId) {
        log.info("Getting subscription by payment ID: {}", paymentId);

        Subscription subscription = subscriptionRepository
                .findByRazorpayPaymentIdAndIsDeletedFalse(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "razorpayPaymentId", paymentId));

        return convertToResponse(subscription);
    }

    // Helper method to convert entity to response DTO
    private SubscriptionResponse convertToResponse(Subscription subscription) {
        // Calculate computed fields
        boolean isActive = subscription.isActive();
        boolean isExpired = subscription.isExpired();
        boolean isTrialActive = subscription.isTrialActive();
        int daysUntilExpiry = subscription.getDaysUntilExpiry();

        String expiryStatus;
        if (isExpired) {
            expiryStatus = "Expired " + Math.abs(daysUntilExpiry) + " days ago";
        } else if (daysUntilExpiry == -1) {
            expiryStatus = "Never expires";
        } else if (daysUntilExpiry <= 7) {
            expiryStatus = "Expires in " + daysUntilExpiry + " days (Soon!)";
        } else {
            expiryStatus = "Expires in " + daysUntilExpiry + " days";
        }

        // Build features summary
        List<String> features = List.of();
        if (subscription.isAiChatbotEnabled()) features.add("AI Chatbot");
        if (subscription.isSmsIntegrationEnabled()) features.add("SMS Integration");
        if (subscription.isWhatsappIntegrationEnabled()) features.add("WhatsApp Integration");
        if (subscription.isAdvancedAnalyticsEnabled()) features.add("Advanced Analytics");
        if (subscription.isCustomFieldsEnabled()) features.add("Custom Fields");
        if (subscription.isTransportManagementEnabled()) features.add("Transport Management");
        if (subscription.isHostelManagementEnabled()) features.add("Hostel Management");

        String featuresSummary = String.join(", ", features);

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .ownerId(subscription.getOwner().getId())
                .ownerName(subscription.getOwner().getUsername())
                .schoolName(subscription.getOwner().getUsername()) // Using username as school name for now
                .plan(subscription.getPlan())
                .planName(subscription.getPlan().getDescription())
                .planDescription(subscription.getPlan().getDescription())
                .planPrice(subscription.getPlan().getPrice())
                .status(subscription.getStatus())
                .statusDisplay(subscription.getStatus().toString())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .nextBillingDate(subscription.getNextBillingDate())
                .trialEndDate(subscription.getTrialEndDate())
                .razorpayOrderId(subscription.getRazorpayOrderId())
                .razorpayPaymentId(subscription.getRazorpayPaymentId())
                .razorpaySubscriptionId(subscription.getRazorpaySubscriptionId())
                .razorpayMandateId(subscription.getRazorpayMandateId())
                .amount(subscription.getAmount())
                .currency(subscription.getCurrency())
                .upiId(subscription.getUpiId())
                .upiMandateId(subscription.getUpiMandateId())
                .upiAutoPayEnabled(subscription.isUpiAutoPayEnabled())
                .upiMandateExpiry(subscription.getUpiMandateExpiry())
                .billingCycle(subscription.getBillingCycle())
                .autoRenew(subscription.isAutoRenew())
                .maxStudents(subscription.getMaxStudents())
                .maxTeachers(subscription.getMaxTeachers())
                .paymentMethod(subscription.getPaymentMethod())
                .paymentMethodDisplay(subscription.getPaymentMethod() != null ? 
                        subscription.getPaymentMethod().toString() : "N/A")
                .aiChatbotEnabled(subscription.isAiChatbotEnabled())
                .smsIntegrationEnabled(subscription.isSmsIntegrationEnabled())
                .whatsappIntegrationEnabled(subscription.isWhatsappIntegrationEnabled())
                .advancedAnalyticsEnabled(subscription.isAdvancedAnalyticsEnabled())
                .customFieldsEnabled(subscription.isCustomFieldsEnabled())
                .transportManagementEnabled(subscription.isTransportManagementEnabled())
                .hostelManagementEnabled(subscription.isHostelManagementEnabled())
                .aiChatbotQueriesUsed(subscription.getAiChatbotQueriesUsed())
                .smsSent(subscription.getSmsSent())
                .whatsappMessagesSent(subscription.getWhatsappMessagesSent())
                .aiQueriesPerDay(subscription.getPlan().getAiQueriesPerDay())
                .aiQueriesRemaining(subscription.getPlan().getAiQueriesPerDay() - 
                        subscription.getAiChatbotQueriesUsed())
                .isActive(isActive)
                .isExpired(isExpired)
                .isTrialActive(isTrialActive)
                .daysUntilExpiry(daysUntilExpiry)
                .expiryStatus(expiryStatus)
                .isDeleted(subscription.isDeleted())
                .createdOn(subscription.getCreatedOn())
                .updatedOn(subscription.getUpdatedOn())
                .featuresSummary(featuresSummary)
                .paymentHistory(subscription.getPaymentHistory())
                .totalPaid(subscription.getAmount())
                .totalPayments(1)
                .lastPaymentDate(subscription.getCreatedOn() != null ? 
                        subscription.getCreatedOn().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }
}
