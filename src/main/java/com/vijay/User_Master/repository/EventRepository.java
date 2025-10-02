package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find by event type
    Page<Event> findByEventTypeAndIsDeletedFalse(Event.EventType eventType, Pageable pageable);
    
    // Find by status
    Page<Event> findByStatusAndIsDeletedFalse(Event.EventStatus status, Pageable pageable);
    
    // Find upcoming events
    @Query("SELECT e FROM Event e WHERE e.startDateTime > :currentDateTime AND " +
           "e.status = 'SCHEDULED' AND e.isDeleted = false ORDER BY e.startDateTime ASC")
    List<Event> findUpcomingEvents(@Param("currentDateTime") LocalDateTime currentDateTime);
    
    // Find by date range
    List<Event> findByStartDateTimeBetweenAndIsDeletedFalse(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find by audience
    List<Event> findByAudienceAndIsDeletedFalse(Event.EventAudience audience);
    
    // Find public events
    @Query("SELECT e FROM Event e WHERE e.isPublic = true AND e.isDeleted = false")
    Page<Event> findPublicEvents(Pageable pageable);
    
    // Find by organizer
    List<Event> findByOrganizer_IdAndIsDeletedFalse(Long organizerId);
    
    // Search events
    @Query("SELECT e FROM Event e WHERE e.isDeleted = false AND " +
           "(LOWER(e.eventName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Event> searchEvents(@Param("keyword") String keyword, Pageable pageable);
    
    // Find events for specific class
    List<Event> findByTargetClass_IdAndIsDeletedFalse(Long classId);
}

