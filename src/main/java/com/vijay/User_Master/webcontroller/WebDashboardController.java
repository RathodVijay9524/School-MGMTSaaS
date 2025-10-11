package com.vijay.User_Master.webcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web Dashboard Controller for role-based dashboards
 * Handles routing to different dashboards based on user roles
 */
@Controller
@RequestMapping("/dashboard")
public class WebDashboardController {

    /**
     * Owner/Admin Dashboard
     */
    @GetMapping("/owner")
    public String ownerDashboard(Model model) {
        model.addAttribute("pageTitle", "Owner Dashboard - School Management");
        model.addAttribute("userRole", "OWNER");
        return "dashboard/owner";
    }

    /**
     * Reports Page - Analytics and Reports
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Reports & Analytics - School Management");
        model.addAttribute("userRole", "OWNER");
        return "dashboard/reports";
    }

    /**
     * Attendance Management Page
     */
    @GetMapping("/attendance")
    public String attendance(Model model) {
        model.addAttribute("pageTitle", "Attendance Management - School Management");
        model.addAttribute("userRole", "OWNER");
        return "dashboard/attendance";
    }

    /**
     * Assignments Management Page
     */
    @GetMapping("/assignments")
    public String assignments(Model model) {
        model.addAttribute("pageTitle", "Assignments Management - School Management");
        model.addAttribute("userRole", "OWNER");
        return "dashboard/assignments";
    }

    /**
     * Grades Management Page
     */
    @GetMapping("/grades")
    public String grades(Model model) {
        model.addAttribute("pageTitle", "Grades Management - School Management");
        model.addAttribute("userRole", "OWNER");
        return "dashboard/grades";
    }

    /**
     * Teacher Dashboard
     */
    @GetMapping("/teacher")
    public String teacherDashboard(Model model) {
        model.addAttribute("pageTitle", "Teacher Dashboard - School Management");
        model.addAttribute("userRole", "TEACHER");
        return "dashboard/teacher";
    }

    /**
     * Teacher Assignment Management page
     */
    @GetMapping("/teacher/assignments")
    public String teacherAssignments(Model model) {
        model.addAttribute("pageTitle", "Assignment Management - Teacher Dashboard");
        model.addAttribute("userRole", "TEACHER");
        return "dashboard/teacher-assignments";
    }

    /**
     * Teacher Schedule Exam page
     */
    @GetMapping("/teacher/schedule-exam")
    public String teacherScheduleExam(Model model) {
        model.addAttribute("pageTitle", "Schedule Exam - Teacher Dashboard");
        model.addAttribute("userRole", "TEACHER");
        return "dashboard/teacher-schedule-exam";
    }

    /**
     * Teacher Grades page
     */
    @GetMapping("/teacher/grades")
    public String teacherGrades(Model model) {
        model.addAttribute("pageTitle", "Grade Management - Teacher Dashboard");
        model.addAttribute("userRole", "TEACHER");
        return "dashboard/teacher-grades";
    }

    /**
     * Teacher Timetable page
     */
    @GetMapping("/teacher/timetable")
    public String teacherTimetable(Model model) {
        model.addAttribute("pageTitle", "Timetable Management - Teacher Dashboard");
        model.addAttribute("userRole", "TEACHER");
        return "dashboard/teacher-timetable";
    }

    /**
     * Teacher My Classes page
     */
    @GetMapping("/teacher/classes")
    public String teacherClasses(Model model) {
        model.addAttribute("pageTitle", "My Classes - Teacher Dashboard");
        model.addAttribute("userRole", "TEACHER");
        return "dashboard/teacher-classes";
    }

    /**
     * Teacher Students page
     */
    @GetMapping("/teacher/students")
    public String teacherStudents(Model model) {
        model.addAttribute("pageTitle", "Students - Teacher Dashboard");
        model.addAttribute("userRole", "TEACHER");
        return "dashboard/teacher-students";
    }

