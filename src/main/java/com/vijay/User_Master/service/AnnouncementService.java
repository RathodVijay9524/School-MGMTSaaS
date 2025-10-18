package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AnnouncementRequest;
import com.vijay.User_Master.dto.AnnouncementResponse;
import com.vijay.User_Master.dto.AnnouncementStatistics;
import com.vijay.User_Master.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Announcement management
 */
public interface AnnouncementService {

    /**
     * Create a new announcement
     */
    AnnouncementResponse createAnnouncement(AnnouncementRequest request, Long ownerId);

    /**
     * Update an existing announcement
     */
    AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request, Long ownerId);

    /**
     * Get announcement by ID
     */
    AnnouncementResponse getAnnouncementById(Long id, Long ownerId);

    /**
     * Get all announcements for owner with pagination
     */
    Page<AnnouncementResponse> getAllAnnouncements(Long ownerId, Pageable pageable);

    /**
     * Get announcements by type
     */
    Page<AnnouncementResponse> getAnnouncementsByType(Announcement.AnnouncementType type, Long ownerId, Pageable pageable);

    /**
     * Get announcements by priority
     */
    Page<AnnouncementResponse> getAnnouncementsByPriority(Announcement.Priority priority, Long ownerId, Pageable pageable);

    /**
     * Get announcements by status
     */
    Page<AnnouncementResponse> getAnnouncementsByStatus(Announcement.AnnouncementStatus status, Long ownerId, Pageable pageable);

    /**
     * Get announcements by target audience
     */
    List<AnnouncementResponse> getAnnouncementsByAudience(Announcement.AnnouncementAudience audience, Long ownerId);

    /**
     * Get active announcements (published and not expired)
     */
    Page<AnnouncementResponse> getActiveAnnouncements(Long ownerId, Pageable pageable);

    /**
     * Get pinned announcements
     */
    List<AnnouncementResponse> getPinnedAnnouncements(Long ownerId);

    /**
     * Get announcements by date range
     */
    List<AnnouncementResponse> getAnnouncementsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Long ownerId);

    /**
     * Get announcements by creator
     */
    Page<AnnouncementResponse> getAnnouncementsByCreator(Long creatorId, Long ownerId, Pageable pageable);

    /**
     * Get announcements for specific class
     */
    List<AnnouncementResponse> getAnnouncementsForClass(Long classId, Long ownerId);

    /**
     * Search announcements by keyword
     */
    Page<AnnouncementResponse> searchAnnouncements(String keyword, Long ownerId, Pageable pageable);

    /**
     * Publish announcement (change status from DRAFT to PUBLISHED)
     */
    AnnouncementResponse publishAnnouncement(Long id, Long ownerId);

    /**
     * Archive announcement
     */
    AnnouncementResponse archiveAnnouncement(Long id, Long ownerId);

    /**
     * Pin announcement to top
     */
    AnnouncementResponse pinAnnouncement(Long id, Long ownerId);

    /**
     * Unpin announcement
     */
    AnnouncementResponse unpinAnnouncement(Long id, Long ownerId);

    /**
     * Increment view count
     */
    void incrementViewCount(Long id, Long ownerId);

    /**
     * Send announcement notifications (email/SMS)
     */
    void sendAnnouncementNotifications(Long id, Long ownerId);

    /**
     * Delete announcement (soft delete)
     */
    void deleteAnnouncement(Long id, Long ownerId);

    /**
     * Restore deleted announcement
     */
    void restoreAnnouncement(Long id, Long ownerId);

    /**
     * Get announcement statistics
     */
    AnnouncementStatistics getAnnouncementStatistics(Long ownerId);

    /**
     * Check and expire announcements past their expiry date
     */
    void checkAndExpireAnnouncements();
}
