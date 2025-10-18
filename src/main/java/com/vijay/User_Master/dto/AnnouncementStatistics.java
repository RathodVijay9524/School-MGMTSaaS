package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for Announcement statistics and analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementStatistics {

    // Overall counts
    private long totalAnnouncements;
    private long activeAnnouncements;
    private long draftAnnouncements;
    private long publishedAnnouncements;
    private long archivedAnnouncements;
    private long expiredAnnouncements;
    private long cancelledAnnouncements;
    
    // By type
    private Map<String, Long> announcementsByType;
    private long generalAnnouncements;
    private long urgentAnnouncements;
    private long academicAnnouncements;
    private long administrativeAnnouncements;
    private long eventAnnouncements;
    
    // By priority
    private Map<String, Long> announcementsByPriority;
    private long highPriorityCount;
    private long mediumPriorityCount;
    private long lowPriorityCount;
    
    // By audience
    private Map<String, Long> announcementsByAudience;
    private long allAudienceCount;
    private long studentsOnlyCount;
    private long teachersOnlyCount;
    private long parentsOnlyCount;
    private long specificClassCount;
    
    // Engagement metrics
    private long totalViews;
    private Double averageViewsPerAnnouncement;
    private long pinnedAnnouncementsCount;
    
    // Notification metrics
    private long emailNotificationsSent;
    private long smsNotificationsSent;
    
    // Time-based metrics
    private long upcomingAnnouncements; // Not yet published
    private long expiringWithin7Days;
    private long publishedLast30Days;
    private long publishedToday;
    
    // Top 5 most viewed announcements
    private Map<String, Integer> topViewedAnnouncements; // Title -> view count
}
