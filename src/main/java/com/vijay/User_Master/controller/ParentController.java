package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.ChildOverviewDTO;
import com.vijay.User_Master.dto.ChildSummaryDTO;
import com.vijay.User_Master.dto.ParentDashboardDTO;
import com.vijay.User_Master.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Parent Portal REST Controller
 * Handles all parent-related API endpoints for viewing children's academic progress,
 * attendance, fees, and other school-related information
 */
@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@Slf4j
public class ParentController {

    private final ParentService parentService;

    /**
     * Get complete parent dashboard with all children and aggregated statistics
     * 
     * @param parentId ID of the parent Worker
     * @return ParentDashboardDTO with dashboard data
     */
    @GetMapping("/{parentId}/dashboard")
    @PreAuthorize("hasRole('ROLE_PARENT')")
    public ResponseEntity<?> getParentDashboard(@PathVariable Long parentId) {
        log.info("Getting parent dashboard for parent ID: {}", parentId);
        
        // Get logged-in user's ID (this is the worker ID for parents)
        Long loggedInParentId = CommonUtils.getLoggedInUser().getId();
        
        ParentDashboardDTO response = parentService.getParentDashboard(loggedInParentId, null);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get list of all children for a parent
     * 
     * @param parentId ID of the parent Worker
     * @return List of ChildSummaryDTO
     */
    @GetMapping("/{parentId}/children")
    @PreAuthorize("hasRole('ROLE_PARENT')")
    public ResponseEntity<?> getParentChildren(@PathVariable Long parentId) {
        log.info("Getting children list for parent ID: {}", parentId);
        
        // Get logged-in user's ID (this is the worker ID for parents)
        Long loggedInParentId = CommonUtils.getLoggedInUser().getId();
        
        List<ChildSummaryDTO> response = parentService.getParentChildren(loggedInParentId, null);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get complete overview of a specific child including attendance, grades, fees, etc.
     * 
     * @param parentId ID of the parent Worker
     * @param studentId ID of the student Worker (child)
     * @return ChildOverviewDTO with complete child data
     */
    @GetMapping("/{parentId}/child/{studentId}/overview")
    @PreAuthorize("hasRole('ROLE_PARENT')")
    public ResponseEntity<?> getChildOverview(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {
        log.info("Getting child overview for parent ID: {} and student ID: {}", parentId, studentId);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        ChildOverviewDTO response = parentService.getChildOverview(parentId, studentId, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get summary information for a specific child
     * 
     * @param parentId ID of the parent Worker
     * @param studentId ID of the student Worker (child)
     * @return ChildSummaryDTO
     */
    @GetMapping("/{parentId}/child/{studentId}/summary")
    @PreAuthorize("hasRole('ROLE_PARENT')")
    public ResponseEntity<?> getChildSummary(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {
        log.info("Getting child summary for parent ID: {} and student ID: {}", parentId, studentId);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        // Verify parent access first
        boolean hasAccess = parentService.verifyParentAccess(parentId, studentId, ownerId);
        if (!hasAccess) {
            return ExceptionUtil.createBuildResponseMessage(
                "Parent does not have access to this student", 
                HttpStatus.FORBIDDEN);
        }
        
        ChildSummaryDTO response = parentService.getChildSummary(studentId, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Verify if a parent has access to a specific student
     * 
     * @param parentId ID of the parent Worker
     * @param studentId ID of the student Worker
     * @return Boolean indicating access status
     */
    @GetMapping("/{parentId}/verify-access/{studentId}")
    @PreAuthorize("hasRole('ROLE_PARENT')")
    public ResponseEntity<?> verifyParentAccess(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {
        log.info("Verifying parent access - Parent ID: {}, Student ID: {}", parentId, studentId);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        boolean hasAccess = parentService.verifyParentAccess(parentId, studentId, ownerId);
        return ExceptionUtil.createBuildResponse(hasAccess, HttpStatus.OK);
    }

    /**
     * Get aggregated statistics for all children of a parent
     * 
     * @param parentId ID of the parent Worker
     * @return DashboardSummaryDTO with aggregated statistics
     */
    @GetMapping("/{parentId}/statistics")
    @PreAuthorize("hasRole('ROLE_PARENT')")
    public ResponseEntity<?> getAggregatedStatistics(@PathVariable Long parentId) {
        log.info("Getting aggregated statistics for parent ID: {}", parentId);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        ParentDashboardDTO.DashboardSummaryDTO response = 
                parentService.getAggregatedStatistics(parentId, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }
}

