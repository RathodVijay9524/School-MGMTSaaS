package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.AdaptiveRecommendationResponse;
import com.vijay.User_Master.entity.SkillMastery;
import com.vijay.User_Master.entity.SkillPrerequisite;
import com.vijay.User_Master.repository.SkillMasteryRepository;
import com.vijay.User_Master.repository.SkillPrerequisiteRepository;
import com.vijay.User_Master.service.PrerequisiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Prerequisite Service
 */
@Service
@Slf4j
public class PrerequisiteServiceImpl implements PrerequisiteService {

    @Autowired
    private SkillPrerequisiteRepository prerequisiteRepository;

    @Autowired
    private SkillMasteryRepository masteryRepository;

    @Override
    public boolean checkPrerequisites(Long studentId, String skillKey) {
        log.info("Checking prerequisites for student: {}, skill: {}", studentId, skillKey);

        List<SkillPrerequisite> prerequisites = prerequisiteRepository.findBySkillKeyAndIsActiveTrueAndIsDeletedFalse(skillKey);

        if (prerequisites.isEmpty()) {
            return true; // No prerequisites
        }

        for (SkillPrerequisite prereq : prerequisites) {
            if (prereq.getIsStrict()) {
                boolean isMet = isPrerequisiteMet(studentId, prereq.getPrerequisiteSkillKey(), prereq.getMinimumMasteryRequired());
                if (!isMet) {
                    log.info("Strict prerequisite not met: {}", prereq.getPrerequisiteSkillKey());
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public List<String> getPrerequisiteChain(String skillKey) {
        List<String> chain = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        
        buildPrerequisiteChain(skillKey, chain, visited);
        
        return chain;
    }

    private void buildPrerequisiteChain(String skillKey, List<String> chain, Set<String> visited) {
        if (visited.contains(skillKey)) {
            return; // Avoid circular dependencies
        }
        
        visited.add(skillKey);
        
        List<SkillPrerequisite> prerequisites = prerequisiteRepository.findBySkillKeyAndIsActiveTrueAndIsDeletedFalse(skillKey);
        
        for (SkillPrerequisite prereq : prerequisites) {
            String prereqKey = prereq.getPrerequisiteSkillKey();
            if (!chain.contains(prereqKey)) {
                chain.add(prereqKey);
                buildPrerequisiteChain(prereqKey, chain, visited);
            }
        }
    }

    @Override
    public List<AdaptiveRecommendationResponse.PrerequisiteBlockInfo> getBlockingSkills(Long studentId, String skillKey) {
        List<AdaptiveRecommendationResponse.PrerequisiteBlockInfo> blockingSkills = new ArrayList<>();

        List<SkillPrerequisite> prerequisites = prerequisiteRepository.findBySkillKeyAndIsActiveTrueAndIsDeletedFalse(skillKey);

        for (SkillPrerequisite prereq : prerequisites) {
            Optional<SkillMastery> masteryOpt = masteryRepository.findByStudentIdAndSkillKeyAndIsDeletedFalse(
                    studentId, prereq.getPrerequisiteSkillKey());

            double currentMastery = masteryOpt.map(SkillMastery::getMasteryLevel).orElse(0.0);
            double requiredMastery = prereq.getMinimumMasteryRequired();

            if (currentMastery < requiredMastery) {
                AdaptiveRecommendationResponse.PrerequisiteBlockInfo blockInfo = 
                    AdaptiveRecommendationResponse.PrerequisiteBlockInfo.builder()
                        .skillKey(prereq.getPrerequisiteSkillKey())
                        .skillName(prereq.getPrerequisiteSkillName())
                        .currentMastery(currentMastery)
                        .requiredMastery(requiredMastery)
                        .gap(requiredMastery - currentMastery)
                        .build();
                
                blockingSkills.add(blockInfo);
            }
        }

        return blockingSkills;
    }

    @Override
    public boolean isPrerequisiteMet(Long studentId, String prerequisiteSkillKey, Double requiredMastery) {
        Optional<SkillMastery> masteryOpt = masteryRepository.findByStudentIdAndSkillKeyAndIsDeletedFalse(
                studentId, prerequisiteSkillKey);

        if (masteryOpt.isEmpty()) {
            return false; // No mastery record means not met
        }

        return masteryOpt.get().getMasteryLevel() >= requiredMastery;
    }

    @Override
    public Map<String, List<String>> getBlockedSkillsMap(Long studentId, Long subjectId) {
        Map<String, List<String>> blockedSkillsMap = new HashMap<>();

        List<SkillMastery> studentMasteries = masteryRepository.findByStudentIdAndSubjectIdAndIsDeletedFalseOrderByMasteryLevelDesc(
                studentId, subjectId);

        List<SkillPrerequisite> allPrerequisites = prerequisiteRepository.findBySubjectIdAndIsActiveTrueAndIsDeletedFalse(subjectId);

        for (SkillPrerequisite prereq : allPrerequisites) {
            boolean isMet = isPrerequisiteMet(studentId, prereq.getPrerequisiteSkillKey(), prereq.getMinimumMasteryRequired());
            
            if (!isMet) {
                blockedSkillsMap.computeIfAbsent(prereq.getPrerequisiteSkillKey(), k -> new ArrayList<>())
                        .add(prereq.getSkillKey());
            }
        }

        return blockedSkillsMap;
    }

    @Override
    public List<String> findPrerequisiteBottlenecks(Long subjectId) {
        List<Object[]> skillsWithMostPrereqs = prerequisiteRepository.findSkillsWithMostPrerequisites();
        
        return skillsWithMostPrereqs.stream()
                .map(row -> (String) row[0])
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculatePrerequisiteCompletion(Long studentId, String skillKey) {
        List<SkillPrerequisite> prerequisites = prerequisiteRepository.findBySkillKeyAndIsActiveTrueAndIsDeletedFalse(skillKey);

        if (prerequisites.isEmpty()) {
            return 100.0; // No prerequisites = 100% complete
        }

        int metCount = 0;
        for (SkillPrerequisite prereq : prerequisites) {
            if (isPrerequisiteMet(studentId, prereq.getPrerequisiteSkillKey(), prereq.getMinimumMasteryRequired())) {
                metCount++;
            }
        }

        return (double) metCount / prerequisites.size() * 100.0;
    }

    @Override
    public List<String> getRecommendedLearningOrder(Long subjectId) {
        // Topological sort of skills based on prerequisites
        List<SkillPrerequisite> allPrerequisites = prerequisiteRepository.findBySubjectIdAndIsActiveTrueAndIsDeletedFalse(subjectId);

        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        Set<String> allSkills = new HashSet<>();

        // Build graph
        for (SkillPrerequisite prereq : allPrerequisites) {
            allSkills.add(prereq.getSkillKey());
            allSkills.add(prereq.getPrerequisiteSkillKey());

            graph.computeIfAbsent(prereq.getPrerequisiteSkillKey(), k -> new ArrayList<>())
                    .add(prereq.getSkillKey());

            inDegree.put(prereq.getSkillKey(), inDegree.getOrDefault(prereq.getSkillKey(), 0) + 1);
            inDegree.putIfAbsent(prereq.getPrerequisiteSkillKey(), 0);
        }

        // Topological sort using Kahn's algorithm
        Queue<String> queue = new LinkedList<>();
        List<String> result = new ArrayList<>();

        for (String skill : allSkills) {
            if (inDegree.getOrDefault(skill, 0) == 0) {
                queue.offer(skill);
            }
        }

        while (!queue.isEmpty()) {
            String skill = queue.poll();
            result.add(skill);

            if (graph.containsKey(skill)) {
                for (String dependent : graph.get(skill)) {
                    inDegree.put(dependent, inDegree.get(dependent) - 1);
                    if (inDegree.get(dependent) == 0) {
                        queue.offer(dependent);
                    }
                }
            }
        }

        return result;
    }
}
