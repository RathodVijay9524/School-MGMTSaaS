package com.vijay.User_Master.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Quiz Response entity - student's answer to a quiz question
 */
@Entity
@Table(name = "quiz_responses", indexes = {
    @Index(name = "idx_quiz_response_attempt", columnList = "attempt_id"),
    @Index(name = "idx_quiz_response_question", columnList = "question_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class QuizResponse extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonBackReference
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "student_answer", columnDefinition = "TEXT")
    private String studentAnswer; // JSON or text depending on question type

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "points_earned")
    private Double pointsEarned;

    @Column(name = "max_points")
    private Double maxPoints;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    // For auto-grading
    @Column(name = "auto_graded")
    @Builder.Default
    private Boolean autoGraded = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by_id")
    private Worker gradedBy; // For manual grading

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "answer_order")
    private Integer answerOrder; // Order in which question was answered

    @Column(name = "is_flagged")
    @Builder.Default
    private Boolean isFlagged = false; // Student flagged for review

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Helper methods
    public void calculatePartialCredit() {
        if (maxPoints != null && maxPoints > 0) {
            if (isCorrect != null && isCorrect) {
                this.pointsEarned = maxPoints;
            } else if (pointsEarned == null) {
                this.pointsEarned = 0.0;
            }
        }
    }

    public Double getPercentageScore() {
        if (maxPoints != null && maxPoints > 0 && pointsEarned != null) {
            return (pointsEarned / maxPoints) * 100.0;
        }
        return 0.0;
    }
}
