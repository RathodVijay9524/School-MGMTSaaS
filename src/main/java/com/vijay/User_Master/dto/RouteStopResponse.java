package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

/**
 * DTO for RouteStop response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopResponse {

    private Long id;
    private String stopName;
    private String stopCode;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalTime pickupTime;
    private LocalTime dropTime;
    private LocalTime returnPickupTime;
    private LocalTime returnDropTime;
    private Integer stopSequence;
    private Double distanceFromSchool;
    private Integer estimatedTravelTimeFromSchool;
    private String landmarks;
    private String contactPerson;
    private String contactPhone;
    private Boolean isPickupPoint;
    private Boolean isDropPoint;
    private Boolean isReturnPickupPoint;
    private Boolean isReturnDropPoint;
    private Boolean isActive;
    private Boolean isSafeLocation;
    private Boolean hasShelter;
    private Boolean hasLighting;
    private Boolean parkingAvailable;
    private String notes;
    private Date createdOn;
    private Date updatedOn;

    // Related entity IDs
    private Long routeId;
    private String routeName;
    private String routeCode;

    // Computed fields
    private String fullAddress;
    private String stopDisplayName;
    private Boolean hasValidCoordinates;
    private Boolean isMorningStop;
    private Boolean isEveningStop;
    private String stopType;
    private String facilities;
    private String locationStatus;
}
