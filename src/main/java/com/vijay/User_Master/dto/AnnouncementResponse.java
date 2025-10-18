package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for Announcement response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {

    private Long id;
    private String title;
    private String content;
    
    private Announcement.AnnouncementType announcementType;
    private Announcement.Priority priority;
    private Announcement.AnnouncementAudience targetAudience;
    
    // Target class details (if applicable)
    private Long targetClassId;
    private String targetClassName;
    
    // Creator details
    private Long createdById;
    private String createdByName;
    private String createdByEmail;
    
    private LocalDateTime publishDate;
    private LocalDateTime expiryDate;
    private Announcement.AnnouncementStatus status;
    
    private boolean isPinned;
    private boolean sendEmail;
    private boolean sendSMS;
    
    private String attachmentUrl;
    private Integer viewCount;
    private String notes;
    
    // Owner details
    private Long ownerId;
    private String ownerName;
    
    // Audit fields
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;
    
    // ========== COMPUTED FIELDS ==========
    
    // Status flags
    private boolean isActive;
    private boolean isExpired;
    private boolean isDraft;
    private boolean isPublished;
    private boolean isArchived;
    
    // Time metrics
    private boolean isUpcoming; // Not yet published
    private long daysUntilPublish;
    private long daysUntilExpiry;
    private long daysSincePublished;
    
    // Display fields
    private String typeDisplay;
    private String priorityDisplay;
    private String audienceDisplay;
    private String statusDisplay;
    private String timeRemainingDisplay; // e.g., "Expires in 5 days"
    
    // Badge indicators for UI
    private boolean showUrgentBadge;
    private boolean showNewBadge; // Published within last 24 hours
    private boolean showExpiringBadge; // Expires within 3 days
}
