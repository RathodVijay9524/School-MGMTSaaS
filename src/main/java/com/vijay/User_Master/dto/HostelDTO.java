package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelDTO {

    private Long id;
    private String hostelName;
    private String hostelCode;
    private String description;
    private String address;
    private String contactNumber;
    private String email;
    private Integer capacity;
    private Integer occupiedBeds;
    private Integer availableBeds;
    private Integer totalRooms;
    private Integer totalBeds;
    private String wardenName;
    private String wardenContact;
    private String wardenEmail;
    private Boolean messAvailable;
    private String messTimings;
    private String rulesAndRegulations;
    private String amenities;
    private Boolean isActive;
    private Double feesPerMonth;
    private Double securityDeposit;
    private Boolean laundryAvailable;
    private Boolean wifiAvailable;
    private Boolean gymAvailable;
    private Boolean libraryAvailable;
    private Date createdOn;
    private Date updatedOn;

    // Additional calculated fields
    private Double occupancyPercentage;
    private Boolean hasAvailableBeds;

    // Nested DTOs for related entities
    private List<RoomDTO> rooms;
    private List<HostelResidentDTO> residents;

    // Owner information
    private Long ownerId;
    private String ownerName;
}
