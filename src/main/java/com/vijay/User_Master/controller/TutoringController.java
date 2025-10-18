package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.service.AcademicTutoringService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.WorkerRepository;
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

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private com.vijay.User_Master.service.AdaptiveLearningService adaptiveLearningService;

    // Tutoring Session endpoints
    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> createTutoringSession(@RequestBody TutoringSessionRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
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
            Long ownerId = getCorrectOwnerId();
            Map<String, Object> response = academicTutoringService.getLearningInsights(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting learning insights for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/dashboard/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getDashboardAnalytics() {
        try {
            Long ownerId = getCorrectOwnerId();
            Map<String, Object> analytics = academicTutoringService.getDashboardAnalytics(ownerId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error getting dashboard analytics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Learning Module endpoints
    @PostMapping("/learning-modules")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> createLearningModule(@RequestBody LearningModuleRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
            LearningModuleResponse response = academicTutoringService.createLearningModule(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating learning module: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-modules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getLearningModuleById(@PathVariable Long id) {
        try {
            Long ownerId = getCorrectOwnerId();
            LearningModuleResponse response = academicTutoringService.getLearningModuleById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting learning module by ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-modules")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getLearningModulesByLearningPath(@RequestParam Long learningPathId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<LearningModuleResponse> response = academicTutoringService.getLearningModulesByLearningPath(learningPathId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting learning modules by learning path: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/learning-modules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> updateLearningModule(@PathVariable Long id, @RequestBody LearningModuleRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
            LearningModuleResponse response = academicTutoringService.updateLearningModule(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating learning module: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/learning-modules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> deleteLearningModule(@PathVariable Long id) {
        try {
            Long ownerId = getCorrectOwnerId();
            academicTutoringService.deleteLearningModule(id, ownerId);
            return ResponseEntity.ok("Learning module deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting learning module: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/learning-modules/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> completeLearningModule(@PathVariable Long id,
                                                    @RequestParam Long studentId,
                                                    @RequestParam Double scorePercentage,
                                                    @RequestParam(required = false) Integer timeSpentMinutes) {
        try {
            Long ownerId = getCorrectOwnerId();
            LearningModuleResponse response = academicTutoringService.completeLearningModule(id, studentId, scorePercentage, timeSpentMinutes, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error completing learning module: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-modules/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getLearningModuleStatistics() {
        try {
            Long ownerId = getCorrectOwnerId();
            Map<String, Object> statistics = academicTutoringService.getLearningModuleStatistics(ownerId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error getting learning module statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/learning-modules/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> searchLearningModules(@RequestParam String keyword,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        try {
            Long ownerId = getCorrectOwnerId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("moduleName").ascending());
            List<LearningModuleResponse> response = academicTutoringService.searchLearningModules(keyword, ownerId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching learning modules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ==================== ADAPTIVE LEARNING ENDPOINTS ====================

    @PostMapping("/adaptive/record-interaction")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> recordLearningInteraction(@RequestBody LearningInteractionRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
            SkillMasteryResponse response = adaptiveLearningService.recordInteraction(request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error recording interaction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/next-module")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getNextModule(@RequestParam Long studentId, @RequestParam Long subjectId) {
        try {
            Long ownerId = getCorrectOwnerId();
            AdaptiveRecommendationResponse response = adaptiveLearningService.getNextModule(studentId, subjectId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting next module: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/review-queue/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getReviewQueue(@PathVariable Long studentId) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<AdaptiveRecommendationResponse> response = adaptiveLearningService.getReviewQueue(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting review queue: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/mastery/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getStudentMastery(@PathVariable Long studentId, @RequestParam(required = false) Long subjectId) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<SkillMasteryResponse> response = adaptiveLearningService.getStudentMastery(studentId, subjectId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting student mastery: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/diagnostic/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getDiagnosticAssessment(@PathVariable Long studentId, @RequestParam Long subjectId) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<AdaptiveRecommendationResponse> response = adaptiveLearningService.getDiagnosticAssessment(studentId, subjectId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting diagnostic assessment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/remedial/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getRemedialContent(@PathVariable Long studentId, @RequestParam String skillKey) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<AdaptiveRecommendationResponse> response = adaptiveLearningService.getRemedialContent(studentId, skillKey, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting remedial content: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/skills-needing-attention/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getSkillsNeedingAttention(@PathVariable Long studentId) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<SkillMasteryResponse> response = adaptiveLearningService.getSkillsNeedingAttention(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting skills needing attention: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/mastered-skills/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getMasteredSkills(@PathVariable Long studentId) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<SkillMasteryResponse> response = adaptiveLearningService.getMasteredSkills(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting mastered skills: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/statistics/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAdaptiveLearningStatistics(@PathVariable Long studentId) {
        try {
            Long ownerId = getCorrectOwnerId();
            Map<String, Object> response = adaptiveLearningService.getAdaptiveLearningStatistics(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting adaptive learning statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/velocity-trends/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getVelocityTrends(@PathVariable Long studentId) {
        try {
            Long ownerId = getCorrectOwnerId();
            Map<String, Object> response = adaptiveLearningService.getVelocityTrends(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting velocity trends: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/adaptive/prerequisite-bottlenecks")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getPrerequisiteBottlenecks(@RequestParam Long subjectId) {
        try {
            Long ownerId = getCorrectOwnerId();
            Map<String, Object> response = adaptiveLearningService.getPrerequisiteBottlenecks(subjectId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting prerequisite bottlenecks: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/adaptive/adjust-mastery")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> adjustMastery(@RequestParam Long studentId, @RequestParam String skillKey, 
                                           @RequestParam Double newMasteryLevel, @RequestParam String reason) {
        try {
            Long ownerId = getCorrectOwnerId();
            SkillMasteryResponse response = adaptiveLearningService.adjustMastery(studentId, skillKey, newMasteryLevel, reason, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adjusting mastery: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/adaptive/reset-mastery")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> resetSkillMastery(@RequestParam Long studentId, @RequestParam String skillKey) {
        try {
            Long ownerId = getCorrectOwnerId();
            adaptiveLearningService.resetSkillMastery(studentId, skillKey, ownerId);
            return ResponseEntity.ok("Skill mastery reset successfully");
        } catch (Exception e) {
            log.error("Error resetting skill mastery: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get the correct owner ID based on the logged-in user.
     * If the logged-in user is a worker (like a student), return their owner's ID.
     * If the logged-in user is a direct owner, return their own ID.
     */
    private Long getCorrectOwnerId() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long userId = loggedInUser.getId();
        
        // Check if the logged-in user is a worker
        Worker worker = workerRepository.findById(userId).orElse(null);
        if (worker != null && worker.getOwner() != null) {
            // User is a worker, return their owner's ID
            Long ownerId = worker.getOwner().getId();
            log.info("Logged-in user is worker ID: {}, returning owner ID: {}", userId, ownerId);
            return ownerId;
        }
        
        // User is a direct owner, return their own ID
        log.info("Logged-in user is direct owner ID: {}", userId);
        return userId;
    }
}
