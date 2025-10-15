package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CustomFieldService {

    // ==================== CUSTOM FIELD DEFINITIONS ====================

    /**
     * Get all custom fields with pagination and filtering
     */
    Page<CustomFieldDTO> getAllCustomFields(Long ownerId, String entityType, String fieldGroup, 
                                           String searchTerm, Pageable pageable);

    /**
     * Get custom fields by entity type
     */
    List<CustomFieldDTO> getCustomFieldsByEntityType(Long ownerId, String entityType);

    /**
     * Get custom field by ID
     */
    CustomFieldDTO getCustomFieldById(Long id, Long ownerId);

    /**
     * Create new custom field
     */
    CustomFieldDTO createCustomField(CustomFieldRequestDTO request, Long ownerId);

    /**
     * Update existing custom field
     */
    CustomFieldDTO updateCustomField(Long id, CustomFieldRequestDTO request, Long ownerId);

    /**
     * Delete custom field (soft delete)
     */
    void deleteCustomField(Long id, Long ownerId);

    /**
     * Update custom field status (activate/deactivate)
     */
    CustomFieldDTO updateCustomFieldStatus(Long id, boolean isActive, Long ownerId);

    /**
     * Update display order of custom fields
     */
    List<CustomFieldDTO> updateCustomFieldsOrder(Map<Long, Integer> fieldOrderMap, Long ownerId);

    /**
     * Get field groups for entity type
     */
    List<String> getFieldGroupsByEntityType(String entityType, Long ownerId);

    // ==================== CUSTOM FIELD VALUES ====================

    /**
     * Get custom field values for a specific entity
     */
    List<CustomFieldValueDTO> getCustomFieldValuesForEntity(String entityType, Long entityId, Long ownerId);

    /**
     * Save custom field value for a specific entity
     */
    CustomFieldValueDTO saveCustomFieldValue(CustomFieldValueRequestDTO request, Long ownerId);

    /**
     * Save multiple custom field values for a specific entity
     */
    List<CustomFieldValueDTO> saveBulkCustomFieldValues(CustomFieldValueRequestDTO request, Long ownerId);

    /**
     * Update custom field value
     */
    CustomFieldValueDTO updateCustomFieldValue(Long id, CustomFieldValueRequestDTO request, Long ownerId);

    /**
     * Delete custom field value
     */
    void deleteCustomFieldValue(Long id, Long ownerId);

    /**
     * Search entities by custom field values
     */
    List<Long> searchEntitiesByCustomFieldValues(String entityType, String fieldKey, String searchTerm, Long ownerId);

    /**
     * Get custom field values as key-value pairs for an entity
     */
    Map<String, String> getCustomFieldValuesAsKeyValuePairs(String entityType, Long entityId, Long ownerId);

    // ==================== UTILITY METHODS ====================

    /**
     * Get available field types
     */
    List<String> getAvailableFieldTypes();

    /**
     * Get available entity types
     */
    List<String> getAvailableEntityTypes();

    /**
     * Validate custom field configuration
     */
    Map<String, Object> validateCustomFieldConfiguration(CustomFieldRequestDTO request);
}
