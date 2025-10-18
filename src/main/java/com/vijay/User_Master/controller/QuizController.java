package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.service.QuizService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.repository.WorkerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Quiz Management
 * Provides 25+ endpoints for quiz operations
 */
@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;
    private final WorkerRepository workerRepository;

    /**
     * Create a new quiz
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuizResponse response = quizService.createQuiz(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating quiz: " + e.getMessage());
        }
    }

    /**
     * Update a quiz
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuizResponse response = quizService.updateQuiz(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating quiz: " + e.getMessage());
        }
    }

    /**
     * Get quiz by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            QuizResponse response = quizService.getQuizById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching quiz: " + e.getMessage());
        }
    }

    /**
     * Delete quiz
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            quizService.deleteQuiz(id, ownerId);
            return ResponseEntity.ok("Quiz deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting quiz: " + e.getMessage());
        }
    }

    /**
     * Get all quizzes
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getAllQuizzes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        try {
            Long ownerId = getOwnerId();
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                Page<QuizResponse> response = quizService.getQuizzesPaginated(ownerId, pageable);
                return ResponseEntity.ok(response);
            } else {
                List<QuizResponse> response = quizService.getAllQuizzes(ownerId);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching quizzes: " + e.getMessage());
        }
    }

    /**
     * Get quizzes by subject
     */
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getQuizzesBySubject(@PathVariable Long subjectId) {
        try {
            Long ownerId = getOwnerId();
            List<QuizResponse> response = quizService.getQuizzesBySubject(subjectId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching quizzes: " + e.getMessage());
        }
    }

    /**
     * Get quizzes by class
     */
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getQuizzesByClass(@PathVariable Long classId) {
        try {
            Long ownerId = getOwnerId();
            List<QuizResponse> response = quizService.getQuizzesByClass(classId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching quizzes: " + e.getMessage());
        }
    }

    /**
     * Get available quizzes for a student
     */
    @GetMapping("/available/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> getAvailableQuizzes(@PathVariable Long studentId) {
        try {
            Long ownerId = getOwnerId();
            List<QuizResponse> response = quizService.getAvailableQuizzes(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching quizzes: " + e.getMessage());
        }
    }

    /**
     * Publish/unpublish a quiz
     */
    @PostMapping("/publish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> publishQuiz(@Valid @RequestBody QuizPublishRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuizResponse response = quizService.publishQuiz(request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error publishing quiz: " + e.getMessage());
        }
    }

    /**
     * Clone a quiz
     */
    @PostMapping("/clone")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> cloneQuiz(@Valid @RequestBody QuizCloneRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuizResponse response = quizService.cloneQuiz(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error cloning quiz: " + e.getMessage());
        }
    }

    /**
     * Start a quiz attempt
     */
    @PostMapping("/{quizId}/start/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> startQuizAttempt(@PathVariable Long quizId, @PathVariable Long studentId) {
        try {
            Long ownerId = getOwnerId();
            QuizAttemptResponse response = quizService.startQuizAttempt(quizId, studentId, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error starting quiz: " + e.getMessage());
        }
    }

    /**
     * Submit quiz attempt
     */
    @PostMapping("/submit/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> submitQuizAttempt(@PathVariable Long studentId, 
                                                @Valid @RequestBody QuizAttemptRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuizAttemptResponse response = quizService.submitQuizAttempt(request, studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting quiz: " + e.getMessage());
        }
    }

    /**
     * Get quiz attempt by ID
     */
    @GetMapping("/attempts/{attemptId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getQuizAttempt(@PathVariable Long attemptId) {
        try {
            Long ownerId = getOwnerId();
            QuizAttemptResponse response = quizService.getQuizAttempt(attemptId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching attempt: " + e.getMessage());
        }
    }

    /**
     * Get all attempts for a quiz
     */
    @GetMapping("/{quizId}/attempts")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuizAttempts(@PathVariable Long quizId) {
        try {
            Long ownerId = getOwnerId();
            List<QuizAttemptResponse> response = quizService.getQuizAttempts(quizId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching attempts: " + e.getMessage());
        }
    }

    /**
     * Get student's attempts for a quiz
     */
    @GetMapping("/{quizId}/attempts/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getStudentAttempts(@PathVariable Long quizId, @PathVariable Long studentId) {
        try {
            Long ownerId = getOwnerId();
            List<QuizAttemptResponse> response = quizService.getStudentAttempts(quizId, studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching attempts: " + e.getMessage());
        }
    }

    /**
     * Get quiz review (after submission)
     */
    @GetMapping("/review/{attemptId}/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuizReview(@PathVariable Long attemptId, @PathVariable Long studentId) {
        try {
            Long ownerId = getOwnerId();
            QuizReviewResponse response = quizService.getQuizReview(attemptId, studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching review: " + e.getMessage());
        }
    }

    /**
     * Get quiz statistics
     */
    @GetMapping("/{quizId}/statistics")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuizStatistics(@PathVariable Long quizId) {
        try {
            Long ownerId = getOwnerId();
            QuizStatisticsResponse response = quizService.getQuizStatistics(quizId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching statistics: " + e.getMessage());
        }
    }

    /**
     * Get student quiz summary/dashboard
     */
    @GetMapping("/dashboard/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> getStudentSummary(@PathVariable Long studentId) {
        try {
            Long ownerId = getOwnerId();
            StudentQuizSummaryResponse response = quizService.getStudentSummary(studentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching summary: " + e.getMessage());
        }
    }

    /**
     * Get teacher quiz dashboard
     */
    @GetMapping("/dashboard/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getTeacherDashboard(@PathVariable Long teacherId) {
        try {
            Long ownerId = getOwnerId();
            TeacherQuizDashboardResponse response = quizService.getTeacherDashboard(teacherId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching dashboard: " + e.getMessage());
        }
    }

    /**
     * Manual grading of a response
     */
    @PostMapping("/grade/manual/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> manualGradeResponse(@PathVariable Long teacherId,
                                                   @Valid @RequestBody ManualGradingRequest request) {
        try {
            Long ownerId = getOwnerId();
            quizService.manualGradeResponse(request, teacherId, ownerId);
            return ResponseEntity.ok("Response graded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error grading response: " + e.getMessage());
        }
    }

    /**
     * Batch grading
     */
    @PostMapping("/grade/batch/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> batchGradeAttempts(@PathVariable Long teacherId,
                                                  @Valid @RequestBody BatchGradingRequest request) {
        try {
            Long ownerId = getOwnerId();
            BatchGradingResponse response = quizService.batchGradeAttempts(request, teacherId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error batch grading: " + e.getMessage());
        }
    }

    /**
     * Get the correct owner ID for data isolation.
     * If the logged-in user is a worker (like a student/teacher), return their owner's ID.
     * If the logged-in user is a direct owner (school admin), return their own ID.
     * This ensures data isolation between independent schools.
     */
    private Long getOwnerId() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long userId = loggedInUser.getId();
        
        // Check if the logged-in user is a worker
        Worker worker = workerRepository.findById(userId).orElse(null);
        if (worker != null && worker.getOwner() != null) {
            // User is a worker, return their owner's ID
            Long ownerId = worker.getOwner().getId();
            log.debug("Logged-in user is worker ID: {}, returning owner ID: {}", userId, ownerId);
            return ownerId;
        }
        
        // User is a direct owner, return their own ID
        log.debug("Logged-in user is direct owner ID: {}", userId);
        return userId;
    }
}
