package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.EventRequest;
import com.vijay.User_Master.dto.EventResponse;
import com.vijay.User_Master.dto.EventStatistics;
import com.vijay.User_Master.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Event management
 */
public interface EventService {

    /**
     * Create a new event
     */
    EventResponse createEvent(EventRequest request, Long ownerId);

    /**
     * Update an existing event
     */
    EventResponse updateEvent(Long id, EventRequest request, Long ownerId);

    /**
     * Get event by ID
     */
    EventResponse getEventById(Long id, Long ownerId);

    /**
     * Get all events for owner with pagination
     */
    Page<EventResponse> getAllEvents(Long ownerId, Pageable pageable);

    /**
     * Get events by event type
     */
    List<EventResponse> getEventsByType(Event.EventType eventType, Long ownerId);

    /**
     * Get events by status
     */
    Page<EventResponse> getEventsByStatus(Event.EventStatus status, Long ownerId, Pageable pageable);

    /**
     * Get events by date range
     */
    List<EventResponse> getEventsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, Long ownerId);

    /**
     * Get upcoming events
     */
    List<EventResponse> getUpcomingEvents(Long ownerId);

    /**
     * Get overdue events
     */
    List<EventResponse> getOverdueEvents(Long ownerId);

    /**
     * Get events by audience
     */
    List<EventResponse> getEventsByAudience(Event.EventAudience audience, Long ownerId);

    /**
     * Get public events
     */
    Page<EventResponse> getPublicEvents(Long ownerId, Pageable pageable);

    /**
     * Get events by organizer
     */
    List<EventResponse> getEventsByOrganizer(Long organizerId, Long ownerId);

    /**
     * Get events by target class
     */
    List<EventResponse> getEventsByTargetClass(Long classId, Long ownerId);

    /**
     * Get events requiring registration
     */
    List<EventResponse> getEventsRequiringRegistration(Long ownerId);

    /**
     * Get events by month
     */
    List<EventResponse> getEventsByMonth(LocalDateTime month, Long ownerId);

    /**
     * Search events by keyword
     */
    Page<EventResponse> searchEvents(String keyword, Long ownerId, Pageable pageable);

    /**
     * Delete event (soft delete)
     */
    void deleteEvent(Long id, Long ownerId);

    /**
     * Restore deleted event
     */
    void restoreEvent(Long id, Long ownerId);

    /**
     * Cancel event
     */
    EventResponse cancelEvent(Long id, String reason, Long ownerId);

    /**
     * Reschedule event
     */
    EventResponse rescheduleEvent(Long id, LocalDateTime newStartDateTime, LocalDateTime newEndDateTime, Long ownerId);

    /**
     * Register participant for event
     */
    EventResponse registerParticipant(Long id, Long ownerId);

    /**
     * Unregister participant from event
     */
    EventResponse unregisterParticipant(Long id, Long ownerId);

    /**
     * Get event statistics
     */
    EventStatistics getEventStatistics(Long ownerId);

    /**
     * Get event calendar for a specific month
     */
    List<EventResponse> getEventCalendar(LocalDateTime month, Long ownerId);
}
