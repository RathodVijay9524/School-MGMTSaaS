package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.BookIssueRequest;
import com.vijay.User_Master.dto.BookIssueResponse;
import com.vijay.User_Master.dto.BookIssueStatistics;
import com.vijay.User_Master.entity.BookIssue;
import com.vijay.User_Master.entity.Library;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.BusinessRuleViolationException;
import com.vijay.User_Master.exceptions.EntityNotFoundException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.BookIssueRepository;
import com.vijay.User_Master.repository.LibraryRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.BookIssueService;
import com.vijay.User_Master.service.SchoolNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for Book Issue management with fine calculation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookIssueServiceImpl implements BookIssueService {

    private final BookIssueRepository bookIssueRepository;
    private final LibraryRepository libraryRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final SchoolNotificationService notificationService;

    private static final int MAX_BOOKS_PER_STUDENT = 3;
    private static final int MAX_BOOKS_PER_TEACHER = 5;
    private static final int MAX_RENEWAL_COUNT = 2;

    @Override
    @Tool(name = "issueBook", description = "Issue a book to a student or teacher from the library")
    public BookIssueResponse issueBook(BookIssueRequest request, Long ownerId) {
        log.info("Issuing book {} to borrower", request.getBookId());
        
        if (!request.isValid()) {
            throw new BusinessRuleViolationException("Either student ID or teacher ID must be provided (not both)");
        }
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner", ownerId));
        
        Library book = libraryRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getBookId(), ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));
        
        if (book.isReferenceOnly()) {
            throw new BusinessRuleViolationException("This book is reference only and cannot be issued");
        }
        
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BusinessRuleViolationException("Book is not available for issue");
        }
        
        boolean isStudent = request.getStudentId() != null;
        Long borrowerId = isStudent ? request.getStudentId() : request.getTeacherId();
        
        Worker borrower = workerRepository.findById(borrowerId)
                .filter(w -> w.getOwner() != null && w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower", "id", borrowerId));
        
        long activeBorrowCount = getActiveBorrowCount(borrowerId, isStudent, ownerId);
        int maxBooks = isStudent ? MAX_BOOKS_PER_STUDENT : MAX_BOOKS_PER_TEACHER;
        
        if (activeBorrowCount >= maxBooks) {
            throw new BusinessRuleViolationException(String.format("Borrower has reached maximum limit of %d books", maxBooks));
        }
        
        LocalDate dueDate = request.getIssueDate().plusDays(book.getMaxBorrowDays() != null ? book.getMaxBorrowDays() : 14);
        
        BookIssue bookIssue = BookIssue.builder()
                .book(book)
                .student(isStudent ? borrower : null)
                .teacher(!isStudent ? borrower : null)
                .issueDate(request.getIssueDate())
                .dueDate(dueDate)
                .status(BookIssue.IssueStatus.ISSUED)
                .bookConditionOnIssue(request.getBookConditionOnIssue() != null ? 
                        request.getBookConditionOnIssue() : BookIssue.BookCondition.GOOD)
                .issueRemarks(request.getIssueRemarks())
                .issuedBy(owner)
                .renewalCount(0)
                .fineCollected(false)
                .owner(owner)
                .build();
        
        try {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            book.setIssuedCopies((book.getIssuedCopies() != null ? book.getIssuedCopies() : 0) + 1);
            libraryRepository.save(book);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessRuleViolationException("Book was modified by another transaction. Please try again.");
        }
        
        BookIssue savedIssue = bookIssueRepository.save(bookIssue);
        log.info("Book issued successfully with ID: {}", savedIssue.getId());
        
        return convertToResponse(savedIssue);
    }

    @Override
    @Tool(name = "returnBook", description = "Return a borrowed book to the library")
    public BookIssueResponse returnBook(Long issueId, BookIssueRequest returnDetails, Long ownerId) {
        log.info("Returning book for issue ID: {}", issueId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner", ownerId));
        
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("BookIssue", issueId));
        
        if (bookIssue.getStatus() == BookIssue.IssueStatus.RETURNED) {
            throw new BusinessRuleViolationException("Book has already been returned");
        }
        
        Library book = bookIssue.getBook();
        
        bookIssue.setReturnDate(returnDetails.getReturnDate() != null ? returnDetails.getReturnDate() : LocalDate.now());
        bookIssue.setBookConditionOnReturn(returnDetails.getBookConditionOnReturn() != null ?
                returnDetails.getBookConditionOnReturn() : BookIssue.BookCondition.GOOD);
        bookIssue.setReturnRemarks(returnDetails.getReturnRemarks());
        bookIssue.setReturnedTo(owner);
        
        if (bookIssue.getReturnDate().isAfter(bookIssue.getDueDate())) {
            int daysOverdue = (int) ChronoUnit.DAYS.between(bookIssue.getDueDate(), bookIssue.getReturnDate());
            bookIssue.setDaysOverdue(daysOverdue);
            
            Double lateFeePerDay = book.getLateFeePerDay() != null ? book.getLateFeePerDay() : 5.0;
            bookIssue.setLateFee(daysOverdue * lateFeePerDay);
            bookIssue.setStatus(BookIssue.IssueStatus.OVERDUE);
        } else {
            bookIssue.setStatus(BookIssue.IssueStatus.RETURNED);
        }
        
        if (returnDetails.getDamageFee() != null && returnDetails.getDamageFee() > 0) {
            bookIssue.setDamageFee(returnDetails.getDamageFee());
            bookIssue.setStatus(BookIssue.IssueStatus.DAMAGED);
        }
        
        try {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.setIssuedCopies(book.getIssuedCopies() - 1);
            libraryRepository.save(book);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessRuleViolationException("Book was modified by another transaction. Please try again.");
        }
        
        BookIssue updatedIssue = bookIssueRepository.save(bookIssue);
        log.info("Book returned successfully");
        
        return convertToResponse(updatedIssue);
    }

    @Override
    @Tool(name = "renewBook", description = "Renew a borrowed book to extend the due date")
    public BookIssueResponse renewBook(Long issueId, Integer additionalDays, Long ownerId) {
        log.info("Renewing book for issue ID: {}", issueId);
        
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("BookIssue", issueId));
        
        if (bookIssue.getStatus() != BookIssue.IssueStatus.ISSUED) {
            throw new BusinessRuleViolationException("Only issued books can be renewed");
        }
        
        int renewalCount = bookIssue.getRenewalCount() != null ? bookIssue.getRenewalCount() : 0;
        if (renewalCount >= MAX_RENEWAL_COUNT) {
            throw new BusinessRuleViolationException(String.format("Book has reached maximum renewal limit of %d times", MAX_RENEWAL_COUNT));
        }
        
        if (LocalDate.now().isAfter(bookIssue.getDueDate())) {
            throw new BusinessRuleViolationException("Cannot renew overdue books");
        }
        
        int days = additionalDays != null ? additionalDays : 
                (bookIssue.getBook().getMaxBorrowDays() != null ? bookIssue.getBook().getMaxBorrowDays() : 14);
        
        bookIssue.setDueDate(bookIssue.getDueDate().plusDays(days));
        bookIssue.setRenewalCount(renewalCount + 1);
        bookIssue.setLastRenewalDate(LocalDate.now());
        bookIssue.setStatus(BookIssue.IssueStatus.RENEWED);
        
        BookIssue renewedIssue = bookIssueRepository.save(bookIssue);
        log.info("Book renewed successfully");
        
        return convertToResponse(renewedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public BookIssueResponse getBookIssueById(Long id, Long ownerId) {
        BookIssue bookIssue = bookIssueRepository.findById(id)
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("BookIssue", id));
        return convertToResponse(bookIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookIssueResponse> getAllBookIssues(Long ownerId, Pageable pageable) {
        return bookIssueRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookIssueResponse> getBookIssuesByStudent(Long studentId, Long ownerId, Pageable pageable) {
        return bookIssueRepository.findByStudent_Id(studentId, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookIssueResponse> getBookIssuesByTeacher(Long teacherId, Long ownerId, Pageable pageable) {
        return bookIssueRepository.findByTeacher_Id(teacherId, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getBookIssuesByBook(Long bookId, Long ownerId) {
        return bookIssueRepository.findByBook_Id(bookId).stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookIssueResponse> getBookIssuesByStatus(BookIssue.IssueStatus status, Long ownerId, Pageable pageable) {
        return bookIssueRepository.findByStatus(status, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookIssueResponse> getCurrentlyIssuedBooks(Long ownerId, Pageable pageable) {
        return bookIssueRepository.findByStatus(BookIssue.IssueStatus.ISSUED, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getOverdueBooks(Long ownerId) {
        return bookIssueRepository.findOverdueBooks(LocalDate.now()).stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getBooksDueToday(Long ownerId) {
        LocalDate today = LocalDate.now();
        return bookIssueRepository.findAll().stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId) &&
                        bi.getStatus() == BookIssue.IssueStatus.ISSUED &&
                        bi.getDueDate().equals(today))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getBookIssuesByDateRange(LocalDate startDate, LocalDate endDate, Long ownerId) {
        return bookIssueRepository.findByIssueDateBetween(startDate, endDate).stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateFine(Long issueId, Long ownerId) {
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("BookIssue", issueId));
        
        Double totalFine = 0.0;
        if (bookIssue.getLateFee() != null) totalFine += bookIssue.getLateFee();
        if (bookIssue.getDamageFee() != null) totalFine += bookIssue.getDamageFee();
        
        return totalFine;
    }

    @Override
    public BookIssueResponse collectFine(Long issueId, Long ownerId) {
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("BookIssue", issueId));
        
        if (bookIssue.isFineCollected()) {
            throw new BusinessRuleViolationException("Fine has already been collected");
        }
        
        Double totalFine = calculateFine(issueId, ownerId);
        if (totalFine <= 0) {
            throw new BusinessRuleViolationException("No fine to collect");
        }
        
        bookIssue.setFineCollected(true);
        BookIssue updatedIssue = bookIssueRepository.save(bookIssue);
        
        log.info("Fine collected: Rs. {} for issue ID: {}", totalFine, issueId);
        return convertToResponse(updatedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getIssuesWithPendingFines(Long ownerId) {
        return bookIssueRepository.findAll().stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId) &&
                        !bi.isFineCollected() &&
                        ((bi.getLateFee() != null && bi.getLateFee() > 0) ||
                         (bi.getDamageFee() != null && bi.getDamageFee() > 0)))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookIssueResponse markBookAsLost(Long issueId, String remarks, Long ownerId) {
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("BookIssue", issueId));
        
        bookIssue.setStatus(BookIssue.IssueStatus.LOST);
        bookIssue.setReturnRemarks(remarks);
        
        Library book = bookIssue.getBook();
        if (book.getPrice() != null) {
            bookIssue.setDamageFee(book.getPrice());
        }
        
        BookIssue updatedIssue = bookIssueRepository.save(bookIssue);
        log.info("Book marked as lost for issue ID: {}", issueId);
        
        return convertToResponse(updatedIssue);
    }

    @Override
    public BookIssueResponse markBookAsDamaged(Long issueId, Double damageFee, String remarks, Long ownerId) {
        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("BookIssue", issueId));
        
        bookIssue.setStatus(BookIssue.IssueStatus.DAMAGED);
        bookIssue.setDamageFee(damageFee);
        bookIssue.setReturnRemarks(remarks);
        
        BookIssue updatedIssue = bookIssueRepository.save(bookIssue);
        log.info("Book marked as damaged for issue ID: {}", issueId);
        
        return convertToResponse(updatedIssue);
    }

    @Override
    public void checkAndUpdateOverdueBooks() {
        LocalDate today = LocalDate.now();
        List<BookIssue> overdueBooks = bookIssueRepository.findOverdueBooks(today);
        
        overdueBooks.forEach(bookIssue -> {
            if (bookIssue.getStatus() == BookIssue.IssueStatus.ISSUED) {
                int daysOverdue = (int) ChronoUnit.DAYS.between(bookIssue.getDueDate(), today);
                bookIssue.setDaysOverdue(daysOverdue);
                bookIssue.setStatus(BookIssue.IssueStatus.OVERDUE);
                
                Library book = bookIssue.getBook();
                Double lateFeePerDay = book.getLateFeePerDay() != null ? book.getLateFeePerDay() : 5.0;
                bookIssue.setLateFee(daysOverdue * lateFeePerDay);
            }
        });
        
        bookIssueRepository.saveAll(overdueBooks);
        log.info("Updated {} overdue books", overdueBooks.size());
    }

    @Override
    public void sendOverdueNotifications() {
        List<BookIssue> overdueBooks = bookIssueRepository.findOverdueBooks(LocalDate.now());
        
        overdueBooks.forEach(bookIssue -> {
            try {
                notificationService.sendLibraryOverdueNotice(bookIssue.getId());
            } catch (Exception e) {
                log.error("Error sending overdue notification for issue ID: {}", bookIssue.getId(), e);
            }
        });
        
        log.info("Sent overdue notifications for {} books", overdueBooks.size());
    }

    @Override
    @Transactional(readOnly = true)
    public BookIssueStatistics getBookIssueStatistics(Long ownerId) {
        List<BookIssue> allIssues = bookIssueRepository.findAll().stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
        
        long totalIssues = allIssues.size();
        long activeIssues = allIssues.stream().filter(bi -> bi.getStatus() == BookIssue.IssueStatus.ISSUED).count();
        long returnedIssues = allIssues.stream().filter(bi -> bi.getStatus() == BookIssue.IssueStatus.RETURNED).count();
        
        Double totalPendingFines = bookIssueRepository.calculateTotalPendingFines();
        
        Map<String, Long> statusMap = allIssues.stream()
                .collect(Collectors.groupingBy(bi -> bi.getStatus().name(), Collectors.counting()));
        
        return BookIssueStatistics.builder()
                .totalIssues(totalIssues)
                .activeIssues(activeIssues)
                .returnedIssues(returnedIssues)
                .totalPendingFines(totalPendingFines != null ? totalPendingFines : 0.0)
                .issuesByStatus(statusMap)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getBorrowerHistory(Long borrowerId, boolean isStudent, Long ownerId) {
        List<BookIssue> issues = isStudent ?
                bookIssueRepository.findByStudent_Id(borrowerId, Pageable.unpaged()).getContent() :
                bookIssueRepository.findByTeacher_Id(borrowerId, Pageable.unpaged()).getContent();
        
        return issues.stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canBorrowerIssueMoreBooks(Long borrowerId, boolean isStudent, Long ownerId) {
        long activeBorrowCount = getActiveBorrowCount(borrowerId, isStudent, ownerId);
        int maxBooks = isStudent ? MAX_BOOKS_PER_STUDENT : MAX_BOOKS_PER_TEACHER;
        return activeBorrowCount < maxBooks;
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveBorrowCount(Long borrowerId, boolean isStudent, Long ownerId) {
        List<BookIssue> activeIssues = isStudent ?
                bookIssueRepository.findIssuedBooksByStudent(borrowerId) :
                bookIssueRepository.findIssuedBooksByTeacher(borrowerId);
        
        return activeIssues.stream()
                .filter(bi -> bi.getOwner().getId().equals(ownerId))
                .count();
    }

    private BookIssueResponse convertToResponse(BookIssue bookIssue) {
        LocalDate now = LocalDate.now();
        Library book = bookIssue.getBook();
        
        boolean isStudent = bookIssue.getStudent() != null;
        Worker borrower = isStudent ? bookIssue.getStudent() : bookIssue.getTeacher();
        
        boolean isOverdue = bookIssue.getStatus() == BookIssue.IssueStatus.ISSUED &&
                bookIssue.getDueDate().isBefore(now);
        
        Double totalFine = (bookIssue.getLateFee() != null ? bookIssue.getLateFee() : 0.0) +
                (bookIssue.getDamageFee() != null ? bookIssue.getDamageFee() : 0.0);
        
        int renewalCount = bookIssue.getRenewalCount() != null ? bookIssue.getRenewalCount() : 0;
        boolean canRenew = bookIssue.getStatus() == BookIssue.IssueStatus.ISSUED &&
                renewalCount < MAX_RENEWAL_COUNT &&
                !now.isAfter(bookIssue.getDueDate());
        
        return BookIssueResponse.builder()
                .id(bookIssue.getId())
                .bookId(book.getId())
                .bookTitle(book.getBookTitle())
                .bookIsbn(book.getIsbn())
                .author(book.getAuthor())
                .studentId(isStudent ? borrower.getId() : null)
                .studentName(isStudent ? borrower.getName() : null)
                .teacherId(!isStudent ? borrower.getId() : null)
                .teacherName(!isStudent ? borrower.getName() : null)
                .issueDate(bookIssue.getIssueDate())
                .dueDate(bookIssue.getDueDate())
                .returnDate(bookIssue.getReturnDate())
                .status(bookIssue.getStatus())
                .lateFee(bookIssue.getLateFee())
                .damageFee(bookIssue.getDamageFee())
                .totalFine(totalFine)
                .fineCollected(bookIssue.isFineCollected())
                .pendingFine(!bookIssue.isFineCollected() ? totalFine : 0.0)
                .renewalCount(renewalCount)
                .canRenew(canRenew)
                .ownerId(bookIssue.getOwner().getId())
                .ownerName(bookIssue.getOwner().getName())
                .createdOn(bookIssue.getCreatedOn())
                .updatedOn(bookIssue.getUpdatedOn())
                .isIssued(bookIssue.getStatus() == BookIssue.IssueStatus.ISSUED)
                .isReturned(bookIssue.getStatus() == BookIssue.IssueStatus.RETURNED)
                .isOverdue(isOverdue)
                .statusDisplay(bookIssue.getStatus().name().replace("_", " "))
                .borrowerName(borrower.getName())
                .borrowerType(isStudent ? "Student" : "Teacher")
                .showOverdueBadge(isOverdue)
                .showFinesPendingBadge(!bookIssue.isFineCollected() && totalFine > 0)
                .build();
    }
}
