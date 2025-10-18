package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.PeerReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PeerReviewService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PeerReviewServiceImpl implements PeerReviewService {

    private final PeerReviewRepository peerReviewRepository;
    private final AssignmentRepository assignmentRepository;
    private final HomeworkSubmissionRepository homeworkSubmissionRepository;
    private final WorkerRepository workerRepository;
    private final RubricRepository rubricRepository;
    private final UserRepository userRepository;

    @Override
    public PeerReviewAssignmentResponse assignPeerReviews(PeerReviewAssignmentRequest request, Long ownerId) {
        log.info("Assigning peer reviews for assignment: {}", request.getAssignmentId());
        
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Rubric rubric = null;
        if (request.getRubricId() != null) {
            rubric = rubricRepository.findById(request.getRubricId()).orElse(null);
        }

        List<HomeworkSubmission> submissions = homeworkSubmissionRepository.findByAssignment_IdAndIsDeletedFalse(request.getAssignmentId());
        List<PeerReviewAssignmentResponse.ReviewAssignmentDetail> assignments = new ArrayList<>();
        int totalReviewsCreated = 0;

        if (request.getRandomAssignment()) {
            // Random assignment algorithm
            for (HomeworkSubmission submission : submissions) {
                List<Worker> availableReviewers = getAvailableReviewers(submissions, submission, request.getAllowSelfReview());
                Collections.shuffle(availableReviewers);
                
                int reviewsToCreate = Math.min(request.getReviewsPerSubmission(), availableReviewers.size());
                
                for (int i = 0; i < reviewsToCreate; i++) {
                    Worker reviewer = availableReviewers.get(i);
                    PeerReview peerReview = createPeerReview(submission, reviewer, rubric, owner, request.getAnonymousReview());
                    PeerReview saved = peerReviewRepository.save(peerReview);
                    
                    assignments.add(PeerReviewAssignmentResponse.ReviewAssignmentDetail.builder()
                            .peerReviewId(saved.getId())
                            .submissionId(submission.getId())
                            .submittedByName(submission.getStudent().getName())
                            .reviewerId(reviewer.getId())
                            .reviewerName(reviewer.getName())
                            .isAnonymous(request.getAnonymousReview())
                            .build());
                    
                    totalReviewsCreated++;
                }
            }
        } else {
            // Manual assignment
            if (request.getManualAssignments() != null) {
                for (PeerReviewAssignmentRequest.ReviewAssignment manual : request.getManualAssignments()) {
                    HomeworkSubmission submission = homeworkSubmissionRepository.findById(manual.getSubmissionId())
                            .orElseThrow(() -> new RuntimeException("Submission not found"));
                    Worker reviewer = workerRepository.findById(manual.getReviewerId())
                            .orElseThrow(() -> new RuntimeException("Reviewer not found"));
                    
                    PeerReview peerReview = createPeerReview(submission, reviewer, rubric, owner, request.getAnonymousReview());
                    PeerReview saved = peerReviewRepository.save(peerReview);
                    
                    assignments.add(PeerReviewAssignmentResponse.ReviewAssignmentDetail.builder()
                            .peerReviewId(saved.getId())
                            .submissionId(submission.getId())
                            .submittedByName(submission.getStudent().getName())
                            .reviewerId(reviewer.getId())
                            .reviewerName(reviewer.getName())
                            .isAnonymous(request.getAnonymousReview())
                            .build());
                    
                    totalReviewsCreated++;
                }
            }
        }

        return PeerReviewAssignmentResponse.builder()
                .assignmentId(assignment.getId())
                .assignmentTitle(assignment.getTitle())
                .totalSubmissions(submissions.size())
                .totalReviewsCreated(totalReviewsCreated)
                .reviewsPerSubmission(request.getReviewsPerSubmission())
                .assignments(assignments)
                .build();
    }

    @Override
    public PeerReviewResponse submitPeerReview(PeerReviewRequest request, Long reviewerId, Long ownerId) {
        log.info("Submitting peer review by reviewer: {}", reviewerId);
        
        HomeworkSubmission submission = homeworkSubmissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        Worker reviewer = workerRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        // Check if review already exists
        if (peerReviewRepository.existsBySubmissionIdAndReviewerIdAndIsDeletedFalse(request.getSubmissionId(), reviewerId)) {
            throw new RuntimeException("Review already submitted for this submission");
        }

        Rubric rubric = null;
        if (request.getRubricId() != null) {
            rubric = rubricRepository.findById(request.getRubricId()).orElse(null);
        }

        PeerReview peerReview = PeerReview.builder()
                .assignment(submission.getAssignment())
                .submission(submission)
                .reviewer(reviewer)
                .rubric(rubric)
                .reviewComments(request.getReviewComments())
                .overallScore(request.getOverallScore())
                .status(ReviewStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .timeSpentMinutes(request.getTimeSpentMinutes())
                .isAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false)
                .owner(owner)
                .build();

        // Add criterion scores
        if (request.getCriterionScores() != null) {
            for (PeerReviewRequest.CriterionScoreRequest scoreReq : request.getCriterionScores()) {
                RubricCriterion criterion = rubric != null ? 
                        rubric.getCriteria().stream()
                                .filter(c -> c.getId().equals(scoreReq.getCriterionId()))
                                .findFirst().orElse(null) : null;

                if (criterion != null) {
                    PeerReviewCriterionScore score = PeerReviewCriterionScore.builder()
                            .peerReview(peerReview)
                            .criterion(criterion)
                            .score(scoreReq.getScore())
                            .comments(scoreReq.getComments())
                            .build();
                    peerReview.addCriterionScore(score);
                }
            }
        }

        peerReview.calculateOverallScore();

        PeerReview saved = peerReviewRepository.save(peerReview);
        log.info("Peer review submitted with ID: {}", saved.getId());
        
        return mapToResponse(saved);
    }

    @Override
    public PeerReviewResponse updatePeerReview(Long id, PeerReviewRequest request, Long reviewerId, Long ownerId) {
        PeerReview peerReview = peerReviewRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Peer review not found"));

        if (!peerReview.getReviewer().getId().equals(reviewerId)) {
            throw new RuntimeException("Unauthorized to update this review");
        }

        if (peerReview.getStatus() == ReviewStatus.APPROVED) {
            throw new RuntimeException("Cannot update approved review");
        }

        peerReview.setReviewComments(request.getReviewComments());
        peerReview.setOverallScore(request.getOverallScore());
        peerReview.setStatus(ReviewStatus.REVISED);

        PeerReview updated = peerReviewRepository.save(peerReview);
        return mapToResponse(updated);
    }

    @Override
    public PeerReviewResponse getPeerReviewById(Long id, Long ownerId) {
        PeerReview peerReview = peerReviewRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Peer review not found"));
        return mapToResponse(peerReview);
    }

    @Override
    public List<PeerReviewResponse> getPeerReviewsByAssignment(Long assignmentId, Long ownerId) {
        return peerReviewRepository.findByAssignmentIdAndIsDeletedFalse(assignmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PeerReviewResponse> getPeerReviewsBySubmission(Long submissionId, Long ownerId) {
        return peerReviewRepository.findBySubmissionIdAndIsDeletedFalse(submissionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PeerReviewResponse> getPeerReviewsByReviewer(Long reviewerId, Long ownerId) {
        return peerReviewRepository.findByReviewerIdAndIsDeletedFalse(reviewerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PeerReviewResponse> getPendingReviews(Long reviewerId, Long ownerId) {
        return peerReviewRepository.findPendingReviewsByReviewer(reviewerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PeerReviewResponse approvePeerReview(PeerReviewApprovalRequest request, Long teacherId, Long ownerId) {
        PeerReview peerReview = peerReviewRepository.findByIdAndOwnerIdAndIsDeletedFalse(request.getPeerReviewId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Peer review not found"));

        Worker teacher = workerRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        peerReview.setStatus(request.getApproved() ? ReviewStatus.APPROVED : ReviewStatus.REJECTED);
        peerReview.setApprovedBy(teacher);
        peerReview.setApprovedAt(LocalDateTime.now());
        peerReview.setTeacherComments(request.getTeacherComments());

        if (request.getAdjustedScore() != null) {
            peerReview.setOverallScore(request.getAdjustedScore());
        }

        PeerReview updated = peerReviewRepository.save(peerReview);
        log.info("Peer review {} by teacher: {}", request.getApproved() ? "approved" : "rejected", teacherId);
        
        return mapToResponse(updated);
    }

    @Override
    public PeerReviewStatisticsResponse getPeerReviewStatistics(Long assignmentId, Long ownerId) {
        List<PeerReview> reviews = peerReviewRepository.findByAssignmentIdAndIsDeletedFalse(assignmentId);
        
        long totalReviews = reviews.size();
        long completedReviews = reviews.stream().filter(r -> r.getStatus() == ReviewStatus.SUBMITTED || r.getStatus() == ReviewStatus.APPROVED).count();
        long pendingReviews = reviews.stream().filter(r -> r.getStatus() == ReviewStatus.PENDING).count();
        long approvedReviews = reviews.stream().filter(r -> r.getStatus() == ReviewStatus.APPROVED).count();

        Assignment assignment = reviews.isEmpty() ? null : reviews.get(0).getAssignment();
        
        return PeerReviewStatisticsResponse.builder()
                .assignmentId(assignmentId)
                .assignmentTitle(assignment != null ? assignment.getTitle() : "")
                .totalReviewsAssigned((int) totalReviews)
                .totalReviewsCompleted((int) completedReviews)
                .totalReviewsPending((int) pendingReviews)
                .totalReviewsApproved((int) approvedReviews)
                .completionRate(totalReviews > 0 ? (double) completedReviews / totalReviews * 100 : 0.0)
                .approvalRate(completedReviews > 0 ? (double) approvedReviews / completedReviews * 100 : 0.0)
                .build();
    }

    @Override
    public void deletePeerReview(Long id, Long ownerId) {
        PeerReview peerReview = peerReviewRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Peer review not found"));
        peerReview.setIsDeleted(true);
        peerReviewRepository.save(peerReview);
    }

    // Helper methods
    private PeerReview createPeerReview(HomeworkSubmission submission, Worker reviewer, Rubric rubric, User owner, Boolean isAnonymous) {
        return PeerReview.builder()
                .assignment(submission.getAssignment())
                .submission(submission)
                .reviewer(reviewer)
                .rubric(rubric)
                .status(ReviewStatus.PENDING)
                .isAnonymous(isAnonymous != null ? isAnonymous : false)
                .owner(owner)
                .build();
    }

    private List<Worker> getAvailableReviewers(List<HomeworkSubmission> submissions, HomeworkSubmission currentSubmission, Boolean allowSelfReview) {
        return submissions.stream()
                .map(HomeworkSubmission::getStudent)
                .filter(worker -> allowSelfReview || !worker.getId().equals(currentSubmission.getStudent().getId()))
                .collect(Collectors.toList());
    }

    private PeerReviewResponse mapToResponse(PeerReview peerReview) {
        return PeerReviewResponse.builder()
                .id(peerReview.getId())
                .assignmentId(peerReview.getAssignment().getId())
                .assignmentTitle(peerReview.getAssignment().getTitle())
                .submissionId(peerReview.getSubmission().getId())
                .reviewerId(peerReview.getReviewer().getId())
                .reviewerName(peerReview.getIsAnonymous() ? "Anonymous" : peerReview.getReviewer().getName())
                .isAnonymous(peerReview.getIsAnonymous())
                .rubricId(peerReview.getRubric() != null ? peerReview.getRubric().getId() : null)
                .rubricName(peerReview.getRubric() != null ? peerReview.getRubric().getName() : null)
                .reviewComments(peerReview.getReviewComments())
                .overallScore(peerReview.getOverallScore())
                .status(peerReview.getStatus())
                .submittedAt(peerReview.getSubmittedAt())
                .timeSpentMinutes(peerReview.getTimeSpentMinutes())
                .approvedById(peerReview.getApprovedBy() != null ? peerReview.getApprovedBy().getId() : null)
                .approvedByName(peerReview.getApprovedBy() != null ? peerReview.getApprovedBy().getName() : null)
                .approvedAt(peerReview.getApprovedAt())
                .teacherComments(peerReview.getTeacherComments())
                .build();
    }
}
