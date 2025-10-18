package com.vijay.User_Master.entity;

/**
 * Enum for peer review status
 */
public enum ReviewStatus {
    PENDING,        // Review not yet started
    IN_PROGRESS,    // Review in progress
    SUBMITTED,      // Review submitted
    APPROVED,       // Review approved by teacher
    REJECTED,       // Review rejected by teacher
    REVISED         // Review revised after feedback
}
