package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AchievementRequest;
import com.vijay.User_Master.dto.AchievementResponse;
import com.vijay.User_Master.dto.StudentAchievementResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GamificationServiceTest extends ServiceTestBase {

    @Mock
    private GamificationService gamificationService;

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
    void createAchievement_withValidRequest_returnsAchievement() {
        AchievementRequest request = new AchievementRequest();
        AchievementResponse mockResponse = new AchievementResponse();
        mockResponse.setId(1L);

        when(gamificationService.createAchievement(request, OWNER_ID)).thenReturn(mockResponse);

        AchievementResponse response = gamificationService.createAchievement(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(gamificationService).createAchievement(request, OWNER_ID);
    }

    @Test
    void getAchievementById_withValidId_returnsAchievement() {
        AchievementResponse mockResponse = new AchievementResponse();
        mockResponse.setId(1L);

        when(gamificationService.getAchievementById(1L, OWNER_ID)).thenReturn(mockResponse);

        AchievementResponse response = gamificationService.getAchievementById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAllAchievements_withValidOwner_returnsPagedAchievements() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AchievementResponse> achievements = new ArrayList<>();
        achievements.add(new AchievementResponse());
        Page<AchievementResponse> page = new PageImpl<>(achievements, pageable, 1);

        when(gamificationService.getAllAchievements(OWNER_ID, pageable)).thenReturn(page);

        Page<AchievementResponse> response = gamificationService.getAllAchievements(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getAchievementsByType_withValidType_returnsAchievements() {
        List<AchievementResponse> mockAchievements = new ArrayList<>();
        mockAchievements.add(new AchievementResponse());

        when(gamificationService.getAchievementsByType(OWNER_ID, "BADGE")).thenReturn(mockAchievements);

        List<AchievementResponse> response = gamificationService.getAchievementsByType(OWNER_ID, "BADGE");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAchievementsByCategory_withValidCategory_returnsAchievements() {
        List<AchievementResponse> mockAchievements = new ArrayList<>();
        mockAchievements.add(new AchievementResponse());

        when(gamificationService.getAchievementsByCategory(OWNER_ID, "ACADEMIC")).thenReturn(mockAchievements);

        List<AchievementResponse> response = gamificationService.getAchievementsByCategory(OWNER_ID, "ACADEMIC");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAchievementsByDifficulty_withValidDifficulty_returnsAchievements() {
        List<AchievementResponse> mockAchievements = new ArrayList<>();
        mockAchievements.add(new AchievementResponse());

        when(gamificationService.getAchievementsByDifficulty(OWNER_ID, "HARD")).thenReturn(mockAchievements);

        List<AchievementResponse> response = gamificationService.getAchievementsByDifficulty(OWNER_ID, "HARD");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void awardAchievement_withValidData_returnsStudentAchievement() {
        StudentAchievementResponse mockResponse = new StudentAchievementResponse();
        mockResponse.setId(1L);

        when(gamificationService.awardAchievement(1L, 100L, OWNER_ID)).thenReturn(mockResponse);

        StudentAchievementResponse response = gamificationService.awardAchievement(1L, 100L, OWNER_ID);

        assertNotNull(response);
        verify(gamificationService).awardAchievement(1L, 100L, OWNER_ID);
    }

    @Test
    void getStudentAchievements_withValidStudentId_returnsAchievements() {
        List<StudentAchievementResponse> mockAchievements = new ArrayList<>();
        mockAchievements.add(new StudentAchievementResponse());

        when(gamificationService.getStudentAchievements(100L, OWNER_ID)).thenReturn(mockAchievements);

        List<StudentAchievementResponse> response = gamificationService.getStudentAchievements(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateAchievementProgress_withValidData_returnsUpdatedAchievement() {
        StudentAchievementResponse mockResponse = new StudentAchievementResponse();
        mockResponse.setId(1L);

        when(gamificationService.updateAchievementProgress(1L, 100L, 50, OWNER_ID)).thenReturn(mockResponse);

        StudentAchievementResponse response = gamificationService.updateAchievementProgress(1L, 100L, 50, OWNER_ID);

        assertNotNull(response);
        verify(gamificationService).updateAchievementProgress(1L, 100L, 50, OWNER_ID);
    }

    @Test
    void getAchievementStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalAchievements", 50);

        when(gamificationService.getAchievementStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = gamificationService.getAchievementStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(50, response.get("totalAchievements"));
    }

    @Test
    void getStudentGamificationProfile_withValidStudentId_returnsProfile() {
        Map<String, Object> mockProfile = new HashMap<>();
        mockProfile.put("level", 5);

        when(gamificationService.getStudentGamificationProfile(100L, OWNER_ID)).thenReturn(mockProfile);

        Map<String, Object> response = gamificationService.getStudentGamificationProfile(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(5, response.get("level"));
    }

    @Test
    void getLeaderboard_withValidCategory_returnsLeaderboard() {
        List<Map<String, Object>> mockLeaderboard = new ArrayList<>();
        mockLeaderboard.add(new HashMap<>());

        when(gamificationService.getLeaderboard(OWNER_ID, "OVERALL", 10)).thenReturn(mockLeaderboard);

        List<Map<String, Object>> response = gamificationService.getLeaderboard(OWNER_ID, "OVERALL", 10);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentPoints_withValidStudentId_returnsPoints() {
        when(gamificationService.getStudentPoints(100L, OWNER_ID)).thenReturn(1500);

        Integer points = gamificationService.getStudentPoints(100L, OWNER_ID);

        assertNotNull(points);
        assertEquals(1500, points);
    }

    @Test
    void getStudentXP_withValidStudentId_returnsXP() {
        when(gamificationService.getStudentXP(100L, OWNER_ID)).thenReturn(2000);

        Integer xp = gamificationService.getStudentXP(100L, OWNER_ID);

        assertNotNull(xp);
        assertEquals(2000, xp);
    }

    @Test
    void getStudentLevel_withValidStudentId_returnsLevel() {
        when(gamificationService.getStudentLevel(100L, OWNER_ID)).thenReturn(10);

        Integer level = gamificationService.getStudentLevel(100L, OWNER_ID);

        assertNotNull(level);
        assertEquals(10, level);
    }

    @Test
    void getStudentBadges_withValidStudentId_returnsBadges() {
        List<AchievementResponse> mockBadges = new ArrayList<>();
        mockBadges.add(new AchievementResponse());

        when(gamificationService.getStudentBadges(100L, OWNER_ID)).thenReturn(mockBadges);

        List<AchievementResponse> response = gamificationService.getStudentBadges(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getDailyChallenges_withValidOwner_returnsChallenges() {
        List<AchievementResponse> mockChallenges = new ArrayList<>();
        mockChallenges.add(new AchievementResponse());

        when(gamificationService.getDailyChallenges(OWNER_ID)).thenReturn(mockChallenges);

        List<AchievementResponse> response = gamificationService.getDailyChallenges(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void completeChallenge_withValidData_returnsCompletedChallenge() {
        StudentAchievementResponse mockResponse = new StudentAchievementResponse();
        mockResponse.setId(1L);

        when(gamificationService.completeChallenge(1L, 100L, OWNER_ID)).thenReturn(mockResponse);

        StudentAchievementResponse response = gamificationService.completeChallenge(1L, 100L, OWNER_ID);

        assertNotNull(response);
        verify(gamificationService).completeChallenge(1L, 100L, OWNER_ID);
    }

    @Test
    void getGamificationDashboard_withValidOwner_returnsDashboard() {
        Map<String, Object> mockDashboard = new HashMap<>();
        mockDashboard.put("activeStudents", 50);

        when(gamificationService.getGamificationDashboard(OWNER_ID)).thenReturn(mockDashboard);

        Map<String, Object> response = gamificationService.getGamificationDashboard(OWNER_ID);

        assertNotNull(response);
        assertEquals(50, response.get("activeStudents"));
    }

    @Test
    void getStudentProgress_withValidStudentId_returnsProgress() {
        Map<String, Object> mockProgress = new HashMap<>();
        mockProgress.put("completionPercentage", 75);

        when(gamificationService.getStudentProgress(100L, OWNER_ID)).thenReturn(mockProgress);

        Map<String, Object> response = gamificationService.getStudentProgress(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(75, response.get("completionPercentage"));
    }

    @Test
    void resetStudentProgress_withValidData_succeeds() {
        doNothing().when(gamificationService).resetStudentProgress(100L, OWNER_ID);

        gamificationService.resetStudentProgress(100L, OWNER_ID);

        verify(gamificationService).resetStudentProgress(100L, OWNER_ID);
    }

    @Test
    void deleteAchievement_withValidId_succeeds() {
        doNothing().when(gamificationService).deleteAchievement(1L, OWNER_ID);

        gamificationService.deleteAchievement(1L, OWNER_ID);

        verify(gamificationService).deleteAchievement(1L, OWNER_ID);
    }

    @Test
    void updateAchievement_withValidData_returnsUpdatedAchievement() {
        AchievementRequest request = new AchievementRequest();
        AchievementResponse mockResponse = new AchievementResponse();
        mockResponse.setId(1L);

        when(gamificationService.updateAchievement(1L, request, OWNER_ID)).thenReturn(mockResponse);

        AchievementResponse response = gamificationService.updateAchievement(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(gamificationService).updateAchievement(1L, request, OWNER_ID);
    }

    @Test
    void getAchievementAnalytics_withValidOwner_returnsAnalytics() {
        Map<String, Object> mockAnalytics = new HashMap<>();
        mockAnalytics.put("awardedCount", 100);

        when(gamificationService.getAchievementAnalytics(OWNER_ID)).thenReturn(mockAnalytics);

        Map<String, Object> response = gamificationService.getAchievementAnalytics(OWNER_ID);

        assertNotNull(response);
        assertEquals(100, response.get("awardedCount"));
    }
}
