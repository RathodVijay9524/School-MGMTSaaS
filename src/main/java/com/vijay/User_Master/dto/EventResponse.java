package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for Event response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String eventName;
    private String description;
    private Event.EventType eventType;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String venue;
    private Long organizerId;
    private String organizerName;
    private Event.EventStatus status;
    private Event.EventAudience audience;
    private Long targetClassId;
    private String targetClassName;
    private Integer expectedParticipants;
    private Integer registeredParticipants;
    private boolean isPublic;
    private boolean requiresRegistration;
    private String bannerImageUrl;
    private String notes;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;

    // Computed fields
    private boolean isUpcoming;
    private boolean isOngoing;
    private boolean isCompleted;
    private boolean isOverdue;
    private long daysUntilEvent;
    private long daysOverdue;
    private long durationHours;
    private String statusDisplay;
    private String typeDisplay;
    private String audienceDisplay;
    private String organizerDisplay;
    private String timeDisplay;
    private String registrationStatus;
    private String participationStatus;
}
