package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Driver entity for Transport Management
 * Represents bus drivers with licensing, experience, and assignment information
 */
@Entity
@Table(name = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Driver extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", unique = true, nullable = false)
    private String employeeId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender; // MALE, FEMALE, OTHER

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relation")
    private String emergencyContactRelation;

    // License Information
    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    @Column(name = "license_type", nullable = false)
    private String licenseType; // LMV, HMV, PSV, etc.

    @Column(name = "license_issue_date")
    private Date licenseIssueDate;

    @Column(name = "license_expiry_date", nullable = false)
    private Date licenseExpiryDate;

    @Column(name = "license_issuing_authority")
    private String licenseIssuingAuthority;

    // Experience Information
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "previous_employers")
    private String previousEmployers;

    @Column(name = "driving_school")
    private String drivingSchool;

    @Column(name = "certification_date")
    private Date certificationDate;

    // Employment Information
    @Column(name = "joining_date", nullable = false)
    private Date joiningDate;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "allowance_per_trip")
    private Double allowancePerTrip;

    @Column(name = "bonus_amount")
    private Double bonusAmount;

    @Column(name = "pf_number")
    private String pfNumber;

    @Column(name = "esi_number")
    private String esiNumber;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    // Medical Information
    @Column(name = "medical_certificate_number")
    private String medicalCertificateNumber;

    @Column(name = "medical_certificate_expiry")
    private Date medicalCertificateExpiry;

    @Column(name = "eye_test_date")
    private Date eyeTestDate;

    @Column(name = "eye_test_result")
    private String eyeTestResult; // PASS, FAIL

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "diabetes_status")
    private String diabetesStatus; // YES, NO, CONTROLLED

    @Column(name = "any_medical_conditions")
    private String anyMedicalConditions;

    // Performance Tracking
    @Column(name = "total_trips_completed")
    @Builder.Default
    private Integer totalTripsCompleted = 0;

    @Column(name = "total_distance_driven")
    @Builder.Default
    private Integer totalDistanceDriven = 0;

    @Column(name = "accidents_count")
    @Builder.Default
    private Integer accidentsCount = 0;

    @Column(name = "violations_count")
    @Builder.Default
    private Integer violationsCount = 0;

    @Column(name = "performance_rating")
    private Double performanceRating; // 1.0 to 5.0

    @Column(name = "last_performance_review")
    private Date lastPerformanceReview;

    @Column(name = "training_completed")
    private String trainingCompleted;

    @Column(name = "next_training_due")
    private Date nextTrainingDue;

    // Status Information
    @Column(name = "employment_status", nullable = false)
    private String employmentStatus; // ACTIVE, INACTIVE, SUSPENDED, TERMINATED

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "is_on_duty")
    @Builder.Default
    private Boolean isOnDuty = false;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "last_check_in")
    private java.time.LocalDateTime lastCheckIn;

    @Column(name = "notes")
    private String notes;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StudentTransport> studentTransports = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isLicenseValid() {
        if (licenseExpiryDate == null) return false;
        return licenseExpiryDate.after(new Date(System.currentTimeMillis()));
    }

    public boolean isMedicalCertificateValid() {
        if (medicalCertificateExpiry == null) return false;
        return medicalCertificateExpiry.after(new Date(System.currentTimeMillis()));
    }

    public boolean needsLicenseRenewal() {
        if (licenseExpiryDate == null) return false;
        Date thirtyDaysFromNow = new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        return licenseExpiryDate.before(thirtyDaysFromNow);
    }

    public boolean needsMedicalRenewal() {
        if (medicalCertificateExpiry == null) return false;
        Date thirtyDaysFromNow = new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        return medicalCertificateExpiry.before(thirtyDaysFromNow);
    }

    public boolean isEligibleToDrive() {
        return employmentStatus.equals("ACTIVE") && 
               isAvailable && 
               isLicenseValid() && 
               isMedicalCertificateValid() &&
               eyeTestResult != null && eyeTestResult.equals("PASS");
    }

    public double calculatePerformanceScore() {
        if (performanceRating == null) return 0.0;
        double score = performanceRating * 20; // Convert to percentage
        
        // Deduct points for accidents and violations
        if (accidentsCount != null) score -= accidentsCount * 10;
        if (violationsCount != null) score -= violationsCount * 5;
        
        return Math.max(0, Math.min(100, score));
    }

    public String getExperienceLevel() {
        if (yearsOfExperience == null) return "NEW";
        if (yearsOfExperience < 2) return "JUNIOR";
        if (yearsOfExperience < 5) return "INTERMEDIATE";
        if (yearsOfExperience < 10) return "SENIOR";
        return "EXPERT";
    }
}
