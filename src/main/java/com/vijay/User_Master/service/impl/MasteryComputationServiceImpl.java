package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.entity.LearningInteraction;
import com.vijay.User_Master.entity.SkillMastery;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.SkillMasteryRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.MasteryComputationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implementation of Mastery Computation Service
 * Uses EWMA (Exponentially Weighted Moving Average) for mastery calculation
 */
@Service
@Slf4j
@Transactional
public class MasteryComputationServiceImpl implements MasteryComputationService {

    @Autowired
    private SkillMasteryRepository skillMasteryRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    // Configuration constants
    private static final double EWMA_ALPHA = 0.3; // Weight for new observations
    private static final double DECAY_RATE_PER_WEEK = 0.05; // 5% decay per week
    private static final int MAX_MASTERY = 100;
    private static final int MIN_MASTERY = 0;

    @Override
    public SkillMastery updateMastery(Long studentId, String skillKey, LearningInteraction.Outcome outcome,
                                      Integer timeTakenSeconds, Integer hintsUsed, Long ownerId) {
        log.info("Updating mastery for student: {}, skill: {}, outcome: {}", studentId, skillKey, outcome);

        // Get or create skill mastery
        SkillMastery skillMastery = skillMasteryRepository.findByStudentIdAndSkillKeyAndIsDeletedFalse(studentId, skillKey)
                .orElse(null);

        if (skillMastery == null) {
            log.warn("SkillMastery not found for student: {}, skill: {}. Cannot update without subject context.", studentId, skillKey);
            return null;
        }

        // Calculate performance score (0-1)
        double performanceScore = calculatePerformanceScore(outcome, timeTakenSeconds, hintsUsed);

        // Update mastery using EWMA
        double currentMastery = skillMastery.getMasteryLevel();
        double newMastery = currentMastery + EWMA_ALPHA * (performanceScore * 100 - currentMastery);
        newMastery = Math.max(MIN_MASTERY, Math.min(MAX_MASTERY, newMastery));

        skillMastery.setMasteryLevel(newMastery);
        skillMastery.incrementAttempts();

        if (outcome == LearningInteraction.Outcome.CORRECT) {
            skillMastery.incrementCorrectAttempts();
        } else {
            skillMastery.incrementIncorrectAttempts();
        }

        skillMastery.updateAccuracy();
        skillMastery.setLastPracticedAt(LocalDateTime.now());

        if (timeTakenSeconds != null) {
            skillMastery.setTimeSpentMinutes(skillMastery.getTimeSpentMinutes() + (timeTakenSeconds / 60));
        }

        if (hintsUsed != null) {
            skillMastery.setHintsUsedCount(skillMastery.getHintsUsedCount() + hintsUsed);
        }

        // Update velocity
        Double velocity = calculateVelocity(studentId, skillKey);
        if (velocity != null) {
            skillMastery.setVelocityScore(velocity);
        }

        // Schedule next review
        int quality = calculateQuality(outcome, hintsUsed);
        LocalDateTime nextReview = scheduleNextReview(skillMastery, quality);
        skillMastery.setNextReviewAt(nextReview);

        return skillMasteryRepository.save(skillMastery);
    }

    @Override
    public void applyDecay(SkillMastery skillMastery) {
        if (skillMastery.getLastPracticedAt() == null) {
            return;
        }

        long weeksSinceLastPractice = ChronoUnit.WEEKS.between(skillMastery.getLastPracticedAt(), LocalDateTime.now());
        
        if (weeksSinceLastPractice > 0) {
            double decayFactor = Math.pow(1 - DECAY_RATE_PER_WEEK, weeksSinceLastPractice);
            double newMastery = skillMastery.getMasteryLevel() * decayFactor;
            newMastery = Math.max(MIN_MASTERY, newMastery);
            
            skillMastery.setMasteryLevel(newMastery);
            skillMastery.setLastDecayAppliedAt(LocalDateTime.now());
            
            skillMasteryRepository.save(skillMastery);
            log.info("Applied decay to skill: {}, new mastery: {}", skillMastery.getSkillKey(), newMastery);
        }
    }

