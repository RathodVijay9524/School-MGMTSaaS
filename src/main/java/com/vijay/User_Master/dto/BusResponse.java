package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Bus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * DTO for Bus response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusResponse {

    private Long id;
    private String busNumber;
    private String busName;
    private String registrationNumber;
    private String make;
    private String model;
    private Integer yearOfManufacture;
    private Integer capacity;
    private Integer currentStudents;
    private Integer availableSeats;
    private String fuelType;
    private Double fuelCapacity;
    private Double mileage;
    private String insuranceNumber;
    private Date insuranceExpiryDate;
    private String fitnessCertificateNumber;
    private Date fitnessExpiryDate;
    private String pollutionCertificateNumber;
    private Date pollutionExpiryDate;
    private String permitNumber;
    private Date permitExpiryDate;
    private String driverLicenseRequired;
    private Boolean gpsTrackingEnabled;
    private Boolean cctvEnabled;
    private Boolean acAvailable;
    private Boolean wifiAvailable;
    private Integer emergencyExits;
    private Boolean fireExtinguisher;
    private Boolean firstAidKit;
    private Boolean speedGovernor;
    private Date lastServiceDate;
    private Date nextServiceDate;
    private Integer serviceIntervalKm;
    private Integer currentMileage;
    private Date purchaseDate;
    private Double purchasePrice;
    private Double currentValue;
    private Double depreciationRate;
    private Double maintenanceCostPerMonth;
    private Double fuelCostPerKm;
    private Double driverSalaryPerMonth;
    private Double conductorSalaryPerMonth;
    private Boolean isActive;
    private Boolean isAvailable;
    private Boolean isUnderMaintenance;
    private String maintenanceReason;
    private String notes;
    private Date createdOn;
    private Date updatedOn;

    // Computed fields
    private Boolean hasAvailableSeats;
    private Double occupancyPercentage;
    private Boolean isDocumentExpired;
    private Boolean needsService;
    private Double monthlyOperatingCost;
    private String status;
    private String displayName;
    private String makeModel;
    private String age;
    private String nextServiceDue;
    private String documentStatus;
    private String fuelEfficiency;
}
