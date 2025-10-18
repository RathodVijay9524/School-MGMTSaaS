package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Rubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Rubric entity
 */
@Repository
public interface RubricRepository extends JpaRepository<Rubric, Long> {

    // Find by owner
    List<Rubric> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    // Find by subject
    List<Rubric> findBySubjectIdAndIsDeletedFalse(Long subjectId);

    // Find active rubrics
    List<Rubric> findByOwnerIdAndIsActiveTrueAndIsDeletedFalse(Long ownerId);

    // Find by type
    List<Rubric> findByRubricTypeAndOwnerIdAndIsDeletedFalse(Rubric.RubricType rubricType, Long ownerId);

    // Find by name
    Optional<Rubric> findByNameAndOwnerIdAndIsDeletedFalse(String name, Long ownerId);

    // Search by name
    @Query("SELECT r FROM Rubric r WHERE r.owner.id = :ownerId AND LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND r.isDeleted = false")
    List<Rubric> searchByName(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);

    // Count by owner
    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
}
