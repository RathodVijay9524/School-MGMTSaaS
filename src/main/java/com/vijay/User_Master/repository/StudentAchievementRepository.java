package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.StudentAchievement;
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
 * Repository for StudentAchievement entity
 */
@Repository
public interface StudentAchievementRepository extends JpaRepository<StudentAchievement, Long> {

    // Find student achievements by owner
    List<StudentAchievement> findByOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(Long ownerId);
    
    // Find student achievements by student
    List<StudentAchievement> findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(Long studentId, Long ownerId);
    
    // Find earned student achievements by student
    List<StudentAchievement> findByStudentIdAndOwnerIdAndIsEarnedTrueAndIsDeletedFalseOrderByEarnedDateDesc(Long studentId, Long ownerId);
    
    // Find student achievement by student and achievement
    Optional<StudentAchievement> findByStudentIdAndAchievementIdAndOwnerIdAndIsDeletedFalse(Long studentId, Long achievementId, Long ownerId);
    
    // Check if student has earned specific achievement
    boolean existsByStudentIdAndAchievementIdAndIsEarnedTrue(Long studentId, Long achievementId);
    
    // Find student achievements by achievement
    List<StudentAchievement> findByAchievementIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(Long achievementId, Long ownerId);
    
    // Find earned student achievements by achievement
    List<StudentAchievement> findByAchievementIdAndOwnerIdAndIsEarnedTrueAndIsDeletedFalseOrderByEarnedDateDesc(Long achievementId, Long ownerId);
    
    // Find student achievements by category
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.achievement.category = :category AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findByOwnerIdAndAchievementCategory(@Param("ownerId") Long ownerId, @Param("category") String category);
    
    // Find student achievements by achievement type
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.achievement.achievementType = :achievementType AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findByOwnerIdAndAchievementType(@Param("ownerId") Long ownerId, @Param("achievementType") String achievementType);
    
    // Find student achievements by difficulty level
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.achievement.difficultyLevel = :difficultyLevel AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findByOwnerIdAndAchievementDifficultyLevel(@Param("ownerId") Long ownerId, @Param("difficultyLevel") String difficultyLevel);
    
    // Find student achievements by rarity
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.achievement.rarity = :rarity AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findByOwnerIdAndAchievementRarity(@Param("ownerId") Long ownerId, @Param("rarity") String rarity);
    
    // Find student achievements by earned date range
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.earnedDate BETWEEN :startDate AND :endDate AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findByOwnerIdAndEarnedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find student achievements earned after date
    List<StudentAchievement> findByOwnerIdAndEarnedDateAfterAndIsEarnedTrueOrderByEarnedDateDesc(Long ownerId, LocalDateTime earnedDate);
    
    // Find student achievements by progress percentage
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.progressPercentage BETWEEN :minProgress AND :maxProgress AND sa.isDeleted = false ORDER BY sa.progressPercentage DESC")
    List<StudentAchievement> findByOwnerIdAndProgressPercentageRange(@Param("ownerId") Long ownerId, @Param("minProgress") Double minProgress, @Param("maxProgress") Double maxProgress);
    
    // Find incomplete student achievements
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.progressPercentage < 100.0 AND sa.isDeleted = false ORDER BY sa.progressPercentage DESC")
    List<StudentAchievement> findIncompleteStudentAchievements(@Param("ownerId") Long ownerId);
    
    // Find displayed student achievements
    List<StudentAchievement> findByOwnerIdAndIsDisplayedTrueAndIsDeletedFalseOrderByDisplayDateDesc(Long ownerId);
    
    // Find non-displayed student achievements
    List<StudentAchievement> findByOwnerIdAndIsDisplayedFalseAndIsEarnedTrueAndIsDeletedFalseOrderByEarnedDateDesc(Long ownerId);
    
