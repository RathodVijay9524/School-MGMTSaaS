package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Document entity operations
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find documents by owner ID
     */
    Page<Document> findByOwnerIdAndDeletedFalseOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    /**
     * Find documents by category
     */
    Page<Document> findByOwnerIdAndCategoryAndDeletedFalseOrderByCreatedAtDesc(Long ownerId, String category, Pageable pageable);

    /**
     * Find documents by file type
     */
    Page<Document> findByOwnerIdAndFileTypeAndDeletedFalseOrderByCreatedAtDesc(Long ownerId, String fileType, Pageable pageable);

    /**
     * Search documents by content (case-insensitive)
     */
    @Query("SELECT d FROM Document d WHERE d.ownerId = :ownerId AND d.deleted = false AND " +
           "(LOWER(d.extractedContent) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.originalFileName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> searchDocumentsByContent(@Param("ownerId") Long ownerId, @Param("query") String query, Pageable pageable);

    /**
     * Find documents by tags
     */
    @Query("SELECT d FROM Document d WHERE d.ownerId = :ownerId AND d.deleted = false AND " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
    Page<Document> findByOwnerIdAndTagsContainingIgnoreCaseAndDeletedFalse(Long ownerId, String tag, Pageable pageable);

    /**
     * Find processed documents
     */
    Page<Document> findByOwnerIdAndIsProcessedTrueAndDeletedFalseOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    /**
     * Find unprocessed documents
     */
    List<Document> findByOwnerIdAndIsProcessedFalseAndDeletedFalseOrderByCreatedAtAsc(Long ownerId);

    /**
     * Find documents created between dates
     */
    Page<Document> findByOwnerIdAndCreatedAtBetweenAndDeletedFalseOrderByCreatedAtDesc(
            Long ownerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find document by file path
     */
    Optional<Document> findByFilePathAndDeletedFalse(String filePath);

    /**
     * Find document by original filename and owner
     */
    Optional<Document> findByOriginalFileNameAndOwnerIdAndDeletedFalse(String originalFileName, Long ownerId);

    /**
     * Get document statistics for owner
     */
    @Query("SELECT d.fileType, COUNT(d) FROM Document d WHERE d.ownerId = :ownerId AND d.deleted = false GROUP BY d.fileType")
    List<Object[]> getDocumentStatisticsByType(@Param("ownerId") Long ownerId);

    /**
     * Get total document count for owner
     */
    long countByOwnerIdAndDeletedFalse(Long ownerId);

    /**
     * Get total processed documents count for owner
     */
    long countByOwnerIdAndIsProcessedTrueAndDeletedFalse(Long ownerId);

    /**
     * Find documents for content extraction (unprocessed)
     */
    @Query("SELECT d FROM Document d WHERE d.isProcessed = false AND d.deleted = false ORDER BY d.createdAt ASC")
    List<Document> findUnprocessedDocuments();

    /**
     * Soft delete document
     */
    @Query("UPDATE Document d SET d.deleted = true, d.deletedAt = :deletedAt WHERE d.id = :id")
    void softDeleteDocument(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
}
