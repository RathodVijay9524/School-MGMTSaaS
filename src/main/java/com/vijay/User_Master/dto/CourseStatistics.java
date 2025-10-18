package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for Course statistics and analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatistics {

    // Overall course counts
    private long totalCourses;
    private long activeCourses;
    private long plannedCourses;
    private long ongoingCourses;
    private long completedCourses;
    private long cancelledCourses;
    
    // Enrollment statistics
    private long totalEnrolledStudents;
    private long totalAvailableSeats;
    private Double averageEnrollmentPercentage;
    private long fullyEnrolledCourses;
    private long underEnrolledCourses; // Less than 50% capacity
    
    // Teacher workload
    private long totalCoursesWithTeachers;
    private long totalCoursesWithoutTeachers;
    private Double averageCoursesPerTeacher;
    
    // Subject distribution
    private Map<String, Long> coursesBySubject; // Subject name -> count
    
    // Class distribution
    private Map<String, Long> coursesByClass; // Class name -> count
    
    // Semester distribution
    private Map<String, Long> coursesBySemester; // Semester -> count
    
    // Status distribution
    private Map<String, Long> coursesByStatus; // Status -> count
    
    // Top 5 most enrolled courses
    private Map<String, Integer> topEnrolledCourses; // Course name -> enrollment count
    
    // Top 5 subjects by course count
    private Map<String, Long> topSubjects; // Subject name -> course count
    
    // Credit distribution
    private Double totalCredits;
    private Double averageCreditsPerCourse;
    
    // Capacity utilization
    private Double overallCapacityUtilization; // Percentage of total seats filled
}
