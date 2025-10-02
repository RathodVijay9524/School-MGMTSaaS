package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AssignmentRequest;
import com.vijay.User_Master.dto.AssignmentResponse;
import com.vijay.User_Master.dto.AssignmentStatistics;
import com.vijay.User_Master.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Assignment management
 */
public interface AssignmentService {

    /**
     * Create a new assignment
     */
    AssignmentResponse createAssignment(AssignmentRequest request, Long ownerId);

    /**
     * Update an existing assignment
     */
    AssignmentResponse updateAssignment(Long id, AssignmentRequest request, Long ownerId);

    /**
     * Get assignment by ID
     */
    AssignmentResponse getAssignmentById(Long id, Long ownerId);

    /**
     * Get all assignments for owner with pagination
     */
    Page<AssignmentResponse> getAllAssignments(Long ownerId, Pageable pageable);

    /**
     * Get assignments by class
     */
    Page<AssignmentResponse> getAssignmentsByClass(Long classId, Long ownerId, Pageable pageable);

    /**
     * Get assignments by subject
     */
    List<AssignmentResponse> getAssignmentsBySubject(Long subjectId, Long ownerId);

    /**
     * Get assignments by teacher
     */
    List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId, Long ownerId);

    /**
     * Get assignments by status
     */
    Page<AssignmentResponse> getAssignmentsByStatus(Assignment.AssignmentStatus status, Long ownerId, Pageable pageable);

    /**
     * Get assignments by type
     */
    List<AssignmentResponse> getAssignmentsByType(Assignment.AssignmentType type, Long ownerId);

    /**
     * Get overdue assignments
     */
    List<AssignmentResponse> getOverdueAssignments(Long ownerId);

    /**
     * Get upcoming assignments
     */
    List<AssignmentResponse> getUpcomingAssignments(Long ownerId);

    /**
     * Get assignments by due date range
     */
    List<AssignmentResponse> getAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Long ownerId);

    /**
     * Search assignments by keyword
     */
    Page<AssignmentResponse> searchAssignments(String keyword, Long ownerId, Pageable pageable);

    /**
     * Delete assignment (soft delete)
     */
    void deleteAssignment(Long id, Long ownerId);

    /**
     * Restore deleted assignment
     */
    void restoreAssignment(Long id, Long ownerId);

    /**
     * Get assignment statistics
     */
    AssignmentStatistics getAssignmentStatistics(Long ownerId);
}
