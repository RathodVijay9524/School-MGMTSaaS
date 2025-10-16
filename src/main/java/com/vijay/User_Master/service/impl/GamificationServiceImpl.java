package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.GamificationService;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Gamification Service
 */
@Service
@Slf4j
@Transactional
public class GamificationServiceImpl implements GamificationService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private StudentAchievementRepository studentAchievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    @Transactional
    public AchievementResponse createAchievement(AchievementRequest request, Long ownerId) {
        log.info("Creating achievement: {} for owner: {}", request.getAchievementName(), ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Achievement achievement = Achievement.builder()
                .achievementName(request.getAchievementName())
                .description(request.getDescription())
                .badgeIcon(request.getBadgeIcon())
                .badgeColor(request.getBadgeColor())
                .achievementType(parseAchievementType(request.getAchievementType()))
                .category(parseCategory(request.getCategory()))
                .difficultyLevel(parseDifficultyLevel(request.getDifficultyLevel()))
                .pointsValue(request.getPointsValue())
                .xpValue(request.getXpValue())
                .criteria(request.getCriteria())
                .requirements(request.getRequirements())
                .isRepeatable(request.getIsRepeatable())
                .isSecret(request.getIsSecret())
                .isActive(request.getIsActive())
                .unlockDate(request.getUnlockDate())
                .expiryDate(request.getExpiryDate())
                .rarity(parseRarity(request.getRarity()))
                .owner(owner)
                .build();

        Achievement savedAchievement = achievementRepository.save(achievement);
        return mapToAchievementResponse(savedAchievement);
    }

    @Override
    public AchievementResponse getAchievementById(Long id, Long ownerId) {
        Achievement achievement = achievementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));

        return mapToAchievementResponse(achievement);
    }

    @Override
    public Page<AchievementResponse> getAllAchievements(Long ownerId, Pageable pageable) {
        Page<Achievement> achievements = achievementRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, pageable);
        
        List<AchievementResponse> responses = achievements.getContent().stream()
                .map(this::mapToAchievementResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, achievements.getTotalElements());
    }

    @Override
    public List<AchievementResponse> getAchievementsByType(Long ownerId, String type) {
        Achievement.AchievementType achievementType = parseAchievementType(type);
        List<Achievement> achievements = achievementRepository.findByOwnerIdAndAchievementTypeAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, achievementType);
        
        return achievements.stream()
                .map(this::mapToAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getAchievementsByCategory(Long ownerId, String category) {
        Achievement.Category achievementCategory = parseCategory(category);
        List<Achievement> achievements = achievementRepository.findByOwnerIdAndCategoryAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, achievementCategory);
        
        return achievements.stream()
                .map(this::mapToAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getAchievementsByDifficulty(Long ownerId, String difficultyLevel) {
        Achievement.DifficultyLevel difficulty = parseDifficultyLevel(difficultyLevel);
        List<Achievement> achievements = achievementRepository.findByOwnerIdAndDifficultyLevelAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, difficulty);
        
        return achievements.stream()
                .map(this::mapToAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getAchievementsByRarity(Long ownerId, String rarity) {
        Achievement.Rarity achievementRarity = parseRarity(rarity);
        List<Achievement> achievements = achievementRepository.findByOwnerIdAndRarityAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, achievementRarity);
        
        return achievements.stream()
                .map(this::mapToAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AchievementResponse> searchAchievements(Long ownerId, String keyword, Pageable pageable) {
        Page<Achievement> achievements = achievementRepository.searchAchievements(ownerId, keyword, pageable);
        
        List<AchievementResponse> responses = achievements.getContent().stream()
                .map(this::mapToAchievementResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, achievements.getTotalElements());
    }

    @Override
    public List<AchievementResponse> getAvailableAchievements(Long ownerId) {
        List<Achievement> achievements = achievementRepository.findAvailableAchievements(ownerId, LocalDateTime.now());
        
        return achievements.stream()
                .map(this::mapToAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentAchievementResponse awardAchievement(Long achievementId, Long studentId, Long ownerId) {
        log.info("Awarding achievement {} to student {}", achievementId, studentId);

        Achievement achievement = achievementRepository.findById(achievementId)
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", achievementId));

        Worker student = workerRepository.findById(studentId)
                .filter(w -> w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        // Check if achievement is already earned (if not repeatable)
        if (!achievement.getIsRepeatable()) {
            boolean alreadyEarned = studentAchievementRepository.existsByStudentIdAndAchievementIdAndIsEarnedTrue(studentId, achievementId);
            if (alreadyEarned) {
                throw new IllegalStateException("Achievement already earned by student");
            }
        }

        StudentAchievement studentAchievement = StudentAchievement.builder()
                .student(student)
                .achievement(achievement)
                .earnedDate(LocalDateTime.now())
                .progressPercentage(100.0)
                .isEarned(true)
                .isDisplayed(false)
                .owner(owner)
                .build();

        StudentAchievement savedStudentAchievement = studentAchievementRepository.save(studentAchievement);
        return mapToStudentAchievementResponse(savedStudentAchievement);
    }

    @Override
    public List<StudentAchievementResponse> getStudentAchievements(Long studentId, Long ownerId) {
        List<StudentAchievement> studentAchievements = studentAchievementRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(studentId, ownerId);
        
        return studentAchievements.stream()
                .map(this::mapToStudentAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentAchievementResponse getStudentAchievementById(Long id, Long ownerId) {
        StudentAchievement studentAchievement = studentAchievementRepository.findById(id)
                .filter(sa -> sa.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudentAchievement", "id", id));

        return mapToStudentAchievementResponse(studentAchievement);
    }

    @Override
    @Transactional
    public StudentAchievementResponse updateAchievementProgress(Long achievementId, Long studentId, int progress, Long ownerId) {
        StudentAchievement studentAchievement = studentAchievementRepository
                .findByStudentIdAndAchievementIdAndOwnerIdAndIsDeletedFalse(studentId, achievementId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentAchievement", "achievementId", achievementId));

        studentAchievement.updateProgress(progress);

        // Check if achievement should be earned
        if (studentAchievement.getProgressPercentage() >= 100.0 && !studentAchievement.getIsEarned()) {
            studentAchievement.earn();
        }

        StudentAchievement savedStudentAchievement = studentAchievementRepository.save(studentAchievement);
        return mapToStudentAchievementResponse(savedStudentAchievement);
    }

    @Override
    public Map<String, Object> getAchievementStatistics(Long ownerId) {
        Object[] stats = achievementRepository.getAchievementStatistics(ownerId);
        
        Map<String, Object> statistics = new HashMap<>();
        if (stats != null && stats.length >= 6) {
            statistics.put("totalAchievements", stats[0]);
            statistics.put("activeAchievements", stats[1]);
            statistics.put("repeatableAchievements", stats[2]);
            statistics.put("secretAchievements", stats[3]);
            statistics.put("avgPoints", stats[4]);
            statistics.put("avgXp", stats[5]);
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStudentGamificationProfile(Long studentId, Long ownerId) {
        Map<String, Object> profile = new HashMap<>();
        
        // Get student achievements
        List<StudentAchievement> studentAchievements = studentAchievementRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(studentId, ownerId);
        
        // Calculate total points and XP
        int totalPoints = studentAchievements.stream()
                .filter(sa -> sa.getIsEarned())
                .mapToInt(sa -> sa.getAchievement().getPointsValue() != null ? sa.getAchievement().getPointsValue() : 0)
                .sum();
        
        int totalXp = studentAchievements.stream()
                .filter(sa -> sa.getIsEarned())
                .mapToInt(sa -> sa.getAchievement().getXpValue() != null ? sa.getAchievement().getXpValue() : 0)
                .sum();
        
        // Calculate level (every 1000 XP = 1 level)
        int level = (totalXp / 1000) + 1;
        
        // Get achievements by category
        Map<String, Long> achievementsByCategory = studentAchievements.stream()
                .filter(sa -> sa.getIsEarned())
                .collect(Collectors.groupingBy(
                        sa -> sa.getAchievement().getCategory().name(),
                        Collectors.counting()
                ));
        
        // Get recent achievements
        List<StudentAchievement> recentAchievements = studentAchievements.stream()
                .filter(sa -> sa.getEarnedDate() != null && sa.getEarnedDate().isAfter(LocalDateTime.now().minusDays(7)))
                .limit(5)
                .collect(Collectors.toList());
        
        profile.put("studentId", studentId);
        profile.put("totalPoints", totalPoints);
        profile.put("totalXp", totalXp);
        profile.put("level", level);
        profile.put("achievementsEarned", studentAchievements.stream().filter(sa -> sa.getIsEarned()).count());
        profile.put("achievementsByCategory", achievementsByCategory);
        profile.put("recentAchievements", recentAchievements.stream().map(this::mapToStudentAchievementResponse).collect(Collectors.toList()));
        
        return profile;
    }

    @Override
    public List<Map<String, Object>> getLeaderboard(Long ownerId, String category, int limit) {
        // Get all students with their points
        List<Worker> students = workerRepository.findByOwnerIdAndIsDeletedFalseOrderByNameAsc(ownerId);
        
        List<Map<String, Object>> leaderboard = students.stream()
                .map(student -> {
                    List<StudentAchievement> studentAchievements = studentAchievementRepository
                            .findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(student.getId(), ownerId);
                    
                    int totalPoints = studentAchievements.stream()
                            .filter(sa -> sa.getIsEarned())
                            .mapToInt(sa -> sa.getAchievement().getPointsValue() != null ? sa.getAchievement().getPointsValue() : 0)
                            .sum();
                    
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("studentId", student.getId());
                    entry.put("studentName", student.getName());
                    entry.put("totalPoints", totalPoints);
                    entry.put("achievementsCount", studentAchievements.stream().filter(sa -> sa.getIsEarned()).count());
                    
                    return entry;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("totalPoints"), (Integer) a.get("totalPoints")))
                .limit(limit)
                .collect(Collectors.toList());
        
        return leaderboard;
    }

    @Override
    public List<Map<String, Object>> getClassLeaderboard(Long ownerId, String gradeLevel, int limit) {
        // Get students by grade level and create leaderboard
        List<Worker> students = workerRepository.findByOwnerIdAndIsDeletedFalseOrderByNameAsc(ownerId);
        
        return students.stream()
                .map(student -> {
                    List<StudentAchievement> studentAchievements = studentAchievementRepository
                            .findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(student.getId(), ownerId);
                    
                    int totalPoints = studentAchievements.stream()
                            .filter(sa -> sa.getIsEarned())
                            .mapToInt(sa -> sa.getAchievement().getPointsValue() != null ? sa.getAchievement().getPointsValue() : 0)
                            .sum();
                    
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("studentId", student.getId());
                    entry.put("studentName", student.getName());
                    entry.put("gradeLevel", gradeLevel);
                    entry.put("totalPoints", totalPoints);
                    entry.put("achievementsCount", studentAchievements.stream().filter(sa -> sa.getIsEarned()).count());
                    
                    return entry;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("totalPoints"), (Integer) a.get("totalPoints")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getSubjectLeaderboard(Long ownerId, String subject, int limit) {
        // This would require subject-specific achievement tracking
        // For now, returning general leaderboard
        return getLeaderboard(ownerId, subject, limit);
    }

    @Override
    @Transactional
    public List<StudentAchievementResponse> checkAndAwardAchievements(Long studentId, String activityType, Map<String, Object> activityData, Long ownerId) {
        List<StudentAchievementResponse> awardedAchievements = new ArrayList<>();
        
        // Get available achievements
        List<Achievement> availableAchievements = achievementRepository.findAvailableAchievements(ownerId, LocalDateTime.now());
        
        for (Achievement achievement : availableAchievements) {
            // Check if achievement criteria is met
            if (isAchievementCriteriaMet(achievement, activityType, activityData)) {
                // Check if already earned (for non-repeatable achievements)
                if (!achievement.getIsRepeatable()) {
                    boolean alreadyEarned = studentAchievementRepository.existsByStudentIdAndAchievementIdAndIsEarnedTrue(studentId, achievement.getId());
                    if (alreadyEarned) continue;
                }
                
                // Award achievement
                StudentAchievementResponse awarded = awardAchievement(achievement.getId(), studentId, ownerId);
                awardedAchievements.add(awarded);
            }
        }
        
        return awardedAchievements;
    }

    @Override
    public List<StudentAchievementResponse> getRecentAchievements(Long ownerId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<StudentAchievement> recentAchievements = studentAchievementRepository.findByOwnerIdAndEarnedDateAfterAndIsEarnedTrueOrderByEarnedDateDesc(ownerId, since);
        
        return recentAchievements.stream()
                .map(this::mapToStudentAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAchievementCategories(Long ownerId) {
        List<Object[]> results = achievementRepository.getCategoryWiseStatistics(ownerId);
        
        return results.stream()
                .map(row -> {
                    Map<String, Object> category = new HashMap<>();
                    category.put("category", row[0]);
                    category.put("achievementCount", row[1]);
                    category.put("avgPoints", row[2]);
                    return category;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Integer getStudentPoints(Long studentId, Long ownerId) {
        List<StudentAchievement> studentAchievements = studentAchievementRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(studentId, ownerId);
        
        return studentAchievements.stream()
                .filter(sa -> sa.getIsEarned())
                .mapToInt(sa -> sa.getAchievement().getPointsValue() != null ? sa.getAchievement().getPointsValue() : 0)
                .sum();
    }

    @Override
    public Integer getStudentXP(Long studentId, Long ownerId) {
        List<StudentAchievement> studentAchievements = studentAchievementRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(studentId, ownerId);
        
        return studentAchievements.stream()
                .filter(sa -> sa.getIsEarned())
                .mapToInt(sa -> sa.getAchievement().getXpValue() != null ? sa.getAchievement().getXpValue() : 0)
                .sum();
    }

    @Override
    public Integer getStudentLevel(Long studentId, Long ownerId) {
        int totalXp = getStudentXP(studentId, ownerId);
        return (totalXp / 1000) + 1;
    }

    @Override
    public List<AchievementResponse> getStudentBadges(Long studentId, Long ownerId) {
        List<StudentAchievement> studentAchievements = studentAchievementRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(studentId, ownerId);
        
        return studentAchievements.stream()
                .filter(sa -> sa.getIsEarned())
                .map(sa -> mapToAchievementResponse(sa.getAchievement()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStudentStreaks(Long studentId, Long ownerId) {
        // This would require streak tracking implementation
        Map<String, Object> streaks = new HashMap<>();
        streaks.put("dailyStreak", 0);
        streaks.put("weeklyStreak", 0);
        streaks.put("monthlyStreak", 0);
        streaks.put("longestStreak", 0);
        
        return streaks;
    }

    @Override
    @Transactional
    public void updateStudentActivity(Long studentId, String activityType, Map<String, Object> activityData, Long ownerId) {
        // Check and award achievements based on activity
        checkAndAwardAchievements(studentId, activityType, activityData, ownerId);
        
        // Update streaks, etc.
        log.info("Updated student activity: {} for student {}", activityType, studentId);
    }

    @Override
    public List<AchievementResponse> getDailyChallenges(Long ownerId) {
        return getAchievementsByType(ownerId, "CHALLENGE").stream()
                .filter(achievement -> achievement.getAchievementType().equals("CHALLENGE"))
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getWeeklyChallenges(Long ownerId) {
        return getAchievementsByType(ownerId, "CHALLENGE").stream()
                .filter(achievement -> achievement.getAchievementType().equals("CHALLENGE"))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getMonthlyChallenges(Long ownerId) {
        return getAchievementsByType(ownerId, "CHALLENGE").stream()
                .filter(achievement -> achievement.getAchievementType().equals("CHALLENGE"))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentAchievementResponse completeChallenge(Long challengeId, Long studentId, Long ownerId) {
        return awardAchievement(challengeId, studentId, ownerId);
    }

    @Override
    public Map<String, Object> getGamificationDashboard(Long ownerId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get achievement statistics
        dashboard.put("achievementStatistics", getAchievementStatistics(ownerId));
        
        // Get category statistics
        dashboard.put("categoryStatistics", getAchievementCategories(ownerId));
        
        // Get recent achievements
        dashboard.put("recentAchievements", getRecentAchievements(ownerId, 7));
        
        // Get top performers
        dashboard.put("topPerformers", getLeaderboard(ownerId, "ALL", 10));
        
        // Get challenges
        dashboard.put("dailyChallenges", getDailyChallenges(ownerId));
        dashboard.put("weeklyChallenges", getWeeklyChallenges(ownerId));
        dashboard.put("monthlyChallenges", getMonthlyChallenges(ownerId));
        
        return dashboard;
    }

    @Override
    public Map<String, Object> getStudentProgress(Long studentId, Long ownerId) {
        Map<String, Object> progress = new HashMap<>();
        
        progress.put("totalPoints", getStudentPoints(studentId, ownerId));
        progress.put("totalXp", getStudentXP(studentId, ownerId));
        progress.put("level", getStudentLevel(studentId, ownerId));
        progress.put("badges", getStudentBadges(studentId, ownerId));
        progress.put("streaks", getStudentStreaks(studentId, ownerId));
        progress.put("achievements", getStudentAchievements(studentId, ownerId));
        
        return progress;
    }

    @Override
    @Transactional
    public void resetStudentProgress(Long studentId, Long ownerId) {
        List<StudentAchievement> studentAchievements = studentAchievementRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByEarnedDateDesc(studentId, ownerId);
        studentAchievementRepository.deleteAll(studentAchievements);
        
        log.info("Reset progress for student: {}", studentId);
    }

    @Override
    @Transactional
    public void deleteAchievement(Long id, Long ownerId) {
        achievementRepository.softDeleteByIdAndOwnerId(id, ownerId);
    }

    @Override
    @Transactional
    public AchievementResponse updateAchievement(Long id, AchievementRequest request, Long ownerId) {
        Achievement achievement = achievementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));

        achievement.setAchievementName(request.getAchievementName());
        achievement.setDescription(request.getDescription());
        achievement.setBadgeIcon(request.getBadgeIcon());
        achievement.setBadgeColor(request.getBadgeColor());
        achievement.setAchievementType(parseAchievementType(request.getAchievementType()));
        achievement.setCategory(parseCategory(request.getCategory()));
        achievement.setDifficultyLevel(parseDifficultyLevel(request.getDifficultyLevel()));
        achievement.setPointsValue(request.getPointsValue());
        achievement.setXpValue(request.getXpValue());
        achievement.setCriteria(request.getCriteria());
        achievement.setRequirements(request.getRequirements());
        achievement.setIsRepeatable(request.getIsRepeatable());
        achievement.setIsSecret(request.getIsSecret());
        achievement.setIsActive(request.getIsActive());
        achievement.setUnlockDate(request.getUnlockDate());
        achievement.setExpiryDate(request.getExpiryDate());
        achievement.setRarity(parseRarity(request.getRarity()));

        Achievement updatedAchievement = achievementRepository.save(achievement);
        return mapToAchievementResponse(updatedAchievement);
    }

    @Override
    public Map<String, Object> getAchievementAnalytics(Long ownerId) {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("achievementStatistics", getAchievementStatistics(ownerId));
        analytics.put("categoryStatistics", getAchievementCategories(ownerId));
        analytics.put("rarityStatistics", getRarityStatistics(ownerId));
        analytics.put("recentActivity", getRecentAchievements(ownerId, 30));
        
        return analytics;
    }

    private List<Map<String, Object>> getRarityStatistics(Long ownerId) {
        List<Object[]> results = achievementRepository.getRarityWiseStatistics(ownerId);
        
        return results.stream()
                .map(row -> {
                    Map<String, Object> rarity = new HashMap<>();
                    rarity.put("rarity", row[0]);
                    rarity.put("achievementCount", row[1]);
                    rarity.put("avgPoints", row[2]);
                    return rarity;
                })
                .collect(Collectors.toList());
    }

    // Helper methods
    private boolean isAchievementCriteriaMet(Achievement achievement, String activityType, Map<String, Object> activityData) {
        // This is a simplified implementation
        // In a real system, you would parse the criteria JSON and check against activity data
        return achievement.getCriteria() != null && achievement.getCriteria().contains(activityType);
    }

    private Achievement.AchievementType parseAchievementType(String type) {
        if (type == null) return Achievement.AchievementType.MILESTONE;
        try {
            return Achievement.AchievementType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Achievement.AchievementType.MILESTONE;
        }
    }

    private Achievement.Category parseCategory(String category) {
        if (category == null) return Achievement.Category.ACADEMIC;
        try {
            return Achievement.Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Achievement.Category.ACADEMIC;
        }
    }

    private Achievement.DifficultyLevel parseDifficultyLevel(String difficultyLevel) {
        if (difficultyLevel == null) return Achievement.DifficultyLevel.BRONZE;
        try {
            return Achievement.DifficultyLevel.valueOf(difficultyLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Achievement.DifficultyLevel.BRONZE;
        }
    }

    private Achievement.Rarity parseRarity(String rarity) {
        if (rarity == null) return Achievement.Rarity.COMMON;
        try {
            return Achievement.Rarity.valueOf(rarity.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Achievement.Rarity.COMMON;
        }
    }

    private AchievementResponse mapToAchievementResponse(Achievement achievement) {
        return AchievementResponse.builder()
                .id(achievement.getId())
                .achievementName(achievement.getAchievementName())
                .description(achievement.getDescription())
                .badgeIcon(achievement.getBadgeIcon())
                .badgeColor(achievement.getBadgeColor())
                .achievementType(achievement.getAchievementType() != null ? achievement.getAchievementType().name() : null)
                .category(achievement.getCategory() != null ? achievement.getCategory().name() : null)
                .difficultyLevel(achievement.getDifficultyLevel() != null ? achievement.getDifficultyLevel().name() : null)
                .pointsValue(achievement.getPointsValue())
                .xpValue(achievement.getXpValue())
                .criteria(achievement.getCriteria())
                .requirements(achievement.getRequirements())
                .isRepeatable(achievement.getIsRepeatable())
                .isSecret(achievement.getIsSecret())
                .isActive(achievement.getIsActive())
                .isUnlocked(achievement.isUnlocked())
                .isExpired(achievement.isExpired())
                .isAvailable(achievement.isAvailable())
                .rarity(achievement.getRarity() != null ? achievement.getRarity().name() : null)
                .rarityColor(achievement.getRarityColor())
                .difficultyIcon(achievement.getDifficultyIcon())
                .categoryIcon(achievement.getCategoryIcon())
                .unlockDate(achievement.getUnlockDate() != null ? java.sql.Date.valueOf(achievement.getUnlockDate().toLocalDate()) : null)
                .expiryDate(achievement.getExpiryDate() != null ? java.sql.Date.valueOf(achievement.getExpiryDate().toLocalDate()) : null)
                .createdOn(achievement.getCreatedOn() != null ? new java.sql.Date(achievement.getCreatedOn().getTime()) : null)
                .updatedOn(achievement.getUpdatedOn() != null ? new java.sql.Date(achievement.getUpdatedOn().getTime()) : null)
                .build();
    }

    private StudentAchievementResponse mapToStudentAchievementResponse(StudentAchievement studentAchievement) {
        return StudentAchievementResponse.builder()
                .id(studentAchievement.getId())
                .studentId(studentAchievement.getStudent().getId())
                .studentName(studentAchievement.getStudent().getName())
                .achievementId(studentAchievement.getAchievement().getId())
                .achievementName(studentAchievement.getAchievement().getAchievementName())
                .achievementDescription(studentAchievement.getAchievement().getDescription())
                .badgeIcon(studentAchievement.getAchievement().getBadgeIcon())
                .badgeColor(studentAchievement.getAchievement().getBadgeColor())
                .achievementType(studentAchievement.getAchievement().getAchievementType() != null ? studentAchievement.getAchievement().getAchievementType().name() : null)
                .category(studentAchievement.getAchievement().getCategory() != null ? studentAchievement.getAchievement().getCategory().name() : null)
                .difficultyLevel(studentAchievement.getAchievement().getDifficultyLevel() != null ? studentAchievement.getAchievement().getDifficultyLevel().name() : null)
                .rarity(studentAchievement.getAchievement().getRarity() != null ? studentAchievement.getAchievement().getRarity().name() : null)
                .rarityColor(studentAchievement.getAchievement().getRarityColor())
                .difficultyIcon(studentAchievement.getAchievement().getDifficultyIcon())
                .categoryIcon(studentAchievement.getAchievement().getCategoryIcon())
                .pointsValue(studentAchievement.getAchievement().getPointsValue() != null ? studentAchievement.getAchievement().getPointsValue() : 0)
                .xpValue(studentAchievement.getAchievement().getXpValue() != null ? studentAchievement.getAchievement().getXpValue() : 0)
                .earnedDate(studentAchievement.getEarnedDate() != null ? java.sql.Date.valueOf(studentAchievement.getEarnedDate().toLocalDate()) : null)
                .formattedEarnedDate(studentAchievement.getFormattedEarnedDate())
                .progressPercentage(studentAchievement.getProgressPercentage())
                .currentCount(studentAchievement.getCurrentCount())
                .targetCount(studentAchievement.getTargetCount())
                .progressText(studentAchievement.getProgressText())
                .isEarned(studentAchievement.getIsEarned())
                .isDisplayed(studentAchievement.getIsDisplayed())
                .displayDate(studentAchievement.getDisplayDate() != null ? java.sql.Date.valueOf(studentAchievement.getDisplayDate().toLocalDate()) : null)
                .contextData(studentAchievement.getContextData())
                .notes(studentAchievement.getNotes())
                .isCompleted(studentAchievement.isCompleted())
                .daysSinceEarned(studentAchievement.getDaysSinceEarned())
                .createdOn(studentAchievement.getCreatedOn() != null ? new java.sql.Date(studentAchievement.getCreatedOn().getTime()) : null)
                .updatedOn(studentAchievement.getUpdatedOn() != null ? new java.sql.Date(studentAchievement.getUpdatedOn().getTime()) : null)
                .build();
    }
}
