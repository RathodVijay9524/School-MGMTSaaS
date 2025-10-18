package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating peer reviews
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerReviewRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Submission ID is required")
    private Long submissionId;

    private Long rubricId;

    private String reviewComments;

    private Double overallScore;

    private List<CriterionScoreRequest> criterionScores;

    private Integer timeSpentMinutes;

    private Boolean isAnonymous;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriterionScoreRequest {
        @NotNull(message = "Criterion ID is required")
        private Long criterionId;
        
        @NotNull(message = "Score is required")
        private Double score;
        
        private String comments;
    }
}
