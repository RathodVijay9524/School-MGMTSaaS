package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for SkillMastery responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillMasteryResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private String skillKey;
    private String skillName;
    private Double masteryLevel;
    private Double avgAccuracy;
    private Integer totalAttempts;
    private Integer correctAttempts;
    private LocalDateTime lastPracticedAt;
    private LocalDateTime nextReviewAt;
    private String lastDifficulty;
    private Double velocityScore;
    private Integer consecutiveCorrect;
    private Integer consecutiveIncorrect;
    private Integer timeSpentMinutes;
    private Integer hintsUsedCount;
    
    // Computed fields
    private String masteryCategory; // Expert, Proficient, Competent, Developing, Beginner
    private String recommendedDifficulty; // EASY, MEDIUM, HARD
    private Boolean needsReview;
    private Boolean isHighMastery;
    private Boolean isMediumMastery;
    private Boolean isLowMastery;
    private String formattedTimeSpent;
    private Integer daysUntilReview;
    private Integer daysSinceLastPractice;
}
