package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AIGradingRequest;
import com.vijay.User_Master.dto.AIGradingResponse;

import java.util.List;
import java.util.Map;

/**
 * Service for AI-powered grading of assignments
 */
public interface AIGradingService {

    /**
     * Grade a submission using AI
     */
    AIGradingResponse gradeSubmission(AIGradingRequest request, Long ownerId);

    /**
     * Get AI grading result by submission ID
     */
    AIGradingResponse getGradingResultBySubmission(Long submissionId, Long ownerId);

    /**
     * Get all grading results pending teacher review
     */
    List<AIGradingResponse> getPendingTeacherReview(Long ownerId);

    /**
     * Approve AI grading result
     */
    AIGradingResponse approveGrading(Long gradingResultId, Long teacherId, Long ownerId);

    /**
     * Reject and modify AI grading result
     */
    AIGradingResponse modifyGrading(Long gradingResultId, Double newScore, String newFeedback, Long teacherId, Long ownerId);

    /**
     * Get high plagiarism cases
     */
    List<AIGradingResponse> getHighPlagiarismCases(Double threshold, Long ownerId);

    /**
     * Get low confidence gradings
     */
    List<AIGradingResponse> getLowConfidenceGradings(Double threshold, Long ownerId);

    /**
     * Get AI grading statistics
     */
    Map<String, Object> getGradingStatistics(Long ownerId);

    /**
     * Regrade a submission
     */
    AIGradingResponse regradeSubmission(Long submissionId, AIGradingRequest request, Long ownerId);

    /**
     * Batch grade multiple submissions
     */
    List<AIGradingResponse> batchGradeSubmissions(List<Long> submissionIds, Long rubricId, Long ownerId);
}
