package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Discussion Topic requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionTopicRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 2000, message = "Content cannot exceed 2000 characters")
    private String content;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    private Long studyGroupId;

    private String category;

    private String tags;
}
