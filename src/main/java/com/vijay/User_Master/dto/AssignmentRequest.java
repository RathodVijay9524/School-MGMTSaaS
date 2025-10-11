package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Assignment;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Assignment creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {

    @NotBlank(message = "Assignment title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    @NotNull(message = "Assignment type is required")
    private Assignment.AssignmentType assignmentType;

    @NotNull(message = "Assigned date is required")
    private LocalDateTime assignedDate;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDateTime dueDate;

    @DecimalMin(value = "0.0", message = "Total marks must be positive")
    @DecimalMax(value = "1000.0", message = "Total marks cannot exceed 1000")
    private Double totalMarks;

    @Builder.Default
    private Assignment.AssignmentStatus status = Assignment.AssignmentStatus.ASSIGNED;

    @Size(max = 500, message = "Attachment URL cannot exceed 500 characters")
    private String attachmentUrl;

    @Size(max = 1000, message = "Instructions cannot exceed 1000 characters")
    private String instructions;

    @Min(value = 0, message = "Submissions count cannot be negative")
    private Integer submissionsCount;

    @Min(value = 1, message = "Total students must be at least 1")
    private Integer totalStudents;

    @Builder.Default
    private boolean allowLateSubmission = true;

    @Min(value = 0, message = "Late penalty percentage cannot be negative")
    @Max(value = 100, message = "Late penalty percentage cannot exceed 100")
    private Integer latePenaltyPercentage;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
