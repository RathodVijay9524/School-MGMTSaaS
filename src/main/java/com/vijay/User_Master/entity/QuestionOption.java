package com.vijay.User_Master.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * Options for Multiple Choice Questions
 */
@Entity
@Table(name = "question_options", indexes = {
    @Index(name = "idx_question_option", columnList = "question_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class QuestionOption extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private Question question;

    @Column(nullable = false, length = 2000)
    private String optionText;

    @Column(name = "is_correct")
    @Builder.Default
    private Boolean isCorrect = false;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "partial_credit_percentage")
    private Double partialCreditPercentage; // For partial credit (0-100)

    @Column(length = 1000)
    private String feedback; // Feedback for selecting this option

    @Column(name = "option_image_url", length = 500)
    private String optionImageUrl; // Image for this option

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}
