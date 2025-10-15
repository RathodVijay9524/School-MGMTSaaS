package com.vijay.User_Master.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.CustomField;
import com.vijay.User_Master.entity.CustomFieldValue;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.CustomFieldRepository;
import com.vijay.User_Master.repository.CustomFieldValueRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.CustomFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomFieldServiceImpl implements CustomFieldService {

    private final CustomFieldRepository customFieldRepository;
    private final CustomFieldValueRepository customFieldValueRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // ==================== CUSTOM FIELD DEFINITIONS ====================

    @Override
    @Transactional(readOnly = true)
    public Page<CustomFieldDTO> getAllCustomFields(Long ownerId, String entityType, String fieldGroup, 
                                                  String searchTerm, Pageable pageable) {
        log.info("Getting all custom fields for owner: {}, entityType: {}, fieldGroup: {}, searchTerm: {}", 
                ownerId, entityType, fieldGroup, searchTerm);

        Page<CustomField> customFields;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Search functionality would need to be implemented in repository
            customFields = customFieldRepository.findByOwnerIdAndIsDeletedFalseOrderByDisplayOrderAsc(ownerId, pageable);
        } else if (entityType != null && !entityType.trim().isEmpty()) {
            customFields = customFieldRepository.findByEntityTypeAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
                    entityType, ownerId, pageable);
        } else {
            customFields = customFieldRepository.findByOwnerIdAndIsDeletedFalseOrderByDisplayOrderAsc(ownerId, pageable);
        }

        return customFields.map(CustomFieldDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomFieldDTO> getCustomFieldsByEntityType(Long ownerId, String entityType) {
        log.info("Getting custom fields for entity type: {} and owner: {}", entityType, ownerId);
        
        List<CustomField> customFields = customFieldRepository.findByEntityTypeAndOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByDisplayOrderAsc(
                entityType, ownerId);
        
        return customFields.stream()
                .map(CustomFieldDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomFieldDTO getCustomFieldById(Long id, Long ownerId) {
        log.info("Getting custom field by id: {} for owner: {}", id, ownerId);
        
        CustomField customField = customFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom field not found with id: " + id));
        
        if (!customField.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Access denied to custom field");
        }
        
        return CustomFieldDTO.fromEntity(customField);
    }

    @Override
    public CustomFieldDTO createCustomField(CustomFieldRequestDTO request, Long ownerId) {
        log.info("Creating custom field: {} for owner: {}", request.getFieldName(), ownerId);
        
        // Validate field key uniqueness
        if (customFieldRepository.existsByFieldKeyAndEntityTypeAndOwnerIdAndIsDeletedFalse(
                request.getFieldKey(), request.getEntityType(), ownerId)) {
            throw new RuntimeException("Field key already exists for this entity type");
        }
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        CustomField customField = CustomField.builder()
                .fieldName(request.getFieldName())
                .fieldKey(request.getFieldKey())
                .description(request.getDescription())
                .fieldType(CustomField.FieldType.valueOf(request.getFieldType()))
                .entityType(request.getEntityType())
                .isRequired(request.isRequired())
                .placeholder(request.getPlaceholder())
                .defaultValue(request.getDefaultValue())
                .validationRules(convertValidationRulesToJson(request.getValidationRules()))
                .dropdownOptions(convertDropdownOptionsToJson(request.getDropdownOptions()))
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .fieldGroup(request.getFieldGroup())
                .isVisible(request.isVisible())
                .isSearchable(request.isSearchable())
                .helpText(request.getHelpText())
                .owner(owner)
                .isActive(true)
                .isDeleted(false)
                .build();
        
        CustomField savedCustomField = customFieldRepository.save(customField);
        log.info("Created custom field with id: {}", savedCustomField.getId());
        
        return CustomFieldDTO.fromEntity(savedCustomField);
    }

    @Override
    public CustomFieldDTO updateCustomField(Long id, CustomFieldRequestDTO request, Long ownerId) {
        log.info("Updating custom field: {} for owner: {}", id, ownerId);
        
        CustomField customField = customFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom field not found with id: " + id));
        
        if (!customField.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Access denied to custom field");
        }
        
        // Check if field key is being changed and if it conflicts
        if (!customField.getFieldKey().equals(request.getFieldKey()) && 
            customFieldRepository.existsByFieldKeyAndEntityTypeAndOwnerIdAndIsDeletedFalse(
                    request.getFieldKey(), request.getEntityType(), ownerId)) {
            throw new RuntimeException("Field key already exists for this entity type");
        }
        
        customField.setFieldName(request.getFieldName());
        customField.setFieldKey(request.getFieldKey());
        customField.setDescription(request.getDescription());
        customField.setFieldType(CustomField.FieldType.valueOf(request.getFieldType()));
        customField.setEntityType(request.getEntityType());
        customField.setRequired(request.isRequired());
        customField.setPlaceholder(request.getPlaceholder());
        customField.setDefaultValue(request.getDefaultValue());
        customField.setValidationRules(convertValidationRulesToJson(request.getValidationRules()));
        customField.setDropdownOptions(convertDropdownOptionsToJson(request.getDropdownOptions()));
        customField.setDisplayOrder(request.getDisplayOrder());
        customField.setFieldGroup(request.getFieldGroup());
        customField.setVisible(request.isVisible());
        customField.setSearchable(request.isSearchable());
        customField.setHelpText(request.getHelpText());
        
        CustomField updatedCustomField = customFieldRepository.save(customField);
        log.info("Updated custom field with id: {}", updatedCustomField.getId());
        
        return CustomFieldDTO.fromEntity(updatedCustomField);
    }

    @Override
    public void deleteCustomField(Long id, Long ownerId) {
        log.info("Deleting custom field: {} for owner: {}", id, ownerId);
        
        CustomField customField = customFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom field not found with id: " + id));
        
        if (!customField.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Access denied to custom field");
        }
        
        // Soft delete the custom field
        customField.setDeleted(true);
        customFieldRepository.save(customField);
        
        // Also soft delete all related custom field values
        try {
            customFieldValueRepository.softDeleteByCustomFieldId(id, ownerId);
            log.info("Successfully soft deleted custom field values for custom field: {}", id);
        } catch (Exception e) {
            log.warn("Failed to soft delete custom field values for custom field: {}, error: {}", id, e.getMessage());
            // Don't fail the operation if we can't delete the values
        }
        
        log.info("Soft deleted custom field with id: {}", id);
    }

    @Override
    public CustomFieldDTO updateCustomFieldStatus(Long id, boolean isActive, Long ownerId) {
        log.info("Updating custom field status: {} to {} for owner: {}", id, isActive, ownerId);
        
        CustomField customField = customFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom field not found with id: " + id));
        
        if (!customField.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Access denied to custom field");
        }
        
        customField.setActive(isActive);
        CustomField updatedCustomField = customFieldRepository.save(customField);
        
        log.info("Updated custom field status with id: {}", id);
        return CustomFieldDTO.fromEntity(updatedCustomField);
    }

    @Override
    public List<CustomFieldDTO> updateCustomFieldsOrder(Map<Long, Integer> fieldOrderMap, Long ownerId) {
        log.info("Updating custom fields order for owner: {}", ownerId);
        
        List<CustomFieldDTO> updatedFields = new ArrayList<>();
        
        for (Map.Entry<Long, Integer> entry : fieldOrderMap.entrySet()) {
            Long fieldId = entry.getKey();
            Integer displayOrder = entry.getValue();
            
            CustomField customField = customFieldRepository.findById(fieldId)
                    .orElseThrow(() -> new RuntimeException("Custom field not found with id: " + fieldId));
            
            if (!customField.getOwner().getId().equals(ownerId)) {
                throw new RuntimeException("Access denied to custom field");
            }
            
            customField.setDisplayOrder(displayOrder);
            customFieldRepository.save(customField);
            updatedFields.add(CustomFieldDTO.fromEntity(customField));
        }
        
        log.info("Updated custom fields order for {} fields", updatedFields.size());
        return updatedFields;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getFieldGroupsByEntityType(String entityType, Long ownerId) {
        log.info("Getting field groups for entity type: {} and owner: {}", entityType, ownerId);
        
        return customFieldRepository.findDistinctFieldGroupsByEntityTypeAndOwnerId(entityType, ownerId);
    }

    // ==================== CUSTOM FIELD VALUES ====================

    @Override
    @Transactional(readOnly = true)
    public List<CustomFieldValueDTO> getCustomFieldValuesForEntity(String entityType, Long entityId, Long ownerId) {
        log.info("Getting custom field values for entity: {} {} and owner: {}", entityType, entityId, ownerId);
        
        List<CustomFieldValue> values = customFieldValueRepository.findByEntityTypeAndEntityIdAndOwnerIdAndIsDeletedFalse(
                entityType, entityId, ownerId);
        
        return values.stream()
                .map(CustomFieldValueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public CustomFieldValueDTO saveCustomFieldValue(CustomFieldValueRequestDTO request, Long ownerId) {
        log.info("Saving custom field value for field: {} and entity: {} {}", 
                request.getCustomFieldId(), request.getEntityType(), request.getEntityId());
        
        CustomField customField = customFieldRepository.findById(request.getCustomFieldId())
                .orElseThrow(() -> new RuntimeException("Custom field not found with id: " + request.getCustomFieldId()));
        
        if (!customField.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Access denied to custom field");
        }
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Check if value already exists for this field and entity
        Optional<CustomFieldValue> existingValue = customFieldValueRepository
                .findByCustomFieldIdAndEntityTypeAndEntityIdAndOwnerIdAndIsDeletedFalse(
                        request.getCustomFieldId(), request.getEntityType(), request.getEntityId(), ownerId);
        
        CustomFieldValue customFieldValue;
        
        if (existingValue.isPresent()) {
            // Update existing value
            customFieldValue = existingValue.get();
            customFieldValue.setFieldValue(request.getFieldValue());
            customFieldValue.setMultiSelectValues(convertMultiSelectValuesToJson(request.getMultiSelectValues()));
        } else {
            // Create new value
            customFieldValue = CustomFieldValue.builder()
                    .customField(customField)
                    .entityType(request.getEntityType())
                    .entityId(request.getEntityId())
                    .fieldValue(request.getFieldValue())
                    .multiSelectValues(convertMultiSelectValuesToJson(request.getMultiSelectValues()))
                    .owner(owner)
                    .isDeleted(false)
                    .build();
        }
        
        CustomFieldValue savedValue = customFieldValueRepository.save(customFieldValue);
        log.info("Saved custom field value with id: {}", savedValue.getId());
        
        return CustomFieldValueDTO.fromEntity(savedValue);
    }

    @Override
    public List<CustomFieldValueDTO> saveBulkCustomFieldValues(CustomFieldValueRequestDTO request, Long ownerId) {
        log.info("Saving bulk custom field values for entity: {} {}", request.getEntityType(), request.getEntityId());
        
        List<CustomFieldValueDTO> savedValues = new ArrayList<>();
        
        if (request.getFieldValues() != null) {
            for (Map.Entry<String, String> entry : request.getFieldValues().entrySet()) {
                String fieldKey = entry.getKey();
                String fieldValue = entry.getValue();
                
                CustomField customField = customFieldRepository
                        .findByFieldKeyAndEntityTypeAndOwnerIdAndIsDeletedFalse(fieldKey, request.getEntityType(), ownerId)
                        .orElseThrow(() -> new RuntimeException("Custom field not found with key: " + fieldKey));
                
                CustomFieldValueRequestDTO valueRequest = CustomFieldValueRequestDTO.builder()
                        .customFieldId(customField.getId())
                        .entityType(request.getEntityType())
                        .entityId(request.getEntityId())
                        .fieldValue(fieldValue)
                        .build();
                
                CustomFieldValueDTO savedValue = saveCustomFieldValue(valueRequest, ownerId);
                savedValues.add(savedValue);
            }
        }
        
        log.info("Saved {} custom field values", savedValues.size());
        return savedValues;
    }

    @Override
    public CustomFieldValueDTO updateCustomFieldValue(Long id, CustomFieldValueRequestDTO request, Long ownerId) {
        log.info("Updating custom field value: {} for owner: {}", id, ownerId);
        
        CustomFieldValue customFieldValue = customFieldValueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom field value not found with id: " + id));
        
        if (!customFieldValue.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Access denied to custom field value");
        }
        
        customFieldValue.setFieldValue(request.getFieldValue());
        customFieldValue.setMultiSelectValues(convertMultiSelectValuesToJson(request.getMultiSelectValues()));
        
        CustomFieldValue updatedValue = customFieldValueRepository.save(customFieldValue);
        log.info("Updated custom field value with id: {}", updatedValue.getId());
        
        return CustomFieldValueDTO.fromEntity(updatedValue);
    }

    @Override
    public void deleteCustomFieldValue(Long id, Long ownerId) {
        log.info("Deleting custom field value: {} for owner: {}", id, ownerId);
        
        CustomFieldValue customFieldValue = customFieldValueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom field value not found with id: " + id));
        
        if (!customFieldValue.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Access denied to custom field value");
        }
        
        customFieldValue.setDeleted(true);
        customFieldValueRepository.save(customFieldValue);
        
        log.info("Soft deleted custom field value with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> searchEntitiesByCustomFieldValues(String entityType, String fieldKey, String searchTerm, Long ownerId) {
        log.info("Searching entities by custom field values: {} {} {}", entityType, fieldKey, searchTerm);
        
        return customFieldValueRepository.findEntityIdsByFieldKeyAndValue(fieldKey, entityType, ownerId, searchTerm);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getCustomFieldValuesAsKeyValuePairs(String entityType, Long entityId, Long ownerId) {
        log.info("Getting custom field values as key-value pairs for entity: {} {}", entityType, entityId);
        
        List<Map<String, String>> keyValuePairs = customFieldValueRepository.getFieldValuesAsKeyValuePairs(
                entityType, entityId, ownerId);
        
        return keyValuePairs.stream()
                .collect(Collectors.toMap(
                        map -> map.get("fieldKey"),
                        map -> map.get("fieldValue"),
                        (existing, replacement) -> existing // Handle duplicates
                ));
    }

    // ==================== UTILITY METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableFieldTypes() {
        return Arrays.stream(CustomField.FieldType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableEntityTypes() {
        // Return all available entity types in the system
        return Arrays.asList(
            "worker",        // For students, teachers, parents, managers, workers
            "user",          // User accounts
            "schoolclass",   // School classes
            "subject",       // Subjects
            "event",         // Events
            "exam",          // Exams
            "assignment",    // Assignments
            "attendance",    // Attendance records
            "fee",           // Fees
            "grade",         // Grades
            "library",       // Library books
            "course",        // Courses
            "announcement",  // Announcements
            "timetable",     // Timetable entries
            "idcard",        // ID Cards
            "transfercertificate" // Transfer Certificates
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateCustomFieldConfiguration(CustomFieldRequestDTO request) {
        Map<String, Object> validationResult = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validate field name
        if (request.getFieldName() == null || request.getFieldName().trim().isEmpty()) {
            errors.add("Field name is required");
        }
        
        // Validate field key
        if (request.getFieldKey() == null || request.getFieldKey().trim().isEmpty()) {
            errors.add("Field key is required");
        } else if (!request.getFieldKey().matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            errors.add("Field key must start with a letter and contain only letters, numbers, and underscores");
        }
        
        // Validate field type
        try {
            CustomField.FieldType.valueOf(request.getFieldType());
        } catch (IllegalArgumentException e) {
            errors.add("Invalid field type: " + request.getFieldType());
        }
        
        // Validate entity type
        List<String> availableEntityTypes = getAvailableEntityTypes();
        if (!availableEntityTypes.contains(request.getEntityType())) {
            warnings.add("Entity type '" + request.getEntityType() + "' is not in the standard list");
        }
        
        // Validate dropdown options for dropdown fields
        if ("DROPDOWN".equals(request.getFieldType()) || "MULTI_SELECT".equals(request.getFieldType())) {
            if (request.getDropdownOptions() == null || request.getDropdownOptions().isEmpty()) {
                errors.add("Dropdown options are required for dropdown and multi-select fields");
            }
        }
        
        validationResult.put("isValid", errors.isEmpty());
        validationResult.put("errors", errors);
        validationResult.put("warnings", warnings);
        
        return validationResult;
    }

    // ==================== HELPER METHODS ====================

    private String convertValidationRulesToJson(Map<String, Object> validationRules) {
        if (validationRules == null || validationRules.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(validationRules);
        } catch (JsonProcessingException e) {
            log.error("Error converting validation rules to JSON", e);
            return null;
        }
    }

    private String convertDropdownOptionsToJson(List<String> dropdownOptions) {
        if (dropdownOptions == null || dropdownOptions.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(dropdownOptions);
        } catch (JsonProcessingException e) {
            log.error("Error converting dropdown options to JSON", e);
            return null;
        }
    }

    private String convertMultiSelectValuesToJson(List<String> multiSelectValues) {
        if (multiSelectValues == null || multiSelectValues.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(multiSelectValues);
        } catch (JsonProcessingException e) {
            log.error("Error converting multi-select values to JSON", e);
            return null;
        }
    }
}
