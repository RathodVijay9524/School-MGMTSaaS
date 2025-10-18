package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for Book Issue statistics and analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookIssueStatistics {

    // Overall counts
    private long totalIssues;
    private long activeIssues; // Currently issued
    private long returnedIssues;
    private long overdueIssues;
    private long lostBooks;
    private long damagedBooks;
    
    // Borrower statistics
    private long totalStudentBorrowers;
    private long totalTeacherBorrowers;
    private long activeStudentBorrowers;
    private long activeTeacherBorrowers;
    
    // Fine statistics
    private Double totalFinesCollected;
    private Double totalPendingFines;
    private Double totalLateFees;
    private Double totalDamageFees;
    private long issuesWithPendingFines;
    
    // Time-based metrics
    private long issuedToday;
    private long returnedToday;
    private long dueToday;
    private long dueTomorrow;
    private long overdueBy1Day;
    private long overdueBy7Days;
    private long overdueBy30Days;
    
    // Renewal statistics
    private long totalRenewals;
    private Double averageRenewalsPerIssue;
    
    // Popular books (most issued)
    private Map<String, Long> topIssuedBooks; // Book title -> issue count
    
    // Active borrowers (most books issued)
    private Map<String, Long> topBorrowers; // Borrower name -> issue count
    
    // Collection metrics
    private Double averageIssueDuration; // Average days books are borrowed
    private Double returnRate; // Percentage of books returned on time
    private Double overdueRate; // Percentage of books that are overdue
    
    // Status distribution
    private Map<String, Long> issuesByStatus;
    
    // Monthly trends
    private Map<String, Long> issuesByMonth; // Month -> count
}
