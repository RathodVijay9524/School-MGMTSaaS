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
@Table(name = "assignments")
@EntityListeners(AuditingEntityListener.class)
public class Assignment extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Worker assignedBy; // Worker with ROLE_TEACHER
    
    @Enumerated(EnumType.STRING)
    private AssignmentType assignmentType; // HOMEWORK, PROJECT, ESSAY, PRACTICAL, PRESENTATION
    
    @Column(nullable = false)
    private LocalDateTime assignedDate;
    
    @Column(nullable = false)
    private LocalDateTime dueDate;
    
    private Double totalMarks;
    
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status; // ASSIGNED, IN_PROGRESS, SUBMITTED, GRADED, OVERDUE
    
    private String attachmentUrl; // File path or URL to assignment document
    
    @Column(length = 1000)
    private String instructions;
    
    private Integer submissionsCount;
    
    private Integer totalStudents;
    
    private boolean allowLateSubmission;
    
    private Integer latePenaltyPercentage;
    
    @Column(length = 500)
    private String notes;
    
    // ========== NEW FIELDS - RUBRIC SUPPORT ==========
    @Column(length = 5000)
    private String rubricJsonData; // JSON string with grading rubric criteria
    
    @lombok.Builder.Default
    private boolean hasRubric = false;
    
    // ========== NEW FIELDS - SUBMISSION FORMAT ==========
    @Column(length = 200)
    private String allowedFileTypes; // e.g., "pdf,docx,jpg,png" - comma separated
    
    private Integer maxFileSize; // Maximum file size in MB
    
    @lombok.Builder.Default
    private Integer maxFilesAllowed = 1; // Number of files student can submit
    
    // ========== NEW FIELDS - GROUP ASSIGNMENT SUPPORT ==========
    @lombok.Builder.Default
    private boolean isGroupAssignment = false;
    
    private Integer minGroupSize; // Minimum students per group
    
    private Integer maxGroupSize; // Maximum students per group
    
    // ========== NEW FIELDS - MULTIPLE ATTEMPTS ==========
    @lombok.Builder.Default
    private boolean allowMultipleAttempts = false;
    
    private Integer maxAttempts; // Maximum submission attempts allowed
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum AssignmentType {
        HOMEWORK, PROJECT, ESSAY, PRACTICAL, PRESENTATION, GROUP_PROJECT, QUIZ
    }

    public enum AssignmentStatus {
        ASSIGNED, IN_PROGRESS, SUBMITTED, GRADED, OVERDUE, CANCELLED
    }
}

