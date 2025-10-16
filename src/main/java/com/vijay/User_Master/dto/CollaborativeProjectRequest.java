package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Collaborative Project requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaborativeProjectRequest {

    @NotBlank(message = "Project name cannot be empty")
    @Size(max = 200, message = "Project name cannot exceed 200 characters")
    private String projectName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Subject cannot be empty")
    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    private String subject;

    @NotBlank(message = "Grade level cannot be empty")
    @Size(max = 50, message = "Grade level cannot exceed 50 characters")
    private String gradeLevel;

    @NotBlank(message = "Topic cannot be empty")
    @Size(max = 200, message = "Topic cannot exceed 200 characters")
    private String topic;

    @Min(value = 2, message = "Max members must be at least 2")
    private Integer maxMembers;

    @NotNull(message = "Creator ID is required")
    private Long creatorId;

    private String startDate;

    private String endDate;

    @Size(max = 2000, message = "Learning objectives cannot exceed 2000 characters")
    private String learningObjectives;
}
