package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "courses")
@EntityListeners(AuditingEntityListener.class)
public class Course extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String courseCode; // e.g., "CS101", "BIO202"
    
    @Column(nullable = false)
    private String courseName;
    
    @Column(length = 2000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Worker teacher; // Worker with ROLE_TEACHER
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String semester; // e.g., "Fall 2024", "Spring 2025"
    
    private Integer credits;
    
    private Integer maxStudents;
    
    private Integer enrolledStudents;
    
    @Enumerated(EnumType.STRING)
    private CourseStatus status; // PLANNED, ONGOING, COMPLETED, CANCELLED
    
    @Column(length = 1000)
    private String syllabus; // URL or text
    
    private String schedule; // e.g., "Mon, Wed, Fri 10:00-11:00 AM"
    
    private String classroom;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum CourseStatus {
        PLANNED, ONGOING, COMPLETED, CANCELLED
    }
}

