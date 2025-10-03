package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.EventRequest;
import com.vijay.User_Master.dto.EventResponse;
import com.vijay.User_Master.dto.EventStatistics;
import com.vijay.User_Master.entity.Event;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import org.springframework.ai.tool.annotation.Tool;
import com.vijay.User_Master.repository.EventRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Event management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final WorkerRepository workerRepository;

    @Override
    @Tool(name = "createEvent", description = "Create a new event with name, description, date, time, venue and event details")
    public EventResponse createEvent(EventRequest request, Long ownerId) {
        log.info("Creating event: {} for owner: {}", request.getEventName(), ownerId);
        
        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Validate organizer exists and belongs to owner (if provided)
        Worker organizer = null;
        if (request.getOrganizerId() != null) {
            organizer = workerRepository.findById(request.getOrganizerId())
                    .filter(w -> w.getOwner() != null && w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                    .orElseThrow(() -> new RuntimeException("Organizer not found or does not belong to owner"));
        }
        
        // Validate target class exists and belongs to owner (if provided)
        SchoolClass targetClass = null;
        if (request.getTargetClassId() != null) {
            targetClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getTargetClassId(), ownerId)
                    .orElseThrow(() -> new RuntimeException("Target class not found or does not belong to owner"));
        }
        
        Event event = Event.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .eventType(request.getEventType())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .venue(request.getVenue())
                .organizer(organizer)
                .status(request.getStatus())
                .audience(request.getAudience())
                .targetClass(targetClass)
                .expectedParticipants(request.getExpectedParticipants())
                .registeredParticipants(request.getRegisteredParticipants() != null ? request.getRegisteredParticipants() : 0)
                .isPublic(request.isPublic())
                .requiresRegistration(request.isRequiresRegistration())
                .bannerImageUrl(request.getBannerImageUrl())
                .notes(request.getNotes())
                .contactPerson(request.getContactPerson())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with ID: {}", savedEvent.getId());
        
        return convertToResponse(savedEvent);
    }

    @Override
    @Tool(name = "updateEvent", description = "Update event details")
    public EventResponse updateEvent(Long id, EventRequest request, Long ownerId) {
        log.info("Updating event: {} for owner: {}", id, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Update fields
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setEventType(request.getEventType());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setVenue(request.getVenue());
        event.setStatus(request.getStatus());
        event.setAudience(request.getAudience());
        event.setExpectedParticipants(request.getExpectedParticipants());
        event.setRegisteredParticipants(request.getRegisteredParticipants());
        event.setPublic(request.isPublic());
        event.setRequiresRegistration(request.isRequiresRegistration());
        event.setBannerImageUrl(request.getBannerImageUrl());
        event.setNotes(request.getNotes());
        event.setContactPerson(request.getContactPerson());
        event.setContactPhone(request.getContactPhone());
        event.setContactEmail(request.getContactEmail());
        
        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully");
        
        return convertToResponse(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id, Long ownerId) {
        log.info("Getting event: {} for owner: {}", id, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        return convertToResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Long ownerId, Pageable pageable) {
        log.info("Getting all events for owner: {}", ownerId);
        
        Page<Event> events = eventRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        return events.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByType(Event.EventType eventType, Long ownerId) {
        log.info("Getting events for type: {} and owner: {}", eventType, ownerId);
        
        List<Event> events = eventRepository.findByOwner_IdAndEventTypeAndIsDeletedFalse(ownerId, eventType);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByStatus(Event.EventStatus status, Long ownerId, Pageable pageable) {
        log.info("Getting events for status: {} and owner: {}", status, ownerId);
        
        Page<Event> events = eventRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, status, pageable);
        return events.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, Long ownerId) {
        log.info("Getting events for date range: {} to {} for owner: {}", startDateTime, endDateTime, ownerId);
        
        List<Event> events = eventRepository.findByOwner_IdAndStartDateTimeBetweenAndIsDeletedFalse(ownerId, startDateTime, endDateTime);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents(Long ownerId) {
        log.info("Getting upcoming events for owner: {}", ownerId);
        
        List<Event> events = eventRepository.findUpcomingEventsByOwner(ownerId, LocalDateTime.now());
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getOverdueEvents(Long ownerId) {
        log.info("Getting overdue events for owner: {}", ownerId);
        
        List<Event> events = eventRepository.findOverdueEventsByOwner(ownerId, LocalDateTime.now());
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByAudience(Event.EventAudience audience, Long ownerId) {
        log.info("Getting events for audience: {} and owner: {}", audience, ownerId);
        
        List<Event> events = eventRepository.findByOwner_IdAndAudienceAndIsDeletedFalse(ownerId, audience);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getPublicEvents(Long ownerId, Pageable pageable) {
        log.info("Getting public events for owner: {}", ownerId);
        
        Page<Event> events = eventRepository.findPublicEventsByOwner(ownerId, pageable);
        return events.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByOrganizer(Long organizerId, Long ownerId) {
        log.info("Getting events for organizer: {} and owner: {}", organizerId, ownerId);
        
        List<Event> events = eventRepository.findByOwner_IdAndOrganizer_IdAndIsDeletedFalse(ownerId, organizerId);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByTargetClass(Long classId, Long ownerId) {
        log.info("Getting events for target class: {} and owner: {}", classId, ownerId);
        
        List<Event> events = eventRepository.findByOwner_IdAndTargetClass_IdAndIsDeletedFalse(ownerId, classId);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsRequiringRegistration(Long ownerId) {
        log.info("Getting events requiring registration for owner: {}", ownerId);
        
        List<Event> events = eventRepository.findEventsRequiringRegistrationByOwner(ownerId);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByMonth(LocalDateTime month, Long ownerId) {
        log.info("Getting events for month: {} and owner: {}", month, ownerId);
        
        LocalDateTime startOfMonth = month.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = month.withDayOfMonth(month.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        
        List<Event> events = eventRepository.findEventsByOwnerAndMonth(ownerId, startOfMonth, endOfMonth);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> searchEvents(String keyword, Long ownerId, Pageable pageable) {
        log.info("Searching events with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Event> events = eventRepository.searchByOwner(ownerId, keyword, pageable);
        return events.map(this::convertToResponse);
    }

    @Override
    public void deleteEvent(Long id, Long ownerId) {
        log.info("Deleting event: {} for owner: {}", id, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.setDeleted(true);
        eventRepository.save(event);
        
        log.info("Event deleted successfully");
    }

    @Override
    public void restoreEvent(Long id, Long ownerId) {
        log.info("Restoring event: {} for owner: {}", id, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.setDeleted(false);
        eventRepository.save(event);
        
        log.info("Event restored successfully");
    }

    @Override
    public EventResponse cancelEvent(Long id, String reason, Long ownerId) {
        log.info("Cancelling event: {} for owner: {}", id, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.setStatus(Event.EventStatus.CANCELLED);
        event.setNotes(event.getNotes() + "\nCancelled: " + reason);
        
        Event updatedEvent = eventRepository.save(event);
        log.info("Event cancelled successfully");
        
        return convertToResponse(updatedEvent);
    }

    @Override
    public EventResponse rescheduleEvent(Long id, LocalDateTime newStartDateTime, LocalDateTime newEndDateTime, Long ownerId) {
        log.info("Rescheduling event: {} to {} for owner: {}", id, newStartDateTime, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        LocalDateTime oldStartDateTime = event.getStartDateTime();
        
        event.setStartDateTime(newStartDateTime);
        event.setEndDateTime(newEndDateTime);
        event.setStatus(Event.EventStatus.RESCHEDULED);
        event.setNotes(event.getNotes() + "\nRescheduled from " + oldStartDateTime + " to " + newStartDateTime);
        
        Event updatedEvent = eventRepository.save(event);
        log.info("Event rescheduled successfully");
        
        return convertToResponse(updatedEvent);
    }

    @Override
    public EventResponse registerParticipant(Long id, Long ownerId) {
        log.info("Registering participant for event: {} for owner: {}", id, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (!event.isRequiresRegistration()) {
            throw new RuntimeException("This event does not require registration");
        }
        
        if (event.getExpectedParticipants() != null && 
            event.getRegisteredParticipants() >= event.getExpectedParticipants()) {
            throw new RuntimeException("Event is full, no more participants can be registered");
        }
        
        event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);
        
        Event updatedEvent = eventRepository.save(event);
        log.info("Participant registered successfully");
        
        return convertToResponse(updatedEvent);
    }

    @Override
    public EventResponse unregisterParticipant(Long id, Long ownerId) {
        log.info("Unregistering participant for event: {} for owner: {}", id, ownerId);
        
        Event event = eventRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (event.getRegisteredParticipants() > 0) {
            event.setRegisteredParticipants(event.getRegisteredParticipants() - 1);
        }
        
        Event updatedEvent = eventRepository.save(event);
        log.info("Participant unregistered successfully");
        
        return convertToResponse(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventStatistics getEventStatistics(Long ownerId) {
        log.info("Getting event statistics for owner: {}", ownerId);
        
        long totalEvents = eventRepository.countByOwner_IdAndIsDeletedFalse(ownerId);
        
        // Count by status
        long scheduledEvents = eventRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Event.EventStatus.SCHEDULED, Pageable.unpaged()).getTotalElements();
        long ongoingEvents = eventRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Event.EventStatus.ONGOING, Pageable.unpaged()).getTotalElements();
        long completedEvents = eventRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Event.EventStatus.COMPLETED, Pageable.unpaged()).getTotalElements();
        long cancelledEvents = eventRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Event.EventStatus.CANCELLED, Pageable.unpaged()).getTotalElements();
        long postponedEvents = eventRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Event.EventStatus.POSTPONED, Pageable.unpaged()).getTotalElements();
        long rescheduledEvents = eventRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Event.EventStatus.RESCHEDULED, Pageable.unpaged()).getTotalElements();
        
        // Count by event type
        List<Event> allEvents = eventRepository.findByOwner_IdAndIsDeletedFalse(ownerId, Pageable.unpaged()).getContent();
        long academicEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.ACADEMIC).count();
        long sportsEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.SPORTS).count();
        long culturalEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.CULTURAL).count();
        long holidayEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.HOLIDAY).count();
        long meetingEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.MEETING).count();
        long examEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.EXAM).count();
        long workshopEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.WORKSHOP).count();
        long seminarEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.SEMINAR).count();
        long competitionEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.COMPETITION).count();
        long celebrationEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.CELEBRATION).count();
        long parentTeacherMeetingEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.PARENT_TEACHER_MEETING).count();
        long fieldTripEvents = allEvents.stream().filter(e -> e.getEventType() == Event.EventType.FIELD_TRIP).count();
        
        // Count public vs private
        long publicEvents = allEvents.stream().filter(Event::isPublic).count();
        long privateEvents = allEvents.stream().filter(e -> !e.isPublic()).count();
        
        // Count registration requirements
        long eventsRequiringRegistration = allEvents.stream().filter(Event::isRequiresRegistration).count();
        long eventsNotRequiringRegistration = allEvents.stream().filter(e -> !e.isRequiresRegistration()).count();
        
        // Count by audience
        long allAudienceEvents = allEvents.stream().filter(e -> e.getAudience() == Event.EventAudience.ALL).count();
        long studentsOnlyEvents = allEvents.stream().filter(e -> e.getAudience() == Event.EventAudience.STUDENTS).count();
        long teachersOnlyEvents = allEvents.stream().filter(e -> e.getAudience() == Event.EventAudience.TEACHERS).count();
        long parentsOnlyEvents = allEvents.stream().filter(e -> e.getAudience() == Event.EventAudience.PARENTS).count();
        long staffOnlyEvents = allEvents.stream().filter(e -> e.getAudience() == Event.EventAudience.STAFF).count();
        long specificClassEvents = allEvents.stream().filter(e -> e.getAudience() == Event.EventAudience.SPECIFIC_CLASS).count();
        long specificGradeEvents = allEvents.stream().filter(e -> e.getAudience() == Event.EventAudience.SPECIFIC_GRADE).count();
        
        // Count upcoming and overdue
        long upcomingEventsCount = eventRepository.findUpcomingEventsByOwner(ownerId, LocalDateTime.now()).size();
        long overdueEventsCount = eventRepository.findOverdueEventsByOwner(ownerId, LocalDateTime.now()).size();
        
        // Calculate totals
        long totalExpectedParticipants = allEvents.stream()
                .filter(e -> e.getExpectedParticipants() != null)
                .mapToInt(Event::getExpectedParticipants)
                .sum();
        
        long totalRegisteredParticipants = allEvents.stream()
                .mapToInt(Event::getRegisteredParticipants)
                .sum();
        
        // Calculate average duration
        double averageEventDuration = allEvents.stream()
                .filter(e -> e.getStartDateTime() != null && e.getEndDateTime() != null)
                .mapToLong(e -> Duration.between(e.getStartDateTime(), e.getEndDateTime()).toHours())
                .average()
                .orElse(0.0);
        
        return EventStatistics.builder()
                .totalEvents(totalEvents)
                .scheduledEvents(scheduledEvents)
                .ongoingEvents(ongoingEvents)
                .completedEvents(completedEvents)
                .cancelledEvents(cancelledEvents)
                .postponedEvents(postponedEvents)
                .rescheduledEvents(rescheduledEvents)
                .academicEvents(academicEvents)
                .sportsEvents(sportsEvents)
                .culturalEvents(culturalEvents)
                .holidayEvents(holidayEvents)
                .meetingEvents(meetingEvents)
                .examEvents(examEvents)
                .workshopEvents(workshopEvents)
                .seminarEvents(seminarEvents)
                .competitionEvents(competitionEvents)
                .celebrationEvents(celebrationEvents)
                .parentTeacherMeetingEvents(parentTeacherMeetingEvents)
                .fieldTripEvents(fieldTripEvents)
                .publicEvents(publicEvents)
                .privateEvents(privateEvents)
                .eventsRequiringRegistration(eventsRequiringRegistration)
                .eventsNotRequiringRegistration(eventsNotRequiringRegistration)
                .allAudienceEvents(allAudienceEvents)
                .studentsOnlyEvents(studentsOnlyEvents)
                .teachersOnlyEvents(teachersOnlyEvents)
                .parentsOnlyEvents(parentsOnlyEvents)
                .staffOnlyEvents(staffOnlyEvents)
                .specificClassEvents(specificClassEvents)
                .specificGradeEvents(specificGradeEvents)
                .upcomingEventsCount(upcomingEventsCount)
                .overdueEventsCount(overdueEventsCount)
                .totalExpectedParticipants(totalExpectedParticipants)
                .totalRegisteredParticipants(totalRegisteredParticipants)
                .averageEventDuration(averageEventDuration)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventCalendar(LocalDateTime month, Long ownerId) {
        log.info("Getting event calendar for month: {} and owner: {}", month, ownerId);
        
        LocalDateTime startOfMonth = month.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = month.withDayOfMonth(month.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        
        List<Event> events = eventRepository.findEventsByOwnerAndMonth(ownerId, startOfMonth, endOfMonth);
        return events.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private EventResponse convertToResponse(Event event) {
        LocalDateTime now = LocalDateTime.now();
        boolean isUpcoming = event.getStartDateTime().isAfter(now) && event.getStatus() == Event.EventStatus.SCHEDULED;
        boolean isOngoing = event.getStartDateTime().isBefore(now) && event.getEndDateTime().isAfter(now) && event.getStatus() == Event.EventStatus.ONGOING;
        boolean isCompleted = event.getStatus() == Event.EventStatus.COMPLETED;
        boolean isOverdue = event.getStartDateTime().isBefore(now) && event.getStatus() == Event.EventStatus.SCHEDULED;
        
        long daysUntilEvent = event.getStartDateTime().isAfter(now) ? 
                ChronoUnit.DAYS.between(now, event.getStartDateTime()) : 0;
        long daysOverdue = isOverdue ? ChronoUnit.DAYS.between(event.getStartDateTime(), now) : 0;
        
        long durationHours = Duration.between(event.getStartDateTime(), event.getEndDateTime()).toHours();
        
        String organizerDisplay = event.getOrganizer() != null ? event.getOrganizer().getName() : "Not assigned";
        String targetClassName = event.getTargetClass() != null ? event.getTargetClass().getClassName() : null;
        
        String registrationStatus = event.isRequiresRegistration() ? "Required" : "Not Required";
        String participationStatus = event.getExpectedParticipants() != null && event.getRegisteredParticipants() >= event.getExpectedParticipants() ? 
                "Full" : "Available";
        
        return EventResponse.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .eventType(event.getEventType())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .venue(event.getVenue())
                .organizerId(event.getOrganizer() != null ? event.getOrganizer().getId() : null)
                .organizerName(organizerDisplay)
                .status(event.getStatus())
                .audience(event.getAudience())
                .targetClassId(event.getTargetClass() != null ? event.getTargetClass().getId() : null)
                .targetClassName(targetClassName)
                .expectedParticipants(event.getExpectedParticipants())
                .registeredParticipants(event.getRegisteredParticipants())
                .isPublic(event.isPublic())
                .requiresRegistration(event.isRequiresRegistration())
                .bannerImageUrl(event.getBannerImageUrl())
                .notes(event.getNotes())
                .contactPerson(event.getContactPerson())
                .contactPhone(event.getContactPhone())
                .contactEmail(event.getContactEmail())
                .isDeleted(event.isDeleted())
                .createdOn(event.getCreatedOn())
                .updatedOn(event.getUpdatedOn())
                .isUpcoming(isUpcoming)
                .isOngoing(isOngoing)
                .isCompleted(isCompleted)
                .isOverdue(isOverdue)
                .daysUntilEvent(daysUntilEvent)
                .daysOverdue(daysOverdue)
                .durationHours(durationHours)
                .statusDisplay(event.getStatus().name().replace("_", " "))
                .typeDisplay(event.getEventType().name().replace("_", " "))
                .audienceDisplay(event.getAudience().name().replace("_", " "))
                .organizerDisplay(organizerDisplay)
                .timeDisplay(event.getStartDateTime().toString() + " - " + event.getEndDateTime().toString())
                .registrationStatus(registrationStatus)
                .participationStatus(participationStatus)
                .build();
    }
}
