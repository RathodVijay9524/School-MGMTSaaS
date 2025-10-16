package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "grades")
@EntityListeners(AuditingEntityListener.class)
public class Grade extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student; // Worker with ROLE_STUDENT
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeType gradeType; // EXAM, ASSIGNMENT, QUIZ, PROJECT, PRACTICAL
    
    private Double marksObtained;
    
    private Double totalMarks;
    
    private Double percentage;
    
    private String letterGrade; // A+, A, B+, B, C, etc.
    
    @Enumerated(EnumType.STRING)
    private GradeStatus status; // PASS, FAIL, PENDING
    
    private String semester; // e.g., "Fall 2024"
    
    private String academicYear; // e.g., "2024-2025"
    
    private LocalDate gradeDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by_teacher_id")
    private Worker gradedBy; // Worker with ROLE_TEACHER
    
    @Column(length = 1000)
    private String feedback;
    
    @Column(length = 500)
    private String remarks;
    
    private boolean isPublished; // Whether visible to student/parent
    
    // ========== NEW FIELDS - GRADE SCALE & WEIGHTED SCORING ==========
    @Column(length = 50)
    private String gradeScale; // e.g., "4.0 Scale", "10 Point Scale", "Percentage"
    
    private Double weightage; // Weightage of this grade in final calculation (e.g., 30% for midterm)
    
    private Double weightedScore; // Calculated: (marksObtained/totalMarks) * weightage
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum GradeType {
        EXAM, ASSIGNMENT, QUIZ, PROJECT, PRACTICAL, MIDTERM, FINAL, UNIT_TEST
    }

    public enum GradeStatus {
        PASS, FAIL, PENDING, RETEST_REQUIRED, INCOMPLETE
    }
}