    /**
     * Student Dashboard
     */
    @GetMapping("/student")
    public String studentDashboard(Model model) {
        model.addAttribute("pageTitle", "Student Dashboard - School Management");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student";
    }

    @GetMapping("/student/timetable")
    public String studentTimetable(Model model) {
        model.addAttribute("pageTitle", "My Timetable - Student Dashboard");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student-timetable";
    }

    @GetMapping("/student/assignments")
    public String studentAssignments(Model model) {
        model.addAttribute("pageTitle", "My Assignments - Student Dashboard");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student-assignments";
    }

    @GetMapping("/student/exams")
    public String studentExams(Model model) {
        model.addAttribute("pageTitle", "My Exams - Student Dashboard");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student-exams";
    }

    @GetMapping("/student/grades")
    public String studentGrades(Model model) {
        model.addAttribute("pageTitle", "My Grades - Student Dashboard");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student-grades";
    }

    @GetMapping("/student/attendance")
    public String studentAttendance(Model model) {
        model.addAttribute("pageTitle", "My Attendance - Student Dashboard");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student-attendance";
    }

    @GetMapping("/student/subjects")
    public String studentSubjects(Model model) {
        model.addAttribute("pageTitle", "My Subjects - Student Dashboard");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student-subjects";
    }

    @GetMapping("/student/fees")
    public String studentFees(Model model) {
        model.addAttribute("pageTitle", "My Fees - Student Dashboard");
        model.addAttribute("userRole", "STUDENT");
        return "dashboard/student-fees";
    }

    /**
     * Parent Dashboard
     */
    @GetMapping("/parent")
    public String parentDashboard(Model model) {
        model.addAttribute("pageTitle", "Parent Dashboard - School Management");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent";
    }

    /**
     * Parent Portal Sub-Pages
     */
    @GetMapping("/parent/children")
    public String parentChildren(Model model) {
        model.addAttribute("pageTitle", "My Children - Parent Portal");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent-children";
    }

    @GetMapping("/parent/progress")
    public String parentProgress(Model model) {
        model.addAttribute("pageTitle", "Academic Progress - Parent Portal");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent-progress";
    }

    @GetMapping("/parent/attendance")
    public String parentAttendance(Model model) {
        model.addAttribute("pageTitle", "Attendance - Parent Portal");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent-attendance";
    }

    @GetMapping("/parent/assignments")
    public String parentAssignments(Model model) {
        model.addAttribute("pageTitle", "Assignments - Parent Portal");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent-assignments";
    }

    @GetMapping("/parent/exams")
    public String parentExams(Model model) {
        model.addAttribute("pageTitle", "Exams - Parent Portal");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent-exams";
    }

    @GetMapping("/parent/fees")
    public String parentFees(Model model) {
        model.addAttribute("pageTitle", "Fees - Parent Portal");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent-fees";
    }

    @GetMapping("/parent/messages")
    public String parentMessages(Model model) {
        model.addAttribute("pageTitle", "Messages - Parent Portal");
        model.addAttribute("userRole", "PARENT");
        return "dashboard/parent-messages";
    }

    /**
     * Librarian Dashboard
     */
    @GetMapping("/librarian")
    public String librarianDashboard(Model model) {
        model.addAttribute("pageTitle", "Librarian Dashboard - School Management");
        model.addAttribute("userRole", "LIBRARIAN");
        return "dashboard/librarian";
    }

    /**
     * Super Admin Dashboard - Application Owner
     */
    @GetMapping("/super-admin")
    public String superAdminDashboard(Model model) {
        model.addAttribute("pageTitle", "Super Admin Dashboard - All Schools Overview");
        model.addAttribute("userRole", "SUPER_ADMIN");
        return "dashboard/super-admin";
    }

    /**
     * General dashboard - will redirect based on user role
     */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard - School Management");
        return "dashboard/index";
    }
}
