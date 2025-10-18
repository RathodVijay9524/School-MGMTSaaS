package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Question entity for Question Bank System
 * Uses Single Table Inheritance for different question types
 */
@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_question_type", columnList = "question_type"),
    @Index(name = "idx_subject", columnList = "subject_id"),
    @Index(name = "idx_difficulty", columnList = "difficulty"),
    @Index(name = "idx_active", columnList = "is_active")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type_discriminator", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class Question extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @Column(length = 200)
    private String chapter;

    @Column(length = 200)
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String explanation; // Explanation for correct answer

    @Column(nullable = false)
    private Double points = 1.0; // Marks for this question

    @Column(columnDefinition = "TEXT")
    private String hints;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "question_tags",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<QuestionTag> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Worker createdByWorker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Auto-grading support
    @Column(name = "auto_gradable")
    private Boolean autoGradable = false;

    // Partial credit support
    @Column(name = "allow_partial_credit")
    private Boolean allowPartialCredit = false;

    // Time limit per question (optional, in seconds)
    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    // Bloom's Taxonomy level
    @Enumerated(EnumType.STRING)
    @Column(name = "blooms_level")
    private BloomsLevel bloomsLevel;

    // Image/Media support
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    // Usage statistics
    @Column(name = "times_used")
    private Integer timesUsed = 0;

    @Column(name = "average_score")
    private Double averageScore;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Helper methods
    public void incrementTimesUsed() {
        this.timesUsed++;
    }

    public void updateAverageScore(Double newScore) {
        if (this.averageScore == null) {
            this.averageScore = newScore;
        } else {
            // Calculate running average
            this.averageScore = (this.averageScore * (this.timesUsed - 1) + newScore) / this.timesUsed;
        }
    }

    public boolean isObjectiveType() {
        return questionType == QuestionType.MULTIPLE_CHOICE ||
               questionType == QuestionType.TRUE_FALSE ||
               questionType == QuestionType.MATCHING ||
               questionType == QuestionType.ORDERING ||
               questionType == QuestionType.FILL_IN_BLANK;
    }

    public boolean isSubjectiveType() {
        return questionType == QuestionType.SHORT_ANSWER ||
               questionType == QuestionType.ESSAY ||
               questionType == QuestionType.FILE_UPLOAD;
    }
}
