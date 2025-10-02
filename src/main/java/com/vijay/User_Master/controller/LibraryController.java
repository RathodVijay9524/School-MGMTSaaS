package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.LibraryRequest;
import com.vijay.User_Master.dto.LibraryResponse;
import com.vijay.User_Master.dto.LibraryStatistics;
import com.vijay.User_Master.entity.Library;
import com.vijay.User_Master.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Library book management
 */
@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Library Management", description = "APIs for managing library books and catalog")
public class LibraryController {

    private final LibraryService libraryService;

    @PostMapping
    @Operation(summary = "Create a new library book", description = "Add a new book to the library catalog")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> createBook(@Valid @RequestBody LibraryRequest request) {
        log.info("Creating library book: {} by author: {}", request.getBookTitle(), request.getAuthor());
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.createBook(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a library book", description = "Update an existing book in the library catalog")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody LibraryRequest request) {
        log.info("Updating library book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.updateBook(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    public ResponseEntity<LibraryResponse> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Getting library book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.getBookById(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all library books", description = "Retrieve all books in the library catalog with pagination")
    public ResponseEntity<Page<LibraryResponse>> getAllBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "bookTitle") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all library books - page: {}, size: {}", page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LibraryResponse> response = libraryService.getAllBooks(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get books by category", description = "Retrieve all books of a specific category")
    public ResponseEntity<Page<LibraryResponse>> getBooksByCategory(
            @Parameter(description = "Book category") @PathVariable Library.BookCategory category,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting books for category: {}", category);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookTitle").ascending());
        Page<LibraryResponse> response = libraryService.getBooksByCategory(category, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get books by status", description = "Retrieve all books with a specific status")
    public ResponseEntity<Page<LibraryResponse>> getBooksByStatus(
            @Parameter(description = "Book status") @PathVariable Library.BookStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting books for status: {}", status);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookTitle").ascending());
        Page<LibraryResponse> response = libraryService.getBooksByStatus(status, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available books", description = "Retrieve all books that are available for borrowing")
    public ResponseEntity<Page<LibraryResponse>> getAvailableBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting available books");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookTitle").ascending());
        Page<LibraryResponse> response = libraryService.getAvailableBooks(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/author/{author}")
    @Operation(summary = "Get books by author", description = "Retrieve all books by a specific author")
    public ResponseEntity<List<LibraryResponse>> getBooksByAuthor(
            @Parameter(description = "Author name") @PathVariable String author) {
        log.info("Getting books for author: {}", author);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getBooksByAuthor(author, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subject/{subject}")
    @Operation(summary = "Get books by subject", description = "Retrieve all books for a specific subject")
    public ResponseEntity<List<LibraryResponse>> getBooksBySubject(
            @Parameter(description = "Subject") @PathVariable String subject) {
        log.info("Getting books for subject: {}", subject);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getBooksBySubject(subject, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/publisher/{publisher}")
    @Operation(summary = "Get books by publisher", description = "Retrieve all books from a specific publisher")
    public ResponseEntity<List<LibraryResponse>> getBooksByPublisher(
            @Parameter(description = "Publisher name") @PathVariable String publisher) {
        log.info("Getting books for publisher: {}", publisher);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getBooksByPublisher(publisher, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/language/{language}")
    @Operation(summary = "Get books by language", description = "Retrieve all books in a specific language")
    public ResponseEntity<List<LibraryResponse>> getBooksByLanguage(
            @Parameter(description = "Language") @PathVariable String language) {
        log.info("Getting books for language: {}", language);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getBooksByLanguage(language, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference-only")
    @Operation(summary = "Get reference only books", description = "Retrieve all books that are for reference only")
    public ResponseEntity<List<LibraryResponse>> getReferenceOnlyBooks() {
        log.info("Getting reference only books");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getReferenceOnlyBooks(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrowable")
    @Operation(summary = "Get borrowable books", description = "Retrieve all books that can be borrowed")
    public ResponseEntity<List<LibraryResponse>> getBorrowableBooks() {
        log.info("Getting borrowable books");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getBorrowableBooks(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/purchase-date-range")
    @Operation(summary = "Get books by purchase date range", description = "Retrieve books purchased within a specific date range")
    public ResponseEntity<List<LibraryResponse>> getBooksByPurchaseDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam LocalDate endDate) {
        log.info("Getting books for purchase date range: {} to {}", startDate, endDate);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getBooksByPurchaseDateRange(startDate, endDate, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shelf/{shelfNumber}")
    @Operation(summary = "Get books by shelf number", description = "Retrieve all books from a specific shelf")
    public ResponseEntity<List<LibraryResponse>> getBooksByShelfNumber(
            @Parameter(description = "Shelf number") @PathVariable String shelfNumber) {
        log.info("Getting books for shelf number: {}", shelfNumber);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getBooksByShelfNumber(shelfNumber, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by keyword in title, author, ISBN, subject, or publisher")
    public ResponseEntity<Page<LibraryResponse>> searchBooks(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching books with keyword: {}", keyword);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookTitle").ascending());
        Page<LibraryResponse> response = libraryService.searchBooks(keyword, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recently-added")
    @Operation(summary = "Get recently added books", description = "Retrieve books added in the last 30 days")
    public ResponseEntity<List<LibraryResponse>> getRecentlyAddedBooks() {
        log.info("Getting recently added books");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<LibraryResponse> response = libraryService.getRecentlyAddedBooks(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get library statistics", description = "Get comprehensive statistics about the library collection")
    public ResponseEntity<LibraryStatistics> getLibraryStatistics() {
        log.info("Getting library statistics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryStatistics response = libraryService.getLibraryStatistics(ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/issue")
    @Operation(summary = "Issue a book", description = "Issue a book to a borrower")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> issueBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Issuing book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.issueBook(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Return a book", description = "Return a book from a borrower")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> returnBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Returning book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.returnBook(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reserve")
    @Operation(summary = "Reserve a book", description = "Reserve a book for future borrowing")
    public ResponseEntity<LibraryResponse> reserveBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Reserving book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.reserveBook(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel-reservation")
    @Operation(summary = "Cancel book reservation", description = "Cancel a book reservation")
    public ResponseEntity<LibraryResponse> cancelReservation(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Cancelling reservation for book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.cancelReservation(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/mark-damaged")
    @Operation(summary = "Mark book as damaged", description = "Mark a book as damaged")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> markBookAsDamaged(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Damage description") @RequestParam String damageDescription) {
        log.info("Marking book as damaged: {} - {}", id, damageDescription);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.markBookAsDamaged(id, damageDescription, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/mark-lost")
    @Operation(summary = "Mark book as lost", description = "Mark a book as lost")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> markBookAsLost(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Loss description") @RequestParam String lossDescription) {
        log.info("Marking book as lost: {} - {}", id, lossDescription);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.markBookAsLost(id, lossDescription, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/mark-under-repair")
    @Operation(summary = "Mark book as under repair", description = "Mark a book as under repair")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> markBookAsUnderRepair(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Repair description") @RequestParam String repairDescription) {
        log.info("Marking book as under repair: {} - {}", id, repairDescription);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.markBookAsUnderRepair(id, repairDescription, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/update-copies")
    @Operation(summary = "Update book copies", description = "Update the number of copies for a book")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<LibraryResponse> updateBookCopies(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Total copies") @RequestParam Integer totalCopies,
            @Parameter(description = "Available copies") @RequestParam Integer availableCopies,
            @Parameter(description = "Issued copies") @RequestParam Integer issuedCopies) {
        log.info("Updating book copies: {} - Total: {}, Available: {}, Issued: {}", id, totalCopies, availableCopies, issuedCopies);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        LibraryResponse response = libraryService.updateBookCopies(id, totalCopies, availableCopies, issuedCopies, ownerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete library book", description = "Soft delete a book from the library catalog")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Deleting library book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        libraryService.deleteBook(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore library book", description = "Restore a soft-deleted book")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> restoreBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("Restoring library book: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        libraryService.restoreBook(id, ownerId);
        return ResponseEntity.ok().build();
    }
}
