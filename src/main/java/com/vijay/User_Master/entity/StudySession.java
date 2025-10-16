package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * StudySession entity for Peer Learning Platform
 * Represents individual study sessions within study groups
 */
@Entity
@Table(name = "study_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StudySession extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_title", nullable = false)
    private String sessionTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "session_type")
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "actual_start_date")
    private LocalDateTime actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "location", columnDefinition = "TEXT")
    private String location; // Physical location or online link

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "agenda", columnDefinition = "TEXT")
    private String agenda;

    @Column(name = "materials", columnDefinition = "TEXT")
    private String materials; // JSON string of materials

    @Column(name = "participants_count")
    @Builder.Default
    private Integer participantsCount = 0;

    @Column(name = "session_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionStatus sessionStatus = SessionStatus.SCHEDULED;

    @Column(name = "session_notes", columnDefinition = "TEXT")
    private String sessionNotes;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback; // JSON string of feedback

    @Column(name = "is_recorded")
    @Builder.Default
    private Boolean isRecorded = false;

    @Column(name = "recording_link")
    private String recordingLink;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facilitator_id", nullable = false)
    private Worker facilitator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum SessionType {
        STUDY_GROUP,      // Group study session
        PEER_TUTORING,    // Peer tutoring session
        DISCUSSION,       // Discussion/Q&A session
        PROJECT_WORK,     // Collaborative project work
        EXAM_PREP,        // Exam preparation
        HOMEWORK_HELP,    // Homework assistance
        REVIEW_SESSION,   // Review session
        BRAINSTORMING     // Brainstorming session
    }

    public enum SessionStatus {
        SCHEDULED, ONGOING, COMPLETED, CANCELLED, POSTPONED
    }

    // Helper methods
    public void start() {
        this.sessionStatus = SessionStatus.ONGOING;
        this.actualStartDate = LocalDateTime.now();
    }

    public void complete() {
        this.sessionStatus = SessionStatus.COMPLETED;
        this.actualEndDate = LocalDateTime.now();
        if (actualStartDate != null) {
            this.durationMinutes = (int) java.time.Duration.between(actualStartDate, actualEndDate).toMinutes();
        }
    }

    public void cancel() {
        this.sessionStatus = SessionStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return sessionStatus == SessionStatus.COMPLETED;
    }

    public boolean isOngoing() {
        return sessionStatus == SessionStatus.ONGOING;
    }

    public boolean isUpcoming() {
        return sessionStatus == SessionStatus.SCHEDULED && 
               scheduledDate != null && 
               scheduledDate.isAfter(LocalDateTime.now());
    }

    public boolean isPast() {
        return (scheduledDate != null && scheduledDate.isBefore(LocalDateTime.now())) ||
               isCompleted();
    }

    public String getFormattedDuration() {
        if (durationMinutes == null) return "Unknown";
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    public String getFormattedScheduledDate() {
        if (scheduledDate == null) return "Not scheduled";
        return scheduledDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
    }

    public String getStatusColor() {
        return switch (sessionStatus) {
            case SCHEDULED -> "#3498db";    // Blue
            case ONGOING -> "#f39c12";      // Orange
            case COMPLETED -> "#27ae60";    // Green
            case CANCELLED -> "#e74c3c";    // Red
            case POSTPONED -> "#9b59b6";    // Purple
        };
    }

    public String getTypeIcon() {
        return switch (sessionType) {
            case STUDY_GROUP -> "📚";
            case PEER_TUTORING -> "🎓";
            case DISCUSSION -> "💬";
            case PROJECT_WORK -> "🔨";
            case EXAM_PREP -> "📝";
            case HOMEWORK_HELP -> "✏️";
            case REVIEW_SESSION -> "🔄";
            case BRAINSTORMING -> "💡";
        };
    }
}
