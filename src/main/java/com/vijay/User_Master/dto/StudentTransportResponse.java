package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * DTO for StudentTransport response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTransportResponse {

    private Long id;
    private String transportId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long busId;
    private String busNumber;
    private String busName;
    private Long routeId;
    private String routeName;
    private String routeCode;
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private Long pickupStopId;
    private String pickupStopName;
    private String pickupStopAddress;
    private Long dropStopId;
    private String dropStopName;
    private String dropStopAddress;
    private Time pickupTime;
    private Time dropTime;
    private Time returnPickupTime;
    private Time returnDropTime;
    private Double monthlyFee;
    private Double annualFee;
    private Double discountPercentage;
    private Double finalFee;
    private String paymentStatus;
    private Date lastPaymentDate;
    private Date nextPaymentDue;
    private Double outstandingAmount;
    private Date assignmentDate;
    private Date effectiveFrom;
    private Date effectiveUntil;
    private String assignmentStatus;
    private Boolean isPickupEnabled;
    private Boolean isDropEnabled;
    private Boolean isReturnPickupEnabled;
    private Boolean isReturnDropEnabled;
    private String parentName;
    private String parentPhone;
    private String parentEmail;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private String medicalConditions;
    private String allergies;
    private String medications;
    private String specialInstructions;
    private String pickupContactPerson;
    private String pickupContactPhone;
    private String dropContactPerson;
    private String dropContactPhone;
    private Boolean isActive;
    private Double attendanceRate;
    private Date lastAttendanceDate;
    private Integer totalTripsTaken;
    private Integer missedTripsCount;
    private Integer latePickupsCount;
    private Integer incidentsCount;
    private Integer complaintsCount;
    private String notes;
    private Date createdOn;
    private Date updatedOn;

    // Computed fields
    private Boolean isCurrentlyActive;
    private Boolean isPaymentOverdue;
    private Boolean canUseTransport;
    private String transportStatus;
    private Double attendancePercentage;
    private String fullTransportDetails;
    private String studentDisplayName;
    private String busDisplayName;
    private String routeDisplayName;
    private String driverDisplayName;
    private String pickupStopDisplay;
    private String dropStopDisplay;
    private String parentContactInfo;
    private String emergencyContactInfo;
    private String medicalInfo;
    private String assignmentStatusDisplay;
    private String paymentStatusDisplay;
    private String timingDisplay;
    private String feeSummary;
    private String performanceSummary;
}
