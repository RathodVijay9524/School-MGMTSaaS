package com.vijay.User_Master.entity;

/**
 * Enum for different types of questions in the question bank
 */
public enum QuestionType {
    MULTIPLE_CHOICE,        // MCQ with single or multiple correct answers
    TRUE_FALSE,             // True/False questions
    SHORT_ANSWER,           // Short text answer
    ESSAY,                  // Long text answer
    MATCHING,               // Match items from two lists
    ORDERING,               // Arrange items in correct order
    FILL_IN_BLANK,         // Fill in the blank(s)
    DRAG_AND_DROP,         // Drag items to correct positions
    HOTSPOT,               // Click on correct area of image
    FILE_UPLOAD,           // Upload file as answer
    CODE,                  // Programming code question
    MATH,                  // Mathematical equation
    MULTIPLE_RESPONSE,     // Multiple correct answers (checkboxes)
    RANKING,               // Rank items by preference/importance
    MATRIX                 // Grid of questions with same options
}
