package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Peer Tutoring session requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerTutoringRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Topic cannot be empty")
    @Size(max = 200, message = "Topic cannot exceed 200 characters")
    private String topic;

    @NotNull(message = "Study group ID is required")
    private Long studyGroupId;

    @NotNull(message = "Tutor ID is required")
    private Long tutorId;

    private String scheduledDate;

    private String location;

    private String meetingLink;

    @Size(max = 2000, message = "Agenda cannot exceed 2000 characters")
    private String agenda;

    private String materials;
}
