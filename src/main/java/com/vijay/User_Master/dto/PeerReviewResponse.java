package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for peer review responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerReviewResponse {

    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long submissionId;
    private Long reviewerId;
    private String reviewerName;
    private Boolean isAnonymous;
    private Long rubricId;
    private String rubricName;
    private String reviewComments;
    private Double overallScore;
    private List<CriterionScoreResponse> criterionScores;
    private ReviewStatus status;
    private LocalDateTime submittedAt;
    private Integer timeSpentMinutes;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String teacherComments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriterionScoreResponse {
        private Long id;
        private Long criterionId;
        private String criterionName;
        private Double score;
        private Double maxPoints;
        private String comments;
    }
}
