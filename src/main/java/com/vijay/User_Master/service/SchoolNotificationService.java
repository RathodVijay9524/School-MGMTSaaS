package com.vijay.User_Master.service;

import com.vijay.User_Master.entity.*;

import java.time.LocalDate;
import java.util.List;

/**
 * School Notification Service
 * Handles all email notifications for school operations
 * Uses existing EmailService infrastructure
 */
public interface SchoolNotificationService {

    // ============= ATTENDANCE NOTIFICATIONS =============
    
    /**
     * Send daily attendance notification to parent
     */
    void sendDailyAttendanceEmail(Long studentId, LocalDate date);
    
    /**
     * Send bulk attendance notifications to all parents
     */
    void sendBulkAttendanceNotifications(Long classId, LocalDate date);
    
    /**
     * Send low attendance warning (below 75%)
     */
    void sendLowAttendanceWarning(Long studentId);
    
    // ============= FEE NOTIFICATIONS =============
    
    /**
     * Send fee payment reminder
     */
    void sendFeeReminder(Long studentId, Long feeId, Integer daysBeforeDue);
    
    /**
     * Send fee overdue notice
     */
    void sendFeeOverdueNotice(Long studentId, Long feeId);
    
    /**
     * Send fee payment receipt
     */
    void sendFeePaymentReceipt(Long studentId, Long feeId);
    
    /**
     * Send bulk fee reminders to all students with pending fees
     */
    void sendBulkFeeReminders(Integer daysBeforeDue);
    
    // ============= GRADE NOTIFICATIONS =============
    
    /**
     * Send grade published notification
     */
    void sendGradePublishedNotification(Long studentId, Long gradeId);
    
    /**
     * Send report card to parent
     */
    void sendReportCard(Long studentId, String semester);
    
    /**
     * Send weekly progress report
     */
    void sendWeeklyProgressReport(Long studentId);
    
    /**
     * Send failing grade alert
     */
    void sendFailingGradeAlert(Long studentId, Long subjectId);
    
    // ============= EXAM NOTIFICATIONS =============
    
    /**
     * Send exam schedule notification
     */
    void sendExamScheduleNotification(Long examId);
    
    /**
     * Send exam reminder (1 day before)
     */
    void sendExamReminder(Long examId);
    
    /**
     * Send exam result notification
     */
    void sendExamResultNotification(Long examId, Long studentId);
    
    // ============= EVENT NOTIFICATIONS =============
    
    /**
     * Send event invitation
     */
    void sendEventInvitation(Long eventId, String audience);
    
    /**
     * Send event reminder
     */
    void sendEventReminder(Long eventId, Integer daysBeforeEvent);
    
    /**
     * Send event cancellation notice
     */
    void sendEventCancellation(Long eventId);
    
    // ============= ANNOUNCEMENT NOTIFICATIONS =============
    
    /**
     * Send announcement email
     */
    void sendAnnouncementEmail(Long announcementId);
    
    /**
     * Send urgent announcement to all
     */
    void sendUrgentAnnouncement(String subject, String message, String audience);
    
    // ============= ADMINISTRATIVE NOTIFICATIONS =============
    
    /**
     * Send welcome email to new student
     */
    void sendStudentWelcomeEmail(Long studentId);
    
    /**
     * Send welcome email to new teacher
     */
    void sendTeacherWelcomeEmail(Long teacherId);
    
    /**
     * Send birthday wishes
     */
    void sendBirthdayWishes(Long studentId);
    
    /**
     * Send assignment deadline reminder
     */
    void sendAssignmentReminder(Long assignmentId, Long studentId);
    
    /**
     * Send library book overdue notice
     */
    void sendLibraryOverdueNotice(Long bookIssueId);
    
    // ============= SCHEDULED NOTIFICATIONS =============
    
    /**
     * Send all scheduled notifications for today
     */
    void sendScheduledNotifications();
    
    /**
     * Send monthly summary to parents
     */
    void sendMonthlySummary(Long studentId);
}

