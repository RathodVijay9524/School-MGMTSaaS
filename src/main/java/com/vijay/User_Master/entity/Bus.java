package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Bus entity for Transport Management
 * Represents school buses with capacity, features, and maintenance tracking
 */
@Entity
@Table(name = "buses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Bus extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_number", unique = true, nullable = false)
    private String busNumber;

    @Column(name = "bus_name")
    private String busName;

    @Column(name = "registration_number", unique = true, nullable = false)
    private String registrationNumber;

    @Column(name = "make")
    private String make; // Toyota, Tata, etc.

    @Column(name = "model")
    private String model;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Column(name = "capacity")
    private Integer capacity; // Total seating capacity

    @Column(name = "current_students")
    @Builder.Default
    private Integer currentStudents = 0;

    @Column(name = "available_seats")
    @Builder.Default
    private Integer availableSeats = 0;

    @Column(name = "fuel_type")
    private String fuelType; // Diesel, Petrol, CNG, Electric

    @Column(name = "fuel_capacity")
    private Double fuelCapacity; // in liters

    @Column(name = "mileage")
    private Double mileage; // km per liter

    @Column(name = "insurance_number")
    private String insuranceNumber;

    @Column(name = "insurance_expiry_date")
    private java.sql.Date insuranceExpiryDate;

    @Column(name = "fitness_certificate_number")
    private String fitnessCertificateNumber;

    @Column(name = "fitness_expiry_date")
    private java.sql.Date fitnessExpiryDate;

    @Column(name = "pollution_certificate_number")
    private String pollutionCertificateNumber;

    @Column(name = "pollution_expiry_date")
    private java.sql.Date pollutionExpiryDate;

    @Column(name = "permit_number")
    private String permitNumber;

    @Column(name = "permit_expiry_date")
    private java.sql.Date permitExpiryDate;

    @Column(name = "driver_license_required")
    private String driverLicenseRequired; // Heavy Vehicle, Light Vehicle

    @Column(name = "gps_tracking_enabled")
    @Builder.Default
    private Boolean gpsTrackingEnabled = false;

    @Column(name = "cctv_enabled")
    @Builder.Default
    private Boolean cctvEnabled = false;

    @Column(name = "ac_available")
    @Builder.Default
    private Boolean acAvailable = false;

    @Column(name = "wifi_available")
    @Builder.Default
    private Boolean wifiAvailable = false;

    @Column(name = "emergency_exits")
    private Integer emergencyExits;

    @Column(name = "fire_extinguisher")
    @Builder.Default
    private Boolean fireExtinguisher = false;

    @Column(name = "first_aid_kit")
    @Builder.Default
    private Boolean firstAidKit = false;

    @Column(name = "speed_governor")
    @Builder.Default
    private Boolean speedGovernor = false;

    @Column(name = "last_service_date")
    private java.sql.Date lastServiceDate;

    @Column(name = "next_service_date")
    private java.sql.Date nextServiceDate;

    @Column(name = "service_interval_km")
    private Integer serviceIntervalKm;

    @Column(name = "current_mileage")
    private Integer currentMileage;

    @Column(name = "purchase_date")
    private java.sql.Date purchaseDate;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "current_value")
    private Double currentValue;

    @Column(name = "depreciation_rate")
    private Double depreciationRate; // Annual depreciation percentage

    @Column(name = "maintenance_cost_per_month")
    private Double maintenanceCostPerMonth;

    @Column(name = "fuel_cost_per_km")
    private Double fuelCostPerKm;

    @Column(name = "driver_salary_per_month")
    private Double driverSalaryPerMonth;

    @Column(name = "conductor_salary_per_month")
    private Double conductorSalaryPerMonth;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "is_under_maintenance")
    @Builder.Default
    private Boolean isUnderMaintenance = false;

    @Column(name = "maintenance_reason")
    private String maintenanceReason;

    @Column(name = "notes")
    private String notes;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relationships
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Route> routes = new ArrayList<>();

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Driver> drivers = new ArrayList<>();

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StudentTransport> studentTransports = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public void calculateAvailableSeats() {
        if (capacity != null && currentStudents != null) {
            this.availableSeats = capacity - currentStudents;
        }
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public double getOccupancyPercentage() {
        if (capacity == null || capacity == 0) return 0.0;
        return (double) currentStudents / capacity * 100;
    }

    public boolean isDocumentExpired() {
        java.util.Date today = new java.util.Date();
        return (insuranceExpiryDate != null && insuranceExpiryDate.before(today)) ||
               (fitnessExpiryDate != null && fitnessExpiryDate.before(today)) ||
               (pollutionExpiryDate != null && pollutionExpiryDate.before(today)) ||
               (permitExpiryDate != null && permitExpiryDate.before(today));
    }

    public boolean needsService() {
        if (nextServiceDate != null) {
            java.util.Date today = new java.util.Date();
            return nextServiceDate.before(today);
        }
        return false;
    }

    public double calculateMonthlyOperatingCost() {
        double cost = 0.0;
        if (maintenanceCostPerMonth != null) cost += maintenanceCostPerMonth;
        if (driverSalaryPerMonth != null) cost += driverSalaryPerMonth;
        if (conductorSalaryPerMonth != null) cost += conductorSalaryPerMonth;
        return cost;
    }
}
