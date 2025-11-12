package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.BookIssueRequest;
import com.vijay.User_Master.dto.BookIssueResponse;
import com.vijay.User_Master.dto.BookIssueStatistics;
import com.vijay.User_Master.entity.BookIssue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookIssueServiceTest extends ServiceTestBase {

    @Mock
    private BookIssueService bookIssueService;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void issueBook_withValidRequest_returnsBookIssue() {
        BookIssueRequest request = new BookIssueRequest();
        BookIssueResponse mockResponse = new BookIssueResponse();
        mockResponse.setId(1L);

        when(bookIssueService.issueBook(request, OWNER_ID)).thenReturn(mockResponse);

        BookIssueResponse response = bookIssueService.issueBook(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(bookIssueService).issueBook(request, OWNER_ID);
    }

    @Test
    void issueBook_withNullRequest_throwsException() {
        when(bookIssueService.issueBook(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                bookIssueService.issueBook(null, OWNER_ID));
    }

    @Test
    void returnBook_withValidData_returnsBookIssue() {
        BookIssueRequest returnDetails = new BookIssueRequest();
        BookIssueResponse mockResponse = new BookIssueResponse();
        mockResponse.setId(1L);

        when(bookIssueService.returnBook(1L, returnDetails, OWNER_ID)).thenReturn(mockResponse);

        BookIssueResponse response = bookIssueService.returnBook(1L, returnDetails, OWNER_ID);

        assertNotNull(response);
        verify(bookIssueService).returnBook(1L, returnDetails, OWNER_ID);
    }

    @Test
    void renewBook_withValidData_returnsRenewedBook() {
        BookIssueResponse mockResponse = new BookIssueResponse();
        mockResponse.setId(1L);

        when(bookIssueService.renewBook(1L, 7, OWNER_ID)).thenReturn(mockResponse);

        BookIssueResponse response = bookIssueService.renewBook(1L, 7, OWNER_ID);

        assertNotNull(response);
        verify(bookIssueService).renewBook(1L, 7, OWNER_ID);
    }

    @Test
    void getBookIssueById_withValidId_returnsBookIssue() {
        BookIssueResponse mockResponse = new BookIssueResponse();
        mockResponse.setId(1L);

        when(bookIssueService.getBookIssueById(1L, OWNER_ID)).thenReturn(mockResponse);

        BookIssueResponse response = bookIssueService.getBookIssueById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getBookIssueById_withInvalidId_throwsException() {
        when(bookIssueService.getBookIssueById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Book issue not found"));

        assertThrows(RuntimeException.class, () ->
                bookIssueService.getBookIssueById(999L, OWNER_ID));
    }

    @Test
    void getAllBookIssues_withValidOwner_returnsPagedIssues() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIssueResponse> issues = new ArrayList<>();
        issues.add(new BookIssueResponse());
        Page<BookIssueResponse> page = new PageImpl<>(issues, pageable, 1);

        when(bookIssueService.getAllBookIssues(OWNER_ID, pageable)).thenReturn(page);

        Page<BookIssueResponse> response = bookIssueService.getAllBookIssues(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getBookIssuesByStudent_withValidStudentId_returnsIssues() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIssueResponse> issues = new ArrayList<>();
        issues.add(new BookIssueResponse());
        Page<BookIssueResponse> page = new PageImpl<>(issues, pageable, 1);

        when(bookIssueService.getBookIssuesByStudent(100L, OWNER_ID, pageable)).thenReturn(page);

        Page<BookIssueResponse> response = bookIssueService.getBookIssuesByStudent(100L, OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getBookIssuesByTeacher_withValidTeacherId_returnsIssues() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIssueResponse> issues = new ArrayList<>();
        issues.add(new BookIssueResponse());
        Page<BookIssueResponse> page = new PageImpl<>(issues, pageable, 1);

        when(bookIssueService.getBookIssuesByTeacher(50L, OWNER_ID, pageable)).thenReturn(page);

        Page<BookIssueResponse> response = bookIssueService.getBookIssuesByTeacher(50L, OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getBookIssuesByBook_withValidBookId_returnsIssues() {
        List<BookIssueResponse> mockIssues = new ArrayList<>();
        mockIssues.add(new BookIssueResponse());

        when(bookIssueService.getBookIssuesByBook(1L, OWNER_ID)).thenReturn(mockIssues);

        List<BookIssueResponse> response = bookIssueService.getBookIssuesByBook(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getCurrentlyIssuedBooks_withValidOwner_returnsCurrentIssues() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIssueResponse> issues = new ArrayList<>();
        issues.add(new BookIssueResponse());
        Page<BookIssueResponse> page = new PageImpl<>(issues, pageable, 1);

        when(bookIssueService.getCurrentlyIssuedBooks(OWNER_ID, pageable)).thenReturn(page);

        Page<BookIssueResponse> response = bookIssueService.getCurrentlyIssuedBooks(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getOverdueBooks_withValidOwner_returnsOverdueBooks() {
        List<BookIssueResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new BookIssueResponse());

        when(bookIssueService.getOverdueBooks(OWNER_ID)).thenReturn(mockBooks);

        List<BookIssueResponse> response = bookIssueService.getOverdueBooks(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBooksDueToday_withValidOwner_returnsDueBooks() {
        List<BookIssueResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new BookIssueResponse());

        when(bookIssueService.getBooksDueToday(OWNER_ID)).thenReturn(mockBooks);

        List<BookIssueResponse> response = bookIssueService.getBooksDueToday(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBookIssuesByDateRange_withValidDates_returnsIssues() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<BookIssueResponse> mockIssues = new ArrayList<>();
        mockIssues.add(new BookIssueResponse());

        when(bookIssueService.getBookIssuesByDateRange(startDate, endDate, OWNER_ID)).thenReturn(mockIssues);

        List<BookIssueResponse> response = bookIssueService.getBookIssuesByDateRange(startDate, endDate, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void calculateFine_withValidIssueId_returnsFineAmount() {
        when(bookIssueService.calculateFine(1L, OWNER_ID)).thenReturn(50.0);

        Double fine = bookIssueService.calculateFine(1L, OWNER_ID);

        assertNotNull(fine);
        assertEquals(50.0, fine);
    }

    @Test
    void collectFine_withValidIssueId_returnsUpdatedIssue() {
        BookIssueResponse mockResponse = new BookIssueResponse();
        mockResponse.setId(1L);

        when(bookIssueService.collectFine(1L, OWNER_ID)).thenReturn(mockResponse);

        BookIssueResponse response = bookIssueService.collectFine(1L, OWNER_ID);

        assertNotNull(response);
        verify(bookIssueService).collectFine(1L, OWNER_ID);
    }

    @Test
    void getIssuesWithPendingFines_withValidOwner_returnsIssues() {
        List<BookIssueResponse> mockIssues = new ArrayList<>();
        mockIssues.add(new BookIssueResponse());

        when(bookIssueService.getIssuesWithPendingFines(OWNER_ID)).thenReturn(mockIssues);

        List<BookIssueResponse> response = bookIssueService.getIssuesWithPendingFines(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void markBookAsLost_withValidData_returnsUpdatedIssue() {
        BookIssueResponse mockResponse = new BookIssueResponse();
        mockResponse.setId(1L);

        when(bookIssueService.markBookAsLost(1L, "Lost in library", OWNER_ID)).thenReturn(mockResponse);

        BookIssueResponse response = bookIssueService.markBookAsLost(1L, "Lost in library", OWNER_ID);

        assertNotNull(response);
        verify(bookIssueService).markBookAsLost(1L, "Lost in library", OWNER_ID);
    }

    @Test
    void markBookAsDamaged_withValidData_returnsUpdatedIssue() {
        BookIssueResponse mockResponse = new BookIssueResponse();
        mockResponse.setId(1L);

        when(bookIssueService.markBookAsDamaged(1L, 100.0, "Water damage", OWNER_ID)).thenReturn(mockResponse);

        BookIssueResponse response = bookIssueService.markBookAsDamaged(1L, 100.0, "Water damage", OWNER_ID);

        assertNotNull(response);
        verify(bookIssueService).markBookAsDamaged(1L, 100.0, "Water damage", OWNER_ID);
    }

    @Test
    void checkAndUpdateOverdueBooks_succeeds() {
        doNothing().when(bookIssueService).checkAndUpdateOverdueBooks();

        bookIssueService.checkAndUpdateOverdueBooks();

        verify(bookIssueService).checkAndUpdateOverdueBooks();
    }

    @Test
    void sendOverdueNotifications_succeeds() {
        doNothing().when(bookIssueService).sendOverdueNotifications();

        bookIssueService.sendOverdueNotifications();

        verify(bookIssueService).sendOverdueNotifications();
    }

    @Test
    void getBookIssueStatistics_withValidOwner_returnsStatistics() {
        BookIssueStatistics mockStats = new BookIssueStatistics();

        when(bookIssueService.getBookIssueStatistics(OWNER_ID)).thenReturn(mockStats);

        BookIssueStatistics response = bookIssueService.getBookIssueStatistics(OWNER_ID);

        assertNotNull(response);
        verify(bookIssueService).getBookIssueStatistics(OWNER_ID);
    }

    @Test
    void canBorrowerIssueMoreBooks_withValidBorrower_returnsBoolean() {
        when(bookIssueService.canBorrowerIssueMoreBooks(100L, true, OWNER_ID)).thenReturn(true);

        boolean result = bookIssueService.canBorrowerIssueMoreBooks(100L, true, OWNER_ID);

        assertTrue(result);
        verify(bookIssueService).canBorrowerIssueMoreBooks(100L, true, OWNER_ID);
    }

    @Test
    void getActiveBorrowCount_withValidBorrower_returnsCount() {
        when(bookIssueService.getActiveBorrowCount(100L, true, OWNER_ID)).thenReturn(3L);

        long count = bookIssueService.getActiveBorrowCount(100L, true, OWNER_ID);

        assertEquals(3L, count);
        verify(bookIssueService).getActiveBorrowCount(100L, true, OWNER_ID);
    }
}
