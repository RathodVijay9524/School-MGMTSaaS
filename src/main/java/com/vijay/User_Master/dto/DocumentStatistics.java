package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for document statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatistics {
    
    private long totalDocuments;
    private long processedDocuments;
    private long unprocessedDocuments;
    private int uniqueFileTypes;
    private int uniqueCategories;
    private double averageFileSize;
    private long totalStorageUsed;
    
    // Additional statistics
    private long pdfCount;
    private long docCount;
    private long docxCount;
    private long xlsCount;
    private long xlsxCount;
    private long pptCount;
    private long pptxCount;
    
    public DocumentStatistics(long totalDocuments, long processedDocuments, long unprocessedDocuments, 
                            int uniqueFileTypes, int uniqueCategories, double averageFileSize, long totalStorageUsed) {
        this.totalDocuments = totalDocuments;
        this.processedDocuments = processedDocuments;
        this.unprocessedDocuments = unprocessedDocuments;
        this.uniqueFileTypes = uniqueFileTypes;
        this.uniqueCategories = uniqueCategories;
        this.averageFileSize = averageFileSize;
        this.totalStorageUsed = totalStorageUsed;
    }
}
