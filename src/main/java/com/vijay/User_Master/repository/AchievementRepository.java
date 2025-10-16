package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Achievement;
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
 * Repository for Achievement entity
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // Find achievements by owner
    Page<Achievement> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Pageable pageable);
    
    // Find active achievements
    List<Achievement> findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find achievements by type
    List<Achievement> findByOwnerIdAndAchievementTypeAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Achievement.AchievementType type);
    
    // Find achievements by category
    List<Achievement> findByOwnerIdAndCategoryAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Achievement.Category category);
    
    // Find achievements by difficulty level
    List<Achievement> findByOwnerIdAndDifficultyLevelAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Achievement.DifficultyLevel difficultyLevel);
    
    // Find achievements by rarity
    List<Achievement> findByOwnerIdAndRarityAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Achievement.Rarity rarity);
    
    // Find repeatable achievements
    List<Achievement> findByOwnerIdAndIsRepeatableTrueAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find secret achievements
    List<Achievement> findByOwnerIdAndIsSecretTrueAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find achievements by points range
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.pointsValue BETWEEN :minPoints AND :maxPoints AND a.isDeleted = false ORDER BY a.pointsValue DESC")
    List<Achievement> findByOwnerIdAndPointsRange(@Param("ownerId") Long ownerId, @Param("minPoints") Integer minPoints, @Param("maxPoints") Integer maxPoints);
    
    // Find achievements by XP range
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.xpValue BETWEEN :minXp AND :maxXp AND a.isDeleted = false ORDER BY a.xpValue DESC")
    List<Achievement> findByOwnerIdAndXpRange(@Param("ownerId") Long ownerId, @Param("minXp") Integer minXp, @Param("maxXp") Integer maxXp);
    
    // Find available achievements (not expired, active)
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.isActive = true AND a.isDeleted = false AND (a.expiryDate IS NULL OR a.expiryDate > :now) ORDER BY a.pointsValue DESC")
    List<Achievement> findAvailableAchievements(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Find expired achievements
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.expiryDate IS NOT NULL AND a.expiryDate <= :now AND a.isDeleted = false ORDER BY a.expiryDate DESC")
    List<Achievement> findExpiredAchievements(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Find achievements expiring soon
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.expiryDate IS NOT NULL AND a.expiryDate BETWEEN :now AND :futureDate AND a.isDeleted = false ORDER BY a.expiryDate ASC")
    List<Achievement> findAchievementsExpiringSoon(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);
    
    // Search achievements by keyword
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND " +
           "(LOWER(a.achievementName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND a.isDeleted = false")
    Page<Achievement> searchAchievements(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Count achievements by type
    long countByOwnerIdAndAchievementTypeAndIsDeletedFalse(Long ownerId, Achievement.AchievementType type);
    
    // Count achievements by category
    long countByOwnerIdAndCategoryAndIsDeletedFalse(Long ownerId, Achievement.Category category);
    
    // Count achievements by rarity
    long countByOwnerIdAndRarityAndIsDeletedFalse(Long ownerId, Achievement.Rarity rarity);
    
    // Count active achievements
    long countByOwnerIdAndIsActiveTrueAndIsDeletedFalse(Long ownerId);
    
    // Get achievement statistics
    @Query("SELECT COUNT(a) as totalAchievements, " +
           "COUNT(CASE WHEN a.isActive = true THEN 1 END) as activeAchievements, " +
           "COUNT(CASE WHEN a.isRepeatable = true THEN 1 END) as repeatableAchievements, " +
           "COUNT(CASE WHEN a.isSecret = true THEN 1 END) as secretAchievements, " +
           "AVG(a.pointsValue) as avgPoints, " +
           "AVG(a.xpValue) as avgXp " +
           "FROM Achievement a WHERE a.owner.id = :ownerId AND a.isDeleted = false")
    Object[] getAchievementStatistics(@Param("ownerId") Long ownerId);
    
    // Get category-wise statistics
    @Query("SELECT a.category, COUNT(a) as achievementCount, AVG(a.pointsValue) as avgPoints " +
           "FROM Achievement a WHERE a.owner.id = :ownerId AND a.isDeleted = false " +
           "GROUP BY a.category ORDER BY achievementCount DESC")
    List<Object[]> getCategoryWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get rarity-wise statistics
    @Query("SELECT a.rarity, COUNT(a) as achievementCount, AVG(a.pointsValue) as avgPoints " +
           "FROM Achievement a WHERE a.owner.id = :ownerId AND a.isDeleted = false " +
           "GROUP BY a.rarity ORDER BY achievementCount DESC")
    List<Object[]> getRarityWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Find achievements by unlock date
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.unlockDate BETWEEN :startDate AND :endDate AND a.isDeleted = false ORDER BY a.unlockDate DESC")
    List<Achievement> findByOwnerIdAndUnlockDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find highest value achievements
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.isDeleted = false ORDER BY a.pointsValue DESC")
    List<Achievement> findHighestValueAchievements(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Find achievements by criteria
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.criteria LIKE %:criteria% AND a.isDeleted = false ORDER BY a.pointsValue DESC")
    List<Achievement> findByOwnerIdAndCriteriaContaining(@Param("ownerId") Long ownerId, @Param("criteria") String criteria);
    
    // Check if achievement name exists for owner
    boolean existsByAchievementNameAndOwnerIdAndIsDeletedFalse(String achievementName, Long ownerId);
    
    // Find achievement by name and owner
    Optional<Achievement> findByAchievementNameAndOwnerIdAndIsDeletedFalse(String achievementName, Long ownerId);
    
    // Soft delete by owner
    @Query("UPDATE Achievement a SET a.isDeleted = true WHERE a.id = :id AND a.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Find achievements created in date range
    @Query("SELECT a FROM Achievement a WHERE a.owner.id = :ownerId AND a.createdOn BETWEEN :startDate AND :endDate AND a.isDeleted = false ORDER BY a.createdOn DESC")
    List<Achievement> findByOwnerIdAndCreatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
