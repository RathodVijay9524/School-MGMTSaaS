package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for AI grading response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIGradingResponse {

    private Long id;
    private Long submissionId;
    private Long rubricId;
    
    // Scores
    private Double aiSuggestedScore;
    private Double aiConfidenceScore;
    private Double totalPossibleScore;
    private Double aiPercentage;
    private String aiLetterGrade;
    
    // Criterion scores
    private Map<String, Double> criterionScores;
    
    // Feedback
    private String aiFeedback;
    private String strengths;
    private String areasForImprovement;
    private String detailedComments;
    
    // Grammar analysis
    private Double grammarScore;
    private Integer grammarIssuesCount;
    private Integer spellingErrorsCount;
    private Double readabilityScore;
    private Integer wordCount;
    private Integer sentenceCount;
    private Double avgSentenceLength;
    
    // Plagiarism
    private Double plagiarismScore;
    private Boolean plagiarismDetected;
    private Double similarityPercentage;
    
    // AI info
    private String aiModelUsed;
    private String aiProvider;
    private Integer tokensUsed;
    private Long processingTimeMs;
    
    // Status
    private String gradingStatus;
    private Boolean teacherReviewed;
    private Boolean teacherApproved;
    private String gradingQuality;
    
    // Teacher review
    private Double finalScore;
    private String finalFeedback;
    private LocalDateTime reviewedAt;
    
    private LocalDateTime createdAt;
}
