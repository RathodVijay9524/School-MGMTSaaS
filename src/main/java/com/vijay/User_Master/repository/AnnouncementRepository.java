package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // Find by type
    Page<Announcement> findByAnnouncementTypeAndIsDeletedFalse(
            Announcement.AnnouncementType announcementType, Pageable pageable);
    
    // Find by priority
    Page<Announcement> findByPriorityAndIsDeletedFalse(Announcement.Priority priority, Pageable pageable);
    
    // Find by status
    Page<Announcement> findByStatusAndIsDeletedFalse(Announcement.AnnouncementStatus status, Pageable pageable);
    
    // Find by target audience
    List<Announcement> findByTargetAudienceAndIsDeletedFalse(Announcement.AnnouncementAudience targetAudience);
    
    // Find active announcements
    @Query("SELECT a FROM Announcement a WHERE a.status = 'PUBLISHED' AND a.isDeleted = false AND " +
           "(a.expiryDate IS NULL OR a.expiryDate > :currentDateTime) ORDER BY a.isPinned DESC, a.publishDate DESC")
    Page<Announcement> findActiveAnnouncements(@Param("currentDateTime") LocalDateTime currentDateTime, Pageable pageable);
    
    // Find pinned announcements
    @Query("SELECT a FROM Announcement a WHERE a.isPinned = true AND a.status = 'PUBLISHED' AND a.isDeleted = false")
    List<Announcement> findPinnedAnnouncements();
    
    // Find by date range
    List<Announcement> findByPublishDateBetweenAndIsDeletedFalse(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find by creator
    Page<Announcement> findByAnnouncementCreatedBy_IdAndIsDeletedFalse(Long createdById, Pageable pageable);
    
    // Search announcements
    @Query("SELECT a FROM Announcement a WHERE a.isDeleted = false AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Announcement> searchAnnouncements(@Param("keyword") String keyword, Pageable pageable);
    
    // Find for specific class
    List<Announcement> findByTargetClass_IdAndIsDeletedFalse(Long classId);
}

