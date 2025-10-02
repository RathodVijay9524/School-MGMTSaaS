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
}