    // Search student achievements by keyword
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND " +
           "(LOWER(sa.achievement.achievementName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sa.achievement.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sa.student.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND sa.isDeleted = false")
    Page<StudentAchievement> searchStudentAchievements(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Count student achievements by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count earned student achievements by owner
    long countByOwnerIdAndIsEarnedTrueAndIsDeletedFalse(Long ownerId);
    
    // Count student achievements by student
    long countByStudentIdAndOwnerIdAndIsDeletedFalse(Long studentId, Long ownerId);
    
    // Count earned student achievements by student
    long countByStudentIdAndOwnerIdAndIsEarnedTrueAndIsDeletedFalse(Long studentId, Long ownerId);
    
    // Count student achievements by achievement
    long countByAchievementIdAndOwnerIdAndIsDeletedFalse(Long achievementId, Long ownerId);
    
    // Count earned student achievements by achievement
    long countByAchievementIdAndOwnerIdAndIsEarnedTrueAndIsDeletedFalse(Long achievementId, Long ownerId);
    
    // Get student achievement statistics
    @Query("SELECT COUNT(sa) as totalAchievements, " +
           "COUNT(CASE WHEN sa.isEarned = true THEN 1 END) as earnedAchievements, " +
           "COUNT(CASE WHEN sa.isDisplayed = true THEN 1 END) as displayedAchievements, " +
           "AVG(sa.progressPercentage) as avgProgress, " +
           "SUM(sa.achievement.pointsValue) as totalPoints, " +
           "SUM(sa.achievement.xpValue) as totalXp " +
           "FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.isDeleted = false")
    Object[] getStudentAchievementStatistics(@Param("ownerId") Long ownerId);
    
    // Get student statistics by student
    @Query("SELECT COUNT(sa) as totalAchievements, " +
           "COUNT(CASE WHEN sa.isEarned = true THEN 1 END) as earnedAchievements, " +
           "AVG(sa.progressPercentage) as avgProgress, " +
           "SUM(sa.achievement.pointsValue) as totalPoints, " +
           "SUM(sa.achievement.xpValue) as totalXp " +
           "FROM StudentAchievement sa WHERE sa.student.id = :studentId AND sa.owner.id = :ownerId AND sa.isDeleted = false")
    Object[] getStudentStatistics(@Param("studentId") Long studentId, @Param("ownerId") Long ownerId);
    
    // Get category-wise statistics
    @Query("SELECT sa.achievement.category, COUNT(sa) as achievementCount, " +
           "COUNT(CASE WHEN sa.isEarned = true THEN 1 END) as earnedCount, " +
           "AVG(sa.progressPercentage) as avgProgress " +
           "FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.isDeleted = false " +
           "GROUP BY sa.achievement.category ORDER BY achievementCount DESC")
    List<Object[]> getCategoryWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get student leaderboard by points
    @Query("SELECT sa.student.id as studentId, sa.student.name as studentName, " +
           "COUNT(sa) as totalAchievements, " +
           "COUNT(CASE WHEN sa.isEarned = true THEN 1 END) as earnedAchievements, " +
           "SUM(CASE WHEN sa.isEarned = true THEN sa.achievement.pointsValue ELSE 0 END) as totalPoints " +
           "FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.isDeleted = false " +
           "GROUP BY sa.student.id, sa.student.name " +
           "ORDER BY totalPoints DESC")
    List<Object[]> getStudentLeaderboardByPoints(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Get student leaderboard by XP
    @Query("SELECT sa.student.id as studentId, sa.student.name as studentName, " +
           "COUNT(sa) as totalAchievements, " +
           "COUNT(CASE WHEN sa.isEarned = true THEN 1 END) as earnedAchievements, " +
           "SUM(CASE WHEN sa.isEarned = true THEN sa.achievement.xpValue ELSE 0 END) as totalXp " +
           "FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.isDeleted = false " +
           "GROUP BY sa.student.id, sa.student.name " +
           "ORDER BY totalXp DESC")
    List<Object[]> getStudentLeaderboardByXp(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Get recent student achievements
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.earnedDate >= :since AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findRecentStudentAchievements(@Param("ownerId") Long ownerId, @Param("since") LocalDateTime since);
    
    // Find student achievements by context data
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.contextData LIKE %:contextData% AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findByOwnerIdAndContextDataContaining(@Param("ownerId") Long ownerId, @Param("contextData") String contextData);
    
    // Find student achievements by notes
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.notes LIKE %:notes% AND sa.isDeleted = false ORDER BY sa.earnedDate DESC")
    List<StudentAchievement> findByOwnerIdAndNotesContaining(@Param("ownerId") Long ownerId, @Param("notes") String notes);
    
    // Soft delete by owner
    @Query("UPDATE StudentAchievement sa SET sa.isDeleted = true WHERE sa.id = :id AND sa.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Soft delete by student
    @Query("UPDATE StudentAchievement sa SET sa.isDeleted = true WHERE sa.student.id = :studentId AND sa.owner.id = :ownerId")
    void softDeleteByStudentIdAndOwnerId(@Param("studentId") Long studentId, @Param("ownerId") Long ownerId);
    
    // Find student achievements created in date range
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.createdOn BETWEEN :startDate AND :endDate AND sa.isDeleted = false ORDER BY sa.createdOn DESC")
    List<StudentAchievement> findByOwnerIdAndCreatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find student achievements updated in date range
    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.owner.id = :ownerId AND sa.updatedOn BETWEEN :startDate AND :endDate AND sa.isDeleted = false ORDER BY sa.updatedOn DESC")
    List<StudentAchievement> findByOwnerIdAndUpdatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
