package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Question Bank management
 */
public interface QuestionBankService {

    /**
     * Create a new question
     */
    QuestionResponse createQuestion(QuestionRequest request, Long ownerId);

    /**
     * Update an existing question
     */
    QuestionResponse updateQuestion(Long id, QuestionRequest request, Long ownerId);

    /**
     * Get question by ID
     */
    QuestionResponse getQuestionById(Long id, Long ownerId);

    /**
     * Delete question (soft delete)
     */
    void deleteQuestion(Long id, Long ownerId);

    /**
     * Get all questions for owner
     */
    List<QuestionResponse> getAllQuestions(Long ownerId);

    /**
     * Get questions with pagination
     */
    Page<QuestionResponse> getQuestionsPaginated(Long ownerId, Pageable pageable);

    /**
     * Get questions by subject
     */
    List<QuestionResponse> getQuestionsBySubject(Long subjectId, Long ownerId);

    /**
     * Get questions by class
     */
    List<QuestionResponse> getQuestionsByClass(Long classId, Long ownerId);

    /**
     * Get questions by type
     */
    List<QuestionResponse> getQuestionsByType(QuestionType questionType, Long ownerId);

    /**
     * Get questions by difficulty
     */
    List<QuestionResponse> getQuestionsByDifficulty(DifficultyLevel difficulty, Long ownerId);

    /**
     * Search questions by keyword
     */
    List<QuestionResponse> searchQuestions(String keyword, Long ownerId);

    /**
     * Advanced search with multiple criteria
     */
    Page<QuestionResponse> advancedSearch(QuestionSearchRequest request, Long ownerId);

    /**
     * Get questions by tags
     */
    List<QuestionResponse> getQuestionsByTags(List<Long> tagIds, Long ownerId);

    /**
     * Duplicate a question
     */
    QuestionResponse duplicateQuestion(QuestionDuplicateRequest request, Long ownerId);

    /**
     * Bulk import questions
     */
    BulkQuestionImportResponse bulkImportQuestions(BulkQuestionImportRequest request, Long ownerId);

    /**
     * Get question bank statistics
     */
    QuestionBankStatisticsResponse getStatistics(Long ownerId);

    /**
     * Create question tag
     */
    QuestionResponse.QuestionTagResponse createTag(QuestionTagRequest request, Long ownerId);

    /**
     * Get all tags
     */
    List<QuestionResponse.QuestionTagResponse> getAllTags(Long ownerId);

    /**
     * Delete tag
     */
    void deleteTag(Long tagId, Long ownerId);
}
