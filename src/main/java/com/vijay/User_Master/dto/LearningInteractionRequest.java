package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for recording learning interactions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningInteractionRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Module ID is required")
    private Long moduleId;

    @NotBlank(message = "Skill key is required")
    private String skillKey;

    @NotBlank(message = "Difficulty is required")
    private String difficulty; // EASY, MEDIUM, HARD

    @NotBlank(message = "Outcome is required")
    private String outcome; // CORRECT, PARTIAL, INCORRECT, SKIPPED

    private Double score; // 0-100

    private Integer timeTakenSeconds;

    @Builder.Default
    private Integer hintsUsed = 0;

    private String questionType;

    private Integer confidenceLevel; // 1-5

    private String notes;
}
