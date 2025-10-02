package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.EventRequest;
import com.vijay.User_Master.dto.EventResponse;
import com.vijay.User_Master.dto.EventStatistics;
import com.vijay.User_Master.entity.Event;
import com.vijay.User_Master.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Event management
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Event Management", description = "APIs for managing school events and activities")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Create a new event", description = "Create a new school event with scheduling and details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("Creating event: {} of type: {}", request.getEventName(), request.getEventType());
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventResponse response = eventService.createEvent(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event", description = "Update an existing school event")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<EventResponse> updateEvent(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {
        log.info("Updating event: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventResponse response = eventService.updateEvent(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its ID")
    public ResponseEntity<EventResponse> getEventById(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        log.info("Getting event: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventResponse response = eventService.getEventById(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieve all events with pagination and sorting")
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDateTime") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all events - page: {}, size: {}", page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EventResponse> response = eventService.getAllEvents(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{eventType}")
    @Operation(summary = "Get events by type", description = "Retrieve all events of a specific type")
    public ResponseEntity<List<EventResponse>> getEventsByType(
            @Parameter(description = "Event type") @PathVariable Event.EventType eventType) {
        log.info("Getting events for type: {}", eventType);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventsByType(eventType, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get events by status", description = "Retrieve all events with a specific status")
    public ResponseEntity<Page<EventResponse>> getEventsByStatus(
            @Parameter(description = "Event status") @PathVariable Event.EventStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events for status: {}", status);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        Page<EventResponse> response = eventService.getEventsByStatus(status, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get events by date range", description = "Retrieve events within a specific date and time range")
    public ResponseEntity<List<EventResponse>> getEventsByDateRange(
            @Parameter(description = "Start date and time (yyyy-MM-ddTHH:mm:ss)") @RequestParam LocalDateTime startDateTime,
            @Parameter(description = "End date and time (yyyy-MM-ddTHH:mm:ss)") @RequestParam LocalDateTime endDateTime) {
        log.info("Getting events for date range: {} to {}", startDateTime, endDateTime);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventsByDateRange(startDateTime, endDateTime, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events", description = "Retrieve all upcoming scheduled events")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        log.info("Getting upcoming events");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getUpcomingEvents(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue events", description = "Retrieve all overdue scheduled events")
    public ResponseEntity<List<EventResponse>> getOverdueEvents() {
        log.info("Getting overdue events");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getOverdueEvents(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audience/{audience}")
    @Operation(summary = "Get events by audience", description = "Retrieve all events for a specific audience")
    public ResponseEntity<List<EventResponse>> getEventsByAudience(
            @Parameter(description = "Event audience") @PathVariable Event.EventAudience audience) {
        log.info("Getting events for audience: {}", audience);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventsByAudience(audience, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    @Operation(summary = "Get public events", description = "Retrieve all public events visible to parents")
    public ResponseEntity<Page<EventResponse>> getPublicEvents(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting public events");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        Page<EventResponse> response = eventService.getPublicEvents(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/organizer/{organizerId}")
    @Operation(summary = "Get events by organizer", description = "Retrieve all events organized by a specific teacher")
    public ResponseEntity<List<EventResponse>> getEventsByOrganizer(
            @Parameter(description = "Organizer ID") @PathVariable Long organizerId) {
        log.info("Getting events for organizer: {}", organizerId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventsByOrganizer(organizerId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get events by target class", description = "Retrieve all events targeting a specific class")
    public ResponseEntity<List<EventResponse>> getEventsByTargetClass(
            @Parameter(description = "Class ID") @PathVariable Long classId) {
        log.info("Getting events for target class: {}", classId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventsByTargetClass(classId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/registration-required")
    @Operation(summary = "Get events requiring registration", description = "Retrieve all events that require participant registration")
    public ResponseEntity<List<EventResponse>> getEventsRequiringRegistration() {
        log.info("Getting events requiring registration");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventsRequiringRegistration(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/month/{month}")
    @Operation(summary = "Get events by month", description = "Retrieve all events for a specific month")
    public ResponseEntity<List<EventResponse>> getEventsByMonth(
            @Parameter(description = "Month (yyyy-MM-dd)") @PathVariable LocalDateTime month) {
        log.info("Getting events for month: {}", month);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventsByMonth(month, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search events", description = "Search events by keyword in name, description, venue, or contact person")
    public ResponseEntity<Page<EventResponse>> searchEvents(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching events with keyword: {}", keyword);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        Page<EventResponse> response = eventService.searchEvents(keyword, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calendar/{month}")
    @Operation(summary = "Get event calendar", description = "Retrieve event calendar for a specific month")
    public ResponseEntity<List<EventResponse>> getEventCalendar(
            @Parameter(description = "Month (yyyy-MM-dd)") @PathVariable LocalDateTime month) {
        log.info("Getting event calendar for month: {}", month);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<EventResponse> response = eventService.getEventCalendar(month, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get event statistics", description = "Get comprehensive statistics about events")
    public ResponseEntity<EventStatistics> getEventStatistics() {
        log.info("Getting event statistics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventStatistics response = eventService.getEventStatistics(ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel event", description = "Cancel a scheduled event")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<EventResponse> cancelEvent(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        log.info("Cancelling event: {} with reason: {}", id, reason);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventResponse response = eventService.cancelEvent(id, reason, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule event", description = "Reschedule an event to new date and time")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<EventResponse> rescheduleEvent(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Parameter(description = "New start date and time (yyyy-MM-ddTHH:mm:ss)") @RequestParam LocalDateTime newStartDateTime,
            @Parameter(description = "New end date and time (yyyy-MM-ddTHH:mm:ss)") @RequestParam LocalDateTime newEndDateTime) {
        log.info("Rescheduling event: {} to {}", id, newStartDateTime);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventResponse response = eventService.rescheduleEvent(id, newStartDateTime, newEndDateTime, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/register")
    @Operation(summary = "Register for event", description = "Register a participant for an event")
    public ResponseEntity<EventResponse> registerParticipant(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        log.info("Registering participant for event: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventResponse response = eventService.registerParticipant(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/unregister")
    @Operation(summary = "Unregister from event", description = "Unregister a participant from an event")
    public ResponseEntity<EventResponse> unregisterParticipant(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        log.info("Unregistering participant from event: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        EventResponse response = eventService.unregisterParticipant(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event", description = "Soft delete an event")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        log.info("Deleting event: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        eventService.deleteEvent(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore event", description = "Restore a soft-deleted event")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> restoreEvent(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        log.info("Restoring event: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        eventService.restoreEvent(id, ownerId);
        return ResponseEntity.ok().build();
    }
}
