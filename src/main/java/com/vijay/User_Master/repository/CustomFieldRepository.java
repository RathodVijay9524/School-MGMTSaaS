package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.CustomField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {

    // Find all custom fields for a specific owner (multi-tenancy)
    List<CustomField> findByOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(Long ownerId);
    
    // Find custom fields by entity type for a specific owner
    List<CustomField> findByEntityTypeAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
        String entityType, Long ownerId);
    
    // Find custom fields by field group
    List<CustomField> findByFieldGroupAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
        String fieldGroup, Long ownerId);
    
    // Find custom fields by field type
    List<CustomField> findByFieldTypeAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
        CustomField.FieldType fieldType, Long ownerId);
    
    // Find searchable custom fields for a specific entity type
    List<CustomField> findByEntityTypeAndIsSearchableTrueAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
        String entityType, Long ownerId);
    
    // Find required custom fields for a specific entity type
    List<CustomField> findByEntityTypeAndIsRequiredTrueAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
        String entityType, Long ownerId);
    
    // Check if field key already exists for owner and entity type
    boolean existsByFieldKeyAndEntityTypeAndOwnerIdAndIsDeletedFalse(String fieldKey, String entityType, Long ownerId);
    
    // Find custom field by field key, entity type and owner
    Optional<CustomField> findByFieldKeyAndEntityTypeAndOwnerIdAndIsDeletedFalse(String fieldKey, String entityType, Long ownerId);
    
    // Search custom fields by name or description
    @Query("SELECT cf FROM CustomField cf WHERE cf.owner.id = :ownerId AND cf.isDeleted = false AND cf.isActive = true " +
           "AND (LOWER(cf.fieldName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(cf.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(cf.fieldKey) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY cf.displayOrder ASC")
    List<CustomField> searchCustomFields(@Param("ownerId") Long ownerId, @Param("searchTerm") String searchTerm);
    
    // Get paginated custom fields for owner
    Page<CustomField> findByOwnerIdAndIsDeletedFalseOrderByDisplayOrderAsc(Long ownerId, Pageable pageable);
    
    // Get custom fields by entity type with pagination
    Page<CustomField> findByEntityTypeAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
        String entityType, Long ownerId, Pageable pageable);
    
    // Count custom fields by entity type for owner
    long countByEntityTypeAndOwnerIdAndIsDeletedFalseAndIsActiveTrue(String entityType, Long ownerId);
    
    // Count custom fields by field type for owner
    long countByFieldTypeAndOwnerIdAndIsDeletedFalseAndIsActiveTrue(CustomField.FieldType fieldType, Long ownerId);
    
    // Find custom fields by multiple entity types
    @Query("SELECT cf FROM CustomField cf WHERE cf.entityType IN :entityTypes AND cf.owner.id = :ownerId " +
           "AND cf.isDeleted = false AND cf.isActive = true ORDER BY cf.displayOrder ASC")
    List<CustomField> findByEntityTypesAndOwnerId(@Param("entityTypes") List<String> entityTypes, @Param("ownerId") Long ownerId);
    
    // Get field groups for a specific entity type and owner
    @Query("SELECT DISTINCT cf.fieldGroup FROM CustomField cf WHERE cf.entityType = :entityType " +
           "AND cf.owner.id = :ownerId AND cf.isDeleted = false AND cf.isActive = true " +
           "AND cf.fieldGroup IS NOT NULL ORDER BY cf.fieldGroup ASC")
    List<String> findDistinctFieldGroupsByEntityTypeAndOwnerId(@Param("entityType") String entityType, @Param("ownerId") Long ownerId);
    
    // Find custom fields with specific field types for entity type
    @Query("SELECT cf FROM CustomField cf WHERE cf.entityType = :entityType AND cf.fieldType IN :fieldTypes " +
           "AND cf.owner.id = :ownerId AND cf.isDeleted = false AND cf.isActive = true ORDER BY cf.displayOrder ASC")
    List<CustomField> findByEntityTypeAndFieldTypesAndOwnerId(
        @Param("entityType") String entityType, 
        @Param("fieldTypes") List<CustomField.FieldType> fieldTypes, 
        @Param("ownerId") Long ownerId);
    
    // Soft delete custom field
    @Query("UPDATE CustomField cf SET cf.isDeleted = true WHERE cf.id = :id AND cf.owner.id = :ownerId")
    void softDeleteByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    // Activate/Deactivate custom field
    @Query("UPDATE CustomField cf SET cf.isActive = :isActive WHERE cf.id = :id AND cf.owner.id = :ownerId")
    void updateActiveStatus(@Param("id") Long id, @Param("ownerId") Long ownerId, @Param("isActive") boolean isActive);
    
    // Update display order for multiple fields
    @Query("UPDATE CustomField cf SET cf.displayOrder = :displayOrder WHERE cf.id = :id AND cf.owner.id = :ownerId")
    void updateDisplayOrder(@Param("id") Long id, @Param("ownerId") Long ownerId, @Param("displayOrder") Integer displayOrder);
}
