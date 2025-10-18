package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.AttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for quiz attempt responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponse {

    private Long id;
    private Long quizId;
    private String quizTitle;
    private Long studentId;
    private String studentName;
    private Integer attemptNumber;
    private AttemptStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer timeSpentSeconds;
    private Double totalScore;
    private Double maxScore;
    private Double percentage;
    private Boolean passed;
    private String feedback;
    private Integer tabSwitchCount;
    private Integer copyPasteAttempts;
    private Integer fullScreenExitCount;
    
    // Question responses
    private List<QuizResponseDetail> responses;
    
    // Statistics
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Integer flaggedQuestions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizResponseDetail {
        private Long responseId;
        private Long questionId;
        private String questionText;
        private String studentAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Double pointsEarned;
        private Double maxPoints;
        private String feedback;
        private Integer timeSpentSeconds;
        private Boolean autoGraded;
        private Boolean isFlagged;
    }
}
