package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.service.AcademicTutoringService;
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
 * REST Controller for Academic Tutoring System
 */
@RestController
@RequestMapping("/api/v1/tutoring")
@CrossOrigin(origins = "*")
@Slf4j
public class TutoringController {

    @Autowired
    private AcademicTutoringService academicTutoringService;

    // Tutoring Session endpoints
    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createTutoringSession(@RequestBody TutoringSessionRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            TutoringSessionResponse response = academicTutoringService.createTutoringSession(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating tutoring session: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/sessions/ai")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createTutoringSessionWithAI(@RequestBody TutoringSessionAIRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            TutoringSessionResponse response = academicTutoringService.createTutoringSessionWithAI(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating AI tutoring session: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getTutoringSessionById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            TutoringSessionResponse response = academicTutoringService.getTutoringSessionById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting tutoring session by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getAllTutoringSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            var response = academicTutoringService.getAllTutoringSessions(ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting all tutoring sessions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getTutoringSessionsByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
            
            var response = academicTutoringService.getTutoringSessionsByStudent(studentId, ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting tutoring sessions for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/subject/{subject}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getTutoringSessionsBySubject(
            @PathVariable String subject,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
            
            var response = academicTutoringService.getTutoringSessionsBySubject(ownerId, subject, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting tutoring sessions for subject {}: {}", subject, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/grade/{gradeLevel}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getTutoringSessionsByGradeLevel(
            @PathVariable String gradeLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
            
            var response = academicTutoringService.getTutoringSessionsByGradeLevel(ownerId, gradeLevel, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting tutoring sessions for grade {}: {}", gradeLevel, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> searchTutoringSessions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
            
            var response = academicTutoringService.searchTutoringSessions(ownerId, keyword, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching tutoring sessions with keyword {}: {}", keyword, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> updateTutoringSession(@PathVariable Long id, @RequestBody TutoringSessionRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            TutoringSessionResponse response = academicTutoringService.updateTutoringSession(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating tutoring session {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/sessions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTutoringSession(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            academicTutoringService.deleteTutoringSession(id, ownerId);
            return ResponseEntity.ok("Tutoring session deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting tutoring session {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/follow-up")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getSessionsRequiringFollowUp() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<TutoringSessionResponse> response = academicTutoringService.getSessionsRequiringFollowUp(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting sessions requiring follow-up: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/teacher-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getSessionsRequiringTeacherReview() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<TutoringSessionResponse> response = academicTutoringService.getSessionsRequiringTeacherReview(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting sessions requiring teacher review: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/sessions/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getRecentTutoringSessions(@RequestParam(defaultValue = "7") int days) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<TutoringSessionResponse> response = academicTutoringService.getRecentTutoringSessions(ownerId, days);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting recent tutoring sessions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Learning Path endpoints
    @PostMapping("/learning-paths")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> createLearningPath(@RequestBody LearningPathRequest request) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            LearningPathResponse response = academicTutoringService.createLearningPath(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating learning path: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-paths/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getLearningPathById(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            LearningPathResponse response = academicTutoringService.getLearningPathById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting learning path by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-paths/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getLearningPathsByStudent(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<LearningPathResponse> response = academicTutoringService.getLearningPathsByStudent(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting learning paths for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-paths/subject/{subject}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getLearningPathsBySubject(@PathVariable String subject) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<LearningPathResponse> response = academicTutoringService.getLearningPathsBySubject(ownerId, subject);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting learning paths for subject {}: {}", subject, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/learning-paths/{id}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> updateLearningPathProgress(
            @PathVariable Long id, 
            @RequestParam Double progressPercentage) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            LearningPathResponse response = academicTutoringService.updateLearningPathProgress(id, progressPercentage, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating learning path progress {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/learning-paths/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> completeLearningPath(@PathVariable Long id) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            LearningPathResponse response = academicTutoringService.completeLearningPath(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error completing learning path {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-paths/student/{studentId}/recommendations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getPersonalizedRecommendations(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<LearningPathResponse> response = academicTutoringService.getPersonalizedRecommendations(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting personalized recommendations for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AI-powered tutoring endpoints
    @PostMapping("/ai/generate-practice-problems")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> generatePracticeProblems(
            @RequestParam String subject,
            @RequestParam String topic,
            @RequestParam String gradeLevel,
            @RequestParam(defaultValue = "5") int count) {
        try {
            String response = academicTutoringService.generatePracticeProblems(subject, topic, gradeLevel, count);
            return ResponseEntity.ok(Map.of("practiceProblems", response));
        } catch (Exception e) {
            log.error("Error generating practice problems: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/ai/explain-concept")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> explainConcept(
            @RequestParam String subject,
            @RequestParam String concept,
            @RequestParam String gradeLevel) {
        try {
            String response = academicTutoringService.explainConcept(subject, concept, gradeLevel);
            return ResponseEntity.ok(Map.of("explanation", response));
        } catch (Exception e) {
            log.error("Error explaining concept: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/ai/step-by-step-solution")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> provideStepByStepSolution(
            @RequestParam String subject,
            @RequestParam String problem,
            @RequestParam String gradeLevel) {
        try {
            String response = academicTutoringService.provideStepByStepSolution(subject, problem, gradeLevel);
            return ResponseEntity.ok(Map.of("solution", response));
        } catch (Exception e) {
            log.error("Error providing step-by-step solution: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Analytics endpoints
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getTutoringStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = academicTutoringService.getTutoringStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting tutoring statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/statistics/subject-wise")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getSubjectWiseStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = academicTutoringService.getSubjectWiseStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting subject-wise statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/statistics/grade-wise")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getGradeWiseStatistics() {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            List<Map<String, Object>> response = academicTutoringService.getGradeWiseStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting grade-wise statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> analyzeStudentPerformance(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = academicTutoringService.analyzeStudentPerformance(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error analyzing student performance for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/students/{studentId}/insights")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getLearningInsights(@PathVariable Long studentId) {
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            Map<String, Object> response = academicTutoringService.getLearningInsights(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting learning insights for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
