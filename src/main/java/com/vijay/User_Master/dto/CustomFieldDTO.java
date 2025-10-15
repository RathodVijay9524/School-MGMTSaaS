package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vijay.User_Master.entity.CustomField;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldDTO {
    
    private Long id;
    private String fieldName;
    private String fieldKey;
    private String description;
    private String fieldType;
    private String entityType;
    private boolean isRequired;
    private String placeholder;
    private String defaultValue;
    private Map<String, Object> validationRules;
    private List<String> dropdownOptions;
    private Integer displayOrder;
    private String fieldGroup;
    private boolean isVisible;
    private boolean isSearchable;
    private String helpText;
    private boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;
    
    // Constructor to convert from Entity to DTO
    public static CustomFieldDTO fromEntity(CustomField customField) {
        return CustomFieldDTO.builder()
                .id(customField.getId())
                .fieldName(customField.getFieldName())
                .fieldKey(customField.getFieldKey())
                .description(customField.getDescription())
                .fieldType(customField.getFieldType().name())
                .entityType(customField.getEntityType())
                .isRequired(customField.isRequired())
                .placeholder(customField.getPlaceholder())
                .defaultValue(customField.getDefaultValue())
                .validationRules(parseValidationRules(customField.getValidationRules()))
                .dropdownOptions(parseDropdownOptions(customField.getDropdownOptions()))
                .displayOrder(customField.getDisplayOrder())
                .fieldGroup(customField.getFieldGroup())
                .isVisible(customField.isVisible())
                .isSearchable(customField.isSearchable())
                .helpText(customField.getHelpText())
                .isActive(customField.isActive())
                .createdOn(convertToLocalDateTime(customField.getCreatedOn()))
                .updatedOn(convertToLocalDateTime(customField.getUpdatedOn()))
                .build();
    }
    
    // Helper method to parse validation rules JSON
    private static Map<String, Object> parseValidationRules(String validationRulesJson) {
        // This would typically use a JSON parser like Jackson ObjectMapper
        // For now, returning null - implement based on your JSON parsing needs
        return null;
    }
    
    // Helper method to parse dropdown options JSON
    private static List<String> parseDropdownOptions(String dropdownOptionsJson) {
        // This would typically use a JSON parser like Jackson ObjectMapper
        // For now, returning null - implement based on your JSON parsing needs
        return null;
    }
    
    // Helper method to convert Date to LocalDateTime
    private static LocalDateTime convertToLocalDateTime(java.util.Date date) {
        if (date == null) return null;
        return new java.sql.Timestamp(date.getTime()).toLocalDateTime();
    }
}
