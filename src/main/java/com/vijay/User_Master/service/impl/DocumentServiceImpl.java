package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.DocumentStatistics;
import com.vijay.User_Master.entity.Document;
import com.vijay.User_Master.repository.DocumentRepository;
import com.vijay.User_Master.service.DocumentProcessingService;
import com.vijay.User_Master.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of DocumentService for managing school documents
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentProcessingService documentProcessingService;

    @Override
    // @Tool(name = "uploadDocument", description = "Upload and process a document for the RAG system")
    public Document uploadDocument(MultipartFile file, Long ownerId, String category, String description, String tags) {
        log.info("Uploading document: {} for owner: {}", file.getOriginalFilename(), ownerId);

        try {
            // Process the document
            DocumentProcessingService.DocumentProcessingResult result = 
                documentProcessingService.processDocument(file, ownerId, category, description, tags);

            if (!result.isSuccess()) {
                throw new RuntimeException("Document processing failed: " + result.getErrorMessage());
            }

            // Save document to database
            Document document = result.getDocument();
            document = documentRepository.save(document);

            log.info("Successfully uploaded document: {} with ID: {}", file.getOriginalFilename(), document.getId());
            return document;

        } catch (Exception e) {
            log.error("Error uploading document: {}", e.getMessage(), e);
            throw new RuntimeException("Error uploading document: " + e.getMessage());
        }
    }

    @Override
    public Optional<Document> getDocumentById(Long id, Long ownerId) {
        return documentRepository.findById(id)
                .filter(doc -> doc.getOwnerId().equals(ownerId) && !doc.getDeleted());
    }

    @Override
    public Page<Document> getDocuments(Long ownerId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return documentRepository.findByOwnerIdAndDeletedFalseOrderByCreatedAtDesc(ownerId, pageable);
    }

    @Override
    public Page<Document> getDocumentsByCategory(Long ownerId, String category, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return documentRepository.findByOwnerIdAndCategoryAndDeletedFalseOrderByCreatedAtDesc(ownerId, category, pageable);
    }

    @Override
    public Page<Document> getDocumentsByFileType(Long ownerId, String fileType, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return documentRepository.findByOwnerIdAndFileTypeAndDeletedFalseOrderByCreatedAtDesc(ownerId, fileType, pageable);
    }

    @Override
    // @Tool(name = "searchDocuments", description = "Search documents by content, filename, or tags")
    public Page<Document> searchDocuments(Long ownerId, String query, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return documentRepository.searchDocumentsByContent(ownerId, query, pageable);
    }

    @Override
    public Page<Document> searchDocumentsByTags(Long ownerId, String tags, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return documentRepository.findByOwnerIdAndTagsContainingIgnoreCaseAndDeletedFalse(ownerId, tags, pageable);
    }

    @Override
    public Page<Document> getProcessedDocuments(Long ownerId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return documentRepository.findByOwnerIdAndIsProcessedTrueAndDeletedFalseOrderByCreatedAtDesc(ownerId, pageable);
    }

    @Override
    public List<Document> getUnprocessedDocuments(Long ownerId) {
        return documentRepository.findByOwnerIdAndIsProcessedFalseAndDeletedFalseOrderByCreatedAtAsc(ownerId);
    }

    @Override
    public List<Document> processUnprocessedDocuments(Long ownerId) {
        log.info("Processing unprocessed documents for owner: {}", ownerId);
        
        List<Document> unprocessedDocs = getUnprocessedDocuments(ownerId);
        List<Document> processedDocs = new java.util.ArrayList<>();

        for (Document doc : unprocessedDocs) {
            try {
                // Re-process the document
                DocumentProcessingService.DocumentProcessingResult result = 
                    documentProcessingService.processDocumentFromPath(
                        doc.getFilePath(), doc.getOwnerId(), doc.getCategory(), doc.getDescription(), doc.getTags());

                if (result.isSuccess()) {
                    // Update document with extracted content
                    doc.setExtractedContent(result.getExtractedContent());
                    doc.setSummary(result.getSummary());
                    doc.setIsProcessed(true);
                    doc.setProcessedAt(LocalDateTime.now());
                    doc.setUpdatedAt(LocalDateTime.now());

                    documentRepository.save(doc);
                    processedDocs.add(doc);
                    
                    log.info("Successfully processed document: {}", doc.getOriginalFileName());
                } else {
                    log.error("Failed to process document: {} - {}", doc.getOriginalFileName(), result.getErrorMessage());
                }

            } catch (Exception e) {
                log.error("Error processing document: {} - {}", doc.getOriginalFileName(), e.getMessage());
            }
        }

        log.info("Processed {}/{} documents for owner: {}", processedDocs.size(), unprocessedDocs.size(), ownerId);
        return processedDocs;
    }

    @Override
    public Document updateDocument(Long id, Long ownerId, String category, String description, String tags) {
        Optional<Document> documentOpt = getDocumentById(id, ownerId);
        if (documentOpt.isEmpty()) {
            throw new RuntimeException("Document not found or access denied");
        }

        Document document = documentOpt.get();
        if (category != null) document.setCategory(category);
        if (description != null) document.setDescription(description);
        if (tags != null) document.setTags(tags);
        document.setUpdatedAt(LocalDateTime.now());

        return documentRepository.save(document);
    }

    @Override
    public boolean deleteDocument(Long id, Long ownerId) {
        Optional<Document> documentOpt = getDocumentById(id, ownerId);
        if (documentOpt.isEmpty()) {
            return false;
        }

        Document document = documentOpt.get();
        document.setDeleted(true);
        document.setDeletedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        documentRepository.save(document);
        log.info("Soft deleted document: {} for owner: {}", document.getOriginalFileName(), ownerId);
        return true;
    }

    @Override
    public DocumentStatistics getDocumentStatistics(Long ownerId) {
        long totalDocuments = documentRepository.countByOwnerIdAndDeletedFalse(ownerId);
        long processedDocuments = documentRepository.countByOwnerIdAndIsProcessedTrueAndDeletedFalse(ownerId);
        long unprocessedDocuments = totalDocuments - processedDocuments;

        // Get statistics by type
        List<Object[]> typeStats = documentRepository.getDocumentStatisticsByType(ownerId);
        
        // Calculate average file size and total storage
        Page<Document> allDocs = getDocuments(ownerId, 0, 1000, "createdAt", "desc");
        long totalSize = 0;
        long pdfCount = 0, docCount = 0, docxCount = 0, xlsCount = 0, xlsxCount = 0, pptCount = 0, pptxCount = 0;
        
        for (Document doc : allDocs.getContent()) {
            totalSize += doc.getFileSize();
            switch (doc.getFileType().toLowerCase()) {
                case "pdf": pdfCount++; break;
                case "doc": docCount++; break;
                case "docx": docxCount++; break;
                case "xls": xlsCount++; break;
                case "xlsx": xlsxCount++; break;
                case "ppt": pptCount++; break;
                case "pptx": pptxCount++; break;
            }
        }
        
        double averageFileSize = allDocs.getTotalElements() > 0 ? (double) totalSize / allDocs.getTotalElements() : 0;

        return new DocumentStatistics(
            totalDocuments,
            processedDocuments,
            unprocessedDocuments,
            typeStats.size(),
            Document.DocumentCategory.values().length,
            averageFileSize,
            totalSize,
            pdfCount,
            docCount,
            docxCount,
            xlsCount,
            xlsxCount,
            pptCount,
            pptxCount
        );
    }

    @Override
    // @Tool(name = "getDocumentsForRAGQuery", description = "Get relevant documents for RAG queries")
    public List<Document> getDocumentsForRAGQuery(Long ownerId, String query) {
        // Get relevant documents based on content search
        Page<Document> searchResults = searchDocuments(ownerId, query, 0, 10, "createdAt", "desc");
        
        // Also search by tags if query contains specific terms
        String[] queryWords = query.toLowerCase().split("\\s+");
        for (String word : queryWords) {
            searchDocumentsByTags(ownerId, word, 0, 5, "createdAt", "desc");
            // Merge results (simplified - in real implementation, you'd want to deduplicate)
        }

        return searchResults.getContent();
    }

    @Override
    public List<Document> bulkUploadDocuments(List<MultipartFile> files, Long ownerId, String category, String description, String tags) {
        List<Document> uploadedDocuments = new java.util.ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Document document = uploadDocument(file, ownerId, category, description, tags);
                uploadedDocuments.add(document);
                log.info("Successfully uploaded document: {}", file.getOriginalFilename());
            } catch (Exception e) {
                log.error("Failed to upload document: {} - {}", file.getOriginalFilename(), e.getMessage());
                // Continue with other files
            }
        }

        log.info("Bulk upload completed: {}/{} documents uploaded successfully", 
                uploadedDocuments.size(), files.size());
        return uploadedDocuments;
    }
}
