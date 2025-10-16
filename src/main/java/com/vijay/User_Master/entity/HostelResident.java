package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "hostel_residents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HostelResident extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resident_id", unique = true, nullable = false)
    private String residentId;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "expected_check_out_date")
    private LocalDate expectedCheckOutDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ResidentStatus status = ResidentStatus.ACTIVE;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_number")
    private String emergencyContactNumber;

    @Column(name = "emergency_contact_relation")
    private String emergencyContactRelation;

    @Column(name = "medical_conditions", columnDefinition = "TEXT")
    private String medicalConditions;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "special_dietary_requirements", columnDefinition = "TEXT")
    private String specialDietaryRequirements;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "guardian_contact")
    private String guardianContact;

    @Column(name = "guardian_address", columnDefinition = "TEXT")
    private String guardianAddress;

    @Column(name = "monthly_fees")
    private Double monthlyFees;

    @Column(name = "security_deposit")
    private Double securityDeposit;

    @Column(name = "security_deposit_paid")
    @Builder.Default
    private Boolean securityDepositPaid = false;

    @Column(name = "security_deposit_paid_date")
    private LocalDate securityDepositPaidDate;

    @Column(name = "mess_fees")
    private Double messFees;

    @Column(name = "mess_subscribed")
    @Builder.Default
    private Boolean messSubscribed = false;

    @Column(name = "laundry_subscribed")
    @Builder.Default
    private Boolean laundrySubscribed = false;

    @Column(name = "wifi_subscribed")
    @Builder.Default
    private Boolean wifiSubscribed = false;

    @Column(name = "gym_subscribed")
    @Builder.Default
    private Boolean gymSubscribed = false;

    @Column(name = "library_access")
    @Builder.Default
    private Boolean libraryAccess = false;

    @Column(name = "visitor_policy_accepted")
    @Builder.Default
    private Boolean visitorPolicyAccepted = false;

    @Column(name = "hostel_rules_accepted")
    @Builder.Default
    private Boolean hostelRulesAccepted = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "last_fee_payment_date")
    private LocalDate lastFeePaymentDate;

    @Column(name = "next_fee_due_date")
    private LocalDate nextFeeDueDate;

    @Column(name = "outstanding_fees")
    @Builder.Default
    private Double outstandingFees = 0.0;

    @Column(name = "late_fee_charges")
    @Builder.Default
    private Double lateFeeCharges = 0.0;

    @Column(name = "penalty_charges")
    @Builder.Default
    private Double penaltyCharges = 0.0;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public boolean isCurrentlyResiding() {
        return isActive && status == ResidentStatus.ACTIVE && checkOutDate == null;
    }

    public boolean hasOverdueFees() {
        return nextFeeDueDate != null && nextFeeDueDate.isBefore(LocalDate.now()) && outstandingFees > 0;
    }

    public void checkOut(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
        this.isActive = false;
        this.status = ResidentStatus.CHECKED_OUT;
        if (bed != null) {
            bed.markAsAvailable();
        }
    }

    public void calculateTotalFees() {
        double total = monthlyFees;
        if (messSubscribed && messFees != null) {
            total += messFees;
        }
        if (laundrySubscribed) {
            total += 500.0; // Default laundry fee
        }
        if (wifiSubscribed) {
            total += 300.0; // Default WiFi fee
        }
        if (gymSubscribed) {
            total += 1000.0; // Default gym fee
        }
        this.outstandingFees = total;
    }

    public enum ResidentStatus {
        ACTIVE,
        CHECKED_OUT,
        SUSPENDED,
        TRANSFERRED,
        TERMINATED
    }
}
