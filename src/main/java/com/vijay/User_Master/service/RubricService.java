package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RubricRequest;
import com.vijay.User_Master.dto.RubricResponse;

import java.util.List;

/**
 * Service for rubric management
 */
public interface RubricService {

    /**
     * Create a new rubric
     */
    RubricResponse createRubric(RubricRequest request, Long ownerId);

    /**
     * Update rubric
     */
    RubricResponse updateRubric(Long id, RubricRequest request, Long ownerId);

    /**
     * Get rubric by ID
     */
    RubricResponse getRubricById(Long id, Long ownerId);

    /**
     * Get all rubrics for owner
     */
    List<RubricResponse> getAllRubrics(Long ownerId);

    /**
     * Get rubrics by subject
     */
    List<RubricResponse> getRubricsBySubject(Long subjectId, Long ownerId);

    /**
     * Get rubrics by type
     */
    List<RubricResponse> getRubricsByType(String rubricType, Long ownerId);

    /**
     * Delete rubric
     */
    void deleteRubric(Long id, Long ownerId);

    /**
     * Search rubrics by name
     */
    List<RubricResponse> searchRubrics(String keyword, Long ownerId);
}
