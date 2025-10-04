package com.vijay.User_Master.service;

import com.vijay.User_Master.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Service for processing documents and extracting text content
 */
public interface DocumentProcessingService {

    /**
     * Process uploaded file and extract text content
     */
    DocumentProcessingResult processDocument(MultipartFile file, Long ownerId, String category, String description, String tags);

    /**
     * Process document from file path and extract text content
     */
    DocumentProcessingResult processDocumentFromPath(String filePath, Long ownerId, String category, String description, String tags);

    /**
     * Extract text content from document input stream
     */
    String extractTextContent(InputStream inputStream, String fileName, String mimeType);

    /**
     * Generate summary of document content
     */
    String generateDocumentSummary(String content, String fileName, String category);

    /**
     * Validate document file
     */
    DocumentValidationResult validateDocument(MultipartFile file);

    /**
     * Get supported file types
     */
    String[] getSupportedFileTypes();

    /**
     * Check if file type is supported
     */
    boolean isSupportedFileType(String fileName, String mimeType);

    /**
     * Document processing result
     */
    class DocumentProcessingResult {
        private boolean success;
        private String extractedContent;
        private String summary;
        private String errorMessage;
        private Document document;
        private String filePath;

        public DocumentProcessingResult(boolean success, String extractedContent, String summary, String errorMessage, Document document, String filePath) {
            this.success = success;
            this.extractedContent = extractedContent;
            this.summary = summary;
            this.errorMessage = errorMessage;
            this.document = document;
            this.filePath = filePath;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getExtractedContent() { return extractedContent; }
        public String getSummary() { return summary; }
        public String getErrorMessage() { return errorMessage; }
        public Document getDocument() { return document; }
        public String getFilePath() { return filePath; }

        // Static factory methods
        public static DocumentProcessingResult success(String extractedContent, String summary, Document document, String filePath) {
            return new DocumentProcessingResult(true, extractedContent, summary, null, document, filePath);
        }

        public static DocumentProcessingResult error(String errorMessage) {
            return new DocumentProcessingResult(false, null, null, errorMessage, null, null);
        }
    }

    /**
     * Document validation result
     */
    class DocumentValidationResult {
        private boolean valid;
        private String errorMessage;
        private String fileType;
        private String mimeType;

        public DocumentValidationResult(boolean valid, String errorMessage, String fileType, String mimeType) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.fileType = fileType;
            this.mimeType = mimeType;
        }

        // Getters
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
        public String getFileType() { return fileType; }
        public String getMimeType() { return mimeType; }

        // Static factory methods
        public static DocumentValidationResult valid(String fileType, String mimeType) {
            return new DocumentValidationResult(true, null, fileType, mimeType);
        }

        public static DocumentValidationResult invalid(String errorMessage) {
            return new DocumentValidationResult(false, errorMessage, null, null);
        }
    }
}
