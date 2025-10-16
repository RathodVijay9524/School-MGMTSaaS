package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * LearningModule entity for Academic Tutoring System
 * Represents individual modules within a learning path
 */
@Entity
@Table(name = "learning_modules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LearningModule extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;

    @Column(name = "module_type")
    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;

    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;

    @Column(name = "resources", columnDefinition = "TEXT")
    private String resources; // JSON string of resources

    @Column(name = "assessment_questions", columnDefinition = "TEXT")
    private String assessmentQuestions; // JSON string of questions

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;

    @Column(name = "score_percentage")
    private Double scorePercentage;

    @Column(name = "attempts_count")
    @Builder.Default
    private Integer attemptsCount = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Worker student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum ModuleType {
        THEORY, PRACTICE, ASSESSMENT, PROJECT, DISCUSSION, VIDEO, READING, EXERCISE
    }

    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    // Helper methods
    public void markCompleted() {
        this.isCompleted = true;
        this.completionDate = LocalDateTime.now();
    }

    public void addAttempt() {
        this.attemptsCount++;
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

    public String getFormattedEstimatedDuration() {
        if (estimatedDurationMinutes == null) return "Unknown";
        int hours = estimatedDurationMinutes / 60;
        int minutes = estimatedDurationMinutes % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    public boolean isPassed() {
        return scorePercentage != null && scorePercentage >= 70.0;
    }

    public String getGrade() {
        if (scorePercentage == null) return "Not Graded";
        if (scorePercentage >= 90) return "A+";
        if (scorePercentage >= 80) return "A";
        if (scorePercentage >= 70) return "B";
        if (scorePercentage >= 60) return "C";
        if (scorePercentage >= 50) return "D";
        return "F";
    }

    public double getProgressPercentage() {
        if (estimatedDurationMinutes == null || estimatedDurationMinutes == 0) return 0.0;
        if (timeSpentMinutes == null) return 0.0;
        return Math.min(100.0, (double) timeSpentMinutes / estimatedDurationMinutes * 100);
    }
}
