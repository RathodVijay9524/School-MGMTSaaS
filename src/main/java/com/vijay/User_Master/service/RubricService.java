package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RubricRequest;
import com.vijay.User_Master.entity.Rubric;

import java.util.List;

/**
 * Service for rubric management
 */
public interface RubricService {

    /**
     * Create a new rubric
     */
    Rubric createRubric(RubricRequest request, Long ownerId);

    /**
     * Update rubric
     */
    Rubric updateRubric(Long id, RubricRequest request, Long ownerId);

    /**
     * Get rubric by ID
     */
    Rubric getRubricById(Long id, Long ownerId);

    /**
     * Get all rubrics for owner
     */
    List<Rubric> getAllRubrics(Long ownerId);

    /**
     * Get rubrics by subject
     */
    List<Rubric> getRubricsBySubject(Long subjectId, Long ownerId);

    /**
     * Get rubrics by type
     */
    List<Rubric> getRubricsByType(String rubricType, Long ownerId);

    /**
     * Delete rubric
     */
    void deleteRubric(Long id, Long ownerId);

    /**
     * Search rubrics by name
     */
    List<Rubric> searchRubrics(String keyword, Long ownerId);
}
