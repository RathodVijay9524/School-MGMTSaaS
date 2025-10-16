package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    // ========== NEW RELATIONSHIP - HOMEWORK SUBMISSIONS ==========
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @lombok.Builder.Default
    private List<HomeworkSubmission> submissions = new ArrayList<>(); // All student submissions for this assignment
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;
    
    // ========== HELPER METHODS FOR SUBMISSION MANAGEMENT ==========
    
    /**
     * Add a submission to this assignment
     */
    public void addSubmission(HomeworkSubmission submission) {
        submissions.add(submission);
        submission.setAssignment(this);
    }
    
    /**
     * Remove a submission from this assignment
     */
    public void removeSubmission(HomeworkSubmission submission) {
        submissions.remove(submission);
        submission.setAssignment(null);
    }
    
    /**
     * Get count of submitted homework
     */
    public int getSubmittedCount() {
        return (int) submissions.stream()
            .filter(s -> s.getStatus() != HomeworkSubmission.SubmissionStatus.NOT_SUBMITTED)
            .count();
    }
    
    /**
     * Get count of graded submissions
     */
    public int getGradedCount() {
        return (int) submissions.stream()
            .filter(s -> s.getStatus() == HomeworkSubmission.SubmissionStatus.GRADED)
            .count();
    }
    
    /**
     * Get count of pending submissions (submitted but not graded)
     */
    public int getPendingGradingCount() {
        return (int) submissions.stream()
            .filter(s -> s.getStatus() == HomeworkSubmission.SubmissionStatus.SUBMITTED || 
                        s.getStatus() == HomeworkSubmission.SubmissionStatus.LATE ||
                        s.getStatus() == HomeworkSubmission.SubmissionStatus.PENDING_REVIEW)
            .count();
    }
    
    /**
     * Get count of late submissions
     */
    public int getLateSubmissionsCount() {
        return (int) submissions.stream()
            .filter(s -> s.getStatus() == HomeworkSubmission.SubmissionStatus.LATE)
            .count();
    }
    
    /**
     * Calculate average marks for this assignment
     */
    public Double getAverageMarks() {
        return submissions.stream()
            .filter(s -> s.getMarksObtained() != null)
            .mapToDouble(HomeworkSubmission::getMarksObtained)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Calculate submission rate percentage
     */
    public Double getSubmissionRate() {
        if (totalStudents == null || totalStudents == 0) return 0.0;
        return (getSubmittedCount() / (double) totalStudents) * 100.0;
    }
    
    /**
     * Check if assignment is overdue
     */
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate);
    }

    public enum AssignmentType {
        HOMEWORK, PROJECT, ESSAY, PRACTICAL, PRESENTATION, GROUP_PROJECT, QUIZ
    }

    public enum AssignmentStatus {
        ASSIGNED, IN_PROGRESS, SUBMITTED, GRADED, OVERDUE, CANCELLED
    }
}

