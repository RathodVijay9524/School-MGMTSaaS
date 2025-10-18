package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for manual grading of quiz responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualGradingRequest {

    @NotNull(message = "Response ID is required")
    private Long responseId;

    @NotNull(message = "Points earned is required")
    private Double pointsEarned;

    private String feedback;
    
    private Boolean isCorrect;
}
