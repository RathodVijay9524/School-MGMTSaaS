package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LearningPath;
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
 * Repository for LearningPath entity
 */
@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {

    // Find learning paths by owner
    Page<LearningPath> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Pageable pageable);
    
    // Find learning paths by student
    List<LearningPath> findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long studentId, Long ownerId);
    
    // Find learning paths by subject
    List<LearningPath> findByOwnerIdAndSubjectAndIsDeletedFalseOrderByPathNameAsc(Long ownerId, String subject);
    
    // Find active learning paths by subject
    List<LearningPath> findByOwnerIdAndSubjectAndIsActiveTrueAndIsDeletedFalseOrderByPathNameAsc(Long ownerId, String subject);
    
    // Find learning paths by grade level
    List<LearningPath> findByOwnerIdAndGradeLevelAndIsDeletedFalseOrderByPathNameAsc(Long ownerId, String gradeLevel);
    
    // Find learning paths by difficulty level
    List<LearningPath> findByOwnerIdAndDifficultyLevelAndIsDeletedFalseOrderByPathNameAsc(Long ownerId, LearningPath.DifficultyLevel difficultyLevel);
    
    // Find active learning paths
    List<LearningPath> findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByPathNameAsc(Long ownerId);
    
    // Find adaptive learning paths
    List<LearningPath> findByOwnerIdAndIsAdaptiveTrueAndIsDeletedFalseOrderByPathNameAsc(Long ownerId);
    
    // Find completed learning paths
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.completionPercentage >= 100.0 AND lp.isDeleted = false ORDER BY lp.updatedOn DESC")
    List<LearningPath> findCompletedLearningPaths(@Param("ownerId") Long ownerId);
    
    // Find learning paths in progress
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.completionPercentage > 0.0 AND lp.completionPercentage < 100.0 AND lp.isDeleted = false ORDER BY lp.completionPercentage DESC")
    List<LearningPath> findLearningPathsInProgress(@Param("ownerId") Long ownerId);
    
    // Find learning paths by completion percentage range
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.completionPercentage BETWEEN :minPercentage AND :maxPercentage AND lp.isDeleted = false ORDER BY lp.completionPercentage DESC")
    List<LearningPath> findByOwnerIdAndCompletionPercentageRange(@Param("ownerId") Long ownerId, @Param("minPercentage") Double minPercentage, @Param("maxPercentage") Double maxPercentage);
    
    // Find learning paths by mastery level range
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.masteryLevel BETWEEN :minMastery AND :maxMastery AND lp.isDeleted = false ORDER BY lp.masteryLevel DESC")
    List<LearningPath> findByOwnerIdAndMasteryLevelRange(@Param("ownerId") Long ownerId, @Param("minMastery") Double minMastery, @Param("maxMastery") Double maxMastery);
    
    // Search learning paths by keyword
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND " +
           "(LOWER(lp.pathName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(lp.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(lp.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(lp.learningObjectives) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND lp.isDeleted = false")
    Page<LearningPath> searchLearningPaths(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Count learning paths by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count active learning paths
    long countByOwnerIdAndIsActiveTrueAndIsDeletedFalse(Long ownerId);
    
    // Count completed learning paths (completion percentage >= 100%)
    @Query("SELECT COUNT(lp) FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.completionPercentage >= 100.0 AND lp.isDeleted = false")
    long countByOwnerIdAndIsCompletedTrueAndIsDeletedFalse(@Param("ownerId") Long ownerId);
    
    // Count learning paths by subject
    long countByOwnerIdAndSubjectAndIsDeletedFalse(Long ownerId, String subject);
    
    // Count learning paths by grade level
    long countByOwnerIdAndGradeLevelAndIsDeletedFalse(Long ownerId, String gradeLevel);
    
    // Count completed learning paths (query method)
    @Query("SELECT COUNT(lp) FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.completionPercentage >= 100.0 AND lp.isDeleted = false")
    long countCompletedLearningPaths(@Param("ownerId") Long ownerId);
    
    // Count learning paths in progress
    @Query("SELECT COUNT(lp) FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.completionPercentage > 0.0 AND lp.completionPercentage < 100.0 AND lp.isDeleted = false")
    long countLearningPathsInProgress(@Param("ownerId") Long ownerId);
    
    // Get learning path statistics
    @Query("SELECT COUNT(lp) as totalPaths, " +
           "COUNT(CASE WHEN lp.isActive = true THEN 1 END) as activePaths, " +
           "COUNT(CASE WHEN lp.completionPercentage >= 100.0 THEN 1 END) as completedPaths, " +
           "COUNT(CASE WHEN lp.completionPercentage > 0.0 AND lp.completionPercentage < 100.0 THEN 1 END) as inProgressPaths, " +
           "AVG(lp.completionPercentage) as avgCompletion, " +
           "AVG(lp.masteryLevel) as avgMastery, " +
           "AVG(lp.estimatedDurationHours) as avgDuration " +
           "FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.isDeleted = false")
    Object[] getLearningPathStatistics(@Param("ownerId") Long ownerId);
    
    // Get subject-wise statistics
    @Query("SELECT lp.subject, COUNT(lp) as pathCount, AVG(lp.completionPercentage) as avgCompletion " +
           "FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.isDeleted = false " +
           "GROUP BY lp.subject ORDER BY pathCount DESC")
    List<Object[]> getSubjectWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get grade-wise statistics
    @Query("SELECT lp.gradeLevel, COUNT(lp) as pathCount, AVG(lp.masteryLevel) as avgMastery " +
           "FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.isDeleted = false " +
           "GROUP BY lp.gradeLevel ORDER BY pathCount DESC")
    List<Object[]> getGradeWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Find learning paths by duration range
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.estimatedDurationHours BETWEEN :minHours AND :maxHours AND lp.isDeleted = false ORDER BY lp.estimatedDurationHours ASC")
    List<LearningPath> findByOwnerIdAndDurationRange(@Param("ownerId") Long ownerId, @Param("minHours") Integer minHours, @Param("maxHours") Integer maxHours);
    
    // Find learning paths created in date range
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.createdOn BETWEEN :startDate AND :endDate AND lp.isDeleted = false ORDER BY lp.createdOn DESC")
    List<LearningPath> findByOwnerIdAndCreatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find learning paths updated in date range
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.updatedOn BETWEEN :startDate AND :endDate AND lp.isDeleted = false ORDER BY lp.updatedOn DESC")
    List<LearningPath> findByOwnerIdAndUpdatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find learning paths by prerequisites
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.prerequisites LIKE %:prerequisite% AND lp.isDeleted = false ORDER BY lp.pathName ASC")
    List<LearningPath> findByOwnerIdAndPrerequisitesContaining(@Param("ownerId") Long ownerId, @Param("prerequisite") String prerequisite);
    
    // Find learning paths by learning objectives
    @Query("SELECT lp FROM LearningPath lp WHERE lp.owner.id = :ownerId AND lp.learningObjectives LIKE %:objective% AND lp.isDeleted = false ORDER BY lp.pathName ASC")
    List<LearningPath> findByOwnerIdAndLearningObjectivesContaining(@Param("ownerId") Long ownerId, @Param("objective") String objective);
    
    // Check if learning path name exists for owner
    boolean existsByPathNameAndOwnerIdAndIsDeletedFalse(String pathName, Long ownerId);
    
    // Find learning path by name and owner
    Optional<LearningPath> findByPathNameAndOwnerIdAndIsDeletedFalse(String pathName, Long ownerId);
    
    // Soft delete by owner
    @Query("UPDATE LearningPath lp SET lp.isDeleted = true WHERE lp.id = :id AND lp.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
}
