package com.vijay.User_Master.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WhatsAppServiceTest extends ServiceTestBase {

    @Mock
    private WhatsAppService whatsAppService;

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
    void sendWhatsAppMessage_withValidData_returnsTrue() {
        when(whatsAppService.sendWhatsAppMessage("+919876543210", "Test message")).thenReturn(true);

        boolean result = whatsAppService.sendWhatsAppMessage("+919876543210", "Test message");

        assertTrue(result);
        verify(whatsAppService).sendWhatsAppMessage("+919876543210", "Test message");
    }

    @Test
    void sendWhatsAppMessage_withInvalidNumber_returnsFalse() {
        when(whatsAppService.sendWhatsAppMessage("invalid", "Test message")).thenReturn(false);

        boolean result = whatsAppService.sendWhatsAppMessage("invalid", "Test message");

        assertFalse(result);
    }

    @Test
    void sendWhatsAppMessageWithMedia_withValidData_returnsTrue() {
        when(whatsAppService.sendWhatsAppMessageWithMedia("+919876543210", "Test message", "https://example.com/image.jpg")).thenReturn(true);

        boolean result = whatsAppService.sendWhatsAppMessageWithMedia("+919876543210", "Test message", "https://example.com/image.jpg");

        assertTrue(result);
        verify(whatsAppService).sendWhatsAppMessageWithMedia("+919876543210", "Test message", "https://example.com/image.jpg");
    }

    @Test
    void sendBulkWhatsAppMessages_withValidData_succeeds() {
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+919876543210");
        phoneNumbers.add("+919876543211");

        doNothing().when(whatsAppService).sendBulkWhatsAppMessages(phoneNumbers, "Bulk test message");

        whatsAppService.sendBulkWhatsAppMessages(phoneNumbers, "Bulk test message");

        verify(whatsAppService).sendBulkWhatsAppMessages(phoneNumbers, "Bulk test message");
    }

    @Test
    void sendAttendanceWhatsApp_withValidData_succeeds() {
        doNothing().when(whatsAppService).sendAttendanceWhatsApp(100L, any(), "PRESENT");

        whatsAppService.sendAttendanceWhatsApp(100L, any(), "PRESENT");

        verify(whatsAppService).sendAttendanceWhatsApp(100L, any(), "PRESENT");
    }

    @Test
    void sendFeeReminderWhatsApp_withValidData_succeeds() {
        doNothing().when(whatsAppService).sendFeeReminderWhatsApp(100L, 1L);

        whatsAppService.sendFeeReminderWhatsApp(100L, 1L);

        verify(whatsAppService).sendFeeReminderWhatsApp(100L, 1L);
    }

    @Test
    void sendReportCardWhatsApp_withValidData_succeeds() {
        doNothing().when(whatsAppService).sendReportCardWhatsApp(100L, "SPRING-2024", "https://example.com/report.pdf");

        whatsAppService.sendReportCardWhatsApp(100L, "SPRING-2024", "https://example.com/report.pdf");

        verify(whatsAppService).sendReportCardWhatsApp(100L, "SPRING-2024", "https://example.com/report.pdf");
    }

    @Test
    void sendAdmitCardWhatsApp_withValidData_succeeds() {
        doNothing().when(whatsAppService).sendAdmitCardWhatsApp(100L, 1L, "https://example.com/admit.pdf");

        whatsAppService.sendAdmitCardWhatsApp(100L, 1L, "https://example.com/admit.pdf");

        verify(whatsAppService).sendAdmitCardWhatsApp(100L, 1L, "https://example.com/admit.pdf");
    }

    @Test
    void sendIDCardWhatsApp_withValidData_succeeds() {
        doNothing().when(whatsAppService).sendIDCardWhatsApp(100L, "https://example.com/idcard.pdf");

        whatsAppService.sendIDCardWhatsApp(100L, "https://example.com/idcard.pdf");

        verify(whatsAppService).sendIDCardWhatsApp(100L, "https://example.com/idcard.pdf");
    }

    @Test
    void sendTCWhatsApp_withValidData_succeeds() {
        doNothing().when(whatsAppService).sendTCWhatsApp(100L, "https://example.com/tc.pdf");

        whatsAppService.sendTCWhatsApp(100L, "https://example.com/tc.pdf");

        verify(whatsAppService).sendTCWhatsApp(100L, "https://example.com/tc.pdf");
    }

    @Test
    void sendEventInvitationWhatsApp_withValidData_succeeds() {
        doNothing().when(whatsAppService).sendEventInvitationWhatsApp(1L);

        whatsAppService.sendEventInvitationWhatsApp(1L);

        verify(whatsAppService).sendEventInvitationWhatsApp(1L);
    }

    @Test
    void sendEmergencyWhatsAppToAll_withValidMessage_succeeds() {
        doNothing().when(whatsAppService).sendEmergencyWhatsAppToAll("Emergency message");

        whatsAppService.sendEmergencyWhatsAppToAll("Emergency message");

        verify(whatsAppService).sendEmergencyWhatsAppToAll("Emergency message");
    }

    @Test
    void sendWelcomeWhatsApp_withValidStudentId_succeeds() {
        doNothing().when(whatsAppService).sendWelcomeWhatsApp(100L);

        whatsAppService.sendWelcomeWhatsApp(100L);

        verify(whatsAppService).sendWelcomeWhatsApp(100L);
    }

    @Test
    void sendBirthdayWhatsApp_withValidStudentId_succeeds() {
        doNothing().when(whatsAppService).sendBirthdayWhatsApp(100L);

        whatsAppService.sendBirthdayWhatsApp(100L);

        verify(whatsAppService).sendBirthdayWhatsApp(100L);
    }

    @Test
    void sendBulkWhatsAppMessages_withEmptyList_succeeds() {
        List<String> phoneNumbers = new ArrayList<>();

        doNothing().when(whatsAppService).sendBulkWhatsAppMessages(phoneNumbers, "Empty list message");

        whatsAppService.sendBulkWhatsAppMessages(phoneNumbers, "Empty list message");

        verify(whatsAppService).sendBulkWhatsAppMessages(phoneNumbers, "Empty list message");
    }

    @Test
    void sendWhatsAppMessageWithMedia_withNullMedia_returnsFalse() {
        when(whatsAppService.sendWhatsAppMessageWithMedia("+919876543210", "Test message", null)).thenReturn(false);

        boolean result = whatsAppService.sendWhatsAppMessageWithMedia("+919876543210", "Test message", null);

        assertFalse(result);
    }
}
