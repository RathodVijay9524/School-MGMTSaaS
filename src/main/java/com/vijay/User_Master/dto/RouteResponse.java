package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

/**
 * DTO for Route response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {

    private Long id;
    private String routeName;
    private String routeCode;
    private String description;
    private String startLocation;
    private String endLocation;
    private Double totalDistance;
    private Integer estimatedTravelTime;
    private LocalTime pickupTime;
    private LocalTime dropTime;
    private LocalTime returnPickupTime;
    private LocalTime returnDropTime;
    private Double routeFare;
    private Double fuelCostPerTrip;
    private Double maintenanceCostPerTrip;
    private Double driverAllowancePerTrip;
    private Double conductorAllowancePerTrip;
    private Double totalCostPerTrip;
    private Integer maxStudentsPerTrip;
    private Integer currentStudents;
    private Integer availableSeats;
    private Boolean isRoundTrip;
    private Boolean isActive;
    private Boolean isOperational;
    private String operationalDays;
    private Boolean weatherDependent;
    private String priority;
    private String notes;
    private Date createdOn;
    private Date updatedOn;

    // Related entity IDs
    private Long busId;
    private String busNumber;
    private String busName;

    // Computed fields
    private Boolean hasAvailableSeats;
    private Double occupancyPercentage;
    private Double revenuePerTrip;
    private Double profitPerTrip;
    private String status;
    private String displayName;
    private String routeDisplay;
    private String timingDisplay;
    private String costAnalysis;
    private String operationalStatus;
}
