package com.vijay.User_Master.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * Peer Review Criterion Score - Score for individual rubric criterion
 */
@Entity
@Table(name = "peer_review_criterion_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PeerReviewCriterionScore extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peer_review_id", nullable = false)
    @JsonBackReference
    private PeerReview peerReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterion_id", nullable = false)
    private RubricCriterion criterion; // Use existing RubricCriterion!

    @Column(nullable = false)
    private Double score;

    @Column(length = 2000)
    private String comments;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}
