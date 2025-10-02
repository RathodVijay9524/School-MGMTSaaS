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
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
public class Event extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventName;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType; // ACADEMIC, SPORTS, CULTURAL, HOLIDAY, MEETING, EXAM, WORKSHOP
    
    @Column(nullable = false)
    private LocalDateTime startDateTime;
    
    @Column(nullable = false)
    private LocalDateTime endDateTime;
    
    private String venue;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organized_by_teacher_id")
    private Worker organizer; // Worker with ROLE_TEACHER
    
    @Enumerated(EnumType.STRING)
    private EventStatus status; // SCHEDULED, ONGOING, COMPLETED, CANCELLED, POSTPONED
    
    @Enumerated(EnumType.STRING)
    private EventAudience audience; // ALL, STUDENTS, TEACHERS, PARENTS, SPECIFIC_CLASS
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_class_id")
    private SchoolClass targetClass; // If specific to a class
    
    private Integer expectedParticipants;
    
    private Integer registeredParticipants;
    
    private boolean isPublic; // Visible to parents
    
    private boolean requiresRegistration;
    
    private String bannerImageUrl;
    
    @Column(length = 1000)
    private String notes;
    
    private String contactPerson;
    
    private String contactPhone;
    
    private String contactEmail;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum EventType {
        ACADEMIC, SPORTS, CULTURAL, HOLIDAY, MEETING, EXAM, WORKSHOP, SEMINAR, 
        COMPETITION, CELEBRATION, PARENT_TEACHER_MEETING, FIELD_TRIP
    }

    public enum EventStatus {
        SCHEDULED, ONGOING, COMPLETED, CANCELLED, POSTPONED, RESCHEDULED
    }

    public enum EventAudience {
        ALL, STUDENTS, TEACHERS, PARENTS, STAFF, SPECIFIC_CLASS, SPECIFIC_GRADE
    }
}

