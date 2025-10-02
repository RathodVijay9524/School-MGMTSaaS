package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "announcements")
@EntityListeners(AuditingEntityListener.class)
public class Announcement extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    
    @Column(length = 3000, nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementType announcementType; // GENERAL, URGENT, ACADEMIC, ADMINISTRATIVE, EVENT
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority; // HIGH, MEDIUM, LOW
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementAudience targetAudience; // ALL, STUDENTS, TEACHERS, PARENTS, SPECIFIC_CLASS
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_class_id")
    private SchoolClass targetClass;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User announcementCreatedBy;
    
    @Column(nullable = false)
    private LocalDateTime publishDate;
    
    private LocalDateTime expiryDate;
    
    @Enumerated(EnumType.STRING)
    private AnnouncementStatus status; // DRAFT, PUBLISHED, ARCHIVED, EXPIRED
    
    private boolean isPinned; // Pin to top
    
    private boolean sendEmail; // Send email notification
    
    private boolean sendSMS; // Send SMS notification
    
    private String attachmentUrl;
    
    private Integer viewCount;
    
    @Column(length = 500)
    private String notes;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum AnnouncementType {
        GENERAL, URGENT, ACADEMIC, ADMINISTRATIVE, EVENT, HOLIDAY, EXAM, FEE, ADMISSION
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public enum AnnouncementAudience {
        ALL, STUDENTS, TEACHERS, PARENTS, STAFF, SPECIFIC_CLASS, SPECIFIC_GRADE
    }

    public enum AnnouncementStatus {
        DRAFT, PUBLISHED, ARCHIVED, EXPIRED, CANCELLED
    }
}

