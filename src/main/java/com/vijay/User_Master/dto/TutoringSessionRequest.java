package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for TutoringSession creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutoringSessionRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "Subject cannot be empty")
    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    private String subject;

    @NotBlank(message = "Topic cannot be empty")
    @Size(max = 200, message = "Topic cannot exceed 200 characters")
    private String topic;

    @NotBlank(message = "Grade level cannot be empty")
    @Size(max = 50, message = "Grade level cannot exceed 50 characters")
    private String gradeLevel;

    @NotBlank(message = "Question cannot be empty")
    @Size(max = 2000, message = "Question cannot exceed 2000 characters")
    private String question;

    private String explanation;

    private String stepByStepSolution;

    private String keyConcepts;

    private String practiceProblems;

    private String relatedTopics;

    private String difficultyLevel;

    private String learningObjective;

    @Min(value = 0, message = "Time spent cannot be negative")
    private Integer timeSpentMinutes;

    @Min(value = 1, message = "Satisfaction rating must be at least 1")
    @Max(value = 5, message = "Satisfaction rating cannot exceed 5")
    private Integer studentSatisfactionRating;

    @Min(value = 0, message = "Comprehension score cannot be negative")
    @Max(value = 100, message = "Comprehension score cannot exceed 100")
    private Double comprehensionScore;

    @Builder.Default
    private Boolean followUpRequired = false;

    @Builder.Default
    private Boolean teacherReviewRequired = false;

    private String sessionStatus;

    private String aiProvider;

    @Min(value = 0, message = "Tokens used cannot be negative")
    private Integer tokensUsed;

    @Min(value = 0, message = "Cost cannot be negative")
    private Double costUsd;
}
