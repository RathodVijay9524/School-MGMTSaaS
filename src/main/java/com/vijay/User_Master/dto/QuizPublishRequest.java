package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for publishing/unpublishing quizzes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizPublishRequest {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotNull(message = "Publish status is required")
    private Boolean publish;

    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;
    private Boolean notifyStudents;
}
