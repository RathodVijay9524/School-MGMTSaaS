package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.HomeworkSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSubmissionRepository extends JpaRepository<HomeworkSubmission, Long> {

    // Find by student
    Page<HomeworkSubmission> findByStudent_IdAndIsDeletedFalse(Long studentId, Pageable pageable);
    
    // Find by assignment
    List<HomeworkSubmission> findByAssignment_IdAndIsDeletedFalse(Long assignmentId);
    
    // Find by student and assignment
    Optional<HomeworkSubmission> findByStudent_IdAndAssignment_IdAndIsDeletedFalse(Long studentId, Long assignmentId);
    
    // Find by status
    Page<HomeworkSubmission> findByStatusAndIsDeletedFalse(HomeworkSubmission.SubmissionStatus status, Pageable pageable);
    
    // Find late submissions
    @Query("SELECT hs FROM HomeworkSubmission hs WHERE hs.isLate = true AND hs.isDeleted = false")
    Page<HomeworkSubmission> findLateSubmissions(Pageable pageable);
    
    // Find pending submissions for grading
    @Query("SELECT hs FROM HomeworkSubmission hs WHERE hs.status IN ('SUBMITTED', 'LATE') AND hs.isDeleted = false")
    Page<HomeworkSubmission> findPendingForGrading(Pageable pageable);
    
    // Find graded submissions
    @Query("SELECT hs FROM HomeworkSubmission hs WHERE hs.status = 'GRADED' AND hs.isDeleted = false")
    Page<HomeworkSubmission> findGradedSubmissions(Pageable pageable);
    
    // Count submissions for assignment
    @Query("SELECT COUNT(hs) FROM HomeworkSubmission hs WHERE hs.assignment.id = :assignmentId AND hs.isDeleted = false")
    long countSubmissionsForAssignment(@Param("assignmentId") Long assignmentId);
    
    // Calculate submission rate
    @Query("SELECT (COUNT(hs) * 100.0 / :totalStudents) FROM HomeworkSubmission hs " +
           "WHERE hs.assignment.id = :assignmentId AND hs.isDeleted = false")
    Double calculateSubmissionRate(@Param("assignmentId") Long assignmentId, @Param("totalStudents") long totalStudents);
}

