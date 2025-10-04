package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DocumentStatistics;
import com.vijay.User_Master.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing school documents in RAG system
 */
public interface DocumentService {

    /**
     * Upload and process a document
     */
    Document uploadDocument(MultipartFile file, Long ownerId, String category, String description, String tags);

    /**
     * Get document by ID
     */
    Optional<Document> getDocumentById(Long id, Long ownerId);

    /**
     * Get all documents for owner with pagination
     */
    Page<Document> getDocuments(Long ownerId, int page, int size, String sortBy, String sortDir);

    /**
     * Get documents by category
     */
    Page<Document> getDocumentsByCategory(Long ownerId, String category, int page, int size, String sortBy, String sortDir);

    /**
     * Get documents by file type
     */
    Page<Document> getDocumentsByFileType(Long ownerId, String fileType, int page, int size, String sortBy, String sortDir);

    /**
     * Search documents by content
     */
    Page<Document> searchDocuments(Long ownerId, String query, int page, int size, String sortBy, String sortDir);

    /**
     * Search documents by tags
     */
    Page<Document> searchDocumentsByTags(Long ownerId, String tags, int page, int size, String sortBy, String sortDir);

    /**
     * Get processed documents
     */
    Page<Document> getProcessedDocuments(Long ownerId, int page, int size, String sortBy, String sortDir);

    /**
     * Get unprocessed documents
     */
    List<Document> getUnprocessedDocuments(Long ownerId);

    /**
     * Process unprocessed documents
     */
    List<Document> processUnprocessedDocuments(Long ownerId);

    /**
     * Update document metadata
     */
    Document updateDocument(Long id, Long ownerId, String category, String description, String tags);

    /**
     * Delete document (soft delete)
     */
    boolean deleteDocument(Long id, Long ownerId);

    /**
     * Get document statistics
     */
    DocumentStatistics getDocumentStatistics(Long ownerId);

    /**
     * Get document content for RAG queries
     */
    List<Document> getDocumentsForRAGQuery(Long ownerId, String query);

    /**
     * Bulk upload documents
     */
    List<Document> bulkUploadDocuments(List<MultipartFile> files, Long ownerId, String category, String description, String tags);
}
