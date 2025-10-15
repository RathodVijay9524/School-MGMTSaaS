package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "custom_field_values")
@EntityListeners(AuditingEntityListener.class)
public class CustomFieldValue extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the custom field definition
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id", nullable = false)
    private CustomField customField;
    
    // The entity this value belongs to (student, teacher, class, etc.)
    @Column(nullable = false)
    private String entityType; // 'student', 'teacher', 'class', 'subject', etc.
    
    // The ID of the specific entity instance
    @Column(nullable = false)
    private Long entityId; // student_id, teacher_id, class_id, etc.
    
    // The actual field value (stored as text for flexibility)
    @Column(columnDefinition = "TEXT")
    private String fieldValue;
    
    // For multi-select fields, store as JSON array
    @Column(columnDefinition = "TEXT")
    private String multiSelectValues; // JSON array for multi-select fields
    
    // Business Owner (Multi-tenancy) - inherited from CustomField
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @Builder.Default
    private boolean isDeleted = false;

    // Unique constraint to prevent duplicate values for same field and entity
    @Table(uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"custom_field_id", "entity_type", "entity_id"},
            name = "uk_custom_field_entity"
        )
    })
    public static class CustomFieldValueTable {}
}
