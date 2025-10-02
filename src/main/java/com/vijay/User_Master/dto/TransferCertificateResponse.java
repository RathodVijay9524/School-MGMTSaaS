package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.TransferCertificate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferCertificateResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String tcNumber;
    private LocalDate issueDate;
    private LocalDate lastAttendanceDate;
    private TransferCertificate.ReasonForLeaving reasonForLeaving;
    private String reasonDetails;
    private String lastClassStudied;
    private String lastClassPassed;
    private String academicYearOfLeaving;
    private String attendancePercentage;
    private String overallGPA;
    private String overallGrade;
    private TransferCertificate.ConductRating conduct;
    private String conductRemarks;
    private String characterRemarks;
    private boolean feeCleared;
    private Double pendingFeeAmount;
    private boolean libraryCleared;
    private Integer pendingBooks;
    private String generalRemarks;
    private String achievements;
    private TransferCertificate.TCStatus status;
    private LocalDate approvalDate;
    private String pdfUrl;
    private Long issuedByUserId;
    private String issuedByName;
    private Long approvedByUserId;
    private String approvedByName;
    
    // Computed fields
    private boolean canBeIssued; // All clearances done
    private String statusDisplay;
}

