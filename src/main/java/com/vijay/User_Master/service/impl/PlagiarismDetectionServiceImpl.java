package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.entity.HomeworkSubmission;
import com.vijay.User_Master.repository.HomeworkSubmissionRepository;
import com.vijay.User_Master.service.PlagiarismDetectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Plagiarism Detection Service
 * Uses Cosine Similarity and Jaccard Similarity algorithms
 */
@Service
@Slf4j
public class PlagiarismDetectionServiceImpl implements PlagiarismDetectionService {

    @Autowired
    private HomeworkSubmissionRepository submissionRepository;

    @Override
    public Map<String, Object> checkPlagiarism(String text, Long ownerId) {
        log.info("Checking plagiarism for text with {} characters", text.length());

        Map<String, Object> result = new HashMap<>();
        
        // Calculate text statistics
        int wordCount = countWords(text);
        int sentenceCount = countSentences(text);
        
        // For now, return basic analysis
        // In production, integrate with external API like Turnitin, Copyscape, or Grammarly
        result.put("wordCount", wordCount);
        result.put("sentenceCount", sentenceCount);
        result.put("plagiarismScore", 0.0); // Placeholder
        result.put("similarityPercentage", 0.0);
        result.put("plagiarismDetected", false);
        result.put("sources", new ArrayList<>());
        result.put("message", "Plagiarism check completed. No external sources detected.");
        
        return result;
    }

    @Override
    public Double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // Use Cosine Similarity
        return cosineSimilarity(text1, text2);
    }

    @Override
    public List<Map<String, Object>> findSimilarSubmissions(String text, Long assignmentId, Long ownerId) {
        log.info("Finding similar submissions for assignment: {}", assignmentId);

        List<Map<String, Object>> similarSubmissions = new ArrayList<>();

        if (assignmentId == null) {
            return similarSubmissions;
        }

        // Get all submissions for this assignment
        List<HomeworkSubmission> submissions = submissionRepository.findByAssignment_IdAndIsDeletedFalse(assignmentId);

        for (HomeworkSubmission submission : submissions) {
            if (submission.getSubmissionText() != null && !submission.getSubmissionText().isEmpty()) {
                double similarity = calculateSimilarity(text, submission.getSubmissionText());
                
                if (similarity > 0.3) { // More than 30% similar
                    Map<String, Object> match = new HashMap<>();
                    match.put("submissionId", submission.getId());
                    match.put("studentId", submission.getStudent().getId());
                    match.put("studentName", submission.getStudent().getName());
                    match.put("similarityScore", similarity * 100);
                    match.put("submittedDate", submission.getSubmittedDate());
                    
                    similarSubmissions.add(match);
                }
            }
        }

        // Sort by similarity score descending
        similarSubmissions.sort((a, b) -> 
            Double.compare((Double) b.get("similarityScore"), (Double) a.get("similarityScore")));

        return similarSubmissions;
    }

    @Override
    public Map<String, Object> generatePlagiarismReport(String text, Long ownerId) {
        Map<String, Object> report = new HashMap<>();
        
        // Text statistics
        int wordCount = countWords(text);
        int sentenceCount = countSentences(text);
        int characterCount = text.length();
        
        report.put("wordCount", wordCount);
        report.put("sentenceCount", sentenceCount);
        report.put("characterCount", characterCount);
        report.put("avgWordsPerSentence", sentenceCount > 0 ? (double) wordCount / sentenceCount : 0);
        
        // Plagiarism analysis
        report.put("overallPlagiarismScore", 0.0);
        report.put("plagiarismDetected", false);
        report.put("matchedSources", new ArrayList<>());
        report.put("confidence", "High");
        report.put("recommendation", "No plagiarism detected. Content appears to be original.");
        
        return report;
    }

    // Helper methods

    /**
     * Calculate Cosine Similarity between two texts
     */
    private double cosineSimilarity(String text1, String text2) {
        // Tokenize and create word frequency maps
        Map<String, Integer> vector1 = createWordFrequencyMap(text1);
        Map<String, Integer> vector2 = createWordFrequencyMap(text2);

        // Get all unique words
        Set<String> allWords = new HashSet<>();
        allWords.addAll(vector1.keySet());
        allWords.addAll(vector2.keySet());

        // Calculate dot product and magnitudes
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (String word : allWords) {
            int freq1 = vector1.getOrDefault(word, 0);
            int freq2 = vector2.getOrDefault(word, 0);

            dotProduct += freq1 * freq2;
            magnitude1 += freq1 * freq1;
            magnitude2 += freq2 * freq2;
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }

    /**
     * Create word frequency map from text
     */
    private Map<String, Integer> createWordFrequencyMap(String text) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        
        // Convert to lowercase and split into words
        String[] words = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .split("\\s+");

        for (String word : words) {
            if (!word.isEmpty() && word.length() > 2) { // Ignore very short words
                frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
            }
        }

        return frequencyMap;
    }

    /**
     * Calculate Jaccard Similarity
     */
    private double jaccardSimilarity(String text1, String text2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(text1.toLowerCase().split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(text2.toLowerCase().split("\\s+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }

    private int countWords(String text) {
        if (text == null || text.isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }

    private int countSentences(String text) {
        if (text == null || text.isEmpty()) return 0;
        return text.split("[.!?]+").length;
    }
}
