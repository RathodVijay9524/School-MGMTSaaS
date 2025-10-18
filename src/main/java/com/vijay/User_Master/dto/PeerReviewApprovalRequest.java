package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for teacher approval/rejection of peer reviews
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerReviewApprovalRequest {

    @NotNull(message = "Peer review ID is required")
    private Long peerReviewId;

    @NotNull(message = "Approval status is required")
    private Boolean approved;

    private String teacherComments;
    
    private Double adjustedScore; // Teacher can adjust the score
}
