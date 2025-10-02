package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.SMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * SMS Service Implementation using Twilio
 * 
 * To enable Twilio:
 * 1. Add dependency in build.gradle:
 *    implementation 'com.twilio.sdk:twilio:9.14.1'
 * 
 * 2. Add properties in application.properties:
 *    twilio.account.sid=YOUR_ACCOUNT_SID
 *    twilio.auth.token=YOUR_AUTH_TOKEN
 *    twilio.phone.number=YOUR_TWILIO_NUMBER
 * 
 * 3. Uncomment Twilio imports above
 */
@Service
@Slf4j
public class SMSServiceImpl implements SMSService {

    @Value("${twilio.account.sid:}")
    private String twilioAccountSid;
    
    @Value("${twilio.auth.token:}")
    private String twilioAuthToken;
    
    @Value("${twilio.phone.number:}")
    private String twilioPhoneNumber;
    
    private final WorkerRepository workerRepository;
    
    private Long getCurrentOwnerId() {
        // Get the current logged-in user ID for multi-tenancy
        return 1L; // Placeholder - should be replaced with actual logged-in user ID
    }
    private final FeeRepository feeRepository;
    private final ExamRepository examRepository;
    
    public SMSServiceImpl(WorkerRepository workerRepository, 
                         FeeRepository feeRepository,
                         ExamRepository examRepository) {
        this.workerRepository = workerRepository;
        this.feeRepository = feeRepository;
        this.examRepository = examRepository;
    }
    
    @PostConstruct
    public void initTwilio() {
        // Initialize Twilio after properties are injected
        if (twilioAccountSid != null && !twilioAccountSid.isEmpty() 
            && twilioAuthToken != null && !twilioAuthToken.isEmpty()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            log.info("✅ Twilio SMS service initialized successfully");
            log.info("📱 SMS notifications are LIVE!");
        } else {
            log.warn("⚠️ Twilio not configured. SMS will run in MOCK mode.");
        }
    }

    @Override
    public boolean sendSMS(String phoneNumber, String message) {
        log.info("Sending SMS to: {}", phoneNumber);
        
        try {
            // TWILIO IMPLEMENTATION - LIVE MODE
            if (twilioAccountSid != null && !twilioAccountSid.isEmpty() 
                && twilioAuthToken != null && !twilioAuthToken.isEmpty()) {
                Message twilioMessage = Message.creator(
                    new PhoneNumber("+91" + phoneNumber),  // Indian number format
                    new PhoneNumber(twilioPhoneNumber),
                    message
                ).create();
                
                log.info("✅ SMS sent successfully. SID: {}", twilioMessage.getSid());
                return true;
            } else {
                // MOCK IMPLEMENTATION (When Twilio not configured)
                log.info("📱 SMS MOCK - To: {} | Message: {}", phoneNumber, message);
                System.out.println("==========================================");
                System.out.println("📱 SMS SENT (MOCK MODE)");
                System.out.println("To: +91-" + phoneNumber);
                System.out.println("Message: " + message);
                System.out.println("==========================================");
                return true;
            }
            
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    @Override
    public void sendBulkSMS(List<String> phoneNumbers, String message) {
        log.info("Sending bulk SMS to {} recipients", phoneNumbers.size());
        
        int successCount = 0;
        for (String phoneNumber : phoneNumbers) {
            if (sendSMS(phoneNumber, message)) {
                successCount++;
            }
        }
        
        log.info("Bulk SMS sent: {}/{} successful", successCount, phoneNumbers.size());
    }

    @Override
    public void sendAttendanceSMS(Long studentId, LocalDate date, String status) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone() != null 
            ? student.getFatherPhone() 
            : student.getMotherPhone();
            
        if (parentPhone == null) {
            log.warn("No parent phone number for student ID: {}", studentId);
            return;
        }
        
        String message = buildAttendanceSMS(student, date, status);
        sendSMS(parentPhone, message);
    }

    @Override
    public void sendFeeReminderSMS(Long studentId, Long feeId, Double amount, LocalDate dueDate) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Fee fee = feeRepository.findById(feeId).orElse(null);
        
        if (student == null || fee == null) return;
        
        String parentPhone = student.getFatherPhone() != null 
            ? student.getFatherPhone() 
            : student.getMotherPhone();
            
        if (parentPhone == null) return;
        
        String message = buildFeeReminderSMS(student, fee, dueDate);
        sendSMS(parentPhone, message);
    }

    @Override
    public void sendExamReminderSMS(Long studentId, Long examId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Exam exam = examRepository.findById(examId).orElse(null);
        
        if (student == null || exam == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = buildExamReminderSMS(student, exam);
        sendSMS(parentPhone, message);
    }

    @Override
    public void sendLowAttendanceWarningSMS(Long studentId, Double percentage) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = buildLowAttendanceWarningSMS(student, percentage);
        sendSMS(parentPhone, message);
    }

