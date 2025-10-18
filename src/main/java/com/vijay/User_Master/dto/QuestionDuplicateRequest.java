package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for duplicating questions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDuplicateRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    private Boolean includeOptions; // For MCQ
    private Boolean includePairs; // For matching
    private Boolean includeItems; // For ordering
    
    private String newQuestionText; // Optional: modify text while duplicating
    private Long newSubjectId; // Optional: change subject
    private Long newClassId; // Optional: change class
}
