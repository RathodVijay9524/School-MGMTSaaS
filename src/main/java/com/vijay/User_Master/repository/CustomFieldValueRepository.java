package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.CustomFieldValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, Long> {

    // Find all custom field values for a specific entity
    List<CustomFieldValue> findByEntityTypeAndEntityIdAndOwnerIdAndIsDeletedFalse(String entityType, Long entityId, Long ownerId);
    
    // Find custom field values by entity type for owner
    List<CustomFieldValue> findByEntityTypeAndOwnerIdAndIsDeletedFalse(String entityType, Long ownerId);
    
    // Find custom field value by custom field and entity
    Optional<CustomFieldValue> findByCustomFieldIdAndEntityTypeAndEntityIdAndOwnerIdAndIsDeletedFalse(
        Long customFieldId, String entityType, Long entityId, Long ownerId);
    
    // Find custom field values by custom field ID
    List<CustomFieldValue> findByCustomFieldIdAndOwnerIdAndIsDeletedFalse(Long customFieldId, Long ownerId);
    
    // Find custom field values by field key and entity
    @Query("SELECT cfv FROM CustomFieldValue cfv JOIN cfv.customField cf " +
           "WHERE cf.fieldKey = :fieldKey AND cfv.entityType = :entityType AND cfv.entityId = :entityId " +
           "AND cfv.owner.id = :ownerId AND cfv.isDeleted = false")
    Optional<CustomFieldValue> findByFieldKeyAndEntityTypeAndEntityIdAndOwnerId(
        @Param("fieldKey") String fieldKey, @Param("entityType") String entityType, 
        @Param("entityId") Long entityId, @Param("ownerId") Long ownerId);
    
    // Find custom field values by field keys and entity
    @Query("SELECT cfv FROM CustomFieldValue cfv JOIN cfv.customField cf " +
           "WHERE cf.fieldKey IN :fieldKeys AND cfv.entityType = :entityType AND cfv.entityId = :entityId " +
           "AND cfv.owner.id = :ownerId AND cfv.isDeleted = false")
    List<CustomFieldValue> findByFieldKeysAndEntityTypeAndEntityIdAndOwnerId(
        @Param("fieldKeys") List<String> fieldKeys, @Param("entityType") String entityType, 
        @Param("entityId") Long entityId, @Param("ownerId") Long ownerId);
    
    // Search custom field values by field value content
    @Query("SELECT cfv FROM CustomFieldValue cfv WHERE cfv.owner.id = :ownerId AND cfv.isDeleted = false " +
           "AND LOWER(cfv.fieldValue) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CustomFieldValue> searchByFieldValue(@Param("ownerId") Long ownerId, @Param("searchTerm") String searchTerm);
    
    // Search custom field values by entity type and field value
    @Query("SELECT cfv FROM CustomFieldValue cfv WHERE cfv.entityType = :entityType " +
           "AND cfv.owner.id = :ownerId AND cfv.isDeleted = false " +
           "AND LOWER(cfv.fieldValue) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CustomFieldValue> searchByEntityTypeAndFieldValue(
        @Param("entityType") String entityType, @Param("ownerId") Long ownerId, @Param("searchTerm") String searchTerm);
    
    // Get paginated custom field values for owner
    Page<CustomFieldValue> findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(Long ownerId, Pageable pageable);
    
    // Get custom field values by entity type with pagination
    Page<CustomFieldValue> findByEntityTypeAndOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(
        String entityType, Long ownerId, Pageable pageable);
    
    // Count custom field values by entity type for owner
    long countByEntityTypeAndOwnerIdAndIsDeletedFalse(String entityType, Long ownerId);
    
    // Count custom field values by custom field for owner
    long countByCustomFieldIdAndOwnerIdAndIsDeletedFalse(Long customFieldId, Long ownerId);
    
    // Find entities that have specific custom field values
    @Query("SELECT DISTINCT cfv.entityId FROM CustomFieldValue cfv JOIN cfv.customField cf " +
           "WHERE cf.fieldKey = :fieldKey AND cfv.entityType = :entityType " +
           "AND cfv.owner.id = :ownerId AND cfv.isDeleted = false " +
           "AND LOWER(cfv.fieldValue) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Long> findEntityIdsByFieldKeyAndValue(
        @Param("fieldKey") String fieldKey, @Param("entityType") String entityType, 
        @Param("ownerId") Long ownerId, @Param("searchTerm") String searchTerm);
    
    // Find custom field values for multiple entities
    @Query("SELECT cfv FROM CustomFieldValue cfv WHERE cfv.entityType = :entityType " +
           "AND cfv.entityId IN :entityIds AND cfv.owner.id = :ownerId AND cfv.isDeleted = false")
    List<CustomFieldValue> findByEntityTypeAndEntityIdsAndOwnerId(
        @Param("entityType") String entityType, @Param("entityIds") List<Long> entityIds, @Param("ownerId") Long ownerId);
    
    // Get field values grouped by field key for an entity
    @Query("SELECT cf.fieldKey as fieldKey, cfv.fieldValue as fieldValue " +
           "FROM CustomFieldValue cfv JOIN cfv.customField cf " +
           "WHERE cfv.entityType = :entityType AND cfv.entityId = :entityId " +
           "AND cfv.owner.id = :ownerId AND cfv.isDeleted = false")
    List<Map<String, Object>> getFieldValuesGroupedByKey(
        @Param("entityType") String entityType, @Param("entityId") Long entityId, @Param("ownerId") Long ownerId);
    
    // Delete custom field values for a specific entity
    @Modifying
    @Transactional
    @Query("UPDATE CustomFieldValue cfv SET cfv.isDeleted = true WHERE cfv.entityType = :entityType " +
           "AND cfv.entityId = :entityId AND cfv.owner.id = :ownerId")
    void softDeleteByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId, @Param("ownerId") Long ownerId);
    
    // Delete custom field values by custom field ID
    @Modifying
    @Transactional
    @Query("UPDATE CustomFieldValue cfv SET cfv.isDeleted = true WHERE cfv.customField.id = :customFieldId " +
           "AND cfv.owner.id = :ownerId")
    void softDeleteByCustomFieldId(@Param("customFieldId") Long customFieldId, @Param("ownerId") Long ownerId);
    
    // Find custom field values with specific field values (for filtering)
    @Query("SELECT cfv FROM CustomFieldValue cfv JOIN cfv.customField cf " +
           "WHERE cf.fieldKey = :fieldKey AND cfv.entityType = :entityType " +
           "AND cfv.owner.id = :ownerId AND cfv.isDeleted = false " +
           "AND cfv.fieldValue = :fieldValue")
    List<CustomFieldValue> findByFieldKeyAndEntityTypeAndValueAndOwnerId(
        @Param("fieldKey") String fieldKey, @Param("entityType") String entityType, 
        @Param("ownerId") Long ownerId, @Param("fieldValue") String fieldValue);
    
    // Get custom field values as key-value pairs for an entity
    @Query("SELECT NEW map(cf.fieldKey, cfv.fieldValue) FROM CustomFieldValue cfv JOIN cfv.customField cf " +
           "WHERE cfv.entityType = :entityType AND cfv.entityId = :entityId " +
           "AND cfv.owner.id = :ownerId AND cfv.isDeleted = false")
    List<Map<String, String>> getFieldValuesAsKeyValuePairs(
        @Param("entityType") String entityType, @Param("entityId") Long entityId, @Param("ownerId") Long ownerId);
}
