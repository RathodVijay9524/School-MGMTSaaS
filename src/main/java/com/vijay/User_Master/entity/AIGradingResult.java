package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AIGradingResult entity
 * Stores AI-generated grading results for homework submissions
 */
@Entity
@Table(name = "ai_grading_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AIGradingResult extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private HomeworkSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubric_id")
    private Rubric rubric;

    // AI Grading Scores
    @Column(name = "ai_suggested_score")
    private Double aiSuggestedScore;

    @Column(name = "ai_confidence_score")
    private Double aiConfidenceScore; // 0-100, how confident AI is

    @Column(name = "total_possible_score")
    private Double totalPossibleScore;

    @Column(name = "ai_percentage")
    private Double aiPercentage;

    @Column(name = "ai_letter_grade")
    private String aiLetterGrade;

    // Criterion-wise scores (JSON)
    @Column(name = "criterion_scores_json", columnDefinition = "TEXT")
    private String criterionScoresJson; // JSON: {"content": 8.5, "grammar": 9.0, ...}

    // AI Feedback
    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "areas_for_improvement", columnDefinition = "TEXT")
    private String areasForImprovement;

    @Column(name = "detailed_comments", columnDefinition = "TEXT")
    private String detailedComments;

    // Grammar & Writing Analysis
    @Column(name = "grammar_score")
    private Double grammarScore; // 0-100

    @Column(name = "grammar_issues_count")
    private Integer grammarIssuesCount;

    @Column(name = "grammar_issues_json", columnDefinition = "TEXT")
    private String grammarIssuesJson; // JSON array of issues

    @Column(name = "spelling_errors_count")
    private Integer spellingErrorsCount;

    @Column(name = "readability_score")
    private Double readabilityScore; // Flesch Reading Ease

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "sentence_count")
    private Integer sentenceCount;

    @Column(name = "avg_sentence_length")
    private Double avgSentenceLength;

    // Plagiarism Detection
    @Column(name = "plagiarism_score")
    private Double plagiarismScore; // 0-100, higher = more plagiarism

    @Column(name = "plagiarism_detected")
    @Builder.Default
    private Boolean plagiarismDetected = false;

    @Column(name = "plagiarism_sources_json", columnDefinition = "TEXT")
    private String plagiarismSourcesJson; // JSON array of matched sources

    @Column(name = "similarity_percentage")
    private Double similarityPercentage;

    // AI Model Information
    @Column(name = "ai_model_used")
    private String aiModelUsed; // e.g., "gpt-4", "gpt-3.5-turbo"

    @Column(name = "ai_provider")
    private String aiProvider; // e.g., "OpenAI", "Anthropic"

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "api_cost_usd")
    private Double apiCostUsd;

    // Teacher Review
    @Column(name = "teacher_reviewed")
    @Builder.Default
    private Boolean teacherReviewed = false;

    @Column(name = "teacher_approved")
    private Boolean teacherApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_teacher_id")
    private Worker reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "teacher_modifications", columnDefinition = "TEXT")
    private String teacherModifications; // What teacher changed

    @Column(name = "final_score")
    private Double finalScore; // After teacher review

    @Column(name = "final_feedback", columnDefinition = "TEXT")
    private String finalFeedback; // After teacher review

    // Status
    @Column(name = "grading_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GradingStatus gradingStatus = GradingStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage; // If AI grading failed

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum GradingStatus {
        PENDING, IN_PROGRESS, COMPLETED, TEACHER_REVIEW, APPROVED, REJECTED, FAILED
    }

    // Helper methods
    public boolean needsTeacherReview() {
        return !teacherReviewed && gradingStatus == GradingStatus.COMPLETED;
    }

    public boolean hasHighPlagiarism() {
        return plagiarismScore != null && plagiarismScore > 30.0;
    }

    public boolean hasLowConfidence() {
        return aiConfidenceScore != null && aiConfidenceScore < 70.0;
    }

    public String getGradingQuality() {
        if (aiConfidenceScore == null) return "Unknown";
        if (aiConfidenceScore >= 90) return "High Confidence";
        if (aiConfidenceScore >= 70) return "Medium Confidence";
        return "Low Confidence - Review Recommended";
    }
}
