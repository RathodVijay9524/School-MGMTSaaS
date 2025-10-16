package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * StudentAchievement entity for Gamification System
 * Represents achievements earned by students
 */
@Entity
@Table(name = "student_achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StudentAchievement extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "earned_date", nullable = false)
    private LocalDateTime earnedDate;

    @Column(name = "progress_percentage")
    @Builder.Default
    private Double progressPercentage = 0.0;

    @Column(name = "current_count")
    @Builder.Default
    private Integer currentCount = 0;

    @Column(name = "target_count")
    private Integer targetCount;

    @Column(name = "is_earned")
    @Builder.Default
    private Boolean isEarned = false;

    @Column(name = "is_displayed")
    @Builder.Default
    private Boolean isDisplayed = false;

    @Column(name = "display_date")
    private LocalDateTime displayDate;

    @Column(name = "context_data", columnDefinition = "TEXT")
    private String contextData; // JSON string with additional context

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public void earn() {
        this.isEarned = true;
        this.earnedDate = LocalDateTime.now();
        this.progressPercentage = 100.0;
    }

    public void display() {
        this.isDisplayed = true;
        this.displayDate = LocalDateTime.now();
    }

    public void updateProgress(int currentCount) {
        this.currentCount = currentCount;
        if (targetCount != null && targetCount > 0) {
            this.progressPercentage = Math.min(100.0, (double) currentCount / targetCount * 100);
        }
    }

    public boolean isCompleted() {
        return isEarned && progressPercentage >= 100.0;
    }

    public String getProgressText() {
        if (targetCount == null) {
            return currentCount + " completed";
        }
        return currentCount + "/" + targetCount + " (" + String.format("%.1f", progressPercentage) + "%)";
    }

    public long getDaysSinceEarned() {
        if (earnedDate == null) return 0;
        return java.time.Duration.between(earnedDate, LocalDateTime.now()).toDays();
    }

    public String getFormattedEarnedDate() {
        if (earnedDate == null) return "Not earned";
        return earnedDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
