package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * DTO for Bus creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusRequest {

    @NotBlank(message = "Bus number cannot be empty")
    @Size(max = 20, message = "Bus number cannot exceed 20 characters")
    private String busNumber;

    @Size(max = 100, message = "Bus name cannot exceed 100 characters")
    private String busName;

    @NotBlank(message = "Registration number cannot be empty")
    @Size(max = 20, message = "Registration number cannot exceed 20 characters")
    private String registrationNumber;

    @Size(max = 50, message = "Make cannot exceed 50 characters")
    private String make;

    @Size(max = 50, message = "Model cannot exceed 50 characters")
    private String model;

    @Min(value = 1900, message = "Year of manufacture must be at least 1900")
    @Max(value = 2030, message = "Year of manufacture cannot exceed 2030")
    private Integer yearOfManufacture;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity cannot exceed 100")
    private Integer capacity;

    @Size(max = 20, message = "Fuel type cannot exceed 20 characters")
    private String fuelType;

    @Min(value = 0, message = "Fuel capacity cannot be negative")
    private Double fuelCapacity;

    @Min(value = 0, message = "Mileage cannot be negative")
    private Double mileage;

    @Size(max = 50, message = "Insurance number cannot exceed 50 characters")
    private String insuranceNumber;

    private Date insuranceExpiryDate;

    @Size(max = 50, message = "Fitness certificate number cannot exceed 50 characters")
    private String fitnessCertificateNumber;

    private Date fitnessExpiryDate;

    @Size(max = 50, message = "Pollution certificate number cannot exceed 50 characters")
    private String pollutionCertificateNumber;

    private Date pollutionExpiryDate;

    @Size(max = 50, message = "Permit number cannot exceed 50 characters")
    private String permitNumber;

    private Date permitExpiryDate;

    @Size(max = 50, message = "Driver license required cannot exceed 50 characters")
    private String driverLicenseRequired;

    @Builder.Default
    private Boolean gpsTrackingEnabled = false;

    @Builder.Default
    private Boolean cctvEnabled = false;

    @Builder.Default
    private Boolean acAvailable = false;

    @Builder.Default
    private Boolean wifiAvailable = false;

    @Min(value = 0, message = "Emergency exits cannot be negative")
    private Integer emergencyExits;

    @Builder.Default
    private Boolean fireExtinguisher = false;

    @Builder.Default
    private Boolean firstAidKit = false;

    @Builder.Default
    private Boolean speedGovernor = false;

    private Date lastServiceDate;

    private Date nextServiceDate;

    @Min(value = 0, message = "Service interval cannot be negative")
    private Integer serviceIntervalKm;

    @Min(value = 0, message = "Current mileage cannot be negative")
    private Integer currentMileage;

    private Date purchaseDate;

    @Min(value = 0, message = "Purchase price cannot be negative")
    private Double purchasePrice;

    @Min(value = 0, message = "Current value cannot be negative")
    private Double currentValue;

    @Min(value = 0, message = "Depreciation rate cannot be negative")
    @Max(value = 100, message = "Depreciation rate cannot exceed 100%")
    private Double depreciationRate;

    @Min(value = 0, message = "Maintenance cost cannot be negative")
    private Double maintenanceCostPerMonth;

    @Min(value = 0, message = "Fuel cost per km cannot be negative")
    private Double fuelCostPerKm;

    @Min(value = 0, message = "Driver salary cannot be negative")
    private Double driverSalaryPerMonth;

    @Min(value = 0, message = "Conductor salary cannot be negative")
    private Double conductorSalaryPerMonth;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private Boolean isUnderMaintenance = false;

    @Size(max = 500, message = "Maintenance reason cannot exceed 500 characters")
    private String maintenanceReason;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
