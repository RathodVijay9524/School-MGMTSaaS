package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.LibraryRequest;
import com.vijay.User_Master.dto.LibraryResponse;
import com.vijay.User_Master.dto.LibraryStatistics;
import com.vijay.User_Master.entity.Library;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.LibraryRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.LibraryService;
import org.springframework.ai.tool.annotation.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Library book management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LibraryServiceImpl implements LibraryService {

    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;

    @Override
    @Tool(name = "createLibraryBook", description = "Create a new library book with title, author, ISBN, publisher and book details")
    public LibraryResponse createBook(LibraryRequest request, Long ownerId) {
        log.info("Creating library book: {} for owner: {}", request.getBookTitle(), ownerId);
        
        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Check if ISBN already exists for this owner
        if (libraryRepository.existsByIsbnAndOwner_Id(request.getIsbn(), ownerId)) {
            throw new RuntimeException("ISBN already exists: " + request.getIsbn());
        }
        
        // Check if accession number already exists for this owner
        if (libraryRepository.existsByAccessionNumberAndOwner_Id(request.getAccessionNumber(), ownerId)) {
            throw new RuntimeException("Accession number already exists: " + request.getAccessionNumber());
        }
        
        // Set default values
        Integer availableCopies = request.getAvailableCopies() != null ? request.getAvailableCopies() : request.getTotalCopies();
        Integer issuedCopies = request.getIssuedCopies() != null ? request.getIssuedCopies() : 0;
        
        Library library = Library.builder()
                .isbn(request.getIsbn())
                .bookTitle(request.getBookTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .edition(request.getEdition())
                .publicationYear(request.getPublicationYear())
                .category(request.getCategory())
                .subject(request.getSubject())
                .language(request.getLanguage())
                .totalPages(request.getTotalPages())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .accessionNumber(request.getAccessionNumber())
                .shelfNumber(request.getShelfNumber())
                .totalCopies(request.getTotalCopies())
                .availableCopies(availableCopies)
                .issuedCopies(issuedCopies)
                .price(request.getPrice())
                .status(request.getStatus())
                .purchaseDate(request.getPurchaseDate())
                .maxBorrowDays(request.getMaxBorrowDays())
                .lateFeePerDay(request.getLateFeePerDay())
                .isReferenceOnly(request.isReferenceOnly())
                .notes(request.getNotes())
                // CONDITION TRACKING
                .bookCondition(request.getBookCondition())
                .lastConditionCheckDate(request.getLastConditionCheckDate())
                // MAINTENANCE TRACKING
                .lastMaintenanceDate(request.getLastMaintenanceDate())
                .nextMaintenanceDate(request.getNextMaintenanceDate())
                .requiresMaintenance(request.isRequiresMaintenance())
                .maintenanceCount(request.getMaintenanceCount())
                .lastMaintenanceNotes(request.getLastMaintenanceNotes())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Library savedBook = libraryRepository.save(library);
        log.info("Library book created successfully with ID: {}", savedBook.getId());
        
        return convertToResponse(savedBook);
    }

    @Override
    @Tool(name = "updateLibraryBook", description = "Update library book details")
    public LibraryResponse updateBook(Long id, LibraryRequest request, Long ownerId) {
        log.info("Updating library book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Update fields
        library.setIsbn(request.getIsbn());
        library.setBookTitle(request.getBookTitle());
        library.setAuthor(request.getAuthor());
        library.setPublisher(request.getPublisher());
        library.setEdition(request.getEdition());
        library.setPublicationYear(request.getPublicationYear());
        library.setCategory(request.getCategory());
        library.setSubject(request.getSubject());
        library.setLanguage(request.getLanguage());
        library.setTotalPages(request.getTotalPages());
        library.setDescription(request.getDescription());
        library.setCoverImageUrl(request.getCoverImageUrl());
        library.setAccessionNumber(request.getAccessionNumber());
        library.setShelfNumber(request.getShelfNumber());
        library.setTotalCopies(request.getTotalCopies());
        library.setAvailableCopies(request.getAvailableCopies());
        library.setIssuedCopies(request.getIssuedCopies());
        library.setPrice(request.getPrice());
        library.setStatus(request.getStatus());
        library.setPurchaseDate(request.getPurchaseDate());
        library.setMaxBorrowDays(request.getMaxBorrowDays());
        library.setLateFeePerDay(request.getLateFeePerDay());
        library.setReferenceOnly(request.isReferenceOnly());
        library.setNotes(request.getNotes());
        // CONDITION TRACKING
        library.setBookCondition(request.getBookCondition());
        library.setLastConditionCheckDate(request.getLastConditionCheckDate());
        // MAINTENANCE TRACKING
        library.setLastMaintenanceDate(request.getLastMaintenanceDate());
        library.setNextMaintenanceDate(request.getNextMaintenanceDate());
        library.setRequiresMaintenance(request.isRequiresMaintenance());
        library.setMaintenanceCount(request.getMaintenanceCount());
        library.setLastMaintenanceNotes(request.getLastMaintenanceNotes());
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Library book updated successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public LibraryResponse getBookById(Long id, Long ownerId) {
        log.info("Getting library book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        return convertToResponse(library);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryResponse> getAllBooks(Long ownerId, Pageable pageable) {
        log.info("Getting all library books for owner: {}", ownerId);
        
        Page<Library> books = libraryRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        return books.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryResponse> getBooksByCategory(Library.BookCategory category, Long ownerId, Pageable pageable) {
        log.info("Getting books for category: {} and owner: {}", category, ownerId);
        
        Page<Library> books = libraryRepository.findByOwner_IdAndCategoryAndIsDeletedFalse(ownerId, category, pageable);
        return books.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryResponse> getBooksByStatus(Library.BookStatus status, Long ownerId, Pageable pageable) {
        log.info("Getting books for status: {} and owner: {}", status, ownerId);
        
        Page<Library> books = libraryRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, status, pageable);
        return books.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryResponse> getAvailableBooks(Long ownerId, Pageable pageable) {
        log.info("Getting available books for owner: {}", ownerId);
        
        Page<Library> books = libraryRepository.findAvailableBooksByOwner(ownerId, pageable);
        return books.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getBooksByAuthor(String author, Long ownerId) {
        log.info("Getting books for author: {} and owner: {}", author, ownerId);
        
        List<Library> books = libraryRepository.findByOwner_IdAndAuthorAndIsDeletedFalse(ownerId, author);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getBooksBySubject(String subject, Long ownerId) {
        log.info("Getting books for subject: {} and owner: {}", subject, ownerId);
        
        List<Library> books = libraryRepository.findByOwner_IdAndSubjectAndIsDeletedFalse(ownerId, subject);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getBooksByPublisher(String publisher, Long ownerId) {
        log.info("Getting books for publisher: {} and owner: {}", publisher, ownerId);
        
        List<Library> books = libraryRepository.findByOwner_IdAndPublisherAndIsDeletedFalse(ownerId, publisher);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getBooksByLanguage(String language, Long ownerId) {
        log.info("Getting books for language: {} and owner: {}", language, ownerId);
        
        List<Library> books = libraryRepository.findByOwner_IdAndLanguageAndIsDeletedFalse(ownerId, language);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getReferenceOnlyBooks(Long ownerId) {
        log.info("Getting reference only books for owner: {}", ownerId);
        
        List<Library> books = libraryRepository.findReferenceOnlyBooksByOwner(ownerId);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getBorrowableBooks(Long ownerId) {
        log.info("Getting borrowable books for owner: {}", ownerId);
        
        List<Library> books = libraryRepository.findBorrowableBooksByOwner(ownerId);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getBooksByPurchaseDateRange(LocalDate startDate, LocalDate endDate, Long ownerId) {
        log.info("Getting books for purchase date range: {} to {} for owner: {}", startDate, endDate, ownerId);
        
        List<Library> books = libraryRepository.findBooksByOwnerAndPurchaseDateRange(ownerId, startDate, endDate);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getBooksByShelfNumber(String shelfNumber, Long ownerId) {
        log.info("Getting books for shelf number: {} and owner: {}", shelfNumber, ownerId);
        
        List<Library> books = libraryRepository.findByOwner_IdAndShelfNumberAndIsDeletedFalse(ownerId, shelfNumber);
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryResponse> searchBooks(String keyword, Long ownerId, Pageable pageable) {
        log.info("Searching books with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Library> books = libraryRepository.searchBooksByOwner(ownerId, keyword, pageable);
        return books.map(this::convertToResponse);
    }

    @Override
    public void deleteBook(Long id, Long ownerId) {
        log.info("Deleting library book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        library.setDeleted(true);
        libraryRepository.save(library);
        
        log.info("Library book deleted successfully");
    }

    @Override
    public void restoreBook(Long id, Long ownerId) {
        log.info("Restoring library book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        library.setDeleted(false);
        libraryRepository.save(library);
        
        log.info("Library book restored successfully");
    }

    @Override
    public LibraryResponse issueBook(Long id, Long ownerId) {
        log.info("Issuing book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (library.isReferenceOnly()) {
            throw new RuntimeException("Reference books cannot be issued");
        }
        
        if (library.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available for issuing");
        }
        
        library.setAvailableCopies(library.getAvailableCopies() - 1);
        library.setIssuedCopies(library.getIssuedCopies() + 1);
        
        if (library.getAvailableCopies() == 0) {
            library.setStatus(Library.BookStatus.ISSUED);
        }
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book issued successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    public LibraryResponse returnBook(Long id, Long ownerId) {
        log.info("Returning book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (library.getIssuedCopies() <= 0) {
            throw new RuntimeException("No copies to return");
        }
        
        library.setAvailableCopies(library.getAvailableCopies() + 1);
        library.setIssuedCopies(library.getIssuedCopies() - 1);
        
        if (library.getAvailableCopies() > 0) {
            library.setStatus(Library.BookStatus.AVAILABLE);
        }
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book returned successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    public LibraryResponse reserveBook(Long id, Long ownerId) {
        log.info("Reserving book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (library.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available for reservation");
        }
        
        library.setStatus(Library.BookStatus.RESERVED);
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book reserved successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    public LibraryResponse cancelReservation(Long id, Long ownerId) {
        log.info("Cancelling reservation for book: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (library.getStatus() != Library.BookStatus.RESERVED) {
            throw new RuntimeException("Book is not reserved");
        }
        
        library.setStatus(Library.BookStatus.AVAILABLE);
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book reservation cancelled successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    public LibraryResponse markBookAsDamaged(Long id, String damageDescription, Long ownerId) {
        log.info("Marking book as damaged: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        library.setStatus(Library.BookStatus.DAMAGED);
        library.setNotes(library.getNotes() + "\nDamaged: " + damageDescription);
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book marked as damaged successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    public LibraryResponse markBookAsLost(Long id, String lossDescription, Long ownerId) {
        log.info("Marking book as lost: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        library.setStatus(Library.BookStatus.LOST);
        library.setNotes(library.getNotes() + "\nLost: " + lossDescription);
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book marked as lost successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    public LibraryResponse markBookAsUnderRepair(Long id, String repairDescription, Long ownerId) {
        log.info("Marking book as under repair: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        library.setStatus(Library.BookStatus.UNDER_REPAIR);
        library.setNotes(library.getNotes() + "\nUnder repair: " + repairDescription);
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book marked as under repair successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    public LibraryResponse updateBookCopies(Long id, Integer totalCopies, Integer availableCopies, Integer issuedCopies, Long ownerId) {
        log.info("Updating book copies: {} for owner: {}", id, ownerId);
        
        Library library = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        library.setTotalCopies(totalCopies);
        library.setAvailableCopies(availableCopies);
        library.setIssuedCopies(issuedCopies);
        
        // Update status based on available copies
        if (availableCopies > 0) {
            library.setStatus(Library.BookStatus.AVAILABLE);
        } else if (issuedCopies > 0) {
            library.setStatus(Library.BookStatus.ISSUED);
        } else {
            library.setStatus(Library.BookStatus.OUT_OF_PRINT);
        }
        
        Library updatedBook = libraryRepository.save(library);
        log.info("Book copies updated successfully");
        
        return convertToResponse(updatedBook);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(description = "Get comprehensive library statistics including total books, available, issued, and category breakdown")
    public LibraryStatistics getLibraryStatistics(Long ownerId) {
        log.info("Getting library statistics for owner: {}", ownerId);
        
        long totalBooks = libraryRepository.countByOwner_IdAndIsDeletedFalse(ownerId);
        
        // Count by status
        long availableBooks = libraryRepository.countAvailableBooksByOwner(ownerId);
        long issuedBooks = libraryRepository.countIssuedBooksByOwner(ownerId);
        
        // Get all books for detailed statistics
        List<Library> allBooks = libraryRepository.findByOwner_IdAndIsDeletedFalse(ownerId, Pageable.unpaged()).getContent();
        
        long reservedBooks = allBooks.stream().filter(b -> b.getStatus() == Library.BookStatus.RESERVED).count();
        long damagedBooks = allBooks.stream().filter(b -> b.getStatus() == Library.BookStatus.DAMAGED).count();
        long lostBooks = allBooks.stream().filter(b -> b.getStatus() == Library.BookStatus.LOST).count();
        long underRepairBooks = allBooks.stream().filter(b -> b.getStatus() == Library.BookStatus.UNDER_REPAIR).count();
        long outOfPrintBooks = allBooks.stream().filter(b -> b.getStatus() == Library.BookStatus.OUT_OF_PRINT).count();
        
        // Count by category
        long textbookBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.TEXTBOOK).count();
        long referenceBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.REFERENCE).count();
        long fictionBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.FICTION).count();
        long nonFictionBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.NON_FICTION).count();
        long biographyBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.BIOGRAPHY).count();
        long scienceBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.SCIENCE).count();
        long mathematicsBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.MATHEMATICS).count();
        long historyBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.HISTORY).count();
        long geographyBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.GEOGRAPHY).count();
        long literatureBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.LITERATURE).count();
        long magazineBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.MAGAZINE).count();
        long journalBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.JOURNAL).count();
        long encyclopediaBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.ENCYCLOPEDIA).count();
        long dictionaryBooks = allBooks.stream().filter(b -> b.getCategory() == Library.BookCategory.DICTIONARY).count();
        
        // Count reference only and borrowable
        long referenceOnlyBooks = allBooks.stream().filter(Library::isReferenceOnly).count();
        long borrowableBooks = allBooks.stream().filter(b -> !b.isReferenceOnly()).count();
        
        // Calculate totals
        long totalCopies = allBooks.stream().mapToInt(Library::getTotalCopies).sum();
        long totalAvailableCopies = allBooks.stream().mapToInt(Library::getAvailableCopies).sum();
        long totalIssuedCopies = allBooks.stream().mapToInt(Library::getIssuedCopies).sum();
        
        // Calculate averages
        double averagePrice = allBooks.stream()
                .filter(b -> b.getPrice() != null)
                .mapToDouble(Library::getPrice)
                .average()
                .orElse(0.0);
        
        double totalValue = allBooks.stream()
                .filter(b -> b.getPrice() != null)
                .mapToDouble(b -> b.getPrice() * b.getTotalCopies())
                .sum();
        
        double averageBorrowDays = allBooks.stream()
                .filter(b -> b.getMaxBorrowDays() != null)
                .mapToInt(Library::getMaxBorrowDays)
                .average()
                .orElse(0.0);
        
        double averageLateFee = allBooks.stream()
                .filter(b -> b.getLateFeePerDay() != null)
                .mapToDouble(Library::getLateFeePerDay)
                .average()
                .orElse(0.0);
        
        // Count recently added books (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long recentlyAddedBooks = allBooks.stream()
                .filter(b -> b.getPurchaseDate() != null && b.getPurchaseDate().isAfter(thirtyDaysAgo))
                .count();
        
        return LibraryStatistics.builder()
                .totalBooks(totalBooks)
                .availableBooks(availableBooks)
                .issuedBooks(issuedBooks)
                .reservedBooks(reservedBooks)
                .damagedBooks(damagedBooks)
                .lostBooks(lostBooks)
                .underRepairBooks(underRepairBooks)
                .outOfPrintBooks(outOfPrintBooks)
                .textbookBooks(textbookBooks)
                .referenceBooks(referenceBooks)
                .fictionBooks(fictionBooks)
                .nonFictionBooks(nonFictionBooks)
                .biographyBooks(biographyBooks)
                .scienceBooks(scienceBooks)
                .mathematicsBooks(mathematicsBooks)
                .historyBooks(historyBooks)
                .geographyBooks(geographyBooks)
                .literatureBooks(literatureBooks)
                .magazineBooks(magazineBooks)
                .journalBooks(journalBooks)
                .encyclopediaBooks(encyclopediaBooks)
                .dictionaryBooks(dictionaryBooks)
                .referenceOnlyBooks(referenceOnlyBooks)
                .borrowableBooks(borrowableBooks)
                .totalCopies(totalCopies)
                .totalAvailableCopies(totalAvailableCopies)
                .totalIssuedCopies(totalIssuedCopies)
                .averagePrice(averagePrice)
                .totalValue(totalValue)
                .recentlyAddedBooks(recentlyAddedBooks)
                .averageBorrowDays(averageBorrowDays)
                .averageLateFee(averageLateFee)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryResponse> getRecentlyAddedBooks(Long ownerId) {
        log.info("Getting recently added books for owner: {}", ownerId);
        
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Library> books = libraryRepository.findBooksByOwnerAndPurchaseDateRange(ownerId, thirtyDaysAgo, LocalDate.now());
        return books.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private LibraryResponse convertToResponse(Library library) {
        boolean isAvailable = library.getStatus() == Library.BookStatus.AVAILABLE;
        boolean isIssued = library.getStatus() == Library.BookStatus.ISSUED;
        boolean isReserved = library.getStatus() == Library.BookStatus.RESERVED;
        boolean isDamaged = library.getStatus() == Library.BookStatus.DAMAGED;
        boolean isLost = library.getStatus() == Library.BookStatus.LOST;
        boolean isUnderRepair = library.getStatus() == Library.BookStatus.UNDER_REPAIR;
        boolean isOutOfPrint = library.getStatus() == Library.BookStatus.OUT_OF_PRINT;
        
        String availabilityStatus = library.getAvailableCopies() > 0 ? "Available" : "Not Available";
        String categoryDisplay = library.getCategory().name().replace("_", " ");
        String statusDisplay = library.getStatus().name().replace("_", " ");
        
        String borrowingRules = library.getMaxBorrowDays() != null ? 
                String.format("Max %d days, Late fee: $%.2f/day", library.getMaxBorrowDays(), library.getLateFeePerDay() != null ? library.getLateFeePerDay() : 0.0) : 
                "No borrowing rules set";
        
        String referenceStatus = library.isReferenceOnly() ? "Reference Only" : "Available for Borrowing";
        
        double availabilityPercentage = library.getTotalCopies() > 0 ? 
                (double) library.getAvailableCopies() / library.getTotalCopies() * 100 : 0.0;
        
        String shelfLocation = library.getShelfNumber() != null ? "Shelf " + library.getShelfNumber() : "Location not set";
        
        // NEW: Book condition display for UI
        String bookConditionDisplay = library.getBookCondition() != null ? 
                library.getBookCondition().name().replace("_", " ") : "GOOD";
        
        // ========== MAINTENANCE TRACKING COMPUTED FIELDS ==========
        boolean needsMaintenanceSoon = library.needsMaintenanceSoon();
        boolean isMaintenanceOverdue = library.isMaintenanceOverdue();
        
        // Calculate days since last maintenance
        Integer daysSinceLastMaintenance = null;
        if (library.getLastMaintenanceDate() != null) {
            daysSinceLastMaintenance = (int) java.time.temporal.ChronoUnit.DAYS.between(
                library.getLastMaintenanceDate(), LocalDate.now());
        }
        
        // Calculate days until next maintenance
        Integer daysUntilNextMaintenance = null;
        if (library.getNextMaintenanceDate() != null) {
            daysUntilNextMaintenance = (int) java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), library.getNextMaintenanceDate());
        }
        
        // Build maintenance status message
        String maintenanceStatus;
        if (isMaintenanceOverdue && library.getNextMaintenanceDate() != null) {
            int daysOverdue = Math.abs(daysUntilNextMaintenance);
            maintenanceStatus = String.format("⚠️ Overdue by %d day%s", daysOverdue, daysOverdue != 1 ? "s" : "");
        } else if (needsMaintenanceSoon && library.getNextMaintenanceDate() != null) {
            maintenanceStatus = String.format("⏰ Due in %d day%s", daysUntilNextMaintenance, daysUntilNextMaintenance != 1 ? "s" : "");
        } else if (library.getNextMaintenanceDate() == null) {
            maintenanceStatus = "No maintenance scheduled";
        } else {
            maintenanceStatus = String.format("✅ Scheduled in %d day%s", daysUntilNextMaintenance, daysUntilNextMaintenance != 1 ? "s" : "");
        }
        
        return LibraryResponse.builder()
                .id(library.getId())
                .isbn(library.getIsbn())
                .bookTitle(library.getBookTitle())
                .author(library.getAuthor())
                .publisher(library.getPublisher())
                .edition(library.getEdition())
                .publicationYear(library.getPublicationYear())
                .category(library.getCategory())
                .subject(library.getSubject())
                .language(library.getLanguage())
                .totalPages(library.getTotalPages())
                .description(library.getDescription())
                .coverImageUrl(library.getCoverImageUrl())
                .accessionNumber(library.getAccessionNumber())
                .shelfNumber(library.getShelfNumber())
                .totalCopies(library.getTotalCopies())
                .availableCopies(library.getAvailableCopies())
                .issuedCopies(library.getIssuedCopies())
                .price(library.getPrice())
                .status(library.getStatus())
                .purchaseDate(library.getPurchaseDate())
                .maxBorrowDays(library.getMaxBorrowDays())
                .lateFeePerDay(library.getLateFeePerDay())
                .isReferenceOnly(library.isReferenceOnly())
                .notes(library.getNotes())
                .isDeleted(library.isDeleted())
                .createdOn(library.getCreatedOn())
                .updatedOn(library.getUpdatedOn())
                // CONDITION TRACKING
                .bookCondition(library.getBookCondition())
                .lastConditionCheckDate(library.getLastConditionCheckDate())
                // MAINTENANCE TRACKING
                .lastMaintenanceDate(library.getLastMaintenanceDate())
                .nextMaintenanceDate(library.getNextMaintenanceDate())
                .requiresMaintenance(library.isRequiresMaintenance())
                .maintenanceCount(library.getMaintenanceCount())
                .lastMaintenanceNotes(library.getLastMaintenanceNotes())
                // Note: maintenanceRecords list is lazy-loaded, not included here to avoid N+1 queries
                // Use dedicated endpoint to fetch maintenance history for a book
                // Computed fields
                .isAvailable(isAvailable)
                .isIssued(isIssued)
                .isReserved(isReserved)
                .isDamaged(isDamaged)
                .isLost(isLost)
                .isUnderRepair(isUnderRepair)
                .isOutOfPrint(isOutOfPrint)
                .availabilityStatus(availabilityStatus)
                .categoryDisplay(categoryDisplay)
                .statusDisplay(statusDisplay)
                .borrowingRules(borrowingRules)
                .referenceStatus(referenceStatus)
                .availabilityPercentage(availabilityPercentage)
                .shelfLocation(shelfLocation)
                .bookConditionDisplay(bookConditionDisplay)
                // MAINTENANCE COMPUTED FIELDS
                .needsMaintenanceSoon(needsMaintenanceSoon)
                .isMaintenanceOverdue(isMaintenanceOverdue)
                .maintenanceStatus(maintenanceStatus)
                .daysSinceLastMaintenance(daysSinceLastMaintenance)
                .daysUntilNextMaintenance(daysUntilNextMaintenance)
                .build();
    }
}
