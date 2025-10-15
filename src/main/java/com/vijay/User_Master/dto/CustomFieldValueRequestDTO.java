package com.vijay.User_Master.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldValueRequestDTO {
    
    private Long customFieldId;
    private String customFieldKey;
    private String entityType;
    private Long entityId;
    private String fieldValue;
    private List<String> multiSelectValues;
    
    // For bulk operations
    private Map<String, String> fieldValues; // fieldKey -> fieldValue mapping
}
