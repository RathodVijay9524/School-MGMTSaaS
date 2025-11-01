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
@Table(name = "agent_steps")
@EntityListeners(AuditingEntityListener.class)
public class AgentStep extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_run_id", nullable = false)
    private AgentRun agentRun;

    @Column(nullable = false)
    private String nodeName;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String inputJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String outputJson;

    @Column
    private String status; // OK, ERROR

    @Column
    private String error;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
