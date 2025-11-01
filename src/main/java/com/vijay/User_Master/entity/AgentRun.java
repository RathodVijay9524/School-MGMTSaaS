package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "agent_runs")
@EntityListeners(AuditingEntityListener.class)
public class AgentRun extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String runId; // external correlation id (e.g., UUID)

    @Column(nullable = false)
    private String agentName; // e.g., AdmissionsFunnel

    @Column
    private Long ownerId; // multi-tenant owner

    @Column(nullable = false)
    private String status; // RUNNING, WAITING, COMPLETED, FAILED

    @Column
    private String currentNode; // which node is next/active

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String stateJson; // serialized shared state

    private LocalDateTime lastHeartbeat; // optional for monitoring
}
