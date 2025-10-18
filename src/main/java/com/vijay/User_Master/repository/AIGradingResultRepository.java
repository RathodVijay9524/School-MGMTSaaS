package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.AIGradingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AIGradingResult entity
 */
@Repository
public interface AIGradingResultRepository extends JpaRepository<AIGradingResult, Long> {

    // Find by submission
    Optional<AIGradingResult> findBySubmissionIdAndIsDeletedFalse(Long submissionId);

    // Find by status
    List<AIGradingResult> findByGradingStatusAndOwnerIdAndIsDeletedFalse(AIGradingResult.GradingStatus status, Long ownerId);

    // Find pending teacher review
    @Query("SELECT a FROM AIGradingResult a WHERE a.teacherReviewed = false AND a.gradingStatus = 'COMPLETED' AND a.owner.id = :ownerId AND a.isDeleted = false")
    List<AIGradingResult> findPendingTeacherReview(@Param("ownerId") Long ownerId);

    // Find high plagiarism cases
    @Query("SELECT a FROM AIGradingResult a WHERE a.plagiarismScore > :threshold AND a.owner.id = :ownerId AND a.isDeleted = false")
    List<AIGradingResult> findHighPlagiarismCases(@Param("threshold") Double threshold, @Param("ownerId") Long ownerId);

    // Find low confidence gradings
    @Query("SELECT a FROM AIGradingResult a WHERE a.aiConfidenceScore < :threshold AND a.owner.id = :ownerId AND a.isDeleted = false")
    List<AIGradingResult> findLowConfidenceGradings(@Param("threshold") Double threshold, @Param("ownerId") Long ownerId);

    // Get statistics
    @Query("SELECT COUNT(a), AVG(a.aiSuggestedScore), AVG(a.aiConfidenceScore), AVG(a.plagiarismScore) FROM AIGradingResult a WHERE a.owner.id = :ownerId AND a.isDeleted = false")
    Object[] getGradingStatistics(@Param("ownerId") Long ownerId);

    // Count by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);

    // Find by rubric
    List<AIGradingResult> findByRubricIdAndIsDeletedFalse(Long rubricId);
}
