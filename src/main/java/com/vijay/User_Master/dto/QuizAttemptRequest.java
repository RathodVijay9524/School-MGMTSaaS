package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for starting/submitting quiz attempts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptRequest {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    private List<QuizAnswerRequest> answers;

    private Integer timeSpentSeconds;

    // Proctoring data
    private String ipAddress;
    private String userAgent;
    private Integer tabSwitchCount;
    private Integer copyPasteAttempts;
    private Integer fullScreenExitCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAnswerRequest {
        @NotNull(message = "Question ID is required")
        private Long questionId;
        
        @NotNull(message = "Answer is required")
        private String answer; // JSON string for complex answers
        
        private Integer timeSpentSeconds;
        private Boolean isFlagged;
    }
}
