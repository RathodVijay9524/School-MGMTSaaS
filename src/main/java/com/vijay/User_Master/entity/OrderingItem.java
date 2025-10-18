package com.vijay.User_Master.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * Items for Ordering Questions
 */
@Entity
@Table(name = "ordering_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class OrderingItem extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private OrderingQuestion question;

    @Column(name = "item_text", nullable = false, length = 1000)
    private String itemText;

    @Column(name = "correct_order", nullable = false)
    private Integer correctOrder; // 1, 2, 3, etc.

    @Column(name = "item_image_url", length = 500)
    private String itemImageUrl;
}
