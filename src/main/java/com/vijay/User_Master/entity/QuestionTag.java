package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tags for categorizing questions
 */
@Entity
@Table(name = "question_tags", indexes = {
    @Index(name = "idx_tag_name", columnList = "tag_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class QuestionTag extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", nullable = false, unique = true, length = 100)
    private String tagName;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}
