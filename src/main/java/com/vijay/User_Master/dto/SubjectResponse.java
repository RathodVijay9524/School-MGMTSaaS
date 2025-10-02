package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectResponse {

    private Long id;
    private String subjectCode;
    private String subjectName;
    private String description;
    private Subject.SubjectType type;
    private Integer credits;
    private Integer totalMarks;
    private Integer passingMarks;
    private String department;
    private Subject.SubjectStatus status;
    
    // Computed fields
    private String fullSubjectName; // subjectCode + " - " + subjectName
    private Double passingPercentage; // (passingMarks / totalMarks) * 100
}

