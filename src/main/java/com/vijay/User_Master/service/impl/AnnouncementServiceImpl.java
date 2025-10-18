package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.AnnouncementRequest;
import com.vijay.User_Master.dto.AnnouncementResponse;
import com.vijay.User_Master.dto.AnnouncementStatistics;
import com.vijay.User_Master.entity.Announcement;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.BusinessRuleViolationException;
import com.vijay.User_Master.exceptions.EntityNotFoundException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.AnnouncementRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.AnnouncementService;
import com.vijay.User_Master.service.SchoolNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for Announcement management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolNotificationService notificationService;

    @Override
    @Tool(name = "createAnnouncement", description = "Create a new announcement with title, content, type, priority, and target audience")
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request, Long ownerId) {
        log.info("Creating announcement: {} for owner: {}", request.getTitle(), ownerId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner", ownerId));
        
        if (request.getExpiryDate() != null && request.getExpiryDate().isBefore(request.getPublishDate())) {
            throw new BusinessRuleViolationException("Expiry date cannot be before publish date");
        }
        
        SchoolClass targetClass = null;
        if (request.getTargetAudience() == Announcement.AnnouncementAudience.SPECIFIC_CLASS) {
            if (request.getTargetClassId() == null) {
                throw new BusinessRuleViolationException("Target class must be specified for SPECIFIC_CLASS audience");
            }
            targetClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getTargetClassId(), ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", request.getTargetClassId()));
        }
        
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .announcementType(request.getAnnouncementType())
                .priority(request.getPriority())
                .targetAudience(request.getTargetAudience())
                .targetClass(targetClass)
                .announcementCreatedBy(owner)
                .publishDate(request.getPublishDate())
                .expiryDate(request.getExpiryDate())
                .status(request.getStatus())
                .isPinned(request.isPinned())
                .sendEmail(request.isSendEmail())
                .sendSMS(request.isSendSMS())
                .attachmentUrl(request.getAttachmentUrl())
                .viewCount(0)
                .notes(request.getNotes())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        log.info("Announcement created successfully with ID: {}", savedAnnouncement.getId());
        
        if (savedAnnouncement.getStatus() == Announcement.AnnouncementStatus.PUBLISHED) {
            sendAnnouncementNotifications(savedAnnouncement.getId(), ownerId);
        }
        
        return convertToResponse(savedAnnouncement);
    }

    @Override
    @Tool(name = "updateAnnouncement", description = "Update announcement details")
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request, Long ownerId) {
        log.info("Updating announcement: {} for owner: {}", id, ownerId);
        
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        if (request.getExpiryDate() != null && request.getExpiryDate().isBefore(request.getPublishDate())) {
            throw new BusinessRuleViolationException("Expiry date cannot be before publish date");
        }
        
        SchoolClass targetClass = null;
        if (request.getTargetAudience() == Announcement.AnnouncementAudience.SPECIFIC_CLASS) {
            if (request.getTargetClassId() == null) {
                throw new BusinessRuleViolationException("Target class must be specified for SPECIFIC_CLASS audience");
            }
            targetClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getTargetClassId(), ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", request.getTargetClassId()));
        }
        
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setAnnouncementType(request.getAnnouncementType());
        announcement.setPriority(request.getPriority());
        announcement.setTargetAudience(request.getTargetAudience());
        announcement.setTargetClass(targetClass);
        announcement.setPublishDate(request.getPublishDate());
        announcement.setExpiryDate(request.getExpiryDate());
        announcement.setStatus(request.getStatus());
        announcement.setPinned(request.isPinned());
        announcement.setSendEmail(request.isSendEmail());
        announcement.setSendSMS(request.isSendSMS());
        announcement.setAttachmentUrl(request.getAttachmentUrl());
        announcement.setNotes(request.getNotes());
        
        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        log.info("Announcement updated successfully");
        
        return convertToResponse(updatedAnnouncement);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getAnnouncementById", description = "Get announcement details by ID")
    public AnnouncementResponse getAnnouncementById(Long id, Long ownerId) {
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        return convertToResponse(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> getAllAnnouncements(Long ownerId, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findAll(pageable);
        return announcements.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> getAnnouncementsByType(Announcement.AnnouncementType type, Long ownerId, Pageable pageable) {
        return announcementRepository.findByAnnouncementTypeAndIsDeletedFalse(type, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> getAnnouncementsByPriority(Announcement.Priority priority, Long ownerId, Pageable pageable) {
        return announcementRepository.findByPriorityAndIsDeletedFalse(priority, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> getAnnouncementsByStatus(Announcement.AnnouncementStatus status, Long ownerId, Pageable pageable) {
        return announcementRepository.findByStatusAndIsDeletedFalse(status, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsByAudience(Announcement.AnnouncementAudience audience, Long ownerId) {
        return announcementRepository.findByTargetAudienceAndIsDeletedFalse(audience).stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getActiveAnnouncements", description = "Get all active announcements that are published and not expired")
    public Page<AnnouncementResponse> getActiveAnnouncements(Long ownerId, Pageable pageable) {
        return announcementRepository.findActiveAnnouncements(LocalDateTime.now(), pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getPinnedAnnouncements(Long ownerId) {
        return announcementRepository.findPinnedAnnouncements().stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Long ownerId) {
        return announcementRepository.findByPublishDateBetweenAndIsDeletedFalse(startDate, endDate).stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> getAnnouncementsByCreator(Long creatorId, Long ownerId, Pageable pageable) {
        return announcementRepository.findByAnnouncementCreatedBy_IdAndIsDeletedFalse(creatorId, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsForClass(Long classId, Long ownerId) {
        return announcementRepository.findByTargetClass_IdAndIsDeletedFalse(classId).stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> searchAnnouncements(String keyword, Long ownerId, Pageable pageable) {
        return announcementRepository.searchAnnouncements(keyword, pageable).map(this::convertToResponse);
    }

    @Override
    @Tool(name = "publishAnnouncement", description = "Publish an announcement and send notifications")
    public AnnouncementResponse publishAnnouncement(Long id, Long ownerId) {
        log.info("Publishing announcement: {}", id);
        
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        if (announcement.getStatus() == Announcement.AnnouncementStatus.PUBLISHED) {
            throw new BusinessRuleViolationException("Announcement is already published");
        }
        
        announcement.setStatus(Announcement.AnnouncementStatus.PUBLISHED);
        Announcement publishedAnnouncement = announcementRepository.save(announcement);
        
        sendAnnouncementNotifications(id, ownerId);
        
        log.info("Announcement published successfully");
        return convertToResponse(publishedAnnouncement);
    }

    @Override
    public AnnouncementResponse archiveAnnouncement(Long id, Long ownerId) {
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        announcement.setStatus(Announcement.AnnouncementStatus.ARCHIVED);
        return convertToResponse(announcementRepository.save(announcement));
    }

    @Override
    public AnnouncementResponse pinAnnouncement(Long id, Long ownerId) {
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        announcement.setPinned(true);
        return convertToResponse(announcementRepository.save(announcement));
    }

    @Override
    public AnnouncementResponse unpinAnnouncement(Long id, Long ownerId) {
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        announcement.setPinned(false);
        return convertToResponse(announcementRepository.save(announcement));
    }

    @Override
    public void incrementViewCount(Long id, Long ownerId) {
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        announcement.setViewCount((announcement.getViewCount() != null ? announcement.getViewCount() : 0) + 1);
        announcementRepository.save(announcement);
    }

    @Override
    public void sendAnnouncementNotifications(Long id, Long ownerId) {
        try {
            notificationService.sendAnnouncementEmail(id);
            log.info("Announcement notifications sent for ID: {}", id);
        } catch (Exception e) {
            log.error("Error sending announcement notifications: {}", e.getMessage());
        }
    }

    @Override
    public void deleteAnnouncement(Long id, Long ownerId) {
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        announcement.setDeleted(true);
        announcementRepository.save(announcement);
    }

    @Override
    public void restoreAnnouncement(Long id, Long ownerId) {
        Announcement announcement = announcementRepository.findById(id)
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("Announcement", id));
        
        announcement.setDeleted(false);
        announcementRepository.save(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementStatistics getAnnouncementStatistics(Long ownerId) {
        List<Announcement> allAnnouncements = announcementRepository.findAll().stream()
                .filter(a -> a.getOwner().getId().equals(ownerId) && !a.isDeleted())
                .collect(Collectors.toList());
        
        long totalAnnouncements = allAnnouncements.size();
        
        Map<String, Long> statusMap = allAnnouncements.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));
        
        Map<String, Long> typeMap = allAnnouncements.stream()
                .collect(Collectors.groupingBy(a -> a.getAnnouncementType().name(), Collectors.counting()));
        
        Map<String, Long> priorityMap = allAnnouncements.stream()
                .collect(Collectors.groupingBy(a -> a.getPriority().name(), Collectors.counting()));
        
        Map<String, Long> audienceMap = allAnnouncements.stream()
                .collect(Collectors.groupingBy(a -> a.getTargetAudience().name(), Collectors.counting()));
        
        long totalViews = allAnnouncements.stream()
                .mapToLong(a -> a.getViewCount() != null ? a.getViewCount() : 0)
                .sum();
        
        Map<String, Integer> topViewed = allAnnouncements.stream()
                .sorted((a1, a2) -> Integer.compare(
                        a2.getViewCount() != null ? a2.getViewCount() : 0,
                        a1.getViewCount() != null ? a1.getViewCount() : 0))
                .limit(5)
                .collect(Collectors.toMap(
                        Announcement::getTitle,
                        a -> a.getViewCount() != null ? a.getViewCount() : 0,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        
        return AnnouncementStatistics.builder()
                .totalAnnouncements(totalAnnouncements)
                .draftAnnouncements(statusMap.getOrDefault("DRAFT", 0L))
                .publishedAnnouncements(statusMap.getOrDefault("PUBLISHED", 0L))
                .archivedAnnouncements(statusMap.getOrDefault("ARCHIVED", 0L))
                .expiredAnnouncements(statusMap.getOrDefault("EXPIRED", 0L))
                .cancelledAnnouncements(statusMap.getOrDefault("CANCELLED", 0L))
                .announcementsByType(typeMap)
                .announcementsByPriority(priorityMap)
                .announcementsByAudience(audienceMap)
                .totalViews(totalViews)
                .averageViewsPerAnnouncement(totalAnnouncements > 0 ? (double) totalViews / totalAnnouncements : 0.0)
                .topViewedAnnouncements(topViewed)
                .build();
    }

    @Override
    public void checkAndExpireAnnouncements() {
        LocalDateTime now = LocalDateTime.now();
        List<Announcement> expiringAnnouncements = announcementRepository.findAll().stream()
                .filter(a -> a.getStatus() == Announcement.AnnouncementStatus.PUBLISHED 
                        && a.getExpiryDate() != null 
                        && a.getExpiryDate().isBefore(now))
                .collect(Collectors.toList());
        
        expiringAnnouncements.forEach(a -> a.setStatus(Announcement.AnnouncementStatus.EXPIRED));
        announcementRepository.saveAll(expiringAnnouncements);
        
        log.info("Expired {} announcements", expiringAnnouncements.size());
    }

    private AnnouncementResponse convertToResponse(Announcement announcement) {
        LocalDateTime now = LocalDateTime.now();
        
        boolean isActive = announcement.getStatus() == Announcement.AnnouncementStatus.PUBLISHED
                && (announcement.getExpiryDate() == null || announcement.getExpiryDate().isAfter(now));
        boolean isExpired = announcement.getExpiryDate() != null && announcement.getExpiryDate().isBefore(now);
        boolean isUpcoming = announcement.getPublishDate().isAfter(now);
        boolean showNewBadge = announcement.getStatus() == Announcement.AnnouncementStatus.PUBLISHED
                && ChronoUnit.HOURS.between(announcement.getPublishDate(), now) < 24;
        boolean showExpiringBadge = announcement.getExpiryDate() != null
                && ChronoUnit.DAYS.between(now, announcement.getExpiryDate()) <= 3
                && ChronoUnit.DAYS.between(now, announcement.getExpiryDate()) >= 0;
        
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .announcementType(announcement.getAnnouncementType())
                .priority(announcement.getPriority())
                .targetAudience(announcement.getTargetAudience())
                .targetClassId(announcement.getTargetClass() != null ? announcement.getTargetClass().getId() : null)
                .targetClassName(announcement.getTargetClass() != null ? announcement.getTargetClass().getClassName() : null)
                .createdById(announcement.getAnnouncementCreatedBy().getId())
                .createdByName(announcement.getAnnouncementCreatedBy().getName())
                .createdByEmail(announcement.getAnnouncementCreatedBy().getEmail())
                .publishDate(announcement.getPublishDate())
                .expiryDate(announcement.getExpiryDate())
                .status(announcement.getStatus())
                .isPinned(announcement.isPinned())
                .sendEmail(announcement.isSendEmail())
                .sendSMS(announcement.isSendSMS())
                .attachmentUrl(announcement.getAttachmentUrl())
                .viewCount(announcement.getViewCount() != null ? announcement.getViewCount() : 0)
                .notes(announcement.getNotes())
                .ownerId(announcement.getOwner().getId())
                .ownerName(announcement.getOwner().getName())
                .isDeleted(announcement.isDeleted())
                .createdOn(announcement.getCreatedOn())
                .updatedOn(announcement.getUpdatedOn())
                .isActive(isActive)
                .isExpired(isExpired)
                .isDraft(announcement.getStatus() == Announcement.AnnouncementStatus.DRAFT)
                .isPublished(announcement.getStatus() == Announcement.AnnouncementStatus.PUBLISHED)
                .isArchived(announcement.getStatus() == Announcement.AnnouncementStatus.ARCHIVED)
                .isUpcoming(isUpcoming)
                .daysUntilPublish(ChronoUnit.DAYS.between(now, announcement.getPublishDate()))
                .daysUntilExpiry(announcement.getExpiryDate() != null ? ChronoUnit.DAYS.between(now, announcement.getExpiryDate()) : 0)
                .daysSincePublished(ChronoUnit.DAYS.between(announcement.getPublishDate(), now))
                .typeDisplay(announcement.getAnnouncementType().name().replace("_", " "))
                .priorityDisplay(announcement.getPriority().name())
                .audienceDisplay(announcement.getTargetAudience().name().replace("_", " "))
                .statusDisplay(announcement.getStatus().name())
                .showUrgentBadge(announcement.getAnnouncementType() == Announcement.AnnouncementType.URGENT)
                .showNewBadge(showNewBadge)
                .showExpiringBadge(showExpiringBadge)
                .build();
    }
}
