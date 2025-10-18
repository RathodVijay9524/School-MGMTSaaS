package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.BookIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

/**
 * DTO for BookIssue response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookIssueResponse {

    private Long id;
    
    // Book details
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String author;
    private String accessionNumber;
    
    // Borrower details (student or teacher)
    private Long studentId;
    private String studentName;
    private String studentEmail;
    
    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    
    private BookIssue.IssueStatus status;
    
    // Fine details
    private Integer daysOverdue;
    private Double lateFee;
    private Double damageFee;
    private Double totalFine;
    private boolean fineCollected;
    private Double pendingFine;
    
    // Librarian details
    private Long issuedById;
    private String issuedByName;
    
    private Long returnedToId;
    private String returnedToName;
    
    private String issueRemarks;
    private String returnRemarks;
    
    // Condition tracking
    private BookIssue.BookCondition bookConditionOnIssue;
    private BookIssue.BookCondition bookConditionOnReturn;
    private boolean conditionDeteriorated; // If condition worsened
    
    // Renewal tracking
    private Integer renewalCount;
    private LocalDate lastRenewalDate;
    private boolean canRenew; // If book can be renewed
    
    // Owner details
    private Long ownerId;
    private String ownerName;
    
    // Audit fields
    private Date createdOn;
    private Date updatedOn;
    
    // ========== COMPUTED FIELDS ==========
    
    // Status flags
    private boolean isIssued;
    private boolean isReturned;
    private boolean isOverdue;
    private boolean isLost;
    private boolean isDamaged;
    
    // Time metrics
    private long daysIssued; // Total days book has been issued
    private long daysUntilDue; // Days remaining until due date
    private long daysFromIssue; // Days since issue date
    
    // Display fields
    private String statusDisplay;
    private String borrowerName; // Either student or teacher name
    private String borrowerType; // "Student" or "Teacher"
    private String dueStatusDisplay; // e.g., "Overdue by 5 days" or "Due in 3 days"
    
    // Badge indicators for UI
    private boolean showOverdueBadge;
    private boolean showDamagedBadge;
    private boolean showFinesPendingBadge;
}
