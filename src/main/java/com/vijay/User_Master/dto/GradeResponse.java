package com.vijay.User_Master.dto;

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
    private boolean isPublished;
    
    // Computed fields
    private String gradeTypeDisplay;
    private String statusDisplay;
    private boolean isPassed;
}

