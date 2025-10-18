package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for generated questions from pool
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPoolGenerateResponse {

    private Long poolId;
    private String poolName;
    private Integer requestedQuestions;
    private Integer generatedQuestions;
    private List<QuestionResponse> questions;
    private Double totalPoints;
}
