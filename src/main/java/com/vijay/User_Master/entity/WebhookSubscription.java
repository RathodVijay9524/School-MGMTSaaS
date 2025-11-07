package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_subscriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    // e.g., RUN_STEP_FAILED
    private String eventType;

    private String url;

    private Boolean active;

    private LocalDateTime createdAt;

    // Optional scoping to a particular manager/agent name (contains match)
    private String agentName;
}
