package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.service.PeerLearningService;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Peer Learning Platform
 */
@RestController
@RequestMapping("/api/v1/peer-learning")
@CrossOrigin(origins = "*")
@Slf4j
public class StudyGroupController {

    @Autowired
    private PeerLearningService peerLearningService;

    // Study Group Management endpoints
    @PostMapping("/study-groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createStudyGroup(@RequestBody StudyGroupRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudyGroupResponse response = peerLearningService.createStudyGroup(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating study group: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudyGroupById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudyGroupResponse response = peerLearningService.getStudyGroupById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study group by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAllStudyGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            var response = peerLearningService.getAllStudyGroups(ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting all study groups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/subject/{subject}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudyGroupsBySubject(@PathVariable String subject) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupResponse> response = peerLearningService.getStudyGroupsBySubject(ownerId, subject);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study groups for subject {}: {}", subject, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/grade/{gradeLevel}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudyGroupsByGradeLevel(@PathVariable String gradeLevel) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupResponse> response = peerLearningService.getStudyGroupsByGradeLevel(ownerId, gradeLevel);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study groups for grade {}: {}", gradeLevel, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/type/{groupType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudyGroupsByType(@PathVariable String groupType) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupResponse> response = peerLearningService.getStudyGroupsByType(ownerId, groupType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study groups for type {}: {}", groupType, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAvailableStudyGroups() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupResponse> response = peerLearningService.getAvailableStudyGroups(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting available study groups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> searchStudyGroups(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
            
            var response = peerLearningService.searchStudyGroups(ownerId, keyword, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching study groups with keyword {}: {}", keyword, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/popular")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getPopularStudyGroups(@RequestParam(defaultValue = "10") int limit) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupResponse> response = peerLearningService.getPopularStudyGroups(ownerId, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting popular study groups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/study-groups/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> updateStudyGroup(@PathVariable Long id, @RequestBody StudyGroupRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudyGroupResponse response = peerLearningService.updateStudyGroup(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating study group {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/study-groups/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> deleteStudyGroup(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.deleteStudyGroup(id, ownerId);
            return ResponseEntity.ok("Study group deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting study group {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/study-groups/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> archiveStudyGroup(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.archiveStudyGroup(id, ownerId);
            return ResponseEntity.ok("Study group archived successfully");
        } catch (Exception e) {
            log.error("Error archiving study group {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/study-groups/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> restoreStudyGroup(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.restoreStudyGroup(id, ownerId);
            return ResponseEntity.ok("Study group restored successfully");
        } catch (Exception e) {
            log.error("Error restoring study group {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Study Group Membership endpoints
    @PostMapping("/study-groups/{groupId}/join/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> joinStudyGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudyGroupMemberResponse response = peerLearningService.joinStudyGroup(groupId, studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error joining study group {} for student {}: {}", groupId, studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/study-groups/{groupId}/leave/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> leaveStudyGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.leaveStudyGroup(groupId, studentId, ownerId);
            return ResponseEntity.ok("Left study group successfully");
        } catch (Exception e) {
            log.error("Error leaving study group {} for student {}: {}", groupId, studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/{groupId}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudyGroupMembers(@PathVariable Long groupId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupMemberResponse> response = peerLearningService.getStudyGroupMembers(groupId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting members for study group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/study-groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentStudyGroups(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupResponse> response = peerLearningService.getStudentStudyGroups(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study groups for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Study Session endpoints
    @PostMapping("/study-sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createStudySession(@RequestBody StudySessionRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudySessionResponse response = peerLearningService.createStudySession(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating study session: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudySessionById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudySessionResponse response = peerLearningService.getStudySessionById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study session by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/{groupId}/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudySessionsByGroup(@PathVariable Long groupId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudySessionResponse> response = peerLearningService.getStudySessionsByGroup(groupId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study sessions for group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-sessions/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getUpcomingStudySessions() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudySessionResponse> response = peerLearningService.getUpcomingStudySessions(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting upcoming study sessions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-sessions/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getCompletedStudySessions() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudySessionResponse> response = peerLearningService.getCompletedStudySessions(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting completed study sessions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/study-sessions/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> startStudySession(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudySessionResponse response = peerLearningService.startStudySession(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting study session {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/study-sessions/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> completeStudySession(@PathVariable Long id, @RequestParam String notes) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudySessionResponse response = peerLearningService.completeStudySession(id, notes, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error completing study session {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/study-sessions/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> cancelStudySession(@PathVariable Long id, @RequestParam String reason) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.cancelStudySession(id, reason, ownerId);
            return ResponseEntity.ok("Study session cancelled successfully");
        } catch (Exception e) {
            log.error("Error cancelling study session {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/study-sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> updateStudySession(@PathVariable Long id, @RequestBody StudySessionRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudySessionResponse response = peerLearningService.updateStudySession(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating study session {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/study-sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> deleteStudySession(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.deleteStudySession(id, ownerId);
            return ResponseEntity.ok("Study session deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting study session {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Peer Tutoring endpoints
    @PostMapping("/peer-tutoring")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createPeerTutoringSession(@RequestBody PeerTutoringRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudySessionResponse response = peerLearningService.createPeerTutoringSession(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating peer tutoring session: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/peer-tutoring")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getPeerTutoringSessions() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudySessionResponse> response = peerLearningService.getPeerTutoringSessions(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting peer tutoring sessions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/peer-tutoring/{sessionId}/rate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> ratePeerTutoringSession(
            @PathVariable Long sessionId,
            @RequestParam int rating,
            @RequestParam String feedback) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.ratePeerTutoringSession(sessionId, rating, feedback, ownerId);
            return ResponseEntity.ok("Rating submitted successfully");
        } catch (Exception e) {
            log.error("Error rating peer tutoring session {}: {}", sessionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Collaborative Projects endpoints
    @GetMapping("/collaborative-projects")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getCollaborativeProjects() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<StudyGroupResponse> response = peerLearningService.getCollaborativeProjects(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting collaborative projects: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/collaborative-projects")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createCollaborativeProject(@RequestBody CollaborativeProjectRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            StudyGroupResponse response = peerLearningService.createCollaborativeProject(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating collaborative project: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Study Buddy and Recommendations endpoints
    @GetMapping("/students/{studentId}/study-buddies")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> findStudyBuddies(
            @PathVariable Long studentId,
            @RequestParam String subject,
            @RequestParam String topic) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = peerLearningService.findStudyBuddies(studentId, subject, topic, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error finding study buddies for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/recommendations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getPeerRecommendations(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = peerLearningService.getPeerRecommendations(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting peer recommendations for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Discussion Forums endpoints
    @GetMapping("/discussion-forums")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getDiscussionForums() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = peerLearningService.getDiscussionForums(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting discussion forums: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/discussion-topics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createDiscussionTopic(@RequestBody DiscussionTopicRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.createDiscussionTopic(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating discussion topic: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/{groupId}/discussion-topics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getDiscussionTopicsByGroup(@PathVariable Long groupId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = peerLearningService.getDiscussionTopicsByGroup(groupId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting discussion topics for group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/discussion-replies")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> postDiscussionReply(@RequestBody DiscussionReplyRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.postDiscussionReply(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error posting discussion reply: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Analytics and Statistics endpoints
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getPeerLearningStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.getPeerLearningStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting peer learning statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/{groupId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudyGroupStatistics(@PathVariable Long groupId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.getStudyGroupStatistics(groupId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study group statistics for group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentPeerLearningProfile(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.getStudentPeerLearningProfile(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting peer learning profile for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/contribution-score")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentContributionScore(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Double response = peerLearningService.getStudentContributionScore(studentId, ownerId);
            return ResponseEntity.ok(Map.of("contributionScore", response));
        } catch (Exception e) {
            log.error("Error getting contribution score for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/students/{studentId}/activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> updateStudentContributionScore(
            @PathVariable Long studentId,
            @RequestParam String activityType) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            peerLearningService.updateStudentContributionScore(studentId, activityType, ownerId);
            return ResponseEntity.ok("Contribution score updated successfully");
        } catch (Exception e) {
            log.error("Error updating contribution score for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Dashboard endpoints
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getPeerLearningDashboard() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.getPeerLearningDashboard(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting peer learning dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/study-groups/{groupId}/activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudyGroupActivity(@PathVariable Long groupId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = peerLearningService.getStudyGroupActivity(groupId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting activity for study group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/trending-topics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getTrendingTopics(@RequestParam(defaultValue = "10") int limit) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = peerLearningService.getTrendingTopics(ownerId, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting trending topics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> getStudyGroupAnalytics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.getStudyGroupAnalytics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting study group analytics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/insights")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getPeerLearningInsights(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = peerLearningService.getPeerLearningInsights(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting peer learning insights for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
