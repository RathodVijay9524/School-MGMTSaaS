package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByOwnerIdAndEventType(Long ownerId, String eventType);
    List<AlertRule> findByOwnerId(Long ownerId);
    List<AlertRule> findByOwnerIdAndEventTypeAndActiveTrue(Long ownerId, String eventType);
    List<AlertRule> findByOwnerIdAndActiveTrue(Long ownerId);
    List<AlertRule> findByOwnerIdAndEventTypeAndMutedUntilAfter(Long ownerId, String eventType, LocalDateTime now);
}
