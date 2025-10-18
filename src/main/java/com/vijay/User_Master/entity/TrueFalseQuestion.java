package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * True/False Question entity
 */
@Entity
@DiscriminatorValue("TRUE_FALSE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrueFalseQuestion extends Question {

    @Column(name = "correct_answer", nullable = false)
    private Boolean correctAnswer;

    @Column(name = "true_feedback", columnDefinition = "TEXT")
    private String trueFeedback; // Feedback if student selects True

    @Column(name = "false_feedback", columnDefinition = "TEXT")
    private String falseFeedback; // Feedback if student selects False

    public TrueFalseQuestion(Long id, String questionText, QuestionType questionType,
                             DifficultyLevel difficulty, Subject subject, SchoolClass schoolClass,
                             String chapter, String topic, String explanation, Double points,
                             String hints, Boolean isActive, List<QuestionTag> tags,
                             Worker createdByWorker, User owner, Boolean autoGradable,
                             Boolean allowPartialCredit, Integer timeLimitSeconds,
                             BloomsLevel bloomsLevel, String imageUrl, String videoUrl,
                             String audioUrl, Integer timesUsed, Double averageScore,
                             Boolean isDeleted, Boolean correctAnswer, String trueFeedback,
                             String falseFeedback) {
        super(id, questionText, questionType, difficulty, subject, schoolClass, chapter, topic,
              explanation, points, hints, isActive, tags, createdByWorker, owner, autoGradable,
              allowPartialCredit, timeLimitSeconds, bloomsLevel, imageUrl, videoUrl, audioUrl,
              timesUsed, averageScore, isDeleted);
        this.correctAnswer = correctAnswer;
        this.trueFeedback = trueFeedback;
        this.falseFeedback = falseFeedback;
    }
}
