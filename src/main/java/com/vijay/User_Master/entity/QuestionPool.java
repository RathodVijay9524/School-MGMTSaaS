package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Question Pool entity for random question selection
 */
@Entity
@Table(name = "question_pools", indexes = {
    @Index(name = "idx_pool_subject", columnList = "subject_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class QuestionPool extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pool_name", nullable = false, length = 500)
    private String poolName;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pool_questions",
        joinColumns = @JoinColumn(name = "pool_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pool_tags",
        joinColumns = @JoinColumn(name = "pool_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<QuestionTag> tags = new ArrayList<>();

    // Pool Configuration
    @Column(name = "questions_to_select")
    private Integer questionsToSelect; // Random selection count

    @Enumerated(EnumType.STRING)
    @Column(name = "target_difficulty")
    private DifficultyLevel targetDifficulty;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Worker createdByWorker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Helper methods
    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }

    public int getQuestionCount() {
        return questions.size();
    }
}
