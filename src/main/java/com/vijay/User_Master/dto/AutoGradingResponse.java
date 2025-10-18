package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for auto-grading response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoGradingResponse {

    private Boolean isCorrect;
    private Double pointsEarned;
    private Double maxPoints;
    private Double percentage;
    private String correctAnswer;
    private String explanation;
    private String feedback;
    private Boolean requiresManualReview;
    private Double confidenceScore; // 0-1 for AI grading confidence
}
