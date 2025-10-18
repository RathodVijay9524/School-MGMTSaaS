package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for question pool responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPoolResponse {

    private Long id;
    private String poolName;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Integer questionCount;
    private List<QuestionResponse> questions;
    private List<QuestionResponse.QuestionTagResponse> tags;
    private Integer questionsToSelect;
    private DifficultyLevel targetDifficulty;
    private Boolean isActive;
}
