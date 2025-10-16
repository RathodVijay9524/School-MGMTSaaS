package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "library_books")
@EntityListeners(AuditingEntityListener.class)
public class Library extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Optimistic Locking - prevents concurrent update conflicts (e.g., book issue/return)
    @Version
    private Long version;

    // Book Information
    @Column(nullable = false, unique = true)
    private String isbn; // International Standard Book Number
    
    @Column(nullable = false)
    private String bookTitle;
    
    @Column(nullable = false)
    private String author;
    
    private String publisher;
    
    private String edition;
    
    private Integer publicationYear;
    
    @Enumerated(EnumType.STRING)
    private BookCategory category; // TEXTBOOK, REFERENCE, FICTION, NON_FICTION, MAGAZINE, JOURNAL
    
    private String subject;
    
    private String language;
    
    private Integer totalPages;
    
    @Column(length = 1000)
    private String description;
    
    private String coverImageUrl;
    
    // Library Management
    @Column(unique = true)
    private String accessionNumber; // Unique library book ID
    
    private String shelfNumber;
    
    private Integer totalCopies;
    
    private Integer availableCopies;
    
    private Integer issuedCopies;
    
    private Double price;
    
    @Enumerated(EnumType.STRING)
    private BookStatus status; // AVAILABLE, ISSUED, RESERVED, DAMAGED, LOST, UNDER_REPAIR
    
    private LocalDate purchaseDate;
    
    // Borrowing Rules
    private Integer maxBorrowDays; // Maximum days a book can be borrowed
    
    private Double lateFeePerDay;
    
    private boolean isReferenceOnly; // Cannot be issued, only read in library (TYPO FIXED)
    
    @Column(length = 500)
    private String notes;
    
    // ========== NEW FIELDS - BOOK CONDITION TRACKING ==========
    @Enumerated(EnumType.STRING)
    @lombok.Builder.Default
    private BookCondition bookCondition = BookCondition.GOOD; // Physical condition of book
    
    private LocalDate lastConditionCheckDate; // When was condition last inspected
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    // Soft Delete
    @lombok.Builder.Default
    private boolean isDeleted = false;

    public enum BookCategory {
        TEXTBOOK, REFERENCE, FICTION, NON_FICTION, BIOGRAPHY, SCIENCE, MATHEMATICS,
        HISTORY, GEOGRAPHY, LITERATURE, MAGAZINE, JOURNAL, ENCYCLOPEDIA, DICTIONARY
    }

    public enum BookStatus {
        AVAILABLE, ISSUED, RESERVED, DAMAGED, LOST, UNDER_REPAIR, OUT_OF_PRINT
    }
    
    public enum BookCondition {
        EXCELLENT, // Like new, perfect condition
        GOOD, // Normal wear and tear
        FAIR, // Noticeable wear, still readable
        POOR, // Significant damage but usable
        DAMAGED // Severe damage, may need repair
    }
}

