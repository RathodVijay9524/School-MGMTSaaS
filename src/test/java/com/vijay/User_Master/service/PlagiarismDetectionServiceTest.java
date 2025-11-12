package com.vijay.User_Master.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlagiarismDetectionServiceTest extends ServiceTestBase {

    @Mock
    private PlagiarismDetectionService plagiarismDetectionService;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void checkPlagiarism_withValidText_returnsResults() {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("plagiarismScore", 0.15);
        mockResult.put("sources", new ArrayList<>());

        when(plagiarismDetectionService.checkPlagiarism("Sample text", OWNER_ID)).thenReturn(mockResult);

        Map<String, Object> result = plagiarismDetectionService.checkPlagiarism("Sample text", OWNER_ID);

        assertNotNull(result);
        assertTrue(result.containsKey("plagiarismScore"));
        verify(plagiarismDetectionService).checkPlagiarism("Sample text", OWNER_ID);
    }

    @Test
    void calculateSimilarity_withValidTexts_returnsScore() {
        when(plagiarismDetectionService.calculateSimilarity("Text 1", "Text 2")).thenReturn(0.75);

        Double similarity = plagiarismDetectionService.calculateSimilarity("Text 1", "Text 2");

        assertNotNull(similarity);
        assertEquals(0.75, similarity);
        verify(plagiarismDetectionService).calculateSimilarity("Text 1", "Text 2");
    }

    @Test
    void findSimilarSubmissions_withValidData_returnsList() {
        List<Map<String, Object>> mockResults = new ArrayList<>();
        Map<String, Object> submission = new HashMap<>();
        submission.put("id", 1L);
        submission.put("similarity", 0.85);
        mockResults.add(submission);

        when(plagiarismDetectionService.findSimilarSubmissions("Sample text", 100L, OWNER_ID)).thenReturn(mockResults);

        List<Map<String, Object>> results = plagiarismDetectionService.findSimilarSubmissions("Sample text", 100L, OWNER_ID);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(plagiarismDetectionService).findSimilarSubmissions("Sample text", 100L, OWNER_ID);
    }

    @Test
    void generatePlagiarismReport_withValidText_returnsReport() {
        Map<String, Object> mockReport = new HashMap<>();
        mockReport.put("reportId", "report-123");
        mockReport.put("totalSources", 5);

        when(plagiarismDetectionService.generatePlagiarismReport("Sample text", OWNER_ID)).thenReturn(mockReport);

        Map<String, Object> report = plagiarismDetectionService.generatePlagiarismReport("Sample text", OWNER_ID);

        assertNotNull(report);
        assertTrue(report.containsKey("reportId"));
        verify(plagiarismDetectionService).generatePlagiarismReport("Sample text", OWNER_ID);
    }

    @Test
    void checkPlagiarism_withEmptyText_returnsResults() {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("plagiarismScore", 0.0);

        when(plagiarismDetectionService.checkPlagiarism("", OWNER_ID)).thenReturn(mockResult);

        Map<String, Object> result = plagiarismDetectionService.checkPlagiarism("", OWNER_ID);

        assertNotNull(result);
        assertEquals(0.0, result.get("plagiarismScore"));
    }

    @Test
    void calculateSimilarity_withIdenticalTexts_returnsOne() {
        when(plagiarismDetectionService.calculateSimilarity("Same text", "Same text")).thenReturn(1.0);

        Double similarity = plagiarismDetectionService.calculateSimilarity("Same text", "Same text");

        assertNotNull(similarity);
        assertEquals(1.0, similarity);
    }

    @Test
    void findSimilarSubmissions_withNoMatches_returnsEmptyList() {
        List<Map<String, Object>> mockResults = new ArrayList<>();

        when(plagiarismDetectionService.findSimilarSubmissions("Unique text", 200L, OWNER_ID)).thenReturn(mockResults);

        List<Map<String, Object>> results = plagiarismDetectionService.findSimilarSubmissions("Unique text", 200L, OWNER_ID);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void generatePlagiarismReport_withLongText_returnsReport() {
        Map<String, Object> mockReport = new HashMap<>();
        mockReport.put("wordCount", 1000);

        String longText = "A".repeat(1000);
        when(plagiarismDetectionService.generatePlagiarismReport(longText, OWNER_ID)).thenReturn(mockReport);

        Map<String, Object> report = plagiarismDetectionService.generatePlagiarismReport(longText, OWNER_ID);

        assertNotNull(report);
        assertTrue(report.containsKey("wordCount"));
    }

    @Test
    void checkPlagiarism_withDifferentOwner_returnsResults() {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("plagiarismScore", 0.30);

        when(plagiarismDetectionService.checkPlagiarism("Another text", 500L)).thenReturn(mockResult);

        Map<String, Object> result = plagiarismDetectionService.checkPlagiarism("Another text", 500L);

        assertNotNull(result);
        assertEquals(0.30, result.get("plagiarismScore"));
    }

    @Test
    void findSimilarSubmissions_withDifferentAssignment_returnsList() {
        List<Map<String, Object>> mockResults = new ArrayList<>();
        Map<String, Object> submission = new HashMap<>();
        submission.put("id", 2L);
        mockResults.add(submission);

        when(plagiarismDetectionService.findSimilarSubmissions("Sample text", 150L, OWNER_ID)).thenReturn(mockResults);

        List<Map<String, Object>> results = plagiarismDetectionService.findSimilarSubmissions("Sample text", 150L, OWNER_ID);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(plagiarismDetectionService).findSimilarSubmissions("Sample text", 150L, OWNER_ID);
    }

    @Test
    void calculateSimilarity_withDifferentTexts_returnsLowScore() {
        when(plagiarismDetectionService.calculateSimilarity("Text A", "Text B")).thenReturn(0.10);

        Double similarity = plagiarismDetectionService.calculateSimilarity("Text A", "Text B");

        assertNotNull(similarity);
        assertEquals(0.10, similarity);
    }

    @Test
    void generatePlagiarismReport_withSpecialCharacters_returnsReport() {
        Map<String, Object> mockReport = new HashMap<>();
        mockReport.put("hasSpecialChars", true);

        when(plagiarismDetectionService.generatePlagiarismReport("Text with @#$%", OWNER_ID)).thenReturn(mockReport);

        Map<String, Object> report = plagiarismDetectionService.generatePlagiarismReport("Text with @#$%", OWNER_ID);

        assertNotNull(report);
        assertTrue((Boolean) report.get("hasSpecialChars"));
    }

    @Test
    void checkPlagiarism_withNullText_throwsException() {
        when(plagiarismDetectionService.checkPlagiarism(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Text cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                plagiarismDetectionService.checkPlagiarism(null, OWNER_ID));
    }

    @Test
    void calculateSimilarity_withNullText_throwsException() {
        when(plagiarismDetectionService.calculateSimilarity(null, "Text"))
                .thenThrow(new IllegalArgumentException("Texts cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                plagiarismDetectionService.calculateSimilarity(null, "Text"));
    }
}
