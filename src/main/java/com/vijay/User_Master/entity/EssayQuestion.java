package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Essay Question entity - Long form text answers
 */
@Entity
@DiscriminatorValue("ESSAY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EssayQuestion extends Question {

    @Column(name = "min_words")
    private Integer minWords; // Minimum word count

    @Column(name = "max_words")
    private Integer maxWords; // Maximum word count

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer; // Sample/model answer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubric_id")
    private Rubric rubric; // Use existing Rubric for grading

    @Column(name = "use_ai_grading")
    private Boolean useAiGrading = true; // Use AI for grading

    @Column(name = "require_manual_review")
    private Boolean requireManualReview = true; // Require teacher review

    public EssayQuestion(Long id, String questionText, QuestionType questionType,
                         DifficultyLevel difficulty, Subject subject, SchoolClass schoolClass,
                         String chapter, String topic, String explanation, Double points,
                         String hints, Boolean isActive, List<QuestionTag> tags,
                         Worker createdByWorker, User owner, Boolean autoGradable,
                         Boolean allowPartialCredit, Integer timeLimitSeconds,
                         BloomsLevel bloomsLevel, String imageUrl, String videoUrl,
                         String audioUrl, Integer timesUsed, Double averageScore,
                         Boolean isDeleted, Integer minWords, Integer maxWords,
                         String sampleAnswer, Rubric rubric, Boolean useAiGrading,
                         Boolean requireManualReview) {
        super(id, questionText, questionType, difficulty, subject, schoolClass, chapter, topic,
              explanation, points, hints, isActive, tags, createdByWorker, owner, autoGradable,
              allowPartialCredit, timeLimitSeconds, bloomsLevel, imageUrl, videoUrl, audioUrl,
              timesUsed, averageScore, isDeleted);
        this.minWords = minWords;
        this.maxWords = maxWords;
        this.sampleAnswer = sampleAnswer;
        this.rubric = rubric;
        this.useAiGrading = useAiGrading != null ? useAiGrading : true;
        this.requireManualReview = requireManualReview != null ? requireManualReview : true;
    }
}
