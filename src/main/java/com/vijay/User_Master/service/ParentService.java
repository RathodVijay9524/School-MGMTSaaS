package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.ChildOverviewDTO;
import com.vijay.User_Master.dto.ChildSummaryDTO;
import com.vijay.User_Master.dto.ParentDashboardDTO;

import java.util.List;

/**
 * Service interface for Parent Portal operations
 * Handles all parent-related business logic including children management,
 * dashboard analytics, and multi-child data aggregation
 */
public interface ParentService {

    /**
     * Get complete dashboard data for parent
     * Includes all children summaries, notifications, events, and aggregated statistics
     *
     * @param parentId ID of the parent Worker
     * @param ownerId ID of the owner (for multi-tenancy)
     * @return ParentDashboardDTO with complete dashboard data
     */
    ParentDashboardDTO getParentDashboard(Long parentId, Long ownerId);

    /**
     * Get list of all children for a parent
     *
     * @param parentId ID of the parent Worker
     * @param ownerId ID of the owner (for multi-tenancy)
     * @return List of ChildSummaryDTO
     */
    List<ChildSummaryDTO> getParentChildren(Long parentId, Long ownerId);

    /**
     * Get complete overview of a specific child
     * Includes attendance, grades, fees, assignments, exams, and notifications
     *
     * @param parentId ID of the parent Worker
     * @param studentId ID of the student Worker
     * @param ownerId ID of the owner (for multi-tenancy)
     * @return ChildOverviewDTO with complete child data
     */
    ChildOverviewDTO getChildOverview(Long parentId, Long studentId, Long ownerId);

    /**
     * Verify if a parent has access to a specific student
     *
     * @param parentId ID of the parent Worker
     * @param studentId ID of the student Worker
     * @param ownerId ID of the owner (for multi-tenancy)
     * @return true if parent has access, false otherwise
     */
    boolean verifyParentAccess(Long parentId, Long studentId, Long ownerId);

    /**
     * Get child summary by student ID
     *
     * @param studentId ID of the student Worker
     * @param ownerId ID of the owner (for multi-tenancy)
     * @return ChildSummaryDTO
     */
    ChildSummaryDTO getChildSummary(Long studentId, Long ownerId);

    /**
     * Get aggregated attendance statistics for all children
     *
     * @param parentId ID of the parent Worker
     * @param ownerId ID of the owner (for multi-tenancy)
     * @return Map of statistics
     */
    ParentDashboardDTO.DashboardSummaryDTO getAggregatedStatistics(Long parentId, Long ownerId);
}

