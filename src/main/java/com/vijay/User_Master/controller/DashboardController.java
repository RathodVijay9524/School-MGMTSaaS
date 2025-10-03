package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.DashboardAnalytics;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Dashboard Analytics REST Controller
 * Provides comprehensive analytics for both Application Owner and School Owner dashboards
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard Analytics", description = "APIs for dashboard analytics and metrics")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get Application Owner Dashboard Analytics
     * Shows aggregated data from all schools in the system
     * Only accessible by SUPER_ADMIN role
     */
    @GetMapping("/application-owner")
    @Operation(summary = "Get Application Owner Dashboard", 
               description = "Get comprehensive analytics for all schools in the system")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> getApplicationOwnerDashboard() {
        log.info("Getting Application Owner Dashboard Analytics");
        DashboardAnalytics analytics = dashboardService.getApplicationOwnerDashboard();
        return ExceptionUtil.createBuildResponse(analytics, HttpStatus.OK);
    }

    /**
     * Get School Owner Dashboard Analytics
     * Shows data specific to the logged-in school owner's school
     * Accessible by school owners and admins
     */
    @GetMapping("/school-owner")
    @Operation(summary = "Get School Owner Dashboard", 
               description = "Get comprehensive analytics for the logged-in school owner's school")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_USER')")
    public ResponseEntity<DashboardAnalytics> getSchoolOwnerDashboard() {
        log.info("Getting School Owner Dashboard Analytics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        DashboardAnalytics analytics = dashboardService.getSchoolOwnerDashboard(ownerId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get School Analytics for a specific school
     * Used by Application Owner to view specific school analytics
     * Only accessible by SUPER_ADMIN role
     */
    @GetMapping("/school/{schoolOwnerId}")
    @Operation(summary = "Get School Analytics", 
               description = "Get analytics for a specific school by school owner ID")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<DashboardAnalytics> getSchoolAnalytics(
            @Parameter(description = "School Owner ID") @PathVariable Long schoolOwnerId) {
        log.info("Getting School Analytics for school owner: {}", schoolOwnerId);
        DashboardAnalytics analytics = dashboardService.getSchoolAnalytics(schoolOwnerId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get Quick Stats for Dashboard Widgets
     * Returns essential metrics for dashboard widgets
     */
    @GetMapping("/quick-stats")
    @Operation(summary = "Get Quick Stats", 
               description = "Get essential metrics for dashboard widgets")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_USER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<DashboardAnalytics.QuickStats> getQuickStats() {
        log.info("Getting Quick Stats for current user");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        // Check if user is super admin (no owner context)
        boolean isSuperAdmin = CommonUtils.getLoggedInUser().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        DashboardAnalytics.QuickStats stats = dashboardService.getQuickStats(isSuperAdmin ? null : ownerId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get Application Owner Quick Stats
     * Shows quick stats for all schools
     * Only accessible by SUPER_ADMIN role
     */
    @GetMapping("/application-owner/quick-stats")
    @Operation(summary = "Get Application Owner Quick Stats", 
               description = "Get essential metrics for all schools")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<DashboardAnalytics.QuickStats> getApplicationOwnerQuickStats() {
        log.info("Getting Application Owner Quick Stats");
        DashboardAnalytics.QuickStats stats = dashboardService.getQuickStats(null);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get School Owner Quick Stats
     * Shows quick stats for the logged-in school owner's school
     */
    @GetMapping("/school-owner/quick-stats")
    @Operation(summary = "Get School Owner Quick Stats", 
               description = "Get essential metrics for the school owner's school")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_USER')")
    public ResponseEntity<DashboardAnalytics.QuickStats> getSchoolOwnerQuickStats() {
        log.info("Getting School Owner Quick Stats");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        DashboardAnalytics.QuickStats stats = dashboardService.getQuickStats(ownerId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get Dashboard Summary
     * Returns a summary of key metrics for the dashboard
     */
    @GetMapping("/summary")
    @Operation(summary = "Get Dashboard Summary", 
               description = "Get a summary of key dashboard metrics")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_USER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<DashboardAnalytics> getDashboardSummary() {
        log.info("Getting Dashboard Summary for current user");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        // Check if user is super admin
        boolean isSuperAdmin = CommonUtils.getLoggedInUser().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        DashboardAnalytics analytics;
        if (isSuperAdmin) {
            analytics = dashboardService.getApplicationOwnerDashboard();
        } else {
            analytics = dashboardService.getSchoolOwnerDashboard(ownerId);
        }
        
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get School Performance Metrics
     * Returns performance metrics for a specific school
     */
    @GetMapping("/school/{schoolOwnerId}/performance")
    @Operation(summary = "Get School Performance Metrics", 
               description = "Get performance metrics for a specific school")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<DashboardAnalytics> getSchoolPerformance(
            @Parameter(description = "School Owner ID") @PathVariable Long schoolOwnerId) {
        log.info("Getting School Performance Metrics for school owner: {}", schoolOwnerId);
        DashboardAnalytics analytics = dashboardService.getSchoolAnalytics(schoolOwnerId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get Dashboard Health Check
     * Returns system health metrics for the dashboard
     */
    @GetMapping("/health")
    @Operation(summary = "Get Dashboard Health Check", 
               description = "Get system health metrics for the dashboard")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_USER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<DashboardAnalytics.QuickStats> getDashboardHealth() {
        log.info("Getting Dashboard Health Check");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        // Check if user is super admin
        boolean isSuperAdmin = CommonUtils.getLoggedInUser().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        DashboardAnalytics.QuickStats health = dashboardService.getQuickStats(isSuperAdmin ? null : ownerId);
        return ResponseEntity.ok(health);
    }
}
