package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * StudentTransport entity for Transport Management
 * Represents student transportation assignments with pickup/drop details
 */
@Entity
@Table(name = "student_transports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StudentTransport extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transport_id", unique = true, nullable = false)
    private String transportId; // Auto-generated transport ID

    // Student Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Worker student;

    // Transport Assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_stop_id", nullable = false)
    private RouteStop pickupStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drop_stop_id", nullable = false)
    private RouteStop dropStop;

    // Timing Information
    @Column(name = "pickup_time")
    private java.sql.Time pickupTime;

    @Column(name = "drop_time")
    private java.sql.Time dropTime;

    @Column(name = "return_pickup_time")
    private java.sql.Time returnPickupTime;

    @Column(name = "return_drop_time")
    private java.sql.Time returnDropTime;

    // Fee Information
    @Column(name = "monthly_fee")
    private Double monthlyFee;

    @Column(name = "annual_fee")
    private Double annualFee;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "final_fee")
    private Double finalFee;

    @Column(name = "payment_status")
    private String paymentStatus; // PENDING, PAID, OVERDUE, PARTIAL

    @Column(name = "last_payment_date")
    private Date lastPaymentDate;

    @Column(name = "next_payment_due")
    private Date nextPaymentDue;

    @Column(name = "outstanding_amount")
    private Double outstandingAmount;

    // Assignment Details
    @Column(name = "assignment_date", nullable = false)
    private Date assignmentDate;

    @Column(name = "effective_from")
    private Date effectiveFrom;

    @Column(name = "effective_until")
    private Date effectiveUntil;

    @Column(name = "assignment_status")
    private String assignmentStatus; // ACTIVE, INACTIVE, SUSPENDED, TERMINATED

    @Column(name = "is_pickup_enabled")
    @Builder.Default
    private Boolean isPickupEnabled = true;

    @Column(name = "is_drop_enabled")
    @Builder.Default
    private Boolean isDropEnabled = true;

    @Column(name = "is_return_pickup_enabled")
    @Builder.Default
    private Boolean isReturnPickupEnabled = true;

    @Column(name = "is_return_drop_enabled")
    @Builder.Default
    private Boolean isReturnDropEnabled = true;

    // Parent/Guardian Information
    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "parent_email")
    private String parentEmail;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relation")
    private String emergencyContactRelation;

    // Additional Information
    @Column(name = "medical_conditions")
    private String medicalConditions;

    @Column(name = "allergies")
    private String allergies;

    @Column(name = "medications")
    private String medications;

    @Column(name = "special_instructions")
    private String specialInstructions;

    @Column(name = "pickup_contact_person")
    private String pickupContactPerson;

    @Column(name = "pickup_contact_phone")
    private String pickupContactPhone;

    @Column(name = "drop_contact_person")
    private String dropContactPerson;

    @Column(name = "drop_contact_phone")
    private String dropContactPhone;

    // Status Tracking
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "attendance_rate")
    private Double attendanceRate; // Percentage of days student used transport

    @Column(name = "last_attendance_date")
    private Date lastAttendanceDate;

    @Column(name = "total_trips_taken")
    @Builder.Default
    private Integer totalTripsTaken = 0;

    @Column(name = "missed_trips_count")
    @Builder.Default
    private Integer missedTripsCount = 0;

    @Column(name = "late_pickups_count")
    @Builder.Default
    private Integer latePickupsCount = 0;

    @Column(name = "incidents_count")
    @Builder.Default
    private Integer incidentsCount = 0;

    @Column(name = "complaints_count")
    @Builder.Default
    private Integer complaintsCount = 0;

    @Column(name = "notes")
    private String notes;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public boolean isCurrentlyActive() {
        return isActive && assignmentStatus.equals("ACTIVE") && 
               (effectiveUntil == null || effectiveUntil.after(new Date(System.currentTimeMillis())));
    }

    public boolean isPaymentOverdue() {
        if (nextPaymentDue == null) return false;
        return nextPaymentDue.before(new Date(System.currentTimeMillis()));
    }

    public void calculateFinalFee() {
        if (monthlyFee != null) {
            double fee = monthlyFee;
            if (discountPercentage != null && discountPercentage > 0) {
                fee = fee * (1 - discountPercentage / 100);
            }
            this.finalFee = fee;
        }
    }

    public void calculateOutstandingAmount() {
        // This would typically be calculated based on payment history
        // For now, simple calculation
        if (finalFee != null && paymentStatus != null) {
            switch (paymentStatus) {
                case "PENDING":
                    this.outstandingAmount = finalFee;
                    break;
                case "PARTIAL":
                    // Would need payment history to calculate accurately
                    this.outstandingAmount = finalFee * 0.5; // Simplified
                    break;
                case "PAID":
                    this.outstandingAmount = 0.0;
                    break;
                case "OVERDUE":
                    this.outstandingAmount = finalFee * 1.1; // Include late fee
                    break;
                default:
                    this.outstandingAmount = finalFee;
            }
        }
    }

    public boolean canUseTransport() {
        return isCurrentlyActive() && 
               !isPaymentOverdue() && 
               bus.getIsActive() && 
               route.getIsActive() && 
               driver.isEligibleToDrive();
    }

    public String getTransportStatus() {
        if (!isActive) return "INACTIVE";
        if (isPaymentOverdue()) return "PAYMENT_OVERDUE";
        if (!bus.getIsActive()) return "BUS_INACTIVE";
        if (!route.getIsActive()) return "ROUTE_INACTIVE";
        if (!driver.isEligibleToDrive()) return "DRIVER_UNAVAILABLE";
        return "ACTIVE";
    }

    public double calculateAttendancePercentage() {
        if (totalTripsTaken == null || missedTripsCount == null) return 0.0;
        int totalPossibleTrips = totalTripsTaken + missedTripsCount;
        if (totalPossibleTrips == 0) return 0.0;
        return (double) totalTripsTaken / totalPossibleTrips * 100;
    }

    public String getFullTransportDetails() {
        return String.format("Transport ID: %s, Bus: %s, Route: %s, Driver: %s", 
                           transportId, 
                           bus.getBusNumber(), 
                           route.getRouteName(), 
                           driver.getFullName());
    }
}
