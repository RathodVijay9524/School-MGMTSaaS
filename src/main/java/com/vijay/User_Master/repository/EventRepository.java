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
import java.util.Optional;

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

    // Multi-tenant queries
    Page<Event> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.isDeleted = false AND " +
           "(LOWER(e.eventName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.venue) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Event> searchByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.status = :status AND e.isDeleted = false")
    Page<Event> findByOwner_IdAndStatusAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("status") Event.EventStatus status, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.eventType = :eventType AND e.isDeleted = false")
    List<Event> findByOwner_IdAndEventTypeAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("eventType") Event.EventType eventType);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.startDateTime BETWEEN :startDateTime AND :endDateTime AND e.isDeleted = false")
    List<Event> findByOwner_IdAndStartDateTimeBetweenAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.audience = :audience AND e.isDeleted = false")
    List<Event> findByOwner_IdAndAudienceAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("audience") Event.EventAudience audience);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.isPublic = true AND e.isDeleted = false")
    Page<Event> findPublicEventsByOwner(@Param("ownerId") Long ownerId, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.organizer.id = :organizerId AND e.isDeleted = false")
    List<Event> findByOwner_IdAndOrganizer_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("organizerId") Long organizerId);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.startDateTime > :currentDateTime AND e.status = 'SCHEDULED' AND e.isDeleted = false ORDER BY e.startDateTime ASC")
    List<Event> findUpcomingEventsByOwner(@Param("ownerId") Long ownerId, @Param("currentDateTime") LocalDateTime currentDateTime);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.targetClass.id = :classId AND e.isDeleted = false")
    List<Event> findByOwner_IdAndTargetClass_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("classId") Long classId);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.startDateTime <= :currentDateTime AND e.status = 'SCHEDULED' AND e.isDeleted = false")
    List<Event> findOverdueEventsByOwner(@Param("ownerId") Long ownerId, @Param("currentDateTime") LocalDateTime currentDateTime);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.requiresRegistration = true AND e.isDeleted = false")
    List<Event> findEventsRequiringRegistrationByOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.startDateTime >= :startDate AND e.startDateTime < :endDate AND e.isDeleted = false")
    List<Event> findEventsByOwnerAndMonth(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Event> findByIdAndOwner_Id(Long id, Long ownerId);
    
    Optional<Event> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
    
    // Parent Portal queries
    @Query("SELECT e FROM Event e WHERE e.owner.id = :ownerId AND e.startDateTime >= :startDateTime AND e.isDeleted = false")
    Page<Event> findUpcomingEventsByOwner(@Param("ownerId") Long ownerId, @Param("startDateTime") LocalDateTime startDateTime, Pageable pageable);
}

