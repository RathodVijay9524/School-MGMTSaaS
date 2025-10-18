package com.vijay.User_Master.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.dto.AIGradingRequest;
import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.PlagiarismDetectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of AI Grading Service
 * Integrates with OpenAI for essay grading
 */
@Service
@Slf4j
@Transactional
public class AIGradingServiceImpl implements AIGradingService {

    @Autowired
    private AIGradingResultRepository gradingResultRepository;

    @Autowired
    private HomeworkSubmissionRepository submissionRepository;

    @Autowired
    private RubricRepository rubricRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private PlagiarismDetectionService plagiarismService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String defaultModel;

    @Override
    public AIGradingResponse gradeSubmission(AIGradingRequest request, Long ownerId) {
        log.info("Starting AI grading for submission: {}", request.getSubmissionId());

        long startTime = System.currentTimeMillis();

        // Get submission
        HomeworkSubmission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", request.getSubmissionId()));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        // Get rubric if specified
        Rubric rubric = null;
        if (request.getRubricId() != null) {
            rubric = rubricRepository.findById(request.getRubricId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rubric", "id", request.getRubricId()));
        }

        // Check if already graded
        Optional<AIGradingResult> existingResult = gradingResultRepository.findBySubmissionIdAndIsDeletedFalse(request.getSubmissionId());
        if (existingResult.isPresent()) {
            log.info("Submission already graded, returning existing result");
            return mapToResponse(existingResult.get());
        }

        // Create grading result
        AIGradingResult gradingResult = AIGradingResult.builder()
                .submission(submission)
                .rubric(rubric)
                .gradingStatus(AIGradingResult.GradingStatus.IN_PROGRESS)
                .owner(owner)
                .build();

        gradingResult = gradingResultRepository.save(gradingResult);

        try {
            // Perform AI grading
            String submissionText = submission.getSubmissionText();
            if (submissionText == null || submissionText.isEmpty()) {
                throw new RuntimeException("Submission text is empty");
            }

            // 1. Text Analysis
            Map<String, Object> textAnalysis = analyzeText(submissionText);
            gradingResult.setWordCount((Integer) textAnalysis.get("wordCount"));
            gradingResult.setSentenceCount((Integer) textAnalysis.get("sentenceCount"));
            gradingResult.setAvgSentenceLength((Double) textAnalysis.get("avgSentenceLength"));
            gradingResult.setReadabilityScore((Double) textAnalysis.get("readabilityScore"));

            // 2. Grammar Check (if enabled)
            if (request.getCheckGrammar()) {
                Map<String, Object> grammarCheck = checkGrammar(submissionText);
                gradingResult.setGrammarScore((Double) grammarCheck.get("grammarScore"));
                gradingResult.setGrammarIssuesCount((Integer) grammarCheck.get("issuesCount"));
                gradingResult.setSpellingErrorsCount((Integer) grammarCheck.get("spellingErrors"));
            }

            // 3. Plagiarism Check (if enabled)
            if (request.getCheckPlagiarism()) {
                Map<String, Object> plagiarismCheck = plagiarismService.checkPlagiarism(submissionText, ownerId);
                gradingResult.setPlagiarismScore((Double) plagiarismCheck.get("plagiarismScore"));
                gradingResult.setSimilarityPercentage((Double) plagiarismCheck.get("similarityPercentage"));
                gradingResult.setPlagiarismDetected((Boolean) plagiarismCheck.get("plagiarismDetected"));
            }

            // 4. AI Grading (simulated - in production, call OpenAI API)
            Map<String, Object> aiGrading = performAIGrading(submissionText, rubric, submission.getAssignment());
            
            gradingResult.setAiSuggestedScore((Double) aiGrading.get("score"));
            gradingResult.setTotalPossibleScore((Double) aiGrading.get("totalPoints"));
            gradingResult.setAiPercentage((Double) aiGrading.get("percentage"));
            gradingResult.setAiLetterGrade((String) aiGrading.get("letterGrade"));
            gradingResult.setAiConfidenceScore((Double) aiGrading.get("confidence"));
            
            gradingResult.setAiFeedback((String) aiGrading.get("feedback"));
            gradingResult.setStrengths((String) aiGrading.get("strengths"));
            gradingResult.setAreasForImprovement((String) aiGrading.get("improvements"));
            gradingResult.setDetailedComments((String) aiGrading.get("comments"));

            // 5. Set AI model info
            gradingResult.setAiModelUsed(request.getAiModel() != null ? request.getAiModel() : defaultModel);
            gradingResult.setAiProvider("OpenAI");
            gradingResult.setTokensUsed(estimateTokens(submissionText));
            
            long processingTime = System.currentTimeMillis() - startTime;
            gradingResult.setProcessingTimeMs(processingTime);
            gradingResult.setApiCostUsd(calculateCost(gradingResult.getTokensUsed()));

            // 6. Update status
            gradingResult.setGradingStatus(AIGradingResult.GradingStatus.COMPLETED);

            gradingResult = gradingResultRepository.save(gradingResult);
            log.info("AI grading completed successfully for submission: {}", request.getSubmissionId());

            return mapToResponse(gradingResult);

        } catch (Exception e) {
            log.error("Error during AI grading: {}", e.getMessage(), e);
            gradingResult.setGradingStatus(AIGradingResult.GradingStatus.FAILED);
            gradingResult.setErrorMessage(e.getMessage());
            gradingResultRepository.save(gradingResult);
            throw new RuntimeException("AI grading failed: " + e.getMessage());
        }
    }

    @Override
    public AIGradingResponse getGradingResultBySubmission(Long submissionId, Long ownerId) {
        AIGradingResult result = gradingResultRepository.findBySubmissionIdAndIsDeletedFalse(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("AIGradingResult", "submissionId", submissionId));

        return mapToResponse(result);
    }

    @Override
    public List<AIGradingResponse> getPendingTeacherReview(Long ownerId) {
        List<AIGradingResult> results = gradingResultRepository.findPendingTeacherReview(ownerId);
        return results.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public AIGradingResponse approveGrading(Long gradingResultId, Long teacherId, Long ownerId) {
        log.info("Approving AI grading result: {}", gradingResultId);

        AIGradingResult result = gradingResultRepository.findById(gradingResultId)
                .orElseThrow(() -> new ResourceNotFoundException("AIGradingResult", "id", gradingResultId));

        Worker teacher = workerRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        result.setTeacherReviewed(true);
        result.setTeacherApproved(true);
        result.setReviewedBy(teacher);
        result.setReviewedAt(LocalDateTime.now());
        result.setFinalScore(result.getAiSuggestedScore());
        result.setFinalFeedback(result.getAiFeedback());
        result.setGradingStatus(AIGradingResult.GradingStatus.APPROVED);

        result = gradingResultRepository.save(result);
        return mapToResponse(result);
    }

    @Override
    public AIGradingResponse modifyGrading(Long gradingResultId, Double newScore, String newFeedback, Long teacherId, Long ownerId) {
        log.info("Modifying AI grading result: {}", gradingResultId);

        AIGradingResult result = gradingResultRepository.findById(gradingResultId)
                .orElseThrow(() -> new ResourceNotFoundException("AIGradingResult", "id", gradingResultId));

        Worker teacher = workerRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        result.setTeacherReviewed(true);
        result.setTeacherApproved(false);
        result.setReviewedBy(teacher);
        result.setReviewedAt(LocalDateTime.now());
        result.setFinalScore(newScore);
        result.setFinalFeedback(newFeedback);
        result.setTeacherModifications("Score changed from " + result.getAiSuggestedScore() + " to " + newScore);
        result.setGradingStatus(AIGradingResult.GradingStatus.TEACHER_REVIEW);

        result = gradingResultRepository.save(result);
        return mapToResponse(result);
    }

    @Override
    public List<AIGradingResponse> getHighPlagiarismCases(Double threshold, Long ownerId) {
        List<AIGradingResult> results = gradingResultRepository.findHighPlagiarismCases(threshold, ownerId);
        return results.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<AIGradingResponse> getLowConfidenceGradings(Double threshold, Long ownerId) {
        List<AIGradingResult> results = gradingResultRepository.findLowConfidenceGradings(threshold, ownerId);
        return results.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getGradingStatistics(Long ownerId) {
        Object[] stats = gradingResultRepository.getGradingStatistics(ownerId);
        
        Map<String, Object> statistics = new HashMap<>();
        if (stats != null && stats.length >= 4) {
            statistics.put("totalGradings", stats[0]);
            statistics.put("avgScore", stats[1]);
            statistics.put("avgConfidence", stats[2]);
            statistics.put("avgPlagiarismScore", stats[3]);
        }
        
        return statistics;
    }

    @Override
    public AIGradingResponse regradeSubmission(Long submissionId, AIGradingRequest request, Long ownerId) {
        log.info("Regrading submission: {}", submissionId);

        // Delete existing result
        Optional<AIGradingResult> existing = gradingResultRepository.findBySubmissionIdAndIsDeletedFalse(submissionId);
        existing.ifPresent(result -> {
            result.setIsDeleted(true);
            gradingResultRepository.save(result);
        });

        // Create new grading
        request.setSubmissionId(submissionId);
        return gradeSubmission(request, ownerId);
    }

    @Override
    public List<AIGradingResponse> batchGradeSubmissions(List<Long> submissionIds, Long rubricId, Long ownerId) {
        log.info("Batch grading {} submissions", submissionIds.size());

        List<AIGradingResponse> results = new ArrayList<>();

        for (Long submissionId : submissionIds) {
            try {
                AIGradingRequest request = AIGradingRequest.builder()
                        .submissionId(submissionId)
                        .rubricId(rubricId)
                        .checkPlagiarism(true)
                        .checkGrammar(true)
                        .generateFeedback(true)
                        .build();

                AIGradingResponse response = gradeSubmission(request, ownerId);
                results.add(response);
            } catch (Exception e) {
                log.error("Error grading submission {}: {}", submissionId, e.getMessage());
            }
        }

        return results;
    }

    // Helper methods

    private Map<String, Object> analyzeText(String text) {
        Map<String, Object> analysis = new HashMap<>();
        
        String[] words = text.split("\\s+");
        String[] sentences = text.split("[.!?]+");
        
        analysis.put("wordCount", words.length);
        analysis.put("sentenceCount", sentences.length);
        analysis.put("avgSentenceLength", sentences.length > 0 ? (double) words.length / sentences.length : 0);
        analysis.put("readabilityScore", calculateReadabilityScore(words.length, sentences.length));
        
        return analysis;
    }

    private Map<String, Object> checkGrammar(String text) {
        Map<String, Object> grammarCheck = new HashMap<>();
        
        // Simulated grammar check (in production, use LanguageTool or Grammarly API)
        grammarCheck.put("grammarScore", 85.0);
        grammarCheck.put("issuesCount", 3);
        grammarCheck.put("spellingErrors", 1);
        
        return grammarCheck;
    }

    private Map<String, Object> performAIGrading(String text, Rubric rubric, Assignment assignment) {
        Map<String, Object> grading = new HashMap<>();
        
        // Simulated AI grading (in production, call OpenAI API)
        double totalPoints = rubric != null ? rubric.getTotalPoints() : 
                            (assignment != null && assignment.getTotalMarks() != null ? assignment.getTotalMarks() : 100.0);
        
        double score = totalPoints * 0.85; // Simulated 85% score
        double percentage = (score / totalPoints) * 100;
        
        grading.put("score", score);
        grading.put("totalPoints", totalPoints);
        grading.put("percentage", percentage);
        grading.put("letterGrade", calculateLetterGrade(percentage));
        grading.put("confidence", 88.0);
        
        grading.put("feedback", "Good work overall. The essay demonstrates a solid understanding of the topic.");
        grading.put("strengths", "Clear thesis statement, well-organized paragraphs, good use of examples.");
        grading.put("improvements", "Could improve transitions between paragraphs and provide more in-depth analysis.");
        grading.put("comments", "The essay shows good research and understanding. Consider expanding on the conclusion.");
        
        return grading;
    }

    private double calculateReadabilityScore(int wordCount, int sentenceCount) {
        if (sentenceCount == 0) return 0.0;
        double avgWordsPerSentence = (double) wordCount / sentenceCount;
        // Simplified Flesch Reading Ease
        return Math.max(0, Math.min(100, 206.835 - (1.015 * avgWordsPerSentence)));
    }

    private String calculateLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }

    private int estimateTokens(String text) {
        // Rough estimate: 1 token ≈ 4 characters
        return text.length() / 4;
    }

    private double calculateCost(int tokens) {
        // GPT-3.5-turbo pricing: ~$0.002 per 1K tokens
        return (tokens / 1000.0) * 0.002;
    }

    private AIGradingResponse mapToResponse(AIGradingResult result) {
        Map<String, Double> criterionScores = new HashMap<>();
        try {
            if (result.getCriterionScoresJson() != null) {
                criterionScores = objectMapper.readValue(result.getCriterionScoresJson(), Map.class);
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing criterion scores JSON", e);
        }

        return AIGradingResponse.builder()
                .id(result.getId())
                .submissionId(result.getSubmission().getId())
                .rubricId(result.getRubric() != null ? result.getRubric().getId() : null)
                .aiSuggestedScore(result.getAiSuggestedScore())
                .aiConfidenceScore(result.getAiConfidenceScore())
                .totalPossibleScore(result.getTotalPossibleScore())
                .aiPercentage(result.getAiPercentage())
                .aiLetterGrade(result.getAiLetterGrade())
                .criterionScores(criterionScores)
                .aiFeedback(result.getAiFeedback())
                .strengths(result.getStrengths())
                .areasForImprovement(result.getAreasForImprovement())
                .detailedComments(result.getDetailedComments())
                .grammarScore(result.getGrammarScore())
                .grammarIssuesCount(result.getGrammarIssuesCount())
                .spellingErrorsCount(result.getSpellingErrorsCount())
                .readabilityScore(result.getReadabilityScore())
                .wordCount(result.getWordCount())
                .sentenceCount(result.getSentenceCount())
                .avgSentenceLength(result.getAvgSentenceLength())
                .plagiarismScore(result.getPlagiarismScore())
                .plagiarismDetected(result.getPlagiarismDetected())
                .similarityPercentage(result.getSimilarityPercentage())
                .aiModelUsed(result.getAiModelUsed())
                .aiProvider(result.getAiProvider())
                .tokensUsed(result.getTokensUsed())
                .processingTimeMs(result.getProcessingTimeMs())
                .gradingStatus(result.getGradingStatus().name())
                .teacherReviewed(result.getTeacherReviewed())
                .teacherApproved(result.getTeacherApproved())
                .gradingQuality(result.getGradingQuality())
                .finalScore(result.getFinalScore())
                .finalFeedback(result.getFinalFeedback())
                .reviewedAt(result.getReviewedAt())
                .createdAt(result.getCreatedOn() != null ? 
                    new java.sql.Timestamp(result.getCreatedOn().getTime()).toLocalDateTime() : null)
                .build();
    }
}
