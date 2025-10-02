package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.WhatsAppService;
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
import java.util.List;

/**
 * WhatsApp Service Implementation using Twilio WhatsApp Business API
 * 
 * To enable WhatsApp:
 * 1. Add Twilio dependency (same as SMS)
 * 2. Configure Twilio WhatsApp Business Account
 * 3. Add whatsapp.enabled=true in properties
 * 4. Uncomment Twilio imports
 */
@Service
@Slf4j
public class WhatsAppServiceImpl implements WhatsAppService {

    @Value("${twilio.account.sid:}")
    private String twilioAccountSid;
    
    @Value("${twilio.auth.token:}")
    private String twilioAuthToken;
    
    @Value("${twilio.whatsapp.number:whatsapp:+14155238886}")
    private String twilioWhatsAppNumber;
    
    @Value("${whatsapp.enabled:false}")
    private boolean whatsappEnabled;
    
    private final WorkerRepository workerRepository;
    
    private Long getCurrentOwnerId() {
        // Get the current logged-in user ID for multi-tenancy
        return 1L; // Placeholder - should be replaced with actual logged-in user ID
    }
    private final FeeRepository feeRepository;
    private final ExamRepository examRepository;
    private final EventRepository eventRepository;
    
    public WhatsAppServiceImpl(WorkerRepository workerRepository,
                              FeeRepository feeRepository,
                              ExamRepository examRepository,
                              EventRepository eventRepository) {
        this.workerRepository = workerRepository;
        this.feeRepository = feeRepository;
        this.examRepository = examRepository;
        this.eventRepository = eventRepository;
    }
    
    @PostConstruct
    public void initTwilio() {
        // Initialize Twilio after properties are injected
        if (whatsappEnabled && twilioAccountSid != null && !twilioAccountSid.isEmpty()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            log.info("✅ Twilio WhatsApp service initialized successfully");
            log.info("💬 WhatsApp notifications are LIVE!");
        } else {
            log.warn("⚠️ WhatsApp not configured. Will run in MOCK mode.");
        }
    }

