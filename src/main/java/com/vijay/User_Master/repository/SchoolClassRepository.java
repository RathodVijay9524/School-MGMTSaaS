package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.SchoolClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    Optional<SchoolClass> findByClassName(String className);
    
    boolean existsByClassName(String className);
    
    // Find by status
    Page<SchoolClass> findByStatusAndIsDeletedFalse(SchoolClass.ClassStatus status, Pageable pageable);
    
    // Find by academic year
    List<SchoolClass> findByAcademicYearAndIsDeletedFalse(String academicYear);
    
    // Find all active classes
    List<SchoolClass> findByIsDeletedFalse();
    
    // Find by class teacher
    List<SchoolClass> findByClassTeacher_IdAndIsDeletedFalse(Long teacherId);
    
    // Find by owner (multi-tenancy)
    Page<SchoolClass> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    List<SchoolClass> findByOwner_IdAndIsDeletedFalse(Long ownerId);
    Optional<SchoolClass> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
    
    // Search classes
    @Query("SELECT c FROM SchoolClass c WHERE c.isDeleted = false AND " +
           "(LOWER(c.className) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.section) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SchoolClass> searchClasses(@Param("keyword") String keyword, Pageable pageable);
    
    // Find by class level
    List<SchoolClass> findByClassLevelAndIsDeletedFalse(Integer classLevel);
    
    // Count active classes
    long countByStatusAndIsDeletedFalse(SchoolClass.ClassStatus status);
    
    // Find all non-deleted classes
    Page<SchoolClass> findByIsDeletedFalse(Pageable pageable);
    
    // Multi-tenancy: Find by business owner
    Page<SchoolClass> findByOwner_IdAndStatusAndIsDeletedFalse(Long ownerId, SchoolClass.ClassStatus status, Pageable pageable);
    
    List<SchoolClass> findByOwner_IdAndAcademicYearAndIsDeletedFalse(Long ownerId, String academicYear);
    
    // Alternative method names for compatibility
    Page<SchoolClass> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    List<SchoolClass> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    Optional<SchoolClass> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<SchoolClass> findByIdAndOwner_Id(Long id, Long ownerId);
}

