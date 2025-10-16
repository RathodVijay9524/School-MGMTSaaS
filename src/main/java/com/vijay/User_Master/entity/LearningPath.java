package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * LearningPath entity for Academic Tutoring System
 * Represents personalized learning paths for students
 */
@Entity
@Table(name = "learning_paths")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LearningPath extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path_name", nullable = false)
    private String pathName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "grade_level", nullable = false)
    private String gradeLevel;

    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;

    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;

    @Column(name = "learning_outcomes", columnDefinition = "TEXT")
    private String learningOutcomes;

    @Column(name = "assessment_criteria", columnDefinition = "TEXT")
    private String assessmentCriteria;

    @Column(name = "resources_needed", columnDefinition = "TEXT")
    private String resourcesNeeded;

    @Column(name = "is_adaptive")
    @Builder.Default
    private Boolean isAdaptive = true;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "path_order")
    private Integer pathOrder;

    @Column(name = "completion_percentage")
    @Builder.Default
    private Double completionPercentage = 0.0;

    @Column(name = "mastery_level")
    @Builder.Default
    private Double masteryLevel = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Worker student;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LearningModule> modules = new ArrayList<>();

    // Enums
    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    // Helper methods
    public int getTotalModules() {
        return modules != null ? modules.size() : 0;
    }

    public int getCompletedModules() {
        if (modules == null) return 0;
        return (int) modules.stream()
                .filter(module -> module.getIsCompleted())
                .count();
    }

    public double getActualCompletionPercentage() {
        int total = getTotalModules();
        if (total == 0) return 0.0;
        return (double) getCompletedModules() / total * 100;
    }

    public boolean isCompleted() {
        return completionPercentage >= 100.0;
    }

    public int getRemainingModules() {
        return getTotalModules() - getCompletedModules();
    }

    public String getStatus() {
        if (completionPercentage >= 100) return "Completed";
        if (completionPercentage >= 75) return "Almost Complete";
        if (completionPercentage >= 50) return "In Progress";
        if (completionPercentage >= 25) return "Getting Started";
        return "Not Started";
    }

    public String getFormattedDuration() {
        if (estimatedDurationHours == null) return "Unknown";
        int days = estimatedDurationHours / 24;
        int hours = estimatedDurationHours % 24;
        if (days > 0) {
            return days + "d " + hours + "h";
        }
        return hours + "h";
    }
}