    @Override
    public boolean sendWhatsAppMessage(String phoneNumber, String message) {
        log.info("Sending WhatsApp message to: {}", phoneNumber);
        
        try {
            // TWILIO WHATSAPP IMPLEMENTATION - LIVE MODE
            if (whatsappEnabled && twilioAccountSid != null && !twilioAccountSid.isEmpty()) {
                Message twilioMessage = Message.creator(
                    new PhoneNumber("whatsapp:+91" + phoneNumber),
                    new PhoneNumber(twilioWhatsAppNumber),
                    message
                ).create();
                
                log.info("✅ WhatsApp sent successfully. SID: {}", twilioMessage.getSid());
                return true;
            } else {
                // MOCK IMPLEMENTATION
                log.info("💬 WhatsApp MOCK - To: {} | Message: {}", phoneNumber, message);
                System.out.println("==========================================");
                System.out.println("💬 WHATSAPP SENT (MOCK MODE)");
                System.out.println("To: +91-" + phoneNumber);
                System.out.println("Message:\n" + message);
                System.out.println("==========================================");
                return true;
            }
            
        } catch (Exception e) {
            log.error("Failed to send WhatsApp to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendWhatsAppMessageWithMedia(String phoneNumber, String message, String mediaUrl) {
        log.info("Sending WhatsApp with media to: {}", phoneNumber);
        
        try {
            // TWILIO WHATSAPP WITH MEDIA - LIVE MODE
            if (whatsappEnabled && twilioAccountSid != null && !twilioAccountSid.isEmpty()) {
                Message twilioMessage = Message.creator(
                    new PhoneNumber("whatsapp:+91" + phoneNumber),
                    new PhoneNumber(twilioWhatsAppNumber),
                    message
                ).setMediaUrl(List.of(new java.net.URI(mediaUrl)))
                 .create();
                
                log.info("✅ WhatsApp with media sent. SID: {}", twilioMessage.getSid());
                return true;
            } else {
                // MOCK IMPLEMENTATION
                System.out.println("==========================================");
                System.out.println("💬 WHATSAPP WITH MEDIA (MOCK MODE)");
                System.out.println("To: +91-" + phoneNumber);
                System.out.println("Message: " + message);
                System.out.println("Media: " + mediaUrl);
                System.out.println("==========================================");
                return true;
            }
            
        } catch (Exception e) {
            log.error("Failed to send WhatsApp with media: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void sendBulkWhatsAppMessages(List<String> phoneNumbers, String message) {
        log.info("Sending bulk WhatsApp to {} recipients", phoneNumbers.size());
        
        int successCount = 0;
        for (String phoneNumber : phoneNumbers) {
            if (sendWhatsAppMessage(phoneNumber, message)) {
                successCount++;
            }
        }
        
        log.info("Bulk WhatsApp sent: {}/{} successful", successCount, phoneNumbers.size());
    }

    @Override
    public void sendAttendanceWhatsApp(Long studentId, LocalDate date, String status) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = buildAttendanceWhatsAppMessage(student, date, status);
        sendWhatsAppMessage(parentPhone, message);
    }

    @Override
    public void sendFeeReminderWhatsApp(Long studentId, Long feeId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Fee fee = feeRepository.findById(feeId).orElse(null);
        
        if (student == null || fee == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = buildFeeReminderWhatsAppMessage(student, fee);
        sendWhatsAppMessage(parentPhone, message);
    }

    @Override
    public void sendReportCardWhatsApp(Long studentId, String semester, String pdfUrl) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = String.format(
            "🎓 *Report Card - %s*\n\n" +
            "Dear Parent,\n" +
            "Report card for *%s %s* is now available.\n\n" +
            "📄 Download: %s\n\n" +
            "- School Management",
            semester,
            student.getFirstName(), student.getLastName(),
            pdfUrl
        );
        
        sendWhatsAppMessageWithMedia(parentPhone, message, pdfUrl);
    }

    @Override
    public void sendAdmitCardWhatsApp(Long studentId, Long examId, String admitCardUrl) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        Exam exam = examRepository.findById(examId).orElse(null);
        
        if (student == null || exam == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = String.format(
            "📝 *Exam Admit Card*\n\n" +
            "Student: *%s %s*\n" +
            "Exam: %s\n" +
            "Subject: %s\n" +
            "Date: %s\n\n" +
            "📄 Download Admit Card: %s\n\n" +
            "All the best!\n" +
            "- School",
            student.getFirstName(), student.getLastName(),
            exam.getExamName(),
            exam.getSubject().getSubjectName(),
            exam.getExamDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
            admitCardUrl
        );
        
        sendWhatsAppMessageWithMedia(parentPhone, message, admitCardUrl);
    }

    @Override
    public void sendIDCardWhatsApp(Long studentId, String idCardPdfUrl) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = String.format(
            "🆔 *Student ID Card*\n\n" +
            "Dear Parent,\n" +
            "ID card for *%s %s* has been generated.\n\n" +
            "Admission No: %s\n\n" +
            "📄 Download ID Card: %s\n\n" +
            "Please collect the physical card from school office.\n\n" +
            "- School Management",
            student.getFirstName(), student.getLastName(),
            student.getAdmissionNumber(),
            idCardPdfUrl
        );
        
        sendWhatsAppMessageWithMedia(parentPhone, message, idCardPdfUrl);
    }

    @Override
    public void sendTCWhatsApp(Long studentId, String tcPdfUrl) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = String.format(
            "📜 *Transfer Certificate*\n\n" +
            "Dear Parent,\n" +
            "Transfer Certificate for *%s %s* is ready.\n\n" +
            "📄 Download TC: %s\n\n" +
            "Please collect the original from school office.\n\n" +
            "- School Management",
            student.getFirstName(), student.getLastName(),
            tcPdfUrl
        );
        
        sendWhatsAppMessageWithMedia(parentPhone, message, tcPdfUrl);
    }

    @Override
    public void sendEventInvitationWhatsApp(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return;
        
        List<Worker> students = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
        
        String message = buildEventInvitationWhatsAppMessage(event);
        
        for (Worker student : students) {
            String parentPhone = student.getFatherPhone();
            if (parentPhone != null) {
                sendWhatsAppMessage(parentPhone, message);
            }
        }
    }

    @Override
    public void sendEmergencyWhatsAppToAll(String message) {
        log.info("Sending emergency WhatsApp to all parents");
        
        List<Worker> students = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
        
        String emergencyMsg = "🚨 *EMERGENCY ALERT*\n\n" + message + "\n\n- School Management";
        
        for (Worker student : students) {
            String parentPhone = student.getFatherPhone();
            if (parentPhone != null) {
                sendWhatsAppMessage(parentPhone, emergencyMsg);
            }
        }
    }

    @Override
    public void sendWelcomeWhatsApp(Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        if (parentPhone == null) return;
        
        String message = buildWelcomeWhatsAppMessage(student);
        sendWhatsAppMessage(parentPhone, message);
    }

    @Override
    public void sendBirthdayWhatsApp(Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return;
        
        String parentPhone = student.getFatherPhone();
        String studentPhone = student.getPhoneNumber();
        
        String message = String.format(
            "🎂 *Happy Birthday!*\n\n" +
            "Dear *%s*,\n\n" +
            "Wishing you a very Happy Birthday! 🎉\n" +
            "May this year bring you success, happiness, and joy!\n\n" +
            "🎈 Best wishes from your school family!\n\n" +
            "- School Management",
            student.getFirstName()
        );
        
        // Send to both student and parent
        if (studentPhone != null) sendWhatsAppMessage(studentPhone, message);
        if (parentPhone != null) sendWhatsAppMessage(parentPhone, message);
    }

    // ============= WHATSAPP TEMPLATE BUILDERS =============

    private String buildAttendanceWhatsAppMessage(Worker student, LocalDate date, String status) {
        String emoji = getStatusEmoji(status);
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        
        return String.format(
            "%s *Daily Attendance Report*\n\n" +
            "*Student:* %s %s\n" +
            "*Class:* %s - Section %s\n" +
            "*Date:* %s\n" +
            "*Status:* *%s*\n\n" +
            "_This is an automated notification from School Management System_",
            emoji,
            student.getFirstName(), student.getLastName(),
            student.getCurrentClass().getClassName(), student.getSection(),
            dateStr,
            status
        );
    }

    private String buildFeeReminderWhatsAppMessage(Worker student, Fee fee) {
        String dateStr = fee.getDueDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fee.getDueDate());
        
        return String.format(
            "💰 *Fee Payment Reminder*\n\n" +
            "Dear Parent,\n\n" +
            "*Student:* %s %s\n" +
            "*Fee Type:* %s\n" +
            "*Amount Due:* ₹%.0f\n" +
            "*Due Date:* %s\n" +
            "*Days Remaining:* %d days\n\n" +
            "📱 *Pay Online:* https://school.com/pay\n" +
            "🏦 *Bank Transfer* or *Cash* at office\n\n" +
            "_Please pay on time to avoid late fees._\n\n" +
            "- School Office",
            student.getFirstName(), student.getLastName(),
            fee.getFeeCategory(),
            fee.getBalanceAmount(),
            dateStr,
            daysRemaining
        );
    }

