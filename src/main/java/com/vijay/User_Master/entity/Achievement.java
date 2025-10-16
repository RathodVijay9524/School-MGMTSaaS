package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Achievement entity for Gamification System
 * Represents achievements/badges that students can earn
 */
@Entity
@Table(name = "achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Achievement extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "achievement_name", nullable = false, unique = true)
    private String achievementName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "badge_icon")
    private String badgeIcon; // URL or icon class

    @Column(name = "badge_color")
    private String badgeColor;

    @Column(name = "achievement_type")
    @Enumerated(EnumType.STRING)
    private AchievementType achievementType;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @Column(name = "points_value")
    private Integer pointsValue;

    @Column(name = "xp_value")
    private Integer xpValue;

    @Column(name = "criteria", columnDefinition = "TEXT")
    private String criteria; // JSON string of criteria

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements; // Human-readable requirements

    @Column(name = "is_repeatable")
    @Builder.Default
    private Boolean isRepeatable = false;

    @Column(name = "is_secret")
    @Builder.Default
    private Boolean isSecret = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "unlock_date")
    private LocalDateTime unlockDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "rarity")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Rarity rarity = Rarity.COMMON;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum AchievementType {
        MILESTONE, // Complete X number of tasks
        STREAK,    // Do something X days in a row
        MASTERY,   // Achieve X% in a subject
        SOCIAL,    // Help peers, participate in discussions
        SPECIAL,   // Event-based or special achievements
        CHALLENGE  // Complete specific challenges
    }

    public enum Category {
        ACADEMIC,      // Study-related achievements
        ATTENDANCE,    // Attendance achievements
        PARTICIPATION, // Class participation
        HELPING,       // Helping peers
        CONSISTENCY,   // Regular usage
        MASTERY,       // Subject mastery
        SOCIAL,        // Social interactions
        SPECIAL        // Special events
    }

    public enum DifficultyLevel {
        BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }

    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    // Helper methods
    public boolean isUnlocked() {
        return unlockDate != null;
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isAvailable() {
        return isActive && !isDeleted && !isExpired();
    }

    public String getRarityColor() {
        return switch (rarity) {
            case COMMON -> "#808080";     // Gray
            case UNCOMMON -> "#00FF00";   // Green
            case RARE -> "#0080FF";       // Blue
            case EPIC -> "#8000FF";       // Purple
            case LEGENDARY -> "#FF8000";  // Orange
        };
    }

    public String getDifficultyIcon() {
        return switch (difficultyLevel) {
            case BRONZE -> "🥉";
            case SILVER -> "🥈";
            case GOLD -> "🥇";
            case PLATINUM -> "💎";
            case DIAMOND -> "💠";
        };
    }

    public String getCategoryIcon() {
        return switch (category) {
            case ACADEMIC -> "📚";
            case ATTENDANCE -> "📅";
            case PARTICIPATION -> "🙋";
            case HELPING -> "🤝";
            case CONSISTENCY -> "🔥";
            case MASTERY -> "🎯";
            case SOCIAL -> "👥";
            case SPECIAL -> "⭐";
        };
    }
}
