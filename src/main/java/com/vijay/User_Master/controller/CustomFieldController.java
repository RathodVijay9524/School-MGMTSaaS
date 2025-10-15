package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.CustomField;
import com.vijay.User_Master.service.CustomFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/custom-fields")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    // ==================== CUSTOM FIELD DEFINITIONS ====================

    /**
     * Get all custom fields for the authenticated user's school
     */
    @GetMapping
    public ResponseEntity<Page<CustomFieldDTO>> getAllCustomFields(
            Authentication authentication,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String fieldGroup,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        Page<CustomFieldDTO> customFields = customFieldService.getAllCustomFields(
                ownerId, entityType, fieldGroup, searchTerm, pageable);
        return ResponseEntity.ok(customFields);
    }

    /**
     * Get custom fields by entity type
     */
    @GetMapping("/entity/{entityType}")
    public ResponseEntity<List<CustomFieldDTO>> getCustomFieldsByEntityType(
            Authentication authentication,
            @PathVariable String entityType) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        List<CustomFieldDTO> customFields = customFieldService.getCustomFieldsByEntityType(ownerId, entityType);
        return ResponseEntity.ok(customFields);
    }

    /**
     * Get custom field by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomFieldDTO> getCustomFieldById(
            Authentication authentication,
            @PathVariable Long id) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        CustomFieldDTO customField = customFieldService.getCustomFieldById(id, ownerId);
        return ResponseEntity.ok(customField);
    }

    /**
     * Create new custom field
     */
    @PostMapping
    public ResponseEntity<CustomFieldDTO> createCustomField(
            Authentication authentication,
            @RequestBody CustomFieldRequestDTO request) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        CustomFieldDTO customField = customFieldService.createCustomField(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(customField);
    }

    /**
     * Update existing custom field
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomFieldDTO> updateCustomField(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody CustomFieldRequestDTO request) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        CustomFieldDTO customField = customFieldService.updateCustomField(id, request, ownerId);
        return ResponseEntity.ok(customField);
    }

    /**
     * Delete custom field (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomField(
            Authentication authentication,
            @PathVariable Long id) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        customFieldService.deleteCustomField(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activate/Deactivate custom field
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomFieldDTO> updateCustomFieldStatus(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        CustomFieldDTO customField = customFieldService.updateCustomFieldStatus(id, isActive, ownerId);
        return ResponseEntity.ok(customField);
    }

    /**
     * Update display order of custom fields
     */
    @PutMapping("/reorder")
    public ResponseEntity<List<CustomFieldDTO>> updateCustomFieldsOrder(
            Authentication authentication,
            @RequestBody Map<Long, Integer> fieldOrderMap) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        List<CustomFieldDTO> customFields = customFieldService.updateCustomFieldsOrder(fieldOrderMap, ownerId);
        return ResponseEntity.ok(customFields);
    }

    /**
     * Get field groups for entity type
     */
    @GetMapping("/entity/{entityType}/groups")
    public ResponseEntity<List<String>> getFieldGroupsByEntityType(
            Authentication authentication,
            @PathVariable String entityType) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        List<String> fieldGroups = customFieldService.getFieldGroupsByEntityType(entityType, ownerId);
        return ResponseEntity.ok(fieldGroups);
    }

    // ==================== CUSTOM FIELD VALUES ====================

    /**
     * Get custom field values for a specific entity
     */
    @GetMapping("/values/entity/{entityType}/{entityId}")
    public ResponseEntity<List<CustomFieldValueDTO>> getCustomFieldValuesForEntity(
            Authentication authentication,
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        List<CustomFieldValueDTO> values = customFieldService.getCustomFieldValuesForEntity(
                entityType, entityId, ownerId);
        return ResponseEntity.ok(values);
    }

    /**
     * Save custom field value for a specific entity
     */
    @PostMapping("/values")
    public ResponseEntity<CustomFieldValueDTO> saveCustomFieldValue(
            Authentication authentication,
            @RequestBody CustomFieldValueRequestDTO request) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        CustomFieldValueDTO value = customFieldService.saveCustomFieldValue(request, ownerId);
        return ResponseEntity.ok(value);
    }

    /**
     * Save multiple custom field values for a specific entity
     */
    @PostMapping("/values/bulk")
    public ResponseEntity<List<CustomFieldValueDTO>> saveBulkCustomFieldValues(
            Authentication authentication,
            @RequestBody CustomFieldValueRequestDTO request) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        List<CustomFieldValueDTO> values = customFieldService.saveBulkCustomFieldValues(request, ownerId);
        return ResponseEntity.ok(values);
    }

    /**
     * Update custom field value
     */
    @PutMapping("/values/{id}")
    public ResponseEntity<CustomFieldValueDTO> updateCustomFieldValue(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody CustomFieldValueRequestDTO request) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        CustomFieldValueDTO value = customFieldService.updateCustomFieldValue(id, request, ownerId);
        return ResponseEntity.ok(value);
    }

    /**
     * Delete custom field value
     */
    @DeleteMapping("/values/{id}")
    public ResponseEntity<Void> deleteCustomFieldValue(
            Authentication authentication,
            @PathVariable Long id) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        customFieldService.deleteCustomFieldValue(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search entities by custom field values
     */
    @GetMapping("/values/search")
    public ResponseEntity<List<Long>> searchEntitiesByCustomFieldValues(
            Authentication authentication,
            @RequestParam String entityType,
            @RequestParam String fieldKey,
            @RequestParam String searchTerm) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        List<Long> entityIds = customFieldService.searchEntitiesByCustomFieldValues(
                entityType, fieldKey, searchTerm, ownerId);
        return ResponseEntity.ok(entityIds);
    }

    /**
     * Get custom field values as key-value pairs for an entity
     */
    @GetMapping("/values/entity/{entityType}/{entityId}/key-value")
    public ResponseEntity<Map<String, String>> getCustomFieldValuesAsKeyValuePairs(
            Authentication authentication,
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        
        Long ownerId = getOwnerIdFromAuthentication(authentication);
        Map<String, String> keyValuePairs = customFieldService.getCustomFieldValuesAsKeyValuePairs(
                entityType, entityId, ownerId);
        return ResponseEntity.ok(keyValuePairs);
    }

    // ==================== UTILITY ENDPOINTS ====================

    /**
     * Get available field types
     */
    @GetMapping("/field-types")
    public ResponseEntity<List<String>> getAvailableFieldTypes() {
        List<String> fieldTypes = customFieldService.getAvailableFieldTypes();
        return ResponseEntity.ok(fieldTypes);
    }

    /**
     * Get available entity types
     */
    @GetMapping("/entity-types")
    public ResponseEntity<List<String>> getAvailableEntityTypes() {
        List<String> entityTypes = customFieldService.getAvailableEntityTypes();
        return ResponseEntity.ok(entityTypes);
    }

    /**
     * Validate custom field configuration
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCustomFieldConfiguration(
            @RequestBody CustomFieldRequestDTO request) {
        
        Map<String, Object> validationResult = customFieldService.validateCustomFieldConfiguration(request);
        return ResponseEntity.ok(validationResult);
    }

    // ==================== HELPER METHODS ====================

    private Long getOwnerIdFromAuthentication(Authentication authentication) {
        // Extract owner ID from authentication
        // This should be implemented based on your authentication mechanism
        // For now, returning a default value - implement according to your auth system
        return 1L; // Replace with actual owner ID extraction logic
    }
}
