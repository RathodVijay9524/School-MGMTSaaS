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
