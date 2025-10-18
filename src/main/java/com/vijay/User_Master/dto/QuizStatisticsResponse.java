package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for quiz statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatisticsResponse {

    private Long quizId;
    private String quizTitle;
    
    // Overall statistics
    private Long totalAttempts;
    private Long completedAttempts;
    private Long inProgressAttempts;
    private Long uniqueStudents;
    
    // Score statistics
    private Double averageScore;
    private Double highestScore;
    private Double lowestScore;
    private Double medianScore;
    private Double passingRate; // Percentage of students who passed
    
    // Time statistics
    private Integer averageTimeSpent; // In seconds
    private Integer shortestTime;
    private Integer longestTime;
    
    // Question statistics
    private List<QuestionStatistics> questionStats;
    
    // Score distribution
    private Map<String, Long> scoreDistribution; // e.g., "0-20": 5, "21-40": 10, etc.
    
    // Proctoring statistics
    private Double averageTabSwitches;
    private Double averageCopyPasteAttempts;
    private Long suspiciousAttempts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionStatistics {
        private Long questionId;
        private String questionText;
        private Long totalResponses;
        private Long correctResponses;
        private Double correctPercentage;
        private Double averageScore;
        private Double averageTimeSpent;
        private String difficulty; // Based on correct percentage
    }
}
