package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    Optional<Exam> findByExamCode(String examCode);
    
    boolean existsByExamCode(String examCode);
    
    // Find by class
    Page<Exam> findBySchoolClass_IdAndIsDeletedFalse(Long classId, Pageable pageable);
    
    // Find by subject
    List<Exam> findBySubject_IdAndIsDeletedFalse(Long subjectId);
    
    // Find by exam type
    List<Exam> findByExamTypeAndIsDeletedFalse(Exam.ExamType examType);
    
    // Find by status
    Page<Exam> findByStatusAndIsDeletedFalse(Exam.ExamStatus status, Pageable pageable);
    
    // Find by date range
    List<Exam> findByExamDateBetweenAndIsDeletedFalse(LocalDate startDate, LocalDate endDate);
    
    // Find upcoming exams
    @Query("SELECT e FROM Exam e WHERE e.examDate > :currentDate AND e.status = 'SCHEDULED' AND e.isDeleted = false")
    List<Exam> findUpcomingExams(@Param("currentDate") LocalDate currentDate);
    
    // Find exams by semester
    List<Exam> findBySemesterAndIsDeletedFalse(String semester);
    
    // Find exams by academic year
    List<Exam> findByAcademicYearAndIsDeletedFalse(String academicYear);
    
    // Search exams
    @Query("SELECT e FROM Exam e WHERE e.isDeleted = false AND " +
           "(LOWER(e.examName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.examCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchExams(@Param("keyword") String keyword, Pageable pageable);
    
    // Multi-tenancy: Find by business owner
    Page<Exam> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.examDate > :currentDate AND e.isDeleted = false")
    List<Exam> findUpcomingExamsByOwner(@Param("ownerId") Long ownerId, @Param("currentDate") LocalDate currentDate);

    // Multi-tenant queries
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.isDeleted = false AND " +
           "(LOWER(e.examName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.examCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.subject.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.schoolClass.className) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.status = :status AND e.isDeleted = false")
    Page<Exam> findByOwner_IdAndStatusAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("status") Exam.ExamStatus status, Pageable pageable);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.schoolClass.id = :classId AND e.isDeleted = false")
    Page<Exam> findByOwner_IdAndSchoolClass_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("classId") Long classId, Pageable pageable);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.subject.id = :subjectId AND e.isDeleted = false")
    List<Exam> findByOwner_IdAndSubject_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("subjectId") Long subjectId);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.examType = :examType AND e.isDeleted = false")
    List<Exam> findByOwner_IdAndExamTypeAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("examType") Exam.ExamType examType);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.examDate BETWEEN :startDate AND :endDate AND e.isDeleted = false")
    List<Exam> findByOwner_IdAndExamDateBetweenAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.semester = :semester AND e.isDeleted = false")
    List<Exam> findByOwner_IdAndSemesterAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("semester") String semester);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.academicYear = :academicYear AND e.isDeleted = false")
    List<Exam> findByOwner_IdAndAcademicYearAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("academicYear") String academicYear);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.supervisor.id = :supervisorId AND e.isDeleted = false")
    List<Exam> findByOwner_IdAndSupervisor_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("supervisorId") Long supervisorId);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.examDate <= :currentDate AND e.status = 'SCHEDULED' AND e.isDeleted = false")
    List<Exam> findOverdueExamsByOwner(@Param("ownerId") Long ownerId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT e FROM Exam e WHERE e.owner.id = :ownerId AND e.examDate = :examDate AND e.isDeleted = false")
    List<Exam> findByOwner_IdAndExamDateAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("examDate") LocalDate examDate);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Exam> findByIdAndOwner_Id(Long id, Long ownerId);
    
    Optional<Exam> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
    
    // Multi-tenant aware existence checks
    boolean existsByExamCodeAndOwner_Id(String examCode, Long ownerId);
    
    // Parent Portal queries
    long countBySchoolClass_IdAndOwner_IdAndExamDateAfterAndIsDeletedFalse(Long classId, Long ownerId, LocalDate date);
}

