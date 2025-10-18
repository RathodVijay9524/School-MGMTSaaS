package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionType;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.service.QuestionBankService;
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
 * REST Controller for Question Bank Management
 * Provides 30+ endpoints for comprehensive question management
 */
@RestController
@RequestMapping("/api/v1/question-bank")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QuestionBankController {

    private final QuestionBankService questionBankService;
    private final WorkerRepository workerRepository;

    /**
     * Create a new question
     */
    @PostMapping("/questions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuestionResponse response = questionBankService.createQuestion(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating question: " + e.getMessage());
        }
    }

    /**
     * Update an existing question
     */
    @PutMapping("/questions/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuestionResponse response = questionBankService.updateQuestion(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating question: " + e.getMessage());
        }
    }

    /**
     * Get question by ID
     */
    @GetMapping("/questions/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            QuestionResponse response = questionBankService.getQuestionById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching question: " + e.getMessage());
        }
    }

    /**
     * Delete question
     */
    @DeleteMapping("/questions/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            questionBankService.deleteQuestion(id, ownerId);
            return ResponseEntity.ok("Question deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting question: " + e.getMessage());
        }
    }

    /**
     * Get all questions
     */
    @GetMapping("/questions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getAllQuestions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        try {
            Long ownerId = getOwnerId();
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                Page<QuestionResponse> response = questionBankService.getQuestionsPaginated(ownerId, pageable);
                return ResponseEntity.ok(response);
            } else {
                List<QuestionResponse> response = questionBankService.getAllQuestions(ownerId);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching questions: " + e.getMessage());
        }
    }

    /**
     * Get questions by subject
     */
    @GetMapping("/questions/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuestionsBySubject(@PathVariable Long subjectId) {
        try {
            Long ownerId = getOwnerId();
            List<QuestionResponse> response = questionBankService.getQuestionsBySubject(subjectId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching questions: " + e.getMessage());
        }
    }

    /**
     * Get questions by class
     */
    @GetMapping("/questions/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuestionsByClass(@PathVariable Long classId) {
        try {
            Long ownerId = getOwnerId();
            List<QuestionResponse> response = questionBankService.getQuestionsByClass(classId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching questions: " + e.getMessage());
        }
    }

    /**
     * Get questions by type
     */
    @GetMapping("/questions/type/{questionType}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuestionsByType(@PathVariable QuestionType questionType) {
        try {
            Long ownerId = getOwnerId();
            List<QuestionResponse> response = questionBankService.getQuestionsByType(questionType, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching questions: " + e.getMessage());
        }
    }

    /**
     * Get questions by difficulty
     */
    @GetMapping("/questions/difficulty/{difficulty}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuestionsByDifficulty(@PathVariable DifficultyLevel difficulty) {
        try {
            Long ownerId = getOwnerId();
            List<QuestionResponse> response = questionBankService.getQuestionsByDifficulty(difficulty, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching questions: " + e.getMessage());
        }
    }

    /**
     * Search questions by keyword
     */
    @GetMapping("/questions/search")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> searchQuestions(@RequestParam String keyword) {
        try {
            Long ownerId = getOwnerId();
            List<QuestionResponse> response = questionBankService.searchQuestions(keyword, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching questions: " + e.getMessage());
        }
    }

    /**
     * Advanced search with multiple criteria
     */
    @PostMapping("/questions/advanced-search")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> advancedSearch(@RequestBody QuestionSearchRequest request) {
        try {
            Long ownerId = getOwnerId();
            Page<QuestionResponse> response = questionBankService.advancedSearch(request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching questions: " + e.getMessage());
        }
    }

    /**
     * Get questions by tags
     */
    @PostMapping("/questions/by-tags")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getQuestionsByTags(@RequestBody List<Long> tagIds) {
        try {
            Long ownerId = getOwnerId();
            List<QuestionResponse> response = questionBankService.getQuestionsByTags(tagIds, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching questions: " + e.getMessage());
        }
    }

    /**
     * Duplicate a question
     */
    @PostMapping("/questions/duplicate")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> duplicateQuestion(@Valid @RequestBody QuestionDuplicateRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuestionResponse response = questionBankService.duplicateQuestion(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error duplicating question: " + e.getMessage());
        }
    }

    /**
     * Bulk import questions
     */
    @PostMapping("/questions/bulk-import")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> bulkImportQuestions(@Valid @RequestBody BulkQuestionImportRequest request) {
        try {
            Long ownerId = getOwnerId();
            BulkQuestionImportResponse response = questionBankService.bulkImportQuestions(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing questions: " + e.getMessage());
        }
    }

    /**
     * Get question bank statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getStatistics() {
        try {
            Long ownerId = getOwnerId();
            QuestionBankStatisticsResponse response = questionBankService.getStatistics(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching statistics: " + e.getMessage());
        }
    }

    /**
     * Create a question tag
     */
    @PostMapping("/tags")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> createTag(@Valid @RequestBody QuestionTagRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuestionResponse.QuestionTagResponse response = questionBankService.createTag(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating tag: " + e.getMessage());
        }
    }

    /**
     * Get all tags
     */
    @GetMapping("/tags")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getAllTags() {
        try {
            Long ownerId = getOwnerId();
            List<QuestionResponse.QuestionTagResponse> response = questionBankService.getAllTags(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching tags: " + e.getMessage());
        }
    }

    /**
     * Get tag by ID
     */
    @GetMapping("/tags/{tagId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getTagById(@PathVariable Long tagId) {
        try {
            Long ownerId = getOwnerId();
            QuestionResponse.QuestionTagResponse response = questionBankService.getTagById(tagId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching tag: " + e.getMessage());
        }
    }

    /**
     * Add questions to a tag
     */
    @PostMapping("/tags/{tagId}/add-questions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> addQuestionsToTag(@PathVariable Long tagId, @RequestBody List<Long> questionIds) {
        try {
            Long ownerId = getOwnerId();
            questionBankService.addQuestionsToTag(tagId, questionIds, ownerId);
            return ResponseEntity.ok("Questions added to tag successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding questions to tag: " + e.getMessage());
        }
    }

    /**
     * Delete a tag
     */
    @DeleteMapping("/tags/{tagId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
        try {
            Long ownerId = getOwnerId();
            questionBankService.deleteTag(tagId, ownerId);
            return ResponseEntity.ok("Tag deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting tag: " + e.getMessage());
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
