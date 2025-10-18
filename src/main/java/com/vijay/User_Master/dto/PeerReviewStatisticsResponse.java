package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for peer review statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerReviewStatisticsResponse {

    private Long assignmentId;
    private String assignmentTitle;
    
    // Overall statistics
    private Integer totalSubmissions;
    private Integer totalReviewsAssigned;
    private Integer totalReviewsCompleted;
    private Integer totalReviewsPending;
    private Integer totalReviewsApproved;
    
    // Completion rate
    private Double completionRate;
    private Double approvalRate;
    
    // Score statistics
    private Double averagePeerScore;
    private Double averageTeacherAdjustment;
    
    // Time statistics
    private Integer averageReviewTime; // In minutes
    
    // Top reviewers
    private List<ReviewerStats> topReviewers;
    
    // Review quality metrics
    private Double averageReviewQuality; // Based on teacher approvals

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewerStats {
        private Long reviewerId;
        private String reviewerName;
        private Integer reviewsCompleted;
        private Double averageScore;
        private Integer approvedReviews;
        private Double qualityScore;
    }
}
