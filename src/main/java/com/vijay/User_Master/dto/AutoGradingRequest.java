package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for auto-grading a single response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoGradingRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotNull(message = "Student answer is required")
    private String studentAnswer;

    private Boolean provideExplanation;
    private Boolean provideFeedback;
}
