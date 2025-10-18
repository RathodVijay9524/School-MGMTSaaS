package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.BloomsLevel;
import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating questions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficulty;

    private Long subjectId;

    private Long classId;

    private String chapter;

    private String topic;

    private String explanation;

    @NotNull(message = "Points are required")
    private Double points;

    private String hints;

    private Boolean isActive;

    private List<Long> tagIds;

    private Boolean autoGradable;

    private Boolean allowPartialCredit;

    private Integer timeLimitSeconds;

    private BloomsLevel bloomsLevel;

    private String imageUrl;

    private String videoUrl;

    private String audioUrl;

    // For Multiple Choice Questions
    private List<QuestionOptionRequest> options;
    private Boolean allowMultipleAnswers;
    private Boolean randomizeOptions;
    private Integer minSelections;
    private Integer maxSelections;

    // For True/False Questions
    private Boolean correctAnswer;
    private String trueFeedback;
    private String falseFeedback;

    // For Short Answer Questions
    private List<String> acceptedAnswers;
    private Boolean caseSensitive;
    private Boolean exactMatch;
    private Integer maxLength;
    private Integer minLength;
    private Boolean useAiGrading;

    // For Essay Questions
    private Integer minWords;
    private Integer maxWords;
    private String sampleAnswer;
    private Long rubricId;
    private Boolean requireManualReview;

    // For Matching Questions
    private List<MatchingPairRequest> pairs;
    private Boolean randomizeLeft;
    private Boolean randomizeRight;

    // For Ordering Questions
    private List<OrderingItemRequest> items;
    private Boolean allowPartialOrder;

    // For Fill in the Blank
    private String questionTemplate;
    private Integer blankCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionOptionRequest {
        @NotBlank(message = "Option text is required")
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
    public static class MatchingPairRequest {
        @NotBlank(message = "Left item is required")
        private String leftItem;
        @NotBlank(message = "Right item is required")
        private String rightItem;
        private Integer orderIndex;
        private String leftImageUrl;
        private String rightImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderingItemRequest {
        @NotBlank(message = "Item text is required")
        private String itemText;
        @NotNull(message = "Correct order is required")
        private Integer correctOrder;
        private String itemImageUrl;
    }
}
