package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * RouteStop entity for Transport Management
 * Represents individual stops along a bus route with pickup/drop timings
 */
@Entity
@Table(name = "route_stops")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RouteStop extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stop_name", nullable = false)
    private String stopName;

    @Column(name = "stop_code", nullable = false)
    private String stopCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "pickup_time")
    private LocalTime pickupTime;

    @Column(name = "drop_time")
    private LocalTime dropTime;

    @Column(name = "return_pickup_time")
    private LocalTime returnPickupTime;

    @Column(name = "return_drop_time")
    private LocalTime returnDropTime;

    @Column(name = "stop_sequence")
    private Integer stopSequence; // Order of stops in the route

    @Column(name = "distance_from_school")
    private Double distanceFromSchool; // in kilometers

    @Column(name = "estimated_travel_time_from_school")
    private Integer estimatedTravelTimeFromSchool; // in minutes

    @Column(name = "landmarks")
    private String landmarks; // Nearby landmarks for easy identification

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "is_pickup_point")
    @Builder.Default
    private Boolean isPickupPoint = true;

    @Column(name = "is_drop_point")
    @Builder.Default
    private Boolean isDropPoint = true;

    @Column(name = "is_return_pickup_point")
    @Builder.Default
    private Boolean isReturnPickupPoint = true;

    @Column(name = "is_return_drop_point")
    @Builder.Default
    private Boolean isReturnDropPoint = true;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_safe_location")
    @Builder.Default
    private Boolean isSafeLocation = true;

    @Column(name = "has_shelter")
    @Builder.Default
    private Boolean hasShelter = false;

    @Column(name = "has_lighting")
    @Builder.Default
    private Boolean hasLighting = false;

    @Column(name = "parking_available")
    @Builder.Default
    private Boolean parkingAvailable = false;

    @Column(name = "notes")
    private String notes;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public boolean isMorningStop() {
        return isPickupPoint || isDropPoint;
    }

    public boolean isEveningStop() {
        return isReturnPickupPoint || isReturnDropPoint;
    }

    public boolean isActiveStop() {
        return isActive && (isPickupPoint || isDropPoint || isReturnPickupPoint || isReturnDropPoint);
    }

    public String getFullAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        if (address != null) addressBuilder.append(address);
        if (landmarks != null && !landmarks.isEmpty()) {
            addressBuilder.append(" (Near ").append(landmarks).append(")");
        }
        return addressBuilder.toString();
    }

    public boolean hasValidCoordinates() {
        return latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0;
    }

    public String getStopDisplayName() {
        return stopName + " (" + stopCode + ")";
    }
}