    @Override
    public void applyDecayToInactiveSkills(int daysInactive) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysInactive);
        List<SkillMastery> inactiveSkills = skillMasteryRepository.findSkillsNeedingDecay(cutoffDate);
        
        log.info("Applying decay to {} inactive skills", inactiveSkills.size());
        
        for (SkillMastery skill : inactiveSkills) {
            applyDecay(skill);
        }
    }

    @Override
    public Double calculateVelocity(Long studentId, String skillKey) {
        // Get recent mastery changes (last 30 days)
        // Velocity = change in mastery per day
        // This is a simplified implementation
        return 0.5; // Placeholder - would need historical data
    }

    @Override
    public LocalDateTime scheduleNextReview(SkillMastery skillMastery, int quality) {
        // SM-2 Spaced Repetition Algorithm
        // quality: 0-5 (0=complete blackout, 5=perfect response)
        
        int interval;
        
        if (quality < 3) {
            // Failed - review tomorrow
            interval = 1;
        } else {
            // Passed - calculate interval based on mastery
            double mastery = skillMastery.getMasteryLevel();
            
            if (mastery >= 90) {
                interval = 30; // Review in 30 days
            } else if (mastery >= 80) {
                interval = 14; // Review in 2 weeks
            } else if (mastery >= 70) {
                interval = 7; // Review in 1 week
            } else if (mastery >= 60) {
                interval = 3; // Review in 3 days
            } else {
                interval = 1; // Review tomorrow
            }
        }
        
        return LocalDateTime.now().plusDays(interval);
    }

    @Override
    public SkillMastery getOrCreateSkillMastery(Long studentId, Long subjectId, String skillKey, 
                                                 String skillName, Long ownerId) {
        return skillMasteryRepository.findByStudentIdAndSkillKeyAndIsDeletedFalse(studentId, skillKey)
                .orElseGet(() -> {
                    Worker student = workerRepository.findById(studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
                    
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));
                    
                    User owner = userRepository.findById(ownerId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
                    
                    SkillMastery newSkillMastery = SkillMastery.builder()
                            .student(student)
                            .subject(subject)
                            .skillKey(skillKey)
                            .skillName(skillName)
                            .masteryLevel(0.0)
                            .avgAccuracy(0.0)
                            .totalAttempts(0)
                            .correctAttempts(0)
                            .consecutiveCorrect(0)
                            .consecutiveIncorrect(0)
                            .timeSpentMinutes(0)
                            .hintsUsedCount(0)
                            .velocityScore(0.0)
                            .owner(owner)
                            .build();
                    
                    return skillMasteryRepository.save(newSkillMastery);
                });
    }

    @Override
    public Double calculateMasteryChangeRate(Long studentId, String skillKey, int days) {
        // Placeholder - would need historical tracking
        return 0.0;
    }

    @Override
    public Double predictFutureMastery(SkillMastery skillMastery, int daysAhead) {
        // Simple linear prediction based on velocity
        double currentMastery = skillMastery.getMasteryLevel();
        double velocity = skillMastery.getVelocityScore();
        
        double predictedMastery = currentMastery + (velocity * daysAhead);
        return Math.max(MIN_MASTERY, Math.min(MAX_MASTERY, predictedMastery));
    }

    @Override
    public Double calculateMasteryConfidence(SkillMastery skillMastery) {
        // Confidence based on number of attempts
        int attempts = skillMastery.getTotalAttempts();
        
        if (attempts < 3) return 0.3;
        if (attempts < 5) return 0.5;
        if (attempts < 10) return 0.7;
        if (attempts < 20) return 0.85;
        return 0.95;
    }

    // Helper methods
    private double calculatePerformanceScore(LearningInteraction.Outcome outcome, 
                                            Integer timeTakenSeconds, Integer hintsUsed) {
        double baseScore;
        
        switch (outcome) {
            case CORRECT:
                baseScore = 1.0;
                break;
            case PARTIAL:
                baseScore = 0.5;
                break;
            case INCORRECT:
                baseScore = 0.0;
                break;
            case SKIPPED:
                baseScore = 0.0;
                break;
            default:
                baseScore = 0.0;
        }
        
        // Penalize for hints
        if (hintsUsed != null && hintsUsed > 0) {
            baseScore *= Math.max(0.5, 1.0 - (hintsUsed * 0.1));
        }
        
        return baseScore;
    }

    private int calculateQuality(LearningInteraction.Outcome outcome, Integer hintsUsed) {
        int quality;
        
        switch (outcome) {
            case CORRECT:
                quality = (hintsUsed == null || hintsUsed == 0) ? 5 : 4;
                break;
            case PARTIAL:
                quality = 3;
                break;
            case INCORRECT:
                quality = 1;
                break;
            case SKIPPED:
                quality = 0;
                break;
            default:
                quality = 0;
        }
        
        return quality;
    }
}
