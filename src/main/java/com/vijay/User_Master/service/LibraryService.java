package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.LibraryRequest;
import com.vijay.User_Master.dto.LibraryResponse;
import com.vijay.User_Master.dto.LibraryStatistics;
import com.vijay.User_Master.entity.Library;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Library book management
 */
public interface LibraryService {

    /**
     * Create a new library book
     */
    LibraryResponse createBook(LibraryRequest request, Long ownerId);

    /**
     * Update an existing library book
     */
    LibraryResponse updateBook(Long id, LibraryRequest request, Long ownerId);

    /**
     * Get book by ID
     */
    LibraryResponse getBookById(Long id, Long ownerId);

    /**
     * Get all books for owner with pagination
     */
    Page<LibraryResponse> getAllBooks(Long ownerId, Pageable pageable);

    /**
     * Get books by category
     */
    Page<LibraryResponse> getBooksByCategory(Library.BookCategory category, Long ownerId, Pageable pageable);

    /**
     * Get books by status
     */
    Page<LibraryResponse> getBooksByStatus(Library.BookStatus status, Long ownerId, Pageable pageable);

    /**
     * Get available books
     */
    Page<LibraryResponse> getAvailableBooks(Long ownerId, Pageable pageable);

    /**
     * Get books by author
     */
    List<LibraryResponse> getBooksByAuthor(String author, Long ownerId);

    /**
     * Get books by subject
     */
    List<LibraryResponse> getBooksBySubject(String subject, Long ownerId);

    /**
     * Get books by publisher
     */
    List<LibraryResponse> getBooksByPublisher(String publisher, Long ownerId);

    /**
     * Get books by language
     */
    List<LibraryResponse> getBooksByLanguage(String language, Long ownerId);

    /**
     * Get reference only books
     */
    List<LibraryResponse> getReferenceOnlyBooks(Long ownerId);

    /**
     * Get borrowable books
     */
    List<LibraryResponse> getBorrowableBooks(Long ownerId);

    /**
     * Get books by purchase date range
     */
    List<LibraryResponse> getBooksByPurchaseDateRange(LocalDate startDate, LocalDate endDate, Long ownerId);

    /**
     * Get books by shelf number
     */
    List<LibraryResponse> getBooksByShelfNumber(String shelfNumber, Long ownerId);

    /**
     * Search books by keyword
     */
    Page<LibraryResponse> searchBooks(String keyword, Long ownerId, Pageable pageable);

    /**
     * Delete book (soft delete)
     */
    void deleteBook(Long id, Long ownerId);

    /**
     * Restore deleted book
     */
    void restoreBook(Long id, Long ownerId);

    /**
     * Issue a book (reduce available copies, increase issued copies)
     */
    LibraryResponse issueBook(Long id, Long ownerId);

    /**
     * Return a book (increase available copies, decrease issued copies)
     */
    LibraryResponse returnBook(Long id, Long ownerId);

    /**
     * Reserve a book
     */
    LibraryResponse reserveBook(Long id, Long ownerId);

    /**
     * Cancel book reservation
     */
    LibraryResponse cancelReservation(Long id, Long ownerId);

    /**
     * Mark book as damaged
     */
    LibraryResponse markBookAsDamaged(Long id, String damageDescription, Long ownerId);

    /**
     * Mark book as lost
     */
    LibraryResponse markBookAsLost(Long id, String lossDescription, Long ownerId);

    /**
     * Mark book as under repair
     */
    LibraryResponse markBookAsUnderRepair(Long id, String repairDescription, Long ownerId);

    /**
     * Update book copies
     */
    LibraryResponse updateBookCopies(Long id, Integer totalCopies, Integer availableCopies, Integer issuedCopies, Long ownerId);

    /**
     * Get library statistics
     */
    LibraryStatistics getLibraryStatistics(Long ownerId);

    /**
     * Get recently added books
     */
    List<LibraryResponse> getRecentlyAddedBooks(Long ownerId);
}
