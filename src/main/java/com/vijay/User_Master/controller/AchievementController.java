package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.service.GamificationService;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Gamification System
 */
@RestController
@RequestMapping("/api/v1/gamification")
@CrossOrigin(origins = "*")
@Slf4j
public class AchievementController {

    @Autowired
    private GamificationService gamificationService;

    // Achievement Management endpoints
    @PostMapping("/achievements")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> createAchievement(@RequestBody AchievementRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            AchievementResponse response = gamificationService.createAchievement(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating achievement: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAchievementById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            AchievementResponse response = gamificationService.getAchievementById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievement by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAllAchievements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            var response = gamificationService.getAllAchievements(ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting all achievements: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAchievementsByType(@PathVariable String type) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getAchievementsByType(ownerId, type);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievements by type {}: {}", type, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAchievementsByCategory(@PathVariable String category) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getAchievementsByCategory(ownerId, category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievements by category {}: {}", category, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements/difficulty/{difficulty}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAchievementsByDifficulty(@PathVariable String difficulty) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getAchievementsByDifficulty(ownerId, difficulty);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievements by difficulty {}: {}", difficulty, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements/rarity/{rarity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAchievementsByRarity(@PathVariable String rarity) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getAchievementsByRarity(ownerId, rarity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievements by rarity {}: {}", rarity, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> searchAchievements(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
            
            var response = gamificationService.searchAchievements(ownerId, keyword, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching achievements with keyword {}: {}", keyword, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/achievements/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAvailableAchievements() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getAvailableAchievements(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting available achievements: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/achievements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> updateAchievement(@PathVariable Long id, @RequestBody AchievementRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            AchievementResponse response = gamificationService.updateAchievement(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating achievement {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/achievements/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAchievement(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            gamificationService.deleteAchievement(id, ownerId);
            return ResponseEntity.ok("Achievement deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting achievement {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Student Achievement endpoints
    @PostMapping("/achievements/{achievementId}/award/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> awardAchievement(@PathVariable Long achievementId, @PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudentAchievementResponse response = gamificationService.awardAchievement(achievementId, studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error awarding achievement {} to student {}: {}", achievementId, studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/achievements")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentAchievements(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudentAchievementResponse> response = gamificationService.getStudentAchievements(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievements for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/student-achievements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentAchievementById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudentAchievementResponse response = gamificationService.getStudentAchievementById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting student achievement by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/achievements/{achievementId}/students/{studentId}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> updateAchievementProgress(
            @PathVariable Long achievementId,
            @PathVariable Long studentId,
            @RequestParam int progress) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudentAchievementResponse response = gamificationService.updateAchievementProgress(achievementId, studentId, progress, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating achievement progress for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Gamification Analytics endpoints
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getAchievementStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = gamificationService.getAchievementStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievement statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentGamificationProfile(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = gamificationService.getStudentGamificationProfile(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting gamification profile for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/leaderboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getLeaderboard(
            @RequestParam(defaultValue = "ALL") String category,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = gamificationService.getLeaderboard(ownerId, category, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting leaderboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/leaderboard/class/{gradeLevel}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getClassLeaderboard(
            @PathVariable String gradeLevel,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = gamificationService.getClassLeaderboard(ownerId, gradeLevel, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting class leaderboard for grade {}: {}", gradeLevel, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/leaderboard/subject/{subject}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getSubjectLeaderboard(
            @PathVariable String subject,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = gamificationService.getSubjectLeaderboard(ownerId, subject, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting subject leaderboard for {}: {}", subject, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Student Progress endpoints
    @GetMapping("/students/{studentId}/points")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentPoints(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Integer response = gamificationService.getStudentPoints(studentId, ownerId);
            return ResponseEntity.ok(Map.of("points", response));
        } catch (Exception e) {
            log.error("Error getting points for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/xp")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentXP(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Integer response = gamificationService.getStudentXP(studentId, ownerId);
            return ResponseEntity.ok(Map.of("xp", response));
        } catch (Exception e) {
            log.error("Error getting XP for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/level")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentLevel(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Integer response = gamificationService.getStudentLevel(studentId, ownerId);
            return ResponseEntity.ok(Map.of("level", response));
        } catch (Exception e) {
            log.error("Error getting level for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/badges")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentBadges(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getStudentBadges(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting badges for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/streaks")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentStreaks(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = gamificationService.getStudentStreaks(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting streaks for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentProgress(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = gamificationService.getStudentProgress(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting progress for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Challenge endpoints
    @GetMapping("/challenges/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getDailyChallenges() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getDailyChallenges(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting daily challenges: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/challenges/weekly")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getWeeklyChallenges() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getWeeklyChallenges(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting weekly challenges: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/challenges/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getMonthlyChallenges() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<AchievementResponse> response = gamificationService.getMonthlyChallenges(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting monthly challenges: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/challenges/{challengeId}/complete/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> completeChallenge(@PathVariable Long challengeId, @PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudentAchievementResponse response = gamificationService.completeChallenge(challengeId, studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error completing challenge {} for student {}: {}", challengeId, studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Activity tracking
    @PostMapping("/students/{studentId}/activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> updateStudentActivity(
            @PathVariable Long studentId,
            @RequestParam String activityType,
            @RequestBody Map<String, Object> activityData) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            gamificationService.updateStudentActivity(studentId, activityType, activityData, ownerId);
            return ResponseEntity.ok("Activity updated successfully");
        } catch (Exception e) {
            log.error("Error updating activity for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Dashboard endpoints
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getGamificationDashboard() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = gamificationService.getGamificationDashboard(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting gamification dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getAchievementAnalytics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = gamificationService.getAchievementAnalytics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting achievement analytics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Reset progress (Admin only)
    @PostMapping("/students/{studentId}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetStudentProgress(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            gamificationService.resetStudentProgress(studentId, ownerId);
            return ResponseEntity.ok("Student progress reset successfully");
        } catch (Exception e) {
            log.error("Error resetting progress for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
