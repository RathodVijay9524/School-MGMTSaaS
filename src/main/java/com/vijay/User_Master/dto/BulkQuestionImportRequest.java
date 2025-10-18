package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk importing questions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkQuestionImportRequest {

    @NotNull(message = "Questions list is required")
    private List<QuestionRequest> questions;

    private Long defaultSubjectId;
    private Long defaultClassId;
    private Boolean skipDuplicates;
    private Boolean validateBeforeImport;
}
