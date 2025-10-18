package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.BookIssue;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for BookIssue creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookIssueRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    // Either student OR teacher must be provided (not both)
    private Long studentId;
    
    private Long teacherId;

    @NotNull(message = "Issue date is required")
    @PastOrPresent(message = "Issue date cannot be in the future")
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    // For return operations
    private LocalDate returnDate;

    private BookIssue.IssueStatus status;

    private BookIssue.BookCondition bookConditionOnIssue;

    // For return operations
    private BookIssue.BookCondition bookConditionOnReturn;

    @Size(max = 500, message = "Issue remarks cannot exceed 500 characters")
    private String issueRemarks;

    @Size(max = 500, message = "Return remarks cannot exceed 500 characters")
    private String returnRemarks;

    // For damage reporting
    private Double damageFee;

    // Custom validation: Either studentId or teacherId must be provided
    public boolean isValid() {
        return (studentId != null && teacherId == null) || 
               (studentId == null && teacherId != null);
    }
}
