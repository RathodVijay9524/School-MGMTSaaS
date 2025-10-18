package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for assigning peer reviews
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerReviewAssignmentRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Number of reviews per submission is required")
    private Integer reviewsPerSubmission;

    private Long rubricId;
    
    private Boolean randomAssignment; // Random vs manual assignment
    
    private Boolean allowSelfReview;
    
    private Boolean anonymousReview;
    
    // Manual assignments (if not random)
    private List<ReviewAssignment> manualAssignments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewAssignment {
        private Long submissionId;
        private Long reviewerId;
    }
}
