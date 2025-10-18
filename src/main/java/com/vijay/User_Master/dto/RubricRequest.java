package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating rubrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RubricRequest {

    @NotBlank(message = "Rubric name is required")
    private String name;

    private String description;

    private Long subjectId;

    @NotNull(message = "Total points is required")
    private Double totalPoints;

    @NotBlank(message = "Rubric type is required")
    private String rubricType; // ESSAY, PROJECT, etc.

    private List<RubricCriterionRequest> criteria;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RubricCriterionRequest {
        @NotBlank(message = "Criterion name is required")
        private String name;
        
        private String description;
        
        @NotNull(message = "Max points is required")
        private Double maxPoints;
        
        private Double weightPercentage;
        private Integer orderIndex;
        
        // Performance level descriptions
        private String excellentDescription;
        private String goodDescription;
        private String satisfactoryDescription;
        private String needsImprovementDescription;
    }
}
