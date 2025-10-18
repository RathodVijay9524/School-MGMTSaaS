package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Matching Question entity - Match items from two lists
 */
@Entity
@DiscriminatorValue("MATCHING")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MatchingQuestion extends Question {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<MatchingPair> pairs = new ArrayList<>();

    @Column(name = "randomize_left")
    private Boolean randomizeLeft = true;

    @Column(name = "randomize_right")
    private Boolean randomizeRight = true;

    public MatchingQuestion(Long id, String questionText, QuestionType questionType,
                            DifficultyLevel difficulty, Subject subject, SchoolClass schoolClass,
                            String chapter, String topic, String explanation, Double points,
                            String hints, Boolean isActive, List<QuestionTag> tags,
                            Worker createdByWorker, User owner, Boolean autoGradable,
                            Boolean allowPartialCredit, Integer timeLimitSeconds,
                            BloomsLevel bloomsLevel, String imageUrl, String videoUrl,
                            String audioUrl, Integer timesUsed, Double averageScore,
                            Boolean isDeleted, List<MatchingPair> pairs, Boolean randomizeLeft,
                            Boolean randomizeRight) {
        super(id, questionText, questionType, difficulty, subject, schoolClass, chapter, topic,
              explanation, points, hints, isActive, tags, createdByWorker, owner, autoGradable,
              allowPartialCredit, timeLimitSeconds, bloomsLevel, imageUrl, videoUrl, audioUrl,
              timesUsed, averageScore, isDeleted);
        this.pairs = pairs != null ? pairs : new ArrayList<>();
        this.randomizeLeft = randomizeLeft != null ? randomizeLeft : true;
        this.randomizeRight = randomizeRight != null ? randomizeRight : true;
    }

    public void addPair(MatchingPair pair) {
        pairs.add(pair);
        pair.setQuestion(this);
    }
}
