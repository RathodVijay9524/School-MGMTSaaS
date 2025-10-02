package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "transfer_certificates")
@EntityListeners(AuditingEntityListener.class)
public class TransferCertificate extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student; // Worker with ROLE_STUDENT
    
    @Column(unique = true, nullable = false)
    private String tcNumber; // TC/2024/001
    
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @Column(nullable = false)
    private LocalDate lastAttendanceDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReasonForLeaving reasonForLeaving;
    
    @Column(length = 1000)
    private String reasonDetails;
    
    // Academic Information at time of leaving
    private String lastClassStudied;
    
    private String lastClassPassed;
    
    private LocalDate dateOfPromotion;
    
    private String academicYearOfLeaving;
    
    private String workingDays;
    
    private String presentDays;
    
    private String attendancePercentage;
    
    private String overallGPA;
    
    private String overallGrade;
    
    // Conduct & Character
    @Enumerated(EnumType.STRING)
    private ConductRating conduct; // EXCELLENT, GOOD, SATISFACTORY, NEEDS_IMPROVEMENT
    
    @Column(length = 1000)
    private String conductRemarks;
    
    @Column(length = 1000)
    private String characterRemarks;
    
    // Fee Clearance
    private boolean feeCleared;
    
    private Double pendingFeeAmount;
    
    @Column(length = 500)
    private String feeRemarks;
    
    // Library Clearance
    private boolean libraryCleared;
    
    private Integer pendingBooks;
    
    private Double libraryFine;
    
    // Remarks
    @Column(length = 1000)
    private String generalRemarks;
    
    @Column(length = 1000)
    private String achievements;
    
    @Column(length = 1000)
    private String extraCurricularActivities;
    
    // Issued By
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_user_id")
    private User issuedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;
    
    @Enumerated(EnumType.STRING)
    private TCStatus status; // DRAFT, PENDING_APPROVAL, APPROVED, ISSUED, CANCELLED
    
    private LocalDate approvalDate;
    
    private String digitalSignature; // Base64 or URL
    
    private String schoolSeal; // Base64 or URL
    
    private String pdfUrl; // URL to generated PDF
    
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum ReasonForLeaving {
        TRANSFER_TO_ANOTHER_SCHOOL,
        FAMILY_RELOCATION,
        FINANCIAL_REASONS,
        HEALTH_REASONS,
        ACADEMIC_REASONS,
        GRADUATION,
        PARENT_CHOICE,
        DISCIPLINARY,
        OTHER
    }

    public enum ConductRating {
        EXCELLENT,
        VERY_GOOD,
        GOOD,
        SATISFACTORY,
        NEEDS_IMPROVEMENT
    }

    public enum TCStatus {
        DRAFT,
        PENDING_APPROVAL,
        APPROVED,
        ISSUED,
        CANCELLED
    }
}

