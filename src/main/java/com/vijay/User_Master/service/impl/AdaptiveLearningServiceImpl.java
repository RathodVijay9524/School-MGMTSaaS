package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.AdaptiveRecommendationResponse;
import com.vijay.User_Master.dto.LearningInteractionRequest;
import com.vijay.User_Master.dto.SkillMasteryResponse;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.AdaptiveLearningService;
import com.vijay.User_Master.service.MasteryComputationService;
import com.vijay.User_Master.service.PrerequisiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Adaptive Learning Service
 */
@Service
@Slf4j
@Transactional
public class AdaptiveLearningServiceImpl implements AdaptiveLearningService {

    @Autowired
    private SkillMasteryRepository masteryRepository;

    @Autowired
    private LearningInteractionRepository interactionRepository;

    @Autowired
    private LearningModuleRepository moduleRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MasteryComputationService masteryComputationService;

    @Autowired
    private PrerequisiteService prerequisiteService;

    @Override
    public AdaptiveRecommendationResponse getNextModule(Long studentId, Long subjectId, Long ownerId) {
        log.info("Getting next module for student: {}, subject: {}", studentId, subjectId);

        // Get student's skill masteries
        List<SkillMastery> masteries = masteryRepository.findByStudentIdAndSubjectIdAndIsDeletedFalseOrderByMasteryLevelDesc(
                studentId, subjectId);

        // Find skills needing improvement
        List<SkillMastery> lowMasterySkills = masteries.stream()
                .filter(m -> m.getMasteryLevel() < 60.0)
                .sorted(Comparator.comparing(SkillMastery::getMasteryLevel))
                .collect(Collectors.toList());

        // If there are low mastery skills, recommend remedial content
        if (!lowMasterySkills.isEmpty()) {
            SkillMastery targetSkill = lowMasterySkills.get(0);
            return buildRecommendation(targetSkill, "REMEDIAL", 1);
        }

        // Check for skills needing review
        List<SkillMastery> reviewSkills = masteries.stream()
                .filter(SkillMastery::needsReview)
                .collect(Collectors.toList());

        if (!reviewSkills.isEmpty()) {
            SkillMastery targetSkill = reviewSkills.get(0);
            return buildRecommendation(targetSkill, "REVIEW", 2);
        }

        // Find next skill to learn (medium mastery)
        List<SkillMastery> mediumMasterySkills = masteries.stream()
                .filter(m -> m.getMasteryLevel() >= 60.0 && m.getMasteryLevel() < 80.0)
                .sorted(Comparator.comparing(SkillMastery::getMasteryLevel).reversed())
                .collect(Collectors.toList());

        if (!mediumMasterySkills.isEmpty()) {
            SkillMastery targetSkill = mediumMasterySkills.get(0);
            return buildRecommendation(targetSkill, "NEXT_MODULE", 3);
        }

        // All skills are high mastery - recommend advanced content
        if (!masteries.isEmpty()) {
            SkillMastery targetSkill = masteries.get(0);
            return buildRecommendation(targetSkill, "NEXT_MODULE", 4);
        }

        // No mastery data - return null
        return null;
    }

    @Override
    public SkillMasteryResponse recordInteraction(LearningInteractionRequest request, Long ownerId) {
        log.info("Recording interaction for student: {}, skill: {}", request.getStudentId(), request.getSkillKey());

        // Validate entities
        Worker student = workerRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        LearningModule module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", request.getModuleId()));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        // Create interaction record
        LearningInteraction interaction = LearningInteraction.builder()
                .student(student)
                .module(module)
                .skillKey(request.getSkillKey())
                .difficulty(LearningInteraction.DifficultyLevel.valueOf(request.getDifficulty()))
                .outcome(LearningInteraction.Outcome.valueOf(request.getOutcome()))
                .score(request.getScore())
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .hintsUsed(request.getHintsUsed())
                .questionType(request.getQuestionType())
                .confidenceLevel(request.getConfidenceLevel())
                .notes(request.getNotes())
                .owner(owner)
                .build();

        interactionRepository.save(interaction);

        // Update mastery
        SkillMastery updatedMastery = masteryComputationService.updateMastery(
                request.getStudentId(),
                request.getSkillKey(),
                interaction.getOutcome(),
                request.getTimeTakenSeconds(),
                request.getHintsUsed(),
                ownerId
        );

        return mapToSkillMasteryResponse(updatedMastery);
    }

