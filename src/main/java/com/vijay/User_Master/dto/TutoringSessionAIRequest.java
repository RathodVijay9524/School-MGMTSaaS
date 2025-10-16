package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI-powered tutoring session requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutoringSessionAIRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "Subject cannot be empty")
    private String subject;

    @NotBlank(message = "Topic cannot be empty")
    private String topic;

    @NotBlank(message = "Grade level cannot be empty")
    private String gradeLevel;

    @NotBlank(message = "Question cannot be empty")
    private String question;

    private String difficultyLevel;

    private String learningObjective;

    private String aiProvider; // GPT-4, Claude, Gemini, etc.

    private Boolean generatePracticeProblems;

    private Boolean explainStepByStep;

    private Boolean provideRelatedTopics;

    private Integer maxTokens;

    private Double temperature; // For AI creativity control

    private String context; // Additional context for the AI
}
