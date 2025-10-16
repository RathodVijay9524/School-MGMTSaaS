package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.PeerLearningService;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Peer Learning Service
 */
@Service
@Slf4j
@Transactional
public class PeerLearningServiceImpl implements PeerLearningService {

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private StudyGroupMemberRepository studyGroupMemberRepository;

    @Override
    @Transactional
    public StudyGroupResponse createStudyGroup(StudyGroupRequest request, Long ownerId) {
        log.info("Creating study group: {} for subject: {}", request.getGroupName(), request.getSubject());

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Worker creator = workerRepository.findById(request.getCreatorId())
                .filter(w -> w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Creator", "id", request.getCreatorId()));

        StudyGroup studyGroup = StudyGroup.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .subject(request.getSubject())
                .gradeLevel(request.getGradeLevel())
                .topic(request.getTopic())
                .learningObjectives(request.getLearningObjectives())
                .groupType(parseGroupType(request.getGroupType()))
                .maxMembers(request.getMaxMembers())
                .currentMembers(1) // Creator is the first member
                .studySchedule(request.getStudySchedule())
                .meetingLink(request.getMeetingLink())
                .chatRoomId(request.getChatRoomId())
                .isPublic(request.getIsPublic())
                .requiresApproval(request.getRequiresApproval())
                .isActive(request.getIsActive())
                .startDate(parseDateTime(request.getStartDate()))
                .endDate(parseDateTime(request.getEndDate()))
                .creator(creator)
                .owner(owner)
                .build();

        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        // Create membership for creator
        StudyGroupMember creatorMember = StudyGroupMember.builder()
                .studyGroup(savedStudyGroup)
                .student(creator)
                .role(StudyGroupMember.MemberRole.CREATOR)
                .joinDate(LocalDateTime.now())
                .status(StudyGroupMember.MembershipStatus.ACTIVE)
                .owner(owner)
                .build();

        // This would be saved through the relationship
        savedStudyGroup.getMembers().add(creatorMember);

        return mapToStudyGroupResponse(savedStudyGroup);
    }

    @Override
    public StudyGroupResponse getStudyGroupById(Long id, Long ownerId) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .filter(sg -> sg.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroup", "id", id));

        return mapToStudyGroupResponse(studyGroup);
    }

    @Override
    public Page<StudyGroupResponse> getAllStudyGroups(Long ownerId, Pageable pageable) {
        Page<StudyGroup> studyGroups = studyGroupRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, pageable);
        
        List<StudyGroupResponse> responses = studyGroups.getContent().stream()
                .map(this::mapToStudyGroupResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, studyGroups.getTotalElements());
    }

