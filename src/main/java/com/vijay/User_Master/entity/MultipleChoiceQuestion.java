package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Choice Question entity
 */
@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MultipleChoiceQuestion extends Question {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<QuestionOption> options = new ArrayList<>();

    @Column(name = "allow_multiple_answers")
    private Boolean allowMultipleAnswers = false;

    @Column(name = "randomize_options")
    private Boolean randomizeOptions = true;

    @Column(name = "min_selections")
    private Integer minSelections; // For multiple answer questions

    @Column(name = "max_selections")
    private Integer maxSelections; // For multiple answer questions

    public MultipleChoiceQuestion(Long id, String questionText, QuestionType questionType, 
                                  DifficultyLevel difficulty, Subject subject, SchoolClass schoolClass,
                                  String chapter, String topic, String explanation, Double points,
                                  String hints, Boolean isActive, List<QuestionTag> tags, 
                                  Worker createdByWorker, User owner, Boolean autoGradable,
                                  Boolean allowPartialCredit, Integer timeLimitSeconds, 
                                  BloomsLevel bloomsLevel, String imageUrl, String videoUrl,
                                  String audioUrl, Integer timesUsed, Double averageScore,
                                  Boolean isDeleted, List<QuestionOption> options,
                                  Boolean allowMultipleAnswers, Boolean randomizeOptions,
                                  Integer minSelections, Integer maxSelections) {
        super(id, questionText, questionType, difficulty, subject, schoolClass, chapter, topic,
              explanation, points, hints, isActive, tags, createdByWorker, owner, autoGradable,
              allowPartialCredit, timeLimitSeconds, bloomsLevel, imageUrl, videoUrl, audioUrl,
              timesUsed, averageScore, isDeleted);
        this.options = options != null ? options : new ArrayList<>();
        this.allowMultipleAnswers = allowMultipleAnswers != null ? allowMultipleAnswers : false;
        this.randomizeOptions = randomizeOptions != null ? randomizeOptions : true;
        this.minSelections = minSelections;
        this.maxSelections = maxSelections;
    }

    // Helper methods
    public void addOption(QuestionOption option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void removeOption(QuestionOption option) {
        options.remove(option);
        option.setQuestion(null);
    }

    public List<QuestionOption> getCorrectOptions() {
        return options.stream()
                .filter(QuestionOption::getIsCorrect)
                .toList();
    }

    public int getCorrectAnswerCount() {
        return (int) options.stream()
                .filter(QuestionOption::getIsCorrect)
                .count();
    }
}
