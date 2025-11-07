package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    // e.g., RUN_STEP_FAILED
    private String eventType;

    private Boolean active;

    private LocalDateTime mutedUntil;

    private LocalDateTime createdAt;

    // Optional scoping to a particular manager/agent name (contains match)
    private String agentName;
}
