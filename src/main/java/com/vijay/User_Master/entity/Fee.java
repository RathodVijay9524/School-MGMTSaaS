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
@Table(name = "fees")
@EntityListeners(AuditingEntityListener.class)
public class Fee extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Optimistic Locking - prevents concurrent update conflicts
    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student; // Worker with ROLE_STUDENT
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType; // TUITION, ADMISSION, EXAM, TRANSPORT, LIBRARY, SPORTS, MISCELLANEOUS
    
    @Column(nullable = false)
    private String feeCategory; // "Annual Fees", "Monthly Fees", "One-time"
    
    private Double totalAmount;
    
    private Double paidAmount;
    
    private Double discountAmount;
    
    private Double balanceAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus; // PENDING, PARTIAL, PAID, OVERDUE, CANCELLED
    
    private LocalDate dueDate;
    
    private LocalDate paymentDate;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // CASH, CARD, ONLINE, CHEQUE, BANK_TRANSFER
    
    private String transactionId;
    
    private String receiptNumber;
    
    private String academicYear; // e.g., "2024-2025"
    
    private String semester; // e.g., "Fall 2024"
    
    @Column(length = 1000)
    private String remarks;
    
    @Column(length = 500)
    private String lateFeeReason;
    
    private Double lateFeeAmount;
    
    private boolean isWaived; // Fee waiver/scholarship
    
    private String waiverReason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by_user_id")
    private User collectedBy;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum FeeType {
        TUITION, ADMISSION, EXAM, TRANSPORT, LIBRARY, SPORTS, LABORATORY, COMPUTER, 
        HOSTEL, MISCELLANEOUS, FINE, LATE_FEE, SECURITY_DEPOSIT
    }

    public enum PaymentStatus {
        PENDING, PARTIAL, PAID, OVERDUE, CANCELLED, REFUNDED
    }

    public enum PaymentMethod {
        CASH, DEBIT_CARD, CREDIT_CARD, ONLINE, CHEQUE, BANK_TRANSFER, UPI, WALLET
    }
}

