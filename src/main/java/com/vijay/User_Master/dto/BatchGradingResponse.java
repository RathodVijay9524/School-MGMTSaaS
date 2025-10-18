package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for batch grading response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGradingResponse {

    private Integer totalAttempts;
    private Integer successfullyGraded;
    private Integer failedGrading;
    private Integer requireManualReview;
    
    private List<GradingResult> results;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradingResult {
        private Long attemptId;
        private Boolean success;
        private String errorMessage;
        private Double totalScore;
        private Double percentage;
        private Boolean passed;
    }
}
