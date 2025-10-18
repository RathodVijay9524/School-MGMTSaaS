package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cloning quizzes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizCloneRequest {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    private String newTitle;
    private Boolean includeQuestions;
    private Boolean includeSettings;
    private Boolean publishImmediately;
    
    private Long newSubjectId; // Optional: change subject
    private Long newClassId; // Optional: change class
}
