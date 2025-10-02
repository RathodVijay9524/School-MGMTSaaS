package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.BookIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    // Find by student
    Page<BookIssue> findByStudent_Id(Long studentId, Pageable pageable);
    
    // Find by teacher
    Page<BookIssue> findByTeacher_Id(Long teacherId, Pageable pageable);
    
    // Find by book
    List<BookIssue> findByBook_Id(Long bookId);
    
    // Find by status
    Page<BookIssue> findByStatus(BookIssue.IssueStatus status, Pageable pageable);
    
    // Find overdue books
    @Query("SELECT bi FROM BookIssue bi WHERE bi.dueDate < :currentDate AND " +
           "bi.status = 'ISSUED'")
    List<BookIssue> findOverdueBooks(@Param("currentDate") LocalDate currentDate);
    
    // Find issued books by student
    @Query("SELECT bi FROM BookIssue bi WHERE bi.student.id = :studentId AND bi.status = 'ISSUED'")
    List<BookIssue> findIssuedBooksByStudent(@Param("studentId") Long studentId);
    
    // Find issued books by teacher
    @Query("SELECT bi FROM BookIssue bi WHERE bi.teacher.id = :teacherId AND bi.status = 'ISSUED'")
    List<BookIssue> findIssuedBooksByTeacher(@Param("teacherId") Long teacherId);
    
    // Count issued books by student
    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE bi.student.id = :studentId AND bi.status = 'ISSUED'")
    long countIssuedBooksByStudent(@Param("studentId") Long studentId);
    
    // Find by issue date range
    List<BookIssue> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find by return date range
    List<BookIssue> findByReturnDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Calculate total late fees
    @Query("SELECT SUM(bi.lateFee) FROM BookIssue bi WHERE bi.fineCollected = false AND bi.lateFee > 0")
    Double calculateTotalPendingFines();
}

