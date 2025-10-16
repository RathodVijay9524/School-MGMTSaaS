package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for Route creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {

    @NotBlank(message = "Route name cannot be empty")
    @Size(max = 100, message = "Route name cannot exceed 100 characters")
    private String routeName;

    @NotBlank(message = "Route code cannot be empty")
    @Size(max = 20, message = "Route code cannot exceed 20 characters")
    private String routeCode;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Start location cannot be empty")
    @Size(max = 200, message = "Start location cannot exceed 200 characters")
    private String startLocation;

    @NotBlank(message = "End location cannot be empty")
    @Size(max = 200, message = "End location cannot exceed 200 characters")
    private String endLocation;

    @Min(value = 0, message = "Total distance cannot be negative")
    private Double totalDistance;

    @Min(value = 0, message = "Estimated travel time cannot be negative")
    private Integer estimatedTravelTime;

    private LocalTime pickupTime;

    private LocalTime dropTime;

    private LocalTime returnPickupTime;

    private LocalTime returnDropTime;

    @Min(value = 0, message = "Route fare cannot be negative")
    private Double routeFare;

    @Min(value = 0, message = "Fuel cost per trip cannot be negative")
    private Double fuelCostPerTrip;

    @Min(value = 0, message = "Maintenance cost per trip cannot be negative")
    private Double maintenanceCostPerTrip;

    @Min(value = 0, message = "Driver allowance per trip cannot be negative")
    private Double driverAllowancePerTrip;

    @Min(value = 0, message = "Conductor allowance per trip cannot be negative")
    private Double conductorAllowancePerTrip;

    @Min(value = 1, message = "Max students per trip must be at least 1")
    private Integer maxStudentsPerTrip;

    @Builder.Default
    private Boolean isRoundTrip = true;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isOperational = true;

    @Size(max = 50, message = "Operational days cannot exceed 50 characters")
    private String operationalDays;

    @Builder.Default
    private Boolean weatherDependent = false;

    @Size(max = 20, message = "Priority cannot exceed 20 characters")
    private String priority;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
