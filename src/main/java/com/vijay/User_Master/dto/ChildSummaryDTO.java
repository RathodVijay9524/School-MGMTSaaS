package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for child summary information displayed in parent portal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildSummaryDTO {

    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String admissionNumber;
    private String rollNumber;
    
    // Class Information
    private Long classId;
    private String className;
    private String section;
    
    // Profile
    private String profileImageUrl;
    private String bloodGroup;
    private String status;
    
    // Quick Stats
    private Double attendancePercentage;
    private String overallGrade;
    private String letterGrade;
    private Double gpa;
    
    // Fees
    private Double totalFees;
    private Double feesPaid;
    private Double pendingFees;
    private boolean hasOverdueFees;
    
    // Academic
    private Integer totalSubjects;
    private Integer pendingAssignments;
    private Integer upcomingExams;
    
    // Attendance
    private Integer totalPresentDays;
    private Integer totalAbsentDays;
    private Integer totalLateDays;
    
    // Recent Activity
    private String lastAttendanceStatus;
    private String lastAttendanceDate;
    private String recentActivity;
    
    // Computed fields
    private boolean needsAttention; // If attendance < 75% or fees overdue
    private String performanceIndicator; // EXCELLENT, GOOD, AVERAGE, POOR
    private Integer alertsCount; // Number of pending items
}

