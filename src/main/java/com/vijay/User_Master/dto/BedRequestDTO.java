package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedRequestDTO {

    @NotBlank(message = "Bed number is required")
    private String bedNumber;

    @NotBlank(message = "Bed type is required")
    private String bedType;

    @Min(value = 0, message = "Bed fees cannot be negative")
    private Double bedFeesPerMonth;

    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private Boolean isOccupied = false;

    @Builder.Default
    private Boolean isUnderMaintenance = false;

    private String maintenanceNotes;

    @NotNull(message = "Room ID is required")
    private Long roomId;
}
