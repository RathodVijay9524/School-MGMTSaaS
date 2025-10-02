package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.TransferCertificate;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferCertificateRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Last attendance date is required")
    @PastOrPresent(message = "Last attendance date cannot be in the future")
    private LocalDate lastAttendanceDate;
    
    @NotNull(message = "Reason for leaving is required")
    private TransferCertificate.ReasonForLeaving reasonForLeaving;
    
    @Size(max = 1000, message = "Reason details must not exceed 1000 characters")
    private String reasonDetails;
    
    private String lastClassStudied;
    
    private String lastClassPassed;
    
    private LocalDate dateOfPromotion;
    
    @NotBlank(message = "Academic year is required")
    private String academicYearOfLeaving;
    
    @NotNull(message = "Conduct rating is required")
    private TransferCertificate.ConductRating conduct;
    
    @Size(max = 1000, message = "Conduct remarks must not exceed 1000 characters")
    private String conductRemarks;
    
    @Size(max = 1000, message = "Character remarks must not exceed 1000 characters")
    private String characterRemarks;
    
    @Size(max = 1000, message = "General remarks must not exceed 1000 characters")
    private String generalRemarks;
    
    @Size(max = 1000, message = "Achievements must not exceed 1000 characters")
    private String achievements;
    
    private Long issuedByUserId;
}

