package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDTO {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @Min(value = 0, message = "Floor number cannot be negative")
    private Integer floorNumber;

    @NotBlank(message = "Room type is required")
    private String roomType;

    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @Min(value = 0, message = "Room fees cannot be negative")
    private Double roomFeesPerMonth;

    private String description;

    private String amenities;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private Boolean acAvailable = false;

    @Builder.Default
    private Boolean attachedBathroom = false;

    @Builder.Default
    private Boolean balconyAvailable = false;

    @Builder.Default
    private Boolean furnitureIncluded = true;

    @NotNull(message = "Hostel ID is required")
    private Long hostelId;
}
