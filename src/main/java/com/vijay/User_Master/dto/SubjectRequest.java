package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Subject;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectRequest {

    @NotBlank(message = "Subject code is required")
    @Size(max = 20, message = "Subject code must not exceed 20 characters")
    private String subjectCode;
    
    @NotBlank(message = "Subject name is required")
    @Size(max = 100, message = "Subject name must not exceed 100 characters")
    private String subjectName;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Subject.SubjectType type;
    
    @PositiveOrZero(message = "Credits must be zero or positive")
    private Integer credits;
    
    @PositiveOrZero(message = "Total marks must be zero or positive")
    private Integer totalMarks;
    
    @PositiveOrZero(message = "Passing marks must be zero or positive")
    private Integer passingMarks;
    
    @Size(max = 50, message = "Department must not exceed 50 characters")
    private String department;
    
    private Subject.SubjectStatus status;
}
