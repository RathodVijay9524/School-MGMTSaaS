package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;

import java.util.List;

/**
 * Service interface for Peer Review management
 */
public interface PeerReviewService {

    /**
     * Assign peer reviews for an assignment
     */
    PeerReviewAssignmentResponse assignPeerReviews(PeerReviewAssignmentRequest request, Long ownerId);

    /**
     * Create/submit a peer review
     */
    PeerReviewResponse submitPeerReview(PeerReviewRequest request, Long reviewerId, Long ownerId);

    /**
     * Update a peer review
     */
    PeerReviewResponse updatePeerReview(Long id, PeerReviewRequest request, Long reviewerId, Long ownerId);

    /**
     * Get peer review by ID
     */
    PeerReviewResponse getPeerReviewById(Long id, Long ownerId);

    /**
     * Get all peer reviews for an assignment
     */
    List<PeerReviewResponse> getPeerReviewsByAssignment(Long assignmentId, Long ownerId);

    /**
     * Get peer reviews for a submission
     */
    List<PeerReviewResponse> getPeerReviewsBySubmission(Long submissionId, Long ownerId);

    /**
     * Get peer reviews by reviewer
     */
    List<PeerReviewResponse> getPeerReviewsByReviewer(Long reviewerId, Long ownerId);

    /**
     * Get pending peer reviews for a reviewer
     */
    List<PeerReviewResponse> getPendingReviews(Long reviewerId, Long ownerId);

    /**
     * Teacher approves/rejects a peer review
     */
    PeerReviewResponse approvePeerReview(PeerReviewApprovalRequest request, Long teacherId, Long ownerId);

    /**
     * Get peer review statistics for an assignment
     */
    PeerReviewStatisticsResponse getPeerReviewStatistics(Long assignmentId, Long ownerId);

    /**
     * Delete peer review
     */
    void deletePeerReview(Long id, Long ownerId);
}
