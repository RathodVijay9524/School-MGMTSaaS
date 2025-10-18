package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for QuestionPool entity
 */
@Repository
public interface QuestionPoolRepository extends JpaRepository<QuestionPool, Long> {

    // Find by owner
    List<QuestionPool> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    Page<QuestionPool> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);

    // Find by subject
    List<QuestionPool> findBySubjectIdAndIsDeletedFalse(Long subjectId);

    // Find by difficulty
    List<QuestionPool> findByTargetDifficultyAndOwnerIdAndIsDeletedFalse(DifficultyLevel difficulty, Long ownerId);

    // Find active pools
    List<QuestionPool> findByIsActiveTrueAndOwnerIdAndIsDeletedFalse(Long ownerId);

    // Search by pool name
    @Query("SELECT qp FROM QuestionPool qp WHERE qp.owner.id = :ownerId AND qp.isDeleted = false " +
           "AND LOWER(qp.poolName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<QuestionPool> searchByPoolName(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);

    // Find by ID and owner
    Optional<QuestionPool> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);

    // Statistics
    @Query("SELECT COUNT(qp) FROM QuestionPool qp WHERE qp.owner.id = :ownerId AND qp.isDeleted = false")
    Long countByOwnerId(@Param("ownerId") Long ownerId);
}
