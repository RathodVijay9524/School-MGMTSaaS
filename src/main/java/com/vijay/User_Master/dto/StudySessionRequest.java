package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for StudySession creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionRequest {

    @NotBlank(message = "Session title cannot be empty")
    @Size(max = 200, message = "Session title cannot exceed 200 characters")
    private String sessionTitle;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Topic cannot be empty")
    @Size(max = 200, message = "Topic cannot exceed 200 characters")
    private String topic;

    @NotBlank(message = "Session type is required")
    private String sessionType;

    private String scheduledDate;

    private String location;

    private String meetingLink;

    @Size(max = 2000, message = "Agenda cannot exceed 2000 characters")
    private String agenda;

    private String materials;

    private String sessionNotes;

    private String feedback;

    @Builder.Default
    private Boolean isRecorded = false;

    private String recordingLink;

    @Builder.Default
    private Boolean isActive = true;

    private Long studyGroupId;

    private Long facilitatorId;
}
