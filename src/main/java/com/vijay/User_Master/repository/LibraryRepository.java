package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Library;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    Optional<Library> findByIsbn(String isbn);
    
    Optional<Library> findByAccessionNumber(String accessionNumber);
    
    boolean existsByIsbn(String isbn);
    
    boolean existsByAccessionNumber(String accessionNumber);
    
    // Find by category
    Page<Library> findByCategoryAndIsDeletedFalse(Library.BookCategory category, Pageable pageable);
    
    // Find by status
    Page<Library> findByStatusAndIsDeletedFalse(Library.BookStatus status, Pageable pageable);
    
    // Search books
    @Query("SELECT l FROM Library l WHERE l.isDeleted = false AND " +
           "(LOWER(l.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.subject) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Library> searchBooks(@Param("keyword") String keyword, Pageable pageable);
    
    // Find available books
    @Query("SELECT l FROM Library l WHERE l.availableCopies > 0 AND l.isDeleted = false")
    Page<Library> findAvailableBooks(Pageable pageable);
    
    // Find by author
    List<Library> findByAuthorAndIsDeletedFalse(String author);
    
    // Find by subject
    List<Library> findBySubjectAndIsDeletedFalse(String subject);
    
    // Find by publisher
    List<Library> findByPublisherAndIsDeletedFalse(String publisher);
    
    // Count total books
    long countByIsDeletedFalse();
    
    // Count available books
    @Query("SELECT COUNT(l) FROM Library l WHERE l.availableCopies > 0 AND l.isDeleted = false")
    long countAvailableBooks();
    
    // Multi-tenancy: Find by business owner
    Page<Library> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.availableCopies > 0 AND l.isDeleted = false")
    Page<Library> findAvailableBooksByOwner(@Param("ownerId") Long ownerId, Pageable pageable);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.isDeleted = false AND " +
           "(LOWER(l.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Library> searchBooksByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
}

