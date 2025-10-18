package com.vijay.User_Master.entity;

/**
 * Enum for quiz attempt status
 */
public enum AttemptStatus {
    NOT_STARTED,    // Quiz not yet started
    IN_PROGRESS,    // Currently taking quiz
    SUBMITTED,      // Submitted, awaiting grading
    GRADED,         // Graded and complete
    ABANDONED,      // Started but not completed
    EXPIRED         // Time limit exceeded
}
