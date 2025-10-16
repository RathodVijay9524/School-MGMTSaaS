package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Discussion Reply requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionReplyRequest {

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 2000, message = "Content cannot exceed 2000 characters")
    private String content;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    private Long parentReplyId;
}
