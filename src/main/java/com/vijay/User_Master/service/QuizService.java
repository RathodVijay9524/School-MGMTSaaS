package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Quiz management
 */
public interface QuizService {

    /**
     * Create a new quiz
     */
    QuizResponse createQuiz(QuizRequest request, Long ownerId);

    /**
     * Update an existing quiz
     */
    QuizResponse updateQuiz(Long id, QuizRequest request, Long ownerId);

    /**
     * Get quiz by ID
     */
    QuizResponse getQuizById(Long id, Long ownerId);

    /**
     * Delete quiz (soft delete)
     */
    void deleteQuiz(Long id, Long ownerId);

    /**
     * Get all quizzes for owner
     */
    List<QuizResponse> getAllQuizzes(Long ownerId);

    /**
     * Get quizzes with pagination
     */
    Page<QuizResponse> getQuizzesPaginated(Long ownerId, Pageable pageable);

    /**
     * Get quizzes by subject
     */
    List<QuizResponse> getQuizzesBySubject(Long subjectId, Long ownerId);

    /**
     * Get quizzes by class
     */
    List<QuizResponse> getQuizzesByClass(Long classId, Long ownerId);

    /**
     * Get available quizzes for a student
     */
    List<QuizResponse> getAvailableQuizzes(Long studentId, Long ownerId);

    /**
     * Publish/unpublish a quiz
     */
    QuizResponse publishQuiz(QuizPublishRequest request, Long ownerId);

    /**
     * Clone a quiz
     */
    QuizResponse cloneQuiz(QuizCloneRequest request, Long ownerId);

    /**
     * Start a quiz attempt
     */
    QuizAttemptResponse startQuizAttempt(Long quizId, Long studentId, Long ownerId);

    /**
     * Submit quiz attempt
     */
    QuizAttemptResponse submitQuizAttempt(QuizAttemptRequest request, Long studentId, Long ownerId);

    /**
     * Get quiz attempt by ID
     */
    QuizAttemptResponse getQuizAttempt(Long attemptId, Long ownerId);

    /**
     * Get all attempts for a quiz
     */
    List<QuizAttemptResponse> getQuizAttempts(Long quizId, Long ownerId);

    /**
     * Get student's attempts for a quiz
     */
    List<QuizAttemptResponse> getStudentAttempts(Long quizId, Long studentId, Long ownerId);

    /**
     * Get quiz review (after submission)
     */
    QuizReviewResponse getQuizReview(Long attemptId, Long studentId, Long ownerId);

    /**
     * Get quiz statistics
     */
    QuizStatisticsResponse getQuizStatistics(Long quizId, Long ownerId);

    /**
     * Get student quiz summary/dashboard
     */
    StudentQuizSummaryResponse getStudentSummary(Long studentId, Long ownerId);

    /**
     * Get teacher quiz dashboard
     */
    TeacherQuizDashboardResponse getTeacherDashboard(Long teacherId, Long ownerId);

    /**
     * Manual grading of a response
     */
    void manualGradeResponse(ManualGradingRequest request, Long teacherId, Long ownerId);

    /**
     * Batch grading
     */
    BatchGradingResponse batchGradeAttempts(BatchGradingRequest request, Long teacherId, Long ownerId);
}
