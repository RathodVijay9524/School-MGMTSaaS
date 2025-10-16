package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vijay.User_Master.entity.BookMaintenance;
import com.vijay.User_Master.entity.Library;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Book Maintenance response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookMaintenanceResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String isbn;
    private LocalDate maintenanceDate;
    private BookMaintenance.MaintenanceType maintenanceType;
    private BookMaintenance.MaintenanceStatus status;
    private String description;
    private String issuesFound;
    private String workPerformed;
    private Library.BookCondition conditionBefore;
    private Library.BookCondition conditionAfter;
    private Double maintenanceCost;
    private Long performedById;
    private String performedByName;
    private LocalDate completionDate;
    private Integer estimatedDays;
    private Integer actualDays;
    private String vendor;
    private String invoiceNumber;
    
    @JsonProperty("isWarrantyRepair")
    private boolean isWarrantyRepair;
    
    private LocalDate nextScheduledMaintenance;
    private String recommendations;
    private String remarks;
    
    // Computed fields
    private String maintenanceTypeDisplay;
    private String statusDisplay;
    private String conditionImprovement; // e.g., "POOR → GOOD"
    
    @JsonProperty("isOverdue")
    private boolean isOverdue;
    
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    
    private String durationDisplay; // e.g., "Completed in 3 days"
    private String costDisplay; // e.g., "$150.00"
}