    @Override
    public void sendGradePublishedSMS(Long studentId, String subject, Double percentage, String grade) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = buildGradePublishedSMS(student, subject, percentage, grade);
        sendSMS(parentPhone, message);
    }

    @Override
    public void sendEmergencySMSToAll(String message) {
        log.info("Sending emergency SMS to all parents");
        
        List<Worker> students = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
        
        for (Worker student : students) {
            String parentPhone = student.getFatherPhone();
            if (parentPhone != null) {
                sendSMS(parentPhone, "🚨 EMERGENCY: " + message);
            }
        }
    }

    @Override
    public boolean sendOTP(String phoneNumber, String otp) {
        String message = String.format(
            "Your OTP for School Management System is: %s\n" +
            "Valid for 10 minutes.\n" +
            "Do not share this OTP with anyone.",
            otp
        );
        return sendSMS(phoneNumber, message);
    }

    @Override
    public void sendWelcomeSMS(Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = buildWelcomeSMS(student);
        sendSMS(parentPhone, message);
    }

    // ============= SMS TEMPLATE BUILDERS =============

    private String buildAttendanceSMS(Worker student, LocalDate date, String status) {
        String emoji = getStatusEmoji(status);
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        
        return String.format(
            "%s Daily Attendance\n\n" +
            "Student: %s %s\n" +
            "Class: %s-%s\n" +
            "Date: %s\n" +
            "Status: %s\n\n" +
            "- School Management",
            emoji,
            student.getFirstName(), student.getLastName(),
            student.getCurrentClass().getClassName(), student.getSection(),
            dateStr,
            status
        );
    }

    private String buildFeeReminderSMS(Worker student, Fee fee, LocalDate dueDate) {
        String dateStr = dueDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        
        return String.format(
            "💰 Fee Reminder\n\n" +
            "Dear Parent,\n" +
            "Student: %s %s\n" +
            "Fee: %s\n" +
            "Amount Due: ₹%.0f\n" +
            "Due Date: %s\n" +
            "Days Left: %d\n\n" +
            "Please pay on time to avoid late fees.\n" +
            "Pay online: https://school.com/pay\n\n" +
            "- School Office",
            student.getFirstName(), student.getLastName(),
            fee.getFeeCategory(),
            fee.getBalanceAmount(),
            dateStr,
            daysRemaining
        );
    }

    private String buildExamReminderSMS(Worker student, Exam exam) {
        String dateStr = exam.getExamDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        
        return String.format(
            "📝 Exam Reminder\n\n" +
            "Student: %s %s\n" +
            "Exam: %s\n" +
            "Subject: %s\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Room: %s\n\n" +
            "All the best!\n" +
            "- School",
            student.getFirstName(), student.getLastName(),
            exam.getExamName(),
            exam.getSubject().getSubjectName(),
            dateStr,
            exam.getStartTime() != null ? exam.getStartTime().toString() : "TBA",
            exam.getRoomNumber() != null ? exam.getRoomNumber() : "TBA"
        );
    }

    private String buildLowAttendanceWarningSMS(Worker student, Double percentage) {
        return String.format(
            "⚠️ Attendance Alert\n\n" +
            "Dear Parent,\n" +
            "Student: %s %s\n" +
            "Current Attendance: %.2f%%\n" +
            "Required: 75%%\n\n" +
            "Please ensure regular attendance.\n" +
            "Contact: 9876543210\n\n" +
            "- School",
            student.getFirstName(), student.getLastName(),
            percentage
        );
    }

    private String buildGradePublishedSMS(Worker student, String subject, Double percentage, String grade) {
        String emoji = percentage >= 75 ? "🎉" : "📚";
        
        return String.format(
            "%s Grade Published\n\n" +
            "Student: %s %s\n" +
            "Subject: %s\n" +
            "Marks: %.2f%%\n" +
            "Grade: %s\n\n" +
            "View details in parent portal.\n" +
            "- School",
            emoji,
            student.getFirstName(), student.getLastName(),
            subject,
            percentage,
            grade
        );
    }

    private String buildWelcomeSMS(Worker student) {
        return String.format(
            "🎓 Welcome to School!\n\n" +
            "Dear Parent,\n" +
            "We welcome %s %s to our school family.\n\n" +
            "Admission No: %s\n" +
            "Class: %s-%s\n" +
            "Roll No: %d\n\n" +
            "ID card will be issued soon.\n" +
            "Login: https://school.com\n\n" +
            "- School Management",
            student.getFirstName(), student.getLastName(),
            student.getAdmissionNumber(),
            student.getCurrentClass().getClassName(), student.getSection(),
            student.getRollNumber()
        );
    }

    private String getStatusEmoji(String status) {
        return switch (status.toUpperCase()) {
            case "PRESENT" -> "✅";
            case "ABSENT" -> "❌";
            case "LATE" -> "⏰";
            case "HALF_DAY" -> "🕐";
            default -> "ℹ️";
        };
    }
}

