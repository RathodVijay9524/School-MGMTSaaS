package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Short Answer Question entity
 */
@Entity
@DiscriminatorValue("SHORT_ANSWER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShortAnswerQuestion extends Question {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "short_answer_accepted_answers", 
                     joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "accepted_answer", columnDefinition = "TEXT")
    private List<String> acceptedAnswers = new ArrayList<>();

    @Column(name = "case_sensitive")
    private Boolean caseSensitive = false;

    @Column(name = "exact_match")
    private Boolean exactMatch = false; // If false, allow fuzzy matching

    @Column(name = "max_length")
    private Integer maxLength; // Maximum character length

    @Column(name = "min_length")
    private Integer minLength; // Minimum character length

    @Column(name = "use_ai_grading")
    private Boolean useAiGrading = false; // Use AI for grading

    public ShortAnswerQuestion(Long id, String questionText, QuestionType questionType,
                               DifficultyLevel difficulty, Subject subject, SchoolClass schoolClass,
                               String chapter, String topic, String explanation, Double points,
                               String hints, Boolean isActive, List<QuestionTag> tags,
                               Worker createdByWorker, User owner, Boolean autoGradable,
                               Boolean allowPartialCredit, Integer timeLimitSeconds,
                               BloomsLevel bloomsLevel, String imageUrl, String videoUrl,
                               String audioUrl, Integer timesUsed, Double averageScore,
                               Boolean isDeleted, List<String> acceptedAnswers, Boolean caseSensitive,
                               Boolean exactMatch, Integer maxLength, Integer minLength,
                               Boolean useAiGrading) {
        super(id, questionText, questionType, difficulty, subject, schoolClass, chapter, topic,
              explanation, points, hints, isActive, tags, createdByWorker, owner, autoGradable,
              allowPartialCredit, timeLimitSeconds, bloomsLevel, imageUrl, videoUrl, audioUrl,
              timesUsed, averageScore, isDeleted);
        this.acceptedAnswers = acceptedAnswers != null ? acceptedAnswers : new ArrayList<>();
        this.caseSensitive = caseSensitive != null ? caseSensitive : false;
        this.exactMatch = exactMatch != null ? exactMatch : false;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.useAiGrading = useAiGrading != null ? useAiGrading : false;
    }
}
