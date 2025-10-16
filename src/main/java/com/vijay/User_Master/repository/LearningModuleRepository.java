package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LearningModule;
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
 * Repository for LearningModule entity
 */
@Repository
public interface LearningModuleRepository extends JpaRepository<LearningModule, Long> {

    // Find learning modules by learning path
    List<LearningModule> findByLearningPathIdAndIsDeletedFalseOrderByOrderIndexAsc(Long learningPathId);
    
    // Find learning modules by student
    List<LearningModule> findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long studentId, Long ownerId);
    
    // Find learning modules by subject through learning path
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.learningPath.subject = :subject AND lm.isDeleted = false ORDER BY lm.moduleName ASC")
    List<LearningModule> findByOwnerIdAndSubjectAndIsDeletedFalseOrderByModuleNameAsc(@Param("ownerId") Long ownerId, @Param("subject") String subject);
    
    // Find learning modules by module type
    List<LearningModule> findByOwnerIdAndModuleTypeAndIsDeletedFalseOrderByModuleNameAsc(Long ownerId, LearningModule.ModuleType moduleType);
    
    // Find learning modules by difficulty level
    List<LearningModule> findByOwnerIdAndDifficultyLevelAndIsDeletedFalseOrderByModuleNameAsc(Long ownerId, LearningModule.DifficultyLevel difficultyLevel);
    
    // Find completed learning modules
    List<LearningModule> findByOwnerIdAndIsCompletedTrueAndIsDeletedFalseOrderByCompletionDateDesc(Long ownerId);
    
    // Find active learning modules
    List<LearningModule> findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByModuleNameAsc(Long ownerId);
    
    // Find learning modules by score range
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.scorePercentage BETWEEN :minScore AND :maxScore AND lm.isDeleted = false ORDER BY lm.scorePercentage DESC")
    List<LearningModule> findByOwnerIdAndScoreRange(@Param("ownerId") Long ownerId, @Param("minScore") Double minScore, @Param("maxScore") Double maxScore);
    
    // Find learning modules by time spent range
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.timeSpentMinutes BETWEEN :minMinutes AND :maxMinutes AND lm.isDeleted = false ORDER BY lm.timeSpentMinutes DESC")
    List<LearningModule> findByOwnerIdAndTimeSpentRange(@Param("ownerId") Long ownerId, @Param("minMinutes") Integer minMinutes, @Param("maxMinutes") Integer maxMinutes);
    
    // Find learning modules by attempts range
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.attemptsCount BETWEEN :minAttempts AND :maxAttempts AND lm.isDeleted = false ORDER BY lm.attemptsCount DESC")
    List<LearningModule> findByOwnerIdAndAttemptsRange(@Param("ownerId") Long ownerId, @Param("minAttempts") Integer minAttempts, @Param("maxAttempts") Integer maxAttempts);
    
    // Find passed learning modules
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.scorePercentage >= 70.0 AND lm.isDeleted = false ORDER BY lm.scorePercentage DESC")
    List<LearningModule> findPassedLearningModules(@Param("ownerId") Long ownerId);
    
    // Find failed learning modules
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.scorePercentage < 70.0 AND lm.scorePercentage IS NOT NULL AND lm.isDeleted = false ORDER BY lm.scorePercentage ASC")
    List<LearningModule> findFailedLearningModules(@Param("ownerId") Long ownerId);
    
    // Search learning modules by keyword
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND " +
           "(LOWER(lm.moduleName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(lm.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(lm.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(lm.learningObjectives) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND lm.isDeleted = false")
    Page<LearningModule> searchLearningModules(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Count learning modules by learning path
    long countByLearningPathIdAndIsDeletedFalse(Long learningPathId);
    
    // Count learning modules by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count completed learning modules
    long countByOwnerIdAndIsCompletedTrueAndIsDeletedFalse(Long ownerId);
    
    // Count learning modules by module type
    long countByOwnerIdAndModuleTypeAndIsDeletedFalse(Long ownerId, LearningModule.ModuleType moduleType);
    
    // Count learning modules by difficulty level
    long countByOwnerIdAndDifficultyLevelAndIsDeletedFalse(Long ownerId, LearningModule.DifficultyLevel difficultyLevel);
    
    // Get learning module statistics
    @Query("SELECT COUNT(lm) as totalModules, " +
           "COUNT(CASE WHEN lm.isCompleted = true THEN 1 END) as completedModules, " +
           "COUNT(CASE WHEN lm.isActive = true THEN 1 END) as activeModules, " +
           "AVG(lm.scorePercentage) as avgScore, " +
           "AVG(lm.timeSpentMinutes) as avgTimeSpent, " +
           "AVG(lm.attemptsCount) as avgAttempts " +
           "FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.isDeleted = false")
    Object[] getLearningModuleStatistics(@Param("ownerId") Long ownerId);
    
    // Get module type statistics
    @Query("SELECT lm.moduleType, COUNT(lm) as moduleCount, AVG(lm.scorePercentage) as avgScore " +
           "FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.isDeleted = false " +
           "GROUP BY lm.moduleType ORDER BY moduleCount DESC")
    List<Object[]> getModuleTypeStatistics(@Param("ownerId") Long ownerId);
    
    // Get difficulty level statistics
    @Query("SELECT lm.difficultyLevel, COUNT(lm) as moduleCount, AVG(lm.scorePercentage) as avgScore " +
           "FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.isDeleted = false " +
           "GROUP BY lm.difficultyLevel ORDER BY moduleCount DESC")
    List<Object[]> getDifficultyLevelStatistics(@Param("ownerId") Long ownerId);
    
    // Find learning modules by completion date range
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.completionDate BETWEEN :startDate AND :endDate AND lm.isDeleted = false ORDER BY lm.completionDate DESC")
    List<LearningModule> findByOwnerIdAndCompletionDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find learning modules by estimated duration range
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.estimatedDurationMinutes BETWEEN :minMinutes AND :maxMinutes AND lm.isDeleted = false ORDER BY lm.estimatedDurationMinutes ASC")
    List<LearningModule> findByOwnerIdAndEstimatedDurationRange(@Param("ownerId") Long ownerId, @Param("minMinutes") Integer minMinutes, @Param("maxMinutes") Integer maxMinutes);
    
    // Find learning modules by prerequisites
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.prerequisites LIKE %:prerequisite% AND lm.isDeleted = false ORDER BY lm.moduleName ASC")
    List<LearningModule> findByOwnerIdAndPrerequisitesContaining(@Param("ownerId") Long ownerId, @Param("prerequisite") String prerequisite);
    
    // Find learning modules by resources
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.resources LIKE %:resource% AND lm.isDeleted = false ORDER BY lm.moduleName ASC")
    List<LearningModule> findByOwnerIdAndResourcesContaining(@Param("ownerId") Long ownerId, @Param("resource") String resource);
    
    // Find learning modules by assessment questions
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.assessmentQuestions LIKE %:question% AND lm.isDeleted = false ORDER BY lm.moduleName ASC")
    List<LearningModule> findByOwnerIdAndAssessmentQuestionsContaining(@Param("ownerId") Long ownerId, @Param("question") String question);
    
    // Find learning modules by learning objectives
    @Query("SELECT lm FROM LearningModule lm WHERE lm.owner.id = :ownerId AND lm.learningObjectives LIKE %:objective% AND lm.isDeleted = false ORDER BY lm.moduleName ASC")
    List<LearningModule> findByOwnerIdAndLearningObjectivesContaining(@Param("ownerId") Long ownerId, @Param("objective") String objective);
    
    // Check if learning module name exists for learning path
    boolean existsByModuleNameAndLearningPathIdAndIsDeletedFalse(String moduleName, Long learningPathId);
    
    // Find learning module by name and learning path
    Optional<LearningModule> findByModuleNameAndLearningPathIdAndIsDeletedFalse(String moduleName, Long learningPathId);
    
    // Find learning modules by order index range
    @Query("SELECT lm FROM LearningModule lm WHERE lm.learningPath.id = :learningPathId AND lm.orderIndex BETWEEN :minOrder AND :maxOrder AND lm.isDeleted = false ORDER BY lm.orderIndex ASC")
    List<LearningModule> findByLearningPathIdAndOrderIndexRange(@Param("learningPathId") Long learningPathId, @Param("minOrder") Integer minOrder, @Param("maxOrder") Integer maxOrder);
    
    // Soft delete by owner
    @Query("UPDATE LearningModule lm SET lm.isDeleted = true WHERE lm.id = :id AND lm.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
}
