package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * LearningInteraction entity for Adaptive Learning System
 * Records every learning event for mastery calculation
 */
@Entity
@Table(name = "learning_interactions", indexes = {
    @Index(name = "idx_student_skill", columnList = "student_id,skill_key"),
    @Index(name = "idx_module", columnList = "module_id"),
    @Index(name = "idx_attempted_at", columnList = "attempted_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LearningInteraction extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private LearningModule module;

    @Column(name = "skill_key", nullable = false, length = 100)
    private String skillKey;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;

    @Column(name = "outcome", nullable = false)
    @Enumerated(EnumType.STRING)
    private Outcome outcome;

    @Column(name = "score")
    private Double score; // 0-100

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "attempted_at", nullable = false)
    @Builder.Default
    private LocalDateTime attemptedAt = LocalDateTime.now();

    @Column(name = "hints_used")
    @Builder.Default
    private Integer hintsUsed = 0;

    @Column(name = "question_type")
    private String questionType; // MCQ, SHORT_ANSWER, ESSAY, etc.

    @Column(name = "confidence_level")
    private Integer confidenceLevel; // 1-5 scale

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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

    public enum Outcome {
        CORRECT, PARTIAL, INCORRECT, SKIPPED
    }

    // Helper methods
    public boolean isCorrect() {
        return outcome == Outcome.CORRECT;
    }

    public boolean isPartiallyCorrect() {
        return outcome == Outcome.PARTIAL;
    }

    public boolean isIncorrect() {
        return outcome == Outcome.INCORRECT;
    }

    public double getSuccessRate() {
        if (outcome == Outcome.CORRECT) return 1.0;
        if (outcome == Outcome.PARTIAL) return 0.5;
        return 0.0;
    }

    public boolean wasHintUsed() {
        return hintsUsed != null && hintsUsed > 0;
    }

    public String getFormattedTimeTaken() {
        if (timeTakenSeconds == null) return "Unknown";
        int minutes = timeTakenSeconds / 60;
        int seconds = timeTakenSeconds % 60;
        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        }
        return seconds + "s";
    }
}
