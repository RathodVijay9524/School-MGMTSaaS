package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for adaptive learning recommendations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdaptiveRecommendationResponse {

    private Long moduleId;
    private String moduleName;
    private String difficulty;
    private String skillKey;
    private String skillName;
    private String rationale;
    private Double currentMastery;
    private Double targetMastery;
    private Integer estimatedDurationMinutes;
    
    // Blocking information
    private Boolean isBlocked;
    private List<PrerequisiteBlockInfo> blockingPrerequisites;
    
    // Recommendation type
    private String recommendationType; // NEXT_MODULE, REVIEW, DIAGNOSTIC, REMEDIAL
    
    // Priority
    private Integer priority; // 1-5, 1 being highest
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrerequisiteBlockInfo {
        private String skillKey;
        private String skillName;
        private Double currentMastery;
        private Double requiredMastery;
        private Double gap;
    }
}
