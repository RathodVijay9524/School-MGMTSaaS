package com.vijay.User_Master.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * Matching pairs for Matching Questions
 */
@Entity
@Table(name = "matching_pairs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MatchingPair extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private MatchingQuestion question;

    @Column(name = "left_item", nullable = false, length = 1000)
    private String leftItem;

    @Column(name = "right_item", nullable = false, length = 1000)
    private String rightItem;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "left_image_url", length = 500)
    private String leftImageUrl;

    @Column(name = "right_image_url", length = 500)
    private String rightImageUrl;
}
