package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Achievement creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRequest {

    @NotBlank(message = "Achievement name cannot be empty")
    @Size(max = 200, message = "Achievement name cannot exceed 200 characters")
    private String achievementName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String badgeIcon;

    private String badgeColor;

    @NotBlank(message = "Achievement type is required")
    private String achievementType;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Difficulty level is required")
    private String difficultyLevel;

    @Min(value = 0, message = "Points value cannot be negative")
    private Integer pointsValue;

    @Min(value = 0, message = "XP value cannot be negative")
    private Integer xpValue;

    private String criteria;

    @Size(max = 500, message = "Requirements cannot exceed 500 characters")
    private String requirements;

    @Builder.Default
    private Boolean isRepeatable = false;

    @Builder.Default
    private Boolean isSecret = false;

    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime unlockDate;

    private LocalDateTime expiryDate;

    @NotBlank(message = "Rarity is required")
    private String rarity;
}
