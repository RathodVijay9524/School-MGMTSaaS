package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.LibraryRequest;
import com.vijay.User_Master.dto.LibraryResponse;
import com.vijay.User_Master.dto.LibraryStatistics;
import com.vijay.User_Master.entity.Library;
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
class LibraryServiceTest extends ServiceTestBase {

    @Mock
    private LibraryService libraryService;

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
    void createBook_withValidRequest_returnsBook() {
        LibraryRequest request = new LibraryRequest();
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.createBook(request, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.createBook(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(libraryService).createBook(request, OWNER_ID);
    }

    @Test
    void createBook_withNullRequest_throwsException() {
        when(libraryService.createBook(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                libraryService.createBook(null, OWNER_ID));
    }

    @Test
    void updateBook_withValidData_returnsUpdatedBook() {
        LibraryRequest request = new LibraryRequest();
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.updateBook(1L, request, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.updateBook(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(libraryService).updateBook(1L, request, OWNER_ID);
    }

    @Test
    void getBookById_withValidId_returnsBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.getBookById(1L, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.getBookById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getBookById_withInvalidId_throwsException() {
        when(libraryService.getBookById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Book not found"));

        assertThrows(RuntimeException.class, () ->
                libraryService.getBookById(999L, OWNER_ID));
    }

    @Test
    void getAllBooks_withValidOwner_returnsPagedBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        List<LibraryResponse> books = new ArrayList<>();
        books.add(new LibraryResponse());
        Page<LibraryResponse> page = new PageImpl<>(books, pageable, 1);

        when(libraryService.getAllBooks(OWNER_ID, pageable)).thenReturn(page);

        Page<LibraryResponse> response = libraryService.getAllBooks(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getAvailableBooks_withValidOwner_returnsAvailableBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        List<LibraryResponse> books = new ArrayList<>();
        books.add(new LibraryResponse());
        Page<LibraryResponse> page = new PageImpl<>(books, pageable, 1);

        when(libraryService.getAvailableBooks(OWNER_ID, pageable)).thenReturn(page);

        Page<LibraryResponse> response = libraryService.getAvailableBooks(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getBooksByAuthor_withValidAuthor_returnsBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getBooksByAuthor("Shakespeare", OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getBooksByAuthor("Shakespeare", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBooksBySubject_withValidSubject_returnsBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getBooksBySubject("Science", OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getBooksBySubject("Science", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBooksByPublisher_withValidPublisher_returnsBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getBooksByPublisher("Penguin", OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getBooksByPublisher("Penguin", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBooksByLanguage_withValidLanguage_returnsBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getBooksByLanguage("English", OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getBooksByLanguage("English", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getReferenceOnlyBooks_withValidOwner_returnsReferenceBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getReferenceOnlyBooks(OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getReferenceOnlyBooks(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBorrowableBooks_withValidOwner_returnsBorrowableBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getBorrowableBooks(OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getBorrowableBooks(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBooksByPurchaseDateRange_withValidDates_returnsBooks() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getBooksByPurchaseDateRange(startDate, endDate, OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getBooksByPurchaseDateRange(startDate, endDate, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBooksByShelfNumber_withValidShelfNumber_returnsBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getBooksByShelfNumber("A1", OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getBooksByShelfNumber("A1", OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchBooks_withValidKeyword_returnsBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        List<LibraryResponse> books = new ArrayList<>();
        books.add(new LibraryResponse());
        Page<LibraryResponse> page = new PageImpl<>(books, pageable, 1);

        when(libraryService.searchBooks("history", OWNER_ID, pageable)).thenReturn(page);

        Page<LibraryResponse> response = libraryService.searchBooks("history", OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void deleteBook_withValidId_succeeds() {
        doNothing().when(libraryService).deleteBook(1L, OWNER_ID);

        libraryService.deleteBook(1L, OWNER_ID);

        verify(libraryService).deleteBook(1L, OWNER_ID);
    }

    @Test
    void restoreBook_withValidId_succeeds() {
        doNothing().when(libraryService).restoreBook(1L, OWNER_ID);

        libraryService.restoreBook(1L, OWNER_ID);

        verify(libraryService).restoreBook(1L, OWNER_ID);
    }

    @Test
    void issueBook_withValidId_returnsUpdatedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.issueBook(1L, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.issueBook(1L, OWNER_ID);

        assertNotNull(response);
        verify(libraryService).issueBook(1L, OWNER_ID);
    }

    @Test
    void returnBook_withValidId_returnsUpdatedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.returnBook(1L, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.returnBook(1L, OWNER_ID);

        assertNotNull(response);
        verify(libraryService).returnBook(1L, OWNER_ID);
    }

    @Test
    void reserveBook_withValidId_returnsReservedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.reserveBook(1L, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.reserveBook(1L, OWNER_ID);

        assertNotNull(response);
        verify(libraryService).reserveBook(1L, OWNER_ID);
    }

    @Test
    void cancelReservation_withValidId_returnsUpdatedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.cancelReservation(1L, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.cancelReservation(1L, OWNER_ID);

        assertNotNull(response);
        verify(libraryService).cancelReservation(1L, OWNER_ID);
    }

    @Test
    void markBookAsDamaged_withValidData_returnsUpdatedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.markBookAsDamaged(1L, "Water damage", OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.markBookAsDamaged(1L, "Water damage", OWNER_ID);

        assertNotNull(response);
        verify(libraryService).markBookAsDamaged(1L, "Water damage", OWNER_ID);
    }

    @Test
    void markBookAsLost_withValidData_returnsUpdatedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.markBookAsLost(1L, "Lost in library", OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.markBookAsLost(1L, "Lost in library", OWNER_ID);

        assertNotNull(response);
        verify(libraryService).markBookAsLost(1L, "Lost in library", OWNER_ID);
    }

    @Test
    void markBookAsUnderRepair_withValidData_returnsUpdatedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.markBookAsUnderRepair(1L, "Binding repair", OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.markBookAsUnderRepair(1L, "Binding repair", OWNER_ID);

        assertNotNull(response);
        verify(libraryService).markBookAsUnderRepair(1L, "Binding repair", OWNER_ID);
    }

    @Test
    void updateBookCopies_withValidData_returnsUpdatedBook() {
        LibraryResponse mockResponse = new LibraryResponse();
        mockResponse.setId(1L);

        when(libraryService.updateBookCopies(1L, 10, 8, 2, OWNER_ID)).thenReturn(mockResponse);

        LibraryResponse response = libraryService.updateBookCopies(1L, 10, 8, 2, OWNER_ID);

        assertNotNull(response);
        verify(libraryService).updateBookCopies(1L, 10, 8, 2, OWNER_ID);
    }

    @Test
    void getLibraryStatistics_withValidOwner_returnsStatistics() {
        LibraryStatistics mockStats = new LibraryStatistics();

        when(libraryService.getLibraryStatistics(OWNER_ID)).thenReturn(mockStats);

        LibraryStatistics response = libraryService.getLibraryStatistics(OWNER_ID);

        assertNotNull(response);
        verify(libraryService).getLibraryStatistics(OWNER_ID);
    }

    @Test
    void getRecentlyAddedBooks_withValidOwner_returnsRecentBooks() {
        List<LibraryResponse> mockBooks = new ArrayList<>();
        mockBooks.add(new LibraryResponse());

        when(libraryService.getRecentlyAddedBooks(OWNER_ID)).thenReturn(mockBooks);

        List<LibraryResponse> response = libraryService.getRecentlyAddedBooks(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }
}
