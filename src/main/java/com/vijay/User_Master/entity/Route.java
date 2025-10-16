package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Route entity for Transport Management
 * Represents bus routes with stops, timings, and distance information
 */
@Entity
@Table(name = "routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Route extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_name", nullable = false)
    private String routeName;

    @Column(name = "route_code", unique = true, nullable = false)
    private String routeCode;

    @Column(name = "description")
    private String description;

    @Column(name = "start_location", nullable = false)
    private String startLocation;

    @Column(name = "end_location", nullable = false)
    private String endLocation;

    @Column(name = "total_distance")
    private Double totalDistance; // in kilometers

    @Column(name = "estimated_travel_time")
    private Integer estimatedTravelTime; // in minutes

    @Column(name = "pickup_time")
    private LocalTime pickupTime;

    @Column(name = "drop_time")
    private LocalTime dropTime;

    @Column(name = "return_pickup_time")
    private LocalTime returnPickupTime;

    @Column(name = "return_drop_time")
    private LocalTime returnDropTime;

    @Column(name = "route_fare")
    private Double routeFare; // Monthly fare per student

    @Column(name = "fuel_cost_per_trip")
    private Double fuelCostPerTrip;

    @Column(name = "maintenance_cost_per_trip")
    private Double maintenanceCostPerTrip;

    @Column(name = "driver_allowance_per_trip")
    private Double driverAllowancePerTrip;

    @Column(name = "conductor_allowance_per_trip")
    private Double conductorAllowancePerTrip;

    @Column(name = "total_cost_per_trip")
    private Double totalCostPerTrip;

    @Column(name = "max_students_per_trip")
    private Integer maxStudentsPerTrip;

    @Column(name = "current_students")
    @Builder.Default
    private Integer currentStudents = 0;

    @Column(name = "available_seats")
    @Builder.Default
    private Integer availableSeats = 0;

    @Column(name = "is_round_trip")
    @Builder.Default
    private Boolean isRoundTrip = true;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_operational")
    @Builder.Default
    private Boolean isOperational = true;

    @Column(name = "operational_days")
    private String operationalDays; // MON,TUE,WED,THU,FRI,SAT,SUN

    @Column(name = "weather_dependent")
    @Builder.Default
    private Boolean weatherDependent = false;

    @Column(name = "priority")
    private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "notes")
    private String notes;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RouteStop> routeStops = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StudentTransport> studentTransports = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Driver> drivers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public void calculateAvailableSeats() {
        if (maxStudentsPerTrip != null && currentStudents != null) {
            this.availableSeats = maxStudentsPerTrip - currentStudents;
        }
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public double getOccupancyPercentage() {
        if (maxStudentsPerTrip == null || maxStudentsPerTrip == 0) return 0.0;
        return (double) currentStudents / maxStudentsPerTrip * 100;
    }

    public void calculateTotalCostPerTrip() {
        double cost = 0.0;
        if (fuelCostPerTrip != null) cost += fuelCostPerTrip;
        if (maintenanceCostPerTrip != null) cost += maintenanceCostPerTrip;
        if (driverAllowancePerTrip != null) cost += driverAllowancePerTrip;
        if (conductorAllowancePerTrip != null) cost += conductorAllowancePerTrip;
        this.totalCostPerTrip = cost;
    }

    public boolean isOperationalToday(String dayOfWeek) {
        if (operationalDays == null || operationalDays.isEmpty()) return true;
        return operationalDays.toUpperCase().contains(dayOfWeek.toUpperCase());
    }

    public boolean isActiveRoute() {
        return isActive && isOperational && bus != null && bus.getIsActive();
    }

    public double calculateRevenuePerTrip() {
        if (routeFare != null && currentStudents != null) {
            return routeFare * currentStudents;
        }
        return 0.0;
    }

    public double calculateProfitPerTrip() {
        double revenue = calculateRevenuePerTrip();
        double cost = totalCostPerTrip != null ? totalCostPerTrip : 0.0;
        return revenue - cost;
    }
}
