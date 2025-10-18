package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for quiz review mode (after submission)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizReviewResponse {

    private Long attemptId;
    private String quizTitle;
    private Double totalScore;
    private Double maxScore;
    private Double percentage;
    private Boolean passed;
    private String overallFeedback;
    
    private List<QuestionReview> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionReview {
        private Long questionId;
        private String questionText;
        private String questionType;
        private String studentAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Double pointsEarned;
        private Double maxPoints;
        private String explanation;
        private String feedback;
        private Integer timeSpent;
        private Boolean wasFlagged;
        
        // For MCQ
        private List<OptionReview> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionReview {
        private String optionText;
        private Boolean isCorrect;
        private Boolean wasSelected;
        private String feedback;
    }
}
