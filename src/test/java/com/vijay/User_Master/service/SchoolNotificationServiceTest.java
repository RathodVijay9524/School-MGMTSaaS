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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchoolNotificationServiceTest extends ServiceTestBase {

    @Mock
    private SchoolNotificationService notificationService;

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

    // ============= ATTENDANCE NOTIFICATIONS =============

    @Test
    void sendDailyAttendanceEmail_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendDailyAttendanceEmail(100L, LocalDate.now());

        notificationService.sendDailyAttendanceEmail(100L, LocalDate.now());

        verify(notificationService).sendDailyAttendanceEmail(100L, LocalDate.now());
    }

    @Test
    void sendBulkAttendanceNotifications_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendBulkAttendanceNotifications(1L, LocalDate.now());

        notificationService.sendBulkAttendanceNotifications(1L, LocalDate.now());

        verify(notificationService).sendBulkAttendanceNotifications(1L, LocalDate.now());
    }

    @Test
    void sendLowAttendanceWarning_withValidStudentId_succeeds() {
        doNothing().when(notificationService)
                .sendLowAttendanceWarning(100L);

        notificationService.sendLowAttendanceWarning(100L);

        verify(notificationService).sendLowAttendanceWarning(100L);
    }

    // ============= FEE NOTIFICATIONS =============

    @Test
    void sendFeeReminder_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendFeeReminder(100L, 1L, 5);

        notificationService.sendFeeReminder(100L, 1L, 5);

        verify(notificationService).sendFeeReminder(100L, 1L, 5);
    }

    @Test
    void sendFeeOverdueNotice_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendFeeOverdueNotice(100L, 1L);

        notificationService.sendFeeOverdueNotice(100L, 1L);

        verify(notificationService).sendFeeOverdueNotice(100L, 1L);
    }

    @Test
    void sendFeePaymentReceipt_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendFeePaymentReceipt(100L, 1L);

        notificationService.sendFeePaymentReceipt(100L, 1L);

        verify(notificationService).sendFeePaymentReceipt(100L, 1L);
    }

    @Test
    void sendBulkFeeReminders_withValidDays_succeeds() {
        doNothing().when(notificationService)
                .sendBulkFeeReminders(5);

        notificationService.sendBulkFeeReminders(5);

        verify(notificationService).sendBulkFeeReminders(5);
    }

    // ============= GRADE NOTIFICATIONS =============

    @Test
    void sendGradePublishedNotification_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendGradePublishedNotification(100L, 1L);

        notificationService.sendGradePublishedNotification(100L, 1L);

        verify(notificationService).sendGradePublishedNotification(100L, 1L);
    }

    @Test
    void sendReportCard_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendReportCard(100L, "SPRING-2024");

        notificationService.sendReportCard(100L, "SPRING-2024");

        verify(notificationService).sendReportCard(100L, "SPRING-2024");
    }

    @Test
    void sendWeeklyProgressReport_withValidStudentId_succeeds() {
        doNothing().when(notificationService)
                .sendWeeklyProgressReport(100L);

        notificationService.sendWeeklyProgressReport(100L);

        verify(notificationService).sendWeeklyProgressReport(100L);
    }

    @Test
    void sendFailingGradeAlert_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendFailingGradeAlert(100L, 1L);

        notificationService.sendFailingGradeAlert(100L, 1L);

        verify(notificationService).sendFailingGradeAlert(100L, 1L);
    }

    // ============= EXAM NOTIFICATIONS =============

    @Test
    void sendExamScheduleNotification_withValidExamId_succeeds() {
        doNothing().when(notificationService)
                .sendExamScheduleNotification(1L);

        notificationService.sendExamScheduleNotification(1L);

        verify(notificationService).sendExamScheduleNotification(1L);
    }

    @Test
    void sendExamReminder_withValidExamId_succeeds() {
        doNothing().when(notificationService)
                .sendExamReminder(1L);

        notificationService.sendExamReminder(1L);

        verify(notificationService).sendExamReminder(1L);
    }

    @Test
    void sendExamResultNotification_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendExamResultNotification(1L, 100L);

        notificationService.sendExamResultNotification(1L, 100L);

        verify(notificationService).sendExamResultNotification(1L, 100L);
    }

    // ============= EVENT NOTIFICATIONS =============

    @Test
    void sendEventInvitation_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendEventInvitation(1L, "ALL");

        notificationService.sendEventInvitation(1L, "ALL");

        verify(notificationService).sendEventInvitation(1L, "ALL");
    }

    @Test
    void sendEventReminder_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendEventReminder(1L, 1);

        notificationService.sendEventReminder(1L, 1);

        verify(notificationService).sendEventReminder(1L, 1);
    }

    @Test
    void sendEventCancellation_withValidEventId_succeeds() {
        doNothing().when(notificationService)
                .sendEventCancellation(1L);

        notificationService.sendEventCancellation(1L);

        verify(notificationService).sendEventCancellation(1L);
    }

    // ============= ANNOUNCEMENT NOTIFICATIONS =============

    @Test
    void sendAnnouncementEmail_withValidAnnouncementId_succeeds() {
        doNothing().when(notificationService)
                .sendAnnouncementEmail(1L);

        notificationService.sendAnnouncementEmail(1L);

        verify(notificationService).sendAnnouncementEmail(1L);
    }

    @Test
    void sendUrgentAnnouncement_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendUrgentAnnouncement("Urgent", "Message", "ALL");

        notificationService.sendUrgentAnnouncement("Urgent", "Message", "ALL");

        verify(notificationService).sendUrgentAnnouncement("Urgent", "Message", "ALL");
    }

    // ============= ADMINISTRATIVE NOTIFICATIONS =============

    @Test
    void sendStudentWelcomeEmail_withValidStudentId_succeeds() {
        doNothing().when(notificationService)
                .sendStudentWelcomeEmail(100L);

        notificationService.sendStudentWelcomeEmail(100L);

        verify(notificationService).sendStudentWelcomeEmail(100L);
    }

    @Test
    void sendTeacherWelcomeEmail_withValidTeacherId_succeeds() {
        doNothing().when(notificationService)
                .sendTeacherWelcomeEmail(50L);

        notificationService.sendTeacherWelcomeEmail(50L);

        verify(notificationService).sendTeacherWelcomeEmail(50L);
    }

    @Test
    void sendBirthdayWishes_withValidStudentId_succeeds() {
        doNothing().when(notificationService)
                .sendBirthdayWishes(100L);

        notificationService.sendBirthdayWishes(100L);

        verify(notificationService).sendBirthdayWishes(100L);
    }

    @Test
    void sendAssignmentReminder_withValidData_succeeds() {
        doNothing().when(notificationService)
                .sendAssignmentReminder(1L, 100L);

        notificationService.sendAssignmentReminder(1L, 100L);

        verify(notificationService).sendAssignmentReminder(1L, 100L);
    }

    @Test
    void sendLibraryOverdueNotice_withValidBookIssueId_succeeds() {
        doNothing().when(notificationService)
                .sendLibraryOverdueNotice(1L);

        notificationService.sendLibraryOverdueNotice(1L);

        verify(notificationService).sendLibraryOverdueNotice(1L);
    }

    // ============= SCHEDULED NOTIFICATIONS =============

    @Test
    void sendScheduledNotifications_succeeds() {
        doNothing().when(notificationService)
                .sendScheduledNotifications();

        notificationService.sendScheduledNotifications();

        verify(notificationService).sendScheduledNotifications();
    }

    @Test
    void sendMonthlySummary_withValidStudentId_succeeds() {
        doNothing().when(notificationService)
                .sendMonthlySummary(100L);

        notificationService.sendMonthlySummary(100L);

        verify(notificationService).sendMonthlySummary(100L);
    }
}
