package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Assignment;
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
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Find by class
    Page<Assignment> findBySchoolClass_IdAndIsDeletedFalse(Long classId, Pageable pageable);
    
    // Find by class and owner (multi-tenant)
    Page<Assignment> findBySchoolClass_IdAndOwner_IdAndIsDeletedFalse(Long classId, Long ownerId, Pageable pageable);
    
    // Find by subject
    List<Assignment> findBySubject_IdAndIsDeletedFalse(Long subjectId);
    
    // Find by subject with owner filter (multi-tenant) - SECURITY FIX
    List<Assignment> findBySubject_IdAndOwner_IdAndIsDeletedFalse(Long subjectId, Long ownerId);
    
    // Find by subject with owner filter and JOIN FETCH to avoid N+1
    @Query("SELECT DISTINCT a FROM Assignment a " +
           "JOIN FETCH a.subject s " +
           "JOIN FETCH a.schoolClass c " +
           "JOIN FETCH a.assignedBy t " +
           "WHERE s.id = :subjectId AND a.owner.id = :ownerId AND a.isDeleted = false")
    List<Assignment> findBySubject_IdAndOwner_IdWithDetailsAndIsDeletedFalse(
            @Param("subjectId") Long subjectId, 
            @Param("ownerId") Long ownerId);
    
    // Find by teacher
    List<Assignment> findByAssignedBy_IdAndIsDeletedFalse(Long teacherId);
    
    // Find by teacher with owner filter (multi-tenant) - SECURITY FIX
    List<Assignment> findByAssignedBy_IdAndOwner_IdAndIsDeletedFalse(Long teacherId, Long ownerId);
    
    // Find by teacher with owner filter and JOIN FETCH
    @Query("SELECT DISTINCT a FROM Assignment a " +
           "JOIN FETCH a.subject s " +
           "JOIN FETCH a.schoolClass c " +
           "JOIN FETCH a.assignedBy t " +
           "WHERE t.id = :teacherId AND a.owner.id = :ownerId AND a.isDeleted = false")
    List<Assignment> findByAssignedBy_IdAndOwner_IdWithDetailsAndIsDeletedFalse(
            @Param("teacherId") Long teacherId, 
            @Param("ownerId") Long ownerId);
    
    // Find by status
    Page<Assignment> findByStatusAndIsDeletedFalse(Assignment.AssignmentStatus status, Pageable pageable);
    
    // Find by type
    List<Assignment> findByAssignmentTypeAndIsDeletedFalse(Assignment.AssignmentType type);
    
    // Find by type with owner filter (multi-tenant) - SECURITY FIX
    List<Assignment> findByAssignmentTypeAndOwner_IdAndIsDeletedFalse(Assignment.AssignmentType type, Long ownerId);
    
    // Find overdue assignments
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :currentDate AND " +
           "a.status IN ('ASSIGNED', 'IN_PROGRESS') AND a.isDeleted = false")
    List<Assignment> findOverdueAssignments(@Param("currentDate") LocalDateTime currentDate);
    
    // Find overdue assignments with owner filter (multi-tenant) - SECURITY FIX
    @Query("SELECT a FROM Assignment a WHERE a.owner.id = :ownerId AND " +
           "a.dueDate < :currentDate AND " +
           "a.status IN ('ASSIGNED', 'IN_PROGRESS') AND a.isDeleted = false")
    List<Assignment> findOverdueAssignmentsByOwner(
            @Param("ownerId") Long ownerId, 
            @Param("currentDate") LocalDateTime currentDate);
    
    // Find upcoming assignments
    @Query("SELECT a FROM Assignment a WHERE a.dueDate > :currentDate AND " +
           "a.status = 'ASSIGNED' AND a.isDeleted = false ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingAssignments(@Param("currentDate") LocalDateTime currentDate);
    
    // Find upcoming assignments with owner filter (multi-tenant) - SECURITY FIX
    @Query("SELECT a FROM Assignment a WHERE a.owner.id = :ownerId AND " +
           "a.dueDate > :currentDate AND " +
           "a.status = 'ASSIGNED' AND a.isDeleted = false ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingAssignmentsByOwner(
            @Param("ownerId") Long ownerId, 
            @Param("currentDate") LocalDateTime currentDate);
    
    // Find assignments by due date range
    List<Assignment> findByDueDateBetweenAndIsDeletedFalse(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find assignments by due date range with owner filter (multi-tenant) - SECURITY FIX
    List<Assignment> findByDueDateBetweenAndOwner_IdAndIsDeletedFalse(
            LocalDateTime startDate, LocalDateTime endDate, Long ownerId);
    
    // Search assignments
    @Query("SELECT a FROM Assignment a WHERE a.isDeleted = false AND " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Assignment> searchAssignments(@Param("keyword") String keyword, Pageable pageable);

    // Multi-tenant queries
    Page<Assignment> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    @Query("SELECT a FROM Assignment a WHERE a.owner.id = :ownerId AND a.isDeleted = false AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.subject.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Assignment> searchByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    @Query("SELECT a FROM Assignment a WHERE a.owner.id = :ownerId AND a.status = :status AND a.isDeleted = false")
    Page<Assignment> findByOwner_IdAndStatusAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("status") Assignment.AssignmentStatus status, Pageable pageable);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Assignment> findByIdAndOwner_Id(Long id, Long ownerId);
    
    Optional<Assignment> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
    
    // Parent Portal queries
    long countBySchoolClass_IdAndOwner_IdAndStatusAndIsDeletedFalse(Long classId, Long ownerId, Assignment.AssignmentStatus status);
}

