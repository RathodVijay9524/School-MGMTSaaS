package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * StudyGroup entity for Peer Learning Platform
 * Represents study groups where students can collaborate
 */
@Entity
@Table(name = "study_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StudyGroup extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "grade_level", nullable = false)
    private String gradeLevel;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;

    @Column(name = "group_type")
    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Column(name = "current_members")
    @Builder.Default
    private Integer currentMembers = 0;

    @Column(name = "group_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GroupStatus groupStatus = GroupStatus.ACTIVE;

    @Column(name = "study_schedule", columnDefinition = "TEXT")
    private String studySchedule; // JSON string

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "chat_room_id")
    private String chatRoomId;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Worker creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StudyGroupMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StudySession> studySessions = new ArrayList<>();

    // Enums
    public enum GroupType {
        STUDY_GROUP,      // General study group
        PROJECT_TEAM,     // Project collaboration
        PEER_TUTORING,    // Peer tutoring sessions
        DISCUSSION_GROUP, // Discussion and Q&A
        EXAM_PREP,        // Exam preparation
        HOMEWORK_HELP     // Homework assistance
    }

    public enum GroupStatus {
        ACTIVE, INACTIVE, COMPLETED, SUSPENDED
    }

    // Helper methods
    public boolean isFull() {
        return maxMembers != null && currentMembers >= maxMembers;
    }

    public boolean hasSpace() {
        return maxMembers == null || currentMembers < maxMembers;
    }

    public boolean isExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    public boolean canJoin() {
        return isActive && !isDeleted && !isFull() && !isExpired() && 
               groupStatus == GroupStatus.ACTIVE;
    }

    public int getAvailableSpots() {
        if (maxMembers == null) return Integer.MAX_VALUE;
        return Math.max(0, maxMembers - currentMembers);
    }

    public String getStatusText() {
        if (isExpired()) return "Expired";
        if (!isActive) return "Inactive";
        return switch (groupStatus) {
            case ACTIVE -> "Active";
            case INACTIVE -> "Inactive";
            case COMPLETED -> "Completed";
            case SUSPENDED -> "Suspended";
        };
    }

    public String getFormattedDuration() {
        if (startDate == null || endDate == null) return "Ongoing";
        long days = java.time.Duration.between(startDate, endDate).toDays();
        if (days > 30) {
            long months = days / 30;
            long remainingDays = days % 30;
            return months + "m " + remainingDays + "d";
        }
        return days + " days";
    }

    public void updateLastActivity() {
        this.lastActivityDate = LocalDateTime.now();
    }

    public void addMember() {
        this.currentMembers++;
        updateLastActivity();
    }

    public void removeMember() {
        this.currentMembers = Math.max(0, this.currentMembers - 1);
        updateLastActivity();
    }
}
