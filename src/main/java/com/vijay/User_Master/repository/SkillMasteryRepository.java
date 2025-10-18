package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.SkillMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for SkillMastery entity
 */
@Repository
public interface SkillMasteryRepository extends JpaRepository<SkillMastery, Long> {

    // Find by student and skill
    Optional<SkillMastery> findByStudentIdAndSkillKeyAndIsDeletedFalse(Long studentId, String skillKey);

    // Find all skills for a student
    List<SkillMastery> findByStudentIdAndIsDeletedFalseOrderByMasteryLevelDesc(Long studentId);

    // Find by student and subject
    List<SkillMastery> findByStudentIdAndSubjectIdAndIsDeletedFalseOrderByMasteryLevelDesc(Long studentId, Long subjectId);

    // Find skills needing review
    List<SkillMastery> findByStudentIdAndNextReviewAtBeforeAndIsDeletedFalse(Long studentId, LocalDateTime now);

    // Find low mastery skills
    @Query("SELECT sm FROM SkillMastery sm WHERE sm.student.id = :studentId AND sm.masteryLevel < :threshold AND sm.isDeleted = false ORDER BY sm.masteryLevel ASC")
    List<SkillMastery> findLowMasterySkills(@Param("studentId") Long studentId, @Param("threshold") Double threshold);

    // Find high mastery skills
    @Query("SELECT sm FROM SkillMastery sm WHERE sm.student.id = :studentId AND sm.masteryLevel >= :threshold AND sm.isDeleted = false ORDER BY sm.masteryLevel DESC")
    List<SkillMastery> findHighMasterySkills(@Param("studentId") Long studentId, @Param("threshold") Double threshold);

    // Find skills by owner
    List<SkillMastery> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    // Find skills needing decay
    @Query("SELECT sm FROM SkillMastery sm WHERE sm.lastPracticedAt < :cutoffDate AND sm.isDeleted = false")
    List<SkillMastery> findSkillsNeedingDecay(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Get average mastery for a student
    @Query("SELECT AVG(sm.masteryLevel) FROM SkillMastery sm WHERE sm.student.id = :studentId AND sm.isDeleted = false")
    Double getAverageMasteryByStudent(@Param("studentId") Long studentId);

    // Get mastery statistics by subject
    @Query("SELECT sm.subject.id, AVG(sm.masteryLevel), COUNT(sm) FROM SkillMastery sm WHERE sm.student.id = :studentId AND sm.isDeleted = false GROUP BY sm.subject.id")
    List<Object[]> getMasteryStatisticsBySubject(@Param("studentId") Long studentId);

    // Count skills by mastery level
    @Query("SELECT " +
           "SUM(CASE WHEN sm.masteryLevel >= 80 THEN 1 ELSE 0 END) as high, " +
           "SUM(CASE WHEN sm.masteryLevel >= 50 AND sm.masteryLevel < 80 THEN 1 ELSE 0 END) as medium, " +
           "SUM(CASE WHEN sm.masteryLevel < 50 THEN 1 ELSE 0 END) as low " +
           "FROM SkillMastery sm WHERE sm.student.id = :studentId AND sm.isDeleted = false")
    Object[] countSkillsByMasteryLevel(@Param("studentId") Long studentId);

    // Find skills by difficulty
    List<SkillMastery> findByStudentIdAndLastDifficultyAndIsDeletedFalse(Long studentId, SkillMastery.DifficultyLevel difficulty);

    // Find skills with consecutive incorrect attempts
    @Query("SELECT sm FROM SkillMastery sm WHERE sm.student.id = :studentId AND sm.consecutiveIncorrect >= :threshold AND sm.isDeleted = false ORDER BY sm.consecutiveIncorrect DESC")
    List<SkillMastery> findSkillsWithStruggles(@Param("studentId") Long studentId, @Param("threshold") Integer threshold);

    // Get velocity statistics
    @Query("SELECT AVG(sm.velocityScore), MAX(sm.velocityScore), MIN(sm.velocityScore) FROM SkillMastery sm WHERE sm.student.id = :studentId AND sm.isDeleted = false")
    Object[] getVelocityStatistics(@Param("studentId") Long studentId);
}
