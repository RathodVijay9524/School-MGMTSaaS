package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.AnnouncementRequest;
import com.vijay.User_Master.dto.AnnouncementResponse;
import com.vijay.User_Master.dto.AnnouncementStatistics;
import com.vijay.User_Master.entity.Announcement;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Announcement management
 */
@RestController
@RequestMapping("/api/v1/announcements")
@AllArgsConstructor
@Slf4j
@Tag(name = "Announcement Management", description = "APIs for managing school announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final WorkerRepository workerRepository;

    @PostMapping
    @Operation(summary = "Create a new announcement", description = "Create a new announcement for the school")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        log.info("Creating announcement: {}", request.getTitle());
        Long ownerId = getCorrectOwnerId();
        AnnouncementResponse response = announcementService.createAnnouncement(request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an announcement", description = "Update an existing announcement")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @Parameter(description = "Announcement ID") @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request) {
        log.info("Updating announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        AnnouncementResponse response = announcementService.updateAnnouncement(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get announcement by ID", description = "Retrieve a specific announcement by its ID")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Getting announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        AnnouncementResponse response = announcementService.getAnnouncementById(id, ownerId);
        
        // Increment view count
        announcementService.incrementViewCount(id, ownerId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all announcements", description = "Retrieve all announcements with pagination and sorting")
    public ResponseEntity<Page<AnnouncementResponse>> getAllAnnouncements(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "publishDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Getting all announcements - page: {}, size: {}", page, size);
        Long ownerId = getCorrectOwnerId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AnnouncementResponse> response = announcementService.getAllAnnouncements(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active announcements", description = "Retrieve all active (published and not expired) announcements")
    public ResponseEntity<Page<AnnouncementResponse>> getActiveAnnouncements(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting active announcements");
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishDate").descending());
        Page<AnnouncementResponse> response = announcementService.getActiveAnnouncements(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pinned")
    @Operation(summary = "Get pinned announcements", description = "Retrieve all pinned announcements")
    public ResponseEntity<List<AnnouncementResponse>> getPinnedAnnouncements() {
        log.info("Getting pinned announcements");
        Long ownerId = getCorrectOwnerId();
        List<AnnouncementResponse> response = announcementService.getPinnedAnnouncements(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get announcements by type", description = "Retrieve all announcements of a specific type")
    public ResponseEntity<Page<AnnouncementResponse>> getAnnouncementsByType(
            @Parameter(description = "Announcement type") @PathVariable Announcement.AnnouncementType type,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting announcements with type: {}", type);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishDate").descending());
        Page<AnnouncementResponse> response = announcementService.getAnnouncementsByType(type, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get announcements by priority", description = "Retrieve all announcements with a specific priority")
    public ResponseEntity<Page<AnnouncementResponse>> getAnnouncementsByPriority(
            @Parameter(description = "Priority level") @PathVariable Announcement.Priority priority,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting announcements with priority: {}", priority);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishDate").descending());
        Page<AnnouncementResponse> response = announcementService.getAnnouncementsByPriority(priority, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get announcements by status", description = "Retrieve all announcements with a specific status")
    public ResponseEntity<Page<AnnouncementResponse>> getAnnouncementsByStatus(
            @Parameter(description = "Announcement status") @PathVariable Announcement.AnnouncementStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting announcements with status: {}", status);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishDate").descending());
        Page<AnnouncementResponse> response = announcementService.getAnnouncementsByStatus(status, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audience/{audience}")
    @Operation(summary = "Get announcements by audience", description = "Retrieve all announcements for a specific audience")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByAudience(
            @Parameter(description = "Target audience") @PathVariable Announcement.AnnouncementAudience audience) {
        log.info("Getting announcements for audience: {}", audience);
        Long ownerId = getCorrectOwnerId();
        List<AnnouncementResponse> response = announcementService.getAnnouncementsByAudience(audience, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get announcements for a class", description = "Retrieve all announcements targeted at a specific class")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsForClass(
            @Parameter(description = "Class ID") @PathVariable Long classId) {
        log.info("Getting announcements for class: {}", classId);
        Long ownerId = getCorrectOwnerId();
        List<AnnouncementResponse> response = announcementService.getAnnouncementsForClass(classId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/creator/{creatorId}")
    @Operation(summary = "Get announcements by creator", description = "Retrieve all announcements created by a specific user")
    public ResponseEntity<Page<AnnouncementResponse>> getAnnouncementsByCreator(
            @Parameter(description = "Creator user ID") @PathVariable Long creatorId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting announcements for creator: {}", creatorId);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishDate").descending());
        Page<AnnouncementResponse> response = announcementService.getAnnouncementsByCreator(creatorId, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get announcements by date range", description = "Retrieve announcements within a specific date range")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByDateRange(
            @Parameter(description = "Start date (ISO format)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Getting announcements between {} and {}", startDate, endDate);
        Long ownerId = getCorrectOwnerId();
        List<AnnouncementResponse> response = announcementService.getAnnouncementsByDateRange(startDate, endDate, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search announcements", description = "Search announcements by keyword in title or content")
    public ResponseEntity<Page<AnnouncementResponse>> searchAnnouncements(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching announcements with keyword: {}", keyword);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishDate").descending());
        Page<AnnouncementResponse> response = announcementService.searchAnnouncements(keyword, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish announcement", description = "Change announcement status from DRAFT to PUBLISHED and send notifications")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<AnnouncementResponse> publishAnnouncement(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Publishing announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        AnnouncementResponse response = announcementService.publishAnnouncement(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive announcement", description = "Move announcement to archived status")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<AnnouncementResponse> archiveAnnouncement(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Archiving announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        AnnouncementResponse response = announcementService.archiveAnnouncement(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pin")
    @Operation(summary = "Pin announcement", description = "Pin announcement to top of the list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<AnnouncementResponse> pinAnnouncement(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Pinning announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        AnnouncementResponse response = announcementService.pinAnnouncement(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unpin")
    @Operation(summary = "Unpin announcement", description = "Remove pin from announcement")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<AnnouncementResponse> unpinAnnouncement(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Unpinning announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        AnnouncementResponse response = announcementService.unpinAnnouncement(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/notify")
    @Operation(summary = "Send notifications", description = "Manually trigger email/SMS notifications for an announcement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> sendAnnouncementNotifications(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Sending notifications for announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        announcementService.sendAnnouncementNotifications(id, ownerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get announcement statistics", description = "Get comprehensive statistics about announcements")
    public ResponseEntity<AnnouncementStatistics> getAnnouncementStatistics() {
        log.info("Getting announcement statistics");
        Long ownerId = getCorrectOwnerId();
        AnnouncementStatistics response = announcementService.getAnnouncementStatistics(ownerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete announcement", description = "Soft delete an announcement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAnnouncement(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Deleting announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        announcementService.deleteAnnouncement(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore announcement", description = "Restore a soft-deleted announcement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> restoreAnnouncement(
            @Parameter(description = "Announcement ID") @PathVariable Long id) {
        log.info("Restoring announcement: {}", id);
        Long ownerId = getCorrectOwnerId();
        announcementService.restoreAnnouncement(id, ownerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get the correct owner ID for the logged-in user.
     * If the logged-in user is a worker (like a teacher), return their owner's ID.
     * If the logged-in user is a direct owner, return their own ID.
     */
    private Long getCorrectOwnerId() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long userId = loggedInUser.getId();
        
        // Check if the logged-in user is a worker
        Worker worker = workerRepository.findById(userId).orElse(null);
        if (worker != null && worker.getOwner() != null) {
            // User is a worker, return their owner's ID
            Long ownerId = worker.getOwner().getId();
            log.info("Logged-in user is worker ID: {}, returning owner ID: {}", userId, ownerId);
            return ownerId;
        }
        
        // User is a direct owner, return their own ID
        log.info("Logged-in user is direct owner ID: {}", userId);
        return userId;
    }
}
