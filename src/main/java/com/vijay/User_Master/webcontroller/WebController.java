package com.vijay.User_Master.webcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Main Web Controller for serving Thymeleaf templates
 * Handles all web page routing and serves static HTML pages
 */
@Controller
@RequestMapping("/")
public class WebController {

    /**
     * Landing page - Home page with features and navigation
     */
    @GetMapping
    public String home(Model model) {
        model.addAttribute("pageTitle", "School Management System - Home");
        return "index";
    }

    /**
     * Login page - Single login form for all user types
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login - School Management System");
        return "auth/login";
    }

    /**
     * Registration page - Owner registration form
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Register - School Management System");
        return "auth/register";
    }

    /**
     * Role selection page - For users with multiple roles
     */
    @GetMapping("/role-selection")
    public String roleSelection(Model model) {
        model.addAttribute("pageTitle", "Select Role - School Management System");
        return "auth/role-selection";
    }

    /**
     * Account verification page - Shows verification status and redirects to login
     */
    @GetMapping("/verify-account")
    public String verifyAccount(Model model) {
        model.addAttribute("pageTitle", "Account Verification - School Management System");
        return "auth/verify-account";
    }

    /**
     * Forgot password page - Email input form for password reset
     */
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("pageTitle", "Forgot Password - School Management System");
        return "auth/forgot-password";
    }

    /**
     * Reset password page - New password form with validation
     */
    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        model.addAttribute("pageTitle", "Reset Password - School Management System");
        return "auth/reset-password";
    }

    /**
     * About page
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About - School Management System");
        return "pages/about";
    }

    /**
     * Contact page
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact - School Management System");
        return "pages/contact";
    }

    /**
     * Features page
     */
    @GetMapping("/features")
    public String features(Model model) {
        model.addAttribute("pageTitle", "Features - School Management System");
        return "pages/features";
    }

    /**
     * Students Management page - Owner dashboard
     */
    @GetMapping("/dashboard/students")
    public String studentsManagement(Model model) {
        model.addAttribute("pageTitle", "Student Management - School Management System");
        return "dashboard/students";
    }

    /**
     * Teachers Management page - Owner dashboard
     */
    @GetMapping("/dashboard/teachers")
    public String teachersManagement(Model model) {
        model.addAttribute("pageTitle", "Teacher Management - School Management System");
        return "dashboard/teachers";
    }

    /**
     * Parents Management page - Owner dashboard
     */
    @GetMapping("/dashboard/parents")
    public String parentsManagement(Model model) {
        model.addAttribute("pageTitle", "Parent Management - School Management System");
        return "dashboard/parents";
    }

    /**
     * Role Management page - Super Admin dashboard
     */
    @GetMapping("/dashboard/role-management")
    public String roleManagement(Model model) {
        model.addAttribute("pageTitle", "Role Management - School Management System");
        return "dashboard/role-management";
    }

    /**
     * Super Admin New Dashboard - With sidebar navigation
     */
    @GetMapping("/dashboard/super-admin-new")
    public String superAdminNew(Model model) {
        model.addAttribute("pageTitle", "Super Admin Dashboard - School Management System");
        return "dashboard/super-admin-new";
    }

    /**
     * User Management page - Super Admin dashboard
     */
    @GetMapping("/dashboard/user-management")
    public String userManagement(Model model) {
        model.addAttribute("pageTitle", "User Management - School Management System");
        return "dashboard/user-management";
    }

    /**
     * Class Management page - Owner dashboard
     */
    @GetMapping("/dashboard/classes")
    public String classesManagement(Model model) {
        model.addAttribute("pageTitle", "Class Management - School Management System");
        return "dashboard/classes";
    }

    /**
     * Subject Management page - Owner dashboard
     */
    @GetMapping("/dashboard/subjects")
    public String subjectsManagement(Model model) {
        model.addAttribute("pageTitle", "Subject Management - School Management System");
        return "dashboard/subjects";
    }

    /**
     * Exam Management page - Owner dashboard
     */
    @GetMapping("/dashboard/exams")
    public String examsManagement(Model model) {
        model.addAttribute("pageTitle", "Exam Management - School Management System");
        return "dashboard/exams";
    }

    /**
     * Fee Management page - Owner dashboard
     */
    @GetMapping("/dashboard/fees")
    public String feesManagement(Model model) {
        model.addAttribute("pageTitle", "Fee Management - School Management System");
        return "dashboard/fees";
    }



}
