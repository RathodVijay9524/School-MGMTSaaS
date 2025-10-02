package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "attendance", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "attendance_date", "session"}))
@EntityListeners(AuditingEntityListener.class)
public class Attendance extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student; // Worker with ROLE_STUDENT
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    @Column(nullable = false)
    private LocalDate attendanceDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status; // PRESENT, ABSENT, LATE, HALF_DAY, EXCUSED
    
    @Enumerated(EnumType.STRING)
    private AttendanceSession session; // FULL_DAY, MORNING, AFTERNOON
    
    private LocalTime checkInTime;
    
    private LocalTime checkOutTime;
    
    @Column(length = 500)
    private String remarks; // Reason for absence, late arrival, etc.
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by_teacher_id")
    private Worker markedBy; // Worker with ROLE_TEACHER
    
    private boolean isVerified; // Parent/Guardian verification
    
    @Column(length = 500)
    private String parentNote;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, HALF_DAY, EXCUSED, MEDICAL_LEAVE, SICK_LEAVE, HOLIDAY
    }

    public enum AttendanceSession {
        FULL_DAY, MORNING, AFTERNOON
    }
}

