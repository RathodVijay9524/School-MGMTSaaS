package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectCode(String subjectCode);
    
    Optional<Subject> findBySubjectName(String subjectName);
    
    boolean existsBySubjectCode(String subjectCode);
    
    // Find by department
    List<Subject> findByDepartmentAndIsDeletedFalse(String department);
    
    // Find by type
    List<Subject> findByTypeAndIsDeletedFalse(Subject.SubjectType type);
    
    // Find by status
    Page<Subject> findByStatusAndIsDeletedFalse(Subject.SubjectStatus status, Pageable pageable);
    
    // Find all active subjects
    List<Subject> findByIsDeletedFalse();
    
    // Search subjects
    @Query("SELECT s FROM Subject s WHERE s.isDeleted = false AND " +
           "(LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Subject> searchSubjects(@Param("keyword") String keyword, Pageable pageable);
    
    // Count active subjects
    long countByStatusAndIsDeletedFalse(Subject.SubjectStatus status);
    
    // Multi-tenancy: Find by business owner
    Page<Subject> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    List<Subject> findByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    List<Subject> findByOwner_IdAndDepartmentAndIsDeletedFalse(Long ownerId, String department);
    
    List<Subject> findByOwner_IdAndTypeAndIsDeletedFalse(Long ownerId, Subject.SubjectType type);
    
    @Query("SELECT s FROM Subject s WHERE s.owner.id = :ownerId AND s.isDeleted = false AND " +
           "(LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Subject> searchSubjectsByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Subject> findByIdAndOwner_Id(Long id, Long ownerId);
    
    Optional<Subject> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
    
    Optional<Subject> findByIdAndOwner_IdAndIsDeletedTrue(Long id, Long ownerId);
    
    Page<Subject> findByOwner_IdAndIsDeletedTrue(Long ownerId, Pageable pageable);
}

