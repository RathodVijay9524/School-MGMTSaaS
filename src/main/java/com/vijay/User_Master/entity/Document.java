package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Document entity for storing school documents in RAG system
 * Supports PDF, Word, Excel, PowerPoint files
 */
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String fileType; // PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String extractedContent; // Text content extracted from document

    @Column(columnDefinition = "TEXT")
    private String summary; // AI-generated summary of the document

    @Column(nullable = false)
    private Long ownerId; // School/tenant ID

    @Column(nullable = false)
    private String category; // academic, administrative, policy, etc.

    @Column
    private String tags; // Comma-separated tags for better search

    @Column
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isProcessed = false; // Whether content extraction is complete

    @Column
    private LocalDateTime processedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column
    private LocalDateTime deletedAt;

    // Document types enum
    public enum DocumentType {
        PDF("application/pdf"),
        DOC("application/msword"),
        DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        XLS("application/vnd.ms-excel"),
        XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        PPT("application/vnd.ms-powerpoint"),
        PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation");

        private final String mimeType;

        DocumentType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }

        public static DocumentType fromMimeType(String mimeType) {
            for (DocumentType type : values()) {
                if (type.mimeType.equals(mimeType)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
        }

        public static DocumentType fromFileName(String fileName) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            switch (extension) {
                case "pdf": return PDF;
                case "doc": return DOC;
                case "docx": return DOCX;
                case "xls": return XLS;
                case "xlsx": return XLSX;
                case "ppt": return PPT;
                case "pptx": return PPTX;
                default: throw new IllegalArgumentException("Unsupported file extension: " + extension);
            }
        }
    }

    // Document categories
    public enum DocumentCategory {
        ACADEMIC("Academic materials, lesson plans, curriculum"),
        ADMINISTRATIVE("Administrative documents, reports, policies"),
        STUDENT("Student records, assignments, grades"),
        TEACHER("Teacher resources, training materials"),
        POLICY("School policies, rules, regulations"),
        FINANCIAL("Budget, financial reports, fee structures"),
        GENERAL("General documents, announcements, newsletters");

        private final String description;

        DocumentCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
