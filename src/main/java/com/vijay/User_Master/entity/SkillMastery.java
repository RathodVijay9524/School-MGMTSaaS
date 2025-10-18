package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SkillMastery entity for Adaptive Learning System
 * Tracks per-student mastery level for each skill/topic
 */
@Entity
@Table(name = "skill_mastery", indexes = {
    @Index(name = "idx_student_subject", columnList = "student_id,subject_id"),
    @Index(name = "idx_skill_key", columnList = "skill_key"),
    @Index(name = "idx_next_review", columnList = "next_review_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SkillMastery extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "skill_key", nullable = false, length = 100)
    private String skillKey; // e.g., "algebra_linear_equations"

    @Column(name = "skill_name", nullable = false)
    private String skillName; // e.g., "Linear Equations"

    @Column(name = "mastery_level", nullable = false)
    @Builder.Default
    private Double masteryLevel = 0.0; // 0-100

    @Column(name = "avg_accuracy")
    @Builder.Default
    private Double avgAccuracy = 0.0; // 0-100

    @Column(name = "total_attempts")
    @Builder.Default
    private Integer totalAttempts = 0;

    @Column(name = "correct_attempts")
    @Builder.Default
    private Integer correctAttempts = 0;

    @Column(name = "last_practiced_at")
    private LocalDateTime lastPracticedAt;

    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt; // Spaced repetition scheduling

    @Column(name = "last_difficulty")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel lastDifficulty;

    @Column(name = "velocity_score")
    @Builder.Default
    private Double velocityScore = 0.0; // Learning speed metric

    @Column(name = "consecutive_correct")
    @Builder.Default
    private Integer consecutiveCorrect = 0;

    @Column(name = "consecutive_incorrect")
    @Builder.Default
    private Integer consecutiveIncorrect = 0;

    @Column(name = "time_spent_minutes")
    @Builder.Default
    private Integer timeSpentMinutes = 0;

    @Column(name = "hints_used_count")
    @Builder.Default
    private Integer hintsUsedCount = 0;

    @Column(name = "last_decay_applied_at")
    private LocalDateTime lastDecayAppliedAt;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }

    // Helper methods
    public void incrementAttempts() {
        this.totalAttempts++;
    }

    public void incrementCorrectAttempts() {
        this.correctAttempts++;
        this.consecutiveCorrect++;
        this.consecutiveIncorrect = 0;
    }

    public void incrementIncorrectAttempts() {
        this.consecutiveIncorrect++;
        this.consecutiveCorrect = 0;
    }

    public void updateAccuracy() {
        if (totalAttempts > 0) {
            this.avgAccuracy = (double) correctAttempts / totalAttempts * 100.0;
        }
    }

    public boolean isHighMastery() {
        return masteryLevel >= 80.0;
    }

    public boolean isMediumMastery() {
        return masteryLevel >= 50.0 && masteryLevel < 80.0;
    }

    public boolean isLowMastery() {
        return masteryLevel < 50.0;
    }

    public boolean needsReview() {
        return nextReviewAt != null && nextReviewAt.isBefore(LocalDateTime.now());
    }

    public String getMasteryCategory() {
        if (masteryLevel >= 90) return "Expert";
        if (masteryLevel >= 75) return "Proficient";
        if (masteryLevel >= 60) return "Competent";
        if (masteryLevel >= 40) return "Developing";
        return "Beginner";
    }

    public DifficultyLevel getRecommendedDifficulty() {
        if (masteryLevel >= 80.0) return DifficultyLevel.HARD;
        if (masteryLevel >= 50.0) return DifficultyLevel.MEDIUM;
        return DifficultyLevel.EASY;
    }
}
