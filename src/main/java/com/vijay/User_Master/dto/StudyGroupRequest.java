package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for StudyGroup creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupRequest {

    @NotBlank(message = "Group name cannot be empty")
    @Size(max = 200, message = "Group name cannot exceed 200 characters")
    private String groupName;

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

    @Size(max = 2000, message = "Learning objectives cannot exceed 2000 characters")
    private String learningObjectives;

    @NotBlank(message = "Group type is required")
    private String groupType;

    @Min(value = 2, message = "Max members must be at least 2")
    private Integer maxMembers;

    private String studySchedule;

    private String meetingLink;

    private String chatRoomId;

    @Builder.Default
    private Boolean isPublic = true;

    @Builder.Default
    private Boolean requiresApproval = false;

    @Builder.Default
    private Boolean isActive = true;

    private String startDate;

    private String endDate;

    private Long creatorId;
}
