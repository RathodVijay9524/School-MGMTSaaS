package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelResidentRequestDTO {

    @NotBlank(message = "Resident ID is required")
    private String residentId;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    @Future(message = "Expected check-out date must be in the future")
    private LocalDate expectedCheckOutDate;

    @Builder.Default
    private Boolean isActive = true;

    private String status;

    private String emergencyContactName;

    private String emergencyContactNumber;

    private String emergencyContactRelation;

    private String medicalConditions;

    private String allergies;

    private String specialDietaryRequirements;

    private String guardianName;

    private String guardianContact;

    private String guardianAddress;

    @Min(value = 0, message = "Monthly fees cannot be negative")
    private Double monthlyFees;

    @Min(value = 0, message = "Security deposit cannot be negative")
    private Double securityDeposit;

    @Builder.Default
    private Boolean securityDepositPaid = false;

    private LocalDate securityDepositPaidDate;

    @Min(value = 0, message = "Mess fees cannot be negative")
    private Double messFees;

    @Builder.Default
    private Boolean messSubscribed = false;

    @Builder.Default
    private Boolean laundrySubscribed = false;

    @Builder.Default
    private Boolean wifiSubscribed = false;

    @Builder.Default
    private Boolean gymSubscribed = false;

    @Builder.Default
    private Boolean libraryAccess = false;

    @Builder.Default
    private Boolean visitorPolicyAccepted = false;

    @Builder.Default
    private Boolean hostelRulesAccepted = false;

    private String notes;

    private LocalDate lastFeePaymentDate;

    private LocalDate nextFeeDueDate;

    @Min(value = 0, message = "Outstanding fees cannot be negative")
    @Builder.Default
    private Double outstandingFees = 0.0;

    @Min(value = 0, message = "Late fee charges cannot be negative")
    @Builder.Default
    private Double lateFeeCharges = 0.0;

    @Min(value = 0, message = "Penalty charges cannot be negative")
    @Builder.Default
    private Double penaltyCharges = 0.0;

    // Required relationships
    @NotNull(message = "Hostel ID is required")
    private Long hostelId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Bed ID is required")
    private Long bedId;

    @NotNull(message = "Student ID is required")
    private Long studentId;
}