    private String buildEventInvitationWhatsAppMessage(Event event) {
        String dateStr = event.getStartDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        
        return String.format(
            "🎉 *Event Invitation*\n\n" +
            "*%s*\n\n" +
            "📅 *Date & Time:* %s\n" +
            "📍 *Venue:* %s\n" +
            "🎯 *Type:* %s\n\n" +
            "*Description:*\n%s\n\n" +
            "%s" +
            "\n_We look forward to your participation!_\n\n" +
            "Contact: %s\n" +
            "- School Management",
            event.getEventName(),
            dateStr,
            event.getVenue() != null ? event.getVenue() : "School Campus",
            event.getEventType().toString(),
            event.getDescription() != null ? event.getDescription() : "",
            event.isRequiresRegistration() ? "⚠️ *Registration Required*\n" : "",
            event.getContactPhone() != null ? event.getContactPhone() : "School Office"
        );
    }

    private String buildWelcomeWhatsAppMessage(Worker student) {
        return String.format(
            "🎓 *Welcome to Our School!*\n\n" +
            "Dear Parent,\n\n" +
            "We are delighted to welcome *%s %s* to our school family! 🎉\n\n" +
            "*Student Details:*\n" +
            "📝 Admission No: %s\n" +
            "📚 Class: %s - Section %s\n" +
            "🔢 Roll Number: %d\n\n" +
            "*Next Steps:*\n" +
            "✅ ID card will be issued soon\n" +
            "✅ Login credentials sent via email\n" +
            "✅ Download parent app for updates\n\n" +
            "🌐 Portal: https://school.com\n" +
            "📱 Download App: https://school.com/app\n\n" +
            "_We wish you a wonderful academic journey!_\n\n" +
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

