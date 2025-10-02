package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Library;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

/**
 * DTO for Library book response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryResponse {

    private Long id;
    private String isbn;
    private String bookTitle;
    private String author;
    private String publisher;
    private String edition;
    private Integer publicationYear;
    private Library.BookCategory category;
    private String subject;
    private String language;
    private Integer totalPages;
    private String description;
    private String coverImageUrl;
    private String accessionNumber;
    private String shelfNumber;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer issuedCopies;
    private Double price;
    private Library.BookStatus status;
    private LocalDate purchaseDate;
    private Integer maxBorrowDays;
    private Double lateFeePerDay;
    private boolean isReferencOnly;
    private String notes;
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;

    // Computed fields
    private boolean isAvailable;
    private boolean isIssued;
    private boolean isReserved;
    private boolean isDamaged;
    private boolean isLost;
    private boolean isUnderRepair;
    private boolean isOutOfPrint;
    private String availabilityStatus;
    private String categoryDisplay;
    private String statusDisplay;
    private String borrowingRules;
    private String referenceStatus;
    private double availabilityPercentage;
    private String shelfLocation;
    private String bookCondition;
}
