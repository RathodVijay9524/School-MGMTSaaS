package com.vijay.User_Master.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.dto.AutoGradingRequest;
import com.vijay.User_Master.dto.AutoGradingResponse;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.QuestionRepository;
import com.vijay.User_Master.service.AutoGradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of AutoGradingService with grading algorithms
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutoGradingServiceImpl implements AutoGradingService {

    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public AutoGradingResponse gradeResponse(AutoGradingRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (question instanceof MultipleChoiceQuestion) {
            return gradeMultipleChoice((MultipleChoiceQuestion) question, request.getStudentAnswer());
        } else if (question instanceof TrueFalseQuestion) {
            return gradeTrueFalse((TrueFalseQuestion) question, request.getStudentAnswer());
        } else if (question instanceof ShortAnswerQuestion) {
            return gradeShortAnswer((ShortAnswerQuestion) question, request.getStudentAnswer());
        } else if (question instanceof EssayQuestion) {
            return gradeEssay((EssayQuestion) question, request.getStudentAnswer());
        } else if (question instanceof MatchingQuestion) {
            return gradeMatching((MatchingQuestion) question, request.getStudentAnswer());
        } else if (question instanceof OrderingQuestion) {
            return gradeOrdering((OrderingQuestion) question, request.getStudentAnswer());
        } else if (question instanceof FillInTheBlankQuestion) {
            return gradeFillInBlank((FillInTheBlankQuestion) question, request.getStudentAnswer());
        }

        throw new RuntimeException("Unsupported question type for auto-grading");
    }

    @Override
    public AutoGradingResponse gradeMultipleChoice(MultipleChoiceQuestion question, String studentAnswer) {
        try {
            // Parse student answer (JSON array of selected option IDs)
            List<Long> selectedIds = objectMapper.readValue(studentAnswer, new TypeReference<List<Long>>() {});
            
            List<QuestionOption> correctOptions = question.getCorrectOptions();
            Set<Long> correctIds = correctOptions.stream()
                    .map(QuestionOption::getId)
                    .collect(Collectors.toSet());

            boolean isCorrect;
            double pointsEarned = 0.0;
            double maxPoints = question.getPoints();

            if (question.getAllowMultipleAnswers()) {
                // Multiple correct answers - calculate partial credit
                Set<Long> selectedSet = new HashSet<>(selectedIds);
                int correctSelections = (int) selectedSet.stream().filter(correctIds::contains).count();
                int incorrectSelections = selectedSet.size() - correctSelections;
                int missedCorrect = correctIds.size() - correctSelections;

                if (question.getAllowPartialCredit()) {
                    // Partial credit: (correct - incorrect) / total correct
                    double score = (double) (correctSelections - incorrectSelections) / correctIds.size();
                    pointsEarned = Math.max(0, score * maxPoints);
                    isCorrect = score >= 0.5; // 50% threshold
                } else {
                    // All or nothing
                    isCorrect = selectedSet.equals(correctIds);
                    pointsEarned = isCorrect ? maxPoints : 0.0;
                }
            } else {
                // Single correct answer
                isCorrect = selectedIds.size() == 1 && correctIds.contains(selectedIds.get(0));
                pointsEarned = isCorrect ? maxPoints : 0.0;
            }

            String correctAnswer = correctIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            return AutoGradingResponse.builder()
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .maxPoints(maxPoints)
                    .percentage((pointsEarned / maxPoints) * 100)
                    .correctAnswer(correctAnswer)
                    .explanation(question.getExplanation())
                    .requiresManualReview(false)
                    .confidenceScore(1.0)
                    .build();

        } catch (Exception e) {
            log.error("Error grading MCQ: {}", e.getMessage());
            return AutoGradingResponse.builder()
                    .requiresManualReview(true)
                    .confidenceScore(0.0)
                    .build();
        }
    }

    @Override
    public AutoGradingResponse gradeTrueFalse(TrueFalseQuestion question, String studentAnswer) {
        Boolean studentBool = Boolean.parseBoolean(studentAnswer.trim());
        boolean isCorrect = studentBool.equals(question.getCorrectAnswer());
        double maxPoints = question.getPoints();
        double pointsEarned = isCorrect ? maxPoints : 0.0;

        String feedback = isCorrect ? question.getTrueFeedback() : question.getFalseFeedback();
        if (studentBool && feedback == null) {
            feedback = question.getTrueFeedback();
        } else if (!studentBool && feedback == null) {
            feedback = question.getFalseFeedback();
        }

        return AutoGradingResponse.builder()
                .isCorrect(isCorrect)
                .pointsEarned(pointsEarned)
                .maxPoints(maxPoints)
                .percentage((pointsEarned / maxPoints) * 100)
                .correctAnswer(question.getCorrectAnswer().toString())
                .explanation(question.getExplanation())
                .feedback(feedback)
                .requiresManualReview(false)
                .confidenceScore(1.0)
                .build();
    }

    @Override
    public AutoGradingResponse gradeShortAnswer(ShortAnswerQuestion question, String studentAnswer) {
        String trimmedAnswer = studentAnswer.trim();
        
        if (!question.getCaseSensitive()) {
            trimmedAnswer = trimmedAnswer.toLowerCase();
        }

        boolean isCorrect = false;
        double maxPoints = question.getPoints();
        double pointsEarned = 0.0;
        double confidenceScore = 1.0;

        List<String> acceptedAnswers = question.getAcceptedAnswers().stream()
                .map(a -> question.getCaseSensitive() ? a : a.toLowerCase())
                .collect(Collectors.toList());

        if (question.getExactMatch()) {
            // Exact match required
            isCorrect = acceptedAnswers.contains(trimmedAnswer);
            pointsEarned = isCorrect ? maxPoints : 0.0;
        } else {
            // Fuzzy matching - find best match
            double bestSimilarity = 0.0;
            for (String accepted : acceptedAnswers) {
                double similarity = calculateSimilarity(trimmedAnswer, accepted);
                bestSimilarity = Math.max(bestSimilarity, similarity);
            }

            confidenceScore = bestSimilarity;
            
            if (bestSimilarity >= 0.9) {
                isCorrect = true;
                pointsEarned = maxPoints;
            } else if (bestSimilarity >= 0.7 && question.getAllowPartialCredit()) {
                isCorrect = false;
                pointsEarned = maxPoints * bestSimilarity;
            } else if (bestSimilarity >= 0.5) {
                // Low confidence - needs manual review
                return AutoGradingResponse.builder()
                        .requiresManualReview(true)
                        .confidenceScore(bestSimilarity)
                        .pointsEarned(0.0)
                        .maxPoints(maxPoints)
                        .correctAnswer(String.join(" OR ", acceptedAnswers))
                        .build();
            }
        }

        return AutoGradingResponse.builder()
                .isCorrect(isCorrect)
                .pointsEarned(pointsEarned)
                .maxPoints(maxPoints)
                .percentage((pointsEarned / maxPoints) * 100)
                .correctAnswer(String.join(" OR ", acceptedAnswers))
                .explanation(question.getExplanation())
                .requiresManualReview(false)
                .confidenceScore(confidenceScore)
                .build();
    }

    @Override
    public AutoGradingResponse gradeEssay(EssayQuestion question, String studentAnswer) {
        // Essay grading requires AI - mark for manual review or use existing AI Grading system
        double maxPoints = question.getPoints();
        
        // Basic checks
        String[] words = studentAnswer.trim().split("\\s+");
        int wordCount = words.length;

        boolean meetsMinWords = question.getMinWords() == null || wordCount >= question.getMinWords();
        boolean meetsMaxWords = question.getMaxWords() == null || wordCount <= question.getMaxWords();

        if (!meetsMinWords || !meetsMaxWords) {
            return AutoGradingResponse.builder()
                    .requiresManualReview(true)
                    .confidenceScore(0.0)
                    .feedback("Word count: " + wordCount + 
                             (question.getMinWords() != null ? " (min: " + question.getMinWords() + ")" : "") +
                             (question.getMaxWords() != null ? " (max: " + question.getMaxWords() + ")" : ""))
                    .maxPoints(maxPoints)
                    .build();
        }

        // If AI grading is enabled, integrate with existing AIGradingService
        if (question.getUseAiGrading()) {
            // TODO: Integrate with existing AIGradingService
            // For now, mark for manual review
        }

        return AutoGradingResponse.builder()
                .requiresManualReview(true)
                .confidenceScore(0.0)
                .maxPoints(maxPoints)
                .feedback("Essay requires manual grading. Word count: " + wordCount)
                .build();
    }

    @Override
    public AutoGradingResponse gradeMatching(MatchingQuestion question, String studentAnswer) {
        try {
            // Parse student answer (JSON map of left item ID -> right item ID)
            Map<Long, Long> studentMatches = objectMapper.readValue(studentAnswer, 
                    new TypeReference<Map<Long, Long>>() {});

            Map<Long, Long> correctMatches = new HashMap<>();
            for (MatchingPair pair : question.getPairs()) {
                correctMatches.put(pair.getId(), pair.getId()); // Simplified - should map left to right
            }

            int correctCount = 0;
            int totalPairs = question.getPairs().size();

            for (Map.Entry<Long, Long> entry : studentMatches.entrySet()) {
                if (correctMatches.get(entry.getKey()) != null && 
                    correctMatches.get(entry.getKey()).equals(entry.getValue())) {
                    correctCount++;
                }
            }

            double maxPoints = question.getPoints();
            double pointsEarned;
            boolean isCorrect;

            if (question.getAllowPartialCredit()) {
                pointsEarned = (maxPoints * correctCount) / totalPairs;
                isCorrect = correctCount == totalPairs;
            } else {
                isCorrect = correctCount == totalPairs;
                pointsEarned = isCorrect ? maxPoints : 0.0;
            }

            return AutoGradingResponse.builder()
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .maxPoints(maxPoints)
                    .percentage((pointsEarned / maxPoints) * 100)
                    .explanation(question.getExplanation())
                    .feedback(correctCount + " out of " + totalPairs + " pairs correct")
                    .requiresManualReview(false)
                    .confidenceScore(1.0)
                    .build();

        } catch (Exception e) {
            log.error("Error grading matching question: {}", e.getMessage());
            return AutoGradingResponse.builder()
                    .requiresManualReview(true)
                    .confidenceScore(0.0)
                    .build();
        }
    }

    @Override
    public AutoGradingResponse gradeOrdering(OrderingQuestion question, String studentAnswer) {
        try {
            // Parse student answer (JSON array of item IDs in order)
            List<Long> studentOrder = objectMapper.readValue(studentAnswer, new TypeReference<List<Long>>() {});

            List<Long> correctOrder = question.getItems().stream()
                    .sorted(Comparator.comparing(OrderingItem::getCorrectOrder))
                    .map(OrderingItem::getId)
                    .collect(Collectors.toList());

            boolean isCorrect = studentOrder.equals(correctOrder);
            double maxPoints = question.getPoints();
            double pointsEarned;

            if (question.getAllowPartialOrder() && !isCorrect) {
                // Calculate partial credit based on correct positions
                int correctPositions = 0;
                for (int i = 0; i < Math.min(studentOrder.size(), correctOrder.size()); i++) {
                    if (studentOrder.get(i).equals(correctOrder.get(i))) {
                        correctPositions++;
                    }
                }
                pointsEarned = (maxPoints * correctPositions) / correctOrder.size();
            } else {
                pointsEarned = isCorrect ? maxPoints : 0.0;
            }

            return AutoGradingResponse.builder()
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .maxPoints(maxPoints)
                    .percentage((pointsEarned / maxPoints) * 100)
                    .explanation(question.getExplanation())
                    .requiresManualReview(false)
                    .confidenceScore(1.0)
                    .build();

        } catch (Exception e) {
            log.error("Error grading ordering question: {}", e.getMessage());
            return AutoGradingResponse.builder()
                    .requiresManualReview(true)
                    .confidenceScore(0.0)
                    .build();
        }
    }

    @Override
    public AutoGradingResponse gradeFillInBlank(FillInTheBlankQuestion question, String studentAnswer) {
        try {
            // Parse student answer (JSON array of answers for each blank)
            List<String> studentAnswers = objectMapper.readValue(studentAnswer, new TypeReference<List<String>>() {});

            List<String> acceptedAnswers = question.getAcceptedAnswers();
            
            if (studentAnswers.size() != question.getBlankCount()) {
                return AutoGradingResponse.builder()
                        .requiresManualReview(true)
                        .confidenceScore(0.0)
                        .feedback("Number of answers doesn't match number of blanks")
                        .build();
            }

            int correctCount = 0;
            for (int i = 0; i < studentAnswers.size(); i++) {
                String studentAns = studentAnswers.get(i).trim();
                String correctAns = acceptedAnswers.get(i);

                if (!question.getCaseSensitive()) {
                    studentAns = studentAns.toLowerCase();
                    correctAns = correctAns.toLowerCase();
                }

                if (question.getExactMatch()) {
                    if (studentAns.equals(correctAns)) {
                        correctCount++;
                    }
                } else {
                    double similarity = calculateSimilarity(studentAns, correctAns);
                    if (similarity >= 0.8) {
                        correctCount++;
                    }
                }
            }

            double maxPoints = question.getPoints();
            double pointsEarned;
            boolean isCorrect;

            if (question.getAllowPartialCredit()) {
                pointsEarned = (maxPoints * correctCount) / question.getBlankCount();
                isCorrect = correctCount == question.getBlankCount();
            } else {
                isCorrect = correctCount == question.getBlankCount();
                pointsEarned = isCorrect ? maxPoints : 0.0;
            }

            return AutoGradingResponse.builder()
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .maxPoints(maxPoints)
                    .percentage((pointsEarned / maxPoints) * 100)
                    .correctAnswer(String.join(", ", acceptedAnswers))
                    .explanation(question.getExplanation())
                    .feedback(correctCount + " out of " + question.getBlankCount() + " blanks correct")
                    .requiresManualReview(false)
                    .confidenceScore(1.0)
                    .build();

        } catch (Exception e) {
            log.error("Error grading fill-in-blank question: {}", e.getMessage());
            return AutoGradingResponse.builder()
                    .requiresManualReview(true)
                    .confidenceScore(0.0)
                    .build();
        }
    }

    @Override
    public Double calculatePartialCredit(Question question, String studentAnswer, String correctAnswer) {
        if (!question.getAllowPartialCredit()) {
            return 0.0;
        }

        double similarity = calculateSimilarity(studentAnswer, correctAnswer);
        return similarity * question.getPoints();
    }

    @Override
    public Double calculateSimilarity(String str1, String str2) {
        // Levenshtein distance based similarity
        if (str1 == null || str2 == null) {
            return 0.0;
        }

        str1 = str1.toLowerCase().trim();
        str2 = str2.toLowerCase().trim();

        if (str1.equals(str2)) {
            return 1.0;
        }

        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) {
            return 1.0;
        }

        int distance = levenshteinDistance(str1, str2);
        return 1.0 - ((double) distance / maxLength);
    }

    private int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                        dp[i - 1][j] + 1,      // deletion
                        dp[i][j - 1] + 1),     // insertion
                        dp[i - 1][j - 1] + cost // substitution
                );
            }
        }

        return dp[str1.length()][str2.length()];
    }
}
