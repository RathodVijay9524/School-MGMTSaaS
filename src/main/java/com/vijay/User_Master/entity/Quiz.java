package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz entity for online assessments
 */
@Entity
@Table(name = "quizzes", indexes = {
    @Index(name = "idx_quiz_subject", columnList = "subject_id"),
    @Index(name = "idx_quiz_class", columnList = "class_id"),
    @Index(name = "idx_quiz_type", columnList = "quiz_type"),
    @Index(name = "idx_quiz_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Quiz extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 5000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", nullable = false)
    @Builder.Default
    private QuizType quizType = QuizType.PRACTICE;

    // Question Configuration
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "quiz_questions",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @Column(name = "randomize_questions")
    @Builder.Default
    private Boolean randomizeQuestions = false;

    @Column(name = "questions_to_show")
    private Integer questionsToShow; // If null, show all questions

    // Timing
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "show_timer")
    @Builder.Default
    private Boolean showTimer = true;

    // Attempts
    @Column(name = "max_attempts")
    @Builder.Default
    private Integer maxAttempts = 1;

    @Column(name = "allow_review")
    @Builder.Default
    private Boolean allowReview = true;

    // Grading
    @Column(name = "total_points")
    private Double totalPoints;

    @Column(name = "passing_score")
    private Double passingScore;

    @Column(name = "auto_grade")
    @Builder.Default
    private Boolean autoGrade = true;

    // Availability
    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_until")
    private LocalDateTime availableUntil;

    // Display Options
    @Column(name = "show_correct_answers")
    @Builder.Default
    private Boolean showCorrectAnswers = false;

    @Column(name = "show_score_immediately")
    @Builder.Default
    private Boolean showScoreImmediately = true;

    @Column(name = "show_feedback")
    @Builder.Default
    private Boolean showFeedback = true;

    @Column(name = "show_one_question_at_time")
    @Builder.Default
    private Boolean showOneQuestionAtTime = false;

    // Proctoring & Security
    @Column(name = "require_proctoring")
    @Builder.Default
    private Boolean requireProctoring = false;

    @Column(name = "prevent_copy_paste")
    @Builder.Default
    private Boolean preventCopyPaste = false;

    @Column(name = "full_screen_mode")
    @Builder.Default
    private Boolean fullScreenMode = false;

    @Column(name = "shuffle_answers")
    @Builder.Default
    private Boolean shuffleAnswers = true;

    @Column(name = "lock_questions_after_answering")
    @Builder.Default
    private Boolean lockQuestionsAfterAnswering = false;

    // Instructions
    @Column(length = 5000)
    private String instructions;

    // Status
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = false;

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

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        if (availableFrom != null && now.isBefore(availableFrom)) {
            return false;
        }
        if (availableUntil != null && now.isAfter(availableUntil)) {
            return false;
        }
        return isActive && isPublished;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public void calculateTotalPoints() {
        this.totalPoints = questions.stream()
                .mapToDouble(Question::getPoints)
                .sum();
    }
}
