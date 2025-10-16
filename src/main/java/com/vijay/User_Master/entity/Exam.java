package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "exams")
@EntityListeners(AuditingEntityListener.class)
public class Exam extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Optimistic Locking - prevents concurrent update conflicts (e.g., grade updates)
    @Version
    private Long version;

    @Column(nullable = false)
    private String examName; // e.g., "Midterm Exam", "Final Exam", "Unit Test 1"
    
    @Column(nullable = false)
    private String examCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType; // MIDTERM, FINAL, UNIT_TEST, QUARTERLY, HALF_YEARLY, ANNUAL
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
    
    @Column(nullable = false)
    private LocalDate examDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private Integer durationMinutes;
    
    private Double totalMarks;
    
    private Double passingMarks;
    
    private String roomNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Worker supervisor; // Worker with ROLE_TEACHER
    
    @Enumerated(EnumType.STRING)
    private ExamStatus status; // SCHEDULED, ONGOING, COMPLETED, CANCELLED, POSTPONED
    
    private String semester; // e.g., "Fall 2024"
    
    private String academicYear; // e.g., "2024-2025"
    
    @Column(length = 1000)
    private String instructions;
    
    @Column(length = 1000)
    private String syllabus;
    
    private boolean resultsPublished;
    
    private LocalDate resultPublishDate;
    
    @Column(length = 500)
    private String notes;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum ExamType {
        MIDTERM, FINAL, UNIT_TEST, QUARTERLY, HALF_YEARLY, ANNUAL, SURPRISE_TEST, PRACTICAL
    }

    public enum ExamStatus {
        SCHEDULED, ONGOING, COMPLETED, CANCELLED, POSTPONED, RESCHEDULED
    }
}

