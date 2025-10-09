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
    
    // Find by subject
    List<Assignment> findBySubject_IdAndIsDeletedFalse(Long subjectId);
    
    // Find by teacher
    List<Assignment> findByAssignedBy_IdAndIsDeletedFalse(Long teacherId);
    
    // Find by status
    Page<Assignment> findByStatusAndIsDeletedFalse(Assignment.AssignmentStatus status, Pageable pageable);
    
    // Find by type
    List<Assignment> findByAssignmentTypeAndIsDeletedFalse(Assignment.AssignmentType type);
    
    // Find overdue assignments
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :currentDate AND " +
           "a.status IN ('ASSIGNED', 'IN_PROGRESS') AND a.isDeleted = false")
    List<Assignment> findOverdueAssignments(@Param("currentDate") LocalDateTime currentDate);
    
    // Find upcoming assignments
    @Query("SELECT a FROM Assignment a WHERE a.dueDate > :currentDate AND " +
           "a.status = 'ASSIGNED' AND a.isDeleted = false ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingAssignments(@Param("currentDate") LocalDateTime currentDate);
    
    // Find assignments by due date range
    List<Assignment> findByDueDateBetweenAndIsDeletedFalse(LocalDateTime startDate, LocalDateTime endDate);
    
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

