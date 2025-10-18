package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Peer Review entity - Student reviews another student's work
 */
@Entity
@Table(name = "peer_reviews", indexes = {
    @Index(name = "idx_peer_review_assignment", columnList = "assignment_id"),
    @Index(name = "idx_peer_review_submission", columnList = "submission_id"),
    @Index(name = "idx_peer_review_reviewer", columnList = "reviewer_id"),
    @Index(name = "idx_peer_review_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PeerReview extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private HomeworkSubmission submission; // Submission being reviewed

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Worker reviewer; // Student doing the review

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubric_id")
    private Rubric rubric; // Use existing Rubric entity!

    @Column(name = "review_comments", length = 10000)
    private String reviewComments;

    @Column(name = "overall_score")
    private Double overallScore;

    @OneToMany(mappedBy = "peerReview", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PeerReviewCriterionScore> criterionScores = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "time_spent_minutes")
    @Builder.Default
    private Integer timeSpentMinutes = 0;

    // Teacher oversight
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private Worker approvedBy; // Teacher who approved

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "teacher_comments", length = 5000)
    private String teacherComments;

    @Column(name = "is_anonymous")
    @Builder.Default
    private Boolean isAnonymous = false; // Hide reviewer identity from reviewee

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Helper methods
    public void addCriterionScore(PeerReviewCriterionScore score) {
        criterionScores.add(score);
        score.setPeerReview(this);
    }

    public void calculateOverallScore() {
        if (criterionScores != null && !criterionScores.isEmpty()) {
            this.overallScore = criterionScores.stream()
                    .mapToDouble(PeerReviewCriterionScore::getScore)
                    .average()
                    .orElse(0.0);
        }
    }

    public boolean isCompleted() {
        return status == ReviewStatus.SUBMITTED || 
               status == ReviewStatus.APPROVED;
    }
}
