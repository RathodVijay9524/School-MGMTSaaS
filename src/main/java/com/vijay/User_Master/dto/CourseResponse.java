package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

/**
 * DTO for Course response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    
    // Subject details
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    
    // Class details
    private Long classId;
    private String className;
    private String classGrade;
    
    // Teacher details
    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private String semester;
    private Integer credits;
    private Integer maxStudents;
    private Integer enrolledStudents;
    private Course.CourseStatus status;
    private String syllabus;
    private String schedule;
    private String classroom;
    
    // Owner details
    private Long ownerId;
    private String ownerName;
    
    // Audit fields
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;
    
    // ========== COMPUTED FIELDS ==========
    
    // Enrollment metrics
    private Integer availableSeats;
    private Double enrollmentPercentage;
    private boolean isFullyEnrolled;
    private boolean hasAvailableSeats;
    
    // Date and duration metrics
    private boolean isOngoing;
    private boolean isCompleted;
    private boolean isUpcoming;
    private long daysUntilStart;
    private long daysUntilEnd;
    private long totalDurationDays;
    private long daysElapsed;
    private Double progressPercentage;
    
    // Status display
    private String statusDisplay;
    private String enrollmentDisplay; // e.g., "45/60 (75%)"
    private String dateRangeDisplay; // e.g., "Jan 15, 2025 - May 30, 2025"
    private String durationDisplay; // e.g., "16 weeks"
}
