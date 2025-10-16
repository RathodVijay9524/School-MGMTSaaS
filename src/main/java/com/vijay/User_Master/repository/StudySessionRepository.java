package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.StudySession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for StudySession entity
 */
@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // Find study sessions by owner
    List<StudySession> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find study sessions by study group
    List<StudySession> findByStudyGroupIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long studyGroupId, Long ownerId);
    
    // Find study sessions by facilitator
    List<StudySession> findByFacilitatorIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long facilitatorId, Long ownerId);
    
    // Find study sessions by type
    List<StudySession> findByOwnerIdAndSessionTypeAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, StudySession.SessionType sessionType);
    
    // Find study sessions by status
    List<StudySession> findByOwnerIdAndSessionStatusAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, StudySession.SessionStatus sessionStatus);
    
    // Find upcoming study sessions
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.scheduledDate > :now AND ss.sessionStatus = 'SCHEDULED' AND ss.isDeleted = false ORDER BY ss.scheduledDate ASC")
    List<StudySession> findUpcomingStudySessions(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Find ongoing study sessions
    List<StudySession> findByOwnerIdAndSessionStatusAndIsDeletedFalseOrderByActualStartDateDesc(Long ownerId, StudySession.SessionStatus sessionStatus);
    
    // Find completed study sessions
    List<StudySession> findByOwnerIdAndSessionStatusAndIsDeletedFalseOrderByActualEndDateDesc(Long ownerId, StudySession.SessionStatus sessionStatus);
    
    // Find study sessions by topic
    List<StudySession> findByOwnerIdAndTopicContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String topic);
    
    // Find study sessions by duration range
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.durationMinutes BETWEEN :minMinutes AND :maxMinutes AND ss.isDeleted = false ORDER BY ss.durationMinutes DESC")
    List<StudySession> findByOwnerIdAndDurationRange(@Param("ownerId") Long ownerId, @Param("minMinutes") Integer minMinutes, @Param("maxMinutes") Integer maxMinutes);
    
    // Find study sessions by participants count range
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.participantsCount BETWEEN :minParticipants AND :maxParticipants AND ss.isDeleted = false ORDER BY ss.participantsCount DESC")
    List<StudySession> findByOwnerIdAndParticipantsCountRange(@Param("ownerId") Long ownerId, @Param("minParticipants") Integer minParticipants, @Param("maxParticipants") Integer maxParticipants);
    
    // Find recorded study sessions
    List<StudySession> findByOwnerIdAndIsRecordedTrueAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find study sessions by scheduled date range
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.scheduledDate BETWEEN :startDate AND :endDate AND ss.isDeleted = false ORDER BY ss.scheduledDate ASC")
    List<StudySession> findByOwnerIdAndScheduledDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find study sessions by actual start date range
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.actualStartDate BETWEEN :startDate AND :endDate AND ss.isDeleted = false ORDER BY ss.actualStartDate DESC")
    List<StudySession> findByOwnerIdAndActualStartDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find study sessions by actual end date range
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.actualEndDate BETWEEN :startDate AND :endDate AND ss.isDeleted = false ORDER BY ss.actualEndDate DESC")
    List<StudySession> findByOwnerIdAndActualEndDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find recent study sessions
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.createdOn >= :since AND ss.isDeleted = false ORDER BY ss.createdOn DESC")
    List<StudySession> findRecentStudySessions(@Param("ownerId") Long ownerId, @Param("since") LocalDateTime since);
    
    // Search study sessions by keyword
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND " +
           "(LOWER(ss.sessionTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ss.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ss.topic) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ss.agenda) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND ss.isDeleted = false")
    Page<StudySession> searchStudySessions(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Count study sessions by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count study sessions by study group
    long countByStudyGroupIdAndOwnerIdAndIsDeletedFalse(Long studyGroupId, Long ownerId);
    
    // Count study sessions by study group and status
    long countByStudyGroupIdAndOwnerIdAndSessionStatusAndIsDeletedFalse(Long studyGroupId, Long ownerId, StudySession.SessionStatus sessionStatus);
    
    // Count study sessions by facilitator
    long countByFacilitatorIdAndOwnerIdAndIsDeletedFalse(Long facilitatorId, Long ownerId);
    
    // Count study sessions by type
    long countByOwnerIdAndSessionTypeAndIsDeletedFalse(Long ownerId, StudySession.SessionType sessionType);
    
    // Count study sessions by status
    long countByOwnerIdAndSessionStatusAndIsDeletedFalse(Long ownerId, StudySession.SessionStatus sessionStatus);
    
    // Count upcoming study sessions
    @Query("SELECT COUNT(ss) FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.scheduledDate > :now AND ss.sessionStatus = 'SCHEDULED' AND ss.isDeleted = false")
    long countUpcomingStudySessions(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Count ongoing study sessions
    @Query("SELECT COUNT(ss) FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.sessionStatus = 'ONGOING' AND ss.isDeleted = false")
    long countOngoingStudySessions(@Param("ownerId") Long ownerId);
    
    // Count completed study sessions
    @Query("SELECT COUNT(ss) FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.sessionStatus = 'COMPLETED' AND ss.isDeleted = false")
    long countCompletedStudySessions(@Param("ownerId") Long ownerId);
    
    // Get study session statistics
    @Query("SELECT COUNT(ss) as totalSessions, " +
           "COUNT(CASE WHEN ss.sessionStatus = 'COMPLETED' THEN 1 END) as completedSessions, " +
           "COUNT(CASE WHEN ss.sessionStatus = 'ONGOING' THEN 1 END) as ongoingSessions, " +
           "COUNT(CASE WHEN ss.sessionStatus = 'SCHEDULED' THEN 1 END) as scheduledSessions, " +
           "COUNT(CASE WHEN ss.sessionStatus = 'CANCELLED' THEN 1 END) as cancelledSessions, " +
           "AVG(ss.durationMinutes) as avgDuration, " +
           "AVG(ss.participantsCount) as avgParticipants " +
           "FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.isDeleted = false")
    Object[] getStudySessionStatistics(@Param("ownerId") Long ownerId);
    
    // Get type-wise statistics
    @Query("SELECT ss.sessionType, COUNT(ss) as sessionCount, AVG(ss.durationMinutes) as avgDuration " +
           "FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.isDeleted = false " +
           "GROUP BY ss.sessionType ORDER BY sessionCount DESC")
    List<Object[]> getTypeWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get facilitator-wise statistics
    @Query("SELECT ss.facilitator.name, COUNT(ss) as sessionCount, AVG(ss.durationMinutes) as avgDuration " +
           "FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.isDeleted = false " +
           "GROUP BY ss.facilitator.name ORDER BY sessionCount DESC")
    List<Object[]> getFacilitatorWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Find study sessions by agenda
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.agenda LIKE %:agenda% AND ss.isDeleted = false ORDER BY ss.createdOn DESC")
    List<StudySession> findByOwnerIdAndAgendaContaining(@Param("ownerId") Long ownerId, @Param("agenda") String agenda);
    
    // Find study sessions by materials
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.materials LIKE %:materials% AND ss.isDeleted = false ORDER BY ss.createdOn DESC")
    List<StudySession> findByOwnerIdAndMaterialsContaining(@Param("ownerId") Long ownerId, @Param("materials") String materials);
    
    // Find study sessions by location
    List<StudySession> findByOwnerIdAndLocationContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String location);
    
    // Find study sessions by meeting link
    List<StudySession> findByOwnerIdAndMeetingLinkContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String meetingLink);
    
    // Find study sessions by recording link
    List<StudySession> findByOwnerIdAndRecordingLinkContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, String recordingLink);
    
    // Find study sessions by feedback
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.feedback LIKE %:feedback% AND ss.isDeleted = false ORDER BY ss.createdOn DESC")
    List<StudySession> findByOwnerIdAndFeedbackContaining(@Param("ownerId") Long ownerId, @Param("feedback") String feedback);
    
    // Find study sessions by session notes
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.sessionNotes LIKE %:notes% AND ss.isDeleted = false ORDER BY ss.createdOn DESC")
    List<StudySession> findByOwnerIdAndSessionNotesContaining(@Param("ownerId") Long ownerId, @Param("notes") String notes);
    
    // Find study sessions by facilitator and status
    List<StudySession> findByFacilitatorIdAndOwnerIdAndSessionStatusAndIsDeletedFalseOrderByCreatedOnDesc(Long facilitatorId, Long ownerId, StudySession.SessionStatus sessionStatus);
    
    // Find study sessions by study group and status
    List<StudySession> findByStudyGroupIdAndOwnerIdAndSessionStatusAndIsDeletedFalseOrderByCreatedOnDesc(Long studyGroupId, Long ownerId, StudySession.SessionStatus sessionStatus);
    
    // Find study sessions by facilitator and type
    List<StudySession> findByFacilitatorIdAndOwnerIdAndSessionTypeAndIsDeletedFalseOrderByCreatedOnDesc(Long facilitatorId, Long ownerId, StudySession.SessionType sessionType);
    
    // Find study sessions by study group and type
    List<StudySession> findByStudyGroupIdAndOwnerIdAndSessionTypeAndIsDeletedFalseOrderByCreatedOnDesc(Long studyGroupId, Long ownerId, StudySession.SessionType sessionType);
    
    // Check if study session title exists for study group
    boolean existsBySessionTitleAndStudyGroupIdAndIsDeletedFalse(String sessionTitle, Long studyGroupId);
    
    // Find study session by title and study group
    Optional<StudySession> findBySessionTitleAndStudyGroupIdAndIsDeletedFalse(String sessionTitle, Long studyGroupId);
    
    // Soft delete by owner
    @Query("UPDATE StudySession ss SET ss.isDeleted = true WHERE ss.id = :id AND ss.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Find study sessions created in date range
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.createdOn BETWEEN :startDate AND :endDate AND ss.isDeleted = false ORDER BY ss.createdOn DESC")
    List<StudySession> findByOwnerIdAndCreatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find study sessions updated in date range
    @Query("SELECT ss FROM StudySession ss WHERE ss.owner.id = :ownerId AND ss.updatedOn BETWEEN :startDate AND :endDate AND ss.isDeleted = false ORDER BY ss.updatedOn DESC")
    List<StudySession> findByOwnerIdAndUpdatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
