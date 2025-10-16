package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    
    // ========== NEW FIELDS - PARENT/GUARDIAN RELATIONSHIP ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Worker parent; // Guardian/Parent who pays the fee
    
    // ========== NEW FIELDS - INSTALLMENT SUPPORT ==========
    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @lombok.Builder.Default
    private List<FeeInstallment> installments = new ArrayList<>();
    
    private Integer totalInstallments;  // Total number of installments planned
    
    @lombok.Builder.Default
    private Integer paidInstallments = 0;   // Number of installments paid so far
    
    private LocalDate nextInstallmentDueDate;  // Next payment due date
    
    private Double installmentAmount;   // Amount per installment (if equal installments)
    
    @lombok.Builder.Default
    private boolean isInstallmentAllowed = false;  // Whether payment plan is enabled
    
    // Payment Plan Type
    @Enumerated(EnumType.STRING)
    @lombok.Builder.Default
    private PaymentPlanType paymentPlanType = PaymentPlanType.FULL_PAYMENT;
    
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
    
    public enum PaymentPlanType {
        FULL_PAYMENT,      // Pay all at once (no installments)
        MONTHLY,           // Monthly installments
        QUARTERLY,         // Quarterly installments (every 3 months)
        SEMI_ANNUAL,       // Half-yearly installments (every 6 months)
        ANNUAL,            // Annual installments
        CUSTOM             // Custom payment plan
    }
}

