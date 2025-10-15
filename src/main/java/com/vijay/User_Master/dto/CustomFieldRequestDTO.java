package com.vijay.User_Master.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldRequestDTO {
    
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
}
