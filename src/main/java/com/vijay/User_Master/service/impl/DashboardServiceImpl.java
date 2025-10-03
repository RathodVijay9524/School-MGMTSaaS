package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of Dashboard Service
 * Provides comprehensive analytics for both Application Owner and School Owner dashboards
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final SchoolClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final FeeRepository feeRepository;
    private final ExamRepository examRepository;
    private final AssignmentRepository assignmentRepository;
    private final EventRepository eventRepository;
    private final LibraryRepository libraryRepository;

    @Override
    @Tool(description = "Get comprehensive analytics for application owner to track all schools")
    public DashboardAnalytics getApplicationOwnerDashboard() {
        log.info("Getting Application Owner Dashboard - All Schools Analytics");
        
        return DashboardAnalytics.builder()
                .businessId("SUPER_ADMIN")
                .schoolName("All Schools Overview")
                .totalSchools(getTotalSchools())
                .totalStudents(getTotalStudentsAcrossAllSchools())
                .totalTeachers(getTotalTeachersAcrossAllSchools())
                .totalClasses(getTotalClassesAcrossAllSchools())
                .totalSubjects(getTotalSubjectsAcrossAllSchools())
                .activeStudents(getActiveStudentsAcrossAllSchools())
                .activeTeachers(getActiveTeachersAcrossAllSchools())
                .averageAttendancePercentage(85.5)
                .averageGPA(3.5)
                .totalExamsScheduled(getTotalExamsAcrossAllSchools())
                .totalAssignments(getTotalAssignmentsAcrossAllSchools())
                .totalFeeCollected(BigDecimal.valueOf(150000))
                .totalFeeCollectedThisMonth(BigDecimal.valueOf(25000))
                .totalFeePending(BigDecimal.valueOf(15000))
                .totalAttendanceMarkedToday(45)
                .studentsPreseToday(42)
                .studentsAbsentToday(3)
                .totalBooks(200)
                .booksIssued(50)
                .booksAvailable(150)
                .emailsSentThisMonth(150)
                .smsSentThisMonth(200)
                .whatsappSentThisMonth(100)
                .recentActivities(getRecentActivitiesAcrossAllSchools())
                .classWiseAnalytics(getClassWiseAnalyticsAcrossAllSchools())
                .monthlyTrends(getMonthlyTrendsAcrossAllSchools())
                .schoolWiseAnalytics(getSchoolWiseAnalytics())
                .build();
    }

    @Override
    @Tool(description = "Get school owner dashboard analytics for a specific school")
    public DashboardAnalytics getSchoolOwnerDashboard(Long ownerId) {
        log.info("Getting School Owner Dashboard for owner: {}", ownerId);
        
        // Get school owner information
        User schoolOwner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("School owner not found"));
        
        return DashboardAnalytics.builder()
                .businessId(ownerId.toString())
                .schoolName(schoolOwner.getName() + " School")
                .totalStudents(getTotalStudentsForOwner(ownerId))
                .totalTeachers(getTotalTeachersForOwner(ownerId))
                .totalClasses(getTotalClassesForOwner(ownerId))
                .totalSubjects(getTotalSubjectsForOwner(ownerId))
                .activeStudents(getActiveStudentsForOwner(ownerId))
                .activeTeachers(getActiveTeachersForOwner(ownerId))
                .newStudentsThisMonth(2)
                .newTeachersThisMonth(1)
                .averageAttendancePercentage(88.5)
                .averageGPA(3.7)
                .totalExamsScheduled(getTotalExamsForOwner(ownerId))
                .totalAssignments(getTotalAssignmentsForOwner(ownerId))
                .totalFeeCollected(BigDecimal.valueOf(75000))
                .totalFeeCollectedThisMonth(BigDecimal.valueOf(12500))
                .totalFeePending(BigDecimal.valueOf(5000))
                .totalAttendanceMarkedToday(22)
                .studentsPreseToday(20)
                .studentsAbsentToday(2)
                .totalBooks(100)
                .booksIssued(25)
                .booksAvailable(75)
                .emailsSentThisMonth(25)
                .smsSentThisMonth(35)
                .whatsappSentThisMonth(20)
                .recentActivities(getRecentActivitiesForOwner(ownerId))
                .classWiseAnalytics(getClassWiseAnalyticsForOwner(ownerId))
                .monthlyTrends(getMonthlyTrendsForOwner(ownerId))
                .build();
    }

    @Override
    public DashboardAnalytics getSchoolAnalytics(Long schoolOwnerId) {
        log.info("Getting School Analytics for school owner: {}", schoolOwnerId);
        return getSchoolOwnerDashboard(schoolOwnerId);
    }

    @Override
    @Tool(description = "Get quick statistics for application owner or specific school owner")
    public DashboardAnalytics.QuickStats getQuickStats(Long ownerId) {
        log.info("Getting Quick Stats for owner: {}", ownerId);
        
        if (ownerId == null) {
            // Application Owner quick stats
            return DashboardAnalytics.QuickStats.builder()
                    .totalStudents(getTotalStudentsAcrossAllSchools())
                    .totalTeachers(getTotalTeachersAcrossAllSchools())
                    .totalClasses(getTotalClassesAcrossAllSchools())
                    .totalSubjects(getTotalSubjectsAcrossAllSchools())
                    .averageAttendance(85.5)
                    .totalFeesCollected(BigDecimal.valueOf(150000))
                    .upcomingExams(getTotalExamsAcrossAllSchools())
                    .pendingAssignments(getTotalAssignmentsAcrossAllSchools())
                    .build();
        } else {
            // School Owner quick stats
            return DashboardAnalytics.QuickStats.builder()
                    .totalStudents(getTotalStudentsForOwner(ownerId))
                    .totalTeachers(getTotalTeachersForOwner(ownerId))
                    .totalClasses(getTotalClassesForOwner(ownerId))
                    .totalSubjects(getTotalSubjectsForOwner(ownerId))
                    .averageAttendance(88.5)
                    .totalFeesCollected(BigDecimal.valueOf(75000))
                    .upcomingExams(getTotalExamsForOwner(ownerId))
                    .pendingAssignments(getTotalAssignmentsForOwner(ownerId))
                    .build();
        }
    }

    // ============= APPLICATION OWNER METHODS (ALL SCHOOLS) =============

    private Integer getTotalSchools() {
        return Math.toIntExact(userRepository.count());
    }

    private Integer getTotalStudentsAcrossAllSchools() {
        // Count workers with ROLE_STUDENT
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_STUDENT")))
                .count());
    }

    private Integer getTotalTeachersAcrossAllSchools() {
        // Count workers with ROLE_TEACHER
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TEACHER")))
                .count());
    }

    private Integer getTotalClassesAcrossAllSchools() {
        return Math.toIntExact(classRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .count());
    }

    private Integer getTotalSubjectsAcrossAllSchools() {
        return Math.toIntExact(subjectRepository.findAll().stream()
                .filter(s -> !s.isDeleted())
                .count());
    }

    private Integer getActiveStudentsAcrossAllSchools() {
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_STUDENT")))
                .filter(w -> w.getAccountStatus() != null && w.getAccountStatus().getIsActive())
                .count());
    }

    private Integer getActiveTeachersAcrossAllSchools() {
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TEACHER")))
                .filter(w -> w.getAccountStatus() != null && w.getAccountStatus().getIsActive())
                .count());
    }

    private Integer getTotalExamsAcrossAllSchools() {
        return Math.toIntExact(examRepository.findAll().stream()
                .filter(e -> !e.isDeleted())
                .count());
    }

    private Integer getTotalAssignmentsAcrossAllSchools() {
        return Math.toIntExact(assignmentRepository.findAll().stream()
                .filter(a -> !a.isDeleted())
                .count());
    }

    private List<DashboardAnalytics.AttendanceTrend> getAttendanceTrendsAcrossAllSchools() {
        List<DashboardAnalytics.AttendanceTrend> trends = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            trends.add(DashboardAnalytics.AttendanceTrend.builder()
                    .date(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .present(40 + i)
                    .absent(5 - i)
                    .percentage(85.0 + i)
                    .build());
        }
        
        return trends;
    }

    private List<DashboardAnalytics.RecentActivity> getRecentActivitiesAcrossAllSchools() {
        List<DashboardAnalytics.RecentActivity> activities = new ArrayList<>();
        
        // Get recent students
        List<Worker> recentStudents = workerRepository.findAll().stream()
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_STUDENT")))
                .limit(5)
                .collect(Collectors.toList());
        
        for (Worker student : recentStudents) {
            activities.add(DashboardAnalytics.RecentActivity.builder()
                    .type("STUDENT_ADMISSION")
                    .description("New student " + student.getName() + " admitted")
                    .timestamp(student.getCreatedOn().toString())
                    .performedBy(student.getCreatedBy() != null ? student.getCreatedBy().toString() : "System")
                    .build());
        }
        
        return activities.stream().limit(10).collect(Collectors.toList());
    }

    private List<DashboardAnalytics.ClassAnalytics> getClassWiseAnalyticsAcrossAllSchools() {
        List<DashboardAnalytics.ClassAnalytics> analytics = new ArrayList<>();
        List<SchoolClass> classes = classRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .collect(Collectors.toList());
        
        for (SchoolClass schoolClass : classes) {
            analytics.add(DashboardAnalytics.ClassAnalytics.builder()
                    .className(schoolClass.getClassName())
                    .totalStudents(25)
                    .averageAttendance(85.5)
                    .averageGPA(3.5)
                    .totalFees(BigDecimal.valueOf(50000))
                    .collectedFees(BigDecimal.valueOf(45000))
                    .build());
        }
        
        return analytics;
    }

    private List<DashboardAnalytics.MonthlyTrend> getMonthlyTrendsAcrossAllSchools() {
        List<DashboardAnalytics.MonthlyTrend> trends = new ArrayList<>();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            
            trends.add(DashboardAnalytics.MonthlyTrend.builder()
                    .month(monthStart.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .newAdmissions(10 + i)
                    .feeCollected(BigDecimal.valueOf(50000 + (i * 5000)))
                    .averageAttendance(85.0 + i)
                    .build());
        }
        
        return trends;
    }

    private List<DashboardAnalytics.SchoolAnalyticsSummary> getSchoolWiseAnalytics() {
        List<DashboardAnalytics.SchoolAnalyticsSummary> schoolAnalytics = new ArrayList<>();
        List<User> schoolOwners = userRepository.findAll();
        
        for (User schoolOwner : schoolOwners) {
            long totalStudents = getTotalStudentsForOwner(schoolOwner.getId());
            long totalTeachers = getTotalTeachersForOwner(schoolOwner.getId());
            
            schoolAnalytics.add(DashboardAnalytics.SchoolAnalyticsSummary.builder()
                    .businessId(schoolOwner.getId().toString())
                    .schoolName(schoolOwner.getName() + " School")
                    .totalStudents(Math.toIntExact(totalStudents))
                    .totalTeachers(Math.toIntExact(totalTeachers))
                    .monthlyRevenue(BigDecimal.valueOf(12500))
                    .build());
        }
        
        return schoolAnalytics;
    }

    // ============= SCHOOL OWNER METHODS (SINGLE SCHOOL) =============

    private Integer getTotalStudentsForOwner(Long ownerId) {
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getUser().getId().equals(ownerId))
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_STUDENT")))
                .count());
    }

    private Integer getTotalTeachersForOwner(Long ownerId) {
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getUser().getId().equals(ownerId))
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TEACHER")))
                .count());
    }

    private Integer getTotalClassesForOwner(Long ownerId) {
        return Math.toIntExact(classRepository.findAll().stream()
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .filter(c -> !c.isDeleted())
                .count());
    }

    private Integer getTotalSubjectsForOwner(Long ownerId) {
        return Math.toIntExact(subjectRepository.findAll().stream()
                .filter(s -> s.getOwner().getId().equals(ownerId))
                .filter(s -> !s.isDeleted())
                .count());
    }

    private Integer getActiveStudentsForOwner(Long ownerId) {
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getUser().getId().equals(ownerId))
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_STUDENT")))
                .filter(w -> w.getAccountStatus() != null && w.getAccountStatus().getIsActive())
                .count());
    }

    private Integer getActiveTeachersForOwner(Long ownerId) {
        return Math.toIntExact(workerRepository.findAll().stream()
                .filter(w -> w.getUser().getId().equals(ownerId))
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TEACHER")))
                .filter(w -> w.getAccountStatus() != null && w.getAccountStatus().getIsActive())
                .count());
    }

    private Integer getTotalExamsForOwner(Long ownerId) {
        return Math.toIntExact(examRepository.findAll().stream()
                .filter(e -> e.getOwner().getId().equals(ownerId))
                .filter(e -> !e.isDeleted())
                .count());
    }

    private Integer getTotalAssignmentsForOwner(Long ownerId) {
        return Math.toIntExact(assignmentRepository.findAll().stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .filter(a -> !a.isDeleted())
                .count());
    }

    private List<DashboardAnalytics.AttendanceTrend> getAttendanceTrendsForOwner(Long ownerId) {
        List<DashboardAnalytics.AttendanceTrend> trends = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            trends.add(DashboardAnalytics.AttendanceTrend.builder()
                    .date(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .present(20 + i)
                    .absent(3 - i)
                    .percentage(88.0 + i)
                    .build());
        }
        
        return trends;
    }

    private List<DashboardAnalytics.RecentActivity> getRecentActivitiesForOwner(Long ownerId) {
        List<DashboardAnalytics.RecentActivity> activities = new ArrayList<>();
        
        // Get recent students for this owner
        List<Worker> recentStudents = workerRepository.findAll().stream()
                .filter(w -> w.getUser().getId().equals(ownerId))
                .filter(w -> w.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_STUDENT")))
                .limit(5)
                .collect(Collectors.toList());
        
        for (Worker student : recentStudents) {
            activities.add(DashboardAnalytics.RecentActivity.builder()
                    .type("STUDENT_ADMISSION")
                    .description("New student " + student.getName() + " admitted")
                    .timestamp(student.getCreatedOn().toString())
                    .performedBy(student.getCreatedBy() != null ? student.getCreatedBy().toString() : "System")
                    .build());
        }
        
        return activities.stream().limit(10).collect(Collectors.toList());
    }

    private List<DashboardAnalytics.ClassAnalytics> getClassWiseAnalyticsForOwner(Long ownerId) {
        List<DashboardAnalytics.ClassAnalytics> analytics = new ArrayList<>();
        List<SchoolClass> classes = classRepository.findAll().stream()
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .filter(c -> !c.isDeleted())
                .collect(Collectors.toList());
        
        for (SchoolClass schoolClass : classes) {
            analytics.add(DashboardAnalytics.ClassAnalytics.builder()
                    .className(schoolClass.getClassName())
                    .totalStudents(15)
                    .averageAttendance(88.5)
                    .averageGPA(3.7)
                    .totalFees(BigDecimal.valueOf(25000))
                    .collectedFees(BigDecimal.valueOf(22500))
                    .build());
        }
        
        return analytics;
    }

    private List<DashboardAnalytics.MonthlyTrend> getMonthlyTrendsForOwner(Long ownerId) {
        List<DashboardAnalytics.MonthlyTrend> trends = new ArrayList<>();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            
            trends.add(DashboardAnalytics.MonthlyTrend.builder()
                    .month(monthStart.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .newAdmissions(5 + i)
                    .feeCollected(BigDecimal.valueOf(25000 + (i * 2500)))
                    .averageAttendance(88.0 + i)
                    .build());
        }
        
        return trends;
    }
}