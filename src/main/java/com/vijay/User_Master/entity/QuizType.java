package com.vijay.User_Master.entity;

/**
 * Enum for different types of quizzes
 */
public enum QuizType {
    PRACTICE,      // Practice quiz (not graded)
    GRADED,        // Graded quiz (counts toward final grade)
    DIAGNOSTIC,    // Diagnostic assessment
    SURVEY,        // Survey/feedback quiz
    FORMATIVE,     // Formative assessment
    SUMMATIVE      // Summative assessment
}
