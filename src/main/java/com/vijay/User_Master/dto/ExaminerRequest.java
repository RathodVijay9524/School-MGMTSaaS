package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Examiner;
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
public class ExaminerRequest {

    private Long teacherId; // Internal teacher ID (optional if external examiner)
    
    // External Examiner Fields
    @Size(max = 200, message = "External examiner name must not exceed 200 characters")
    private String externalExaminerName;
    
    @Email(message = "Invalid email format")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    private String externalExaminerEmail;
    
    @Pattern(regexp = "^[0-9+\\-() ]*$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String externalExaminerPhone;
    
    @Size(max = 200, message = "Institution name must not exceed 200 characters")
    private String institution;
    
    @NotNull(message = "Examiner role is required")
    private Examiner.ExaminerRole role; // PRIMARY, SECONDARY, EXTERNAL, MODERATOR
    
    private Examiner.ExaminerStatus status; // ASSIGNED, COMPLETED, PENDING
    
    private LocalDate assignedDate;
    
    private LocalDate completionDate;
    
    @Size(max = 1000, message = "Specialization must not exceed 1000 characters")
    private String specialization;
    
    private boolean isBlindGrading;
    
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}

