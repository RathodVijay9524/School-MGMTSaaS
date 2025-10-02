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
@Table(name = "homework_submissions")
@EntityListeners(AuditingEntityListener.class)
public class HomeworkSubmission extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student; // Worker with ROLE_STUDENT
    
    @Column(nullable = false)
    private LocalDateTime submittedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status; // SUBMITTED, LATE, GRADED, RESUBMIT_REQUIRED, NOT_SUBMITTED
    
    private String submissionFileUrl;
    
    @Column(length = 2000)
    private String submissionText;
    
    private boolean isLate;
    
    private Integer daysLate;
    
    private Double marksObtained;
    
    private Double totalMarks;
    
    private Double percentage;
    
    private String grade; // A+, A, B, etc.
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by_teacher_id")
    private Worker gradedBy; // Worker with ROLE_TEACHER
    
    private LocalDateTime gradedDate;
    
    @Column(length = 1000)
    private String teacherFeedback;
    
    @Column(length = 500)
    private String studentRemarks;
    
    private boolean requiresResubmission;
    
    private LocalDateTime resubmissionDeadline;
    
    private Integer submissionAttempt; // For resubmissions
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum SubmissionStatus {
        SUBMITTED, LATE, GRADED, RESUBMIT_REQUIRED, NOT_SUBMITTED, PENDING_REVIEW
    }
}

