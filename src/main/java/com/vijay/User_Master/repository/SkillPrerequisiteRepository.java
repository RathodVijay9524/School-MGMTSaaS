package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.SkillPrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for SkillPrerequisite entity
 */
@Repository
public interface SkillPrerequisiteRepository extends JpaRepository<SkillPrerequisite, Long> {

    // Find prerequisites for a skill
    List<SkillPrerequisite> findBySkillKeyAndIsActiveTrueAndIsDeletedFalse(String skillKey);

    // Find prerequisites by subject
    List<SkillPrerequisite> findBySubjectIdAndIsActiveTrueAndIsDeletedFalse(Long subjectId);

    // Find all skills that require a specific prerequisite
    List<SkillPrerequisite> findByPrerequisiteSkillKeyAndIsActiveTrueAndIsDeletedFalse(String prerequisiteSkillKey);

    // Find strict prerequisites for a skill
    @Query("SELECT sp FROM SkillPrerequisite sp WHERE sp.skillKey = :skillKey AND sp.isStrict = true AND sp.isActive = true AND sp.isDeleted = false")
    List<SkillPrerequisite> findStrictPrerequisites(@Param("skillKey") String skillKey);

    // Find by owner
    List<SkillPrerequisite> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    // Check if prerequisite exists
    boolean existsBySkillKeyAndPrerequisiteSkillKeyAndIsDeletedFalse(String skillKey, String prerequisiteSkillKey);

    // Get prerequisite chain (recursive)
    @Query("SELECT sp FROM SkillPrerequisite sp WHERE sp.skillKey = :skillKey AND sp.isActive = true AND sp.isDeleted = false ORDER BY sp.weight DESC")
    List<SkillPrerequisite> getPrerequisiteChain(@Param("skillKey") String skillKey);

    // Find high priority prerequisites
    @Query("SELECT sp FROM SkillPrerequisite sp WHERE sp.skillKey = :skillKey AND sp.weight >= 0.8 AND sp.isActive = true AND sp.isDeleted = false")
    List<SkillPrerequisite> findHighPriorityPrerequisites(@Param("skillKey") String skillKey);

    // Count prerequisites for a skill
    long countBySkillKeyAndIsActiveTrueAndIsDeletedFalse(String skillKey);

    // Find skills with most prerequisites
    @Query("SELECT sp.skillKey, COUNT(sp) as prereqCount FROM SkillPrerequisite sp WHERE sp.isActive = true AND sp.isDeleted = false GROUP BY sp.skillKey ORDER BY prereqCount DESC")
    List<Object[]> findSkillsWithMostPrerequisites();
}
