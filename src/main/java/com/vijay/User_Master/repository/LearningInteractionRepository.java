package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LearningInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for LearningInteraction entity
 */
@Repository
public interface LearningInteractionRepository extends JpaRepository<LearningInteraction, Long> {

    // Find interactions by student
    List<LearningInteraction> findByStudentIdAndIsDeletedFalseOrderByAttemptedAtDesc(Long studentId);

    // Find interactions by student and skill
    List<LearningInteraction> findByStudentIdAndSkillKeyAndIsDeletedFalseOrderByAttemptedAtDesc(Long studentId, String skillKey);

    // Find interactions by module
    List<LearningInteraction> findByModuleIdAndIsDeletedFalseOrderByAttemptedAtDesc(Long moduleId);

    // Find recent interactions
    List<LearningInteraction> findByStudentIdAndAttemptedAtAfterAndIsDeletedFalse(Long studentId, LocalDateTime since);

    // Find interactions by outcome
    List<LearningInteraction> findByStudentIdAndOutcomeAndIsDeletedFalse(Long studentId, LearningInteraction.Outcome outcome);

    // Get interaction statistics for a skill
    @Query("SELECT " +
           "COUNT(li), " +
           "SUM(CASE WHEN li.outcome = 'CORRECT' THEN 1 ELSE 0 END), " +
           "AVG(li.score), " +
           "AVG(li.timeTakenSeconds) " +
           "FROM LearningInteraction li WHERE li.student.id = :studentId AND li.skillKey = :skillKey AND li.isDeleted = false")
    Object[] getInteractionStatistics(@Param("studentId") Long studentId, @Param("skillKey") String skillKey);

    // Count interactions by student
    long countByStudentIdAndIsDeletedFalse(Long studentId);

    // Get success rate by skill
    @Query("SELECT li.skillKey, " +
           "COUNT(li), " +
           "SUM(CASE WHEN li.outcome = 'CORRECT' THEN 1 ELSE 0 END) * 100.0 / COUNT(li) as successRate " +
           "FROM LearningInteraction li WHERE li.student.id = :studentId AND li.isDeleted = false " +
           "GROUP BY li.skillKey")
    List<Object[]> getSuccessRateBySkill(@Param("studentId") Long studentId);

    // Find interactions by difficulty
    List<LearningInteraction> findByStudentIdAndDifficultyAndIsDeletedFalse(Long studentId, LearningInteraction.DifficultyLevel difficulty);

    // Get average time by skill
    @Query("SELECT li.skillKey, AVG(li.timeTakenSeconds) FROM LearningInteraction li WHERE li.student.id = :studentId AND li.isDeleted = false GROUP BY li.skillKey")
    List<Object[]> getAverageTimeBySkill(@Param("studentId") Long studentId);

    // Find interactions with hints
    @Query("SELECT li FROM LearningInteraction li WHERE li.student.id = :studentId AND li.hintsUsed > 0 AND li.isDeleted = false ORDER BY li.attemptedAt DESC")
    List<LearningInteraction> findInteractionsWithHints(@Param("studentId") Long studentId);

    // Get daily interaction count
    @Query("SELECT DATE(li.attemptedAt), COUNT(li) FROM LearningInteraction li WHERE li.student.id = :studentId AND li.attemptedAt >= :startDate AND li.isDeleted = false GROUP BY DATE(li.attemptedAt) ORDER BY DATE(li.attemptedAt)")
    List<Object[]> getDailyInteractionCount(@Param("studentId") Long studentId, @Param("startDate") LocalDateTime startDate);

    // Find by owner
    List<LearningInteraction> findByOwnerIdAndIsDeletedFalse(Long ownerId);
}
