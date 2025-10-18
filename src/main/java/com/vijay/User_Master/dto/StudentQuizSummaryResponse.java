package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for student's quiz summary/dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizSummaryResponse {

    private Long studentId;
    private String studentName;
    
    // Overall statistics
    private Integer totalQuizzesAvailable;
    private Integer quizzesCompleted;
    private Integer quizzesInProgress;
    private Integer quizzesPassed;
    private Integer quizzesFailed;
    
    // Score statistics
    private Double averageScore;
    private Double highestScore;
    private Double lowestScore;
    
    // Time statistics
    private Integer totalTimeSpent; // In minutes
    private Integer averageTimePerQuiz;
    
    // Recent quizzes
    private List<QuizSummary> recentQuizzes;
    
    // Upcoming quizzes
    private List<QuizSummary> upcomingQuizzes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizSummary {
        private Long quizId;
        private String quizTitle;
        private String subjectName;
        private Integer attempts;
        private Integer maxAttempts;
        private Double bestScore;
        private Double latestScore;
        private Boolean passed;
        private String status; // AVAILABLE, IN_PROGRESS, COMPLETED, EXPIRED
    }
}
