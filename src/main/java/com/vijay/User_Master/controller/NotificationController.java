package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.service.SchoolNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Email Notification Controller
 * Trigger various email notifications for students, parents, and teachers
 */
@RestController
@RequestMapping("/api/v1/notifications")
@AllArgsConstructor
@Slf4j
public class NotificationController {

    private final SchoolNotificationService notificationService;

    // ============= ATTENDANCE NOTIFICATIONS =============

    /**
     * Send daily attendance email to parent
     */
    @PostMapping("/attendance/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendDailyAttendance(
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Triggering daily attendance email for student ID: {}", studentId);
        notificationService.sendDailyAttendanceEmail(studentId, date);
        return ExceptionUtil.createBuildResponseMessage(
            "Daily attendance email sent successfully", HttpStatus.OK);
    }

    /**
     * Send bulk attendance notifications for entire class
     */
    @PostMapping("/attendance/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendBulkAttendanceNotifications(
            @RequestParam Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Triggering bulk attendance emails for class ID: {}", classId);
        notificationService.sendBulkAttendanceNotifications(classId, date);
        return ExceptionUtil.createBuildResponseMessage(
            "Bulk attendance notifications sent successfully", HttpStatus.OK);
    }

    /**
     * Send low attendance warning (for students below 75%)
     */
    @PostMapping("/attendance/low-warning")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendLowAttendanceWarning(@RequestParam Long studentId) {
        log.info("Sending low attendance warning for student ID: {}", studentId);
        notificationService.sendLowAttendanceWarning(studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Low attendance warning sent", HttpStatus.OK);
    }

    // ============= FEE NOTIFICATIONS =============

    /**
     * Send fee payment reminder
     */
    @PostMapping("/fees/reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendFeeReminder(
            @RequestParam Long studentId,
            @RequestParam Long feeId,
            @RequestParam(defaultValue = "7") Integer daysBeforeDue) {
        log.info("Sending fee reminder for student ID: {}, fee ID: {}", studentId, feeId);
        notificationService.sendFeeReminder(studentId, feeId, daysBeforeDue);
        return ExceptionUtil.createBuildResponseMessage(
            "Fee reminder sent successfully", HttpStatus.OK);
    }

    /**
     * Send bulk fee reminders to all students with pending fees
     */
    @PostMapping("/fees/bulk-reminders")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendBulkFeeReminders(
            @RequestParam(defaultValue = "7") Integer daysBeforeDue) {
        log.info("Sending bulk fee reminders for fees due in {} days", daysBeforeDue);
        notificationService.sendBulkFeeReminders(daysBeforeDue);
        return ExceptionUtil.createBuildResponseMessage(
            "Bulk fee reminders sent successfully", HttpStatus.OK);
    }

    /**
     * Send fee overdue notice
     */
    @PostMapping("/fees/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendFeeOverdueNotice(
            @RequestParam Long studentId,
            @RequestParam Long feeId) {
        log.info("Sending fee overdue notice for student ID: {}", studentId);
        notificationService.sendFeeOverdueNotice(studentId, feeId);
        return ExceptionUtil.createBuildResponseMessage(
            "Fee overdue notice sent", HttpStatus.OK);
    }

    /**
     * Send fee payment receipt
     */
    @PostMapping("/fees/receipt")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendFeeReceipt(
            @RequestParam Long studentId,
            @RequestParam Long feeId) {
        log.info("Sending fee receipt for student ID: {}", studentId);
        notificationService.sendFeePaymentReceipt(studentId, feeId);
        return ExceptionUtil.createBuildResponseMessage(
            "Fee receipt sent successfully", HttpStatus.OK);
    }

    // ============= GRADE NOTIFICATIONS =============

    /**
     * Send grade published notification
     */
    @PostMapping("/grades/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendGradePublishedNotification(
            @RequestParam Long studentId,
            @RequestParam Long gradeId) {
        log.info("Sending grade published notification for student ID: {}", studentId);
        notificationService.sendGradePublishedNotification(studentId, gradeId);
        return ExceptionUtil.createBuildResponseMessage(
            "Grade notification sent successfully", HttpStatus.OK);
    }

    /**
     * Send report card to parent
     */
    @PostMapping("/grades/report-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendReportCard(
            @RequestParam Long studentId,
            @RequestParam String semester) {
        log.info("Sending report card for student ID: {}, semester: {}", studentId, semester);
        notificationService.sendReportCard(studentId, semester);
        return ExceptionUtil.createBuildResponseMessage(
            "Report card sent successfully", HttpStatus.OK);
    }

    /**
     * Send weekly progress report
     */
    @PostMapping("/grades/weekly-progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendWeeklyProgress(@RequestParam Long studentId) {
        log.info("Sending weekly progress report for student ID: {}", studentId);
        notificationService.sendWeeklyProgressReport(studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Weekly progress report sent", HttpStatus.OK);
    }

    /**
     * Send failing grade alert
     */
    @PostMapping("/grades/failing-alert")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendFailingGradeAlert(
            @RequestParam Long studentId,
            @RequestParam Long subjectId) {
        log.info("Sending failing grade alert for student ID: {}", studentId);
        notificationService.sendFailingGradeAlert(studentId, subjectId);
        return ExceptionUtil.createBuildResponseMessage(
            "Failing grade alert sent", HttpStatus.OK);
    }

    // ============= EXAM NOTIFICATIONS =============

    /**
     * Send exam schedule notification to all students in exam class
     */
    @PostMapping("/exams/schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendExamSchedule(@RequestParam Long examId) {
        log.info("Sending exam schedule notification for exam ID: {}", examId);
        notificationService.sendExamScheduleNotification(examId);
        return ExceptionUtil.createBuildResponseMessage(
            "Exam schedule notifications sent to all students", HttpStatus.OK);
    }

    /**
     * Send exam reminder (1 day before exam)
     */
    @PostMapping("/exams/reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendExamReminder(@RequestParam Long examId) {
        log.info("Sending exam reminder for exam ID: {}", examId);
        notificationService.sendExamReminder(examId);
        return ExceptionUtil.createBuildResponseMessage(
            "Exam reminders sent successfully", HttpStatus.OK);
    }

    /**
     * Send exam result notification
     */
    @PostMapping("/exams/results")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendExamResults(
            @RequestParam Long examId,
            @RequestParam Long studentId) {
        log.info("Sending exam result for exam ID: {}, student ID: {}", examId, studentId);
        notificationService.sendExamResultNotification(examId, studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Exam result notification sent", HttpStatus.OK);
    }

    // ============= EVENT NOTIFICATIONS =============

    /**
     * Send event invitation to specified audience
     */
    @PostMapping("/events/invitation")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendEventInvitation(
            @RequestParam Long eventId,
            @RequestParam(defaultValue = "ALL") String audience) {
        log.info("Sending event invitation for event ID: {} to audience: {}", eventId, audience);
        notificationService.sendEventInvitation(eventId, audience);
        return ExceptionUtil.createBuildResponseMessage(
            "Event invitations sent successfully", HttpStatus.OK);
    }

    /**
     * Send event reminder
     */
    @PostMapping("/events/reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendEventReminder(
            @RequestParam Long eventId,
            @RequestParam(defaultValue = "1") Integer daysBeforeEvent) {
        log.info("Sending event reminder for event ID: {}", eventId);
        notificationService.sendEventReminder(eventId, daysBeforeEvent);
        return ExceptionUtil.createBuildResponseMessage(
            "Event reminders sent successfully", HttpStatus.OK);
    }

    /**
     * Send event cancellation notice
     */
    @PostMapping("/events/cancellation")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendEventCancellation(@RequestParam Long eventId) {
        log.info("Sending event cancellation for event ID: {}", eventId);
        notificationService.sendEventCancellation(eventId);
        return ExceptionUtil.createBuildResponseMessage(
            "Event cancellation notices sent", HttpStatus.OK);
    }

    // ============= ADMINISTRATIVE NOTIFICATIONS =============

    /**
     * Send welcome email to new student
     */
    @PostMapping("/welcome/student")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendStudentWelcomeEmail(@RequestParam Long studentId) {
        log.info("Sending welcome email for student ID: {}", studentId);
        notificationService.sendStudentWelcomeEmail(studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Welcome email sent successfully", HttpStatus.OK);
    }

    /**
     * Send welcome email to new teacher
     */
    @PostMapping("/welcome/teacher")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendTeacherWelcomeEmail(@RequestParam Long teacherId) {
        log.info("Sending welcome email for teacher ID: {}", teacherId);
        notificationService.sendTeacherWelcomeEmail(teacherId);
        return ExceptionUtil.createBuildResponseMessage(
            "Teacher welcome email sent", HttpStatus.OK);
    }

    /**
     * Send birthday wishes to student
     */
    @PostMapping("/birthday")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendBirthdayWishes(@RequestParam Long studentId) {
        log.info("Sending birthday wishes for student ID: {}", studentId);
        notificationService.sendBirthdayWishes(studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Birthday wishes sent successfully", HttpStatus.OK);
    }

    /**
     * Send announcement email
     */
    @PostMapping("/announcement")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendAnnouncementEmail(@RequestParam Long announcementId) {
        log.info("Sending announcement email for announcement ID: {}", announcementId);
        notificationService.sendAnnouncementEmail(announcementId);
        return ExceptionUtil.createBuildResponseMessage(
            "Announcement email sent", HttpStatus.OK);
    }

    /**
     * Send urgent announcement to all
     */
    @PostMapping("/urgent")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendUrgentAnnouncement(
            @RequestParam String subject,
            @RequestParam String message,
            @RequestParam(defaultValue = "ALL") String audience) {
        log.info("Sending urgent announcement to: {}", audience);
        notificationService.sendUrgentAnnouncement(subject, message, audience);
        return ExceptionUtil.createBuildResponseMessage(
            "Urgent announcement sent to " + audience, HttpStatus.OK);
    }

    /**
     * Send assignment deadline reminder
     */
    @PostMapping("/assignment/reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> sendAssignmentReminder(
            @RequestParam Long assignmentId,
            @RequestParam Long studentId) {
        log.info("Sending assignment reminder for assignment ID: {}", assignmentId);
        notificationService.sendAssignmentReminder(assignmentId, studentId);
        return ExceptionUtil.createBuildResponseMessage(
            "Assignment reminder sent", HttpStatus.OK);
    }

    /**
     * Send library book overdue notice
     */
    @PostMapping("/library/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> sendLibraryOverdueNotice(@RequestParam Long bookIssueId) {
        log.info("Sending library overdue notice for book issue ID: {}", bookIssueId);
        notificationService.sendLibraryOverdueNotice(bookIssueId);
        return ExceptionUtil.createBuildResponseMessage(
            "Library overdue notice sent", HttpStatus.OK);
    }
}

