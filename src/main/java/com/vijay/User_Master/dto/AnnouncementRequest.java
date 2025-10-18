package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Announcement;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Announcement creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 3000, message = "Content must be between 10 and 3000 characters")
    private String content;

    @NotNull(message = "Announcement type is required")
    private Announcement.AnnouncementType announcementType;

    @NotNull(message = "Priority is required")
    private Announcement.Priority priority;

    @NotNull(message = "Target audience is required")
    private Announcement.AnnouncementAudience targetAudience;

    private Long targetClassId; // Required if targetAudience is SPECIFIC_CLASS

    @NotNull(message = "Publish date is required")
    @FutureOrPresent(message = "Publish date cannot be in the past")
    private LocalDateTime publishDate;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;

    @Builder.Default
    private Announcement.AnnouncementStatus status = Announcement.AnnouncementStatus.DRAFT;

    @Builder.Default
    private boolean isPinned = false;

    @Builder.Default
    private boolean sendEmail = false;

    @Builder.Default
    private boolean sendSMS = false;

    @Size(max = 500, message = "Attachment URL cannot exceed 500 characters")
    private String attachmentUrl;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
