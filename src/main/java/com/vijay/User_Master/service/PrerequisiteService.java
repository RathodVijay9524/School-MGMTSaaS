package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AdaptiveRecommendationResponse;

import java.util.List;
import java.util.Map;

/**
 * Service for managing skill prerequisites
 */
public interface PrerequisiteService {

    /**
     * Check if student meets all prerequisites for a skill
     */
    boolean checkPrerequisites(Long studentId, String skillKey);

    /**
     * Get all prerequisites for a skill
     */
    List<String> getPrerequisiteChain(String skillKey);

    /**
     * Get blocking skills (prerequisites not met)
     */
    List<AdaptiveRecommendationResponse.PrerequisiteBlockInfo> getBlockingSkills(Long studentId, String skillKey);

    /**
     * Check if a specific prerequisite is met
     */
    boolean isPrerequisiteMet(Long studentId, String prerequisiteSkillKey, Double requiredMastery);

    /**
     * Get all skills blocked by low mastery
     */
    Map<String, List<String>> getBlockedSkillsMap(Long studentId, Long subjectId);

    /**
     * Find prerequisite bottlenecks (skills blocking many others)
     */
    List<String> findPrerequisiteBottlenecks(Long subjectId);

    /**
     * Calculate prerequisite completion percentage
     */
    Double calculatePrerequisiteCompletion(Long studentId, String skillKey);

    /**
     * Get recommended prerequisite learning order
     */
    List<String> getRecommendedLearningOrder(Long subjectId);
}
