package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolClassRequest {
    private String className;
    private String section;
    private Integer classLevel;
    private Integer capacity;
    private String roomNumber;
    private String academicYear;
    private String description;
    private String status;
}