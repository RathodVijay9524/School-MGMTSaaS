package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO for LearningModule creation/update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningModuleRequest {

    @NotNull(message = "Learning Path ID is required")
    private Long learningPathId;

    @NotBlank(message = "Module name is required")
    @Size(max = 255, message = "Module name must not exceed 255 characters")
    private String moduleName;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @NotNull(message = "Module type is required")
    private String moduleType; // LECTURE, QUIZ, ASSIGNMENT, PROJECT, DISCUSSION

    @NotNull(message = "Difficulty level is required")
    private String difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED

    @Min(value = 1, message = "Order index must be at least 1")
    @Max(value = 1000, message = "Order index must not exceed 1000")
    private Integer orderIndex;

    @Min(value = 1, message = "Estimated duration must be at least 1 minute")
    @Max(value = 1440, message = "Estimated duration must not exceed 1440 minutes (24 hours)")
    private Integer estimatedDurationMinutes;

    @Size(max = 1000, message = "Learning objectives must not exceed 1000 characters")
    private String learningObjectives;

    @Size(max = 1000, message = "Prerequisites must not exceed 1000 characters")
    private String prerequisites;

    @Size(max = 1000, message = "Resources must not exceed 1000 characters")
    private String resources;

    @Size(max = 2000, message = "Assessment questions must not exceed 2000 characters")
    private String assessmentQuestions;

    @Min(value = 0, message = "Passing score must be at least 0")
    @Max(value = 100, message = "Passing score must not exceed 100")
    private Double passingScorePercentage;

    @Size(max = 500, message = "Instructions must not exceed 500 characters")
    private String instructions;

    private Boolean isActive;

    private Boolean isRequired;

    @Size(max = 1000, message = "Tags must not exceed 1000 characters")
    private String tags;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
