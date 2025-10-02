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
@Table(name = "school_classes")
@EntityListeners(AuditingEntityListener.class)
public class SchoolClass extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String className; // e.g., "Class 10", "Grade 5", "Year 1"
    
    private String section; // e.g., "A", "B", "C"
    
    @Column(nullable = false)
    private Integer classLevel; // 1, 2, 3... 12 for ordering
    
    private Integer maxStudents; // Maximum capacity
    private Integer capacity; // Alternative capacity field
    
    private Integer currentStudents; // Current enrolled students
    
    private String roomNumber;
    
    @Column(length = 1000)
    private String description;
    
    // Class Teacher
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private Worker classTeacher; // Worker with ROLE_TEACHER
    
    // Academic Year
    private String academicYear; // e.g., "2024-2025"
    
    @Enumerated(EnumType.STRING)
    private ClassStatus status; // ACTIVE, ARCHIVED, PLANNED
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;
    
    // Active status
    @lombok.Builder.Default
    private boolean isActive = true;

    // Relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "class_subjects",
        joinColumns = @JoinColumn(name = "class_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects;

    public enum ClassStatus {
        ACTIVE, ARCHIVED, PLANNED
    }
}

