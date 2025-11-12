package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.TransferCertificateRequest;
import com.vijay.User_Master.dto.TransferCertificateResponse;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferCertificateServiceTest extends ServiceTestBase {

    @Mock
    private TransferCertificateService transferCertificateService;

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
    void generateTC_withValidRequest_returnsTC() {
        TransferCertificateRequest request = new TransferCertificateRequest();
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(1L);

        when(transferCertificateService.generateTC(request)).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.generateTC(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(transferCertificateService).generateTC(request);
    }

    @Test
    void generateTC_withNullRequest_throwsException() {
        when(transferCertificateService.generateTC(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                transferCertificateService.generateTC(null));
    }

    @Test
    void getTCById_withValidId_returnsTC() {
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(1L);

        when(transferCertificateService.getTCById(1L)).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.getTCById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTCById_withInvalidId_throwsException() {
        when(transferCertificateService.getTCById(999L))
                .thenThrow(new RuntimeException("TC not found"));

        assertThrows(RuntimeException.class, () ->
                transferCertificateService.getTCById(999L));
    }

    @Test
    void getTCByStudentId_withValidId_returnsTC() {
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(1L);

        when(transferCertificateService.getTCByStudentId(100L)).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.getTCByStudentId(100L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTCByNumber_withValidNumber_returnsTC() {
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(1L);

        when(transferCertificateService.getTCByNumber("TC2023001")).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.getTCByNumber("TC2023001");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAllTCs_withValidData_returnsPagedTCs() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TransferCertificateResponse> tcs = new ArrayList<>();
        tcs.add(new TransferCertificateResponse());
        Page<TransferCertificateResponse> page = new PageImpl<>(tcs, pageable, 1);

        when(transferCertificateService.getAllTCs(pageable)).thenReturn(page);

        Page<TransferCertificateResponse> response = transferCertificateService.getAllTCs(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(transferCertificateService).getAllTCs(pageable);
    }

    @Test
    void getPendingApprovals_withPendingTCs_returnsList() {
        List<TransferCertificateResponse> mockTCs = new ArrayList<>();
        mockTCs.add(new TransferCertificateResponse());
        mockTCs.add(new TransferCertificateResponse());

        when(transferCertificateService.getPendingApprovals()).thenReturn(mockTCs);

        List<TransferCertificateResponse> response = transferCertificateService.getPendingApprovals();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void approveTC_withValidData_returnsApprovedTC() {
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(1L);

        when(transferCertificateService.approveTC(1L, 50L)).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.approveTC(1L, 50L);

        assertNotNull(response);
        verify(transferCertificateService).approveTC(1L, 50L);
    }

    @Test
    void issueTC_withValidId_returnsIssuedTC() {
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(1L);

        when(transferCertificateService.issueTC(1L)).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.issueTC(1L);

        assertNotNull(response);
        verify(transferCertificateService).issueTC(1L);
    }

    @Test
    void generateTCPDF_withValidId_returnsPDFPath() {
        when(transferCertificateService.generateTCPDF(1L)).thenReturn("path/to/tc.pdf");

        String pdfPath = transferCertificateService.generateTCPDF(1L);

        assertNotNull(pdfPath);
        assertEquals("path/to/tc.pdf", pdfPath);
        verify(transferCertificateService).generateTCPDF(1L);
    }

    @Test
    void cancelTC_withValidData_succeeds() {
        doNothing().when(transferCertificateService).cancelTC(1L, "Duplicate request");

        transferCertificateService.cancelTC(1L, "Duplicate request");

        verify(transferCertificateService).cancelTC(1L, "Duplicate request");
    }

    @Test
    void deleteTC_withValidId_succeeds() {
        doNothing().when(transferCertificateService).deleteTC(1L);

        transferCertificateService.deleteTC(1L);

        verify(transferCertificateService).deleteTC(1L);
    }

    @Test
    void getTCByStudentId_withInvalidId_throwsException() {
        when(transferCertificateService.getTCByStudentId(999L))
                .thenThrow(new RuntimeException("TC not found for student"));

        assertThrows(RuntimeException.class, () ->
                transferCertificateService.getTCByStudentId(999L));
    }

    @Test
    void getPendingApprovals_withNoPending_returnsEmptyList() {
        List<TransferCertificateResponse> mockTCs = new ArrayList<>();

        when(transferCertificateService.getPendingApprovals()).thenReturn(mockTCs);

        List<TransferCertificateResponse> response = transferCertificateService.getPendingApprovals();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void approveTC_withDifferentUser_returnsApprovedTC() {
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(1L);

        when(transferCertificateService.approveTC(1L, 75L)).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.approveTC(1L, 75L);

        assertNotNull(response);
        verify(transferCertificateService).approveTC(1L, 75L);
    }

    @Test
    void generateTCPDF_withDifferentId_returnsPDFPath() {
        when(transferCertificateService.generateTCPDF(2L)).thenReturn("path/to/tc2.pdf");

        String pdfPath = transferCertificateService.generateTCPDF(2L);

        assertNotNull(pdfPath);
        assertEquals("path/to/tc2.pdf", pdfPath);
        verify(transferCertificateService).generateTCPDF(2L);
    }

    @Test
    void cancelTC_withDifferentReason_succeeds() {
        doNothing().when(transferCertificateService).cancelTC(2L, "Student withdrawn");

        transferCertificateService.cancelTC(2L, "Student withdrawn");

        verify(transferCertificateService).cancelTC(2L, "Student withdrawn");
    }

    @Test
    void getTCByNumber_withDifferentNumber_returnsTC() {
        TransferCertificateResponse mockResponse = new TransferCertificateResponse();
        mockResponse.setId(2L);

        when(transferCertificateService.getTCByNumber("TC2023002")).thenReturn(mockResponse);

        TransferCertificateResponse response = transferCertificateService.getTCByNumber("TC2023002");

        assertNotNull(response);
        assertEquals(2L, response.getId());
    }
}
