package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;

import java.util.List;

/**
 * Service interface for Question Pool management
 */
public interface QuestionPoolService {

    /**
     * Create a new question pool
     */
    QuestionPoolResponse createPool(QuestionPoolRequest request, Long ownerId);

    /**
     * Update an existing pool
     */
    QuestionPoolResponse updatePool(Long id, QuestionPoolRequest request, Long ownerId);

    /**
     * Get pool by ID
     */
    QuestionPoolResponse getPoolById(Long id, Long ownerId);

    /**
     * Delete pool (soft delete)
     */
    void deletePool(Long id, Long ownerId);

    /**
     * Get all pools for owner
     */
    List<QuestionPoolResponse> getAllPools(Long ownerId);

    /**
     * Get pools by subject
     */
    List<QuestionPoolResponse> getPoolsBySubject(Long subjectId, Long ownerId);

    /**
     * Generate random questions from pool
     */
    QuestionPoolGenerateResponse generateQuestions(QuestionPoolGenerateRequest request, Long ownerId);

    /**
     * Add questions to pool
     */
    QuestionPoolResponse addQuestionsToPool(Long poolId, List<Long> questionIds, Long ownerId);

    /**
     * Remove questions from pool
     */
    QuestionPoolResponse removeQuestionsFromPool(Long poolId, List<Long> questionIds, Long ownerId);
}
