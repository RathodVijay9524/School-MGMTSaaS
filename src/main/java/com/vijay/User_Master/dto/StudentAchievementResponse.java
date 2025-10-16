package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * DTO for StudentAchievement responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAchievementResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long achievementId;
    private String achievementName;
    private String achievementDescription;
    private String badgeIcon;
    private String badgeColor;
    private String achievementType;
    private String category;
    private String difficultyLevel;
    private String rarity;
    private String rarityColor;
    private String difficultyIcon;
    private String categoryIcon;
    private Integer pointsValue;
    private Integer xpValue;
    private Date earnedDate;
    private String formattedEarnedDate;
    private Double progressPercentage;
    private Integer currentCount;
    private Integer targetCount;
    private String progressText;
    private Boolean isEarned;
    private Boolean isDisplayed;
    private Date displayDate;
    private String contextData;
    private String notes;
    private Boolean isCompleted;
    private Long daysSinceEarned;
    private Date createdOn;
    private Date updatedOn;
}
