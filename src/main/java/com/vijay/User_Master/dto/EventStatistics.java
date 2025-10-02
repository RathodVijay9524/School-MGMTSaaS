package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Event statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatistics {

    private long totalEvents;
    private long scheduledEvents;
    private long ongoingEvents;
    private long completedEvents;
    private long cancelledEvents;
    private long postponedEvents;
    private long rescheduledEvents;
    private long academicEvents;
    private long sportsEvents;
    private long culturalEvents;
    private long holidayEvents;
    private long meetingEvents;
    private long examEvents;
    private long workshopEvents;
    private long seminarEvents;
    private long competitionEvents;
    private long celebrationEvents;
    private long parentTeacherMeetingEvents;
    private long fieldTripEvents;
    private long publicEvents;
    private long privateEvents;
    private long eventsRequiringRegistration;
    private long eventsNotRequiringRegistration;
    private long allAudienceEvents;
    private long studentsOnlyEvents;
    private long teachersOnlyEvents;
    private long parentsOnlyEvents;
    private long staffOnlyEvents;
    private long specificClassEvents;
    private long specificGradeEvents;
    private long upcomingEventsCount;
    private long overdueEventsCount;
    private long totalExpectedParticipants;
    private long totalRegisteredParticipants;
    private double averageEventDuration;
    private long eventsByOrganizer; // Example: count of events by a specific organizer
    private long eventsByClass;     // Example: count of events for a specific class
}
