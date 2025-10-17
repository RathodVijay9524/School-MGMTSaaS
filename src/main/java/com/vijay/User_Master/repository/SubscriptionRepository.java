package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Subscription entity
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Find subscription by owner
    Page<Subscription> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);

    // Find active subscription for owner
    Optional<Subscription> findByOwner_IdAndStatusAndIsDeletedFalse(Long ownerId, Subscription.SubscriptionStatus status);

    // Find subscription by Razorpay order ID
    Optional<Subscription> findByRazorpayOrderIdAndIsDeletedFalse(String razorpayOrderId);

    // Find subscription by Razorpay payment ID
    Optional<Subscription> findByRazorpayPaymentIdAndIsDeletedFalse(String razorpayPaymentId);

    // Find subscription by Razorpay subscription ID
    Optional<Subscription> findByRazorpaySubscriptionIdAndIsDeletedFalse(String razorpaySubscriptionId);

    // Find subscriptions expiring soon
    @Query("SELECT s FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE AND s.endDate BETWEEN :startDate AND :endDate")
    Page<Subscription> findSubscriptionsExpiringSoon(@Param("ownerId") Long ownerId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate, 
                                                   Pageable pageable);

    // Find expired subscriptions
    @Query("SELECT s FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE AND s.endDate < CURRENT_DATE AND s.status = 'ACTIVE'")
    Page<Subscription> findExpiredSubscriptions(@Param("ownerId") Long ownerId, Pageable pageable);

    // Find subscriptions by plan
    Page<Subscription> findByOwner_IdAndPlanAndIsDeletedFalse(Long ownerId, Subscription.SubscriptionPlan plan, Pageable pageable);

    // Find subscriptions by status
    Page<Subscription> findByOwner_IdAndStatusAndIsDeletedFalse(Long ownerId, Subscription.SubscriptionStatus status, Pageable pageable);

    // Find subscriptions with UPI AutoPay enabled
    Page<Subscription> findByOwner_IdAndUpiAutoPayEnabledTrueAndIsDeletedFalse(Long ownerId, Pageable pageable);

    // Find subscriptions due for renewal
    @Query("SELECT s FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE AND s.nextBillingDate <= :date AND s.autoRenew = TRUE AND s.status = 'ACTIVE'")
    List<Subscription> findSubscriptionsDueForRenewal(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);

    // Count active subscriptions by plan
    long countByOwner_IdAndPlanAndStatusAndIsDeletedFalse(Long ownerId, Subscription.SubscriptionPlan plan, Subscription.SubscriptionStatus status);

    // Find trial subscriptions expiring soon
    @Query("SELECT s FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE AND s.status = 'TRIAL' AND s.trialEndDate BETWEEN :startDate AND :endDate")
    Page<Subscription> findTrialSubscriptionsExpiringSoon(@Param("ownerId") Long ownerId, 
                                                         @Param("startDate") LocalDate startDate, 
                                                         @Param("endDate") LocalDate endDate, 
                                                         Pageable pageable);

    // Find subscriptions by payment method
    Page<Subscription> findByOwner_IdAndPaymentMethodAndIsDeletedFalse(Long ownerId, Subscription.PaymentMethod paymentMethod, Pageable pageable);

    // Get subscription statistics for owner
    @Query("SELECT s.plan, COUNT(s) FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE GROUP BY s.plan")
    List<Object[]> getSubscriptionStatisticsByPlan(@Param("ownerId") Long ownerId);

    // Find subscriptions with specific features enabled
    @Query("SELECT s FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE AND s.aiChatbotEnabled = TRUE")
    Page<Subscription> findSubscriptionsWithAiChatbot(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE AND s.smsIntegrationEnabled = TRUE")
    Page<Subscription> findSubscriptionsWithSmsIntegration(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE s.owner.id = :ownerId AND s.isDeleted = FALSE AND s.whatsappIntegrationEnabled = TRUE")
    Page<Subscription> findSubscriptionsWithWhatsappIntegration(@Param("ownerId") Long ownerId, Pageable pageable);
}
