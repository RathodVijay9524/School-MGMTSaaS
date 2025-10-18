package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AutoGradingRequest;
import com.vijay.User_Master.dto.AutoGradingResponse;
import com.vijay.User_Master.entity.*;

/**
 * Service for auto-grading different question types
 */
public interface AutoGradingService {

    /**
     * Auto-grade a question response
     */
    AutoGradingResponse gradeResponse(AutoGradingRequest request);

    /**
     * Grade Multiple Choice Question
     */
    AutoGradingResponse gradeMultipleChoice(MultipleChoiceQuestion question, String studentAnswer);

    /**
     * Grade True/False Question
     */
    AutoGradingResponse gradeTrueFalse(TrueFalseQuestion question, String studentAnswer);

    /**
     * Grade Short Answer Question
     */
    AutoGradingResponse gradeShortAnswer(ShortAnswerQuestion question, String studentAnswer);

    /**
     * Grade Essay Question (using AI)
     */
    AutoGradingResponse gradeEssay(EssayQuestion question, String studentAnswer);

    /**
     * Grade Matching Question
     */
    AutoGradingResponse gradeMatching(MatchingQuestion question, String studentAnswer);

    /**
     * Grade Ordering Question
     */
    AutoGradingResponse gradeOrdering(OrderingQuestion question, String studentAnswer);

    /**
     * Grade Fill in the Blank Question
     */
    AutoGradingResponse gradeFillInBlank(FillInTheBlankQuestion question, String studentAnswer);

    /**
     * Calculate partial credit
     */
    Double calculatePartialCredit(Question question, String studentAnswer, String correctAnswer);

    /**
     * Calculate similarity between two strings (for fuzzy matching)
     */
    Double calculateSimilarity(String answer1, String answer2);
}
