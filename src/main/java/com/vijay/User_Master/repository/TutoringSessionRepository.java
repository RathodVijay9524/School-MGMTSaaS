package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.TutoringSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TutoringSession entity
 */
@Repository
public interface TutoringSessionRepository extends JpaRepository<TutoringSession, Long> {

    // Find sessions by owner
    Page<TutoringSession> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Pageable pageable);
    
    // Find sessions by student
    Page<TutoringSession> findByStudentIdAndIsDeletedFalseOrderByCreatedOnDesc(Long studentId, Pageable pageable);
    
    // Find sessions by subject
    Page<TutoringSession> findByOwnerIdAndSubjectAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String subject, Pageable pageable);
    
    // Find sessions by grade level
    Page<TutoringSession> findByOwnerIdAndGradeLevelAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String gradeLevel, Pageable pageable);
    
    // Find sessions by topic
    Page<TutoringSession> findByOwnerIdAndTopicContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String topic, Pageable pageable);
    
    // Find sessions by difficulty level
    List<TutoringSession> findByOwnerIdAndDifficultyLevelAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, TutoringSession.DifficultyLevel difficultyLevel);
    
    // Find sessions by status
    List<TutoringSession> findByOwnerIdAndSessionStatusAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, TutoringSession.SessionStatus status);
    
    // Find sessions requiring follow-up
    List<TutoringSession> findByOwnerIdAndFollowUpRequiredTrueAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find sessions requiring teacher review
    List<TutoringSession> findByOwnerIdAndTeacherReviewRequiredTrueAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find sessions by AI provider
    List<TutoringSession> findByOwnerIdAndAiProviderAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String aiProvider);
    
    // Find sessions by date range
    @Query("SELECT ts FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.createdOn BETWEEN :startDate AND :endDate AND ts.isDeleted = false ORDER BY ts.createdOn DESC")
    List<TutoringSession> findByOwnerIdAndDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find sessions with low satisfaction
    @Query("SELECT ts FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.studentSatisfactionRating < :rating AND ts.isDeleted = false ORDER BY ts.createdOn DESC")
    List<TutoringSession> findByOwnerIdAndLowSatisfaction(@Param("ownerId") Long ownerId, @Param("rating") Integer rating);
    
    // Find sessions by comprehension score range
    @Query("SELECT ts FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.comprehensionScore BETWEEN :minScore AND :maxScore AND ts.isDeleted = false ORDER BY ts.comprehensionScore DESC")
    List<TutoringSession> findByOwnerIdAndComprehensionScoreRange(@Param("ownerId") Long ownerId, @Param("minScore") Double minScore, @Param("maxScore") Double maxScore);
    
    // Count sessions by student
    long countByStudentIdAndIsDeletedFalse(Long studentId);
    
    // Count sessions by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count sessions by subject
    long countByOwnerIdAndSubjectAndIsDeletedFalse(Long ownerId, String subject);
    
    // Count sessions by status
    long countByOwnerIdAndSessionStatusAndIsDeletedFalse(Long ownerId, TutoringSession.SessionStatus status);
    
    // Get tutoring statistics
    @Query("SELECT COUNT(ts) as totalSessions, " +
           "COUNT(CASE WHEN ts.sessionStatus = 'COMPLETED' THEN 1 END) as completedSessions, " +
           "AVG(ts.timeSpentMinutes) as avgTimeSpent, " +
           "AVG(ts.studentSatisfactionRating) as avgSatisfaction, " +
           "AVG(ts.comprehensionScore) as avgComprehension, " +
           "SUM(ts.costUsd) as totalCost " +
           "FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false")
    Object[] getTutoringStatistics(@Param("ownerId") Long ownerId);
    
    // Get subject-wise statistics
    @Query("SELECT ts.subject, COUNT(ts) as sessionCount, AVG(ts.studentSatisfactionRating) as avgSatisfaction " +
           "FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false " +
           "GROUP BY ts.subject ORDER BY sessionCount DESC")
    List<Object[]> getSubjectWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get grade-wise statistics
    @Query("SELECT ts.gradeLevel, COUNT(ts) as sessionCount, AVG(ts.comprehensionScore) as avgComprehension " +
           "FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false " +
           "GROUP BY ts.gradeLevel ORDER BY sessionCount DESC")
    List<Object[]> getGradeWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Search sessions by keyword
    @Query("SELECT ts FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND " +
           "(LOWER(ts.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ts.topic) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ts.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND ts.isDeleted = false")
    Page<TutoringSession> searchSessions(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Find recent sessions
    @Query("SELECT ts FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.createdOn >= :since AND ts.isDeleted = false ORDER BY ts.createdOn DESC")
    List<TutoringSession> findRecentSessions(@Param("ownerId") Long ownerId, @Param("since") LocalDateTime since);
    
    // Find sessions by time spent range
    @Query("SELECT ts FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.timeSpentMinutes BETWEEN :minMinutes AND :maxMinutes AND ts.isDeleted = false ORDER BY ts.timeSpentMinutes DESC")
    List<TutoringSession> findByOwnerIdAndTimeSpentRange(@Param("ownerId") Long ownerId, @Param("minMinutes") Integer minMinutes, @Param("maxMinutes") Integer maxMinutes);
    
    // Find most popular topics
    @Query("SELECT ts.topic, COUNT(ts) as topicCount FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false GROUP BY ts.topic ORDER BY topicCount DESC")
    List<Object[]> findMostPopularTopics(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Find sessions by cost range
    @Query("SELECT ts FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.costUsd BETWEEN :minCost AND :maxCost AND ts.isDeleted = false ORDER BY ts.costUsd DESC")
    List<TutoringSession> findByOwnerIdAndCostRange(@Param("ownerId") Long ownerId, @Param("minCost") Double minCost, @Param("maxCost") Double maxCost);
    
    // Check if session exists by student and topic
    boolean existsByStudentIdAndTopicAndIsDeletedFalse(Long studentId, String topic);
    
    // Soft delete by owner
    @Query("UPDATE TutoringSession ts SET ts.isDeleted = true WHERE ts.id = :id AND ts.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Dashboard analytics methods
    @Query("SELECT AVG(ts.studentSatisfactionRating) FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false AND ts.studentSatisfactionRating IS NOT NULL")
    Double getAverageSatisfactionRating(@Param("ownerId") Long ownerId);
    
    @Query("SELECT AVG(ts.comprehensionScore) FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false AND ts.comprehensionScore IS NOT NULL")
    Double getAverageComprehensionScore(@Param("ownerId") Long ownerId);
    
    @Query("SELECT SUM(ts.timeSpentMinutes) FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false AND ts.timeSpentMinutes IS NOT NULL")
    Integer getTotalTimeSpent(@Param("ownerId") Long ownerId);
    
    @Query("SELECT ts.subject, COUNT(ts) FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false GROUP BY ts.subject")
    List<Object[]> getSessionsBySubject(@Param("ownerId") Long ownerId);
    
    @Query("SELECT ts.difficultyLevel, COUNT(ts) FROM TutoringSession ts WHERE ts.owner.id = :ownerId AND ts.isDeleted = false GROUP BY ts.difficultyLevel")
    List<Object[]> getSessionsByDifficultyLevel(@Param("ownerId") Long ownerId);
}
