package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.EventRequest;
import com.vijay.User_Master.dto.EventResponse;
import com.vijay.User_Master.dto.EventStatistics;
import com.vijay.User_Master.entity.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest extends ServiceTestBase {

    @Mock
    private EventService eventService;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void createEvent_withValidRequest_returnsEvent() {
        EventRequest request = new EventRequest();
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);

        when(eventService.createEvent(request, OWNER_ID)).thenReturn(mockResponse);

        EventResponse response = eventService.createEvent(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(eventService).createEvent(request, OWNER_ID);
    }

    @Test
    void createEvent_withNullRequest_throwsException() {
        when(eventService.createEvent(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.createEvent(null, OWNER_ID));
    }

    @Test
    void updateEvent_withValidData_returnsUpdatedEvent() {
        EventRequest request = new EventRequest();
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);

        when(eventService.updateEvent(1L, request, OWNER_ID)).thenReturn(mockResponse);

        EventResponse response = eventService.updateEvent(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(eventService).updateEvent(1L, request, OWNER_ID);
    }

    @Test
    void getEventById_withValidId_returnsEvent() {
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);

        when(eventService.getEventById(1L, OWNER_ID)).thenReturn(mockResponse);

        EventResponse response = eventService.getEventById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getEventById_withInvalidId_throwsException() {
        when(eventService.getEventById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Event not found"));

        assertThrows(RuntimeException.class, () ->
                eventService.getEventById(999L, OWNER_ID));
    }

    @Test
    void getAllEvents_withValidOwner_returnsPagedEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventResponse> events = new ArrayList<>();
        events.add(new EventResponse());
        Page<EventResponse> page = new PageImpl<>(events, pageable, 1);

        when(eventService.getAllEvents(OWNER_ID, pageable)).thenReturn(page);

        Page<EventResponse> response = eventService.getAllEvents(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(eventService).getAllEvents(OWNER_ID, pageable);
    }

    @Test
    void getEventsByType_withValidType_returnsEvents() {
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventsByType(Event.EventType.ACADEMIC, OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventsByType(Event.EventType.ACADEMIC, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getEventsByStatus_withValidStatus_returnsPagedEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventResponse> events = new ArrayList<>();
        events.add(new EventResponse());
        Page<EventResponse> page = new PageImpl<>(events, pageable, 1);

        when(eventService.getEventsByStatus(Event.EventStatus.SCHEDULED, OWNER_ID, pageable)).thenReturn(page);

        Page<EventResponse> response = eventService.getEventsByStatus(Event.EventStatus.SCHEDULED, OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getEventsByDateRange_withValidDates_returnsEvents() {
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(7);
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventsByDateRange(startDateTime, endDateTime, OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventsByDateRange(startDateTime, endDateTime, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getUpcomingEvents_withValidOwner_returnsEvents() {
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getUpcomingEvents(OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getUpcomingEvents(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getOverdueEvents_withValidOwner_returnsEvents() {
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getOverdueEvents(OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getOverdueEvents(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getEventsByAudience_withValidAudience_returnsEvents() {
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventsByAudience(Event.EventAudience.STUDENTS, OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventsByAudience(Event.EventAudience.STUDENTS, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPublicEvents_withValidOwner_returnsPagedEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventResponse> events = new ArrayList<>();
        events.add(new EventResponse());
        Page<EventResponse> page = new PageImpl<>(events, pageable, 1);

        when(eventService.getPublicEvents(OWNER_ID, pageable)).thenReturn(page);

        Page<EventResponse> response = eventService.getPublicEvents(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getEventsByOrganizer_withValidOrganizerId_returnsEvents() {
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventsByOrganizer(50L, OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventsByOrganizer(50L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getEventsByTargetClass_withValidClassId_returnsEvents() {
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventsByTargetClass(1L, OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventsByTargetClass(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getEventsRequiringRegistration_withValidOwner_returnsEvents() {
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventsRequiringRegistration(OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventsRequiringRegistration(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getEventsByMonth_withValidMonth_returnsEvents() {
        LocalDateTime month = LocalDateTime.now();
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventsByMonth(month, OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventsByMonth(month, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchEvents_withValidKeyword_returnsPagedEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventResponse> events = new ArrayList<>();
        events.add(new EventResponse());
        Page<EventResponse> page = new PageImpl<>(events, pageable, 1);

        when(eventService.searchEvents("exam", OWNER_ID, pageable)).thenReturn(page);

        Page<EventResponse> response = eventService.searchEvents("exam", OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void deleteEvent_withValidId_succeeds() {
        doNothing().when(eventService).deleteEvent(1L, OWNER_ID);

        eventService.deleteEvent(1L, OWNER_ID);

        verify(eventService).deleteEvent(1L, OWNER_ID);
    }

    @Test
    void restoreEvent_withValidId_succeeds() {
        doNothing().when(eventService).restoreEvent(1L, OWNER_ID);

        eventService.restoreEvent(1L, OWNER_ID);

        verify(eventService).restoreEvent(1L, OWNER_ID);
    }

    @Test
    void cancelEvent_withValidData_returnsCancelledEvent() {
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);

        when(eventService.cancelEvent(1L, "Weather issue", OWNER_ID)).thenReturn(mockResponse);

        EventResponse response = eventService.cancelEvent(1L, "Weather issue", OWNER_ID);

        assertNotNull(response);
        verify(eventService).cancelEvent(1L, "Weather issue", OWNER_ID);
    }

    @Test
    void rescheduleEvent_withValidData_returnsRescheduledEvent() {
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);

        LocalDateTime newStart = LocalDateTime.now().plusDays(1);
        LocalDateTime newEnd = LocalDateTime.now().plusDays(1).plusHours(2);

        when(eventService.rescheduleEvent(1L, newStart, newEnd, OWNER_ID)).thenReturn(mockResponse);

        EventResponse response = eventService.rescheduleEvent(1L, newStart, newEnd, OWNER_ID);

        assertNotNull(response);
        verify(eventService).rescheduleEvent(1L, newStart, newEnd, OWNER_ID);
    }

    @Test
    void registerParticipant_withValidData_returnsUpdatedEvent() {
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);

        when(eventService.registerParticipant(1L, OWNER_ID)).thenReturn(mockResponse);

        EventResponse response = eventService.registerParticipant(1L, OWNER_ID);

        assertNotNull(response);
        verify(eventService).registerParticipant(1L, OWNER_ID);
    }

    @Test
    void unregisterParticipant_withValidData_returnsUpdatedEvent() {
        EventResponse mockResponse = new EventResponse();
        mockResponse.setId(1L);

        when(eventService.unregisterParticipant(1L, OWNER_ID)).thenReturn(mockResponse);

        EventResponse response = eventService.unregisterParticipant(1L, OWNER_ID);

        assertNotNull(response);
        verify(eventService).unregisterParticipant(1L, OWNER_ID);
    }

    @Test
    void getEventStatistics_withValidOwner_returnsStatistics() {
        EventStatistics mockStats = new EventStatistics();

        when(eventService.getEventStatistics(OWNER_ID)).thenReturn(mockStats);

        EventStatistics response = eventService.getEventStatistics(OWNER_ID);

        assertNotNull(response);
        verify(eventService).getEventStatistics(OWNER_ID);
    }

    @Test
    void getEventCalendar_withValidMonth_returnsEvents() {
        LocalDateTime month = LocalDateTime.now();
        List<EventResponse> mockEvents = new ArrayList<>();
        mockEvents.add(new EventResponse());

        when(eventService.getEventCalendar(month, OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getEventCalendar(month, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateEvent_withNullRequest_throwsException() {
        when(eventService.updateEvent(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateEvent(1L, null, OWNER_ID));
    }

    @Test
    void getUpcomingEvents_withNoEvents_returnsEmptyList() {
        List<EventResponse> mockEvents = new ArrayList<>();

        when(eventService.getUpcomingEvents(OWNER_ID)).thenReturn(mockEvents);

        List<EventResponse> response = eventService.getUpcomingEvents(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
