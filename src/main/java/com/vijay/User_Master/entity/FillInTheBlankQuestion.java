package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Fill in the Blank Question entity
 */
@Entity
@DiscriminatorValue("FILL_IN_BLANK")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FillInTheBlankQuestion extends Question {

    @Column(name = "question_template", columnDefinition = "TEXT")
    private String questionTemplate; // "The capital of France is ___."

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "fill_blank_accepted_answers",
                     joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "accepted_answer", columnDefinition = "TEXT")
    private List<String> acceptedAnswers = new ArrayList<>();

    @Column(name = "case_sensitive")
    private Boolean caseSensitive = false;

    @Column(name = "exact_match")
    private Boolean exactMatch = false; // If false, allow fuzzy matching

    @Column(name = "blank_count")
    private Integer blankCount = 1; // Number of blanks in question

    public FillInTheBlankQuestion(Long id, String questionText, QuestionType questionType,
                                  DifficultyLevel difficulty, Subject subject, SchoolClass schoolClass,
                                  String chapter, String topic, String explanation, Double points,
                                  String hints, Boolean isActive, List<QuestionTag> tags,
                                  Worker createdByWorker, User owner, Boolean autoGradable,
                                  Boolean allowPartialCredit, Integer timeLimitSeconds,
                                  BloomsLevel bloomsLevel, String imageUrl, String videoUrl,
                                  String audioUrl, Integer timesUsed, Double averageScore,
                                  Boolean isDeleted, String questionTemplate, List<String> acceptedAnswers,
                                  Boolean caseSensitive, Boolean exactMatch, Integer blankCount) {
        super(id, questionText, questionType, difficulty, subject, schoolClass, chapter, topic,
              explanation, points, hints, isActive, tags, createdByWorker, owner, autoGradable,
              allowPartialCredit, timeLimitSeconds, bloomsLevel, imageUrl, videoUrl, audioUrl,
              timesUsed, averageScore, isDeleted);
        this.questionTemplate = questionTemplate;
        this.acceptedAnswers = acceptedAnswers != null ? acceptedAnswers : new ArrayList<>();
        this.caseSensitive = caseSensitive != null ? caseSensitive : false;
        this.exactMatch = exactMatch != null ? exactMatch : false;
        this.blankCount = blankCount != null ? blankCount : 1;
    }
}
