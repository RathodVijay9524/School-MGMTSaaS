package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.AcademicTutoringService;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Academic Tutoring Service
 */
@Service
@Slf4j
@Transactional
public class AcademicTutoringServiceImpl implements AcademicTutoringService {

    @Autowired
    private TutoringSessionRepository tutoringSessionRepository;

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Autowired
    private LearningModuleRepository learningModuleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    @Transactional
    public TutoringSessionResponse createTutoringSession(TutoringSessionRequest request, Long ownerId) {
        log.info("Creating tutoring session for student: {}, subject: {}", request.getStudentId(), request.getSubject());

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Worker student = workerRepository.findById(request.getStudentId())
                .filter(w -> w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        TutoringSession session = TutoringSession.builder()
                .student(student)
                .subject(request.getSubject())
                .topic(request.getTopic())
                .gradeLevel(request.getGradeLevel())
                .question(request.getQuestion())
                .aiResponse(request.getExplanation() != null ? request.getExplanation() : "")
                .explanation(request.getExplanation())
                .stepByStepSolution(request.getStepByStepSolution())
                .keyConcepts(request.getKeyConcepts())
                .practiceProblems(request.getPracticeProblems())
                .relatedTopics(request.getRelatedTopics())
                .difficultyLevel(parseDifficultyLevel(request.getDifficultyLevel()))
                .learningObjective(request.getLearningObjective())
                .timeSpentMinutes(request.getTimeSpentMinutes())
                .studentSatisfactionRating(request.getStudentSatisfactionRating())
                .comprehensionScore(request.getComprehensionScore())
                .followUpRequired(request.getFollowUpRequired())
                .teacherReviewRequired(request.getTeacherReviewRequired())
                .sessionStatus(parseSessionStatus(request.getSessionStatus()))
                .aiProvider(request.getAiProvider())
                .tokensUsed(request.getTokensUsed())
                .costUsd(request.getCostUsd())
                .owner(owner)
                .build();

        TutoringSession savedSession = tutoringSessionRepository.save(session);
        return mapToTutoringSessionResponse(savedSession);
    }

    @Override
    public TutoringSessionResponse getTutoringSessionById(Long id, Long ownerId) {
        TutoringSession session = tutoringSessionRepository.findById(id)
                .filter(s -> s.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("TutoringSession", "id", id));

        return mapToTutoringSessionResponse(session);
    }

    @Override
    public Page<TutoringSessionResponse> getAllTutoringSessions(Long ownerId, Pageable pageable) {
        Page<TutoringSession> sessions = tutoringSessionRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, pageable);
        
        List<TutoringSessionResponse> responses = sessions.getContent().stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, sessions.getTotalElements());
    }

    @Override
    public Page<TutoringSessionResponse> getTutoringSessionsByStudent(Long studentId, Long ownerId, Pageable pageable) {
        Page<TutoringSession> sessions = tutoringSessionRepository.findByStudentIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, pageable);
        
        List<TutoringSessionResponse> responses = sessions.getContent().stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, sessions.getTotalElements());
    }

    @Override
    public Page<TutoringSessionResponse> getTutoringSessionsBySubject(Long ownerId, String subject, Pageable pageable) {
        Page<TutoringSession> sessions = tutoringSessionRepository.findByOwnerIdAndSubjectAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, subject, pageable);
        
        List<TutoringSessionResponse> responses = sessions.getContent().stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, sessions.getTotalElements());
    }

    @Override
    public Page<TutoringSessionResponse> getTutoringSessionsByGradeLevel(Long ownerId, String gradeLevel, Pageable pageable) {
        Page<TutoringSession> sessions = tutoringSessionRepository.findByOwnerIdAndGradeLevelAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, gradeLevel, pageable);
        
        List<TutoringSessionResponse> responses = sessions.getContent().stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, sessions.getTotalElements());
    }

    @Override
    public Page<TutoringSessionResponse> getTutoringSessionsByTopic(Long ownerId, String topic, Pageable pageable) {
        Page<TutoringSession> sessions = tutoringSessionRepository.findByOwnerIdAndTopicContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, topic, pageable);
        
        List<TutoringSessionResponse> responses = sessions.getContent().stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, sessions.getTotalElements());
    }

    @Override
    public Page<TutoringSessionResponse> searchTutoringSessions(Long ownerId, String keyword, Pageable pageable) {
        Page<TutoringSession> sessions = tutoringSessionRepository.searchSessions(ownerId, keyword, pageable);
        
        List<TutoringSessionResponse> responses = sessions.getContent().stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, sessions.getTotalElements());
    }

    @Override
    public Map<String, Object> getTutoringStatistics(Long ownerId) {
        Object[] stats = tutoringSessionRepository.getTutoringStatistics(ownerId);
        
        Map<String, Object> statistics = new HashMap<>();
        if (stats != null && stats.length >= 6) {
            statistics.put("totalSessions", stats[0]);
            statistics.put("completedSessions", stats[1]);
            statistics.put("avgTimeSpent", stats[2]);
            statistics.put("avgSatisfaction", stats[3]);
            statistics.put("avgComprehension", stats[4]);
            statistics.put("totalCost", stats[5]);
        }
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> getSubjectWiseStatistics(Long ownerId) {
        List<Object[]> results = tutoringSessionRepository.getSubjectWiseStatistics(ownerId);
        
        return results.stream()
                .map(row -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("subject", row[0]);
                    stat.put("sessionCount", row[1]);
                    stat.put("avgSatisfaction", row[2]);
                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getGradeWiseStatistics(Long ownerId) {
        List<Object[]> results = tutoringSessionRepository.getGradeWiseStatistics(ownerId);
        
        return results.stream()
                .map(row -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("gradeLevel", row[0]);
                    stat.put("sessionCount", row[1]);
                    stat.put("avgComprehension", row[2]);
                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TutoringSessionResponse> getSessionsRequiringFollowUp(Long ownerId) {
        List<TutoringSession> sessions = tutoringSessionRepository.findByOwnerIdAndFollowUpRequiredTrueAndIsDeletedFalseOrderByCreatedOnDesc(ownerId);
        
        return sessions.stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TutoringSessionResponse> getSessionsRequiringTeacherReview(Long ownerId) {
        List<TutoringSession> sessions = tutoringSessionRepository.findByOwnerIdAndTeacherReviewRequiredTrueAndIsDeletedFalseOrderByCreatedOnDesc(ownerId);
        
        return sessions.stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TutoringSessionResponse> getRecentTutoringSessions(Long ownerId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<TutoringSession> sessions = tutoringSessionRepository.findRecentSessions(ownerId, since);
        
        return sessions.stream()
                .map(this::mapToTutoringSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TutoringSessionResponse updateTutoringSession(Long id, TutoringSessionRequest request, Long ownerId) {
        TutoringSession session = tutoringSessionRepository.findById(id)
                .filter(s -> s.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("TutoringSession", "id", id));

        session.setSubject(request.getSubject());
        session.setTopic(request.getTopic());
        session.setGradeLevel(request.getGradeLevel());
        session.setQuestion(request.getQuestion());
        session.setAiResponse(request.getExplanation() != null ? request.getExplanation() : "");
        session.setExplanation(request.getExplanation());
        session.setStepByStepSolution(request.getStepByStepSolution());
        session.setKeyConcepts(request.getKeyConcepts());
        session.setPracticeProblems(request.getPracticeProblems());
        session.setRelatedTopics(request.getRelatedTopics());
        session.setDifficultyLevel(parseDifficultyLevel(request.getDifficultyLevel()));
        session.setLearningObjective(request.getLearningObjective());
        session.setTimeSpentMinutes(request.getTimeSpentMinutes());
        session.setStudentSatisfactionRating(request.getStudentSatisfactionRating());
        session.setComprehensionScore(request.getComprehensionScore());
        session.setFollowUpRequired(request.getFollowUpRequired());
        session.setTeacherReviewRequired(request.getTeacherReviewRequired());
        session.setSessionStatus(parseSessionStatus(request.getSessionStatus()));
        session.setAiProvider(request.getAiProvider());
        session.setTokensUsed(request.getTokensUsed());
        session.setCostUsd(request.getCostUsd());

        TutoringSession updatedSession = tutoringSessionRepository.save(session);
        return mapToTutoringSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public void deleteTutoringSession(Long id, Long ownerId) {
        tutoringSessionRepository.softDeleteByIdAndOwnerId(id, ownerId);
    }

    @Override
    @Transactional
    public LearningPathResponse createLearningPath(LearningPathRequest request, Long ownerId) {
        log.info("Creating learning path: {} for subject: {}", request.getPathName(), request.getSubject());

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Worker student = null;
        if (request.getStudentId() != null) {
            student = workerRepository.findById(request.getStudentId())
                    .filter(w -> w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        }

        LearningPath learningPath = LearningPath.builder()
                .pathName(request.getPathName())
                .description(request.getDescription())
                .subject(request.getSubject())
                .gradeLevel(request.getGradeLevel())
                .learningObjectives(request.getLearningObjectives())
                .estimatedDurationHours(request.getEstimatedDurationHours())
                .difficultyLevel(parseLearningPathDifficultyLevel(request.getDifficultyLevel()))
                .prerequisites(request.getPrerequisites())
                .learningOutcomes(request.getLearningOutcomes())
                .assessmentCriteria(request.getAssessmentCriteria())
                .resourcesNeeded(request.getResourcesNeeded())
                .isAdaptive(request.getIsAdaptive())
                .isActive(request.getIsActive())
                .pathOrder(request.getPathOrder())
                .student(student)
                .owner(owner)
                .build();

        LearningPath savedPath = learningPathRepository.save(learningPath);
        return mapToLearningPathResponse(savedPath);
    }

    @Override
    public LearningPathResponse getLearningPathById(Long id, Long ownerId) {
        LearningPath learningPath = learningPathRepository.findById(id)
                .filter(lp -> lp.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath", "id", id));

        return mapToLearningPathResponse(learningPath);
    }

    @Override
    public List<LearningPathResponse> getLearningPathsByStudent(Long studentId, Long ownerId) {
        List<LearningPath> learningPaths = learningPathRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, ownerId);
        
        return learningPaths.stream()
                .map(this::mapToLearningPathResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LearningPathResponse> getLearningPathsBySubject(Long ownerId, String subject) {
        List<LearningPath> learningPaths = learningPathRepository.findByOwnerIdAndSubjectAndIsDeletedFalseOrderByPathNameAsc(ownerId, subject);
        
        return learningPaths.stream()
                .map(this::mapToLearningPathResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LearningPathResponse updateLearningPathProgress(Long id, Double progressPercentage, Long ownerId) {
        LearningPath learningPath = learningPathRepository.findById(id)
                .filter(lp -> lp.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath", "id", id));

        learningPath.setCompletionPercentage(Math.min(100.0, Math.max(0.0, progressPercentage)));
        
        LearningPath updatedPath = learningPathRepository.save(learningPath);
        return mapToLearningPathResponse(updatedPath);
    }

    @Override
    @Transactional
    public LearningPathResponse completeLearningPath(Long id, Long ownerId) {
        LearningPath learningPath = learningPathRepository.findById(id)
                .filter(lp -> lp.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath", "id", id));

        learningPath.setCompletionPercentage(100.0);
        learningPath.setMasteryLevel(100.0);
        
        LearningPath updatedPath = learningPathRepository.save(learningPath);
        return mapToLearningPathResponse(updatedPath);
    }

    @Override
    public List<LearningPathResponse> getPersonalizedRecommendations(Long studentId, Long ownerId) {
        // Get student's performance data
        List<TutoringSession> recentSessions = tutoringSessionRepository.findRecentSessions(ownerId, LocalDateTime.now().minusDays(30));
        
        // Analyze weak subjects/topics
        Map<String, Integer> subjectFrequency = new HashMap<>();
        Map<String, Double> subjectScores = new HashMap<>();
        
        for (TutoringSession session : recentSessions) {
            if (session.getStudent().getId().equals(studentId)) {
                subjectFrequency.put(session.getSubject(), subjectFrequency.getOrDefault(session.getSubject(), 0) + 1);
                if (session.getComprehensionScore() != null) {
                    subjectScores.merge(session.getSubject(), session.getComprehensionScore(), Double::sum);
                }
            }
        }
        
        // Find subjects with low scores
        List<String> weakSubjects = subjectScores.entrySet().stream()
                .filter(entry -> subjectFrequency.get(entry.getKey()) >= 3) // At least 3 sessions
                .filter(entry -> entry.getValue() / subjectFrequency.get(entry.getKey()) < 70.0) // Average score < 70%
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // Get learning paths for weak subjects
        List<LearningPath> recommendations = new ArrayList<>();
        for (String subject : weakSubjects) {
            recommendations.addAll(learningPathRepository.findByOwnerIdAndSubjectAndIsActiveTrueAndIsDeletedFalseOrderByPathNameAsc(ownerId, subject));
        }
        
        return recommendations.stream()
                .map(this::mapToLearningPathResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String generatePracticeProblems(String subject, String topic, String gradeLevel, int count) {
        // This would integrate with AI service to generate practice problems
        // For now, returning a placeholder
        return String.format("Generated %d practice problems for %s - %s (Grade %s)", 
                count, subject, topic, gradeLevel);
    }

    @Override
    public String explainConcept(String subject, String concept, String gradeLevel) {
        // This would integrate with AI service to explain concepts
        // For now, returning a placeholder
        return String.format("Explanation for %s concept '%s' at Grade %s level", 
                subject, concept, gradeLevel);
    }

    @Override
    public String provideStepByStepSolution(String subject, String problem, String gradeLevel) {
        // This would integrate with AI service to provide step-by-step solutions
        // For now, returning a placeholder
        return String.format("Step-by-step solution for %s problem at Grade %s level:\n%s", 
                subject, gradeLevel, problem);
    }

    @Override
    public Map<String, Object> analyzeStudentPerformance(Long studentId, Long ownerId) {
        List<TutoringSession> sessions = tutoringSessionRepository.findByStudentIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, null).getContent();
        
        Map<String, Object> analysis = new HashMap<>();
        
        // Basic statistics
        analysis.put("totalSessions", sessions.size());
        analysis.put("avgComprehension", sessions.stream()
                .filter(s -> s.getComprehensionScore() != null)
                .mapToDouble(TutoringSession::getComprehensionScore)
                .average().orElse(0.0));
        analysis.put("avgSatisfaction", sessions.stream()
                .filter(s -> s.getStudentSatisfactionRating() != null)
                .mapToInt(TutoringSession::getStudentSatisfactionRating)
                .average().orElse(0.0));
        
        // Subject performance
        Map<String, List<Double>> subjectScores = new HashMap<>();
        for (TutoringSession session : sessions) {
            if (session.getComprehensionScore() != null) {
                subjectScores.computeIfAbsent(session.getSubject(), k -> new ArrayList<>()).add(session.getComprehensionScore());
            }
        }
        
        Map<String, Double> subjectAverages = new HashMap<>();
        subjectScores.forEach((subject, scores) -> 
                subjectAverages.put(subject, scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0)));
        
        analysis.put("subjectPerformance", subjectAverages);
        
        return analysis;
    }

    @Override
    public Map<String, Object> getLearningInsights(Long studentId, Long ownerId) {
        Map<String, Object> insights = new HashMap<>();
        
        // Get recent performance
        List<TutoringSessionResponse> recentSessions = getRecentTutoringSessions(ownerId, 30);
        List<TutoringSessionResponse> studentSessions = recentSessions.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .collect(Collectors.toList());
        
        insights.put("recentActivity", studentSessions.size());
        insights.put("improvementAreas", analyzeStudentPerformance(studentId, ownerId));
        insights.put("recommendations", getPersonalizedRecommendations(studentId, ownerId));
        
        return insights;
    }

    @Override
    @Transactional
    public TutoringSessionResponse createTutoringSessionWithAI(TutoringSessionAIRequest request, Long ownerId) {
        log.info("Creating AI-powered tutoring session for student: {}, subject: {}", request.getStudentId(), request.getSubject());

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Worker student = workerRepository.findById(request.getStudentId())
                .filter(w -> w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        // Generate AI response
        String aiResponse = generateAIResponse(request);
        String explanation = generateExplanation(request);
        String stepByStepSolution = request.getExplainStepByStep() ? generateStepByStepSolution(request) : null;
        String practiceProblems = request.getGeneratePracticeProblems() ? generatePracticeProblems(request.getSubject(), request.getTopic(), request.getGradeLevel(), 3) : null;
        String relatedTopics = request.getProvideRelatedTopics() ? generateRelatedTopics(request) : null;

        TutoringSession session = TutoringSession.builder()
                .student(student)
                .subject(request.getSubject())
                .topic(request.getTopic())
                .gradeLevel(request.getGradeLevel())
                .question(request.getQuestion())
                .aiResponse(aiResponse)
                .explanation(explanation)
                .stepByStepSolution(stepByStepSolution)
                .keyConcepts(extractKeyConcepts(request.getQuestion(), request.getSubject()))
                .practiceProblems(practiceProblems)
                .relatedTopics(relatedTopics)
                .difficultyLevel(parseDifficultyLevel(request.getDifficultyLevel()))
                .learningObjective(request.getLearningObjective())
                .aiProvider(request.getAiProvider() != null ? request.getAiProvider() : "GPT-4")
                .sessionStatus(TutoringSession.SessionStatus.COMPLETED)
                .owner(owner)
                .build();

        TutoringSession savedSession = tutoringSessionRepository.save(session);
        return mapToTutoringSessionResponse(savedSession);
    }

    // Helper methods
    private TutoringSession.DifficultyLevel parseDifficultyLevel(String difficultyLevel) {
        if (difficultyLevel == null) return TutoringSession.DifficultyLevel.INTERMEDIATE;
        try {
            return TutoringSession.DifficultyLevel.valueOf(difficultyLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TutoringSession.DifficultyLevel.INTERMEDIATE;
        }
    }

    private TutoringSession.SessionStatus parseSessionStatus(String sessionStatus) {
        if (sessionStatus == null) return TutoringSession.SessionStatus.ACTIVE;
        try {
            return TutoringSession.SessionStatus.valueOf(sessionStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TutoringSession.SessionStatus.ACTIVE;
        }
    }

    private LearningPath.DifficultyLevel parseLearningPathDifficultyLevel(String difficultyLevel) {
        if (difficultyLevel == null) return LearningPath.DifficultyLevel.INTERMEDIATE;
        try {
            return LearningPath.DifficultyLevel.valueOf(difficultyLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LearningPath.DifficultyLevel.INTERMEDIATE;
        }
    }

    private String generateAIResponse(TutoringSessionAIRequest request) {
        // This would integrate with actual AI service
        return String.format("AI Response for %s question: %s", request.getSubject(), request.getQuestion());
    }

    private String generateExplanation(TutoringSessionAIRequest request) {
        // This would integrate with actual AI service
        return String.format("Detailed explanation for %s concept at %s level", request.getTopic(), request.getGradeLevel());
    }

    private String generateStepByStepSolution(TutoringSessionAIRequest request) {
        // This would integrate with actual AI service
        return String.format("Step-by-step solution for %s problem", request.getQuestion());
    }

    private String generateRelatedTopics(TutoringSessionAIRequest request) {
        // This would integrate with actual AI service
        return String.format("Related topics for %s: Topic 1, Topic 2, Topic 3", request.getTopic());
    }

    private String extractKeyConcepts(String question, String subject) {
        // This would use NLP to extract key concepts
        return String.format("Key concepts for %s: Concept 1, Concept 2, Concept 3", subject);
    }

    private TutoringSessionResponse mapToTutoringSessionResponse(TutoringSession session) {
        return TutoringSessionResponse.builder()
                .id(session.getId())
                .studentId(session.getStudent().getId())
                .studentName(session.getStudent().getName())
                .subject(session.getSubject())
                .topic(session.getTopic())
                .gradeLevel(session.getGradeLevel())
                .question(session.getQuestion())
                .aiResponse(session.getAiResponse())
                .explanation(session.getExplanation())
                .stepByStepSolution(session.getStepByStepSolution())
                .keyConcepts(session.getKeyConcepts())
                .practiceProblems(session.getPracticeProblems())
                .relatedTopics(session.getRelatedTopics())
                .difficultyLevel(session.getDifficultyLevel() != null ? session.getDifficultyLevel().name() : null)
                .learningObjective(session.getLearningObjective())
                .timeSpentMinutes(session.getTimeSpentMinutes())
                .formattedTimeSpent(session.getFormattedTimeSpent())
                .studentSatisfactionRating(session.getStudentSatisfactionRating())
                .comprehensionScore(session.getComprehensionScore())
                .followUpRequired(session.getFollowUpRequired())
                .teacherReviewRequired(session.getTeacherReviewRequired())
                .sessionStatus(session.getSessionStatus() != null ? session.getSessionStatus().name() : null)
                .aiProvider(session.getAiProvider())
                .tokensUsed(session.getTokensUsed())
                .costUsd(session.getCostUsd())
                .costPerMinute(session.getCostPerMinute())
                .isCompleted(session.isCompleted())
                .needsFollowUp(session.needsFollowUp())
                .createdOn(session.getCreatedOn() != null ? new java.sql.Date(session.getCreatedOn().getTime()) : null)
                .updatedOn(session.getUpdatedOn() != null ? new java.sql.Date(session.getUpdatedOn().getTime()) : null)
                .build();
    }

    private LearningPathResponse mapToLearningPathResponse(LearningPath learningPath) {
        return LearningPathResponse.builder()
                .id(learningPath.getId())
                .pathName(learningPath.getPathName())
                .description(learningPath.getDescription())
                .subject(learningPath.getSubject())
                .gradeLevel(learningPath.getGradeLevel())
                .learningObjectives(learningPath.getLearningObjectives())
                .estimatedDurationHours(learningPath.getEstimatedDurationHours())
                .formattedDuration(learningPath.getFormattedDuration())
                .difficultyLevel(learningPath.getDifficultyLevel() != null ? learningPath.getDifficultyLevel().name() : null)
                .prerequisites(learningPath.getPrerequisites())
                .learningOutcomes(learningPath.getLearningOutcomes())
                .assessmentCriteria(learningPath.getAssessmentCriteria())
                .resourcesNeeded(learningPath.getResourcesNeeded())
                .isAdaptive(learningPath.getIsAdaptive())
                .isActive(learningPath.getIsActive())
                .pathOrder(learningPath.getPathOrder())
                .completionPercentage(learningPath.getCompletionPercentage())
                .masteryLevel(learningPath.getMasteryLevel())
                .status(learningPath.getStatus())
                .totalModules(learningPath.getTotalModules())
                .completedModules(learningPath.getCompletedModules())
                .remainingModules(learningPath.getRemainingModules())
                .isCompleted(learningPath.isCompleted())
                .studentId(learningPath.getStudent() != null ? learningPath.getStudent().getId() : null)
                .studentName(learningPath.getStudent() != null ? learningPath.getStudent().getName() : null)
                .createdOn(learningPath.getCreatedOn() != null ? new java.sql.Date(learningPath.getCreatedOn().getTime()) : null)
                .updatedOn(learningPath.getUpdatedOn() != null ? new java.sql.Date(learningPath.getUpdatedOn().getTime()) : null)
                .build();
    }

    @Override
    public Map<String, Object> getDashboardAnalytics(Long ownerId) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Get total tutoring sessions
        long totalSessions = tutoringSessionRepository.countByOwnerIdAndIsDeletedFalse(ownerId);
        long completedSessions = tutoringSessionRepository.countByOwnerIdAndSessionStatusAndIsDeletedFalse(ownerId, TutoringSession.SessionStatus.COMPLETED);
        long scheduledSessions = tutoringSessionRepository.countByOwnerIdAndSessionStatusAndIsDeletedFalse(ownerId, TutoringSession.SessionStatus.ACTIVE);
        
        // Get total learning paths
        long totalLearningPaths = learningPathRepository.countByOwnerIdAndIsDeletedFalse(ownerId);
        long activeLearningPaths = learningPathRepository.countByOwnerIdAndIsActiveTrueAndIsDeletedFalse(ownerId);
        long completedLearningPaths = learningPathRepository.countByOwnerIdAndIsCompletedTrueAndIsDeletedFalse(ownerId);
        
        // Get total learning modules
        long totalModules = learningModuleRepository.countByOwnerIdAndIsDeletedFalse(ownerId);
        long completedModules = learningModuleRepository.countByOwnerIdAndIsCompletedTrueAndIsDeletedFalse(ownerId);
        
        // Get average satisfaction rating
        Double avgSatisfaction = tutoringSessionRepository.getAverageSatisfactionRating(ownerId);
        
        // Get average comprehension score
        Double avgComprehension = tutoringSessionRepository.getAverageComprehensionScore(ownerId);
        
        // Get total time spent
        Integer totalTimeSpent = tutoringSessionRepository.getTotalTimeSpent(ownerId);
        
        // Get sessions by subject
        List<Object[]> subjectResults = tutoringSessionRepository.getSessionsBySubject(ownerId);
        Map<String, Long> sessionsBySubject = new HashMap<>();
        for (Object[] result : subjectResults) {
            sessionsBySubject.put((String) result[0], ((Number) result[1]).longValue());
        }
        
        // Get sessions by difficulty level
        List<Object[]> difficultyResults = tutoringSessionRepository.getSessionsByDifficultyLevel(ownerId);
        Map<String, Long> sessionsByDifficulty = new HashMap<>();
        for (Object[] result : difficultyResults) {
            sessionsByDifficulty.put(result[0] != null ? result[0].toString() : "UNKNOWN", ((Number) result[1]).longValue());
        }
        
        // Populate analytics
        analytics.put("totalSessions", totalSessions);
        analytics.put("completedSessions", completedSessions);
        analytics.put("scheduledSessions", scheduledSessions);
        analytics.put("totalLearningPaths", totalLearningPaths);
        analytics.put("activeLearningPaths", activeLearningPaths);
        analytics.put("completedLearningPaths", completedLearningPaths);
        analytics.put("totalModules", totalModules);
        analytics.put("completedModules", completedModules);
        analytics.put("avgSatisfactionRating", avgSatisfaction != null ? avgSatisfaction : 0.0);
        analytics.put("avgComprehensionScore", avgComprehension != null ? avgComprehension : 0.0);
        analytics.put("totalTimeSpentMinutes", totalTimeSpent != null ? totalTimeSpent : 0);
        analytics.put("sessionsBySubject", sessionsBySubject);
        analytics.put("sessionsByDifficulty", sessionsByDifficulty);
        
        // Calculate completion rates
        double sessionCompletionRate = totalSessions > 0 ? (double) completedSessions / totalSessions * 100 : 0.0;
        double learningPathCompletionRate = totalLearningPaths > 0 ? (double) completedLearningPaths / totalLearningPaths * 100 : 0.0;
        double moduleCompletionRate = totalModules > 0 ? (double) completedModules / totalModules * 100 : 0.0;
        
        analytics.put("sessionCompletionRate", Math.round(sessionCompletionRate * 100.0) / 100.0);
        analytics.put("learningPathCompletionRate", Math.round(learningPathCompletionRate * 100.0) / 100.0);
        analytics.put("moduleCompletionRate", Math.round(moduleCompletionRate * 100.0) / 100.0);
        
        return analytics;
    }

    // Learning Module methods
    @Override
    @Transactional
    public LearningModuleResponse createLearningModule(LearningModuleRequest request, Long ownerId) {
        log.info("Creating learning module: {} for owner: {}", request.getModuleName(), ownerId);
        
        // Validate learning path exists and belongs to owner
        LearningPath learningPath = learningPathRepository.findById(request.getLearningPathId())
                .filter(lp -> lp.getOwner().getId().equals(ownerId) && !lp.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath", "id", request.getLearningPathId()));

        // Create learning module
        LearningModule learningModule = LearningModule.builder()
                .moduleName(request.getModuleName())
                .description(request.getDescription())
                .content(request.getContent())
                .learningObjectives(request.getLearningObjectives())
                .moduleType(LearningModule.ModuleType.valueOf(request.getModuleType()))
                .difficultyLevel(LearningModule.DifficultyLevel.valueOf(request.getDifficultyLevel()))
                .orderIndex(request.getOrderIndex())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .prerequisites(request.getPrerequisites())
                .resources(request.getResources())
                .assessmentQuestions(request.getAssessmentQuestions())
                .passingScorePercentage(request.getPassingScorePercentage())
                .instructions(request.getInstructions())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .tags(request.getTags())
                .notes(request.getNotes())
                .learningPath(learningPath)
                .owner(learningPath.getOwner())
                .isCompleted(false)
                .attemptsCount(0)
                .isDeleted(false)
                .build();

        LearningModule savedModule = learningModuleRepository.save(learningModule);
        log.info("Learning module created successfully with ID: {}", savedModule.getId());
        
        return mapToLearningModuleResponse(savedModule);
    }

    @Override
    public LearningModuleResponse getLearningModuleById(Long id, Long ownerId) {
        log.info("Getting learning module by ID: {} for owner: {}", id, ownerId);
        
        LearningModule learningModule = learningModuleRepository.findById(id)
                .filter(lm -> lm.getOwner().getId().equals(ownerId) && !lm.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", id));
        
        return mapToLearningModuleResponse(learningModule);
    }

    @Override
    public List<LearningModuleResponse> getLearningModulesByLearningPath(Long learningPathId, Long ownerId) {
        log.info("Getting learning modules for learning path: {} by owner: {}", learningPathId, ownerId);
        
        List<LearningModule> modules = learningModuleRepository.findByLearningPathIdAndIsDeletedFalseOrderByOrderIndexAsc(learningPathId);
        
        return modules.stream()
                .filter(lm -> lm.getOwner().getId().equals(ownerId))
                .map(this::mapToLearningModuleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LearningModuleResponse updateLearningModule(Long id, LearningModuleRequest request, Long ownerId) {
        log.info("Updating learning module: {} for owner: {}", id, ownerId);
        
        LearningModule learningModule = learningModuleRepository.findById(id)
                .filter(lm -> lm.getOwner().getId().equals(ownerId) && !lm.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", id));

        // Update fields
        learningModule.setModuleName(request.getModuleName());
        learningModule.setDescription(request.getDescription());
        learningModule.setContent(request.getContent());
        learningModule.setLearningObjectives(request.getLearningObjectives());
        learningModule.setModuleType(LearningModule.ModuleType.valueOf(request.getModuleType()));
        learningModule.setDifficultyLevel(LearningModule.DifficultyLevel.valueOf(request.getDifficultyLevel()));
        learningModule.setOrderIndex(request.getOrderIndex());
        learningModule.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        learningModule.setPrerequisites(request.getPrerequisites());
        learningModule.setResources(request.getResources());
        learningModule.setAssessmentQuestions(request.getAssessmentQuestions());
        learningModule.setPassingScorePercentage(request.getPassingScorePercentage());
        learningModule.setInstructions(request.getInstructions());
        if (request.getIsActive() != null) learningModule.setIsActive(request.getIsActive());
        if (request.getIsRequired() != null) learningModule.setIsRequired(request.getIsRequired());
        learningModule.setTags(request.getTags());
        learningModule.setNotes(request.getNotes());

        LearningModule updatedModule = learningModuleRepository.save(learningModule);
        log.info("Learning module updated successfully");
        
        return mapToLearningModuleResponse(updatedModule);
    }

    @Override
    @Transactional
    public void deleteLearningModule(Long id, Long ownerId) {
        log.info("Deleting learning module: {} for owner: {}", id, ownerId);
        
        LearningModule learningModule = learningModuleRepository.findById(id)
                .filter(lm -> lm.getOwner().getId().equals(ownerId) && !lm.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", id));

        learningModule.setIsDeleted(true);
        learningModuleRepository.save(learningModule);
        log.info("Learning module soft deleted successfully");
    }

    @Override
    @Transactional
    public LearningModuleResponse completeLearningModule(Long id, Long studentId, Double scorePercentage, Integer timeSpentMinutes, Long ownerId) {
        log.info("Completing learning module: {} for student: {} by owner: {}", id, studentId, ownerId);
        
        LearningModule learningModule = learningModuleRepository.findById(id)
                .filter(lm -> lm.getOwner().getId().equals(ownerId) && !lm.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("LearningModule", "id", id));

        learningModule.setIsCompleted(true);
        learningModule.setCompletionDate(LocalDateTime.now());
        learningModule.setScorePercentage(scorePercentage);
        if (timeSpentMinutes != null) {
            learningModule.setTimeSpentMinutes(timeSpentMinutes);
        }
        learningModule.setAttemptsCount(learningModule.getAttemptsCount() + 1);

        LearningModule completedModule = learningModuleRepository.save(learningModule);
        log.info("Learning module completed successfully");
        
        return mapToLearningModuleResponse(completedModule);
    }

    @Override
    public Map<String, Object> getLearningModuleStatistics(Long ownerId) {
        log.info("Getting learning module statistics for owner: {}", ownerId);
        
        Object[] stats = learningModuleRepository.getLearningModuleStatistics(ownerId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalModules", stats[0]);
        statistics.put("completedModules", stats[1]);
        statistics.put("activeModules", stats[2]);
        statistics.put("avgScore", stats[3]);
        statistics.put("avgTimeSpent", stats[4]);
        statistics.put("avgAttempts", stats[5]);
        
        return statistics;
    }

    @Override
    public List<LearningModuleResponse> searchLearningModules(String keyword, Long ownerId, Pageable pageable) {
        log.info("Searching learning modules with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<LearningModule> modules = learningModuleRepository.searchLearningModules(ownerId, keyword, pageable);
        
        return modules.stream()
                .map(this::mapToLearningModuleResponse)
                .collect(Collectors.toList());
    }

    private LearningModuleResponse mapToLearningModuleResponse(LearningModule learningModule) {
        return LearningModuleResponse.builder()
                .id(learningModule.getId())
                .moduleName(learningModule.getModuleName())
                .description(learningModule.getDescription())
                .content(learningModule.getContent())
                .learningObjectives(learningModule.getLearningObjectives())
                .moduleType(learningModule.getModuleType() != null ? learningModule.getModuleType().name() : null)
                .difficultyLevel(learningModule.getDifficultyLevel() != null ? learningModule.getDifficultyLevel().name() : null)
                .orderIndex(learningModule.getOrderIndex())
                .estimatedDurationMinutes(learningModule.getEstimatedDurationMinutes())
                .prerequisites(learningModule.getPrerequisites())
                .resources(learningModule.getResources())
                .assessmentQuestions(learningModule.getAssessmentQuestions())
                .passingScorePercentage(learningModule.getPassingScorePercentage())
                .instructions(learningModule.getInstructions())
                .isActive(learningModule.getIsActive())
                .isRequired(learningModule.getIsRequired())
                .tags(learningModule.getTags())
                .notes(learningModule.getNotes())
                .isCompleted(learningModule.getIsCompleted())
                .completionDate(learningModule.getCompletionDate() != null ? java.sql.Date.valueOf(learningModule.getCompletionDate().toLocalDate()) : null)
                .timeSpentMinutes(learningModule.getTimeSpentMinutes())
                .scorePercentage(learningModule.getScorePercentage())
                .attemptsCount(learningModule.getAttemptsCount())
                .learningPathId(learningModule.getLearningPath().getId())
                .learningPathName(learningModule.getLearningPath().getPathName())
                .studentId(learningModule.getStudent() != null ? learningModule.getStudent().getId() : null)
                .studentName(learningModule.getStudent() != null ? learningModule.getStudent().getName() : null)
                .ownerId(learningModule.getOwner().getId())
                .createdOn(learningModule.getCreatedOn() != null ? new java.sql.Date(learningModule.getCreatedOn().getTime()) : null)
                .updatedOn(learningModule.getUpdatedOn() != null ? new java.sql.Date(learningModule.getUpdatedOn().getTime()) : null)
                .build();
    }
}
