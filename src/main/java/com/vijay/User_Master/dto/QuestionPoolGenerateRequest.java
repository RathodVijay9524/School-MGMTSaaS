package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for generating random questions from pool
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPoolGenerateRequest {

    @NotNull(message = "Pool ID is required")
    private Long poolId;

    @NotNull(message = "Number of questions is required")
    private Integer numberOfQuestions;

    private DifficultyLevel targetDifficulty;
    private List<QuestionType> questionTypes;
    
    // Distribution by difficulty (e.g., {"EASY": 5, "MEDIUM": 10, "HARD": 5})
    private Map<String, Integer> difficultyDistribution;
    
    // Distribution by type (e.g., {"MULTIPLE_CHOICE": 10, "TRUE_FALSE": 5})
    private Map<String, Integer> typeDistribution;
    
    private Boolean ensureUnique; // Don't repeat questions
}
