package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelResidentDTO {

    private Long id;
    private String residentId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDate expectedCheckOutDate;
    private Boolean isActive;
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
    private Double monthlyFees;
    private Double securityDeposit;
    private Boolean securityDepositPaid;
    private LocalDate securityDepositPaidDate;
    private Double messFees;
    private Boolean messSubscribed;
    private Boolean laundrySubscribed;
    private Boolean wifiSubscribed;
    private Boolean gymSubscribed;
    private Boolean libraryAccess;
    private Boolean visitorPolicyAccepted;
    private Boolean hostelRulesAccepted;
    private String notes;
    private LocalDate lastFeePaymentDate;
    private LocalDate nextFeeDueDate;
    private Double outstandingFees;
    private Double lateFeeCharges;
    private Double penaltyCharges;
    private Date createdOn;
    private Date updatedOn;

    // Additional calculated fields
    private Boolean isCurrentlyResiding;
    private Boolean hasOverdueFees;
    private Double totalFees;

    // Related entities
    private Long hostelId;
    private String hostelName;
    private Long roomId;
    private String roomNumber;
    private Long bedId;
    private String bedNumber;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
}
