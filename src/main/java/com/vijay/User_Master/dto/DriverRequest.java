package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * DTO for Driver creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequest {

    @NotBlank(message = "Employee ID cannot be empty")
    @Size(max = 20, message = "Employee ID cannot exceed 20 characters")
    private String employeeId;

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid alternate phone number format")
    private String alternatePhone;

    @NotBlank(message = "Address cannot be empty")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    private Date dateOfBirth;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 70, message = "Age cannot exceed 70")
    private Integer age;

    @Size(max = 10, message = "Gender cannot exceed 10 characters")
    private String gender;

    @Size(max = 5, message = "Blood group cannot exceed 5 characters")
    private String bloodGroup;

    @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid emergency contact phone format")
    private String emergencyContactPhone;

    @Size(max = 50, message = "Emergency contact relation cannot exceed 50 characters")
    private String emergencyContactRelation;

    // License Information
    @NotBlank(message = "License number cannot be empty")
    @Size(max = 20, message = "License number cannot exceed 20 characters")
    private String licenseNumber;

    @NotBlank(message = "License type cannot be empty")
    @Size(max = 20, message = "License type cannot exceed 20 characters")
    private String licenseType;

    private Date licenseIssueDate;

    @NotNull(message = "License expiry date is required")
    private Date licenseExpiryDate;

    @Size(max = 100, message = "License issuing authority cannot exceed 100 characters")
    private String licenseIssuingAuthority;

    // Experience Information
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;

    @Size(max = 500, message = "Previous employers cannot exceed 500 characters")
    private String previousEmployers;

    @Size(max = 100, message = "Driving school cannot exceed 100 characters")
    private String drivingSchool;

    private Date certificationDate;

    // Employment Information
    @NotNull(message = "Joining date is required")
    private Date joiningDate;

    @Min(value = 0, message = "Salary cannot be negative")
    private Double salary;

    @Min(value = 0, message = "Allowance per trip cannot be negative")
    private Double allowancePerTrip;

    @Min(value = 0, message = "Bonus amount cannot be negative")
    private Double bonusAmount;

    @Size(max = 20, message = "PF number cannot exceed 20 characters")
    private String pfNumber;

    @Size(max = 20, message = "ESI number cannot exceed 20 characters")
    private String esiNumber;

    @Size(max = 30, message = "Bank account number cannot exceed 30 characters")
    private String bankAccountNumber;

    @Size(max = 100, message = "Bank name cannot exceed 100 characters")
    private String bankName;

    @Size(max = 15, message = "IFSC code cannot exceed 15 characters")
    private String ifscCode;

    // Medical Information
    @Size(max = 50, message = "Medical certificate number cannot exceed 50 characters")
    private String medicalCertificateNumber;

    private Date medicalCertificateExpiry;

    private Date eyeTestDate;

    @Size(max = 10, message = "Eye test result cannot exceed 10 characters")
    private String eyeTestResult;

    @Size(max = 20, message = "Blood pressure cannot exceed 20 characters")
    private String bloodPressure;

    @Size(max = 20, message = "Diabetes status cannot exceed 20 characters")
    private String diabetesStatus;

    @Size(max = 500, message = "Medical conditions cannot exceed 500 characters")
    private String anyMedicalConditions;

    // Performance Tracking
    @Min(value = 0, message = "Total trips completed cannot be negative")
    private Integer totalTripsCompleted;

    @Min(value = 0, message = "Total distance driven cannot be negative")
    private Integer totalDistanceDriven;

    @Min(value = 0, message = "Accidents count cannot be negative")
    private Integer accidentsCount;

    @Min(value = 0, message = "Violations count cannot be negative")
    private Integer violationsCount;

    @DecimalMin(value = "1.0", message = "Performance rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Performance rating cannot exceed 5.0")
    private Double performanceRating;

    private Date lastPerformanceReview;

    @Size(max = 500, message = "Training completed cannot exceed 500 characters")
    private String trainingCompleted;

    private Date nextTrainingDue;

    // Status Information
    @NotBlank(message = "Employment status is required")
    @Size(max = 20, message = "Employment status cannot exceed 20 characters")
    private String employmentStatus;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private Boolean isOnDuty = false;

    @Size(max = 100, message = "Current location cannot exceed 100 characters")
    private String currentLocation;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    // Related entity IDs
    private Long busId;
    private Long routeId;
}
