package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Event;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Event creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Event name cannot be empty")
    @Size(max = 200, message = "Event name cannot exceed 200 characters")
    private String eventName;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Event type is required")
    private Event.EventType eventType;

    @NotNull(message = "Start date and time is required")
    @Future(message = "Start date and time must be in the future")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    private LocalDateTime endDateTime;

    @Size(max = 200, message = "Venue cannot exceed 200 characters")
    private String venue;

    private Long organizerId; // Teacher ID who organizes the event

    @Builder.Default
    private Event.EventStatus status = Event.EventStatus.SCHEDULED;

    @NotNull(message = "Event audience is required")
    private Event.EventAudience audience;

    private Long targetClassId; // If specific to a class

    @Min(value = 1, message = "Expected participants must be at least 1")
    private Integer expectedParticipants;

    @Min(value = 0, message = "Registered participants cannot be negative")
    private Integer registeredParticipants;

    @Builder.Default
    private boolean isPublic = false;

    @Builder.Default
    private boolean requiresRegistration = false;

    @Size(max = 500, message = "Banner image URL cannot exceed 500 characters")
    private String bannerImageUrl;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    private String contactPerson;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid phone number format")
    private String contactPhone;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String contactEmail;
}
