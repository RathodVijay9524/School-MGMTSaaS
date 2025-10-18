package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz Attempt entity - tracks student's quiz attempt
 */
@Entity
@Table(name = "quiz_attempts", indexes = {
    @Index(name = "idx_quiz_attempt_quiz", columnList = "quiz_id"),
    @Index(name = "idx_quiz_attempt_student", columnList = "student_id"),
    @Index(name = "idx_quiz_attempt_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class QuizAttempt extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student;

    @Column(name = "attempt_number", nullable = false)
    @Builder.Default
    private Integer attemptNumber = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttemptStatus status = AttemptStatus.NOT_STARTED;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuizResponse> responses = new ArrayList<>();

    // Scoring
    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "max_score")
    private Double maxScore;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "passed")
    private Boolean passed;

    @Column(length = 5000)
    private String feedback;

    // Proctoring Data
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "tab_switch_count")
    @Builder.Default
    private Integer tabSwitchCount = 0;

    @Column(name = "copy_paste_attempts")
    @Builder.Default
    private Integer copyPasteAttempts = 0;

    @Column(name = "full_screen_exit_count")
    @Builder.Default
    private Integer fullScreenExitCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Helper methods
    public void addResponse(QuizResponse response) {
        responses.add(response);
        response.setAttempt(this);
    }

    public void calculateScore() {
        this.totalScore = responses.stream()
                .mapToDouble(r -> r.getPointsEarned() != null ? r.getPointsEarned() : 0.0)
                .sum();
        
        this.maxScore = responses.stream()
                .mapToDouble(r -> r.getMaxPoints() != null ? r.getMaxPoints() : 0.0)
                .sum();
        
        if (maxScore != null && maxScore > 0) {
            this.percentage = (totalScore / maxScore) * 100.0;
        }
    }

    public void checkPassed(Double passingScore) {
        if (percentage != null && passingScore != null) {
            this.passed = percentage >= passingScore;
        }
    }

    public boolean isInProgress() {
        return status == AttemptStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == AttemptStatus.GRADED || status == AttemptStatus.SUBMITTED;
    }
}
