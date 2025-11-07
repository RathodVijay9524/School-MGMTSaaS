package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {
    List<WebhookSubscription> findByOwnerIdAndActiveTrueAndEventType(Long ownerId, String eventType);
    List<WebhookSubscription> findByOwnerId(Long ownerId);
}
