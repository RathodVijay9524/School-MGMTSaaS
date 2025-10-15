package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "custom_fields")
@EntityListeners(AuditingEntityListener.class)
public class CustomField extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fieldName; // e.g., "Bus Route Number", "Medical Allergies"
    
    @Column(nullable = false)
    private String fieldKey; // e.g., "bus_route_number", "medical_allergies"
    
    @Column(length = 1000)
    private String description; // Field description/help text
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType fieldType; // TEXT, NUMBER, DATE, DROPDOWN, BOOLEAN, EMAIL
    
    @Column(nullable = false)
    private String entityType; // 'student', 'teacher', 'class', 'subject', etc.
    
    @Column(name = "required")
    @Builder.Default
    private boolean isRequired = false;
    
    @Column(length = 100)
    private String placeholder; // Placeholder text for input fields
    
    @Column(length = 100)
    private String defaultValue; // Default value for the field
    
    // Validation Rules (stored as JSON)
    @Column(columnDefinition = "TEXT")
    private String validationRules; // JSON string with validation rules
    
    // For dropdown fields - options stored as JSON array
    @Column(columnDefinition = "TEXT")
    private String dropdownOptions; // JSON array of dropdown options
    
    // Field display order
    @Builder.Default
    private Integer displayOrder = 0;
    
    // Field grouping/category
    private String fieldGroup; // e.g., "Personal Info", "Academic", "Medical"
    
    // Whether field is visible in forms
    @Column(name = "visible")
    @Builder.Default
    private boolean isVisible = true;
    
    // Whether field can be searched/filtered
    @Column(name = "searchable")
    @Builder.Default
    private boolean isSearchable = false;
    
    // Field help text/tooltip
    @Column(length = 500)
    private String helpText;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @Builder.Default
    private boolean isDeleted = false;
    
    // Active status
    @Builder.Default
    private boolean isActive = true;

    // Enum for field types
    public enum FieldType {
        TEXT,           // Single line text input
        TEXTAREA,       // Multi-line text input
        NUMBER,         // Numeric input
        EMAIL,          // Email input with validation
        PHONE,          // Phone number input
        DATE,           // Date picker
        DATETIME,       // Date and time picker
        DROPDOWN,       // Select dropdown
        MULTI_SELECT,   // Multiple selection dropdown
        BOOLEAN,        // Checkbox
        RADIO,          // Radio buttons
        FILE,           // File upload
        URL,            // URL input
        CURRENCY,       // Currency input
        PERCENTAGE,     // Percentage input
        PASSWORD,       // Password input
        RATING          // Star rating
    }

    // Relationships
    @OneToMany(mappedBy = "customField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CustomFieldValue> customFieldValues;
}
