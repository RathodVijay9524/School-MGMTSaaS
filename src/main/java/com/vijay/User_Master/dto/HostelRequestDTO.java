package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelRequestDTO {

    @NotBlank(message = "Hostel name is required")
    private String hostelName;

    @NotBlank(message = "Hostel code is required")
    private String hostelCode;

    private String description;

    private String address;

    private String contactNumber;

    @Email(message = "Invalid email format")
    private String email;

    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    private String wardenName;

    private String wardenContact;

    @Email(message = "Invalid warden email format")
    private String wardenEmail;

    @Builder.Default
    private Boolean messAvailable = false;

    private String messTimings;

    private String rulesAndRegulations;

    private String amenities;

    @Builder.Default
    private Boolean isActive = true;

    @Min(value = 0, message = "Fees cannot be negative")
    private Double feesPerMonth;

    @Min(value = 0, message = "Security deposit cannot be negative")
    private Double securityDeposit;

    @Builder.Default
    private Boolean laundryAvailable = false;

    @Builder.Default
    private Boolean wifiAvailable = false;

    @Builder.Default
    private Boolean gymAvailable = false;

    @Builder.Default
    private Boolean libraryAvailable = false;
}