    @Override
    public List<StudyGroupResponse> getStudyGroupsBySubject(Long ownerId, String subject) {
        List<StudyGroup> studyGroups = studyGroupRepository.findByOwnerIdAndSubjectAndIsDeletedFalseOrderByGroupNameAsc(ownerId, subject);
        
        return studyGroups.stream()
                .map(this::mapToStudyGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyGroupResponse> getStudyGroupsByGradeLevel(Long ownerId, String gradeLevel) {
        List<StudyGroup> studyGroups = studyGroupRepository.findByOwnerIdAndGradeLevelAndIsDeletedFalseOrderByGroupNameAsc(ownerId, gradeLevel);
        
        return studyGroups.stream()
                .map(this::mapToStudyGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyGroupResponse> getStudyGroupsByType(Long ownerId, String groupType) {
        StudyGroup.GroupType type = parseGroupType(groupType);
        List<StudyGroup> studyGroups = studyGroupRepository.findByOwnerIdAndGroupTypeAndIsDeletedFalseOrderByGroupNameAsc(ownerId, type);
        
        return studyGroups.stream()
                .map(this::mapToStudyGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyGroupResponse> getAvailableStudyGroups(Long ownerId) {
        List<StudyGroup> studyGroups = studyGroupRepository.findAvailableStudyGroups(ownerId, LocalDateTime.now());
        
        return studyGroups.stream()
                .map(this::mapToStudyGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyGroupMemberResponse joinStudyGroup(Long groupId, Long studentId, Long ownerId) {
        log.info("Student {} joining study group {}", studentId, groupId);

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .filter(sg -> sg.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroup", "id", groupId));

        Worker student = workerRepository.findById(studentId)
                .filter(w -> w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        // Check if student is already a member
        boolean alreadyMember = studyGroup.getMembers().stream()
                .anyMatch(member -> member.getStudent().getId().equals(studentId));

        if (alreadyMember) {
            throw new IllegalStateException("Student is already a member of this study group");
        }

        // Check if group has space
        if (!studyGroup.hasSpace()) {
            throw new IllegalStateException("Study group is full");
        }

        StudyGroupMember.MembershipStatus status = studyGroup.getRequiresApproval() 
                ? StudyGroupMember.MembershipStatus.PENDING 
                : StudyGroupMember.MembershipStatus.ACTIVE;

        StudyGroupMember member = StudyGroupMember.builder()
                .studyGroup(studyGroup)
                .student(student)
                .role(StudyGroupMember.MemberRole.MEMBER)
                .joinDate(LocalDateTime.now())
                .status(status)
                .owner(owner)
                .build();

        StudyGroupMember savedMember = studyGroupMemberRepository.save(member);
        
        // Update study group member count
        studyGroup.addMember();
        studyGroupRepository.save(studyGroup);

        return mapToStudyGroupMemberResponse(savedMember);
    }

    @Override
    @Transactional
    public void leaveStudyGroup(Long groupId, Long studentId, Long ownerId) {
        log.info("Student {} leaving study group {}", studentId, groupId);

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .filter(sg -> sg.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroup", "id", groupId));

        StudyGroupMember member = studyGroup.getMembers().stream()
                .filter(m -> m.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroupMember", "studentId", studentId));

        // Don't allow creator to leave
        if (member.getRole() == StudyGroupMember.MemberRole.CREATOR) {
            throw new IllegalStateException("Creator cannot leave the study group");
        }

        member.setStatus(StudyGroupMember.MembershipStatus.LEFT);
        studyGroupMemberRepository.save(member);

        // Update study group member count
        studyGroup.removeMember();
        studyGroupRepository.save(studyGroup);
    }

    @Override
    public List<StudyGroupMemberResponse> getStudyGroupMembers(Long groupId, Long ownerId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .filter(sg -> sg.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroup", "id", groupId));

        return studyGroup.getMembers().stream()
                .filter(member -> true)
                .map(this::mapToStudyGroupMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyGroupResponse> getStudentStudyGroups(Long studentId, Long ownerId) {
        List<StudyGroupMember> memberships = studyGroupMemberRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, ownerId);
        
        return memberships.stream()
                .filter(member -> member.getStatus() == StudyGroupMember.MembershipStatus.ACTIVE)
                .map(member -> mapToStudyGroupResponse(member.getStudyGroup()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudySessionResponse createStudySession(StudySessionRequest request, Long ownerId) {
        log.info("Creating study session: {} for group: {}", request.getSessionTitle(), request.getStudyGroupId());

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .filter(sg -> sg.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroup", "id", request.getStudyGroupId()));

        Worker facilitator = workerRepository.findById(request.getFacilitatorId())
                .filter(w -> w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Facilitator", "id", request.getFacilitatorId()));

        StudySession studySession = StudySession.builder()
                .sessionTitle(request.getSessionTitle())
                .description(request.getDescription())
                .topic(request.getTopic())
                .sessionType(parseSessionType(request.getSessionType()))
                .scheduledDate(parseDateTime(request.getScheduledDate()))
                .location(request.getLocation())
                .meetingLink(request.getMeetingLink())
                .agenda(request.getAgenda())
                .materials(request.getMaterials())
                .sessionNotes(request.getSessionNotes())
                .feedback(request.getFeedback())
                .isRecorded(request.getIsRecorded())
                .recordingLink(request.getRecordingLink())
                .isActive(request.getIsActive())
                .studyGroup(studyGroup)
                .facilitator(facilitator)
                .owner(owner)
                .build();

        StudySession savedStudySession = studySessionRepository.save(studySession);
        return mapToStudySessionResponse(savedStudySession);
    }

    @Override
    public StudySessionResponse getStudySessionById(Long id, Long ownerId) {
        StudySession studySession = studySessionRepository.findById(id)
                .filter(ss -> ss.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudySession", "id", id));

        return mapToStudySessionResponse(studySession);
    }

    @Override
    public List<StudySessionResponse> getStudySessionsByGroup(Long groupId, Long ownerId) {
        List<StudySession> studySessions = studySessionRepository.findByStudyGroupIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(groupId, ownerId);
        
        return studySessions.stream()
                .map(this::mapToStudySessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudySessionResponse> getUpcomingStudySessions(Long ownerId) {
        List<StudySession> studySessions = studySessionRepository.findUpcomingStudySessions(ownerId, LocalDateTime.now());
        
        return studySessions.stream()
                .map(this::mapToStudySessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudySessionResponse> getCompletedStudySessions(Long ownerId) {
        List<StudySession> studySessions = studySessionRepository.findByOwnerIdAndSessionStatusAndIsDeletedFalseOrderByActualEndDateDesc(ownerId, StudySession.SessionStatus.COMPLETED);
        
        return studySessions.stream()
                .map(this::mapToStudySessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudySessionResponse startStudySession(Long sessionId, Long ownerId) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .filter(ss -> ss.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudySession", "id", sessionId));

        studySession.start();
        StudySession savedStudySession = studySessionRepository.save(studySession);
        
        return mapToStudySessionResponse(savedStudySession);
    }

    @Override
    @Transactional
    public StudySessionResponse completeStudySession(Long sessionId, String notes, Long ownerId) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .filter(ss -> ss.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudySession", "id", sessionId));

        studySession.complete();
        studySession.setSessionNotes(notes);
        StudySession savedStudySession = studySessionRepository.save(studySession);
        
        return mapToStudySessionResponse(savedStudySession);
    }

    @Override
    @Transactional
    public void cancelStudySession(Long sessionId, String reason, Long ownerId) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .filter(ss -> ss.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudySession", "id", sessionId));

        studySession.cancel();
        studySession.setSessionNotes(reason);
        studySessionRepository.save(studySession);
    }

    @Override
    @Transactional
    public StudyGroupResponse updateStudyGroup(Long id, StudyGroupRequest request, Long ownerId) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .filter(sg -> sg.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroup", "id", id));

        studyGroup.setGroupName(request.getGroupName());
        studyGroup.setDescription(request.getDescription());
        studyGroup.setSubject(request.getSubject());
        studyGroup.setGradeLevel(request.getGradeLevel());
        studyGroup.setTopic(request.getTopic());
        studyGroup.setLearningObjectives(request.getLearningObjectives());
        studyGroup.setGroupType(parseGroupType(request.getGroupType()));
        studyGroup.setMaxMembers(request.getMaxMembers());
        studyGroup.setStudySchedule(request.getStudySchedule());
        studyGroup.setMeetingLink(request.getMeetingLink());
        studyGroup.setChatRoomId(request.getChatRoomId());
        studyGroup.setIsPublic(request.getIsPublic());
        studyGroup.setRequiresApproval(request.getRequiresApproval());
        studyGroup.setIsActive(request.getIsActive());
        studyGroup.setStartDate(parseDateTime(request.getStartDate()));
        studyGroup.setEndDate(parseDateTime(request.getEndDate()));

        StudyGroup updatedStudyGroup = studyGroupRepository.save(studyGroup);
        return mapToStudyGroupResponse(updatedStudyGroup);
    }

    @Override
    @Transactional
    public StudySessionResponse updateStudySession(Long id, StudySessionRequest request, Long ownerId) {
        StudySession studySession = studySessionRepository.findById(id)
                .filter(ss -> ss.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudySession", "id", id));

        studySession.setSessionTitle(request.getSessionTitle());
        studySession.setDescription(request.getDescription());
        studySession.setTopic(request.getTopic());
        studySession.setSessionType(parseSessionType(request.getSessionType()));
        studySession.setScheduledDate(parseDateTime(request.getScheduledDate()));
        studySession.setLocation(request.getLocation());
        studySession.setMeetingLink(request.getMeetingLink());
        studySession.setAgenda(request.getAgenda());
        studySession.setMaterials(request.getMaterials());
        studySession.setSessionNotes(request.getSessionNotes());
        studySession.setFeedback(request.getFeedback());
        studySession.setIsRecorded(request.getIsRecorded());
        studySession.setRecordingLink(request.getRecordingLink());
        studySession.setIsActive(request.getIsActive());

        StudySession updatedStudySession = studySessionRepository.save(studySession);
        return mapToStudySessionResponse(updatedStudySession);
    }

    @Override
    @Transactional
    public void deleteStudyGroup(Long id, Long ownerId) {
        studyGroupRepository.softDeleteByIdAndOwnerId(id, ownerId);
    }

    @Override
    @Transactional
    public void deleteStudySession(Long id, Long ownerId) {
        studySessionRepository.softDeleteByIdAndOwnerId(id, ownerId);
    }

    @Override
    public Map<String, Object> getPeerLearningStatistics(Long ownerId) {
        Object[] stats = studyGroupRepository.getStudyGroupStatistics(ownerId);
        
        Map<String, Object> statistics = new HashMap<>();
        if (stats != null && stats.length >= 6) {
            statistics.put("totalGroups", stats[0]);
            statistics.put("activeGroups", stats[1]);
            statistics.put("activeStatusGroups", stats[2]);
            statistics.put("publicGroups", stats[3]);
            statistics.put("avgMembers", stats[4]);
            statistics.put("totalMembers", stats[5]);
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStudyGroupStatistics(Long groupId, Long ownerId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .filter(sg -> sg.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudyGroup", "id", groupId));

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("groupName", studyGroup.getGroupName());
        statistics.put("currentMembers", studyGroup.getCurrentMembers());
        statistics.put("maxMembers", studyGroup.getMaxMembers());
        statistics.put("totalSessions", studySessionRepository.countByStudyGroupIdAndOwnerIdAndIsDeletedFalse(groupId, ownerId));
        statistics.put("completedSessions", studySessionRepository.countByStudyGroupIdAndOwnerIdAndSessionStatusAndIsDeletedFalse(groupId, ownerId, StudySession.SessionStatus.COMPLETED));
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStudentPeerLearningProfile(Long studentId, Long ownerId) {
        Map<String, Object> profile = new HashMap<>();
        
        // Get student's study groups
        List<StudyGroupResponse> studyGroups = getStudentStudyGroups(studentId, ownerId);
        
        // Get student's contributions
        List<StudyGroupMember> memberships = studyGroupMemberRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, ownerId);
        
        int totalHelpProvided = memberships.stream()
                .mapToInt(StudyGroupMember::getHelpProvidedCount)
                .sum();
        
        int totalHelpReceived = memberships.stream()
                .mapToInt(StudyGroupMember::getHelpReceivedCount)
                .sum();
        
        double contributionScore = memberships.stream()
                .mapToDouble(StudyGroupMember::getContributionScore)
                .average()
                .orElse(0.0);
        
        profile.put("studentId", studentId);
        profile.put("studyGroupsCount", studyGroups.size());
        profile.put("totalHelpProvided", totalHelpProvided);
        profile.put("totalHelpReceived", totalHelpReceived);
        profile.put("contributionScore", contributionScore);
        profile.put("studyGroups", studyGroups);
        
        return profile;
    }

    @Override
    public List<Map<String, Object>> findStudyBuddies(Long studentId, String subject, String topic, Long ownerId) {
        // Get students studying the same subject/topic
        List<Worker> students = workerRepository.findByOwnerIdAndIsDeletedFalseOrderByNameAsc(ownerId);
        
        return students.stream()
                .filter(student -> !student.getId().equals(studentId))
                .map(student -> {
                    // Get student's study groups for the subject
                    List<StudyGroup> studentGroups = studyGroupRepository.findByOwnerIdAndSubjectAndIsDeletedFalseOrderByGroupNameAsc(ownerId, subject);
                    
                    boolean hasMatchingTopic = studentGroups.stream()
                            .anyMatch(group -> group.getTopic().toLowerCase().contains(topic.toLowerCase()));
                    
                    if (hasMatchingTopic) {
                        Map<String, Object> buddy = new HashMap<>();
                        buddy.put("studentId", student.getId());
                        buddy.put("studentName", student.getName());
                        buddy.put("subject", subject);
                        buddy.put("topic", topic);
                        buddy.put("matchScore", 85.0); // Simplified scoring
                        return buddy;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public StudySessionResponse createPeerTutoringSession(PeerTutoringRequest request, Long ownerId) {
        // This would be a specialized study session for peer tutoring
        StudySessionRequest sessionRequest = StudySessionRequest.builder()
                .sessionTitle(request.getTitle())
                .description(request.getDescription())
                .topic(request.getTopic())
                .sessionType("PEER_TUTORING")
                .studyGroupId(request.getStudyGroupId())
                .facilitatorId(request.getTutorId())
                .build();
        
        return createStudySession(sessionRequest, ownerId);
    }

    @Override
    public List<StudySessionResponse> getPeerTutoringSessions(Long ownerId) {
        List<StudySession> sessions = studySessionRepository.findByOwnerIdAndSessionTypeAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, StudySession.SessionType.PEER_TUTORING);
        
        return sessions.stream()
                .map(this::mapToStudySessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void ratePeerTutoringSession(Long sessionId, int rating, String feedback, Long ownerId) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .filter(ss -> ss.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("StudySession", "id", sessionId));

        studySession.setFeedback(feedback + " | Rating: " + rating + "/5");
        studySessionRepository.save(studySession);
    }

    @Override
    public List<Map<String, Object>> getPeerRecommendations(Long studentId, Long ownerId) {
        // Get student's interests and recommend compatible peers
        List<StudyGroupMember> memberships = studyGroupMemberRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, ownerId);
        
        Set<String> subjects = memberships.stream()
                .map(member -> member.getStudyGroup().getSubject())
                .collect(Collectors.toSet());
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        for (String subject : subjects) {
            List<StudyGroupMember> subjectMembers = studyGroupMemberRepository.findByOwnerIdAndStudyGroupSubjectAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, subject);
            
            subjectMembers.stream()
                    .filter(member -> !member.getStudent().getId().equals(studentId))
                    .limit(3)
                    .forEach(member -> {
                        Map<String, Object> recommendation = new HashMap<>();
                        recommendation.put("studentId", member.getStudent().getId());
                        recommendation.put("studentName", member.getStudent().getName());
                        recommendation.put("subject", subject);
                        recommendation.put("reason", "Studying the same subject");
                        recommendation.put("matchScore", 90.0);
                        recommendations.add(recommendation);
                    });
        }
        
        return recommendations.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyGroupResponse> getCollaborativeProjects(Long ownerId) {
        return getStudyGroupsByType(ownerId, "PROJECT_TEAM");
    }

    @Override
    public StudyGroupResponse createCollaborativeProject(CollaborativeProjectRequest request, Long ownerId) {
        StudyGroupRequest groupRequest = StudyGroupRequest.builder()
                .groupName(request.getProjectName())
                .description(request.getDescription())
                .subject(request.getSubject())
                .gradeLevel(request.getGradeLevel())
                .topic(request.getTopic())
                .groupType("PROJECT_TEAM")
                .maxMembers(request.getMaxMembers())
                .creatorId(request.getCreatorId())
                .build();
        
        return createStudyGroup(groupRequest, ownerId);
    }

    @Override
    public List<Map<String, Object>> getDiscussionForums(Long ownerId) {
        // This would return discussion forums - simplified implementation
        List<Map<String, Object>> forums = new ArrayList<>();
        
        Map<String, Object> forum = new HashMap<>();
        forum.put("id", 1L);
        forum.put("name", "General Discussion");
        forum.put("description", "General academic discussions");
        forum.put("topicCount", 25);
        forum.put("postCount", 150);
        forums.add(forum);
        
        return forums;
    }

    @Override
    public Map<String, Object> createDiscussionTopic(DiscussionTopicRequest request, Long ownerId) {
        Map<String, Object> topic = new HashMap<>();
        topic.put("id", System.currentTimeMillis());
        topic.put("title", request.getTitle());
        topic.put("content", request.getContent());
        topic.put("authorId", request.getAuthorId());
        topic.put("createdDate", LocalDateTime.now());
        topic.put("repliesCount", 0);
        
        return topic;
    }

    @Override
    public List<Map<String, Object>> getDiscussionTopicsByGroup(Long groupId, Long ownerId) {
        // This would return discussion topics for a specific group
        List<Map<String, Object>> topics = new ArrayList<>();
        
        Map<String, Object> topic = new HashMap<>();
        topic.put("id", 1L);
        topic.put("title", "Study Session Discussion");
        topic.put("content", "Let's discuss the upcoming study session");
        topic.put("authorId", 1L);
        topic.put("createdDate", LocalDateTime.now());
        topic.put("repliesCount", 5);
        topics.add(topic);
        
        return topics;
    }

    @Override
    public Map<String, Object> postDiscussionReply(DiscussionReplyRequest request, Long ownerId) {
        Map<String, Object> reply = new HashMap<>();
        reply.put("id", System.currentTimeMillis());
        reply.put("content", request.getContent());
        reply.put("authorId", request.getAuthorId());
        reply.put("topicId", request.getTopicId());
        reply.put("createdDate", LocalDateTime.now());
        
        return reply;
    }

    @Override
    public Map<String, Object> getStudyGroupAnalytics(Long ownerId) {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("peerLearningStatistics", getPeerLearningStatistics(ownerId));
        analytics.put("subjectWiseStatistics", getSubjectWiseStatistics(ownerId));
        analytics.put("typeWiseStatistics", getTypeWiseStatistics(ownerId));
        analytics.put("recentActivity", getRecentActivity(ownerId));
        
        return analytics;
    }

    @Override
    public Map<String, Object> getPeerLearningInsights(Long studentId, Long ownerId) {
        Map<String, Object> insights = new HashMap<>();
        
        insights.put("peerLearningProfile", getStudentPeerLearningProfile(studentId, ownerId));
        insights.put("studyBuddies", findStudyBuddies(studentId, "Mathematics", "Algebra", ownerId));
        insights.put("recommendations", getPeerRecommendations(studentId, ownerId));
        
        return insights;
    }

    @Override
    public Page<StudyGroupResponse> searchStudyGroups(Long ownerId, String keyword, Pageable pageable) {
        Page<StudyGroup> studyGroups = studyGroupRepository.searchStudyGroups(ownerId, keyword, pageable);
        
        List<StudyGroupResponse> responses = studyGroups.getContent().stream()
                .map(this::mapToStudyGroupResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, studyGroups.getTotalElements());
    }

    @Override
    public List<StudyGroupResponse> getPopularStudyGroups(Long ownerId, int limit) {
        List<StudyGroup> studyGroups = studyGroupRepository.findPopularStudyGroups(ownerId, PageRequest.of(0, limit));
        
        return studyGroups.stream()
                .map(this::mapToStudyGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTrendingTopics(Long ownerId, int limit) {
        // This would analyze study group topics and return trending ones
        List<Map<String, Object>> trendingTopics = new ArrayList<>();
        
        Map<String, Object> topic = new HashMap<>();
        topic.put("topic", "Algebra");
        topic.put("subject", "Mathematics");
        topic.put("groupsCount", 15);
        topic.put("studentsCount", 120);
        topic.put("trendScore", 95.0);
        trendingTopics.add(topic);
        
        return trendingTopics;
    }

    @Override
    public Map<String, Object> getPeerLearningDashboard(Long ownerId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("peerLearningStatistics", getPeerLearningStatistics(ownerId));
        dashboard.put("availableStudyGroups", getAvailableStudyGroups(ownerId));
        dashboard.put("upcomingStudySessions", getUpcomingStudySessions(ownerId));
        dashboard.put("popularStudyGroups", getPopularStudyGroups(ownerId, 5));
        dashboard.put("trendingTopics", getTrendingTopics(ownerId, 5));
        
        return dashboard;
    }

    @Override
    @Transactional
    public void archiveStudyGroup(Long groupId, Long ownerId) {
        studyGroupRepository.archiveStudyGroup(groupId, ownerId);
    }

    @Override
    @Transactional
    public void restoreStudyGroup(Long groupId, Long ownerId) {
        studyGroupRepository.restoreStudyGroup(groupId, ownerId);
    }

    @Override
    public List<Map<String, Object>> getStudyGroupActivity(Long groupId, Long ownerId) {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Get recent study sessions
        List<StudySession> recentSessions = studySessionRepository.findByStudyGroupIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(groupId, ownerId);
        
        for (StudySession session : recentSessions) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "STUDY_SESSION");
            activity.put("title", session.getSessionTitle());
            activity.put("date", session.getCreatedOn());
            activity.put("description", "Study session: " + session.getTopic());
            activities.add(activity);
        }
        
        return activities;
    }

    @Override
    public Double getStudentContributionScore(Long studentId, Long ownerId) {
        List<StudyGroupMember> memberships = studyGroupMemberRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, ownerId);
        
        return memberships.stream()
                .mapToDouble(StudyGroupMember::getContributionScore)
                .average()
                .orElse(0.0);
    }

    @Override
    @Transactional
    public void updateStudentContributionScore(Long studentId, String activityType, Long ownerId) {
        List<StudyGroupMember> memberships = studyGroupMemberRepository.findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(studentId, ownerId);
        
        for (StudyGroupMember membership : memberships) {
            switch (activityType) {
                case "HELP_PROVIDED":
                    membership.provideHelp();
                    break;
                case "HELP_RECEIVED":
                    membership.receiveHelp();
                    break;
                case "PARTICIPATION":
                    membership.incrementParticipation();
                    break;
            }
            
            // Update contribution score based on activities
            double helpRatio = membership.getHelpRatio();
            int participationCount = membership.getParticipationCount();
            
            double contributionScore = (helpRatio * 0.6) + (Math.min(participationCount * 0.1, 0.4));
            membership.setContributionScore(contributionScore * 100);
            
            studyGroupMemberRepository.save(membership);
        }
    }

    // Helper methods
    private StudyGroup.GroupType parseGroupType(String groupType) {
        if (groupType == null) return StudyGroup.GroupType.STUDY_GROUP;
        try {
            return StudyGroup.GroupType.valueOf(groupType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return StudyGroup.GroupType.STUDY_GROUP;
        }
    }

    private StudySession.SessionType parseSessionType(String sessionType) {
        if (sessionType == null) return StudySession.SessionType.STUDY_GROUP;
        try {
            return StudySession.SessionType.valueOf(sessionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return StudySession.SessionType.STUDY_GROUP;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Map<String, Object>> getSubjectWiseStatistics(Long ownerId) {
        List<Object[]> results = studyGroupRepository.getSubjectWiseStatistics(ownerId);
        
        return results.stream()
                .map(row -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("subject", row[0]);
                    stat.put("groupCount", row[1]);
                    stat.put("avgMembers", row[2]);
                    return stat;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getTypeWiseStatistics(Long ownerId) {
        List<Object[]> results = studyGroupRepository.getTypeWiseStatistics(ownerId);
        
        return results.stream()
                .map(row -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("groupType", row[0]);
                    stat.put("groupCount", row[1]);
                    stat.put("avgMembers", row[2]);
                    return stat;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getRecentActivity(Long ownerId) {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Get recent study groups
        List<StudyGroup> recentGroups = studyGroupRepository.findByOwnerIdAndCreatedDateRange(ownerId, LocalDateTime.now().minusDays(7), LocalDateTime.now());
        
        for (StudyGroup group : recentGroups) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "STUDY_GROUP_CREATED");
            activity.put("title", group.getGroupName());
            activity.put("date", group.getCreatedOn());
            activity.put("description", "New study group created: " + group.getSubject());
            activities.add(activity);
        }
        
        return activities;
    }

    private StudyGroupResponse mapToStudyGroupResponse(StudyGroup studyGroup) {
        return StudyGroupResponse.builder()
                .id(studyGroup.getId())
                .groupName(studyGroup.getGroupName())
                .description(studyGroup.getDescription())
                .subject(studyGroup.getSubject())
                .gradeLevel(studyGroup.getGradeLevel())
                .topic(studyGroup.getTopic())
                .learningObjectives(studyGroup.getLearningObjectives())
                .groupType(studyGroup.getGroupType() != null ? studyGroup.getGroupType().name() : null)
                .maxMembers(studyGroup.getMaxMembers())
                .currentMembers(studyGroup.getCurrentMembers())
                .groupStatus(studyGroup.getGroupStatus() != null ? studyGroup.getGroupStatus().name() : null)
                .studySchedule(studyGroup.getStudySchedule())
                .meetingLink(studyGroup.getMeetingLink())
                .chatRoomId(studyGroup.getChatRoomId())
                .isPublic(studyGroup.getIsPublic())
                .requiresApproval(studyGroup.getRequiresApproval())
                .isActive(studyGroup.getIsActive())
                .statusText(studyGroup.getStatusText())
                .formattedDuration(studyGroup.getFormattedDuration())
                .isFull(studyGroup.isFull())
                .hasSpace(studyGroup.hasSpace())
                .isExpired(studyGroup.isExpired())
                .canJoin(studyGroup.canJoin())
                .availableSpots(studyGroup.getAvailableSpots())
                .creatorId(studyGroup.getCreator().getId())
                .creatorName(studyGroup.getCreator().getName())
                .startDate(studyGroup.getStartDate() != null ? java.sql.Date.valueOf(studyGroup.getStartDate().toLocalDate()) : null)
                .endDate(studyGroup.getEndDate() != null ? java.sql.Date.valueOf(studyGroup.getEndDate().toLocalDate()) : null)
                .lastActivityDate(studyGroup.getLastActivityDate() != null ? java.sql.Date.valueOf(studyGroup.getLastActivityDate().toLocalDate()) : null)
                .createdOn(studyGroup.getCreatedOn() != null ? new java.sql.Date(studyGroup.getCreatedOn().getTime()) : null)
                .updatedOn(studyGroup.getUpdatedOn() != null ? new java.sql.Date(studyGroup.getUpdatedOn().getTime()) : null)
                .build();
    }

    private StudyGroupMemberResponse mapToStudyGroupMemberResponse(StudyGroupMember member) {
        return StudyGroupMemberResponse.builder()
                .id(member.getId())
                .studyGroupId(member.getStudyGroup().getId())
                .studyGroupName(member.getStudyGroup().getGroupName())
                .studentId(member.getStudent().getId())
                .studentName(member.getStudent().getName())
                .role(member.getRole() != null ? member.getRole().name() : null)
                .roleIcon(member.getRoleIcon())
                .status(member.getStatus() != null ? member.getStatus().name() : null)
                .joinDate(member.getJoinDate() != null ? java.sql.Date.valueOf(member.getJoinDate().toLocalDate()) : null)
                .formattedJoinDate(member.getFormattedJoinDate())
                .contributionScore(member.getContributionScore())
                .contributionLevel(member.getContributionLevel())
                .participationCount(member.getParticipationCount())
                .helpProvidedCount(member.getHelpProvidedCount())
                .helpReceivedCount(member.getHelpReceivedCount())
                .helpRatio(member.getHelpRatio())
                .lastActiveDate(member.getLastActiveDate() != null ? java.sql.Date.valueOf(member.getLastActiveDate().toLocalDate()) : null)
                .daysSinceJoined(member.getDaysSinceJoined())
                .daysSinceLastActive(member.getDaysSinceLastActive())
                .isNotificationEnabled(member.getIsNotificationEnabled())
                .notes(member.getNotes())
                .isActive(member.isActive())
                .isPending(member.isPending())
                .isModerator(member.isModerator())
                .canManageGroup(member.canManageGroup())
                .createdOn(member.getCreatedOn() != null ? new java.sql.Date(member.getCreatedOn().getTime()) : null)
                .updatedOn(member.getUpdatedOn() != null ? new java.sql.Date(member.getUpdatedOn().getTime()) : null)
                .build();
    }

    private StudySessionResponse mapToStudySessionResponse(StudySession studySession) {
        return StudySessionResponse.builder()
                .id(studySession.getId())
                .sessionTitle(studySession.getSessionTitle())
                .description(studySession.getDescription())
                .topic(studySession.getTopic())
                .sessionType(studySession.getSessionType() != null ? studySession.getSessionType().name() : null)
                .typeIcon(studySession.getTypeIcon())
                .scheduledDate(studySession.getScheduledDate() != null ? java.sql.Date.valueOf(studySession.getScheduledDate().toLocalDate()) : null)
                .formattedScheduledDate(studySession.getFormattedScheduledDate())
                .actualStartDate(studySession.getActualStartDate() != null ? java.sql.Date.valueOf(studySession.getActualStartDate().toLocalDate()) : null)
                .actualEndDate(studySession.getActualEndDate() != null ? java.sql.Date.valueOf(studySession.getActualEndDate().toLocalDate()) : null)
                .durationMinutes(studySession.getDurationMinutes())
                .formattedDuration(studySession.getFormattedDuration())
                .location(studySession.getLocation())
                .meetingLink(studySession.getMeetingLink())
                .agenda(studySession.getAgenda())
                .materials(studySession.getMaterials())
                .participantsCount(studySession.getParticipantsCount())
                .sessionStatus(studySession.getSessionStatus() != null ? studySession.getSessionStatus().name() : null)
                .statusColor(studySession.getStatusColor())
                .sessionNotes(studySession.getSessionNotes())
                .feedback(studySession.getFeedback())
                .isRecorded(studySession.getIsRecorded())
                .recordingLink(studySession.getRecordingLink())
                .isActive(studySession.getIsActive())
                .isCompleted(studySession.isCompleted())
                .isOngoing(studySession.isOngoing())
                .isUpcoming(studySession.isUpcoming())
                .isPast(studySession.isPast())
                .studyGroupId(studySession.getStudyGroup().getId())
                .studyGroupName(studySession.getStudyGroup().getGroupName())
                .facilitatorId(studySession.getFacilitator().getId())
                .facilitatorName(studySession.getFacilitator().getName())
                .createdOn(studySession.getCreatedOn() != null ? new java.sql.Date(studySession.getCreatedOn().getTime()) : null)
                .updatedOn(studySession.getUpdatedOn() != null ? new java.sql.Date(studySession.getUpdatedOn().getTime()) : null)
                .build();
    }
}
