package com.vijay.User_Master.service;

import java.util.List;
import java.util.Map;

/**
 * Service for plagiarism detection
 */
public interface PlagiarismDetectionService {

    /**
     * Check text for plagiarism
     */
    Map<String, Object> checkPlagiarism(String text, Long ownerId);

    /**
     * Calculate similarity between two texts
     */
    Double calculateSimilarity(String text1, String text2);

    /**
     * Find similar submissions in database
     */
    List<Map<String, Object>> findSimilarSubmissions(String text, Long assignmentId, Long ownerId);

    /**
     * Generate plagiarism report
     */
    Map<String, Object> generatePlagiarismReport(String text, Long ownerId);
}
