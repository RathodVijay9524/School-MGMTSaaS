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
public class RoomDTO {

    private Long id;
    private String roomNumber;
    private Integer floorNumber;
    private String roomType;
    private Integer capacity;
    private Integer occupiedBeds;
    private Integer availableBeds;
    private Double roomFeesPerMonth;
    private String description;
    private String amenities;
    private Boolean isActive;
    private Boolean isAvailable;
    private Boolean acAvailable;
    private Boolean attachedBathroom;
    private Boolean balconyAvailable;
    private Boolean furnitureIncluded;
    private Date createdOn;
    private Date updatedOn;

    // Additional calculated fields
    private Double occupancyPercentage;
    private Boolean hasAvailableBeds;

    // Related entities
    private Long hostelId;
    private String hostelName;
    private List<BedDTO> beds;
    private List<HostelResidentDTO> residents;
}
