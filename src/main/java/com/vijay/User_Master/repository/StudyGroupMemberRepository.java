package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.StudyGroupMember;
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
 * Repository for StudyGroupMember entity
 */
@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    // Find study group members by owner
    List<StudyGroupMember> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find study group members by student
    List<StudyGroupMember> findByStudentIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long studentId, Long ownerId);
    
    // Find study group members by study group
    List<StudyGroupMember> findByStudyGroupIdAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long studyGroupId, Long ownerId);
    
    // Find active study group members by student
    List<StudyGroupMember> findByStudentIdAndOwnerIdAndStatusAndIsDeletedFalseOrderByCreatedOnDesc(Long studentId, Long ownerId, StudyGroupMember.MembershipStatus status);
    
    // Find study group members by role
    List<StudyGroupMember> findByOwnerIdAndRoleAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, StudyGroupMember.MemberRole role);
    
    // Find study group members by status
    List<StudyGroupMember> findByOwnerIdAndStatusAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, StudyGroupMember.MembershipStatus status);
    
    // Find study group members by contribution score range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.contributionScore BETWEEN :minScore AND :maxScore AND sgm.isDeleted = false ORDER BY sgm.contributionScore DESC")
    List<StudyGroupMember> findByOwnerIdAndContributionScoreRange(@Param("ownerId") Long ownerId, @Param("minScore") Double minScore, @Param("maxScore") Double maxScore);
    
    // Find study group members by participation count range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.participationCount BETWEEN :minCount AND :maxCount AND sgm.isDeleted = false ORDER BY sgm.participationCount DESC")
    List<StudyGroupMember> findByOwnerIdAndParticipationCountRange(@Param("ownerId") Long ownerId, @Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount);
    
    // Find study group members by help provided count range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.helpProvidedCount BETWEEN :minCount AND :maxCount AND sgm.isDeleted = false ORDER BY sgm.helpProvidedCount DESC")
    List<StudyGroupMember> findByOwnerIdAndHelpProvidedCountRange(@Param("ownerId") Long ownerId, @Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount);
    
    // Find study group members by help received count range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.helpReceivedCount BETWEEN :minCount AND :maxCount AND sgm.isDeleted = false ORDER BY sgm.helpReceivedCount DESC")
    List<StudyGroupMember> findByOwnerIdAndHelpReceivedCountRange(@Param("ownerId") Long ownerId, @Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount);
    
    // Find study group members by join date range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.joinDate BETWEEN :startDate AND :endDate AND sgm.isDeleted = false ORDER BY sgm.joinDate DESC")
    List<StudyGroupMember> findByOwnerIdAndJoinDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find study group members by last active date range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.lastActiveDate BETWEEN :startDate AND :endDate AND sgm.isDeleted = false ORDER BY sgm.lastActiveDate DESC")
    List<StudyGroupMember> findByOwnerIdAndLastActiveDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find recent study group members
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.createdOn >= :since AND sgm.isDeleted = false ORDER BY sgm.createdOn DESC")
    List<StudyGroupMember> findRecentStudyGroupMembers(@Param("ownerId") Long ownerId, @Param("since") LocalDateTime since);
    
    // Find inactive study group members
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND (sgm.lastActiveDate IS NULL OR sgm.lastActiveDate < :inactiveSince) AND sgm.isDeleted = false ORDER BY sgm.lastActiveDate ASC")
    List<StudyGroupMember> findInactiveStudyGroupMembers(@Param("ownerId") Long ownerId, @Param("inactiveSince") LocalDateTime inactiveSince);
    
    // Find study group members with high contribution scores
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.contributionScore >= :minScore AND sgm.isDeleted = false ORDER BY sgm.contributionScore DESC")
    List<StudyGroupMember> findHighContributors(@Param("ownerId") Long ownerId, @Param("minScore") Double minScore);
    
    // Find study group members with low contribution scores
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.contributionScore < :maxScore AND sgm.isDeleted = false ORDER BY sgm.contributionScore ASC")
    List<StudyGroupMember> findLowContributors(@Param("ownerId") Long ownerId, @Param("maxScore") Double maxScore);
    
    // Search study group members by keyword
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND " +
           "(LOWER(sgm.student.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sgm.studyGroup.groupName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sgm.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND sgm.isDeleted = false")
    Page<StudyGroupMember> searchStudyGroupMembers(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    // Count study group members by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    // Count study group members by student
    long countByStudentIdAndOwnerIdAndIsDeletedFalse(Long studentId, Long ownerId);
    
    // Count study group members by study group
    long countByStudyGroupIdAndOwnerIdAndIsDeletedFalse(Long studyGroupId, Long ownerId);
    
    // Count study group members by role
    long countByOwnerIdAndRoleAndIsDeletedFalse(Long ownerId, StudyGroupMember.MemberRole role);
    
    // Count study group members by status
    long countByOwnerIdAndStatusAndIsDeletedFalse(Long ownerId, StudyGroupMember.MembershipStatus status);
    
    // Get study group member statistics
    @Query("SELECT COUNT(sgm) as totalMembers, " +
           "COUNT(CASE WHEN sgm.status = 'ACTIVE' THEN 1 END) as activeMembers, " +
           "COUNT(CASE WHEN sgm.role = 'CREATOR' THEN 1 END) as creators, " +
           "COUNT(CASE WHEN sgm.role = 'MODERATOR' THEN 1 END) as moderators, " +
           "AVG(sgm.contributionScore) as avgContribution, " +
           "AVG(sgm.participationCount) as avgParticipation " +
           "FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.isDeleted = false")
    Object[] getStudyGroupMemberStatistics(@Param("ownerId") Long ownerId);
    
    // Get role-wise statistics
    @Query("SELECT sgm.role, COUNT(sgm) as memberCount, AVG(sgm.contributionScore) as avgContribution " +
           "FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.isDeleted = false " +
           "GROUP BY sgm.role ORDER BY memberCount DESC")
    List<Object[]> getRoleWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get status-wise statistics
    @Query("SELECT sgm.status, COUNT(sgm) as memberCount, AVG(sgm.contributionScore) as avgContribution " +
           "FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.isDeleted = false " +
           "GROUP BY sgm.status ORDER BY memberCount DESC")
    List<Object[]> getStatusWiseStatistics(@Param("ownerId") Long ownerId);
    
    // Get student leaderboard by contribution score
    @Query("SELECT sgm.student.id as studentId, sgm.student.name as studentName, " +
           "COUNT(sgm) as groupCount, " +
           "AVG(sgm.contributionScore) as avgContribution, " +
           "SUM(sgm.helpProvidedCount) as totalHelpProvided " +
           "FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.isDeleted = false " +
           "GROUP BY sgm.student.id, sgm.student.name " +
           "ORDER BY avgContribution DESC")
    List<Object[]> getStudentLeaderboardByContribution(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Find study group members by study group and role
    List<StudyGroupMember> findByStudyGroupIdAndOwnerIdAndRoleAndIsDeletedFalseOrderByCreatedOnDesc(Long studyGroupId, Long ownerId, StudyGroupMember.MemberRole role);
    
    // Find study group members by study group and status
    List<StudyGroupMember> findByStudyGroupIdAndOwnerIdAndStatusAndIsDeletedFalseOrderByCreatedOnDesc(Long studyGroupId, Long ownerId, StudyGroupMember.MembershipStatus status);
    
    // Find study group members by student and role
    List<StudyGroupMember> findByStudentIdAndOwnerIdAndRoleAndIsDeletedFalseOrderByCreatedOnDesc(Long studentId, Long ownerId, StudyGroupMember.MemberRole role);
    
    // Find study group members by notes
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.notes LIKE %:notes% AND sgm.isDeleted = false ORDER BY sgm.createdOn DESC")
    List<StudyGroupMember> findByOwnerIdAndNotesContaining(@Param("ownerId") Long ownerId, @Param("notes") String notes);
    
    // Find study group members with notification enabled
    List<StudyGroupMember> findByOwnerIdAndIsNotificationEnabledTrueAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Find study group members with notification disabled
    List<StudyGroupMember> findByOwnerIdAndIsNotificationEnabledFalseAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId);
    
    // Check if student is member of study group
    boolean existsByStudentIdAndStudyGroupIdAndOwnerIdAndIsDeletedFalse(Long studentId, Long studyGroupId, Long ownerId);
    
    // Find study group member by student and study group
    Optional<StudyGroupMember> findByStudentIdAndStudyGroupIdAndOwnerIdAndIsDeletedFalse(Long studentId, Long studyGroupId, Long ownerId);
    
    // Find study group members by study group subject
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.studyGroup.subject = :subject AND sgm.isDeleted = false ORDER BY sgm.createdOn DESC")
    List<StudyGroupMember> findByOwnerIdAndStudyGroupSubjectAndIsDeletedFalseOrderByCreatedOnDesc(@Param("ownerId") Long ownerId, @Param("subject") String subject);
    
    // Soft delete by owner
    @Query("UPDATE StudyGroupMember sgm SET sgm.isDeleted = true WHERE sgm.id = :id AND sgm.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Soft delete by student
    @Query("UPDATE StudyGroupMember sgm SET sgm.isDeleted = true WHERE sgm.student.id = :studentId AND sgm.owner.id = :ownerId")
    void softDeleteByStudentIdAndOwnerId(@Param("studentId") Long studentId, @Param("ownerId") Long ownerId);
    
    // Find study group members created in date range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.createdOn BETWEEN :startDate AND :endDate AND sgm.isDeleted = false ORDER BY sgm.createdOn DESC")
    List<StudyGroupMember> findByOwnerIdAndCreatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find study group members updated in date range
    @Query("SELECT sgm FROM StudyGroupMember sgm WHERE sgm.owner.id = :ownerId AND sgm.updatedOn BETWEEN :startDate AND :endDate AND sgm.isDeleted = false ORDER BY sgm.updatedOn DESC")
    List<StudyGroupMember> findByOwnerIdAndUpdatedDateRange(@Param("ownerId") Long ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
