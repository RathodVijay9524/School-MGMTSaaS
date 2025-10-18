package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.AttemptStatus;
import com.vijay.User_Master.entity.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for QuizAttempt entity
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // Find by quiz
    List<QuizAttempt> findByQuizIdAndIsDeletedFalse(Long quizId);
    
    Page<QuizAttempt> findByQuizIdAndIsDeletedFalse(Long quizId, Pageable pageable);

    // Find by student
    List<QuizAttempt> findByStudentIdAndIsDeletedFalse(Long studentId);
    
    Page<QuizAttempt> findByStudentIdAndIsDeletedFalse(Long studentId, Pageable pageable);

    // Find by quiz and student
    List<QuizAttempt> findByQuizIdAndStudentIdAndIsDeletedFalse(Long quizId, Long studentId);

    // Find by status
    List<QuizAttempt> findByStatusAndIsDeletedFalse(AttemptStatus status);
    
    List<QuizAttempt> findByQuizIdAndStatusAndIsDeletedFalse(Long quizId, AttemptStatus status);

    // Find in-progress attempts
    List<QuizAttempt> findByStudentIdAndStatusAndIsDeletedFalse(Long studentId, AttemptStatus status);

    // Count attempts by student for a quiz
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
           "AND qa.student.id = :studentId AND qa.isDeleted = false")
    Long countAttemptsByStudentAndQuiz(@Param("quizId") Long quizId, @Param("studentId") Long studentId);

    // Find latest attempt
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
           "AND qa.student.id = :studentId AND qa.isDeleted = false " +
           "ORDER BY qa.attemptNumber DESC")
    Optional<QuizAttempt> findLatestAttempt(@Param("quizId") Long quizId, @Param("studentId") Long studentId);

    // Find best attempt (highest score)
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
           "AND qa.student.id = :studentId AND qa.isDeleted = false " +
           "AND qa.totalScore IS NOT NULL " +
           "ORDER BY qa.totalScore DESC")
    Optional<QuizAttempt> findBestAttempt(@Param("quizId") Long quizId, @Param("studentId") Long studentId);

    // Find passed attempts
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
           "AND qa.passed = true AND qa.isDeleted = false")
    List<QuizAttempt> findPassedAttempts(@Param("quizId") Long quizId);

    // Statistics
    @Query("SELECT AVG(qa.totalScore) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
           "AND qa.status = 'GRADED' AND qa.isDeleted = false")
    Double getAverageScore(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
           "AND qa.status = 'GRADED' AND qa.isDeleted = false")
    Long countCompletedAttempts(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(DISTINCT qa.student.id) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
           "AND qa.isDeleted = false")
    Long countUniqueStudents(@Param("quizId") Long quizId);

    // Find by ID and owner
    Optional<QuizAttempt> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);
}
