package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * DTO for Achievement responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponse {

    private Long id;
    private String achievementName;
    private String description;
    private String badgeIcon;
    private String badgeColor;
    private String achievementType;
    private String category;
    private String difficultyLevel;
    private Integer pointsValue;
    private Integer xpValue;
    private String criteria;
    private String requirements;
    private Boolean isRepeatable;
    private Boolean isSecret;
    private Boolean isActive;
    private Boolean isUnlocked;
    private Boolean isExpired;
    private Boolean isAvailable;
    private String rarity;
    private String rarityColor;
    private String difficultyIcon;
    private String categoryIcon;
    private Date unlockDate;
    private Date expiryDate;
    private Date createdOn;
    private Date updatedOn;
}
