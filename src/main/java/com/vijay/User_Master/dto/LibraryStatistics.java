package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Library statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryStatistics {

    private long totalBooks;
    private long availableBooks;
    private long issuedBooks;
    private long reservedBooks;
    private long damagedBooks;
    private long lostBooks;
    private long underRepairBooks;
    private long outOfPrintBooks;
    private long textbookBooks;
    private long referenceBooks;
    private long fictionBooks;
    private long nonFictionBooks;
    private long biographyBooks;
    private long scienceBooks;
    private long mathematicsBooks;
    private long historyBooks;
    private long geographyBooks;
    private long literatureBooks;
    private long magazineBooks;
    private long journalBooks;
    private long encyclopediaBooks;
    private long dictionaryBooks;
    private long referenceOnlyBooks;
    private long borrowableBooks;
    private long totalCopies;
    private long totalAvailableCopies;
    private long totalIssuedCopies;
    private double averagePrice;
    private double totalValue;
    private long booksByAuthor; // Example: count of books by a specific author
    private long booksByPublisher; // Example: count of books by a specific publisher
    private long booksBySubject; // Example: count of books for a specific subject
    private long booksByLanguage; // Example: count of books in a specific language
    private long recentlyAddedBooks; // Books added in last 30 days
    private long overdueBooks; // Books that are overdue
    private double averageBorrowDays;
    private double averageLateFee;
}
