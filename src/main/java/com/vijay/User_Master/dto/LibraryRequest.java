package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Library;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Library book creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryRequest {

    // Book Information
    @NotBlank(message = "ISBN cannot be empty")
    @Pattern(regexp = "^[0-9-]{10,17}$", message = "Invalid ISBN format")
    @Size(max = 17, message = "ISBN cannot exceed 17 characters")
    private String isbn;

    @NotBlank(message = "Book title cannot be empty")
    @Size(max = 200, message = "Book title cannot exceed 200 characters")
    private String bookTitle;

    @NotBlank(message = "Author cannot be empty")
    @Size(max = 100, message = "Author name cannot exceed 100 characters")
    private String author;

    @Size(max = 100, message = "Publisher name cannot exceed 100 characters")
    private String publisher;

    @Size(max = 50, message = "Edition cannot exceed 50 characters")
    private String edition;

    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 2030, message = "Publication year cannot exceed 2030")
    private Integer publicationYear;

    @NotNull(message = "Book category is required")
    private Library.BookCategory category;

    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    private String subject;

    @Size(max = 50, message = "Language cannot exceed 50 characters")
    private String language;

    @Min(value = 1, message = "Total pages must be at least 1")
    private Integer totalPages;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Cover image URL cannot exceed 500 characters")
    private String coverImageUrl;

    // Library Management
    @NotBlank(message = "Accession number cannot be empty")
    @Size(max = 50, message = "Accession number cannot exceed 50 characters")
    private String accessionNumber;

    @Size(max = 50, message = "Shelf number cannot exceed 50 characters")
    private String shelfNumber;

    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies;

    @Min(value = 0, message = "Issued copies cannot be negative")
    private Integer issuedCopies;

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private Double price;

    @Builder.Default
    private Library.BookStatus status = Library.BookStatus.AVAILABLE;

    private LocalDate purchaseDate;

    // Borrowing Rules
    @Min(value = 1, message = "Max borrow days must be at least 1")
    private Integer maxBorrowDays;

    @DecimalMin(value = "0.0", message = "Late fee per day cannot be negative")
    private Double lateFeePerDay;

    @Builder.Default
    private boolean isReferencOnly = false;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
