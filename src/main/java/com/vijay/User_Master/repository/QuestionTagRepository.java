package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for QuestionTag entity
 */
@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {

    // Find by owner
    List<QuestionTag> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    // Find by tag name
    Optional<QuestionTag> findByTagNameAndOwnerIdAndIsDeletedFalse(String tagName, Long ownerId);

    // Search by tag name
    @Query("SELECT t FROM QuestionTag t WHERE t.owner.id = :ownerId AND t.isDeleted = false " +
           "AND LOWER(t.tagName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<QuestionTag> searchByTagName(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);

    // Check if tag exists
    boolean existsByTagNameAndOwnerIdAndIsDeletedFalse(String tagName, Long ownerId);

    // Find by ID and owner
    Optional<QuestionTag> findByIdAndOwnerIdAndIsDeletedFalse(Long id, Long ownerId);
}
