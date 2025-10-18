package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Question entity
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Find by owner
    List<Question> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    Page<Question> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);

    // Find by subject
    List<Question> findBySubjectIdAndIsDeletedFalse(Long subjectId);
    
    Page<Question> findBySubjectIdAndIsDeletedFalse(Long subjectId, Pageable pageable);

    // Find by class
    List<Question> findBySchoolClassIdAndIsDeletedFalse(Long classId);

    // Find by question type
    List<Question> findByQuestionTypeAndOwnerIdAndIsDeletedFalse(QuestionType questionType, Long ownerId);
    
    Page<Question> findByQuestionTypeAndOwnerIdAndIsDeletedFalse(QuestionType questionType, Long ownerId, Pageable pageable);

    // Find by difficulty
    List<Question> findByDifficultyAndOwnerIdAndIsDeletedFalse(DifficultyLevel difficulty, Long ownerId);
    
    Page<Question> findByDifficultyAndOwnerIdAndIsDeletedFalse(DifficultyLevel difficulty, Long ownerId, Pageable pageable);

    // Find by chapter
    List<Question> findByChapterAndSubjectIdAndIsDeletedFalse(String chapter, Long subjectId);

    // Find by topic
    List<Question> findByTopicAndSubjectIdAndIsDeletedFalse(String topic, Long subjectId);

    // Find by Bloom's level
    List<Question> findByBloomsLevelAndOwnerIdAndIsDeletedFalse(BloomsLevel bloomsLevel, Long ownerId);

    // Find active questions
    List<Question> findByIsActiveTrueAndIsDeletedFalse();
    
    List<Question> findByIsActiveTrueAndOwnerIdAndIsDeletedFalse(Long ownerId);

    // Find auto-gradable questions
    List<Question> findByAutoGradableTrueAndOwnerIdAndIsDeletedFalse(Long ownerId);

    // Search by question text
    @Query("SELECT q FROM Question q WHERE q.owner.id = :ownerId AND q.isDeleted = false " +
           "AND LOWER(q.questionText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Question> searchByQuestionText(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);
    
    @Query("SELECT q FROM Question q WHERE q.owner.id = :ownerId AND q.isDeleted = false " +
           "AND LOWER(q.questionText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> searchByQuestionText(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);

    // Find by tags
    @Query("SELECT DISTINCT q FROM Question q JOIN q.tags t WHERE t.id IN :tagIds AND q.owner.id = :ownerId AND q.isDeleted = false")
    List<Question> findByTagIds(@Param("tagIds") List<Long> tagIds, @Param("ownerId") Long ownerId);

    // Find by multiple criteria
    @Query("SELECT q FROM Question q WHERE q.owner.id = :ownerId " +
           "AND (:subjectId IS NULL OR q.subject.id = :subjectId) " +
           "AND (:questionType IS NULL OR q.questionType = :questionType) " +
           "AND (:difficulty IS NULL OR q.difficulty = :difficulty) " +
           "AND q.isDeleted = false")
    Page<Question> findByCriteria(@Param("ownerId") Long ownerId,
                                   @Param("subjectId") Long subjectId,
                                   @Param("questionType") QuestionType questionType,
                                   @Param("difficulty") DifficultyLevel difficulty,
                                   Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(q) FROM Question q WHERE q.owner.id = :ownerId AND q.isDeleted = false")
    Long countByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.subject.id = :subjectId AND q.isDeleted = false")
    Long countBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT q.questionType, COUNT(q) FROM Question q WHERE q.owner.id = :ownerId AND q.isDeleted = false GROUP BY q.questionType")
    List<Object[]> countByQuestionType(@Param("ownerId") Long ownerId);

    // Find by ID and owner
    Optional<Question> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);
}
