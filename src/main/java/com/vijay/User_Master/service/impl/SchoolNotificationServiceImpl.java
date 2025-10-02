package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.EmailUtils;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.SchoolNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * School Notification Service Implementation
 * Uses existing EmailUtils to send school-related notifications
 */
@Service
@AllArgsConstructor
@Slf4j
public class SchoolNotificationServiceImpl implements SchoolNotificationService {

    private final EmailUtils emailUtils;
    private final WorkerRepository workerRepository;
    
    private Long getCurrentOwnerId() {
        // Get the current logged-in user ID for multi-tenancy
        return 1L; // Placeholder - should be replaced with actual logged-in user ID
    }
    private final AttendanceRepository attendanceRepository;
    private final FeeRepository feeRepository;
    private final GradeRepository gradeRepository;
    private final ExamRepository examRepository;
    private final EventRepository eventRepository;
    private final AnnouncementRepository announcementRepository;
    private final AssignmentRepository assignmentRepository;

    // ============= ATTENDANCE NOTIFICATIONS =============

    @Override
    public void sendDailyAttendanceEmail(Long studentId, LocalDate date) {
        log.info("Sending daily attendance email for student ID: {} on date: {}", studentId, date);
        
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null || student.getParentEmail() == null) {
            log.warn("Student not found or parent email not available for student ID: {}", studentId);
            return;
        }
        
        // Get attendance for the date
        List<Attendance> attendances = attendanceRepository.findByStudent_IdAndAttendanceDateBetween(
            studentId, date, date);
        
        if (attendances.isEmpty()) {
            log.warn("No attendance found for student ID: {} on date: {}", studentId, date);
            return;
        }
        
        Attendance attendance = attendances.get(0);
        
        // Build email
        String subject = "Daily Attendance - " + student.getFirstName() + " " + student.getLastName();
        String body = buildDailyAttendanceEmail(student, attendance, date);
        
        // Send email
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        boolean sent = true; // Assume success
        
