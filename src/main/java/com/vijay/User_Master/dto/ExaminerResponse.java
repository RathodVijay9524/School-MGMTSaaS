package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vijay.User_Master.entity.Examiner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExaminerResponse {

    private Long id;
    private Long examId;
    private String examName;
    private Long teacherId;
    private String teacherName;
    
    // External Examiner Details
    private String externalExaminerName;
    private String externalExaminerEmail;
    private String externalExaminerPhone;
    private String institution;
    
    private Examiner.ExaminerRole role;
    private Examiner.ExaminerStatus status;
    private LocalDate assignedDate;
    private LocalDate completionDate;
    private String specialization;
    
    @JsonProperty("isBlindGrading")
    private boolean isBlindGrading;
    
    private String remarks;
    
    // Computed fields
    private String displayName; // Teacher name or external examiner name
    private String roleDisplay;
    private String statusDisplay;
    
    @JsonProperty("isExternal")
    private boolean isExternal; // True if external examiner
    
    @JsonProperty("isCompleted")
    private boolean isCompleted; // True if status is COMPLETED
}

