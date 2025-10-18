package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.BookIssueRequest;
import com.vijay.User_Master.dto.BookIssueResponse;
import com.vijay.User_Master.dto.BookIssueStatistics;
import com.vijay.User_Master.entity.BookIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Book Issue management
 */
public interface BookIssueService {

    /**
     * Issue a book to a student or teacher
     */
    BookIssueResponse issueBook(BookIssueRequest request, Long ownerId);

    /**
     * Return a book
     */
    BookIssueResponse returnBook(Long issueId, BookIssueRequest returnDetails, Long ownerId);

    /**
     * Renew a book (extend due date)
     */
    BookIssueResponse renewBook(Long issueId, Integer additionalDays, Long ownerId);

    /**
     * Get book issue by ID
     */
    BookIssueResponse getBookIssueById(Long id, Long ownerId);

    /**
     * Get all book issues with pagination
     */
    Page<BookIssueResponse> getAllBookIssues(Long ownerId, Pageable pageable);

    /**
     * Get book issues by student
     */
    Page<BookIssueResponse> getBookIssuesByStudent(Long studentId, Long ownerId, Pageable pageable);

    /**
     * Get book issues by teacher
     */
    Page<BookIssueResponse> getBookIssuesByTeacher(Long teacherId, Long ownerId, Pageable pageable);

    /**
     * Get book issues by book
     */
    List<BookIssueResponse> getBookIssuesByBook(Long bookId, Long ownerId);

    /**
     * Get book issues by status
     */
    Page<BookIssueResponse> getBookIssuesByStatus(BookIssue.IssueStatus status, Long ownerId, Pageable pageable);

    /**
     * Get currently issued books (not returned)
     */
    Page<BookIssueResponse> getCurrentlyIssuedBooks(Long ownerId, Pageable pageable);

    /**
     * Get overdue books
     */
    List<BookIssueResponse> getOverdueBooks(Long ownerId);

    /**
     * Get books due today
     */
    List<BookIssueResponse> getBooksDueToday(Long ownerId);

    /**
     * Get books issued by date range
     */
    List<BookIssueResponse> getBookIssuesByDateRange(LocalDate startDate, LocalDate endDate, Long ownerId);

    /**
     * Calculate fine for a book issue
     */
    Double calculateFine(Long issueId, Long ownerId);

    /**
     * Collect fine for a book issue
     */
    BookIssueResponse collectFine(Long issueId, Long ownerId);

    /**
     * Get issues with pending fines
     */
    List<BookIssueResponse> getIssuesWithPendingFines(Long ownerId);

    /**
     * Mark book as lost
     */
    BookIssueResponse markBookAsLost(Long issueId, String remarks, Long ownerId);

    /**
     * Mark book as damaged on return
     */
    BookIssueResponse markBookAsDamaged(Long issueId, Double damageFee, String remarks, Long ownerId);

    /**
     * Check and update overdue statuses
     */
    void checkAndUpdateOverdueBooks();

    /**
     * Send overdue notifications
     */
    void sendOverdueNotifications();

    /**
     * Get book issue statistics
     */
    BookIssueStatistics getBookIssueStatistics(Long ownerId);

    /**
     * Get issue history for a borrower (student or teacher)
     */
    List<BookIssueResponse> getBorrowerHistory(Long borrowerId, boolean isStudent, Long ownerId);

    /**
     * Check if borrower can issue more books
     */
    boolean canBorrowerIssueMoreBooks(Long borrowerId, boolean isStudent, Long ownerId);

    /**
     * Get active issue count for borrower
     */
    long getActiveBorrowCount(Long borrowerId, boolean isStudent, Long ownerId);
}
