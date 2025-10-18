package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for question bank statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankStatisticsResponse {

    private Long totalQuestions;
    private Long activeQuestions;
    private Long autoGradableQuestions;
    
    // By question type
    private Map<String, Long> questionsByType;
    
    // By difficulty
    private Map<String, Long> questionsByDifficulty;
    
    // By Bloom's level
    private Map<String, Long> questionsByBloomsLevel;
    
    // By subject
    private Map<String, Long> questionsBySubject;
    
    // Usage statistics
    private Long totalQuestionUsage;
    private Double averageUsagePerQuestion;
    private Long mostUsedQuestionId;
    private String mostUsedQuestionText;
    
    // Quality metrics
    private Double averageQuestionScore;
    private Long questionsNeedingReview; // Low average scores
}
