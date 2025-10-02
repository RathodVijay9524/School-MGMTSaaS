package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Find by student
    Page<Grade> findByStudent_Id(Long studentId, Pageable pageable);
    
    // Find by student and subject
    List<Grade> findByStudent_IdAndSubject_Id(Long studentId, Long subjectId);
    
    // Find by student and semester
    List<Grade> findByStudent_IdAndSemester(Long studentId, String semester);
    
    // Find by student and academic year
    List<Grade> findByStudent_IdAndAcademicYear(Long studentId, String academicYear);
    
    // Find by exam
    List<Grade> findByExam_Id(Long examId);
    
    // Find by assignment
    List<Grade> findByAssignment_Id(Long assignmentId);
    
    // Find by grade type
    List<Grade> findByStudent_IdAndGradeType(Long studentId, Grade.GradeType gradeType);
    
    // Find published grades
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.isPublished = true")
    List<Grade> findPublishedGrades(@Param("studentId") Long studentId);
    
    // Calculate average percentage for student in subject
    @Query("SELECT AVG(g.percentage) FROM Grade g WHERE g.student.id = :studentId " +
           "AND g.subject.id = :subjectId AND g.isPublished = true")
    Double calculateAveragePercentage(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);
    
    // Get overall GPA
    @Query("SELECT AVG(g.percentage) FROM Grade g WHERE g.student.id = :studentId AND g.isPublished = true")
    Double calculateOverallGPA(@Param("studentId") Long studentId);
    
    // Find failing grades
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.status = 'FAIL'")
    List<Grade> findFailingGrades(@Param("studentId") Long studentId);
    
    // Multi-tenancy: Find by business owner
    @Query("SELECT g FROM Grade g WHERE g.owner.id = :ownerId AND g.isPublished = true")
    Page<Grade> findByOwnerAndPublished(@Param("ownerId") Long ownerId, Pageable pageable);
    
    @Query("SELECT g FROM Grade g WHERE g.owner.id = :ownerId AND g.academicYear = :academicYear")
    List<Grade> findByOwnerAndAcademicYear(@Param("ownerId") Long ownerId, @Param("academicYear") String academicYear);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Grade> findByIdAndOwner_Id(Long id, Long ownerId);
}

