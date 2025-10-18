package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.PeerReview;
import com.vijay.User_Master.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PeerReview entity
 */
@Repository
public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {

    // Find by assignment
    List<PeerReview> findByAssignmentIdAndIsDeletedFalse(Long assignmentId);
    
    Page<PeerReview> findByAssignmentIdAndIsDeletedFalse(Long assignmentId, Pageable pageable);

    // Find by submission
    List<PeerReview> findBySubmissionIdAndIsDeletedFalse(Long submissionId);

    // Find by reviewer
    List<PeerReview> findByReviewerIdAndIsDeletedFalse(Long reviewerId);
    
    Page<PeerReview> findByReviewerIdAndIsDeletedFalse(Long reviewerId, Pageable pageable);

    // Find by status
    List<PeerReview> findByStatusAndIsDeletedFalse(ReviewStatus status);
    
    List<PeerReview> findByReviewerIdAndStatusAndIsDeletedFalse(Long reviewerId, ReviewStatus status);

    // Find pending reviews for a reviewer
    @Query("SELECT pr FROM PeerReview pr WHERE pr.reviewer.id = :reviewerId " +
           "AND pr.status = 'PENDING' AND pr.isDeleted = false")
    List<PeerReview> findPendingReviewsByReviewer(@Param("reviewerId") Long reviewerId);

    // Find submitted reviews awaiting teacher approval
    @Query("SELECT pr FROM PeerReview pr WHERE pr.assignment.id = :assignmentId " +
           "AND pr.status = 'SUBMITTED' AND pr.isDeleted = false")
    List<PeerReview> findSubmittedReviewsForAssignment(@Param("assignmentId") Long assignmentId);

    // Find approved reviews
    @Query("SELECT pr FROM PeerReview pr WHERE pr.submission.id = :submissionId " +
           "AND pr.status = 'APPROVED' AND pr.isDeleted = false")
    List<PeerReview> findApprovedReviewsForSubmission(@Param("submissionId") Long submissionId);

    // Count reviews by reviewer
    @Query("SELECT COUNT(pr) FROM PeerReview pr WHERE pr.reviewer.id = :reviewerId " +
           "AND pr.isDeleted = false")
    Long countByReviewerId(@Param("reviewerId") Long reviewerId);

    // Count completed reviews by reviewer
    @Query("SELECT COUNT(pr) FROM PeerReview pr WHERE pr.reviewer.id = :reviewerId " +
           "AND pr.status IN ('SUBMITTED', 'APPROVED') AND pr.isDeleted = false")
    Long countCompletedByReviewerId(@Param("reviewerId") Long reviewerId);

    // Average score for a submission
    @Query("SELECT AVG(pr.overallScore) FROM PeerReview pr WHERE pr.submission.id = :submissionId " +
           "AND pr.status = 'APPROVED' AND pr.isDeleted = false")
    Double getAverageScoreForSubmission(@Param("submissionId") Long submissionId);

    // Find by ID and owner
    Optional<PeerReview> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);

    // Check if reviewer already reviewed a submission
    boolean existsBySubmissionIdAndReviewerIdAndIsDeletedFalse(Long submissionId, Long reviewerId);
}
