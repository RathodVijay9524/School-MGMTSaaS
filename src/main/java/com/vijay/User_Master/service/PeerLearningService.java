package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Peer Learning Platform
 */
public interface PeerLearningService {

    /**
     * Create a new study group
     */
    StudyGroupResponse createStudyGroup(StudyGroupRequest request, Long ownerId);

    /**
     * Get study group by ID
     */
    StudyGroupResponse getStudyGroupById(Long id, Long ownerId);

    /**
     * Get all study groups with pagination
     */
    Page<StudyGroupResponse> getAllStudyGroups(Long ownerId, Pageable pageable);

    /**
     * Get study groups by subject
     */
    List<StudyGroupResponse> getStudyGroupsBySubject(Long ownerId, String subject);

    /**
     * Get study groups by grade level
     */
    List<StudyGroupResponse> getStudyGroupsByGradeLevel(Long ownerId, String gradeLevel);

    /**
     * Get study groups by type
     */
    List<StudyGroupResponse> getStudyGroupsByType(Long ownerId, String groupType);

    /**
     * Get available study groups
     */
    List<StudyGroupResponse> getAvailableStudyGroups(Long ownerId);

    /**
     * Join study group
     */
    StudyGroupMemberResponse joinStudyGroup(Long groupId, Long studentId, Long ownerId);

    /**
     * Leave study group
     */
    void leaveStudyGroup(Long groupId, Long studentId, Long ownerId);

    /**
     * Get study group members
     */
    List<StudyGroupMemberResponse> getStudyGroupMembers(Long groupId, Long ownerId);

    /**
     * Get student study groups
     */
    List<StudyGroupResponse> getStudentStudyGroups(Long studentId, Long ownerId);

    /**
     * Create study session
     */
    StudySessionResponse createStudySession(StudySessionRequest request, Long ownerId);

    /**
     * Get study session by ID
     */
    StudySessionResponse getStudySessionById(Long id, Long ownerId);

    /**
     * Get study sessions by group
     */
    List<StudySessionResponse> getStudySessionsByGroup(Long groupId, Long ownerId);

    /**
     * Get upcoming study sessions
     */
    List<StudySessionResponse> getUpcomingStudySessions(Long ownerId);

    /**
     * Get completed study sessions
     */
    List<StudySessionResponse> getCompletedStudySessions(Long ownerId);

    /**
     * Start study session
     */
    StudySessionResponse startStudySession(Long sessionId, Long ownerId);

    /**
     * Complete study session
     */
    StudySessionResponse completeStudySession(Long sessionId, String notes, Long ownerId);

    /**
     * Cancel study session
     */
    void cancelStudySession(Long sessionId, String reason, Long ownerId);

    /**
     * Update study group
     */
    StudyGroupResponse updateStudyGroup(Long id, StudyGroupRequest request, Long ownerId);

    /**
     * Update study session
     */
    StudySessionResponse updateStudySession(Long id, StudySessionRequest request, Long ownerId);

    /**
     * Delete study group
     */
    void deleteStudyGroup(Long id, Long ownerId);

    /**
     * Delete study session
     */
    void deleteStudySession(Long id, Long ownerId);

    /**
     * Get peer learning statistics
     */
    Map<String, Object> getPeerLearningStatistics(Long ownerId);

    /**
     * Get study group statistics
     */
    Map<String, Object> getStudyGroupStatistics(Long groupId, Long ownerId);

    /**
     * Get student peer learning profile
     */
    Map<String, Object> getStudentPeerLearningProfile(Long studentId, Long ownerId);

    /**
     * Find study buddies
     */
    List<Map<String, Object>> findStudyBuddies(Long studentId, String subject, String topic, Long ownerId);

    /**
     * Create peer tutoring session
     */
    StudySessionResponse createPeerTutoringSession(PeerTutoringRequest request, Long ownerId);

    /**
     * Get peer tutoring sessions
     */
    List<StudySessionResponse> getPeerTutoringSessions(Long ownerId);

    /**
     * Rate peer tutoring session
     */
    void ratePeerTutoringSession(Long sessionId, int rating, String feedback, Long ownerId);

    /**
     * Get peer recommendations
     */
    List<Map<String, Object>> getPeerRecommendations(Long studentId, Long ownerId);

    /**
     * Get collaborative projects
     */
    List<StudyGroupResponse> getCollaborativeProjects(Long ownerId);

    /**
     * Create collaborative project
     */
    StudyGroupResponse createCollaborativeProject(CollaborativeProjectRequest request, Long ownerId);

    /**
     * Get discussion forums
     */
    List<Map<String, Object>> getDiscussionForums(Long ownerId);

    /**
     * Create discussion topic
     */
    Map<String, Object> createDiscussionTopic(DiscussionTopicRequest request, Long ownerId);

    /**
     * Get discussion topics by group
     */
    List<Map<String, Object>> getDiscussionTopicsByGroup(Long groupId, Long ownerId);

    /**
     * Post discussion reply
     */
    Map<String, Object> postDiscussionReply(DiscussionReplyRequest request, Long ownerId);

    /**
     * Get study group analytics
     */
    Map<String, Object> getStudyGroupAnalytics(Long ownerId);

    /**
     * Get peer learning insights
     */
    Map<String, Object> getPeerLearningInsights(Long studentId, Long ownerId);

    /**
     * Search study groups
     */
    Page<StudyGroupResponse> searchStudyGroups(Long ownerId, String keyword, Pageable pageable);

    /**
     * Get popular study groups
     */
    List<StudyGroupResponse> getPopularStudyGroups(Long ownerId, int limit);

    /**
     * Get trending topics
     */
    List<Map<String, Object>> getTrendingTopics(Long ownerId, int limit);

    /**
     * Get peer learning dashboard
     */
    Map<String, Object> getPeerLearningDashboard(Long ownerId);

    /**
     * Archive study group
     */
    void archiveStudyGroup(Long groupId, Long ownerId);

    /**
     * Restore study group
     */
    void restoreStudyGroup(Long groupId, Long ownerId);

    /**
     * Get study group activity
     */
    List<Map<String, Object>> getStudyGroupActivity(Long groupId, Long ownerId);

    /**
     * Get student contribution score
     */
    Double getStudentContributionScore(Long studentId, Long ownerId);

    /**
     * Update student contribution score
     */
    void updateStudentContributionScore(Long studentId, String activityType, Long ownerId);
}
