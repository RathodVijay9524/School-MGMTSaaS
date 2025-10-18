package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.BloomsLevel;
import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for question responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private Long id;
    private String questionText;
    private QuestionType questionType;
    private DifficultyLevel difficulty;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private String chapter;
    private String topic;
    private String explanation;
    private Double points;
    private String hints;
    private Boolean isActive;
    private List<QuestionTagResponse> tags;
    private Boolean autoGradable;
    private Boolean allowPartialCredit;
    private Integer timeLimitSeconds;
    private BloomsLevel bloomsLevel;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private Integer timesUsed;
    private Double averageScore;

    // For Multiple Choice Questions
    private List<QuestionOptionResponse> options;
    private Boolean allowMultipleAnswers;
    private Boolean randomizeOptions;

    // For True/False Questions
    private Boolean correctAnswer;
    private String trueFeedback;
    private String falseFeedback;

    // For Short Answer Questions
    private List<String> acceptedAnswers;
    private Boolean caseSensitive;
    private Boolean exactMatch;

    // For Essay Questions
    private Integer minWords;
    private Integer maxWords;
    private String sampleAnswer;
    private Long rubricId;

    // For Matching Questions
    private List<MatchingPairResponse> pairs;

    // For Ordering Questions
    private List<OrderingItemResponse> items;

    // For Fill in the Blank
    private String questionTemplate;
    private Integer blankCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionOptionResponse {
        private Long id;
        private String optionText;
        private Boolean isCorrect;
        private Integer orderIndex;
        private Double partialCreditPercentage;
        private String feedback;
        private String optionImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingPairResponse {
        private Long id;
        private String leftItem;
        private String rightItem;
        private Integer orderIndex;
        private String leftImageUrl;
        private String rightImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderingItemResponse {
        private Long id;
        private String itemText;
        private Integer correctOrder;
        private String itemImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionTagResponse {
        private Long id;
        private String tagName;
        private String description;
    }
}
