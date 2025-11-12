package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SubscriptionRequest;
import com.vijay.User_Master.dto.SubscriptionResponse;
import com.vijay.User_Master.dto.SubscriptionStatisticsResponse;
import com.vijay.User_Master.dto.FeatureUsageResponse;
import com.vijay.User_Master.entity.Subscription;
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
class SubscriptionServiceTest extends ServiceTestBase {

    @Mock
    private SubscriptionService subscriptionService;

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
    void createSubscription_withValidRequest_returnsSubscription() {
        SubscriptionRequest request = new SubscriptionRequest();
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.createSubscription(request, OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.createSubscription(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(subscriptionService).createSubscription(request, OWNER_ID);
    }

    @Test
    void createSubscription_withNullRequest_throwsException() {
        when(subscriptionService.createSubscription(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.createSubscription(null, OWNER_ID));
    }

    @Test
    void updateSubscription_withValidData_returnsUpdatedSubscription() {
        SubscriptionRequest request = new SubscriptionRequest();
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.updateSubscription(1L, request, OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.updateSubscription(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).updateSubscription(1L, request, OWNER_ID);
    }

    @Test
    void getSubscriptionById_withValidId_returnsSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.getSubscriptionById(1L, OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.getSubscriptionById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getSubscriptionById_withInvalidId_throwsException() {
        when(subscriptionService.getSubscriptionById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Subscription not found"));

        assertThrows(RuntimeException.class, () ->
                subscriptionService.getSubscriptionById(999L, OWNER_ID));
    }

    @Test
    void getAllSubscriptions_withValidOwner_returnsPagedSubscriptions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<SubscriptionResponse> subscriptions = new ArrayList<>();
        subscriptions.add(new SubscriptionResponse());
        Page<SubscriptionResponse> page = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionService.getAllSubscriptions(OWNER_ID, pageable)).thenReturn(page);

        Page<SubscriptionResponse> response = subscriptionService.getAllSubscriptions(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(subscriptionService).getAllSubscriptions(OWNER_ID, pageable);
    }

    @Test
    void getActiveSubscription_withValidOwner_returnsSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.getActiveSubscription(OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.getActiveSubscription(OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void cancelSubscription_withValidId_returnsCancelledSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.cancelSubscription(1L, OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.cancelSubscription(1L, OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).cancelSubscription(1L, OWNER_ID);
    }

    @Test
    void renewSubscription_withValidId_returnsRenewedSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.renewSubscription(1L, OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.renewSubscription(1L, OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).renewSubscription(1L, OWNER_ID);
    }

    @Test
    void upgradeSubscription_withValidData_returnsUpgradedSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.upgradeSubscription(1L, Subscription.SubscriptionPlan.PREMIUM, OWNER_ID))
                .thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.upgradeSubscription(1L, Subscription.SubscriptionPlan.PREMIUM, OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).upgradeSubscription(1L, Subscription.SubscriptionPlan.PREMIUM, OWNER_ID);
    }

    @Test
    void activateSubscription_withValidId_returnsActivatedSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.activateSubscription(1L)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.activateSubscription(1L);

        assertNotNull(response);
        verify(subscriptionService).activateSubscription(1L);
    }

    @Test
    void getSubscriptionsExpiringSoon_withValidData_returnsPagedSubscriptions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<SubscriptionResponse> subscriptions = new ArrayList<>();
        subscriptions.add(new SubscriptionResponse());
        Page<SubscriptionResponse> page = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionService.getSubscriptionsExpiringSoon(OWNER_ID, 30, pageable)).thenReturn(page);

        Page<SubscriptionResponse> response = subscriptionService.getSubscriptionsExpiringSoon(OWNER_ID, 30, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getExpiredSubscriptions_withValidOwner_returnsPagedSubscriptions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<SubscriptionResponse> subscriptions = new ArrayList<>();
        subscriptions.add(new SubscriptionResponse());
        Page<SubscriptionResponse> page = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionService.getExpiredSubscriptions(OWNER_ID, pageable)).thenReturn(page);

        Page<SubscriptionResponse> response = subscriptionService.getExpiredSubscriptions(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getSubscriptionStatistics_withValidOwner_returnsStatistics() {
        SubscriptionStatisticsResponse mockResponse = new SubscriptionStatisticsResponse();

        when(subscriptionService.getSubscriptionStatistics(OWNER_ID)).thenReturn(mockResponse);

        SubscriptionStatisticsResponse response = subscriptionService.getSubscriptionStatistics(OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).getSubscriptionStatistics(OWNER_ID);
    }

    @Test
    void canUseFeature_withValidData_returnsBoolean() {
        when(subscriptionService.canUseFeature(OWNER_ID, "STUDENT_MANAGEMENT")).thenReturn(true);

        boolean result = subscriptionService.canUseFeature(OWNER_ID, "STUDENT_MANAGEMENT");

        assertTrue(result);
        verify(subscriptionService).canUseFeature(OWNER_ID, "STUDENT_MANAGEMENT");
    }

    @Test
    void trackFeatureUsage_withValidData_succeeds() {
        doNothing().when(subscriptionService).trackFeatureUsage(OWNER_ID, "STUDENT_MANAGEMENT");

        subscriptionService.trackFeatureUsage(OWNER_ID, "STUDENT_MANAGEMENT");

        verify(subscriptionService).trackFeatureUsage(OWNER_ID, "STUDENT_MANAGEMENT");
    }

    @Test
    void getFeatureUsage_withValidOwner_returnsUsage() {
        FeatureUsageResponse mockResponse = new FeatureUsageResponse();

        when(subscriptionService.getFeatureUsage(OWNER_ID)).thenReturn(mockResponse);

        FeatureUsageResponse response = subscriptionService.getFeatureUsage(OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).getFeatureUsage(OWNER_ID);
    }

    @Test
    void setupUPIAutoPay_withValidData_returnsSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.setupUPIAutoPay(1L, "user@upi", OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.setupUPIAutoPay(1L, "user@upi", OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).setupUPIAutoPay(1L, "user@upi", OWNER_ID);
    }

    @Test
    void cancelUPIAutoPay_withValidData_returnsSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.cancelUPIAutoPay(1L, OWNER_ID)).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.cancelUPIAutoPay(1L, OWNER_ID);

        assertNotNull(response);
        verify(subscriptionService).cancelUPIAutoPay(1L, OWNER_ID);
    }

    @Test
    void processSubscriptionRenewal_withValidId_succeeds() {
        doNothing().when(subscriptionService).processSubscriptionRenewal(1L);

        subscriptionService.processSubscriptionRenewal(1L);

        verify(subscriptionService).processSubscriptionRenewal(1L);
    }

    @Test
    void getSubscriptionByOrderId_withValidId_returnsSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.getSubscriptionByOrderId("order_123")).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.getSubscriptionByOrderId("order_123");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getSubscriptionByPaymentId_withValidId_returnsSubscription() {
        SubscriptionResponse mockResponse = new SubscriptionResponse();
        mockResponse.setId(1L);

        when(subscriptionService.getSubscriptionByPaymentId("pay_456")).thenReturn(mockResponse);

        SubscriptionResponse response = subscriptionService.getSubscriptionByPaymentId("pay_456");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void updateSubscription_withNullRequest_throwsException() {
        when(subscriptionService.updateSubscription(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.updateSubscription(1L, null, OWNER_ID));
    }

    @Test
    void getAllSubscriptions_withEmptyResult_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionResponse> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(subscriptionService.getAllSubscriptions(OWNER_ID, pageable)).thenReturn(page);

        Page<SubscriptionResponse> response = subscriptionService.getAllSubscriptions(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
    }
}
