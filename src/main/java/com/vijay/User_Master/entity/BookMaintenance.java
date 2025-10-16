package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * BookMaintenance entity for tracking library book maintenance history
 * Supports detailed maintenance records, repair tracking, and cost management
 */
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "book_maintenance")
@EntityListeners(AuditingEntityListener.class)
public class BookMaintenance extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Library book;

    @Column(nullable = false)
    private LocalDate maintenanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType; // REPAIR, REBINDING, CLEANING, INSPECTION, PAGE_REPLACEMENT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @lombok.Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(length = 2000)
    private String description; // Detailed description of maintenance work

    @Column(length = 1000)
    private String issuesFound; // Problems identified during maintenance

    @Column(length = 1000)
    private String workPerformed; // Actions taken during maintenance

    @Enumerated(EnumType.STRING)
    private Library.BookCondition conditionBefore; // Condition before maintenance

    @Enumerated(EnumType.STRING)
    private Library.BookCondition conditionAfter; // Condition after maintenance

    private Double maintenanceCost; // Cost of maintenance/repair

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private Worker performedBy; // Staff who performed maintenance

    private LocalDate completionDate; // When maintenance was completed

    private Integer estimatedDays; // Estimated days for completion

    private Integer actualDays; // Actual days taken

    @Column(length = 500)
    private String vendor; // External vendor if applicable

    private String invoiceNumber; // Invoice number for external maintenance

    @lombok.Builder.Default
    private boolean isWarrantyRepair = false; // Whether repair was under warranty

    private LocalDate nextScheduledMaintenance; // When next maintenance is recommended

    @Column(length = 1000)
    private String recommendations; // Recommendations for future care

    @Column(length = 500)
    private String remarks;

    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum MaintenanceType {
        REPAIR,              // General repair work
        REBINDING,           // Book rebinding/cover repair
        CLEANING,            // Deep cleaning
        INSPECTION,          // Routine inspection
        PAGE_REPLACEMENT,    // Replace damaged pages
        SPINE_REPAIR,        // Fix book spine
        COVER_RESTORATION,   // Restore book cover
        PEST_TREATMENT,      // Treatment for pests/mold
        LAMINATION,          // Laminate pages
        DIGITIZATION         // Convert to digital format
    }

    public enum MaintenanceStatus {
        SCHEDULED,      // Maintenance scheduled
        IN_PROGRESS,    // Currently being maintained
        COMPLETED,      // Maintenance completed
        CANCELLED,      // Maintenance cancelled
        PENDING_APPROVAL, // Waiting for approval
        ON_HOLD         // Temporarily on hold
    }

    /**
     * Calculate actual days taken for maintenance
     */
    public Integer calculateActualDays() {
        if (maintenanceDate != null && completionDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(maintenanceDate, completionDate);
        }
        return null;
    }

    /**
     * Check if maintenance is overdue
     */
    public boolean isOverdue() {
        if (status == MaintenanceStatus.COMPLETED || status == MaintenanceStatus.CANCELLED) {
            return false;
        }
        if (estimatedDays != null && maintenanceDate != null) {
            LocalDate expectedCompletion = maintenanceDate.plusDays(estimatedDays);
            return LocalDate.now().isAfter(expectedCompletion);
        }
        return false;
    }

    /**
     * Mark maintenance as completed
     */
    public void markAsCompleted(Library.BookCondition newCondition) {
        this.status = MaintenanceStatus.COMPLETED;
        this.completionDate = LocalDate.now();
        this.conditionAfter = newCondition;
        this.actualDays = calculateActualDays();
    }
}

