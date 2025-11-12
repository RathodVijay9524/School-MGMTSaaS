package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.IDCardRequest;
import com.vijay.User_Master.dto.IDCardResponse;
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
class IDCardServiceTest extends ServiceTestBase {

    @Mock
    private IDCardService idCardService;

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
    void generateStudentIDCard_withValidData_returnsIDCard() {
        LocalDate expiryDate = LocalDate.now().plusYears(1);
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.generateStudentIDCard(100L, expiryDate)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.generateStudentIDCard(100L, expiryDate);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(idCardService).generateStudentIDCard(100L, expiryDate);
    }

    @Test
    void generateTeacherIDCard_withValidData_returnsIDCard() {
        LocalDate expiryDate = LocalDate.now().plusYears(1);
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.generateTeacherIDCard(50L, expiryDate)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.generateTeacherIDCard(50L, expiryDate);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(idCardService).generateTeacherIDCard(50L, expiryDate);
    }

    @Test
    void createIDCard_withValidRequest_returnsIDCard() {
        IDCardRequest request = new IDCardRequest();
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.createIDCard(request)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.createIDCard(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(idCardService).createIDCard(request);
    }

    @Test
    void createIDCard_withNullRequest_throwsException() {
        when(idCardService.createIDCard(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                idCardService.createIDCard(null));
    }

    @Test
    void getIDCardById_withValidId_returnsIDCard() {
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.getIDCardById(1L)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.getIDCardById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getIDCardById_withInvalidId_throwsException() {
        when(idCardService.getIDCardById(999L))
                .thenThrow(new RuntimeException("ID card not found"));

        assertThrows(RuntimeException.class, () ->
                idCardService.getIDCardById(999L));
    }

    @Test
    void getIDCardByNumber_withValidNumber_returnsIDCard() {
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setCardNumber("STU123456");

        when(idCardService.getIDCardByNumber("STU123456")).thenReturn(mockResponse);

        IDCardResponse response = idCardService.getIDCardByNumber("STU123456");

        assertNotNull(response);
        assertEquals("STU123456", response.getCardNumber());
    }

    @Test
    void getActiveStudentCard_withValidStudentId_returnsIDCard() {
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.getActiveStudentCard(100L)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.getActiveStudentCard(100L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getActiveTeacherCard_withValidTeacherId_returnsIDCard() {
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.getActiveTeacherCard(50L)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.getActiveTeacherCard(50L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAllIDCards_withValidPageable_returnsPagedCards() {
        Pageable pageable = PageRequest.of(0, 10);
        List<IDCardResponse> cards = new ArrayList<>();
        cards.add(new IDCardResponse());
        Page<IDCardResponse> page = new PageImpl<>(cards, pageable, 1);

        when(idCardService.getAllIDCards(pageable)).thenReturn(page);

        Page<IDCardResponse> response = idCardService.getAllIDCards(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(idCardService).getAllIDCards(pageable);
    }

    @Test
    void getExpiredCards_returnsExpiredCards() {
        List<IDCardResponse> mockCards = new ArrayList<>();
        mockCards.add(new IDCardResponse());

        when(idCardService.getExpiredCards()).thenReturn(mockCards);

        List<IDCardResponse> response = idCardService.getExpiredCards();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getCardsExpiringSoon_returnsExpiringCards() {
        List<IDCardResponse> mockCards = new ArrayList<>();
        mockCards.add(new IDCardResponse());

        when(idCardService.getCardsExpiringSoon()).thenReturn(mockCards);

        List<IDCardResponse> response = idCardService.getCardsExpiringSoon();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void reportLost_withValidData_returnsUpdatedCard() {
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.reportLost(1L, "Lost in library")).thenReturn(mockResponse);

        IDCardResponse response = idCardService.reportLost(1L, "Lost in library");

        assertNotNull(response);
        verify(idCardService).reportLost(1L, "Lost in library");
    }

    @Test
    void reportDamaged_withValidData_returnsUpdatedCard() {
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.reportDamaged(1L, "Water damage")).thenReturn(mockResponse);

        IDCardResponse response = idCardService.reportDamaged(1L, "Water damage");

        assertNotNull(response);
        verify(idCardService).reportDamaged(1L, "Water damage");
    }

    @Test
    void reissueIDCard_withValidData_returnsNewCard() {
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(2L);

        when(idCardService.reissueIDCard(1L, 50.0)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.reissueIDCard(1L, 50.0);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        verify(idCardService).reissueIDCard(1L, 50.0);
    }

    @Test
    void generateIDCardPDF_withValidId_returnsFilePath() {
        when(idCardService.generateIDCardPDF(1L)).thenReturn("/path/idcard-1.pdf");

        String response = idCardService.generateIDCardPDF(1L);

        assertNotNull(response);
        assertTrue(response.contains(".pdf"));
        verify(idCardService).generateIDCardPDF(1L);
    }

    @Test
    void generateQRCode_withValidId_returnsQRCode() {
        when(idCardService.generateQRCode(1L)).thenReturn("QR_CODE_DATA_1");

        String response = idCardService.generateQRCode(1L);

        assertNotNull(response);
        assertTrue(response.startsWith("QR_CODE"));
        verify(idCardService).generateQRCode(1L);
    }

    @Test
    void cancelIDCard_withValidId_succeeds() {
        doNothing().when(idCardService).cancelIDCard(1L);

        idCardService.cancelIDCard(1L);

        verify(idCardService).cancelIDCard(1L);
    }

    @Test
    void getExpiredCards_withNoExpiredCards_returnsEmptyList() {
        List<IDCardResponse> mockCards = new ArrayList<>();

        when(idCardService.getExpiredCards()).thenReturn(mockCards);

        List<IDCardResponse> response = idCardService.getExpiredCards();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void generateStudentIDCard_withFutureExpiryDate_returnsIDCard() {
        LocalDate expiryDate = LocalDate.now().plusYears(2);
        IDCardResponse mockResponse = new IDCardResponse();
        mockResponse.setId(1L);

        when(idCardService.generateStudentIDCard(100L, expiryDate)).thenReturn(mockResponse);

        IDCardResponse response = idCardService.generateStudentIDCard(100L, expiryDate);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }
}
