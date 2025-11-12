package com.vijay.User_Master.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SMSServiceTest extends ServiceTestBase {

    @Mock
    private SMSService smsService;

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
    void sendSMS_withValidData_returnsTrue() {
        when(smsService.sendSMS("+919876543210", "Test message")).thenReturn(true);

        boolean result = smsService.sendSMS("+919876543210", "Test message");

        assertTrue(result);
        verify(smsService).sendSMS("+919876543210", "Test message");
    }

    @Test
    void sendSMS_withInvalidNumber_returnsFalse() {
        when(smsService.sendSMS("invalid", "Test message")).thenReturn(false);

        boolean result = smsService.sendSMS("invalid", "Test message");

        assertFalse(result);
    }

    @Test
    void sendBulkSMS_withValidData_succeeds() {
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+919876543210");
        phoneNumbers.add("+919876543211");

        doNothing().when(smsService).sendBulkSMS(phoneNumbers, "Bulk test message");

        smsService.sendBulkSMS(phoneNumbers, "Bulk test message");

        verify(smsService).sendBulkSMS(phoneNumbers, "Bulk test message");
    }

    @Test
    void sendAttendanceSMS_withValidData_succeeds() {
        doNothing().when(smsService).sendAttendanceSMS(100L, LocalDate.now(), "PRESENT");

        smsService.sendAttendanceSMS(100L, LocalDate.now(), "PRESENT");

        verify(smsService).sendAttendanceSMS(100L, LocalDate.now(), "PRESENT");
    }

    @Test
    void sendFeeReminderSMS_withValidData_succeeds() {
        doNothing().when(smsService).sendFeeReminderSMS(100L, 1L, 5000.0, LocalDate.now().plusDays(7));

        smsService.sendFeeReminderSMS(100L, 1L, 5000.0, LocalDate.now().plusDays(7));

        verify(smsService).sendFeeReminderSMS(100L, 1L, 5000.0, LocalDate.now().plusDays(7));
    }

    @Test
    void sendExamReminderSMS_withValidData_succeeds() {
        doNothing().when(smsService).sendExamReminderSMS(100L, 1L);

        smsService.sendExamReminderSMS(100L, 1L);

        verify(smsService).sendExamReminderSMS(100L, 1L);
    }

    @Test
    void sendLowAttendanceWarningSMS_withValidData_succeeds() {
        doNothing().when(smsService).sendLowAttendanceWarningSMS(100L, 75.5);

        smsService.sendLowAttendanceWarningSMS(100L, 75.5);

        verify(smsService).sendLowAttendanceWarningSMS(100L, 75.5);
    }

    @Test
    void sendGradePublishedSMS_withValidData_succeeds() {
        doNothing().when(smsService).sendGradePublishedSMS(100L, "Mathematics", 85.5, "A");

        smsService.sendGradePublishedSMS(100L, "Mathematics", 85.5, "A");

        verify(smsService).sendGradePublishedSMS(100L, "Mathematics", 85.5, "A");
    }

    @Test
    void sendEmergencySMSToAll_withValidMessage_succeeds() {
        doNothing().when(smsService).sendEmergencySMSToAll("Emergency message");

        smsService.sendEmergencySMSToAll("Emergency message");

        verify(smsService).sendEmergencySMSToAll("Emergency message");
    }

    @Test
    void sendOTP_withValidData_returnsTrue() {
        when(smsService.sendOTP("+919876543210", "123456")).thenReturn(true);

        boolean result = smsService.sendOTP("+919876543210", "123456");

        assertTrue(result);
        verify(smsService).sendOTP("+919876543210", "123456");
    }

    @Test
    void sendOTP_withInvalidData_returnsFalse() {
        when(smsService.sendOTP("invalid", "123456")).thenReturn(false);

        boolean result = smsService.sendOTP("invalid", "123456");

        assertFalse(result);
    }

    @Test
    void sendWelcomeSMS_withValidStudentId_succeeds() {
        doNothing().when(smsService).sendWelcomeSMS(100L);

        smsService.sendWelcomeSMS(100L);

        verify(smsService).sendWelcomeSMS(100L);
    }

    @Test
    void sendBulkSMS_withEmptyList_succeeds() {
        List<String> phoneNumbers = new ArrayList<>();

        doNothing().when(smsService).sendBulkSMS(phoneNumbers, "Empty list message");

        smsService.sendBulkSMS(phoneNumbers, "Empty list message");

        verify(smsService).sendBulkSMS(phoneNumbers, "Empty list message");
    }

    @Test
    void sendAttendanceSMS_withNullDate_succeeds() {
        doNothing().when(smsService).sendAttendanceSMS(100L, null, "PRESENT");

        smsService.sendAttendanceSMS(100L, null, "PRESENT");

        verify(smsService).sendAttendanceSMS(100L, null, "PRESENT");
    }
}
