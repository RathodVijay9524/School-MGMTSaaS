package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolClassRequest {
    
    @NotBlank(message = "Class name is required")
    @Size(max = 100, message = "Class name must not exceed 100 characters")
    private String className;
    
    @Size(max = 50, message = "Section must not exceed 50 characters")
    private String section;
    
    @NotNull(message = "Class level is required")
    @Min(value = 1, message = "Class level must be at least 1")
    @Max(value = 12, message = "Class level must be at most 12")
    private Integer classLevel;
    
    @PositiveOrZero(message = "Capacity must be zero or positive")
    private Integer capacity;
    
    @Size(max = 50, message = "Room number must not exceed 50 characters")
    private String roomNumber;
    
    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    private String academicYear;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private String status;
}