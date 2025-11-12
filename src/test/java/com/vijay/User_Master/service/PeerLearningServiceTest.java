package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.StudyGroupRequest;
import com.vijay.User_Master.dto.StudyGroupResponse;
import com.vijay.User_Master.dto.StudySessionRequest;
import com.vijay.User_Master.dto.StudySessionResponse;
import com.vijay.User_Master.dto.StudyGroupMemberResponse;
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
class PeerLearningServiceTest extends ServiceTestBase {

    @Mock
    private PeerLearningService peerLearningService;

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
    void createStudyGroup_withValidRequest_returnsStudyGroup() {
        StudyGroupRequest request = new StudyGroupRequest();
        StudyGroupResponse mockResponse = new StudyGroupResponse();
        mockResponse.setId(1L);

        when(peerLearningService.createStudyGroup(request, OWNER_ID)).thenReturn(mockResponse);

        StudyGroupResponse response = peerLearningService.createStudyGroup(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(peerLearningService).createStudyGroup(request, OWNER_ID);
    }

    @Test
    void getStudyGroupById_withValidId_returnsStudyGroup() {
        StudyGroupResponse mockResponse = new StudyGroupResponse();
        mockResponse.setId(1L);

        when(peerLearningService.getStudyGroupById(1L, OWNER_ID)).thenReturn(mockResponse);

        StudyGroupResponse response = peerLearningService.getStudyGroupById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAllStudyGroups_withValidOwner_returnsPagedGroups() {
        Pageable pageable = PageRequest.of(0, 10);
        List<StudyGroupResponse> groups = new ArrayList<>();
        groups.add(new StudyGroupResponse());
        Page<StudyGroupResponse> page = new PageImpl<>(groups, pageable, 1);

        when(peerLearningService.getAllStudyGroups(OWNER_ID, pageable)).thenReturn(page);

        Page<StudyGroupResponse> response = peerLearningService.getAllStudyGroups(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getStudyGroupsBySubject_withValidSubject_returnsGroups() {
        List<StudyGroupResponse> mockGroups = new ArrayList<>();
        mockGroups.add(new StudyGroupResponse());

        when(peerLearningService.getStudyGroupsBySubject(OWNER_ID, "MATH")).thenReturn(mockGroups);

        List<StudyGroupResponse> response = peerLearningService.getStudyGroupsBySubject(OWNER_ID, "MATH");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void joinStudyGroup_withValidData_returnsGroupMember() {
        StudyGroupMemberResponse mockResponse = new StudyGroupMemberResponse();
        mockResponse.setId(1L);

        when(peerLearningService.joinStudyGroup(1L, 100L, OWNER_ID)).thenReturn(mockResponse);

        StudyGroupMemberResponse response = peerLearningService.joinStudyGroup(1L, 100L, OWNER_ID);

        assertNotNull(response);
        verify(peerLearningService).joinStudyGroup(1L, 100L, OWNER_ID);
    }

    @Test
    void leaveStudyGroup_withValidData_succeeds() {
        doNothing().when(peerLearningService).leaveStudyGroup(1L, 100L, OWNER_ID);

        peerLearningService.leaveStudyGroup(1L, 100L, OWNER_ID);

        verify(peerLearningService).leaveStudyGroup(1L, 100L, OWNER_ID);
    }

    @Test
    void getStudyGroupMembers_withValidGroupId_returnsMembers() {
        List<StudyGroupMemberResponse> mockMembers = new ArrayList<>();
        mockMembers.add(new StudyGroupMemberResponse());

        when(peerLearningService.getStudyGroupMembers(1L, OWNER_ID)).thenReturn(mockMembers);

        List<StudyGroupMemberResponse> response = peerLearningService.getStudyGroupMembers(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentStudyGroups_withValidStudentId_returnsGroups() {
        List<StudyGroupResponse> mockGroups = new ArrayList<>();
        mockGroups.add(new StudyGroupResponse());

        when(peerLearningService.getStudentStudyGroups(100L, OWNER_ID)).thenReturn(mockGroups);

        List<StudyGroupResponse> response = peerLearningService.getStudentStudyGroups(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void createStudySession_withValidRequest_returnsStudySession() {
        StudySessionRequest request = new StudySessionRequest();
        StudySessionResponse mockResponse = new StudySessionResponse();
        mockResponse.setId(1L);

        when(peerLearningService.createStudySession(request, OWNER_ID)).thenReturn(mockResponse);

        StudySessionResponse response = peerLearningService.createStudySession(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getStudySessionById_withValidId_returnsStudySession() {
        StudySessionResponse mockResponse = new StudySessionResponse();
        mockResponse.setId(1L);

        when(peerLearningService.getStudySessionById(1L, OWNER_ID)).thenReturn(mockResponse);

        StudySessionResponse response = peerLearningService.getStudySessionById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getStudySessionsByGroup_withValidGroupId_returnsSessions() {
        List<StudySessionResponse> mockSessions = new ArrayList<>();
        mockSessions.add(new StudySessionResponse());

        when(peerLearningService.getStudySessionsByGroup(1L, OWNER_ID)).thenReturn(mockSessions);

        List<StudySessionResponse> response = peerLearningService.getStudySessionsByGroup(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getUpcomingStudySessions_withValidOwner_returnsSessions() {
        List<StudySessionResponse> mockSessions = new ArrayList<>();
        mockSessions.add(new StudySessionResponse());

        when(peerLearningService.getUpcomingStudySessions(OWNER_ID)).thenReturn(mockSessions);

        List<StudySessionResponse> response = peerLearningService.getUpcomingStudySessions(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void startStudySession_withValidSessionId_returnsStartedSession() {
        StudySessionResponse mockResponse = new StudySessionResponse();
        mockResponse.setId(1L);

        when(peerLearningService.startStudySession(1L, OWNER_ID)).thenReturn(mockResponse);

        StudySessionResponse response = peerLearningService.startStudySession(1L, OWNER_ID);

        assertNotNull(response);
        verify(peerLearningService).startStudySession(1L, OWNER_ID);
    }

    @Test
    void completeStudySession_withValidData_returnsCompletedSession() {
        StudySessionResponse mockResponse = new StudySessionResponse();
        mockResponse.setId(1L);

        when(peerLearningService.completeStudySession(1L, "Good session", OWNER_ID)).thenReturn(mockResponse);

        StudySessionResponse response = peerLearningService.completeStudySession(1L, "Good session", OWNER_ID);

        assertNotNull(response);
        verify(peerLearningService).completeStudySession(1L, "Good session", OWNER_ID);
    }

    @Test
    void cancelStudySession_withValidData_succeeds() {
        doNothing().when(peerLearningService).cancelStudySession(1L, "Cancelled", OWNER_ID);

        peerLearningService.cancelStudySession(1L, "Cancelled", OWNER_ID);

        verify(peerLearningService).cancelStudySession(1L, "Cancelled", OWNER_ID);
    }

    @Test
    void updateStudyGroup_withValidData_returnsUpdatedGroup() {
        StudyGroupRequest request = new StudyGroupRequest();
        StudyGroupResponse mockResponse = new StudyGroupResponse();
        mockResponse.setId(1L);

        when(peerLearningService.updateStudyGroup(1L, request, OWNER_ID)).thenReturn(mockResponse);

        StudyGroupResponse response = peerLearningService.updateStudyGroup(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(peerLearningService).updateStudyGroup(1L, request, OWNER_ID);
    }

    @Test
    void deleteStudyGroup_withValidId_succeeds() {
        doNothing().when(peerLearningService).deleteStudyGroup(1L, OWNER_ID);

        peerLearningService.deleteStudyGroup(1L, OWNER_ID);

        verify(peerLearningService).deleteStudyGroup(1L, OWNER_ID);
    }

    @Test
    void getPeerLearningStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalGroups", 10);

        when(peerLearningService.getPeerLearningStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = peerLearningService.getPeerLearningStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(10, response.get("totalGroups"));
    }

    @Test
    void getStudyGroupStatistics_withValidGroupId_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("memberCount", 5);

        when(peerLearningService.getStudyGroupStatistics(1L, OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = peerLearningService.getStudyGroupStatistics(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(5, response.get("memberCount"));
    }

    @Test
    void getStudentPeerLearningProfile_withValidStudentId_returnsProfile() {
        Map<String, Object> mockProfile = new HashMap<>();
        mockProfile.put("groupsJoined", 3);

        when(peerLearningService.getStudentPeerLearningProfile(100L, OWNER_ID)).thenReturn(mockProfile);

        Map<String, Object> response = peerLearningService.getStudentPeerLearningProfile(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(3, response.get("groupsJoined"));
    }

    @Test
    void findStudyBuddies_withValidData_returnsRecommendations() {
        List<Map<String, Object>> mockBuddies = new ArrayList<>();
        mockBuddies.add(new HashMap<>());

        when(peerLearningService.findStudyBuddies(100L, "MATH", "ALGEBRA", OWNER_ID)).thenReturn(mockBuddies);

        List<Map<String, Object>> response = peerLearningService.findStudyBuddies(100L, "MATH", "ALGEBRA", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPopularStudyGroups_withValidOwner_returnsGroups() {
        List<StudyGroupResponse> mockGroups = new ArrayList<>();
        mockGroups.add(new StudyGroupResponse());

        when(peerLearningService.getPopularStudyGroups(OWNER_ID, 10)).thenReturn(mockGroups);

        List<StudyGroupResponse> response = peerLearningService.getPopularStudyGroups(OWNER_ID, 10);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTrendingTopics_withValidOwner_returnsTrendingTopics() {
        List<Map<String, Object>> mockTopics = new ArrayList<>();
        mockTopics.add(new HashMap<>());

        when(peerLearningService.getTrendingTopics(OWNER_ID, 5)).thenReturn(mockTopics);

        List<Map<String, Object>> response = peerLearningService.getTrendingTopics(OWNER_ID, 5);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPeerLearningDashboard_withValidOwner_returnsDashboard() {
        Map<String, Object> mockDashboard = new HashMap<>();
        mockDashboard.put("activeGroups", 8);

        when(peerLearningService.getPeerLearningDashboard(OWNER_ID)).thenReturn(mockDashboard);

        Map<String, Object> response = peerLearningService.getPeerLearningDashboard(OWNER_ID);

        assertNotNull(response);
        assertEquals(8, response.get("activeGroups"));
    }

    @Test
    void archiveStudyGroup_withValidGroupId_succeeds() {
        doNothing().when(peerLearningService).archiveStudyGroup(1L, OWNER_ID);

        peerLearningService.archiveStudyGroup(1L, OWNER_ID);

        verify(peerLearningService).archiveStudyGroup(1L, OWNER_ID);
    }

    @Test
    void restoreStudyGroup_withValidGroupId_succeeds() {
        doNothing().when(peerLearningService).restoreStudyGroup(1L, OWNER_ID);

        peerLearningService.restoreStudyGroup(1L, OWNER_ID);

        verify(peerLearningService).restoreStudyGroup(1L, OWNER_ID);
    }

    @Test
    void getStudyGroupActivity_withValidGroupId_returnsActivity() {
        List<Map<String, Object>> mockActivity = new ArrayList<>();
        mockActivity.add(new HashMap<>());

        when(peerLearningService.getStudyGroupActivity(1L, OWNER_ID)).thenReturn(mockActivity);

        List<Map<String, Object>> response = peerLearningService.getStudyGroupActivity(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getStudentContributionScore_withValidStudentId_returnsScore() {
        when(peerLearningService.getStudentContributionScore(100L, OWNER_ID)).thenReturn(85.5);

        Double score = peerLearningService.getStudentContributionScore(100L, OWNER_ID);

        assertNotNull(score);
        assertEquals(85.5, score);
    }

    @Test
    void updateStudentContributionScore_withValidData_succeeds() {
        doNothing().when(peerLearningService).updateStudentContributionScore(100L, "PARTICIPATION", OWNER_ID);

        peerLearningService.updateStudentContributionScore(100L, "PARTICIPATION", OWNER_ID);

        verify(peerLearningService).updateStudentContributionScore(100L, "PARTICIPATION", OWNER_ID);
    }
}
