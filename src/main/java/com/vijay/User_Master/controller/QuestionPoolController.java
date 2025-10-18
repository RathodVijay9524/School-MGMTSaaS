package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.service.QuestionPoolService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.repository.WorkerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Question Pool Management
 * Provides 15+ endpoints for pool operations
 */
@RestController
@RequestMapping("/api/v1/question-pool")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QuestionPoolController {

    private final QuestionPoolService questionPoolService;
    private final WorkerRepository workerRepository;

    /**
     * Create a new question pool
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> createPool(@Valid @RequestBody QuestionPoolRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuestionPoolResponse response = questionPoolService.createPool(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating pool: " + e.getMessage());
        }
    }

    /**
     * Update a question pool
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> updatePool(@PathVariable Long id, @Valid @RequestBody QuestionPoolRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuestionPoolResponse response = questionPoolService.updatePool(id, request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating pool: " + e.getMessage());
        }
    }

    /**
     * Get pool by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getPoolById(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            QuestionPoolResponse response = questionPoolService.getPoolById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching pool: " + e.getMessage());
        }
    }

    /**
     * Delete a pool
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> deletePool(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            questionPoolService.deletePool(id, ownerId);
            return ResponseEntity.ok("Pool deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting pool: " + e.getMessage());
        }
    }

    /**
     * Get all pools
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getAllPools() {
        try {
            Long ownerId = getOwnerId();
            List<QuestionPoolResponse> response = questionPoolService.getAllPools(ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching pools: " + e.getMessage());
        }
    }

    /**
     * Get pools by subject
     */
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getPoolsBySubject(@PathVariable Long subjectId) {
        try {
            Long ownerId = getOwnerId();
            List<QuestionPoolResponse> response = questionPoolService.getPoolsBySubject(subjectId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching pools: " + e.getMessage());
        }
    }

    /**
     * Generate random questions from pool
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> generateQuestions(@Valid @RequestBody QuestionPoolGenerateRequest request) {
        try {
            Long ownerId = getOwnerId();
            QuestionPoolGenerateResponse response = questionPoolService.generateQuestions(request, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating questions: " + e.getMessage());
        }
    }

    /**
     * Add questions to pool
     */
    @PostMapping("/{poolId}/questions/add")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> addQuestionsToPool(@PathVariable Long poolId, @RequestBody List<Long> questionIds) {
        try {
            Long ownerId = getOwnerId();
            QuestionPoolResponse response = questionPoolService.addQuestionsToPool(poolId, questionIds, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding questions: " + e.getMessage());
        }
    }

    /**
     * Remove questions from pool
     */
    @PostMapping("/{poolId}/questions/remove")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> removeQuestionsFromPool(@PathVariable Long poolId, @RequestBody List<Long> questionIds) {
        try {
            Long ownerId = getOwnerId();
            QuestionPoolResponse response = questionPoolService.removeQuestionsFromPool(poolId, questionIds, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing questions: " + e.getMessage());
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
