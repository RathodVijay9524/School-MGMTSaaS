package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordering Question entity - Arrange items in correct order
 */
@Entity
@DiscriminatorValue("ORDERING")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderingQuestion extends Question {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("correctOrder ASC")
    private List<OrderingItem> items = new ArrayList<>();

    @Column(name = "allow_partial_order")
    private Boolean allowPartialOrder = false; // Partial credit for partially correct order

    public OrderingQuestion(Long id, String questionText, QuestionType questionType,
                            DifficultyLevel difficulty, Subject subject, SchoolClass schoolClass,
                            String chapter, String topic, String explanation, Double points,
                            String hints, Boolean isActive, List<QuestionTag> tags,
                            Worker createdByWorker, User owner, Boolean autoGradable,
                            Boolean allowPartialCredit, Integer timeLimitSeconds,
                            BloomsLevel bloomsLevel, String imageUrl, String videoUrl,
                            String audioUrl, Integer timesUsed, Double averageScore,
                            Boolean isDeleted, List<OrderingItem> items, Boolean allowPartialOrder) {
        super(id, questionText, questionType, difficulty, subject, schoolClass, chapter, topic,
              explanation, points, hints, isActive, tags, createdByWorker, owner, autoGradable,
              allowPartialCredit, timeLimitSeconds, bloomsLevel, imageUrl, videoUrl, audioUrl,
              timesUsed, averageScore, isDeleted);
        this.items = items != null ? items : new ArrayList<>();
        this.allowPartialOrder = allowPartialOrder != null ? allowPartialOrder : false;
    }

    public void addItem(OrderingItem item) {
        items.add(item);
        item.setQuestion(this);
    }
}
