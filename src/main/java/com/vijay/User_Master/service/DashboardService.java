package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DashboardAnalytics;

/**
 * Service interface for Dashboard Analytics
 * Provides analytics for both Application Owner (all schools) and School Owner (single school)
 */
public interface DashboardService {
    
    /**
     * Get comprehensive dashboard analytics for Application Owner (Super Admin)
     * Shows aggregated data from all schools in the system
     * 
     * @return DashboardAnalytics with all schools data
     */
    DashboardAnalytics getApplicationOwnerDashboard();
    
    /**
     * Get comprehensive dashboard analytics for School Owner
     * Shows data specific to the logged-in school owner's school
     * 
     * @param ownerId The school owner's user ID
     * @return DashboardAnalytics with school-specific data
     */
    DashboardAnalytics getSchoolOwnerDashboard(Long ownerId);
    
    /**
     * Get dashboard analytics for a specific school (by school owner ID)
     * Used by Application Owner to view specific school analytics
     * 
     * @param schoolOwnerId The school owner's user ID
     * @return DashboardAnalytics for the specific school
     */
    DashboardAnalytics getSchoolAnalytics(Long schoolOwnerId);
    
    /**
     * Get quick stats summary for dashboard widgets
     * 
     * @param ownerId School owner ID (null for application owner)
     * @return Quick stats for dashboard widgets
     */
    DashboardAnalytics.QuickStats getQuickStats(Long ownerId);
}
