package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.QuizType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for quiz responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private QuizType quizType;
    private Integer questionCount;
    private Boolean randomizeQuestions;
    private Integer questionsToShow;
    private Integer timeLimitMinutes;
    private Boolean showTimer;
    private Integer maxAttempts;
    private Boolean allowReview;
    private Double totalPoints;
    private Double passingScore;
    private Boolean autoGrade;
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;
    private Boolean showCorrectAnswers;
    private Boolean showScoreImmediately;
    private Boolean showFeedback;
    private Boolean showOneQuestionAtTime;
    private Boolean requireProctoring;
    private Boolean preventCopyPaste;
    private Boolean fullScreenMode;
    private Boolean shuffleAnswers;
    private Boolean lockQuestionsAfterAnswering;
    private String instructions;
    private Boolean isActive;
    private Boolean isPublished;
    private Boolean isAvailable;
    
    // Statistics
    private Long totalAttempts;
    private Long uniqueStudents;
    private Double averageScore;
    
    // For student view
    private Integer studentAttempts;
    private Integer attemptsRemaining;
    private QuizAttemptSummary latestAttempt;
    private QuizAttemptSummary bestAttempt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAttemptSummary {
        private Long attemptId;
        private Integer attemptNumber;
        private Double totalScore;
        private Double percentage;
        private Boolean passed;
        private LocalDateTime submittedAt;
    }
}
