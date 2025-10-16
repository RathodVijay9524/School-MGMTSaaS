package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * StudyGroupMember entity for Peer Learning Platform
 * Represents membership of students in study groups
 */
@Entity
@Table(name = "study_group_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StudyGroupMember extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MembershipStatus status = MembershipStatus.ACTIVE;

    @Column(name = "contribution_score")
    @Builder.Default
    private Double contributionScore = 0.0;

    @Column(name = "participation_count")
    @Builder.Default
    private Integer participationCount = 0;

    @Column(name = "help_provided_count")
    @Builder.Default
    private Integer helpProvidedCount = 0;

    @Column(name = "help_received_count")
    @Builder.Default
    private Integer helpReceivedCount = 0;

    @Column(name = "last_active_date")
    private LocalDateTime lastActiveDate;

    @Column(name = "is_notification_enabled")
    @Builder.Default
    private Boolean isNotificationEnabled = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum MemberRole {
        CREATOR,    // Group creator
        MODERATOR,  // Can manage group
        TUTOR,      // Provides tutoring
        MEMBER      // Regular member
    }

    public enum MembershipStatus {
        ACTIVE, PENDING, SUSPENDED, LEFT, REMOVED
    }

    // Helper methods
    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }

    public boolean isPending() {
        return status == MembershipStatus.PENDING;
    }

    public boolean isModerator() {
        return role == MemberRole.MODERATOR || role == MemberRole.CREATOR;
    }

    public boolean canManageGroup() {
        return isModerator() && isActive();
    }

    public void updateLastActivity() {
        this.lastActiveDate = LocalDateTime.now();
    }

    public void incrementParticipation() {
        this.participationCount++;
        updateLastActivity();
    }

    public void provideHelp() {
        this.helpProvidedCount++;
        updateLastActivity();
    }

    public void receiveHelp() {
        this.helpReceivedCount++;
        updateLastActivity();
    }

    public double getHelpRatio() {
        if (helpProvidedCount == 0) return 0.0;
        return (double) helpProvidedCount / (helpProvidedCount + helpReceivedCount);
    }

    public String getContributionLevel() {
        if (contributionScore >= 90) return "Excellent";
        if (contributionScore >= 70) return "Good";
        if (contributionScore >= 50) return "Average";
        if (contributionScore >= 30) return "Below Average";
        return "Poor";
    }

    public long getDaysSinceJoined() {
        return java.time.Duration.between(joinDate, LocalDateTime.now()).toDays();
    }

    public long getDaysSinceLastActive() {
        if (lastActiveDate == null) return getDaysSinceJoined();
        return java.time.Duration.between(lastActiveDate, LocalDateTime.now()).toDays();
    }

    public String getFormattedJoinDate() {
        return joinDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public String getRoleIcon() {
        return switch (role) {
            case CREATOR -> "👑";
            case MODERATOR -> "🛡️";
            case TUTOR -> "🎓";
            case MEMBER -> "👤";
        };
    }
}
