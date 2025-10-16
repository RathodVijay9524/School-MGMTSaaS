package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing individual fee installments for payment plans
 */
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "fee_installments", indexes = {
    @Index(name = "idx_fee_installment_fee_id", columnList = "fee_id"),
    @Index(name = "idx_fee_installment_due_date", columnList = "due_date"),
    @Index(name = "idx_fee_installment_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class FeeInstallment extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Optimistic Locking
    @Version
    private Long version;
    
    // Parent Fee
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;
    
    // Installment Details
    @Column(nullable = false)
    private Integer installmentNumber;  // 1, 2, 3, etc.
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    private LocalDate paidDate;
    
    private LocalDateTime paidAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @lombok.Builder.Default
    private InstallmentStatus status = InstallmentStatus.PENDING;
    
    // Payment Details
    @Enumerated(EnumType.STRING)
    private Fee.PaymentMethod paymentMethod;
    
    private String transactionId;
    
    private String receiptNumber;
    
    @Column(length = 1000)
    private String remarks;
    
    // Late Fee Handling
    private Double lateFeeAmount;
    
    private Integer daysOverdue;
    
    private boolean isLatePayment;
    
    // Waiver/Discount
    private Double discountAmount;
    
    private boolean isWaived;
    
    private String waiverReason;
    
    // Collection Details
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
    
    /**
     * Calculate if installment is overdue
     */
    public boolean isOverdue() {
        return status == InstallmentStatus.PENDING && 
               dueDate != null && 
               dueDate.isBefore(LocalDate.now());
    }
    
    /**
     * Calculate days overdue
     */
    public int calculateDaysOverdue() {
        if (isOverdue()) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }
    
    /**
     * Mark installment as paid
     */
    public void markAsPaid(Fee.PaymentMethod method, String transactionId, User collectedBy) {
        this.status = InstallmentStatus.PAID;
        this.paidDate = LocalDate.now();
        this.paidAt = LocalDateTime.now();
        this.paymentMethod = method;
        this.transactionId = transactionId;
        this.collectedBy = collectedBy;
        
        // Calculate late fee if overdue
        if (isLatePayment) {
            this.daysOverdue = calculateDaysOverdue();
        }
    }

    public enum InstallmentStatus {
        PENDING,      // Not yet paid
        PAID,         // Fully paid
        OVERDUE,      // Past due date and not paid
        PARTIALLY_PAID, // Partial payment received
        WAIVED,       // Fee waived/exempted
        CANCELLED     // Installment cancelled
    }
}

