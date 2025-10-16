package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.StudyGroup;
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
 * Repository for StudyGroup entity
 */
@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    // Find study groups by owner
    Page<StudyGroup> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Pageable pageable);
    
    // Find study groups by subject
    List<StudyGroup> findByOwnerIdAndSubjectAndIsDeletedFalseOrderByGroupNameAsc(Long ownerId, String subject);
    
    // Find study groups by grade level
    List<StudyGroup> findByOwnerIdAndGradeLevelAndIsDeletedFalseOrderByGroupNameAsc(Long ownerId, String gradeLevel);
    
    // Find study groups by type
    List<StudyGroup> findByOwnerIdAndGroupTypeAndIsDeletedFalseOrderByGroupNameAsc(Long ownerId, StudyGroup.GroupType groupType);
    
    // Find active study groups
    List<StudyGroup> findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByGroupNameAsc(Long ownerId);
    
    // Find available study groups (not full, active, not expired)
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isActive = true AND sg.isDeleted = false AND sg.groupStatus = 'ACTIVE' AND (sg.maxMembers IS NULL OR sg.currentMembers < sg.maxMembers) AND (sg.endDate IS NULL OR sg.endDate > :now) ORDER BY sg.groupName ASC")
    List<StudyGroup> findAvailableStudyGroups(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Find public study groups
    List<StudyGroup> findByOwnerIdAndIsPublicTrueAndIsActiveTrueAndIsDeletedFalseOrderByGroupNameAsc(Long ownerId);
    
    // Find study groups by creator
    List<StudyGroup> findByCreatorIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long creatorId, Long ownerId);
    
    // Find study groups by topic
    List<StudyGroup> findByOwnerIdAndTopicContainingIgnoreCaseAndIsDeletedFalseOrderByGroupNameAsc(Long ownerId, String topic);
    
    // Find study groups by learning objectives
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.learningObjectives LIKE %:objective% AND sg.isDeleted = false ORDER BY sg.groupName ASC")
    List<StudyGroup> findByOwnerIdAndLearningObjectivesContaining(@Param("ownerId") Long ownerId, @Param("objective") String objective);
    
    // Find study groups by status
    List<StudyGroup> findByOwnerIdAndGroupStatusAndIsDeletedFalseOrderByGroupNameAsc(Long ownerId, StudyGroup.GroupStatus groupStatus);
    
    // Find study groups with available spots
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isActive = true AND sg.isDeleted = false AND (sg.maxMembers IS NULL OR sg.currentMembers < sg.maxMembers) ORDER BY sg.currentMembers ASC")
    List<StudyGroup> findStudyGroupsWithAvailableSpots(@Param("ownerId") Long ownerId);
    
    // Find full study groups
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.maxMembers IS NOT NULL AND sg.currentMembers >= sg.maxMembers AND sg.isDeleted = false ORDER BY sg.groupName ASC")
    List<StudyGroup> findFullStudyGroups(@Param("ownerId") Long ownerId);
    
    // Find expired study groups
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.endDate IS NOT NULL AND sg.endDate <= :now AND sg.isDeleted = false ORDER BY sg.endDate DESC")
    List<StudyGroup> findExpiredStudyGroups(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Find study groups expiring soon
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.endDate IS NOT NULL AND sg.endDate BETWEEN :now AND :futureDate AND sg.isDeleted = false ORDER BY sg.endDate ASC")
    List<StudyGroup> findStudyGroupsExpiringSoon(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);
    
    // Find study groups by date range
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.createdOn BETWEEN :startDate AND :endDate AND sg.isDeleted = false ORDER BY sg.createdOn DESC")
    List<StudyGroup> findByOwnerIdAndCreatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find study groups by activity date range
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.lastActivityDate BETWEEN :startDate AND :endDate AND sg.isDeleted = false ORDER BY sg.lastActivityDate DESC")
    List<StudyGroup> findByOwnerIdAndActivityDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Search study groups by keyword
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND " +
           "(LOWER(sg.groupName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sg.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sg.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sg.topic) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND sg.isDeleted = false")
    Page<StudyGroup> searchStudyGroups(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Find popular study groups (by member count)
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isActive = true AND sg.isDeleted = false ORDER BY sg.currentMembers DESC")
    List<StudyGroup> findPopularStudyGroups(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Find trending study groups (by recent activity)
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isActive = true AND sg.isDeleted = false AND sg.lastActivityDate >= :since ORDER BY sg.lastActivityDate DESC")
    List<StudyGroup> findTrendingStudyGroups(@Param("ownerId") Long ownerId, @Param("since") LocalDateTime since, Pageable pageable);
    
    // Count study groups by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count active study groups by owner
    long countByOwnerIdAndIsActiveTrueAndIsDeletedFalse(Long ownerId);
    
    // Count study groups by subject
    long countByOwnerIdAndSubjectAndIsDeletedFalse(Long ownerId, String subject);
    
    // Count study groups by type
    long countByOwnerIdAndGroupTypeAndIsDeletedFalse(Long ownerId, StudyGroup.GroupType groupType);
    
    // Count study groups by status
    long countByOwnerIdAndGroupStatusAndIsDeletedFalse(Long ownerId, StudyGroup.GroupStatus groupStatus);
    
    // Count study groups with available spots
    @Query("SELECT COUNT(sg) FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isActive = true AND sg.isDeleted = false AND (sg.maxMembers IS NULL OR sg.currentMembers < sg.maxMembers)")
    long countStudyGroupsWithAvailableSpots(@Param("ownerId") Long ownerId);
    
    // Get study group statistics
    @Query("SELECT COUNT(sg) as totalGroups, " +
           "COUNT(CASE WHEN sg.isActive = true THEN 1 END) as activeGroups, " +
           "COUNT(CASE WHEN sg.groupStatus = 'ACTIVE' THEN 1 END) as activeStatusGroups, " +
           "COUNT(CASE WHEN sg.isPublic = true THEN 1 END) as publicGroups, " +
           "AVG(sg.currentMembers) as avgMembers, " +
           "SUM(sg.currentMembers) as totalMembers " +
           "FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isDeleted = false")
    Object[] getStudyGroupStatistics(@Param("ownerId") Long ownerId);
    
    // Get subject-wise statistics
    @Query("SELECT sg.subject, COUNT(sg) as groupCount, AVG(sg.currentMembers) as avgMembers " +
           "FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isDeleted = false " +
           "GROUP BY sg.subject ORDER BY groupCount DESC")
    List<Object[]> getSubjectWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get type-wise statistics
    @Query("SELECT sg.groupType, COUNT(sg) as groupCount, AVG(sg.currentMembers) as avgMembers " +
           "FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.isDeleted = false " +
           "GROUP BY sg.groupType ORDER BY groupCount DESC")
    List<Object[]> getTypeWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Find study groups by member count range
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.currentMembers BETWEEN :minMembers AND :maxMembers AND sg.isDeleted = false ORDER BY sg.currentMembers ASC")
    List<StudyGroup> findByOwnerIdAndMemberCountRange(@Param("ownerId") Long ownerId, @Param("minMembers") Integer minMembers, @Param("maxMembers") Integer maxMembers);
    
    // Find study groups by max members range
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.owner.id = :ownerId AND sg.maxMembers BETWEEN :minMaxMembers AND :maxMaxMembers AND sg.isDeleted = false ORDER BY sg.maxMembers ASC")
    List<StudyGroup> findByOwnerIdAndMaxMembersRange(@Param("ownerId") Long ownerId, @Param("minMaxMembers") Integer minMaxMembers, @Param("maxMaxMembers") Integer maxMaxMembers);
    
    // Check if study group name exists for owner
    boolean existsByGroupNameAndOwnerIdAndIsDeletedFalse(String groupName, Long ownerId);
    
    // Find study group by name and owner
    Optional<StudyGroup> findByGroupNameAndOwnerIdAndIsDeletedFalse(String groupName, Long ownerId);
    
    // Soft delete by owner
    @Query("UPDATE StudyGroup sg SET sg.isDeleted = true WHERE sg.id = :id AND sg.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Archive study group
    @Query("UPDATE StudyGroup sg SET sg.groupStatus = 'COMPLETED' WHERE sg.id = :id AND sg.owner.id = :ownerId")
    void archiveStudyGroup(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Restore study group
    @Query("UPDATE StudyGroup sg SET sg.groupStatus = 'ACTIVE' WHERE sg.id = :id AND sg.owner.id = :ownerId")
    void restoreStudyGroup(@Param("id") Long id, @Param("ownerId") Long ownerId);
}