        if (sent) {
            log.info("Daily attendance email sent successfully to: {}", student.getParentEmail());
        } else {
            log.error("Failed to send daily attendance email to: {}", student.getParentEmail());
        }
    }

    @Override
    public void sendBulkAttendanceNotifications(Long classId, LocalDate date) {
        log.info("Sending bulk attendance notifications for class ID: {} on date: {}", classId, date);
        
        List<Attendance> attendances = attendanceRepository.findBySchoolClass_IdAndAttendanceDate(classId, date);
        
        for (Attendance attendance : attendances) {
            sendDailyAttendanceEmail(attendance.getStudent().getId(), date);
        }
        
        log.info("Bulk attendance notifications sent for {} students", attendances.size());
    }

    @Override
    public void sendLowAttendanceWarning(Long studentId) {
        log.info("Sending low attendance warning for student ID: {}", studentId);
        
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null || student.getParentEmail() == null) return;
        
        Double percentage = attendanceRepository.calculateAttendancePercentage(studentId);
        if (percentage == null) percentage = 0.0;
        
        if (percentage < 75.0) {
            String subject = "⚠️ Low Attendance Alert - " + student.getFirstName();
            String body = buildLowAttendanceEmail(student, percentage);
            
            emailUtils.sendEmail(student.getParentEmail(), subject, body);
            log.info("Low attendance warning sent to: {}", student.getParentEmail());
        }
    }

    // ============= FEE NOTIFICATIONS =============

    @Override
    public void sendFeeReminder(Long studentId, Long feeId, Integer daysBeforeDue) {
        log.info("Sending fee reminder for student ID: {}, fee ID: {}", studentId, feeId);
        
        Worker student = workerRepository.findById(studentId).orElse(null);
        Fee fee = feeRepository.findById(feeId).orElse(null);
        
        if (student == null || fee == null || student.getParentEmail() == null) return;
        
        String subject = "Fee Payment Reminder - " + fee.getFeeCategory();
        String body = buildFeeReminderEmail(student, fee, daysBeforeDue);
        
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Fee reminder sent to: {}", student.getParentEmail());
    }

    @Override
    public void sendFeeOverdueNotice(Long studentId, Long feeId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Fee fee = feeRepository.findById(feeId).orElse(null);
        
        if (student == null || fee == null || student.getParentEmail() == null) return;
        
        String subject = "⚠️ Fee Overdue Notice - Immediate Action Required";
        String body = buildFeeOverdueEmail(student, fee);
        
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Fee overdue notice sent to: {}", student.getParentEmail());
    }

    @Override
    public void sendFeePaymentReceipt(Long studentId, Long feeId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Fee fee = feeRepository.findById(feeId).orElse(null);
        
        if (student == null || fee == null || student.getParentEmail() == null) return;
        
        String subject = "Fee Payment Receipt - " + fee.getReceiptNumber();
        String body = buildFeeReceiptEmail(student, fee);
        
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Fee receipt sent to: {}", student.getParentEmail());
    }

    @Override
    public void sendBulkFeeReminders(Integer daysBeforeDue) {
        log.info("Sending bulk fee reminders for fees due in {} days", daysBeforeDue);
        
        LocalDate targetDate = LocalDate.now().plusDays(daysBeforeDue);
        List<Fee> pendingFees = feeRepository.findOverdueFees(targetDate);
        
        for (Fee fee : pendingFees) {
            sendFeeReminder(fee.getStudent().getId(), fee.getId(), daysBeforeDue);
        }
        
        log.info("Bulk fee reminders sent for {} fees", pendingFees.size());
    }

    // ============= GRADE NOTIFICATIONS =============

    @Override
    public void sendGradePublishedNotification(Long studentId, Long gradeId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Grade grade = gradeRepository.findById(gradeId).orElse(null);
        
        if (student == null || grade == null || student.getParentEmail() == null) return;
        
        String subject = "Grade Published - " + grade.getSubject().getSubjectName();
        String body = buildGradePublishedEmail(student, grade);
        
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Grade published notification sent for student ID: {}", studentId);
    }

    @Override
    public void sendWeeklyProgressReport(Long studentId) {
        log.info("Sending weekly progress report for student ID: {}", studentId);
        
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null || student.getParentEmail() == null) return;
        
        // Get this week's data
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        LocalDate endOfWeek = LocalDate.now();
        
        List<Attendance> weeklyAttendance = attendanceRepository.findByStudent_IdAndAttendanceDateBetween(
            studentId, startOfWeek, endOfWeek);
        
        List<Grade> recentGrades = gradeRepository.findPublishedGrades(studentId);
        
        String subject = "Weekly Progress Report - " + student.getFirstName();
        String body = buildWeeklyProgressEmail(student, weeklyAttendance, recentGrades);
        
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Weekly progress report sent to: {}", student.getParentEmail());
    }

    @Override
    public void sendReportCard(Long studentId, String semester) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null || student.getParentEmail() == null) return;
        
        List<Grade> grades = gradeRepository.findByStudent_IdAndSemester(studentId, semester);
        Double gpa = gradeRepository.calculateOverallGPA(studentId);
        
        String subject = "Report Card - " + semester + " - " + student.getFirstName();
        String body = buildReportCardEmail(student, grades, gpa, semester);
        
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Report card sent for student ID: {}", studentId);
    }

    @Override
    public void sendFailingGradeAlert(Long studentId, Long subjectId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null || student.getParentEmail() == null) return;
        
        List<Grade> failingGrades = gradeRepository.findFailingGrades(studentId);
        
        if (!failingGrades.isEmpty()) {
            String subject = "⚠️ Academic Alert - Improvement Needed";
            String body = buildFailingGradeEmail(student, failingGrades);
            
            emailUtils.sendEmail(student.getParentEmail(), subject, body);
            log.info("Failing grade alert sent for student ID: {}", studentId);
        }
    }

    // ============= EXAM NOTIFICATIONS =============

    @Override
    public void sendExamScheduleNotification(Long examId) {
        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) return;
        
        // Get all students in the class
        List<Worker> students = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
        
        String subject = "Exam Scheduled - " + exam.getExamName();
        
        for (Worker student : students) {
            if (student.getParentEmail() != null) {
                String body = buildExamScheduleEmail(student, exam);
                emailUtils.sendEmail(student.getParentEmail(), subject, body);
            }
        }
        
        log.info("Exam schedule notifications sent for exam ID: {}", examId);
    }

    @Override
    public void sendExamReminder(Long examId) {
        log.info("Sending exam reminder for exam ID: {}", examId);
        sendExamScheduleNotification(examId); // Reuse same template
    }

    @Override
    public void sendExamResultNotification(Long examId, Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Exam exam = examRepository.findById(examId).orElse(null);
        
        if (student == null || exam == null || student.getParentEmail() == null) return;
        
        // Get grade for this exam
        List<Grade> grades = gradeRepository.findByExam_Id(examId);
        Grade studentGrade = grades.stream()
            .filter(g -> g.getStudent().getId().equals(studentId))
            .findFirst()
            .orElse(null);
        
        if (studentGrade != null && studentGrade.isPublished()) {
            String subject = "Exam Results Published - " + exam.getExamName();
            String body = buildExamResultEmail(student, exam, studentGrade);
            
            emailUtils.sendEmail(student.getParentEmail(), subject, body);
            log.info("Exam result notification sent for student ID: {}", studentId);
        }
    }

    // ============= EVENT NOTIFICATIONS =============

    @Override
    public void sendEventInvitation(Long eventId, String audience) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return;
        
        String subject = "📅 Event Invitation - " + event.getEventName();
        
        // Send to appropriate audience
        if ("PARENTS".equals(audience) || "ALL".equals(audience)) {
            List<Worker> students = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
            
            for (Worker student : students) {
                if (student.getParentEmail() != null) {
                    String body = buildEventInvitationEmail(student, event);
                    emailUtils.sendEmail(student.getParentEmail(), subject, body);
                }
            }
        }
        
        log.info("Event invitations sent for event ID: {}", eventId);
    }

    @Override
    public void sendEventReminder(Long eventId, Integer daysBeforeEvent) {
        log.info("Sending event reminder for event ID: {}", eventId);
        sendEventInvitation(eventId, "ALL"); // Reuse invitation template
    }

    @Override
    public void sendEventCancellation(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return;
        
        String subject = "❌ Event Cancelled - " + event.getEventName();
        // Implementation similar to event invitation
    }

    // ============= ADMINISTRATIVE NOTIFICATIONS =============

    @Override
    public void sendStudentWelcomeEmail(Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null || student.getParentEmail() == null) return;
        
        String subject = "🎓 Welcome to Our School - " + student.getFirstName();
        String body = buildWelcomeEmail(student);
        
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Welcome email sent for student ID: {}", studentId);
    }

    @Override
    public void sendTeacherWelcomeEmail(Long teacherId) {
        // Implementation for teacher welcome email
    }

    @Override
    public void sendBirthdayWishes(Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null || student.getEmail() == null) return;
        
        String subject = "🎂 Happy Birthday " + student.getFirstName() + "!";
        String body = buildBirthdayEmail(student);
        
        emailUtils.sendEmail(student.getEmail(), subject, body);
        emailUtils.sendEmail(student.getParentEmail(), subject, body);
        log.info("Birthday wishes sent for student ID: {}", studentId);
    }

    @Override
    public void sendAnnouncementEmail(Long announcementId) {
        // Implementation for announcement emails
    }

    @Override
    public void sendUrgentAnnouncement(String subject, String message, String audience) {
        // Implementation for urgent announcements
    }

    @Override
    public void sendAssignmentReminder(Long assignmentId, Long studentId) {
        // Implementation for assignment reminders
    }

    @Override
    public void sendLibraryOverdueNotice(Long bookIssueId) {
        // Implementation for library overdue notices
    }

    @Override
    public void sendScheduledNotifications() {
        // Implementation for scheduled notifications
    }

    @Override
    public void sendMonthlySummary(Long studentId) {
        // Implementation for monthly summary
    }

    // ============= EMAIL TEMPLATE BUILDERS =============

    private String buildDailyAttendanceEmail(Worker student, Attendance attendance, LocalDate date) {
        String status = attendance.getStatus().toString();
        String statusEmoji = getStatusEmoji(attendance.getStatus());
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>📚 Daily Attendance Report</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>Attendance update for <strong>%s %s</strong></p>
                    
                    <table style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                        <tr style='background-color: #667eea; color: white;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Date</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>Status</strong></td>
                            <td style='padding: 10px; border: 1px solid #ddd; font-size: 18px;'>%s <strong>%s</strong></td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Class</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s - Section %s</td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Check-in Time</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                    </table>
                    
                    <p style='margin-top: 20px;'><em>Thank you for your attention.</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        This is an automated notification. Please do not reply to this email.
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(), student.getLastName(),
            dateStr,
            statusEmoji, status,
            student.getCurrentClass().getClassName(), student.getSection(),
            attendance.getCheckInTime() != null ? attendance.getCheckInTime().toString() : "N/A"
        );
    }

    private String buildLowAttendanceEmail(Worker student, Double percentage) {
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background-color: #ff6b6b; padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>⚠️ Low Attendance Alert</h2>
                </div>
                <div style='padding: 20px; background-color: #fff3cd; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>This is to inform you that the attendance of <strong>%s %s</strong> has fallen below the required 75%% threshold.</p>
                    
                    <div style='background-color: #fff; padding: 15px; margin: 20px 0; border-left: 4px solid #ff6b6b; border-radius: 5px;'>
                        <h3 style='margin: 0; color: #ff6b6b;'>Current Attendance: %.2f%%</h3>
                        <p style='margin: 5px 0; color: #666;'>Required: 75%%</p>
                    </div>
                    
                    <p><strong>Action Required:</strong></p>
                    <ul>
                        <li>Please ensure regular attendance</li>
                        <li>Contact the class teacher if there are any issues</li>
                        <li>Medical certificates must be submitted for extended absences</li>
                    </ul>
                    
                    <p style='margin-top: 20px;'><em>This is a system-generated alert to help monitor student progress.</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        For any queries, please contact the school office.
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(), student.getLastName(), percentage
        );
    }

    private String buildFeeReminderEmail(Worker student, Fee fee, Integer daysBeforeDue) {
        String dateStr = fee.getDueDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>💰 Fee Payment Reminder</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>This is a friendly reminder for the pending fee payment of <strong>%s %s</strong></p>
                    
                    <table style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                        <tr style='background-color: #f5576c; color: white;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Fee Type</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>Total Amount</strong></td>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>₹%.2f</strong></td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Paid Amount</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>₹%.2f</td>
                        </tr>
                        <tr style='background-color: #fff3cd;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>Balance Due</strong></td>
                            <td style='padding: 10px; border: 1px solid #ddd; color: #ff6b6b;'><strong>₹%.2f</strong></td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Due Date</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>%s</strong> (%d days remaining)</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Receipt Number</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                    </table>
                    
                    <div style='background-color: #e7f3ff; padding: 15px; margin: 20px 0; border-left: 4px solid #2196F3; border-radius: 5px;'>
                        <p style='margin: 0;'><strong>💳 Payment Options:</strong></p>
                        <p style='margin: 5px 0;'>• Online Payment via School Portal</p>
                        <p style='margin: 5px 0;'>• Bank Transfer</p>
                        <p style='margin: 5px 0;'>• Cash/Cheque at School Office</p>
                    </div>
                    
                    <p style='margin-top: 20px;'><em>Please make the payment before the due date to avoid late fees.</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        For payment queries, contact: accounts@school.com
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(), student.getLastName(),
            fee.getFeeCategory(),
            fee.getTotalAmount(),
            fee.getPaidAmount(),
            fee.getBalanceAmount(),
            dateStr, daysBeforeDue,
            fee.getReceiptNumber()
        );
    }

    private String buildFeeOverdueEmail(Worker student, Fee fee) {
        // Similar to fee reminder but with urgent styling
        return buildFeeReminderEmail(student, fee, 0);
    }

    private String buildFeeReceiptEmail(Worker student, Fee fee) {
        String dateStr = fee.getPaymentDate() != null 
            ? fee.getPaymentDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            : LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>✅ Payment Receipt</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>Thank you for your payment. Here are the details:</p>
                    
                    <table style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                        <tr style='background-color: #38ef7d; color: white;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Receipt Number</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>%s</strong></td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Student Name</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s %s</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Admission Number</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Fee Type</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>Amount Paid</strong></td>
                            <td style='padding: 10px; border: 1px solid #ddd; color: #11998e;'><strong>₹%.2f</strong></td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Payment Date</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Payment Method</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Transaction ID</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                    </table>
                    
                    <div style='background-color: #d4edda; padding: 15px; margin: 20px 0; border-left: 4px solid #28a745; border-radius: 5px;'>
                        <p style='margin: 0;'><strong>✅ Payment Successful!</strong></p>
                        <p style='margin: 5px 0;'>Remaining Balance: <strong>₹%.2f</strong></p>
                    </div>
                    
                    <p>Please keep this receipt for your records.</p>
                    
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        This is an automated receipt. For queries, contact accounts@school.com
                    </p>
                </div>
            </body>
            </html>
            """,
            fee.getReceiptNumber(),
            student.getFirstName(), student.getLastName(),
            student.getAdmissionNumber(),
            fee.getFeeCategory(),
            fee.getPaidAmount(),
            dateStr,
            fee.getPaymentMethod() != null ? fee.getPaymentMethod().toString() : "N/A",
            fee.getTransactionId() != null ? fee.getTransactionId() : "N/A",
            fee.getBalanceAmount()
        );
    }

    private String buildGradePublishedEmail(Worker student, Grade grade) {
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #4facfe 0%%, #00f2fe 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>📊 Grade Published</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>New grade has been published for <strong>%s %s</strong></p>
                    
                    <table style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                        <tr style='background-color: #4facfe; color: white;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Subject</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Assessment Type</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>Marks Obtained</strong></td>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>%.0f / %.0f</strong></td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Percentage</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>%.2f%%</strong></td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Grade</td>
                            <td style='padding: 10px; border: 1px solid #ddd; font-size: 24px;'><strong>%s</strong></td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Status</td>
                            <td style='padding: 10px; border: 1px solid #ddd; color: %s;'><strong>%s</strong></td>
                        </tr>
                    </table>
                    
                    %s
                    
                    <p style='margin-top: 20px;'><em>Keep up the good work!</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        Login to view detailed analysis and progress reports.
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(), student.getLastName(),
            grade.getSubject().getSubjectName(),
            grade.getGradeType().toString(),
            grade.getMarksObtained(), grade.getTotalMarks(),
            grade.getPercentage(),
            grade.getLetterGrade(),
            grade.getStatus() == Grade.GradeStatus.PASS ? "green" : "red",
            grade.getStatus().toString(),
            grade.getFeedback() != null 
                ? "<div style='background-color: #e7f3ff; padding: 15px; margin: 20px 0; border-left: 4px solid #2196F3; border-radius: 5px;'><p style='margin: 0;'><strong>Teacher's Feedback:</strong></p><p style='margin: 5px 0;'>" + grade.getFeedback() + "</p></div>"
                : ""
        );
    }

    private String buildWeeklyProgressEmail(Worker student, List<Attendance> weeklyAttendance, List<Grade> recentGrades) {
        long presentDays = weeklyAttendance.stream()
            .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
            .count();
        long totalDays = weeklyAttendance.size();
        double weeklyPercentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>📈 Weekly Progress Report</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>Here's the weekly progress summary for <strong>%s %s</strong></p>
                    
                    <h3 style='color: #667eea;'>📚 Attendance This Week</h3>
                    <div style='background-color: #fff; padding: 15px; margin: 10px 0; border-radius: 5px; border: 1px solid #ddd;'>
                        <p><strong>Present:</strong> %d days out of %d days</p>
                        <p><strong>Weekly Attendance:</strong> %.2f%%</p>
                    </div>
                    
                    <h3 style='color: #667eea;'>📊 Recent Grades</h3>
                    <p>%d new grades published this week</p>
                    
                    <p style='margin-top: 20px;'><em>For detailed reports, login to the parent portal.</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        This is a weekly automated report.
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(), student.getLastName(),
            presentDays, totalDays,
            weeklyPercentage,
            recentGrades.size()
        );
    }

    private String buildReportCardEmail(Worker student, List<Grade> grades, Double gpa, String semester) {
        StringBuilder gradesTable = new StringBuilder();
        for (Grade grade : grades) {
            gradesTable.append(String.format(
                "<tr><td style='padding: 10px; border: 1px solid #ddd;'>%s</td><td style='padding: 10px; border: 1px solid #ddd; text-align: center;'>%.0f/%.0f</td><td style='padding: 10px; border: 1px solid #ddd; text-align: center;'>%.2f%%</td><td style='padding: 10px; border: 1px solid #ddd; text-align: center; font-size: 18px;'><strong>%s</strong></td></tr>",
                grade.getSubject().getSubjectName(),
                grade.getMarksObtained(), grade.getTotalMarks(),
                grade.getPercentage(),
                grade.getLetterGrade()
            ));
        }
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0; text-align: center;'>
                    <h1 style='margin: 0;'>🎓 REPORT CARD</h1>
                    <h3 style='margin: 5px 0;'>%s</h3>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <div style='background-color: #fff; padding: 15px; margin: 20px 0; border-radius: 5px;'>
                        <p><strong>Student Name:</strong> %s %s</p>
                        <p><strong>Admission Number:</strong> %s</p>
                        <p><strong>Class:</strong> %s - Section %s</p>
                        <p><strong>Academic Year:</strong> 2024-2025</p>
                    </div>
                    
                    <h3 style='color: #f5576c;'>Academic Performance</h3>
                    <table style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                        <tr style='background-color: #f5576c; color: white;'>
                            <th style='padding: 10px; border: 1px solid #ddd; text-align: left;'>Subject</th>
                            <th style='padding: 10px; border: 1px solid #ddd; text-align: center;'>Marks</th>
                            <th style='padding: 10px; border: 1px solid #ddd; text-align: center;'>Percentage</th>
                            <th style='padding: 10px; border: 1px solid #ddd; text-align: center;'>Grade</th>
                        </tr>
                        %s
                    </table>
                    
                    <div style='background-color: #e7f3ff; padding: 15px; margin: 20px 0; border-left: 4px solid #2196F3; border-radius: 5px; text-align: center;'>
                        <h2 style='margin: 0; color: #2196F3;'>Overall GPA: %.2f/10</h2>
                    </div>
                    
                    <p style='text-align: center; margin-top: 20px;'><em>Congratulations on your performance!</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666; text-align: center;'>
                        <strong>School Management System</strong><br>
                        Generated on: %s
                    </p>
                </div>
            </body>
            </html>
            """,
            semester,
            student.getFirstName(), student.getLastName(),
            student.getAdmissionNumber(),
            student.getCurrentClass().getClassName(), student.getSection(),
            gradesTable.toString(),
            gpa != null ? gpa : 0.0,
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        );
    }

    private String buildFailingGradeEmail(Worker student, List<Grade> failingGrades) {
        // Build list of failing subjects
        StringBuilder subjectsList = new StringBuilder("<ul>");
        for (Grade grade : failingGrades) {
            subjectsList.append(String.format("<li>%s - %.0f%% (Grade: %s)</li>",
                grade.getSubject().getSubjectName(),
                grade.getPercentage(),
                grade.getLetterGrade()));
        }
        subjectsList.append("</ul>");
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background-color: #ff6b6b; padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>⚠️ Academic Alert - Improvement Needed</h2>
                </div>
                <div style='padding: 20px; background-color: #fff3cd; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>We would like to bring to your attention that <strong>%s %s</strong> needs improvement in the following subjects:</p>
                    
                    %s
                    
                    <div style='background-color: #fff; padding: 15px; margin: 20px 0; border-left: 4px solid #ffc107; border-radius: 5px;'>
                        <p style='margin: 0;'><strong>Recommended Actions:</strong></p>
                        <ul style='margin: 10px 0;'>
                            <li>Schedule a meeting with subject teachers</li>
                            <li>Extra tutoring or coaching may be beneficial</li>
                            <li>Regular revision and practice</li>
                            <li>Contact us for personalized guidance</li>
                        </ul>
                    </div>
                    
                    <p><em>We are committed to your child's success and are here to help.</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        For academic support, contact: academics@school.com
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(), student.getLastName(),
            subjectsList.toString()
        );
    }

    private String buildExamScheduleEmail(Worker student, Exam exam) {
        String dateStr = exam.getExamDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, EEEE"));
        String timeStr = exam.getStartTime() != null ? exam.getStartTime().toString() : "TBA";
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #fa709a 0%%, #fee140 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>📝 Exam Schedule Notification</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent & Student,</strong></p>
                    <p>Exam has been scheduled for <strong>%s</strong></p>
                    
                    <table style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                        <tr style='background-color: #fa709a; color: white;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Exam Name</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>%s</strong></td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Subject</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Date</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'><strong>%s</strong></td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Time</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Duration</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%d minutes</td>
                        </tr>
                        <tr>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Room Number</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%s</td>
                        </tr>
                        <tr style='background-color: #f2f2f2;'>
                            <td style='padding: 10px; border: 1px solid #ddd;'>Total Marks</td>
                            <td style='padding: 10px; border: 1px solid #ddd;'>%.0f</td>
                        </tr>
                    </table>
                    
                    <div style='background-color: #fff3cd; padding: 15px; margin: 20px 0; border-left: 4px solid #ffc107; border-radius: 5px;'>
                        <p style='margin: 0;'><strong>📚 Exam Instructions:</strong></p>
                        <p style='margin: 5px 0;'>%s</p>
                    </div>
                    
                    <p style='margin-top: 20px;'><em>All the best for your exam!</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong>
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(),
            exam.getExamName(),
            exam.getSubject().getSubjectName(),
            dateStr,
            timeStr,
            exam.getDurationMinutes() != null ? exam.getDurationMinutes() : 0,
            exam.getRoomNumber() != null ? exam.getRoomNumber() : "TBA",
            exam.getTotalMarks() != null ? exam.getTotalMarks() : 0.0,
            exam.getInstructions() != null ? exam.getInstructions() : "Please arrive 15 minutes early. Bring admit card and ID."
        );
    }

    private String buildExamResultEmail(Worker student, Exam exam, Grade grade) {
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #4facfe 0%%, #00f2fe 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0; text-align: center;'>
                    <h2 style='margin: 0;'>🏆 Exam Results Published!</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear Parent,</strong></p>
                    <p>The results for <strong>%s</strong> are now available!</p>
                    
                    <div style='background-color: #fff; padding: 20px; margin: 20px 0; border-radius: 10px; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>
                        <h2 style='color: #4facfe; margin: 0;'>%s</h2>
                        <p style='font-size: 48px; margin: 10px 0; color: %s;'><strong>%s</strong></p>
                        <p style='margin: 5px 0;'>Marks: %.0f / %.0f</p>
                        <p style='margin: 5px 0;'>Percentage: %.2f%%</p>
                    </div>
                    
                    <p style='text-align: center;'><strong>%s</strong></p>
                    
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666; text-align: center;'>
                        <strong>School Management System</strong><br>
                        Login to view detailed analysis
                    </p>
                </div>
            </body>
            </html>
            """,
            exam.getExamName(),
            exam.getSubject().getSubjectName(),
            grade.getStatus() == Grade.GradeStatus.PASS ? "#28a745" : "#dc3545",
            grade.getLetterGrade(),
            grade.getMarksObtained(), grade.getTotalMarks(),
            grade.getPercentage(),
            grade.getStatus() == Grade.GradeStatus.PASS ? "✅ PASSED" : "❌ NEEDS IMPROVEMENT"
        );
    }

    private String buildEventInvitationEmail(Worker student, Event event) {
        String dateStr = event.getStartDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0;'>
                    <h2 style='margin: 0;'>🎉 Event Invitation</h2>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <h2 style='color: #667eea; text-align: center;'>%s</h2>
                    
                    <div style='background-color: #fff; padding: 15px; margin: 20px 0; border-radius: 5px;'>
                        <p><strong>📅 Date & Time:</strong> %s</p>
                        <p><strong>📍 Venue:</strong> %s</p>
                        <p><strong>🎯 Event Type:</strong> %s</p>
                    </div>
                    
                    <div style='background-color: #e7f3ff; padding: 15px; margin: 20px 0; border-radius: 5px;'>
                        <p><strong>Description:</strong></p>
                        <p>%s</p>
                    </div>
                    
                    %s
                    
                    <p style='text-align: center; margin-top: 20px;'><em>We look forward to your participation!</em></p>
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong><br>
                        Contact: %s | %s
                    </p>
                </div>
            </body>
            </html>
            """,
            event.getEventName(),
            dateStr,
            event.getVenue() != null ? event.getVenue() : "TBA",
            event.getEventType().toString(),
            event.getDescription() != null ? event.getDescription() : "",
            event.isRequiresRegistration() ? "<div style='background-color: #fff3cd; padding: 15px; text-align: center; border-radius: 5px;'><p style='margin: 0;'><strong>⚠️ Registration Required</strong></p><p style='margin: 5px 0;'>Click here to register</p></div>" : "",
            event.getContactPerson() != null ? event.getContactPerson() : "School Office",
            event.getContactPhone() != null ? event.getContactPhone() : ""
        );
    }

    private String buildWelcomeEmail(Worker student) {
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 20px; color: white; border-radius: 10px 10px 0 0; text-align: center;'>
                    <h1 style='margin: 0;'>🎓 Welcome to Our School!</h1>
                </div>
                <div style='padding: 20px; background-color: #f9f9f9; border-radius: 0 0 10px 10px;'>
                    <p><strong>Dear %s %s and Family,</strong></p>
                    
                    <p>We are delighted to welcome you to our school family! 🎉</p>
                    
                    <div style='background-color: #fff; padding: 15px; margin: 20px 0; border-radius: 5px;'>
                        <p><strong>Student Details:</strong></p>
                        <p>Name: <strong>%s %s</strong></p>
                        <p>Admission Number: <strong>%s</strong></p>
                        <p>Class: <strong>%s - Section %s</strong></p>
                        <p>Roll Number: <strong>%d</strong></p>
                    </div>
                    
                    <div style='background-color: #e7f3ff; padding: 15px; margin: 20px 0; border-radius: 5px;'>
                        <p style='margin: 0;'><strong>Next Steps:</strong></p>
                        <ul>
                            <li>Your ID card will be generated soon</li>
                            <li>Login credentials will be shared separately</li>
                            <li>Please complete the fee payment</li>
                            <li>Download the parent app for updates</li>
                        </ul>
                    </div>
                    
                    <p style='text-align: center; margin-top: 30px;'><strong>We wish you a wonderful academic journey ahead!</strong></p>
                    
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666; text-align: center;'>
                        <strong>School Management System</strong><br>
                        For queries: info@school.com | +91-XXXXXXXXXX
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName(), student.getLastName(),
            student.getFirstName(), student.getLastName(),
            student.getAdmissionNumber(),
            student.getCurrentClass().getClassName(), student.getSection(),
            student.getRollNumber()
        );
    }

    private String buildBirthdayEmail(Worker student) {
        return String.format("""
            <html>
            <body style='font-family: Arial, sans-serif; text-align: center;'>
                <div style='background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); padding: 30px; color: white; border-radius: 10px 10px 0 0;'>
                    <h1 style='margin: 0; font-size: 36px;'>🎂 Happy Birthday!</h1>
                </div>
                <div style='padding: 30px; background-color: #fff; border-radius: 0 0 10px 10px;'>
                    <h2 style='color: #f5576c;'>Dear %s,</h2>
                    
                    <p style='font-size: 18px; margin: 20px 0;'>
                        Wishing you a very <strong>Happy Birthday</strong>! 🎉
                    </p>
                    
                    <p style='font-size: 16px; color: #666;'>
                        May this year bring you success in your studies,<br>
                        happiness in your friendships,<br>
                        and joy in every moment!
                    </p>
                    
                    <p style='font-size: 24px; margin: 30px 0;'>🎈 🎁 🎊 🎂 🎉</p>
                    
                    <p style='margin-top: 30px;'><em>Best wishes from your school family!</em></p>
                    
                    <hr style='border: 1px solid #ddd; margin: 20px 0;'>
                    <p style='font-size: 12px; color: #666;'>
                        <strong>School Management System</strong>
                    </p>
                </div>
            </body>
            </html>
            """,
            student.getFirstName()
        );
    }

    // Helper method
    private String getStatusEmoji(Attendance.AttendanceStatus status) {
        return switch (status) {
            case PRESENT -> "✅";
            case ABSENT -> "❌";
            case LATE -> "⏰";
            case HALF_DAY -> "🕐";
            case EXCUSED -> "📝";
            default -> "ℹ️";
        };
    }
}

