package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vijay.User_Master.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradeResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Long subjectId;
    private String subjectName;
    private Long examId;
    private String examName;
    private Long assignmentId;
    private String assignmentTitle;
    private Grade.GradeType gradeType;
    private Double marksObtained;
    private Double totalMarks;
    private Double percentage;
    private String letterGrade;
    private Grade.GradeStatus status;
    private String semester;
    private String academicYear;
    private LocalDate gradeDate;
    private Long gradedByTeacherId;
    private String gradedByTeacherName;
    private String feedback;
    private String remarks;
    
    @JsonProperty("isPublished")
    private boolean isPublished;
    
    // ========== NEW FIELDS - GRADE SCALE & WEIGHTED SCORING ==========
    private String gradeScale;
    private Double weightage;
    private Double weightedScore;
    
    // ========== NEW FIELDS - GPA CALCULATION ==========
    private Double gradePoint; // Numeric grade point (e.g., 4.0, 3.7, 3.3)
    private Double gpaValue; // GPA for this specific subject/grade
    private Double cumulativeGPA; // Cumulative GPA including all previous semesters
    private String gpaScale; // e.g., "4.0", "5.0", "10.0"
    
    // ========== NEW FIELDS - CLASS RANK ==========
    private Integer classRank; // Student's rank in class (1 = highest)
    private Integer totalStudents; // Total number of students in class
    private Double percentile; // Percentile rank (0-100, where 100 is best)
    private Integer sectionRank; // Rank within section
    private Integer gradeRank; // Rank within entire grade level
    
    // Computed fields
    private String gradeTypeDisplay;
    private String statusDisplay;
    private String rankDisplay; // e.g., "Rank 5 of 45 (Top 11%)"
    
    @JsonProperty("isPassed")
    private boolean isPassed;
    
    @JsonProperty("isTopPerformer")
    private boolean isTopPerformer; // True if rank <= 10 or percentile >= 90
}