    @Override
    public List<AdaptiveRecommendationResponse> getReviewQueue(Long studentId, Long ownerId) {
        log.info("Getting review queue for student: {}", studentId);

        List<SkillMastery> reviewSkills = masteryRepository.findByStudentIdAndNextReviewAtBeforeAndIsDeletedFalse(
                studentId, LocalDateTime.now());

        return reviewSkills.stream()
                .map(skill -> buildRecommendation(skill, "REVIEW", 1))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canAccessModule(Long studentId, Long moduleId) {
        LearningModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", moduleId));

        // Check if module has skill keys defined
        if (module.getPrerequisites() == null || module.getPrerequisites().isEmpty()) {
            return true; // No prerequisites
        }

        // Parse skill keys from prerequisites (assuming comma-separated)
        String[] skillKeys = module.getPrerequisites().split(",");
        
        for (String skillKey : skillKeys) {
            if (!prerequisiteService.checkPrerequisites(studentId, skillKey.trim())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<AdaptiveRecommendationResponse> getDiagnosticAssessment(Long studentId, Long subjectId, Long ownerId) {
        log.info("Getting diagnostic assessment for student: {}, subject: {}", studentId, subjectId);

        // Find skills with low mastery or high consecutive incorrect
        List<SkillMastery> weakSkills = masteryRepository.findLowMasterySkills(studentId, 50.0);
        List<SkillMastery> strugglingSkills = masteryRepository.findSkillsWithStruggles(studentId, 3);

        Set<SkillMastery> diagnosticSkills = new HashSet<>();
        diagnosticSkills.addAll(weakSkills);
        diagnosticSkills.addAll(strugglingSkills);

        return diagnosticSkills.stream()
                .map(skill -> buildRecommendation(skill, "DIAGNOSTIC", 1))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdaptiveRecommendationResponse> getRemedialContent(Long studentId, String skillKey, Long ownerId) {
        log.info("Getting remedial content for student: {}, skill: {}", studentId, skillKey);

        Optional<SkillMastery> masteryOpt = masteryRepository.findByStudentIdAndSkillKeyAndIsDeletedFalse(studentId, skillKey);

        if (masteryOpt.isEmpty()) {
            return Collections.emptyList();
        }

        SkillMastery mastery = masteryOpt.get();
        AdaptiveRecommendationResponse recommendation = buildRecommendation(mastery, "REMEDIAL", 1);

        return Collections.singletonList(recommendation);
    }

    @Override
    public List<SkillMasteryResponse> getStudentMastery(Long studentId, Long subjectId, Long ownerId) {
        List<SkillMastery> masteries;
        
        if (subjectId != null) {
            masteries = masteryRepository.findByStudentIdAndSubjectIdAndIsDeletedFalseOrderByMasteryLevelDesc(studentId, subjectId);
        } else {
            masteries = masteryRepository.findByStudentIdAndIsDeletedFalseOrderByMasteryLevelDesc(studentId);
        }

        return masteries.stream()
                .map(this::mapToSkillMasteryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getMasteryHeatmap(Long subjectId, Long ownerId) {
        // Placeholder implementation
        Map<String, Object> heatmap = new HashMap<>();
        heatmap.put("message", "Mastery heatmap feature coming soon");
        return heatmap;
    }

    @Override
    public Map<String, Object> getVelocityTrends(Long studentId, Long ownerId) {
        Object[] velocityStats = masteryRepository.getVelocityStatistics(studentId);
        
        Map<String, Object> trends = new HashMap<>();
        if (velocityStats != null && velocityStats.length >= 3) {
            trends.put("avgVelocity", velocityStats[0]);
            trends.put("maxVelocity", velocityStats[1]);
            trends.put("minVelocity", velocityStats[2]);
        }
        
        return trends;
    }

    @Override
    public Map<String, Object> getPrerequisiteBottlenecks(Long subjectId, Long ownerId) {
        List<String> bottlenecks = prerequisiteService.findPrerequisiteBottlenecks(subjectId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("bottleneckSkills", bottlenecks);
        result.put("count", bottlenecks.size());
        
        return result;
    }

    @Override
    public Map<String, Object> getAdaptiveLearningStatistics(Long studentId, Long ownerId) {
        Map<String, Object> stats = new HashMap<>();

        // Get mastery statistics
        Double avgMastery = masteryRepository.getAverageMasteryByStudent(studentId);
        Object[] masteryLevels = masteryRepository.countSkillsByMasteryLevel(studentId);

        stats.put("avgMastery", avgMastery != null ? avgMastery : 0.0);
        
        if (masteryLevels != null && masteryLevels.length >= 3) {
            stats.put("highMasterySkills", masteryLevels[0]);
            stats.put("mediumMasterySkills", masteryLevels[1]);
            stats.put("lowMasterySkills", masteryLevels[2]);
        }

        // Get interaction statistics
        long totalInteractions = interactionRepository.countByStudentIdAndIsDeletedFalse(studentId);
        stats.put("totalInteractions", totalInteractions);

        return stats;
    }

    @Override
    public SkillMasteryResponse adjustMastery(Long studentId, String skillKey, Double newMasteryLevel, 
                                              String reason, Long ownerId) {
        log.info("Manually adjusting mastery for student: {}, skill: {}, new level: {}", 
                studentId, skillKey, newMasteryLevel);

        SkillMastery mastery = masteryRepository.findByStudentIdAndSkillKeyAndIsDeletedFalse(studentId, skillKey)
                .orElseThrow(() -> new ResourceNotFoundException("SkillMastery", "skillKey", skillKey));

        mastery.setMasteryLevel(Math.max(0.0, Math.min(100.0, newMasteryLevel)));
        masteryRepository.save(mastery);

        return mapToSkillMasteryResponse(mastery);
    }

    @Override
    public void resetSkillMastery(Long studentId, String skillKey, Long ownerId) {
        log.info("Resetting mastery for student: {}, skill: {}", studentId, skillKey);

        SkillMastery mastery = masteryRepository.findByStudentIdAndSkillKeyAndIsDeletedFalse(studentId, skillKey)
                .orElseThrow(() -> new ResourceNotFoundException("SkillMastery", "skillKey", skillKey));

        mastery.setMasteryLevel(0.0);
        mastery.setTotalAttempts(0);
        mastery.setCorrectAttempts(0);
        mastery.setConsecutiveCorrect(0);
        mastery.setConsecutiveIncorrect(0);
        mastery.setTimeSpentMinutes(0);
        mastery.setHintsUsedCount(0);

        masteryRepository.save(mastery);
    }

    @Override
    public List<SkillMasteryResponse> getSkillsNeedingAttention(Long studentId, Long ownerId) {
        List<SkillMastery> lowMasterySkills = masteryRepository.findLowMasterySkills(studentId, 50.0);
        List<SkillMastery> strugglingSkills = masteryRepository.findSkillsWithStruggles(studentId, 3);

        Set<SkillMastery> needsAttention = new HashSet<>();
        needsAttention.addAll(lowMasterySkills);
        needsAttention.addAll(strugglingSkills);

        return needsAttention.stream()
                .map(this::mapToSkillMasteryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillMasteryResponse> getMasteredSkills(Long studentId, Long ownerId) {
        List<SkillMastery> masteredSkills = masteryRepository.findHighMasterySkills(studentId, 80.0);

        return masteredSkills.stream()
                .map(this::mapToSkillMasteryResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private AdaptiveRecommendationResponse buildRecommendation(SkillMastery skill, String type, int priority) {
        return AdaptiveRecommendationResponse.builder()
                .skillKey(skill.getSkillKey())
                .skillName(skill.getSkillName())
                .difficulty(skill.getRecommendedDifficulty().name())
                .currentMastery(skill.getMasteryLevel())
                .targetMastery(type.equals("REMEDIAL") ? 60.0 : 80.0)
                .rationale(buildRationale(skill, type))
                .recommendationType(type)
                .priority(priority)
                .isBlocked(false)
                .build();
    }

    private String buildRationale(SkillMastery skill, String type) {
        switch (type) {
            case "REMEDIAL":
                return String.format("Low mastery (%.1f%%) - needs improvement", skill.getMasteryLevel());
            case "REVIEW":
                return "Scheduled for review to maintain mastery";
            case "DIAGNOSTIC":
                return String.format("Struggling with %d consecutive incorrect attempts", skill.getConsecutiveIncorrect());
            case "NEXT_MODULE":
                return String.format("Ready for next level (current mastery: %.1f%%)", skill.getMasteryLevel());
            default:
                return "Recommended for learning";
        }
    }

    private SkillMasteryResponse mapToSkillMasteryResponse(SkillMastery mastery) {
        if (mastery == null) return null;

        return SkillMasteryResponse.builder()
                .id(mastery.getId())
                .studentId(mastery.getStudent().getId())
                .studentName(mastery.getStudent().getName())
                .subjectId(mastery.getSubject().getId())
                .subjectName(mastery.getSubject().getSubjectName())
                .skillKey(mastery.getSkillKey())
                .skillName(mastery.getSkillName())
                .masteryLevel(mastery.getMasteryLevel())
                .avgAccuracy(mastery.getAvgAccuracy())
                .totalAttempts(mastery.getTotalAttempts())
                .correctAttempts(mastery.getCorrectAttempts())
                .lastPracticedAt(mastery.getLastPracticedAt())
                .nextReviewAt(mastery.getNextReviewAt())
                .lastDifficulty(mastery.getLastDifficulty() != null ? mastery.getLastDifficulty().name() : null)
                .velocityScore(mastery.getVelocityScore())
                .consecutiveCorrect(mastery.getConsecutiveCorrect())
                .consecutiveIncorrect(mastery.getConsecutiveIncorrect())
                .timeSpentMinutes(mastery.getTimeSpentMinutes())
                .hintsUsedCount(mastery.getHintsUsedCount())
                .masteryCategory(mastery.getMasteryCategory())
                .recommendedDifficulty(mastery.getRecommendedDifficulty().name())
                .needsReview(mastery.needsReview())
                .isHighMastery(mastery.isHighMastery())
                .isMediumMastery(mastery.isMediumMastery())
                .isLowMastery(mastery.isLowMastery())
                .formattedTimeSpent(formatTime(mastery.getTimeSpentMinutes()))
                .daysUntilReview(mastery.getNextReviewAt() != null ? 
                        (int) ChronoUnit.DAYS.between(LocalDateTime.now(), mastery.getNextReviewAt()) : null)
                .daysSinceLastPractice(mastery.getLastPracticedAt() != null ? 
                        (int) ChronoUnit.DAYS.between(mastery.getLastPracticedAt(), LocalDateTime.now()) : null)
                .build();
    }

    private String formatTime(Integer minutes) {
        if (minutes == null || minutes == 0) return "0 min";
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0) {
            return hours + "h " + mins + "m";
        }
        return mins + "m";
    }
}
