package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.AIGradingRequest;
import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.dto.RubricRequest;
import com.vijay.User_Master.dto.RubricResponse;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.RubricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for AI Grading Assistant
 */
@RestController
@RequestMapping("/api/v1/ai-grading")
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "AI Grading Assistant", description = "APIs for AI-powered essay grading, plagiarism detection, and rubric management")
public class AIGradingController {

    @Autowired
    private AIGradingService aiGradingService;

    @Autowired
    private RubricService rubricService;

    @Autowired
    private WorkerRepository workerRepository;

    // ==================== AI GRADING ENDPOINTS ====================

    @PostMapping("/grade")
    @Operation(summary = "Grade a submission using AI", description = "Automatically grade an essay/assignment using AI with rubric-based scoring")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> gradeSubmission(@Valid @RequestBody AIGradingRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
            AIGradingResponse response = aiGradingService.gradeSubmission(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error grading submission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/result/submission/{submissionId}")
    @Operation(summary = "Get AI grading result by submission", description = "Retrieve AI grading result for a specific submission")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getGradingResultBySubmission(@PathVariable Long submissionId) {
        try {
            Long ownerId = getCorrectOwnerId();
            AIGradingResponse response = aiGradingService.getGradingResultBySubmission(submissionId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting grading result: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/pending-review")
    @Operation(summary = "Get pending teacher reviews", description = "Get all AI gradings pending teacher review")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getPendingTeacherReview() {
        try {
            Long ownerId = getCorrectOwnerId();
            List<AIGradingResponse> response = aiGradingService.getPendingTeacherReview(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting pending reviews: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/approve/{gradingResultId}")
    @Operation(summary = "Approve AI grading", description = "Teacher approves the AI-generated grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> approveGrading(@PathVariable Long gradingResultId) {
        try {
            Long ownerId = getCorrectOwnerId();
            CustomUserDetails user = CommonUtils.getLoggedInUser();
            Long teacherId = user.getId();
            
            AIGradingResponse response = aiGradingService.approveGrading(gradingResultId, teacherId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error approving grading: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/modify/{gradingResultId}")
    @Operation(summary = "Modify AI grading", description = "Teacher modifies the AI-generated grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> modifyGrading(
            @PathVariable Long gradingResultId,
            @RequestParam Double newScore,
            @RequestParam String newFeedback) {
        try {
            Long ownerId = getCorrectOwnerId();
            CustomUserDetails user = CommonUtils.getLoggedInUser();
            Long teacherId = user.getId();
            
            AIGradingResponse response = aiGradingService.modifyGrading(gradingResultId, newScore, newFeedback, teacherId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error modifying grading: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/high-plagiarism")
    @Operation(summary = "Get high plagiarism cases", description = "Get submissions with high plagiarism scores")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getHighPlagiarismCases(@RequestParam(defaultValue = "30.0") Double threshold) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<AIGradingResponse> response = aiGradingService.getHighPlagiarismCases(threshold, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting high plagiarism cases: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/low-confidence")
    @Operation(summary = "Get low confidence gradings", description = "Get AI gradings with low confidence scores")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getLowConfidenceGradings(@RequestParam(defaultValue = "70.0") Double threshold) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<AIGradingResponse> response = aiGradingService.getLowConfidenceGradings(threshold, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting low confidence gradings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get AI grading statistics", description = "Get overall AI grading statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getGradingStatistics() {
        try {
            Long ownerId = getCorrectOwnerId();
            Map<String, Object> response = aiGradingService.getGradingStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting grading statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/regrade/{submissionId}")
    @Operation(summary = "Regrade a submission", description = "Regrade a submission with new parameters")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> regradeSubmission(@PathVariable Long submissionId, @Valid @RequestBody AIGradingRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
            AIGradingResponse response = aiGradingService.regradeSubmission(submissionId, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error regrading submission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/batch-grade")
    @Operation(summary = "Batch grade submissions", description = "Grade multiple submissions at once")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> batchGradeSubmissions(
            @RequestParam List<Long> submissionIds,
            @RequestParam(required = false) Long rubricId) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<AIGradingResponse> response = aiGradingService.batchGradeSubmissions(submissionIds, rubricId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error batch grading submissions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ==================== RUBRIC MANAGEMENT ENDPOINTS ====================

    @PostMapping("/rubrics")
    @Operation(summary = "Create rubric", description = "Create a new grading rubric")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> createRubric(@Valid @RequestBody RubricRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
            RubricResponse rubric = rubricService.createRubric(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(rubric);
        } catch (Exception e) {
            log.error("Error creating rubric: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/rubrics/{id}")
    @Operation(summary = "Get rubric by ID", description = "Retrieve a specific rubric")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getRubricById(@PathVariable Long id) {
        try {
            Long ownerId = getCorrectOwnerId();
            RubricResponse rubric = rubricService.getRubricById(id, ownerId);
            return ResponseEntity.ok(rubric);
        } catch (Exception e) {
            log.error("Error getting rubric: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/rubrics")
    @Operation(summary = "Get all rubrics", description = "Get all rubrics for the school")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getAllRubrics() {
        try {
            Long ownerId = getCorrectOwnerId();
            List<RubricResponse> rubrics = rubricService.getAllRubrics(ownerId);
            return ResponseEntity.ok(rubrics);
        } catch (Exception e) {
            log.error("Error getting rubrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/rubrics/subject/{subjectId}")
    @Operation(summary = "Get rubrics by subject", description = "Get all rubrics for a specific subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getRubricsBySubject(@PathVariable Long subjectId) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<RubricResponse> rubrics = rubricService.getRubricsBySubject(subjectId, ownerId);
            return ResponseEntity.ok(rubrics);
        } catch (Exception e) {
            log.error("Error getting rubrics by subject: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/rubrics/type/{rubricType}")
    @Operation(summary = "Get rubrics by type", description = "Get all rubrics of a specific type")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getRubricsByType(@PathVariable String rubricType) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<RubricResponse> rubrics = rubricService.getRubricsByType(rubricType, ownerId);
            return ResponseEntity.ok(rubrics);
        } catch (Exception e) {
            log.error("Error getting rubrics by type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/rubrics/{id}")
    @Operation(summary = "Update rubric", description = "Update an existing rubric")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> updateRubric(@PathVariable Long id, @Valid @RequestBody RubricRequest request) {
        try {
            Long ownerId = getCorrectOwnerId();
            RubricResponse rubric = rubricService.updateRubric(id, request, ownerId);
            return ResponseEntity.ok(rubric);
        } catch (Exception e) {
            log.error("Error updating rubric: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/rubrics/{id}")
    @Operation(summary = "Delete rubric", description = "Delete a rubric")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> deleteRubric(@PathVariable Long id) {
        try {
            Long ownerId = getCorrectOwnerId();
            rubricService.deleteRubric(id, ownerId);
            return ResponseEntity.ok("Rubric deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting rubric: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/rubrics/search")
    @Operation(summary = "Search rubrics", description = "Search rubrics by name")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> searchRubrics(@RequestParam String keyword) {
        try {
            Long ownerId = getCorrectOwnerId();
            List<RubricResponse> rubrics = rubricService.searchRubrics(keyword, ownerId);
            return ResponseEntity.ok(rubrics);
        } catch (Exception e) {
            log.error("Error searching rubrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get the correct owner ID based on the logged-in user
     */
    private Long getCorrectOwnerId() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long userId = loggedInUser.getId();
        
        // Check if the logged-in user is a worker
        Worker worker = workerRepository.findById(userId).orElse(null);
        if (worker != null && worker.getOwner() != null) {
            Long ownerId = worker.getOwner().getId();
            log.info("Logged-in user is worker ID: {}, returning owner ID: {}", userId, ownerId);
            return ownerId;
        }
        
        // User is a direct owner, return their own ID
        log.info("Logged-in user is direct owner ID: {}", userId);
        return userId;
    }
}
