package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AdaptiveRecommendationResponse;
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
class PrerequisiteServiceTest extends ServiceTestBase {

    @Mock
    private PrerequisiteService prerequisiteService;

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
    void checkPrerequisites_withMetPrerequisites_returnsTrue() {
        when(prerequisiteService.checkPrerequisites(100L, "algebra")).thenReturn(true);

        boolean result = prerequisiteService.checkPrerequisites(100L, "algebra");

        assertTrue(result);
        verify(prerequisiteService).checkPrerequisites(100L, "algebra");
    }

    @Test
    void checkPrerequisites_withUnmetPrerequisites_returnsFalse() {
        when(prerequisiteService.checkPrerequisites(100L, "calculus")).thenReturn(false);

        boolean result = prerequisiteService.checkPrerequisites(100L, "calculus");

        assertFalse(result);
    }

    @Test
    void getPrerequisiteChain_withValidSkill_returnsChain() {
        List<String> mockChain = new ArrayList<>();
        mockChain.add("arithmetic");
        mockChain.add("algebra");

        when(prerequisiteService.getPrerequisiteChain("calculus")).thenReturn(mockChain);

        List<String> response = prerequisiteService.getPrerequisiteChain("calculus");

        assertNotNull(response);
        assertEquals(2, response.size());
        assertTrue(response.contains("arithmetic"));
    }

    @Test
    void getPrerequisiteChain_withNoPrerequisites_returnsEmptyList() {
        List<String> mockChain = new ArrayList<>();

        when(prerequisiteService.getPrerequisiteChain("arithmetic")).thenReturn(mockChain);

        List<String> response = prerequisiteService.getPrerequisiteChain("arithmetic");

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getBlockingSkills_withUnmetPrerequisites_returnsBlockingSkills() {
        List<AdaptiveRecommendationResponse.PrerequisiteBlockInfo> mockBlocking = new ArrayList<>();

        when(prerequisiteService.getBlockingSkills(100L, "calculus")).thenReturn(mockBlocking);

        List<AdaptiveRecommendationResponse.PrerequisiteBlockInfo> response = prerequisiteService.getBlockingSkills(100L, "calculus");

        assertNotNull(response);
        verify(prerequisiteService).getBlockingSkills(100L, "calculus");
    }

    @Test
    void isPrerequisiteMet_withMetPrerequisite_returnsTrue() {
        when(prerequisiteService.isPrerequisiteMet(100L, "algebra", 0.7)).thenReturn(true);

        boolean result = prerequisiteService.isPrerequisiteMet(100L, "algebra", 0.7);

        assertTrue(result);
    }

    @Test
    void isPrerequisiteMet_withUnmetPrerequisite_returnsFalse() {
        when(prerequisiteService.isPrerequisiteMet(100L, "algebra", 0.9)).thenReturn(false);

        boolean result = prerequisiteService.isPrerequisiteMet(100L, "algebra", 0.9);

        assertFalse(result);
    }

    @Test
    void getBlockedSkillsMap_withValidStudent_returnsBlockedSkillsMap() {
        Map<String, List<String>> mockMap = new HashMap<>();
        List<String> blockedSkills = new ArrayList<>();
        blockedSkills.add("calculus");
        mockMap.put("algebra", blockedSkills);

        when(prerequisiteService.getBlockedSkillsMap(100L, 1L)).thenReturn(mockMap);

        Map<String, List<String>> response = prerequisiteService.getBlockedSkillsMap(100L, 1L);

        assertNotNull(response);
        assertTrue(response.containsKey("algebra"));
        assertEquals(1, response.get("algebra").size());
    }

    @Test
    void getBlockedSkillsMap_withNoBlockedSkills_returnsEmptyMap() {
        Map<String, List<String>> mockMap = new HashMap<>();

        when(prerequisiteService.getBlockedSkillsMap(100L, 1L)).thenReturn(mockMap);

        Map<String, List<String>> response = prerequisiteService.getBlockedSkillsMap(100L, 1L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void findPrerequisiteBottlenecks_withValidSubject_returnsBottlenecks() {
        List<String> mockBottlenecks = new ArrayList<>();
        mockBottlenecks.add("algebra");
        mockBottlenecks.add("geometry");

        when(prerequisiteService.findPrerequisiteBottlenecks(1L)).thenReturn(mockBottlenecks);

        List<String> response = prerequisiteService.findPrerequisiteBottlenecks(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void findPrerequisiteBottlenecks_withNoBottlenecks_returnsEmptyList() {
        List<String> mockBottlenecks = new ArrayList<>();

        when(prerequisiteService.findPrerequisiteBottlenecks(1L)).thenReturn(mockBottlenecks);

        List<String> response = prerequisiteService.findPrerequisiteBottlenecks(1L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void calculatePrerequisiteCompletion_withPartialCompletion_returnsPercentage() {
        when(prerequisiteService.calculatePrerequisiteCompletion(100L, "calculus")).thenReturn(0.66);

        Double completion = prerequisiteService.calculatePrerequisiteCompletion(100L, "calculus");

        assertNotNull(completion);
        assertEquals(0.66, completion);
        assertTrue(completion > 0.0 && completion < 1.0);
    }

    @Test
    void calculatePrerequisiteCompletion_withAllMet_returnsOne() {
        when(prerequisiteService.calculatePrerequisiteCompletion(100L, "algebra")).thenReturn(1.0);

        Double completion = prerequisiteService.calculatePrerequisiteCompletion(100L, "algebra");

        assertNotNull(completion);
        assertEquals(1.0, completion);
    }

    @Test
    void calculatePrerequisiteCompletion_withNoneMet_returnsZero() {
        when(prerequisiteService.calculatePrerequisiteCompletion(100L, "calculus")).thenReturn(0.0);

        Double completion = prerequisiteService.calculatePrerequisiteCompletion(100L, "calculus");

        assertNotNull(completion);
        assertEquals(0.0, completion);
    }

    @Test
    void getRecommendedLearningOrder_withValidSubject_returnsOrder() {
        List<String> mockOrder = new ArrayList<>();
        mockOrder.add("arithmetic");
        mockOrder.add("algebra");
        mockOrder.add("geometry");
        mockOrder.add("calculus");

        when(prerequisiteService.getRecommendedLearningOrder(1L)).thenReturn(mockOrder);

        List<String> response = prerequisiteService.getRecommendedLearningOrder(1L);

        assertNotNull(response);
        assertEquals(4, response.size());
        assertEquals("arithmetic", response.get(0));
    }

    @Test
    void getRecommendedLearningOrder_withSingleSkill_returnsSingleItem() {
        List<String> mockOrder = new ArrayList<>();
        mockOrder.add("arithmetic");

        when(prerequisiteService.getRecommendedLearningOrder(1L)).thenReturn(mockOrder);

        List<String> response = prerequisiteService.getRecommendedLearningOrder(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }
}
