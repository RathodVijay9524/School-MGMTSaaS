package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating question tags
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTagRequest {

    @NotBlank(message = "Tag name is required")
    private String tagName;

    private String description;
}
