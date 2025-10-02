package com.vijay.User_Master.service;

import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.entity.Fee;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.FeeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Scheduled Notification Service
 * Automatically sends notifications at scheduled intervals
 */
@Service
@AllArgsConstructor
@Slf4j
public class ScheduledNotificationService {

    private final SchoolNotificationService notificationService;
    private final WorkerRepository workerRepository;
    
    private Long getCurrentOwnerId() {
        // Get the current logged-in user ID for multi-tenancy
        return 1L; // Placeholder - should be replaced with actual logged-in user ID
    }
    private final FeeRepository feeRepository;

    /**
     * Send birthday wishes daily at 8:00 AM
     * Cron: Every day at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyBirthdayWishes() {
        log.info("Running scheduled task: Send birthday wishes");
        
        LocalDate today = LocalDate.now();
        
        // Find students with birthday today
        List<Worker> students = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
        
        for (Worker student : students) {
            if (student.getDateOfBirth() != null) {
                LocalDate dob = student.getDateOfBirth();
                if (dob.getMonth() == today.getMonth() && dob.getDayOfMonth() == today.getDayOfMonth()) {
                    notificationService.sendBirthdayWishes(student.getId());
                    log.info("Birthday wishes sent to student ID: {}", student.getId());
                }
            }
        }
    }

    /**
     * Send fee reminders - 7 days before due date
     * Cron: Every day at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendFeeRemindersSevenDays() {
        log.info("Running scheduled task: Send fee reminders (7 days before due)");
        notificationService.sendBulkFeeReminders(7);
    }

    /**
     * Send fee reminders - 3 days before due date
     * Cron: Every day at 9:30 AM
     */
    @Scheduled(cron = "0 30 9 * * ?")
    public void sendFeeRemindersThreeDays() {
        log.info("Running scheduled task: Send fee reminders (3 days before due)");
        notificationService.sendBulkFeeReminders(3);
    }

    /**
     * Send fee reminders - 1 day before due date
     * Cron: Every day at 10:00 AM
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendFeeRemindersOneDay() {
        log.info("Running scheduled task: Send fee reminders (1 day before due)");
        notificationService.sendBulkFeeReminders(1);
    }

    /**
     * Send weekly progress reports - Every Friday at 5:00 PM
     * Cron: Every Friday at 5:00 PM
     */
    @Scheduled(cron = "0 0 17 * * FRI")
    public void sendWeeklyProgressReports() {
        log.info("Running scheduled task: Send weekly progress reports");
        
        List<Worker> activeStudents = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
        
        for (Worker student : activeStudents) {
            try {
                notificationService.sendWeeklyProgressReport(student.getId());
                log.info("Weekly progress report sent to student ID: {}", student.getId());
            } catch (Exception e) {
                log.error("Error sending weekly report for student ID: {}", student.getId(), e);
            }
        }
        
        log.info("Weekly progress reports sent to {} students", activeStudents.size());
    }

    /**
     * Check and send low attendance warnings - Daily at 6:00 PM
     * Cron: Every day at 6:00 PM
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void checkAndSendLowAttendanceWarnings() {
        log.info("Running scheduled task: Check low attendance warnings");
        
        List<Worker> activeStudents = workerRepository.findByUser_IdAndIsDeletedFalse(getCurrentOwnerId(), Pageable.unpaged());
        
        for (Worker student : activeStudents) {
            try {
                notificationService.sendLowAttendanceWarning(student.getId());
            } catch (Exception e) {
                log.error("Error checking attendance for student ID: {}", student.getId(), e);
            }
        }
    }

    /**
     * Send fee overdue notices - Daily at 11:00 AM
     * Cron: Every day at 11:00 AM
     */
    @Scheduled(cron = "0 0 11 * * ?")
    public void sendOverdueFeeNotices() {
        log.info("Running scheduled task: Send overdue fee notices");
        
        List<Fee> overdueFees = feeRepository.findOverdueFees(LocalDate.now());
        
        for (Fee fee : overdueFees) {
            try {
                notificationService.sendFeeOverdueNotice(fee.getStudent().getId(), fee.getId());
                log.info("Overdue notice sent for fee ID: {}", fee.getId());
            } catch (Exception e) {
                log.error("Error sending overdue notice for fee ID: {}", fee.getId(), e);
            }
        }
        
        log.info("Overdue fee notices sent for {} fees", overdueFees.size());
    }
}

