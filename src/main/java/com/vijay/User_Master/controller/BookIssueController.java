package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.BookIssueRequest;
import com.vijay.User_Master.dto.BookIssueResponse;
import com.vijay.User_Master.dto.BookIssueStatistics;
import com.vijay.User_Master.entity.BookIssue;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.BookIssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Book Issue management
 */
@RestController
@RequestMapping("/api/v1/book-issues")
@AllArgsConstructor
@Slf4j
@Tag(name = "Book Issue Management", description = "APIs for managing library book issues, returns, renewals, and fines")
public class BookIssueController {

    private final BookIssueService bookIssueService;
    private final WorkerRepository workerRepository;

    @PostMapping
    @Operation(summary = "Issue a book", description = "Issue a book to a student or teacher")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<?> issueBook(@Valid @RequestBody BookIssueRequest request) {
        log.info("Issuing book: {}", request.getBookId());
        Long ownerId = getCorrectOwnerId();
        BookIssueResponse response = bookIssueService.issueBook(request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return a book", description = "Return a borrowed book to the library")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookIssueResponse> returnBook(
            @Parameter(description = "Book Issue ID") @PathVariable Long id,
            @Valid @RequestBody BookIssueRequest returnDetails) {
        log.info("Returning book for issue ID: {}", id);
        Long ownerId = getCorrectOwnerId();
        BookIssueResponse response = bookIssueService.returnBook(id, returnDetails, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/renew")
    @Operation(summary = "Renew a book", description = "Extend the due date of a borrowed book")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookIssueResponse> renewBook(
            @Parameter(description = "Book Issue ID") @PathVariable Long id,
            @Parameter(description = "Additional days to extend") @RequestParam(required = false) Integer additionalDays) {
        log.info("Renewing book for issue ID: {}", id);
        Long ownerId = getCorrectOwnerId();
        BookIssueResponse response = bookIssueService.renewBook(id, additionalDays, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book issue by ID", description = "Retrieve a specific book issue by its ID")
    public ResponseEntity<BookIssueResponse> getBookIssueById(
            @Parameter(description = "Book Issue ID") @PathVariable Long id) {
        log.info("Getting book issue: {}", id);
        Long ownerId = getCorrectOwnerId();
        BookIssueResponse response = bookIssueService.getBookIssueById(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all book issues", description = "Retrieve all book issues with pagination and sorting")
    public ResponseEntity<Page<BookIssueResponse>> getAllBookIssues(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "issueDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Getting all book issues - page: {}, size: {}", page, size);
        Long ownerId = getCorrectOwnerId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookIssueResponse> response = bookIssueService.getAllBookIssues(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get book issues by student", description = "Retrieve all book issues for a specific student")
    public ResponseEntity<Page<BookIssueResponse>> getBookIssuesByStudent(
            @Parameter(description = "Student ID") @PathVariable Long studentId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting book issues for student: {}", studentId);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<BookIssueResponse> response = bookIssueService.getBookIssuesByStudent(studentId, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get book issues by teacher", description = "Retrieve all book issues for a specific teacher")
    public ResponseEntity<Page<BookIssueResponse>> getBookIssuesByTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting book issues for teacher: {}", teacherId);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<BookIssueResponse> response = bookIssueService.getBookIssuesByTeacher(teacherId, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get book issues by book", description = "Retrieve issue history for a specific book")
    public ResponseEntity<List<BookIssueResponse>> getBookIssuesByBook(
            @Parameter(description = "Book ID") @PathVariable Long bookId) {
        log.info("Getting book issues for book: {}", bookId);
        Long ownerId = getCorrectOwnerId();
        List<BookIssueResponse> response = bookIssueService.getBookIssuesByBook(bookId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get book issues by status", description = "Retrieve all book issues with a specific status")
    public ResponseEntity<Page<BookIssueResponse>> getBookIssuesByStatus(
            @Parameter(description = "Issue status (ISSUED, RETURNED, OVERDUE, LOST, DAMAGED, RENEWED)") 
            @PathVariable BookIssue.IssueStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting book issues with status: {}", status);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<BookIssueResponse> response = bookIssueService.getBookIssuesByStatus(status, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/currently-issued")
    @Operation(summary = "Get currently issued books", description = "Retrieve all books that are currently issued (not returned)")
    public ResponseEntity<Page<BookIssueResponse>> getCurrentlyIssuedBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting currently issued books");
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<BookIssueResponse> response = bookIssueService.getCurrentlyIssuedBooks(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue books", description = "Retrieve all books that are overdue")
    public ResponseEntity<List<BookIssueResponse>> getOverdueBooks() {
        log.info("Getting overdue books");
        Long ownerId = getCorrectOwnerId();
        List<BookIssueResponse> response = bookIssueService.getOverdueBooks(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/due-today")
    @Operation(summary = "Get books due today", description = "Retrieve all books that are due for return today")
    public ResponseEntity<List<BookIssueResponse>> getBooksDueToday() {
        log.info("Getting books due today");
        Long ownerId = getCorrectOwnerId();
        List<BookIssueResponse> response = bookIssueService.getBooksDueToday(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get book issues by date range", description = "Retrieve book issues within a specific date range")
    public ResponseEntity<List<BookIssueResponse>> getBookIssuesByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting book issues between {} and {}", startDate, endDate);
        Long ownerId = getCorrectOwnerId();
        List<BookIssueResponse> response = bookIssueService.getBookIssuesByDateRange(startDate, endDate, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/fine")
    @Operation(summary = "Calculate fine", description = "Calculate the total fine for a book issue")
    public ResponseEntity<Double> calculateFine(
            @Parameter(description = "Book Issue ID") @PathVariable Long id) {
        log.info("Calculating fine for issue ID: {}", id);
        Long ownerId = getCorrectOwnerId();
        Double fine = bookIssueService.calculateFine(id, ownerId);
        return ResponseEntity.ok(fine);
    }

    @PostMapping("/{id}/collect-fine")
    @Operation(summary = "Collect fine", description = "Mark fine as collected for a book issue")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookIssueResponse> collectFine(
            @Parameter(description = "Book Issue ID") @PathVariable Long id) {
        log.info("Collecting fine for issue ID: {}", id);
        Long ownerId = getCorrectOwnerId();
        BookIssueResponse response = bookIssueService.collectFine(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-fines")
    @Operation(summary = "Get issues with pending fines", description = "Retrieve all book issues with uncollected fines")
    public ResponseEntity<List<BookIssueResponse>> getIssuesWithPendingFines() {
        log.info("Getting issues with pending fines");
        Long ownerId = getCorrectOwnerId();
        List<BookIssueResponse> response = bookIssueService.getIssuesWithPendingFines(ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/mark-lost")
    @Operation(summary = "Mark book as lost", description = "Mark a borrowed book as lost")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookIssueResponse> markBookAsLost(
            @Parameter(description = "Book Issue ID") @PathVariable Long id,
            @Parameter(description = "Remarks") @RequestParam String remarks) {
        log.info("Marking book as lost for issue ID: {}", id);
        Long ownerId = getCorrectOwnerId();
        BookIssueResponse response = bookIssueService.markBookAsLost(id, remarks, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/mark-damaged")
    @Operation(summary = "Mark book as damaged", description = "Mark a returned book as damaged with damage fee")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookIssueResponse> markBookAsDamaged(
            @Parameter(description = "Book Issue ID") @PathVariable Long id,
            @Parameter(description = "Damage fee") @RequestParam Double damageFee,
            @Parameter(description = "Remarks") @RequestParam String remarks) {
        log.info("Marking book as damaged for issue ID: {}", id);
        Long ownerId = getCorrectOwnerId();
        BookIssueResponse response = bookIssueService.markBookAsDamaged(id, damageFee, remarks, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get book issue statistics", description = "Get comprehensive statistics about book issues")
    public ResponseEntity<BookIssueStatistics> getBookIssueStatistics() {
        log.info("Getting book issue statistics");
        Long ownerId = getCorrectOwnerId();
        BookIssueStatistics response = bookIssueService.getBookIssueStatistics(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrower/{borrowerId}/history")
    @Operation(summary = "Get borrower history", description = "Get complete borrowing history for a student or teacher")
    public ResponseEntity<List<BookIssueResponse>> getBorrowerHistory(
            @Parameter(description = "Borrower ID") @PathVariable Long borrowerId,
            @Parameter(description = "Is student (true) or teacher (false)") @RequestParam boolean isStudent) {
        log.info("Getting borrower history for ID: {}", borrowerId);
        Long ownerId = getCorrectOwnerId();
        List<BookIssueResponse> response = bookIssueService.getBorrowerHistory(borrowerId, isStudent, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrower/{borrowerId}/can-issue")
    @Operation(summary = "Check if borrower can issue more books", description = "Check if a borrower has reached their book limit")
    public ResponseEntity<Boolean> canBorrowerIssueMoreBooks(
            @Parameter(description = "Borrower ID") @PathVariable Long borrowerId,
            @Parameter(description = "Is student (true) or teacher (false)") @RequestParam boolean isStudent) {
        log.info("Checking if borrower {} can issue more books", borrowerId);
        Long ownerId = getCorrectOwnerId();
        boolean canIssue = bookIssueService.canBorrowerIssueMoreBooks(borrowerId, isStudent, ownerId);
        return ResponseEntity.ok(canIssue);
    }

    @GetMapping("/borrower/{borrowerId}/active-count")
    @Operation(summary = "Get active borrow count", description = "Get the number of books currently borrowed by a student or teacher")
    public ResponseEntity<Long> getActiveBorrowCount(
            @Parameter(description = "Borrower ID") @PathVariable Long borrowerId,
            @Parameter(description = "Is student (true) or teacher (false)") @RequestParam boolean isStudent) {
        log.info("Getting active borrow count for borrower: {}", borrowerId);
        Long ownerId = getCorrectOwnerId();
        long count = bookIssueService.getActiveBorrowCount(borrowerId, isStudent, ownerId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get the correct owner ID for the logged-in user.
     * If the logged-in user is a worker (like a librarian), return their owner's ID.
     * If the logged-in user is a direct owner, return their own ID.
     */
    private Long getCorrectOwnerId() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long userId = loggedInUser.getId();
        
        // Check if the logged-in user is a worker
        Worker worker = workerRepository.findById(userId).orElse(null);
        if (worker != null && worker.getOwner() != null) {
            // User is a worker, return their owner's ID
            Long ownerId = worker.getOwner().getId();
            log.info("Logged-in user is worker ID: {}, returning owner ID: {}", userId, ownerId);
            return ownerId;
        }
        
        // User is a direct owner, return their own ID
        log.info("Logged-in user is direct owner ID: {}", userId);
        return userId;
    }
}
