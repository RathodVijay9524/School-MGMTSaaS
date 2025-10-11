package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vijay.User_Master.entity.Grade;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradeRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    
    private Long examId;
    
    private Long assignmentId;
    
    @NotNull(message = "Grade type is required")
    private Grade.GradeType gradeType;
    
    @NotNull(message = "Marks obtained is required")
    @PositiveOrZero(message = "Marks obtained must be zero or positive")
    private Double marksObtained;
    
    @NotNull(message = "Total marks is required")
    @Positive(message = "Total marks must be positive")
    private Double totalMarks;
    
    private String letterGrade;
    
    private Grade.GradeStatus status;
    
    @NotBlank(message = "Semester is required")
    private String semester;
    
    @NotBlank(message = "Academic year is required")
    private String academicYear;
    
    @NotNull(message = "Grade date is required")
    private LocalDate gradeDate;
    
    private Long gradedByTeacherId;
    
    @Size(max = 1000, message = "Feedback must not exceed 1000 characters")
    private String feedback;
    
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
    
    @JsonProperty("isPublished")
    private boolean isPublished;
}

