package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Assignment statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentStatistics {

    private long totalAssignments;
    private long assignedCount;
    private long inProgressCount;
    private long submittedCount;
    private long gradedCount;
    private long overdueCount;
    private long cancelledCount;
    private double averageSubmissions;
    private double submissionRate;
    private long homeworkCount;
    private long projectCount;
    private long essayCount;
    private long practicalCount;
    private long presentationCount;
    private long groupProjectCount;
    private long quizCount;
}
