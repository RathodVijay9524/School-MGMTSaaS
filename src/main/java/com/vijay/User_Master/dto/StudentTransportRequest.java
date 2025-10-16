package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * DTO for StudentTransport creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTransportRequest {

    // Student Information
    @NotNull(message = "Student ID is required")
    private Long studentId;

    // Transport Assignment
    @NotNull(message = "Bus ID is required")
    private Long busId;

    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Pickup stop ID is required")
    private Long pickupStopId;

    @NotNull(message = "Drop stop ID is required")
    private Long dropStopId;

    // Timing Information
    private Time pickupTime;
    private Time dropTime;
    private Time returnPickupTime;
    private Time returnDropTime;

    // Fee Information
    @Min(value = 0, message = "Monthly fee cannot be negative")
    private Double monthlyFee;

    @Min(value = 0, message = "Annual fee cannot be negative")
    private Double annualFee;

    @Min(value = 0, message = "Discount percentage cannot be negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100%")
    private Double discountPercentage;

    @Min(value = 0, message = "Final fee cannot be negative")
    private Double finalFee;

    @Size(max = 20, message = "Payment status cannot exceed 20 characters")
    private String paymentStatus;

    private Date lastPaymentDate;
    private Date nextPaymentDue;

    @Min(value = 0, message = "Outstanding amount cannot be negative")
    private Double outstandingAmount;

    // Assignment Details
    @NotNull(message = "Assignment date is required")
    private Date assignmentDate;

    private Date effectiveFrom;

    private Date effectiveUntil;

    @Size(max = 20, message = "Assignment status cannot exceed 20 characters")
    private String assignmentStatus;

    @Builder.Default
    private Boolean isPickupEnabled = true;

    @Builder.Default
    private Boolean isDropEnabled = true;

    @Builder.Default
    private Boolean isReturnPickupEnabled = true;

    @Builder.Default
    private Boolean isReturnDropEnabled = true;

    // Parent/Guardian Information
    @Size(max = 100, message = "Parent name cannot exceed 100 characters")
    private String parentName;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid parent phone number format")
    private String parentPhone;

    @Email(message = "Invalid parent email format")
    @Size(max = 100, message = "Parent email cannot exceed 100 characters")
    private String parentEmail;

    @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid emergency contact phone format")
    private String emergencyContactPhone;

    @Size(max = 50, message = "Emergency contact relation cannot exceed 50 characters")
    private String emergencyContactRelation;

    // Additional Information
    @Size(max = 500, message = "Medical conditions cannot exceed 500 characters")
    private String medicalConditions;

    @Size(max = 500, message = "Allergies cannot exceed 500 characters")
    private String allergies;

    @Size(max = 500, message = "Medications cannot exceed 500 characters")
    private String medications;

    @Size(max = 1000, message = "Special instructions cannot exceed 1000 characters")
    private String specialInstructions;

    @Size(max = 100, message = "Pickup contact person cannot exceed 100 characters")
    private String pickupContactPerson;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid pickup contact phone format")
    private String pickupContactPhone;

    @Size(max = 100, message = "Drop contact person cannot exceed 100 characters")
    private String dropContactPerson;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid drop contact phone format")
    private String dropContactPhone;

    // Status Tracking
    @Builder.Default
    private Boolean isActive = true;

    @Min(value = 0, message = "Attendance rate cannot be negative")
    @Max(value = 100, message = "Attendance rate cannot exceed 100%")
    private Double attendanceRate;

    private Date lastAttendanceDate;

    @Min(value = 0, message = "Total trips taken cannot be negative")
    private Integer totalTripsTaken;

    @Min(value = 0, message = "Missed trips count cannot be negative")
    private Integer missedTripsCount;

    @Min(value = 0, message = "Late pickups count cannot be negative")
    private Integer latePickupsCount;

    @Min(value = 0, message = "Incidents count cannot be negative")
    private Integer incidentsCount;

    @Min(value = 0, message = "Complaints count cannot be negative")
    private Integer complaintsCount;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
