package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk import response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkQuestionImportResponse {

    private Integer totalQuestions;
    private Integer successfulImports;
    private Integer failedImports;
    private Integer skippedDuplicates;
    
    private List<ImportError> errors;
    private List<Long> importedQuestionIds;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private Integer questionIndex;
        private String questionText;
        private String errorMessage;
    }
}
