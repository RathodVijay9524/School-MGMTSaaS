package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Rubric responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RubricResponse {

    private Long id;
    private String name;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Double totalPoints;
    private Boolean isActive;
    private String rubricType;
    private List<RubricCriterionResponse> criteria;
    private Long createdByTeacherId;
    private String createdByTeacherName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RubricCriterionResponse {
        private Long id;
        private String name;
        private String description;
        private Double maxPoints;
        private Double weightPercentage;
        private Integer orderIndex;
        private String excellentDescription;
        private String goodDescription;
        private String satisfactoryDescription;
        private String needsImprovementDescription;
    }
}
