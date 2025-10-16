package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    
    // ========== NEW FIELDS - QUESTION PAPER DETAILS ==========
    private String questionPaperUrl; // URL/path to uploaded question paper PDF
    
    private Integer totalQuestions; // Total number of questions in exam
    
    @Column(length = 500)
    private String questionPattern; // e.g., "MCQ: 20 (40 marks), Short Answer: 10 (30 marks), Essay: 5 (30 marks)"
    
    // ========== NEW FIELDS - NEGATIVE MARKING ==========
    @lombok.Builder.Default
    private boolean hasNegativeMarking = false;
    
    private Double negativeMarkingPercentage; // e.g., 25.0 means 25% marks deducted for wrong answer
    
    // ========== NEW FIELDS - BLIND GRADING ==========
    @lombok.Builder.Default
    private boolean isBlindGraded = false; // Anonymous grading to reduce bias
    
    // ========== NEW FIELDS - MULTIPLE EXAMINERS SUPPORT ==========
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @lombok.Builder.Default
    private List<Examiner> examiners = new ArrayList<>(); // Support for multiple examiners
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;
    
    // ========== HELPER METHODS FOR EXAMINER MANAGEMENT ==========
    
    /**
     * Add an examiner to this exam
     */
    public void addExaminer(Examiner examiner) {
        examiners.add(examiner);
        examiner.setExam(this);
    }
    
    /**
     * Remove an examiner from this exam
     */
    public void removeExaminer(Examiner examiner) {
        examiners.remove(examiner);
        examiner.setExam(null);
    }
    
    /**
     * Get primary examiner (if exists)
     */
    public Examiner getPrimaryExaminer() {
        return examiners.stream()
            .filter(e -> e.getRole() == Examiner.ExaminerRole.PRIMARY)
            .findFirst()
            .orElse(null);
    }

    public enum ExamType {
        MIDTERM, FINAL, UNIT_TEST, QUARTERLY, HALF_YEARLY, ANNUAL, SURPRISE_TEST, PRACTICAL
    }

    public enum ExamStatus {
        SCHEDULED, ONGOING, COMPLETED, CANCELLED, POSTPONED, RESCHEDULED
    }
}

