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
@Table(name = "id_cards")
@EntityListeners(AuditingEntityListener.class)
public class IDCard extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Worker student; // Worker with ROLE_STUDENT
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Worker teacher; // Worker with ROLE_TEACHER
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType; // STUDENT_ID, TEACHER_ID, STAFF_ID
    
    @Column(unique = true, nullable = false)
    private String cardNumber; // ID-STU-2024-001 or ID-TCH-2024-001
    
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @Column(nullable = false)
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    private CardStatus status; // ACTIVE, EXPIRED, LOST, DAMAGED, CANCELLED, REPLACED
    
    // Card Design Information
    private String photoUrl; // Student/Teacher photo
    
    private String barcodeData; // For scanning
    
    private String qrCodeData; // QR code with student/teacher info
    
    // Student-specific (if cardType = STUDENT_ID)
    private String studentClass;
    
    private String section;
    
    private Integer rollNumber;
    
    private String bloodGroup;
    
    private String emergencyContactName;
    
    private String emergencyContactPhone;
    
    // Teacher-specific (if cardType = TEACHER_ID)
    private String designation;
    
    private String department;
    
    private String employeeId;
    
    // Common Information
    private String address;
    
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    
    // Replacement Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_by_card_id")
    private IDCard replacedBy; // If lost/damaged and reissued
    
    private LocalDate replacementDate;
    
    private String replacementReason;
    
    private Double replacementFee;
    
    // Issued By
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_user_id")
    private User issuedBy;
    
    @Column(length = 500)
    private String remarks;
    
    // PDF/Image URLs
    private String frontSideImageUrl; // Front side design
    
    private String backSideImageUrl; // Back side design
    
    private String pdfUrl; // Printable PDF
    
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum CardType {
        STUDENT_ID,
        TEACHER_ID,
        STAFF_ID
    }

    public enum CardStatus {
        ACTIVE,
        EXPIRED,
        LOST,
        DAMAGED,
        CANCELLED,
        REPLACED,
        PENDING
    }
}

