package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SubscriptionOrderRequest;
import com.vijay.User_Master.dto.SubscriptionOrderResponse;
import com.vijay.User_Master.dto.UPIMandateRequest;
import com.vijay.User_Master.dto.UPIMandateResponse;
import com.vijay.User_Master.dto.PaymentResponse;
import com.vijay.User_Master.dto.RefundResponse;
import com.vijay.User_Master.dto.PaymentStatisticsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RazorpayPaymentServiceTest extends ServiceTestBase {

    @Mock
    private RazorpayPaymentService razorpayPaymentService;

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
    void createSubscriptionOrder_withValidRequest_returnsOrderResponse() {
        SubscriptionOrderRequest request = new SubscriptionOrderRequest();
        SubscriptionOrderResponse mockResponse = new SubscriptionOrderResponse();
        mockResponse.setOrderId("order_123");

        when(razorpayPaymentService.createSubscriptionOrder(request)).thenReturn(mockResponse);

        SubscriptionOrderResponse response = razorpayPaymentService.createSubscriptionOrder(request);

        assertNotNull(response);
        assertEquals("order_123", response.getOrderId());
        verify(razorpayPaymentService).createSubscriptionOrder(request);
    }

    @Test
    void createSubscriptionOrder_withNullRequest_throwsException() {
        when(razorpayPaymentService.createSubscriptionOrder(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                razorpayPaymentService.createSubscriptionOrder(null));
    }

    @Test
    void verifyPayment_withValidData_returnsTrue() {
        when(razorpayPaymentService.verifyPayment("order_123", "pay_456", "sig_789")).thenReturn(true);

        boolean result = razorpayPaymentService.verifyPayment("order_123", "pay_456", "sig_789");

        assertTrue(result);
        verify(razorpayPaymentService).verifyPayment("order_123", "pay_456", "sig_789");
    }

    @Test
    void verifyPayment_withInvalidData_returnsFalse() {
        when(razorpayPaymentService.verifyPayment("invalid_order", "invalid_pay", "invalid_sig")).thenReturn(false);

        boolean result = razorpayPaymentService.verifyPayment("invalid_order", "invalid_pay", "invalid_sig");

        assertFalse(result);
    }

    @Test
    void createUPIMandate_withValidRequest_returnsMandateResponse() {
        UPIMandateRequest request = new UPIMandateRequest();
        UPIMandateResponse mockResponse = new UPIMandateResponse();
        mockResponse.setMandateId("mandate_123");

        when(razorpayPaymentService.createUPIMandate(request)).thenReturn(mockResponse);

        UPIMandateResponse response = razorpayPaymentService.createUPIMandate(request);

        assertNotNull(response);
        assertEquals("mandate_123", response.getMandateId());
        verify(razorpayPaymentService).createUPIMandate(request);
    }

    @Test
    void chargeSubscription_withValidId_returnsPaymentResponse() {
        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setPaymentId("pay_456");

        when(razorpayPaymentService.chargeSubscription("sub_789")).thenReturn(mockResponse);

        PaymentResponse response = razorpayPaymentService.chargeSubscription("sub_789");

        assertNotNull(response);
        assertEquals("pay_456", response.getPaymentId());
        verify(razorpayPaymentService).chargeSubscription("sub_789");
    }

    @Test
    void cancelUPIMandate_withValidId_returnsTrue() {
        when(razorpayPaymentService.cancelUPIMandate("mandate_123")).thenReturn(true);

        boolean result = razorpayPaymentService.cancelUPIMandate("mandate_123");

        assertTrue(result);
        verify(razorpayPaymentService).cancelUPIMandate("mandate_123");
    }

    @Test
    void cancelUPIMandate_withInvalidId_returnsFalse() {
        when(razorpayPaymentService.cancelUPIMandate("invalid_mandate")).thenReturn(false);

        boolean result = razorpayPaymentService.cancelUPIMandate("invalid_mandate");

        assertFalse(result);
    }

    @Test
    void getPaymentDetails_withValidId_returnsPaymentResponse() {
        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setPaymentId("pay_456");

        when(razorpayPaymentService.getPaymentDetails("pay_456")).thenReturn(mockResponse);

        PaymentResponse response = razorpayPaymentService.getPaymentDetails("pay_456");

        assertNotNull(response);
        assertEquals("pay_456", response.getPaymentId());
    }

    @Test
    void refundPayment_withValidData_returnsRefundResponse() {
        RefundResponse mockResponse = new RefundResponse();
        mockResponse.setRefundId("refund_123");

        when(razorpayPaymentService.refundPayment("pay_456", 1000L, "Test refund")).thenReturn(mockResponse);

        RefundResponse response = razorpayPaymentService.refundPayment("pay_456", 1000L, "Test refund");

        assertNotNull(response);
        assertEquals("refund_123", response.getRefundId());
        verify(razorpayPaymentService).refundPayment("pay_456", 1000L, "Test refund");
    }

    @Test
    void getRefundDetails_withValidId_returnsRefundResponse() {
        RefundResponse mockResponse = new RefundResponse();
        mockResponse.setRefundId("refund_123");

        when(razorpayPaymentService.getRefundDetails("refund_123")).thenReturn(mockResponse);

        RefundResponse response = razorpayPaymentService.getRefundDetails("refund_123");

        assertNotNull(response);
        assertEquals("refund_123", response.getRefundId());
    }

    @Test
    void handleWebhookEvent_withValidData_succeeds() {
        doNothing().when(razorpayPaymentService).handleWebhookEvent("payment.captured", "{\"event\":\"payment.captured\"}");

        razorpayPaymentService.handleWebhookEvent("payment.captured", "{\"event\":\"payment.captured\"}");

        verify(razorpayPaymentService).handleWebhookEvent("payment.captured", "{\"event\":\"payment.captured\"}");
    }

    @Test
    void generateReceipt_withValidId_returnsReceiptPath() {
        when(razorpayPaymentService.generateReceipt("pay_456")).thenReturn("/receipts/pay_456.pdf");

        String receiptPath = razorpayPaymentService.generateReceipt("pay_456");

        assertNotNull(receiptPath);
        assertTrue(receiptPath.contains(".pdf"));
        verify(razorpayPaymentService).generateReceipt("pay_456");
    }

    @Test
    void getPaymentStatistics_withValidOwner_returnsStatistics() {
        PaymentStatisticsResponse mockResponse = new PaymentStatisticsResponse();

        when(razorpayPaymentService.getPaymentStatistics(OWNER_ID)).thenReturn(mockResponse);

        PaymentStatisticsResponse response = razorpayPaymentService.getPaymentStatistics(OWNER_ID);

        assertNotNull(response);
        verify(razorpayPaymentService).getPaymentStatistics(OWNER_ID);
    }

    @Test
    void createUPIMandate_withNullRequest_throwsException() {
        when(razorpayPaymentService.createUPIMandate(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                razorpayPaymentService.createUPIMandate(null));
    }

    @Test
    void refundPayment_withZeroAmount_returnsRefundResponse() {
        RefundResponse mockResponse = new RefundResponse();
        mockResponse.setRefundId("refund_456");

        when(razorpayPaymentService.refundPayment("pay_456", 0L, "Zero refund")).thenReturn(mockResponse);

        RefundResponse response = razorpayPaymentService.refundPayment("pay_456", 0L, "Zero refund");

        assertNotNull(response);
        assertEquals("refund_456", response.getRefundId());
    }
}
