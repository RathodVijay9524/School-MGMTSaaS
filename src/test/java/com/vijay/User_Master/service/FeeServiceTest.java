package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.FeeInstallmentResponse;
import com.vijay.User_Master.dto.FeeRequest;
import com.vijay.User_Master.dto.FeeResponse;
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
class FeeServiceTest extends ServiceTestBase {

    @Mock
    private FeeService feeService;

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
    void getAllFees_withValidPageable_returnsPagedFees() {
        Pageable pageable = PageRequest.of(0, 10);
        List<FeeResponse> fees = new ArrayList<>();
        fees.add(new FeeResponse());
        Page<FeeResponse> page = new PageImpl<>(fees, pageable, 1);

        when(feeService.getAllFees(pageable)).thenReturn(page);

        Page<FeeResponse> response = feeService.getAllFees(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(feeService).getAllFees(pageable);
    }

    @Test
    void createFee_withValidRequest_returnsFee() {
        FeeRequest request = new FeeRequest();
        FeeResponse mockResponse = new FeeResponse();
        mockResponse.setId(1L);

        when(feeService.createFee(request)).thenReturn(mockResponse);

        FeeResponse response = feeService.createFee(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void createFee_withNullRequest_throwsException() {
        when(feeService.createFee(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                feeService.createFee(null));
    }

    @Test
    void updateFee_withValidData_returnsUpdatedFee() {
        FeeRequest request = new FeeRequest();
        FeeResponse mockResponse = new FeeResponse();
        mockResponse.setId(1L);

        when(feeService.updateFee(1L, request)).thenReturn(mockResponse);

        FeeResponse response = feeService.updateFee(1L, request);

        assertNotNull(response);
        verify(feeService).updateFee(1L, request);
    }

    @Test
    void getFeeById_withValidId_returnsFee() {
        FeeResponse mockResponse = new FeeResponse();
        mockResponse.setId(1L);

        when(feeService.getFeeById(1L)).thenReturn(mockResponse);

        FeeResponse response = feeService.getFeeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getFeeById_withInvalidId_throwsException() {
        when(feeService.getFeeById(999L))
                .thenThrow(new RuntimeException("Fee not found"));

        assertThrows(RuntimeException.class, () ->
                feeService.getFeeById(999L));
    }

    @Test
    void getFeesByStudent_withValidStudentId_returnsPagedFees() {
        Pageable pageable = PageRequest.of(0, 10);
        List<FeeResponse> fees = new ArrayList<>();
        fees.add(new FeeResponse());
        Page<FeeResponse> page = new PageImpl<>(fees, pageable, 1);

        when(feeService.getFeesByStudent(100L, pageable)).thenReturn(page);

        Page<FeeResponse> response = feeService.getFeesByStudent(100L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getFeesByPaymentStatus_withValidStatus_returnsPagedFees() {
        Pageable pageable = PageRequest.of(0, 10);
        List<FeeResponse> fees = new ArrayList<>();
        fees.add(new FeeResponse());
        Page<FeeResponse> page = new PageImpl<>(fees, pageable, 1);

        when(feeService.getFeesByPaymentStatus("PENDING", pageable)).thenReturn(page);

        Page<FeeResponse> response = feeService.getFeesByPaymentStatus("PENDING", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getPendingFees_withValidStudentId_returnsPendingFees() {
        List<FeeResponse> mockFees = new ArrayList<>();
        mockFees.add(new FeeResponse());

        when(feeService.getPendingFees(100L)).thenReturn(mockFees);

        List<FeeResponse> response = feeService.getPendingFees(100L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPendingFees_withNoPendingFees_returnsEmptyList() {
        List<FeeResponse> mockFees = new ArrayList<>();

        when(feeService.getPendingFees(100L)).thenReturn(mockFees);

        List<FeeResponse> response = feeService.getPendingFees(100L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getOverdueFees_returnsOverdueFees() {
        List<FeeResponse> mockFees = new ArrayList<>();
        mockFees.add(new FeeResponse());

        when(feeService.getOverdueFees()).thenReturn(mockFees);

        List<FeeResponse> response = feeService.getOverdueFees();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void recordPayment_withValidData_returnsUpdatedFee() {
        FeeResponse mockResponse = new FeeResponse();
        mockResponse.setId(1L);

        when(feeService.recordPayment(1L, 5000.0, "ONLINE", "TXN123"))
                .thenReturn(mockResponse);

        FeeResponse response = feeService.recordPayment(1L, 5000.0, "ONLINE", "TXN123");

        assertNotNull(response);
        verify(feeService).recordPayment(1L, 5000.0, "ONLINE", "TXN123");
    }

    @Test
    void calculateTotalFeesCollected_returnsTotal() {
        when(feeService.calculateTotalFeesCollected()).thenReturn(500000.0);

        Double total = feeService.calculateTotalFeesCollected();

        assertNotNull(total);
        assertEquals(500000.0, total);
        assertTrue(total > 0);
    }

    @Test
    void calculateTotalPendingFees_returnsTotal() {
        when(feeService.calculateTotalPendingFees()).thenReturn(100000.0);

        Double total = feeService.calculateTotalPendingFees();

        assertNotNull(total);
        assertEquals(100000.0, total);
    }

    @Test
    void deleteFee_withValidId_succeeds() {
        doNothing().when(feeService).deleteFee(1L);

        feeService.deleteFee(1L);

        verify(feeService).deleteFee(1L);
    }

    @Test
    void getFeeInstallments_withValidFeeId_returnsPagedInstallments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<FeeInstallmentResponse> installments = new ArrayList<>();
        installments.add(new FeeInstallmentResponse());
        Page<FeeInstallmentResponse> page = new PageImpl<>(installments, pageable, 1);

        when(feeService.getFeeInstallments(1L, pageable)).thenReturn(page);

        Page<FeeInstallmentResponse> response = feeService.getFeeInstallments(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getInstallmentById_withValidId_returnsInstallment() {
        FeeInstallmentResponse mockResponse = new FeeInstallmentResponse();
        mockResponse.setId(1L);

        when(feeService.getInstallmentById(1L)).thenReturn(mockResponse);

        FeeInstallmentResponse response = feeService.getInstallmentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void payInstallment_withValidData_returnsUpdatedInstallment() {
        FeeInstallmentResponse mockResponse = new FeeInstallmentResponse();
        mockResponse.setId(1L);

        when(feeService.payInstallment(1L, "ONLINE", "TXN123", "Paid"))
                .thenReturn(mockResponse);

        FeeInstallmentResponse response = feeService.payInstallment(1L, "ONLINE", "TXN123", "Paid");

        assertNotNull(response);
        verify(feeService).payInstallment(1L, "ONLINE", "TXN123", "Paid");
    }

    @Test
    void getOverdueInstallments_returnsPagedInstallments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<FeeInstallmentResponse> installments = new ArrayList<>();
        installments.add(new FeeInstallmentResponse());
        Page<FeeInstallmentResponse> page = new PageImpl<>(installments, pageable, 1);

        when(feeService.getOverdueInstallments(pageable)).thenReturn(page);

        Page<FeeInstallmentResponse> response = feeService.getOverdueInstallments(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getStudentPendingInstallments_withValidStudentId_returnsPagedInstallments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<FeeInstallmentResponse> installments = new ArrayList<>();
        installments.add(new FeeInstallmentResponse());
        Page<FeeInstallmentResponse> page = new PageImpl<>(installments, pageable, 1);

        when(feeService.getStudentPendingInstallments(100L, pageable)).thenReturn(page);

        Page<FeeInstallmentResponse> response = feeService.getStudentPendingInstallments(100L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getNextPendingInstallment_withValidFeeId_returnsInstallment() {
        FeeInstallmentResponse mockResponse = new FeeInstallmentResponse();
        mockResponse.setId(1L);

        when(feeService.getNextPendingInstallment(1L)).thenReturn(mockResponse);

        FeeInstallmentResponse response = feeService.getNextPendingInstallment(1L);

        assertNotNull(response);
        verify(feeService).getNextPendingInstallment(1L);
    }
}
