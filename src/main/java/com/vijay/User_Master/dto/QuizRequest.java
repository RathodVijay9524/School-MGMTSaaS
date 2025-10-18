package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.QuizType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for creating/updating quizzes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Long subjectId;

    private Long classId;

    @NotNull(message = "Quiz type is required")
    private QuizType quizType;

    private List<Long> questionIds;

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
}
