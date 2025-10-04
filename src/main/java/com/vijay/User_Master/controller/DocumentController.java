package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.DocumentStatistics;
import com.vijay.User_Master.entity.Document;
import com.vijay.User_Master.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing school documents in RAG system
 */
@RestController
@RequestMapping("/api/v1/documents")
@AllArgsConstructor
@Slf4j
@Tag(name = "Document Management", description = "Upload, manage, and search school documents for RAG system")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload a single document
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Upload and process a document for RAG system")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> uploadDocument(
            @Parameter(description = "Document file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Document category") @RequestParam(value = "category", defaultValue = "general") String category,
            @Parameter(description = "Document description") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Document tags (comma-separated)") @RequestParam(value = "tags", required = false) String tags) {
        
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            log.info("Uploading document: {} for owner: {}", file.getOriginalFilename(), ownerId);

            Document document = documentService.uploadDocument(file, ownerId, category, description, tags);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document uploaded successfully");
            response.put("document", document);
            response.put("timestamp", System.currentTimeMillis());

            return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error uploading document: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error uploading document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk upload multiple documents
     */
    @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Bulk upload documents", description = "Upload multiple documents at once")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> bulkUploadDocuments(
            @Parameter(description = "Document files to upload") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "Document category") @RequestParam(value = "category", defaultValue = "general") String category,
            @Parameter(description = "Document description") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Document tags (comma-separated)") @RequestParam(value = "tags", required = false) String tags) {
        
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            log.info("Bulk uploading {} documents for owner: {}", files.size(), ownerId);

            List<Document> documents = documentService.bulkUploadDocuments(files, ownerId, category, description, tags);

            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("Successfully uploaded %d documents", documents.size()));
            response.put("documents", documents);
            response.put("uploadedCount", documents.size());
            response.put("totalFiles", files.size());
            response.put("timestamp", System.currentTimeMillis());

            return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error bulk uploading documents: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error bulk uploading documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get document by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieve a specific document")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getDocument(@PathVariable Long id) {
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            Optional<Document> documentOpt = documentService.getDocumentById(id, ownerId);
            if (documentOpt.isPresent()) {
                return ExceptionUtil.createBuildResponse(documentOpt.get(), HttpStatus.OK);
            } else {
                return ExceptionUtil.createBuildResponseMessage("Document not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("Error getting document: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error retrieving document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all documents with pagination
     */
    @GetMapping
    @Operation(summary = "Get all documents", description = "Retrieve all documents with pagination")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getDocuments(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            Page<Document> documents = documentService.getDocuments(ownerId, page, size, sortBy, sortDir);

            Map<String, Object> response = new HashMap<>();
            response.put("documents", documents.getContent());
            response.put("totalElements", documents.getTotalElements());
            response.put("totalPages", documents.getTotalPages());
            response.put("currentPage", documents.getNumber());
            response.put("pageSize", documents.getSize());
            response.put("hasNext", documents.hasNext());
            response.put("hasPrevious", documents.hasPrevious());

            return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error getting documents: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error retrieving documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search documents by content
     */
    @GetMapping("/search")
    @Operation(summary = "Search documents", description = "Search documents by content, filename, or tags")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> searchDocuments(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            Page<Document> documents = documentService.searchDocuments(ownerId, query, page, size, sortBy, sortDir);

            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("documents", documents.getContent());
            response.put("totalElements", documents.getTotalElements());
            response.put("totalPages", documents.getTotalPages());
            response.put("currentPage", documents.getNumber());
            response.put("pageSize", documents.getSize());

            return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error searching documents: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error searching documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get documents by category
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get documents by category", description = "Retrieve documents filtered by category")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getDocumentsByCategory(
            @PathVariable String category,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            Page<Document> documents = documentService.getDocumentsByCategory(ownerId, category, page, size, sortBy, sortDir);

            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("documents", documents.getContent());
            response.put("totalElements", documents.getTotalElements());
            response.put("totalPages", documents.getTotalPages());
            response.put("currentPage", documents.getNumber());
            response.put("pageSize", documents.getSize());

            return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error getting documents by category: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error retrieving documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get document statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get document statistics", description = "Get statistics about uploaded documents")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> getDocumentStatistics() {
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            DocumentStatistics statistics = documentService.getDocumentStatistics(ownerId);

            return ExceptionUtil.createBuildResponse(statistics, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error getting document statistics: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error retrieving statistics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update document metadata
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update document", description = "Update document metadata")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {
        
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            String category = updates.get("category");
            String description = updates.get("description");
            String tags = updates.get("tags");

            Document document = documentService.updateDocument(id, ownerId, category, description, tags);

            return ExceptionUtil.createBuildResponse(document, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error updating document: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error updating document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete document
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Soft delete a document")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            boolean deleted = documentService.deleteDocument(id, ownerId);

            if (deleted) {
                return ExceptionUtil.createBuildResponseMessage("Document deleted successfully", HttpStatus.OK);
            } else {
                return ExceptionUtil.createBuildResponseMessage("Document not found or access denied", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("Error deleting document: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error deleting document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Process unprocessed documents
     */
    @PostMapping("/process-unprocessed")
    @Operation(summary = "Process unprocessed documents", description = "Process documents that failed initial processing")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> processUnprocessedDocuments() {
        try {
            Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getId();
            
            List<Document> processedDocs = documentService.processUnprocessedDocuments(ownerId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("Processed %d documents", processedDocs.size()));
            response.put("processedDocuments", processedDocs);
            response.put("timestamp", System.currentTimeMillis());

            return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error processing unprocessed documents: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error processing documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get supported file types
     */
    @GetMapping("/supported-types")
    @Operation(summary = "Get supported file types", description = "Get list of supported file types for upload")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> getSupportedFileTypes() {
        try {
            String[] supportedTypes = com.vijay.User_Master.service.impl.DocumentProcessingServiceImpl.class
                    .getDeclaredConstructor().newInstance()
                    .getSupportedFileTypes();

            Map<String, Object> response = new HashMap<>();
            response.put("supportedTypes", supportedTypes);
            response.put("maxFileSize", "50MB");
            response.put("description", "Supported file types for document upload");

            return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error getting supported file types: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error retrieving supported types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
