package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.IDCard;
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
public class IDCardRequest {

    private Long studentId;
    
    private Long teacherId;
    
    @NotNull(message = "Card type is required")
    private IDCard.CardType cardType;
    
    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;
    
    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
    
    private String photoUrl;
    
    // For replacement cards
    private Long replacingCardId;
    
    @Size(max = 500, message = "Replacement reason must not exceed 500 characters")
    private String replacementReason;
    
    private Double replacementFee;
    
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
    
    private Long issuedByUserId;
}

