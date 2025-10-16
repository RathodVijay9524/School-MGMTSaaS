package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * DTO for Driver response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String alternatePhone;
    private String address;
    private Date dateOfBirth;
    private Integer age;
    private String gender;
    private String bloodGroup;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private String licenseNumber;
    private String licenseType;
    private Date licenseIssueDate;
    private Date licenseExpiryDate;
    private String licenseIssuingAuthority;
    private Integer yearsOfExperience;
    private String previousEmployers;
    private String drivingSchool;
    private Date certificationDate;
    private Date joiningDate;
    private Double salary;
    private Double allowancePerTrip;
    private Double bonusAmount;
    private String pfNumber;
    private String esiNumber;
    private String bankAccountNumber;
    private String bankName;
    private String ifscCode;
    private String medicalCertificateNumber;
    private Date medicalCertificateExpiry;
    private Date eyeTestDate;
    private String eyeTestResult;
    private String bloodPressure;
    private String diabetesStatus;
    private String anyMedicalConditions;
    private Integer totalTripsCompleted;
    private Integer totalDistanceDriven;
    private Integer accidentsCount;
    private Integer violationsCount;
    private Double performanceRating;
    private Date lastPerformanceReview;
    private String trainingCompleted;
    private Date nextTrainingDue;
    private String employmentStatus;
    private Boolean isAvailable;
    private Boolean isOnDuty;
    private String currentLocation;
    private String notes;
    private Date createdOn;
    private Date updatedOn;

    // Related entity IDs
    private Long busId;
    private String busNumber;
    private String busName;
    private Long routeId;
    private String routeName;
    private String routeCode;

    // Computed fields
    private String fullName;
    private Boolean isLicenseValid;
    private Boolean isMedicalCertificateValid;
    private Boolean needsLicenseRenewal;
    private Boolean needsMedicalRenewal;
    private Boolean isEligibleToDrive;
    private Double performanceScore;
    private String experienceLevel;
    private String status;
    private String displayName;
    private String licenseStatus;
    private String medicalStatus;
    private String performanceLevel;
    private String availabilityStatus;
}
