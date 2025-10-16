package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for LearningPath creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathRequest {

    @NotBlank(message = "Path name cannot be empty")
    @Size(max = 200, message = "Path name cannot exceed 200 characters")
    private String pathName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Subject cannot be empty")
    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    private String subject;

    @NotBlank(message = "Grade level cannot be empty")
    @Size(max = 50, message = "Grade level cannot exceed 50 characters")
    private String gradeLevel;

    @Size(max = 2000, message = "Learning objectives cannot exceed 2000 characters")
    private String learningObjectives;

    @Min(value = 1, message = "Estimated duration must be at least 1 hour")
    private Integer estimatedDurationHours;

    private String difficultyLevel;

    @Size(max = 1000, message = "Prerequisites cannot exceed 1000 characters")
    private String prerequisites;

    @Size(max = 2000, message = "Learning outcomes cannot exceed 2000 characters")
    private String learningOutcomes;

    @Size(max = 1000, message = "Assessment criteria cannot exceed 1000 characters")
    private String assessmentCriteria;

    @Size(max = 1000, message = "Resources needed cannot exceed 1000 characters")
    private String resourcesNeeded;

    @Builder.Default
    private Boolean isAdaptive = true;

    @Builder.Default
    private Boolean isActive = true;

    private Integer pathOrder;

    private Long studentId; // For personalized paths
}
