package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.IDCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IDCardResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Long teacherId;
    private String teacherName;
    private String employeeId;
    private IDCard.CardType cardType;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private IDCard.CardStatus status;
    private String photoUrl;
    private String barcodeData;
    private String qrCodeData;
    
    // Student info
    private String studentClass;
    private String section;
    private Integer rollNumber;
    private String bloodGroup;
    private String emergencyContactName;
    private String emergencyContactPhone;
    
    // Teacher info
    private String designation;
    private String department;
    
    // Common
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    
    // Replacement
    private Long replacedByCardId;
    private String replacementReason;
    private Double replacementFee;
    
    private String frontSideImageUrl;
    private String backSideImageUrl;
    private String pdfUrl;
    
    private String remarks;
    
    // Computed fields
    private boolean isExpired;
    private Integer daysToExpiry;
    private String cardTypeDisplay;
    private String statusDisplay;
}

