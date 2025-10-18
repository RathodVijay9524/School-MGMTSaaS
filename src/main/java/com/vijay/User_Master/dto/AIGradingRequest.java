package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI grading request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIGradingRequest {

    @NotNull(message = "Submission ID is required")
    private Long submissionId;

    private Long rubricId; // Optional: use specific rubric

    @Builder.Default
    private Boolean checkPlagiarism = true;

    @Builder.Default
    private Boolean checkGrammar = true;

    @Builder.Default
    private Boolean generateFeedback = true;

    @Builder.Default
    private Boolean detailedAnalysis = true;

    private String aiModel; // Optional: specify AI model (gpt-4, gpt-3.5-turbo)

    private String additionalInstructions; // Optional: custom grading instructions
}
