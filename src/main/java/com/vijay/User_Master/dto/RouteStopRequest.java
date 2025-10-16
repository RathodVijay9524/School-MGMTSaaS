package com.vijay.User_Master.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for RouteStop creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopRequest {

    @NotBlank(message = "Stop name cannot be empty")
    @Size(max = 100, message = "Stop name cannot exceed 100 characters")
    private String stopName;

    @NotBlank(message = "Stop code cannot be empty")
    @Size(max = 20, message = "Stop code cannot exceed 20 characters")
    private String stopCode;

    @NotBlank(message = "Address cannot be empty")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double longitude;

    private LocalTime pickupTime;

    private LocalTime dropTime;

    private LocalTime returnPickupTime;

    private LocalTime returnDropTime;

    @Min(value = 1, message = "Stop sequence must be at least 1")
    private Integer stopSequence;

    @Min(value = 0, message = "Distance from school cannot be negative")
    private Double distanceFromSchool;

    @Min(value = 0, message = "Estimated travel time cannot be negative")
    private Integer estimatedTravelTimeFromSchool;

    @Size(max = 200, message = "Landmarks cannot exceed 200 characters")
    private String landmarks;

    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    private String contactPerson;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid phone number format")
    private String contactPhone;

    @Builder.Default
    private Boolean isPickupPoint = true;

    @Builder.Default
    private Boolean isDropPoint = true;

    @Builder.Default
    private Boolean isReturnPickupPoint = true;

    @Builder.Default
    private Boolean isReturnDropPoint = true;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isSafeLocation = true;

    @Builder.Default
    private Boolean hasShelter = false;

    @Builder.Default
    private Boolean hasLighting = false;

    @Builder.Default
    private Boolean parkingAvailable = false;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Related entity ID
    @NotNull(message = "Route ID is required")
    private Long routeId;
}
