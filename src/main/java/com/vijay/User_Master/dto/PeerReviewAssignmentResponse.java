package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for peer review assignment response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerReviewAssignmentResponse {

    private Long assignmentId;
    private String assignmentTitle;
    private Integer totalSubmissions;
    private Integer totalReviewsCreated;
    private Integer reviewsPerSubmission;
    
    private List<ReviewAssignmentDetail> assignments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewAssignmentDetail {
        private Long peerReviewId;
        private Long submissionId;
        private String submittedByName;
        private Long reviewerId;
        private String reviewerName;
        private Boolean isAnonymous;
    }
}
