package com.vijay.User_Master.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Analytics DTO
 * Used for both Super Admin (all schools) and School Owner (single school)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAnalytics {
    
    // School Information (null for super admin viewing all schools)
    private String businessId;
    private String schoolName;
    
    // Overall Statistics
    private Integer totalSchools; // Only for super admin
    private Integer totalStudents;
    private Integer totalTeachers;
    private Integer totalClasses;
    private Integer totalSubjects;
    
    // Student Analytics
    private Integer activeStudents;
    private Integer inactiveStudents;
    private Integer newStudentsThisMonth;
    private Double averageAttendancePercentage;
    
    // Teacher Analytics
    private Integer activeTeachers;
    private Integer inactiveTeachers;
    private Integer newTeachersThisMonth;
    
    // Academic Analytics
    private Double averageGPA;
    private Integer totalExamsScheduled;
    private Integer upcomingExams;
    private Integer totalAssignments;
    private Integer pendingAssignments;
    
    // Financial Analytics
    private BigDecimal totalFeeCollected;
    private BigDecimal totalFeeCollectedThisMonth;
    private BigDecimal totalFeePending;
    private BigDecimal totalFeeOverdue;
    private Integer studentsWithPendingFees;
    private Integer studentsWithOverdueFees;
    
    // Attendance Analytics
    private Integer totalAttendanceMarkedToday;
    private Integer studentsPreseToday;
    private Integer studentsAbsentToday;
    private List<AttendanceTrend> attendanceTrends; // Last 7 days
    
    // Library Analytics
    private Integer totalBooks;
    private Integer booksIssued;
    private Integer booksAvailable;
    private Integer overdueBooks;
    
    // Communication Analytics
    private Integer emailsSentThisMonth;
    private Integer smsSentThisMonth;
    private Integer whatsappSentThisMonth;
    
    // Recent Activities
    private List<RecentActivity> recentActivities;
    
    // Class-wise breakdown
    private List<ClassAnalytics> classWiseAnalytics;
    
    // Monthly trends
    private List<MonthlyTrend> monthlyTrends;
    
    // For Super Admin - School-wise analytics
    private List<SchoolAnalyticsSummary> schoolWiseAnalytics;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendanceTrend {
        private String date;
        private Integer present;
        private Integer absent;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentActivity {
        private String type; // STUDENT_ADMISSION, FEE_PAYMENT, EXAM_SCHEDULED, etc.
        private String description;
        private String timestamp;
        private String performedBy;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClassAnalytics {
        private String className;
        private Integer totalStudents;
        private Double averageAttendance;
        private Double averageGPA;
        private BigDecimal totalFees;
        private BigDecimal collectedFees;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrend {
        private String month;
        private Integer newAdmissions;
        private BigDecimal feeCollected;
        private Double averageAttendance;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuickStats {
        private Integer totalStudents;
        private Integer totalTeachers;
        private Integer totalClasses;
        private Integer totalSubjects;
        private Double averageAttendance;
        private BigDecimal totalFeesCollected;
        private Integer upcomingExams;
        private Integer pendingAssignments;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SchoolAnalyticsSummary {
        private String businessId;
        private String schoolName;
        private Integer totalStudents;
        private Integer totalTeachers;
        private BigDecimal monthlyRevenue;
        private String subscriptionPlan;
        private Boolean isActive;
    }
}

