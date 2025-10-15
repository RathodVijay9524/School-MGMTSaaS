package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldValueDTO {
    
    private Long id;
    private Long customFieldId;
    private String customFieldKey;
    private String customFieldName;
    private String fieldType;
    private String entityType;
    private Long entityId;
    private String fieldValue;
    private String multiSelectValues;
    private boolean isRequired;
    private String placeholder;
    private String helpText;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;
    
    // Constructor to convert from Entity to DTO
    public static CustomFieldValueDTO fromEntity(com.vijay.User_Master.entity.CustomFieldValue customFieldValue) {
        return CustomFieldValueDTO.builder()
                .id(customFieldValue.getId())
                .customFieldId(customFieldValue.getCustomField().getId())
                .customFieldKey(customFieldValue.getCustomField().getFieldKey())
                .customFieldName(customFieldValue.getCustomField().getFieldName())
                .fieldType(customFieldValue.getCustomField().getFieldType().name())
                .entityType(customFieldValue.getEntityType())
                .entityId(customFieldValue.getEntityId())
                .fieldValue(customFieldValue.getFieldValue())
                .multiSelectValues(customFieldValue.getMultiSelectValues())
                .isRequired(customFieldValue.getCustomField().isRequired())
                .placeholder(customFieldValue.getCustomField().getPlaceholder())
                .helpText(customFieldValue.getCustomField().getHelpText())
                .createdOn(convertToLocalDateTime(customFieldValue.getCreatedOn()))
                .updatedOn(convertToLocalDateTime(customFieldValue.getUpdatedOn()))
                .build();
    }
    
    // Helper method to convert Date to LocalDateTime
    private static LocalDateTime convertToLocalDateTime(java.util.Date date) {
        if (date == null) return null;
        return new java.sql.Timestamp(date.getTime()).toLocalDateTime();
    }
}
