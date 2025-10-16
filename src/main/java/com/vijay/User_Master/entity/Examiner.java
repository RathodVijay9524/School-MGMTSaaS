package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * Examiner entity to support multiple examiners per exam
 * Enables blind grading, multi-examiner marking, and external examiner tracking
 */
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "examiners")
@EntityListeners(AuditingEntityListener.class)
public class Examiner extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Worker teacher; // Internal teacher (Worker with ROLE_TEACHER)

    // External Examiner Support
    @Column(length = 200)
    private String externalExaminerName; // For external examiners who aren't in Worker table

    @Column(length = 200)
    private String externalExaminerEmail;

    @Column(length = 20)
    private String externalExaminerPhone;

    @Column(length = 200)
    private String institution; // Institution/organization of external examiner

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExaminerRole role; // PRIMARY, SECONDARY, EXTERNAL, MODERATOR

    @Enumerated(EnumType.STRING)
    @lombok.Builder.Default
    private ExaminerStatus status = ExaminerStatus.ASSIGNED; // ASSIGNED, COMPLETED, PENDING

    private LocalDate assignedDate;

    private LocalDate completionDate; // When examiner completed grading

    @Column(length = 1000)
    private String specialization; // Subject specialization/expertise

    @lombok.Builder.Default
    private boolean isBlindGrading = false; // Whether this examiner grades anonymously

    @Column(length = 500)
    private String remarks;

    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum ExaminerRole {
        PRIMARY,      // Main examiner responsible for grading
        SECONDARY,    // Secondary examiner for cross-checking
        EXTERNAL,     // External examiner from another institution
        MODERATOR,    // Moderator who reviews grades
        CO_EXAMINER   // Co-examiner working with primary
    }

    public enum ExaminerStatus {
        ASSIGNED,     // Assigned but not started
        IN_PROGRESS,  // Currently grading
        COMPLETED,    // Grading completed
        PENDING       // Awaiting assignment acceptance
    }

    /**
     * Gets the display name of the examiner (internal teacher or external examiner)
     */
    public String getDisplayName() {
        if (teacher != null) {
            return teacher.getFirstName() + " " + teacher.getLastName();
        }
        return externalExaminerName;
    }

    /**
     * Checks if this is an external examiner
     */
    public boolean isExternal() {
        return teacher == null && externalExaminerName != null;
    }
}

