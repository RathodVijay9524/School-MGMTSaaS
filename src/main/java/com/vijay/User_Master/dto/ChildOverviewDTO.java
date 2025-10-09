package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for complete child overview with all details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildOverviewDTO {

    private ChildSummaryDTO student;
    
    // Attendance Details
    private AttendanceSummaryDTO attendance;
    
    // Grade Details
    private GradeSummaryDTO grades;
    
    // Fee Details
    private FeeSummaryDTO fees;
    
    // Recent Assignments
    private List<AssignmentResponse> recentAssignments;
    
    // Upcoming Exams
    private List<ExamResponse> upcomingExams;
    
    // Recent Notifications
    private List<NotificationDTO> notifications;
    
    // Teacher Comments
    private List<TeacherCommentDTO> teacherComments;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceSummaryDTO {
        private Double percentage;
        private Integer totalDays;
        private Integer presentDays;
        private Integer absentDays;
        private Integer lateDays;
        private Integer excusedDays;
        private Integer currentMonthPresent;
        private Integer currentMonthAbsent;
        private String trend; // IMPROVING, DECLINING, STABLE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradeSummaryDTO {
        private Double overallGpa;
        private String overallLetterGrade;
        private Integer totalGrades;
        private Integer publishedGrades;
        private Integer pendingGrades;
        private String highestSubject;
        private String lowestSubject;
        private String trend; // IMPROVING, DECLINING, STABLE
        private List<SubjectGradeDTO> subjectGrades;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectGradeDTO {
        private String subjectName;
        private Double averageScore;
        private String letterGrade;
        private Integer totalAssessments;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeeSummaryDTO {
        private Double totalFees;
        private Double paidFees;
        private Double pendingFees;
        private Double overdueFees;
        private String nextDueDate;
        private Double nextDueAmount;
        private Integer totalInstallments;
        private Integer paidInstallments;
        private String paymentStatus; // UP_TO_DATE, PENDING, OVERDUE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDTO {
        private Long id;
        private String type; // ATTENDANCE, FEE, GRADE, EXAM, ASSIGNMENT, GENERAL
        private String title;
        private String message;
        private String date;
        private String priority; // HIGH, MEDIUM, LOW
        private boolean isRead;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherCommentDTO {
        private Long id;
        private String teacherName;
        private String subject;
        private String comment;
        private String date;
        private String type; // ACADEMIC, BEHAVIORAL, GENERAL
    }
}

