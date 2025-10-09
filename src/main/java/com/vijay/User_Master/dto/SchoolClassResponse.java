package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.SchoolClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolClassResponse {

    private Long id;
    private String className;
    private String section;
    private Integer classLevel;
    private Integer maxStudents;
    private Integer capacity; // Alternative capacity field
    private Integer currentStudents;
    private String roomNumber;
    private String description;
    private Long classTeacherId;
    private String classTeacherName;
    private String academicYear;
    private String status;
    private boolean isActive; // Active status field
    private java.time.LocalDateTime createdOn; // Add createdOn field
    private java.time.LocalDateTime updatedOn; // Add updatedOn field
    
    // Computed fields
    private Integer availableSeats; // maxStudents - currentStudents
    private Double occupancyPercentage; // (currentStudents / maxStudents) * 100
    private String fullClassName; // className + " - " + section
    private boolean isFull; // currentStudents >= maxStudents
}

