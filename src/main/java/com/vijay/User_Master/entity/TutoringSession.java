package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TutoringSession entity for Academic Tutoring System
 * Represents individual tutoring sessions between AI and students
 */
@Entity
@Table(name = "tutoring_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TutoringSession extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "grade_level", nullable = false)
    private String gradeLevel;

    @Column(name = "question", columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(name = "ai_response", columnDefinition = "TEXT", nullable = false)
    private String aiResponse;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "step_by_step_solution", columnDefinition = "TEXT")
    private String stepByStepSolution;

    @Column(name = "key_concepts", columnDefinition = "TEXT")
    private String keyConcepts;

    @Column(name = "practice_problems", columnDefinition = "TEXT")
    private String practiceProblems;

    @Column(name = "related_topics", columnDefinition = "TEXT")
    private String relatedTopics;

    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @Column(name = "learning_objective")
    private String learningObjective;

    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;

    @Column(name = "student_satisfaction_rating")
    private Integer studentSatisfactionRating; // 1-5 scale

    @Column(name = "comprehension_score")
    private Double comprehensionScore; // 0-100

    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;

    @Column(name = "teacher_review_required")
    @Builder.Default
    private Boolean teacherReviewRequired = false;

    @Column(name = "session_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionStatus sessionStatus = SessionStatus.ACTIVE;

    @Column(name = "ai_provider")
    private String aiProvider; // GPT-4, Claude, Gemini, etc.

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "cost_usd")
    private Double costUsd;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    public enum SessionStatus {
        ACTIVE, COMPLETED, ABANDONED, UNDER_REVIEW
    }

    // Helper methods
    public boolean isCompleted() {
        return sessionStatus == SessionStatus.COMPLETED;
    }

    public boolean needsFollowUp() {
        return followUpRequired || studentSatisfactionRating != null && studentSatisfactionRating < 3;
    }

    public String getFormattedTimeSpent() {
        if (timeSpentMinutes == null) return "0 min";
        int hours = timeSpentMinutes / 60;
        int minutes = timeSpentMinutes % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    public double getCostPerMinute() {
        if (timeSpentMinutes == null || timeSpentMinutes == 0 || costUsd == null) return 0.0;
        return costUsd / timeSpentMinutes;
    }
}
