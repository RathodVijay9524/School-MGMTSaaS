package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.exceptions.UnauthorizedException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ParentService
 * Handles all parent portal business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParentServiceImpl implements ParentService {

    private final WorkerRepository workerRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final FeeRepository feeRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExamRepository examRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public ParentDashboardDTO getParentDashboard(Long parentId, Long ownerId) {
        log.info("Getting parent dashboard for parent ID: {}", parentId);
        
        // Get parent
        Worker parent = workerRepository.findByIdAndOwner_IdAndIsDeletedFalse(parentId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", "id", parentId));
        
        // Get all children
        List<ChildSummaryDTO> children = getParentChildren(parentId, ownerId);
        
        // Get aggregated statistics
        ParentDashboardDTO.DashboardSummaryDTO summary = getAggregatedStatistics(parentId, ownerId);
        
        // Get notifications (sample - can be enhanced)
        List<ParentDashboardDTO.ParentNotificationDTO> notifications = generateNotifications(children);
        
        // Get upcoming events
        List<ParentDashboardDTO.EventSummaryDTO> events = getUpcomingEvents(ownerId);
        
        // Get pending items
        ParentDashboardDTO.PendingItemsDTO pendingItems = calculatePendingItems(children);
        
        // Get recent activities
        List<ParentDashboardDTO.ActivityDTO> activities = generateRecentActivities(children);
        
        return ParentDashboardDTO.builder()
                .parentId(parent.getId())
                .parentName(parent.getName())
                .email(parent.getEmail())
                .phone(parent.getPhoneNumber())
                .children(children)
                .totalChildren(children.size())
                .summary(summary)
                .notifications(notifications)
                .upcomingEvents(events)
                .pendingItems(pendingItems)
                .recentActivities(activities)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildSummaryDTO> getParentChildren(Long parentId, Long ownerId) {
        log.info("Getting children for parent ID: {}", parentId);
        
        // Get parent
        Worker parent = workerRepository.findByIdAndOwner_IdAndIsDeletedFalse(parentId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", "id", parentId));
        
        // Find children by parent email
        List<Worker> children = workerRepository.findChildrenByParentEmail(parent.getEmail(), ownerId);
        
        if (children.isEmpty()) {
            log.warn("No children found for parent email: {}", parent.getEmail());
        }
        
        return children.stream()
                .map(this::mapToChildSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChildOverviewDTO getChildOverview(Long parentId, Long studentId, Long ownerId) {
        log.info("Getting child overview for parent ID: {} and student ID: {}", parentId, studentId);
        
        // Verify parent access
        if (!verifyParentAccess(parentId, studentId, ownerId)) {
            throw new UnauthorizedException("Parent does not have access to this student");
        }
        
        // Get student summary
        ChildSummaryDTO student = getChildSummary(studentId, ownerId);
        
        // Get attendance summary
        ChildOverviewDTO.AttendanceSummaryDTO attendance = getAttendanceSummary(studentId, ownerId);
        
        // Get grade summary
        ChildOverviewDTO.GradeSummaryDTO grades = getGradeSummary(studentId, ownerId);
        
        // Get fee summary
        ChildOverviewDTO.FeeSummaryDTO fees = getFeeSummary(studentId, ownerId);
        
        // Get recent assignments
        List<AssignmentResponse> assignments = getRecentAssignments(studentId, ownerId);
        
        // Get upcoming exams
        List<ExamResponse> exams = getUpcomingExams(studentId, ownerId);
        
        // Get notifications (sample)
        List<ChildOverviewDTO.NotificationDTO> notifications = new ArrayList<>();
        
        // Get teacher comments (sample)
        List<ChildOverviewDTO.TeacherCommentDTO> comments = new ArrayList<>();
        
        return ChildOverviewDTO.builder()
                .student(student)
                .attendance(attendance)
                .grades(grades)
                .fees(fees)
                .recentAssignments(assignments)
                .upcomingExams(exams)
                .notifications(notifications)
                .teacherComments(comments)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyParentAccess(Long parentId, Long studentId, Long ownerId) {
        log.info("Verifying parent access - Parent ID: {}, Student ID: {}", parentId, studentId);
        
        Worker parent = workerRepository.findByIdAndOwner_IdAndIsDeletedFalse(parentId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", "id", parentId));
        
        return workerRepository.verifyParentStudentRelationship(parent.getEmail(), studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public ChildSummaryDTO getChildSummary(Long studentId, Long ownerId) {
        Worker student = workerRepository.findByIdAndOwner_IdAndIsDeletedFalse(studentId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        
        return mapToChildSummary(student);
    }

    @Override
    @Transactional(readOnly = true)
    public ParentDashboardDTO.DashboardSummaryDTO getAggregatedStatistics(Long parentId, Long ownerId) {
        List<ChildSummaryDTO> children = getParentChildren(parentId, ownerId);
        
        double totalPendingFees = children.stream()
                .mapToDouble(c -> c.getPendingFees() != null ? c.getPendingFees() : 0.0)
                .sum();
        
        double totalOverdueFees = children.stream()
                .filter(ChildSummaryDTO::isHasOverdueFees)
                .mapToDouble(c -> c.getPendingFees() != null ? c.getPendingFees() : 0.0)
                .sum();
        
        double averageAttendance = children.stream()
                .mapToDouble(c -> c.getAttendancePercentage() != null ? c.getAttendancePercentage() : 0.0)
                .average()
                .orElse(0.0);
        
        int totalPendingAssignments = children.stream()
                .mapToInt(c -> c.getPendingAssignments() != null ? c.getPendingAssignments() : 0)
                .sum();
        
        int upcomingExamsCount = children.stream()
                .mapToInt(c -> c.getUpcomingExams() != null ? c.getUpcomingExams() : 0)
                .sum();
        
        int urgentAlerts = (int) children.stream()
                .filter(ChildSummaryDTO::isNeedsAttention)
                .count();
        
        return ParentDashboardDTO.DashboardSummaryDTO.builder()
                .totalChildren(children.size())
                .totalPendingFees(totalPendingFees)
                .totalOverdueFees(totalOverdueFees)
                .averageAttendance(averageAttendance)
                .totalPendingAssignments(totalPendingAssignments)
                .upcomingExamsCount(upcomingExamsCount)
                .unreadNotifications(0) // TODO: Implement notifications
                .urgentAlerts(urgentAlerts)
                .build();
    }

    // ==================== Private Helper Methods ====================

    private ChildSummaryDTO mapToChildSummary(Worker student) {
        // Calculate attendance percentage
        Double attendancePercentage = calculateAttendancePercentage(student.getId(), student.getOwner().getId());
        
        // Calculate GPA and grades
        String overallGrade = calculateOverallGrade(student.getId(), student.getOwner().getId());
        
        // Get fees information
        Double pendingFees = student.getFeesBalance() != null ? student.getFeesBalance() : 0.0;
        boolean hasOverdueFees = checkOverdueFees(student.getId(), student.getOwner().getId());
        
        // Get academic counts
        Integer pendingAssignments = countPendingAssignments(student.getId(), student.getOwner().getId());
        Integer upcomingExams = countUpcomingExams(student.getId(), student.getOwner().getId());
        
        // Get last attendance
        String lastAttendanceStatus = getLastAttendanceStatus(student.getId(), student.getOwner().getId());
        
        // Check if needs attention
        boolean needsAttention = (attendancePercentage != null && attendancePercentage < 75.0) || hasOverdueFees;
        
        // Determine performance indicator
        String performanceIndicator = determinePerformance(attendancePercentage, overallGrade);
        
        return ChildSummaryDTO.builder()
                .id(student.getId())
                .name(student.getName())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .admissionNumber(student.getAdmissionNumber())
                .rollNumber(student.getRollNumber())
                .classId(student.getCurrentClass() != null ? student.getCurrentClass().getId() : null)
                .className(student.getCurrentClass() != null ? student.getCurrentClass().getClassName() : "N/A")
                .section(student.getSection())
                .profileImageUrl(student.getProfileImageUrl())
                .bloodGroup(student.getBloodGroup())
                .status(student.getStatus())
                .attendancePercentage(attendancePercentage)
                .overallGrade(overallGrade)
                .totalFees(student.getTotalFees())
                .feesPaid(student.getFeesPaid())
                .pendingFees(pendingFees)
                .hasOverdueFees(hasOverdueFees)
                .pendingAssignments(pendingAssignments)
                .upcomingExams(upcomingExams)
                .lastAttendanceStatus(lastAttendanceStatus)
                .needsAttention(needsAttention)
                .performanceIndicator(performanceIndicator)
                .alertsCount((hasOverdueFees ? 1 : 0) + (needsAttention ? 1 : 0))
                .build();
    }

    private Double calculateAttendancePercentage(Long studentId, Long ownerId) {
        try {
            long totalDays = attendanceRepository.countByStudent_IdAndOwner_Id(studentId, ownerId);
            if (totalDays == 0) return null;
            
            long presentDays = attendanceRepository.countByStudent_IdAndStatusAndOwner_Id(
                    studentId, com.vijay.User_Master.entity.Attendance.AttendanceStatus.PRESENT, ownerId);
            
            return ((double) presentDays / totalDays) * 100.0;
        } catch (Exception e) {
            log.error("Error calculating attendance percentage: {}", e.getMessage());
            return null;
        }
    }

    private String calculateOverallGrade(Long studentId, Long ownerId) {
        try {
            List<com.vijay.User_Master.entity.Grade> grades = gradeRepository
                    .findByStudent_IdAndOwner_IdAndIsPublishedTrue(studentId, ownerId, PageRequest.of(0, 100))
                    .getContent();
            
            if (grades.isEmpty()) return "N/A";
            
            double averagePercentage = grades.stream()
                    .filter(g -> g.getPercentage() != null)
                    .mapToDouble(com.vijay.User_Master.entity.Grade::getPercentage)
                    .average()
                    .orElse(0.0);
            
            return convertPercentageToGrade(averagePercentage);
        } catch (Exception e) {
            log.error("Error calculating overall grade: {}", e.getMessage());
            return "N/A";
        }
    }

    private boolean checkOverdueFees(Long studentId, Long ownerId) {
        try {
            return feeRepository.existsByStudent_IdAndOwner_IdAndDueDateBeforeAndPaymentStatusNot(
                    studentId, ownerId, LocalDate.now(), com.vijay.User_Master.entity.Fee.PaymentStatus.PAID);
        } catch (Exception e) {
            log.error("Error checking overdue fees: {}", e.getMessage());
            return false;
        }
    }

    private Integer countPendingAssignments(Long studentId, Long ownerId) {
        try {
            Worker student = workerRepository.findById(studentId).orElse(null);
            if (student == null || student.getCurrentClass() == null) return 0;
            
            return (int) assignmentRepository.countBySchoolClass_IdAndOwner_IdAndStatusAndIsDeletedFalse(
                    student.getCurrentClass().getId(), 
                    ownerId,
                    com.vijay.User_Master.entity.Assignment.AssignmentStatus.ASSIGNED);
        } catch (Exception e) {
            log.error("Error counting pending assignments: {}", e.getMessage());
            return 0;
        }
    }

    private Integer countUpcomingExams(Long studentId, Long ownerId) {
        try {
            Worker student = workerRepository.findById(studentId).orElse(null);
            if (student == null || student.getCurrentClass() == null) return 0;
            
            return (int) examRepository.countBySchoolClass_IdAndOwner_IdAndExamDateAfterAndIsDeletedFalse(
                    student.getCurrentClass().getId(), 
                    ownerId,
                    LocalDate.now());
        } catch (Exception e) {
            log.error("Error counting upcoming exams: {}", e.getMessage());
            return 0;
        }
    }

    private String getLastAttendanceStatus(Long studentId, Long ownerId) {
        try {
            Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "attendanceDate"));
            List<com.vijay.User_Master.entity.Attendance> attendance = attendanceRepository
                    .findByStudent_IdAndOwner_Id(studentId, ownerId, pageable)
                    .getContent();
            
            if (attendance.isEmpty()) return "No Record";
            
            return attendance.get(0).getStatus().name();
        } catch (Exception e) {
            log.error("Error getting last attendance status: {}", e.getMessage());
            return "Unknown";
        }
    }

    private String convertPercentageToGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }

    private String determinePerformance(Double attendance, String grade) {
        if (attendance == null || grade == null || grade.equals("N/A")) return "AVERAGE";
        
        if (attendance >= 90 && (grade.equals("A+") || grade.equals("A"))) return "EXCELLENT";
        if (attendance >= 80 && (grade.equals("A+") || grade.equals("A") || grade.equals("B+"))) return "GOOD";
        if (attendance < 75 || grade.equals("D") || grade.equals("F")) return "POOR";
        return "AVERAGE";
    }

    private ChildOverviewDTO.AttendanceSummaryDTO getAttendanceSummary(Long studentId, Long ownerId) {
        try {
            long totalDays = attendanceRepository.countByStudent_IdAndOwner_Id(studentId, ownerId);
            long presentDays = attendanceRepository.countByStudent_IdAndStatusAndOwner_Id(
                    studentId, com.vijay.User_Master.entity.Attendance.AttendanceStatus.PRESENT, ownerId);
            long absentDays = attendanceRepository.countByStudent_IdAndStatusAndOwner_Id(
                    studentId, com.vijay.User_Master.entity.Attendance.AttendanceStatus.ABSENT, ownerId);
            long lateDays = attendanceRepository.countByStudent_IdAndStatusAndOwner_Id(
                    studentId, com.vijay.User_Master.entity.Attendance.AttendanceStatus.LATE, ownerId);
            
            double percentage = totalDays > 0 ? ((double) presentDays / totalDays) * 100.0 : 0.0;
            
            return ChildOverviewDTO.AttendanceSummaryDTO.builder()
                    .percentage(percentage)
                    .totalDays((int) totalDays)
                    .presentDays((int) presentDays)
                    .absentDays((int) absentDays)
                    .lateDays((int) lateDays)
                    .trend("STABLE")
                    .build();
        } catch (Exception e) {
            log.error("Error getting attendance summary: {}", e.getMessage());
            return ChildOverviewDTO.AttendanceSummaryDTO.builder().build();
        }
    }

    private ChildOverviewDTO.GradeSummaryDTO getGradeSummary(Long studentId, Long ownerId) {
        // TODO: Implement full grade summary logic
        return ChildOverviewDTO.GradeSummaryDTO.builder()
                .overallGpa(0.0)
                .totalGrades(0)
                .build();
    }

    private ChildOverviewDTO.FeeSummaryDTO getFeeSummary(Long studentId, Long ownerId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        if (student == null) return ChildOverviewDTO.FeeSummaryDTO.builder().build();
        
        return ChildOverviewDTO.FeeSummaryDTO.builder()
                .totalFees(student.getTotalFees())
                .paidFees(student.getFeesPaid())
                .pendingFees(student.getFeesBalance())
                .paymentStatus(student.getFeesBalance() > 0 ? "PENDING" : "UP_TO_DATE")
                .build();
    }

    private List<AssignmentResponse> getRecentAssignments(Long studentId, Long ownerId) {
        // TODO: Implement assignment fetching
        return new ArrayList<>();
    }

    private List<ExamResponse> getUpcomingExams(Long studentId, Long ownerId) {
        // TODO: Implement exam fetching
        return new ArrayList<>();
    }

    private List<ParentDashboardDTO.ParentNotificationDTO> generateNotifications(List<ChildSummaryDTO> children) {
        List<ParentDashboardDTO.ParentNotificationDTO> notifications = new ArrayList<>();
        
        for (ChildSummaryDTO child : children) {
            if (child.isHasOverdueFees()) {
                notifications.add(ParentDashboardDTO.ParentNotificationDTO.builder()
                        .childName(child.getName())
                        .childId(child.getId())
                        .type("FEE_DUE")
                        .title("Fee Payment Overdue")
                        .message("Fee payment is overdue for " + child.getName())
                        .priority("HIGH")
                        .isRead(false)
                        .actionRequired("YES")
                        .build());
            }
            
            if (child.getAttendancePercentage() != null && child.getAttendancePercentage() < 75) {
                notifications.add(ParentDashboardDTO.ParentNotificationDTO.builder()
                        .childName(child.getName())
                        .childId(child.getId())
                        .type("ATTENDANCE_LOW")
                        .title("Low Attendance Alert")
                        .message("Attendance is below 75% for " + child.getName())
                        .priority("HIGH")
                        .isRead(false)
                        .build());
            }
        }
        
        return notifications;
    }

    private List<ParentDashboardDTO.EventSummaryDTO> getUpcomingEvents(Long ownerId) {
        try {
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "startDateTime"));
            return eventRepository.findUpcomingEventsByOwner(
                    ownerId, java.time.LocalDateTime.now(), pageable)
                    .stream()
                    .map(event -> ParentDashboardDTO.EventSummaryDTO.builder()
                            .id(event.getId())
                            .title(event.getEventName())
                            .description(event.getDescription())
                            .eventType(event.getEventType().name())
                            .date(event.getStartDateTime().toLocalDate().toString())
                            .time(event.getStartDateTime().toLocalTime().toString())
                            .location(event.getVenue())
                            .status(event.getStatus() != null ? event.getStatus().name() : "SCHEDULED")
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching upcoming events: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private ParentDashboardDTO.PendingItemsDTO calculatePendingItems(List<ChildSummaryDTO> children) {
        int pendingFees = (int) children.stream().filter(c -> c.getPendingFees() > 0).count();
        int pendingAssignments = children.stream().mapToInt(c -> c.getPendingAssignments() != null ? c.getPendingAssignments() : 0).sum();
        int upcomingExams = children.stream().mapToInt(c -> c.getUpcomingExams() != null ? c.getUpcomingExams() : 0).sum();
        
        return ParentDashboardDTO.PendingItemsDTO.builder()
                .pendingFees(pendingFees)
                .pendingAssignments(pendingAssignments)
                .upcomingExams(upcomingExams)
                .unreadMessages(0)
                .build();
    }

    private List<ParentDashboardDTO.ActivityDTO> generateRecentActivities(List<ChildSummaryDTO> children) {
        // TODO: Implement activity timeline
        return new ArrayList<>();
    }
}

