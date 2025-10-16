package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedDTO {

    private Long id;
    private String bedNumber;
    private String bedType;
    private Boolean isOccupied;
    private Boolean isAvailable;
    private Boolean isActive;
    private Double bedFeesPerMonth;
    private String description;
    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime nextMaintenanceDate;
    private String maintenanceNotes;
    private Boolean isUnderMaintenance;
    private Date createdOn;
    private Date updatedOn;

    // Additional calculated fields
    private Boolean isAvailableForAllocation;

    // Related entities
    private Long roomId;
    private String roomNumber;
    private Long hostelId;
    private String hostelName;
    private HostelResidentDTO resident;
}
