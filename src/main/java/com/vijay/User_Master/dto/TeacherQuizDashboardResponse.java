package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for teacher's quiz dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherQuizDashboardResponse {

    private Long teacherId;
    private String teacherName;
    
    // Quiz statistics
    private Integer totalQuizzes;
    private Integer publishedQuizzes;
    private Integer draftQuizzes;
    private Integer activeQuizzes;
    
    // Student engagement
    private Integer totalStudents;
    private Integer activeStudents;
    private Integer totalAttempts;
    private Integer pendingGrading;
    
    // Performance metrics
    private Double averageClassScore;
    private Double averagePassRate;
    
    // Recent activity
    private List<RecentActivity> recentActivities;
    
    // Quizzes needing attention
    private List<QuizAlert> alerts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String activityType; // QUIZ_CREATED, ATTEMPT_SUBMITTED, etc.
        private String description;
        private String timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAlert {
        private Long quizId;
        private String quizTitle;
        private String alertType; // PENDING_GRADING, LOW_PARTICIPATION, HIGH_FAILURE_RATE
        private String message;
        private Integer count;
    }
}
