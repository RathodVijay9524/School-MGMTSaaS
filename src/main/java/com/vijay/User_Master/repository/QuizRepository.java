package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Quiz;
import com.vijay.User_Master.entity.QuizType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Quiz entity
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Find by owner
    List<Quiz> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    Page<Quiz> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);

    // Find by subject
    List<Quiz> findBySubjectIdAndIsDeletedFalse(Long subjectId);
    
    Page<Quiz> findBySubjectIdAndIsDeletedFalse(Long subjectId, Pageable pageable);

    // Find by class
    List<Quiz> findBySchoolClassIdAndIsDeletedFalse(Long classId);
    
    Page<Quiz> findBySchoolClassIdAndIsDeletedFalse(Long classId, Pageable pageable);

    // Find by quiz type
    List<Quiz> findByQuizTypeAndOwnerIdAndIsDeletedFalse(QuizType quizType, Long ownerId);

    // Find active quizzes
    List<Quiz> findByIsActiveTrueAndIsDeletedFalse();
    
    List<Quiz> findByIsActiveTrueAndOwnerIdAndIsDeletedFalse(Long ownerId);

    // Find published quizzes
    List<Quiz> findByIsPublishedTrueAndIsDeletedFalse();
    
    List<Quiz> findByIsPublishedTrueAndOwnerIdAndIsDeletedFalse(Long ownerId);

    // Find available quizzes (within date range)
    @Query("SELECT q FROM Quiz q WHERE q.owner.id = :ownerId AND q.isDeleted = false " +
           "AND q.isActive = true AND q.isPublished = true " +
           "AND (q.availableFrom IS NULL OR q.availableFrom <= :now) " +
           "AND (q.availableUntil IS NULL OR q.availableUntil >= :now)")
    List<Quiz> findAvailableQuizzes(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    // Find upcoming quizzes
    @Query("SELECT q FROM Quiz q WHERE q.owner.id = :ownerId AND q.isDeleted = false " +
           "AND q.availableFrom > :now")
    List<Quiz> findUpcomingQuizzes(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    // Find expired quizzes
    @Query("SELECT q FROM Quiz q WHERE q.owner.id = :ownerId AND q.isDeleted = false " +
           "AND q.availableUntil < :now")
    List<Quiz> findExpiredQuizzes(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    // Search by title
    @Query("SELECT q FROM Quiz q WHERE q.owner.id = :ownerId AND q.isDeleted = false " +
           "AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Quiz> searchByTitle(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);
    
    @Query("SELECT q FROM Quiz q WHERE q.owner.id = :ownerId AND q.isDeleted = false " +
           "AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Quiz> searchByTitle(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);

    // Find by subject and class
    List<Quiz> findBySubjectIdAndSchoolClassIdAndIsDeletedFalse(Long subjectId, Long classId);

    // Statistics
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.owner.id = :ownerId AND q.isDeleted = false")
    Long countByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.subject.id = :subjectId AND q.isDeleted = false")
    Long countBySubjectId(@Param("subjectId") Long subjectId);

    // Find by ID and owner
    Optional<Quiz> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);
}
