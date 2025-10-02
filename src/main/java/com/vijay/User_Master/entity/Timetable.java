package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "timetables")
@EntityListeners(AuditingEntityListener.class)
public class Timetable extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Worker teacher; // Worker with ROLE_TEACHER
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek; // MONDAY, TUESDAY, etc.
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    private Integer periodNumber; // 1, 2, 3, etc.
    
    private String roomNumber;
    
    @Enumerated(EnumType.STRING)
    private PeriodType periodType; // LECTURE, PRACTICAL, BREAK, LUNCH, SPORTS, ASSEMBLY
    
    private String academicYear; // e.g., "2024-2025"
    
    private String semester; // e.g., "Fall 2024"
    
    @Enumerated(EnumType.STRING)
    private TimetableStatus status; // ACTIVE, INACTIVE, TEMPORARY
    
    @Column(length = 500)
    private String notes;
    
    private boolean isRecurring; // Weekly recurring schedule
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    public enum PeriodType {
        LECTURE, PRACTICAL, LAB, BREAK, LUNCH, SPORTS, ASSEMBLY, LIBRARY, STUDY_HALL
    }

    public enum TimetableStatus {
        ACTIVE, INACTIVE, TEMPORARY, CANCELLED
    }
}

