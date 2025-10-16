package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Academic Tutoring System
 */
public interface AcademicTutoringService {

    /**
     * Create a new tutoring session
     */
    TutoringSessionResponse createTutoringSession(TutoringSessionRequest request, Long ownerId);

    /**
     * Get tutoring session by ID
     */
    TutoringSessionResponse getTutoringSessionById(Long id, Long ownerId);

    /**
     * Get all tutoring sessions with pagination
     */
    Page<TutoringSessionResponse> getAllTutoringSessions(Long ownerId, Pageable pageable);

    /**
     * Get tutoring sessions by student
     */
    Page<TutoringSessionResponse> getTutoringSessionsByStudent(Long studentId, Long ownerId, Pageable pageable);

    /**
     * Get tutoring sessions by subject
     */
    Page<TutoringSessionResponse> getTutoringSessionsBySubject(Long ownerId, String subject, Pageable pageable);

    /**
     * Get tutoring sessions by grade level
     */
    Page<TutoringSessionResponse> getTutoringSessionsByGradeLevel(Long ownerId, String gradeLevel, Pageable pageable);

    /**
     * Get tutoring sessions by topic
     */
    Page<TutoringSessionResponse> getTutoringSessionsByTopic(Long ownerId, String topic, Pageable pageable);

    /**
     * Search tutoring sessions by keyword
     */
    Page<TutoringSessionResponse> searchTutoringSessions(Long ownerId, String keyword, Pageable pageable);

    /**
     * Get tutoring statistics
     */
    Map<String, Object> getTutoringStatistics(Long ownerId);

    /**
     * Get subject-wise statistics
     */
    List<Map<String, Object>> getSubjectWiseStatistics(Long ownerId);

    /**
     * Get grade-wise statistics
     */
    List<Map<String, Object>> getGradeWiseStatistics(Long ownerId);

    /**
     * Get sessions requiring follow-up
     */
    List<TutoringSessionResponse> getSessionsRequiringFollowUp(Long ownerId);

    /**
     * Get sessions requiring teacher review
     */
    List<TutoringSessionResponse> getSessionsRequiringTeacherReview(Long ownerId);

    /**
     * Get recent tutoring sessions
     */
    List<TutoringSessionResponse> getRecentTutoringSessions(Long ownerId, int days);

    /**
     * Update tutoring session
     */
    TutoringSessionResponse updateTutoringSession(Long id, TutoringSessionRequest request, Long ownerId);

    /**
     * Delete tutoring session
     */
    void deleteTutoringSession(Long id, Long ownerId);

    /**
     * Create learning path
     */
    LearningPathResponse createLearningPath(LearningPathRequest request, Long ownerId);

    /**
     * Get learning path by ID
     */
    LearningPathResponse getLearningPathById(Long id, Long ownerId);

    /**
     * Get learning paths by student
     */
    List<LearningPathResponse> getLearningPathsByStudent(Long studentId, Long ownerId);

    /**
     * Get learning paths by subject
     */
    List<LearningPathResponse> getLearningPathsBySubject(Long ownerId, String subject);

    /**
     * Update learning path progress
     */
    LearningPathResponse updateLearningPathProgress(Long id, Double progressPercentage, Long ownerId);

    /**
     * Complete learning path
     */
    LearningPathResponse completeLearningPath(Long id, Long ownerId);

    /**
     * Get personalized learning recommendations
     */
    List<LearningPathResponse> getPersonalizedRecommendations(Long studentId, Long ownerId);

    /**
     * Generate practice problems
     */
    String generatePracticeProblems(String subject, String topic, String gradeLevel, int count);

    /**
     * Explain concept
     */
    String explainConcept(String subject, String concept, String gradeLevel);

    /**
     * Provide step-by-step solution
     */
    String provideStepByStepSolution(String subject, String problem, String gradeLevel);

    /**
     * Analyze student performance
     */
    Map<String, Object> analyzeStudentPerformance(Long studentId, Long ownerId);

    /**
     * Get learning insights
     */
    Map<String, Object> getLearningInsights(Long studentId, Long ownerId);

    /**
     * Create tutoring session with AI
     */
    TutoringSessionResponse createTutoringSessionWithAI(TutoringSessionAIRequest request, Long ownerId);
}
