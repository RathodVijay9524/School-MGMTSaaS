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
@Table(name = "subjects")
@EntityListeners(AuditingEntityListener.class)
public class Subject extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String subjectCode; // e.g., "MATH101", "ENG201"
    
    @Column(nullable = false)
    private String subjectName; // e.g., "Mathematics", "English Literature"
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private SubjectType type; // CORE, ELECTIVE, EXTRA_CURRICULAR
    
    private Integer credits; // Credit hours
    
    private Integer totalMarks; // Maximum marks for exams
    
    private Integer passingMarks; // Minimum marks to pass
    
    private String department; // e.g., "Science", "Arts", "Commerce"
    
    @Enumerated(EnumType.STRING)
    private SubjectStatus status; // ACTIVE, INACTIVE, ARCHIVED
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum SubjectType {
        CORE, ELECTIVE, EXTRA_CURRICULAR, OPTIONAL
    }

    public enum SubjectStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }
}

