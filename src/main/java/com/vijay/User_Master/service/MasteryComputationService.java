package com.vijay.User_Master.service;

import com.vijay.User_Master.entity.LearningInteraction;
import com.vijay.User_Master.entity.SkillMastery;

import java.time.LocalDateTime;

/**
 * Service for computing and updating skill mastery levels
 * Uses EWMA (Exponentially Weighted Moving Average) algorithm
 */
public interface MasteryComputationService {

    /**
     * Update mastery level based on a learning interaction
     */
    SkillMastery updateMastery(Long studentId, String skillKey, LearningInteraction.Outcome outcome, 
                               Integer timeTakenSeconds, Integer hintsUsed, Long ownerId);

    /**
     * Apply time-based decay to mastery level
     */
    void applyDecay(SkillMastery skillMastery);

    /**
     * Apply decay to all skills that haven't been practiced recently
     */
    void applyDecayToInactiveSkills(int daysInactive);

    /**
     * Calculate learning velocity (speed of improvement)
     */
    Double calculateVelocity(Long studentId, String skillKey);

    /**
     * Schedule next review using SM-2 spaced repetition algorithm
     */
    LocalDateTime scheduleNextReview(SkillMastery skillMastery, int quality);

    /**
     * Get or create skill mastery record
     */
    SkillMastery getOrCreateSkillMastery(Long studentId, Long subjectId, String skillKey, String skillName, Long ownerId);

    /**
     * Calculate mastery change rate (improvement per day)
     */
    Double calculateMasteryChangeRate(Long studentId, String skillKey, int days);

    /**
     * Predict future mastery level
     */
    Double predictFutureMastery(SkillMastery skillMastery, int daysAhead);

    /**
     * Calculate confidence interval for mastery
     */
    Double calculateMasteryConfidence(SkillMastery skillMastery);
}
