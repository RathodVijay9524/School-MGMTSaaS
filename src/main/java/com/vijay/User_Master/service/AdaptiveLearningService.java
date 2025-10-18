package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AdaptiveRecommendationResponse;
import com.vijay.User_Master.dto.LearningInteractionRequest;
import com.vijay.User_Master.dto.SkillMasteryResponse;
import com.vijay.User_Master.entity.LearningModule;

import java.util.List;
import java.util.Map;

/**
 * Service for adaptive learning functionality
 */
public interface AdaptiveLearningService {

    /**
     * Get next recommended module for a student
     */
    AdaptiveRecommendationResponse getNextModule(Long studentId, Long subjectId, Long ownerId);

    /**
     * Record a learning interaction and update mastery
     */
    SkillMasteryResponse recordInteraction(LearningInteractionRequest request, Long ownerId);

    /**
     * Get review queue for a student (modules due for review)
     */
    List<AdaptiveRecommendationResponse> getReviewQueue(Long studentId, Long ownerId);

    /**
     * Check if student can access a module (prerequisites met)
     */
    boolean canAccessModule(Long studentId, Long moduleId);

    /**
     * Get diagnostic assessment for weak skills
     */
    List<AdaptiveRecommendationResponse> getDiagnosticAssessment(Long studentId, Long subjectId, Long ownerId);

    /**
     * Get remedial content recommendations
     */
    List<AdaptiveRecommendationResponse> getRemedialContent(Long studentId, String skillKey, Long ownerId);

    /**
     * Get all skill mastery for a student
     */
    List<SkillMasteryResponse> getStudentMastery(Long studentId, Long subjectId, Long ownerId);

    /**
     * Get mastery heatmap (all students, all skills)
     */
    Map<String, Object> getMasteryHeatmap(Long subjectId, Long ownerId);

    /**
     * Get learning velocity trends
     */
    Map<String, Object> getVelocityTrends(Long studentId, Long ownerId);

    /**
     * Get prerequisite bottleneck report
     */
    Map<String, Object> getPrerequisiteBottlenecks(Long subjectId, Long ownerId);

    /**
     * Get adaptive learning statistics
     */
    Map<String, Object> getAdaptiveLearningStatistics(Long studentId, Long ownerId);

    /**
     * Manually adjust mastery level (teacher override)
     */
    SkillMasteryResponse adjustMastery(Long studentId, String skillKey, Double newMasteryLevel, String reason, Long ownerId);

    /**
     * Reset skill mastery
     */
    void resetSkillMastery(Long studentId, String skillKey, Long ownerId);

    /**
     * Get skills needing attention (low mastery or struggling)
     */
    List<SkillMasteryResponse> getSkillsNeedingAttention(Long studentId, Long ownerId);

    /**
     * Get mastered skills
     */
    List<SkillMasteryResponse> getMasteredSkills(Long studentId, Long ownerId);
}
