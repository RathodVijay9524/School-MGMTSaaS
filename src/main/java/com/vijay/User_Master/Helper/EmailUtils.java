package com.vijay.User_Master.Helper;

import org.springframework.stereotype.Component;

@Component
public class EmailUtils {
    
    public void sendEmail(String to, String subject, String body) {
        // Implementation for sending email
        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }
    
    public void sendPasswordResetEmail(String email, String resetToken) {
        String subject = "Password Reset Request";
        String body = "Click the link to reset your password: " + resetToken;
        sendEmail(email, subject, body);
    }
    
    public void sendWelcomeEmail(String email, String username) {
        String subject = "Welcome to School Management System";
        String body = "Welcome " + username + "! Your account has been created successfully.";
        sendEmail(email, subject, body);
    }
}