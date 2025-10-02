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
           "(LOWER(l.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.publisher) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Library> searchBooksByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.category = :category AND l.isDeleted = false")
    Page<Library> findByOwner_IdAndCategoryAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("category") Library.BookCategory category, Pageable pageable);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.status = :status AND l.isDeleted = false")
    Page<Library> findByOwner_IdAndStatusAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("status") Library.BookStatus status, Pageable pageable);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.author = :author AND l.isDeleted = false")
    List<Library> findByOwner_IdAndAuthorAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("author") String author);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.subject = :subject AND l.isDeleted = false")
    List<Library> findByOwner_IdAndSubjectAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("subject") String subject);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.publisher = :publisher AND l.isDeleted = false")
    List<Library> findByOwner_IdAndPublisherAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("publisher") String publisher);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.language = :language AND l.isDeleted = false")
    List<Library> findByOwner_IdAndLanguageAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("language") String language);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.isReferencOnly = true AND l.isDeleted = false")
    List<Library> findReferenceOnlyBooksByOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.availableCopies > 0 AND l.isReferencOnly = false AND l.isDeleted = false")
    List<Library> findBorrowableBooksByOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.purchaseDate >= :startDate AND l.purchaseDate <= :endDate AND l.isDeleted = false")
    List<Library> findBooksByOwnerAndPurchaseDateRange(@Param("ownerId") Long ownerId, @Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);
    
    @Query("SELECT l FROM Library l WHERE l.owner.id = :ownerId AND l.shelfNumber = :shelfNumber AND l.isDeleted = false")
    List<Library> findByOwner_IdAndShelfNumberAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("shelfNumber") String shelfNumber);
    
    @Query("SELECT COUNT(l) FROM Library l WHERE l.owner.id = :ownerId AND l.availableCopies > 0 AND l.isDeleted = false")
    long countAvailableBooksByOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT COUNT(l) FROM Library l WHERE l.owner.id = :ownerId AND l.issuedCopies > 0 AND l.isDeleted = false")
    long countIssuedBooksByOwner(@Param("ownerId") Long ownerId);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Library> findByIdAndOwner_Id(Long id, Long ownerId);
    
    Optional<Library> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
    
    // Multi-tenant aware existence checks
    boolean existsByIsbnAndOwner_Id(String isbn, Long ownerId);
    
    boolean existsByAccessionNumberAndOwner_Id(String accessionNumber, Long ownerId);
}

