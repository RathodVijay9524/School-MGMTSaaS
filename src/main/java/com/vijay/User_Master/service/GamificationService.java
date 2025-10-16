package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Gamification System
 */
public interface GamificationService {

    /**
     * Create a new achievement
     */
    AchievementResponse createAchievement(AchievementRequest request, Long ownerId);

    /**
     * Get achievement by ID
     */
    AchievementResponse getAchievementById(Long id, Long ownerId);

    /**
     * Get all achievements with pagination
     */
    Page<AchievementResponse> getAllAchievements(Long ownerId, Pageable pageable);

    /**
     * Get achievements by type
     */
    List<AchievementResponse> getAchievementsByType(Long ownerId, String type);

    /**
     * Get achievements by category
     */
    List<AchievementResponse> getAchievementsByCategory(Long ownerId, String category);

    /**
     * Get achievements by difficulty level
     */
    List<AchievementResponse> getAchievementsByDifficulty(Long ownerId, String difficultyLevel);

    /**
     * Get achievements by rarity
     */
    List<AchievementResponse> getAchievementsByRarity(Long ownerId, String rarity);

    /**
     * Search achievements by keyword
     */
    Page<AchievementResponse> searchAchievements(Long ownerId, String keyword, Pageable pageable);

    /**
     * Get available achievements
     */
    List<AchievementResponse> getAvailableAchievements(Long ownerId);

    /**
     * Award achievement to student
     */
    StudentAchievementResponse awardAchievement(Long achievementId, Long studentId, Long ownerId);

    /**
     * Get student achievements
     */
    List<StudentAchievementResponse> getStudentAchievements(Long studentId, Long ownerId);

    /**
     * Get student achievement by ID
     */
    StudentAchievementResponse getStudentAchievementById(Long id, Long ownerId);

    /**
     * Update student achievement progress
     */
    StudentAchievementResponse updateAchievementProgress(Long achievementId, Long studentId, int progress, Long ownerId);

    /**
     * Get achievement statistics
     */
    Map<String, Object> getAchievementStatistics(Long ownerId);

    /**
     * Get student gamification profile
     */
    Map<String, Object> getStudentGamificationProfile(Long studentId, Long ownerId);

    /**
     * Get leaderboard
     */
    List<Map<String, Object>> getLeaderboard(Long ownerId, String category, int limit);

    /**
     * Get class leaderboard
     */
    List<Map<String, Object>> getClassLeaderboard(Long ownerId, String gradeLevel, int limit);

    /**
     * Get subject leaderboard
     */
    List<Map<String, Object>> getSubjectLeaderboard(Long ownerId, String subject, int limit);

    /**
     * Check and award achievements
     */
    List<StudentAchievementResponse> checkAndAwardAchievements(Long studentId, String activityType, Map<String, Object> activityData, Long ownerId);

    /**
     * Get recent achievements
     */
    List<StudentAchievementResponse> getRecentAchievements(Long ownerId, int days);

    /**
     * Get achievement categories
     */
    List<Map<String, Object>> getAchievementCategories(Long ownerId);

    /**
     * Get student points
     */
    Integer getStudentPoints(Long studentId, Long ownerId);

    /**
     * Get student XP
     */
    Integer getStudentXP(Long studentId, Long ownerId);

    /**
     * Get student level
     */
    Integer getStudentLevel(Long studentId, Long ownerId);

    /**
     * Get student badges
     */
    List<AchievementResponse> getStudentBadges(Long studentId, Long ownerId);

    /**
     * Get student streaks
     */
    Map<String, Object> getStudentStreaks(Long studentId, Long ownerId);

    /**
     * Update student activity
     */
    void updateStudentActivity(Long studentId, String activityType, Map<String, Object> activityData, Long ownerId);

    /**
     * Get daily challenges
     */
    List<AchievementResponse> getDailyChallenges(Long ownerId);

    /**
     * Get weekly challenges
     */
    List<AchievementResponse> getWeeklyChallenges(Long ownerId);

    /**
     * Get monthly challenges
     */
    List<AchievementResponse> getMonthlyChallenges(Long ownerId);

    /**
     * Complete challenge
     */
    StudentAchievementResponse completeChallenge(Long challengeId, Long studentId, Long ownerId);

    /**
     * Get gamification dashboard
     */
    Map<String, Object> getGamificationDashboard(Long ownerId);

    /**
     * Get student progress
     */
    Map<String, Object> getStudentProgress(Long studentId, Long ownerId);

    /**
     * Reset student progress
     */
    void resetStudentProgress(Long studentId, Long ownerId);

    /**
     * Delete achievement
     */
    void deleteAchievement(Long id, Long ownerId);

    /**
     * Update achievement
     */
    AchievementResponse updateAchievement(Long id, AchievementRequest request, Long ownerId);

    /**
     * Get achievement analytics
     */
    Map<String, Object> getAchievementAnalytics(Long ownerId);
}
