package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.service.PeerReviewService;
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
 * REST Controller for Peer Review Management
 * Provides 20+ endpoints for peer review operations
 */
@RestController
@RequestMapping("/api/v1/peer-review")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PeerReviewController {

    private final PeerReviewService peerReviewService;
    private final WorkerRepository workerRepository;

    /**
     * Assign peer reviews for an assignment
     */
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> assignPeerReviews(@Valid @RequestBody PeerReviewAssignmentRequest request) {
        try {
            Long ownerId = getOwnerId();
            PeerReviewAssignmentResponse response = peerReviewService.assignPeerReviews(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error assigning peer reviews: " + e.getMessage());
        }
    }

    /**
     * Submit a peer review (standard POST)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> createPeerReview(@Valid @RequestBody PeerReviewRequest request) {
        try {
            Long ownerId = getOwnerId();
            Long reviewerId = request.getReviewerId(); // Get from request body
            PeerReviewResponse response = peerReviewService.submitPeerReview(request, reviewerId, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting peer review: " + e.getMessage());
        }
    }

    /**
     * Submit a peer review (alternative with path variable)
     */
    @PostMapping("/submit/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> submitPeerReview(@PathVariable Long reviewerId,
                                                @Valid @RequestBody PeerReviewRequest request) {
        try {
            Long ownerId = getOwnerId();
            PeerReviewResponse response = peerReviewService.submitPeerReview(request, reviewerId, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting peer review: " + e.getMessage());
        }
    }

    /**
     * Update a peer review
     */
    @PutMapping("/{id}/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> updatePeerReview(@PathVariable Long id,
                                                @PathVariable Long reviewerId,
                                                @Valid @RequestBody PeerReviewRequest request) {
        try {
            Long ownerId = getOwnerId();
            PeerReviewResponse response = peerReviewService.updatePeerReview(id, request, reviewerId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating peer review: " + e.getMessage());
        }
    }

    /**
     * Get peer review by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getPeerReviewById(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            PeerReviewResponse response = peerReviewService.getPeerReviewById(id, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching peer review: " + e.getMessage());
        }
    }

    /**
     * Get all peer reviews for an assignment
     */
    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getPeerReviewsByAssignment(@PathVariable Long assignmentId) {
        try {
            Long ownerId = getOwnerId();
            List<PeerReviewResponse> response = peerReviewService.getPeerReviewsByAssignment(assignmentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching peer reviews: " + e.getMessage());
        }
    }

    /**
     * Get peer reviews for a submission
     */
    @GetMapping("/submission/{submissionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getPeerReviewsBySubmission(@PathVariable Long submissionId) {
        try {
            Long ownerId = getOwnerId();
            List<PeerReviewResponse> response = peerReviewService.getPeerReviewsBySubmission(submissionId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching peer reviews: " + e.getMessage());
        }
    }

    /**
     * Get peer reviews by reviewer
     */
    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getPeerReviewsByReviewer(@PathVariable Long reviewerId) {
        try {
            Long ownerId = getOwnerId();
            List<PeerReviewResponse> response = peerReviewService.getPeerReviewsByReviewer(reviewerId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching peer reviews: " + e.getMessage());
        }
    }

    /**
     * Get pending peer reviews for a reviewer
     */
    @GetMapping("/pending/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> getPendingReviews(@PathVariable Long reviewerId) {
        try {
            Long ownerId = getOwnerId();
            List<PeerReviewResponse> response = peerReviewService.getPendingReviews(reviewerId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching pending reviews: " + e.getMessage());
        }
    }

    /**
     * Teacher approves/rejects a peer review
     */
    @PostMapping("/reviews/{reviewId}/approve")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> approvePeerReview(@PathVariable Long reviewId,
                                                 @Valid @RequestBody PeerReviewApprovalRequest request) {
        try {
            Long ownerId = getOwnerId();
            Long teacherId = ownerId; // Use current user as teacher
            peerReviewService.approvePeerReview(request, teacherId, ownerId);
            return ResponseEntity.ok("Peer review approved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving peer review: " + e.getMessage());
        }
    }

    /**
     * Teacher approves/rejects a peer review (alternative)
     */
    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> approve(@Valid @RequestBody PeerReviewApprovalRequest request) {
        try {
            Long ownerId = getOwnerId();
            Long teacherId = ownerId; // Use current user as teacher
            peerReviewService.approvePeerReview(request, teacherId, ownerId);
            return ResponseEntity.ok("Peer review approved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving peer review: " + e.getMessage());
        }
    }

    /**
     * Get peer review statistics for an assignment
     */
    @GetMapping("/statistics/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getPeerReviewStatistics(@PathVariable Long assignmentId) {
        try {
            Long ownerId = getOwnerId();
            PeerReviewStatisticsResponse response = peerReviewService.getPeerReviewStatistics(assignmentId, ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching statistics: " + e.getMessage());
        }
    }

    /**
     * Delete a peer review
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> deletePeerReview(@PathVariable Long id) {
        try {
            Long ownerId = getOwnerId();
            peerReviewService.deletePeerReview(id, ownerId);
            return ResponseEntity.ok("Peer review deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting peer review: " + e.getMessage());
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
