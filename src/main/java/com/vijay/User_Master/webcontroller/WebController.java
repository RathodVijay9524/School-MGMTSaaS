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
}
