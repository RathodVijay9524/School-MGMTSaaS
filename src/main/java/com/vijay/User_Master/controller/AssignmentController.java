package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.AssignmentRequest;
import com.vijay.User_Master.dto.AssignmentResponse;
import com.vijay.User_Master.dto.AssignmentStatistics;
import com.vijay.User_Master.entity.Assignment;
import com.vijay.User_Master.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Assignment management
 */
@RestController
@RequestMapping("/api/v1/assignments")
@AllArgsConstructor
@Slf4j
@Tag(name = "Assignment Management", description = "APIs for managing assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @Operation(summary = "Create a new assignment", description = "Create a new assignment for a class and subject")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> createAssignment(@Valid @RequestBody AssignmentRequest request) {
        log.info("Creating assignment: {}", request.getTitle());
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        AssignmentResponse response = assignmentService.createAssignment(request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an assignment", description = "Update an existing assignment")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @Parameter(description = "Assignment ID") @PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request) {
        log.info("Updating assignment: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        AssignmentResponse response = assignmentService.updateAssignment(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID", description = "Retrieve a specific assignment by its ID")
    public ResponseEntity<AssignmentResponse> getAssignmentById(
            @Parameter(description = "Assignment ID") @PathVariable Long id) {
        log.info("Getting assignment: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        AssignmentResponse response = assignmentService.getAssignmentById(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all assignments", description = "Retrieve all assignments with pagination and sorting")
    public ResponseEntity<Page<AssignmentResponse>> getAllAssignments(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "dueDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all assignments - page: {}, size: {}", page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AssignmentResponse> response = assignmentService.getAllAssignments(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get assignments by class", description = "Retrieve all assignments for a specific class")
    public ResponseEntity<Page<AssignmentResponse>> getAssignmentsByClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting assignments for class: {}", classId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<AssignmentResponse> response = assignmentService.getAssignmentsByClass(classId, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get assignments by subject", description = "Retrieve all assignments for a specific subject")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsBySubject(
            @Parameter(description = "Subject ID") @PathVariable Long subjectId) {
        log.info("Getting assignments for subject: {}", subjectId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<AssignmentResponse> response = assignmentService.getAssignmentsBySubject(subjectId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get assignments by teacher", description = "Retrieve all assignments created by a specific teacher")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        log.info("Getting assignments for teacher: {}", teacherId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<AssignmentResponse> response = assignmentService.getAssignmentsByTeacher(teacherId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get assignments by status", description = "Retrieve all assignments with a specific status")
    public ResponseEntity<Page<AssignmentResponse>> getAssignmentsByStatus(
            @Parameter(description = "Assignment status") @PathVariable Assignment.AssignmentStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting assignments with status: {}", status);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<AssignmentResponse> response = assignmentService.getAssignmentsByStatus(status, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get assignments by type", description = "Retrieve all assignments of a specific type")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByType(
            @Parameter(description = "Assignment type") @PathVariable Assignment.AssignmentType type) {
        log.info("Getting assignments with type: {}", type);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<AssignmentResponse> response = assignmentService.getAssignmentsByType(type, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue assignments", description = "Retrieve all assignments that are past their due date")
    public ResponseEntity<List<AssignmentResponse>> getOverdueAssignments() {
        log.info("Getting overdue assignments");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<AssignmentResponse> response = assignmentService.getOverdueAssignments(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming assignments", description = "Retrieve all assignments that are due soon")
    public ResponseEntity<List<AssignmentResponse>> getUpcomingAssignments() {
        log.info("Getting upcoming assignments");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<AssignmentResponse> response = assignmentService.getUpcomingAssignments(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get assignments by date range", description = "Retrieve assignments within a specific date range")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByDateRange(
            @Parameter(description = "Start date (ISO format)") @RequestParam String startDate,
            @Parameter(description = "End date (ISO format)") @RequestParam String endDate) {
        log.info("Getting assignments between {} and {}", startDate, endDate);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<AssignmentResponse> response = assignmentService.getAssignmentsByDateRange(start, end, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search assignments", description = "Search assignments by keyword in title, description, or subject")
    public ResponseEntity<Page<AssignmentResponse>> searchAssignments(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching assignments with keyword: {}", keyword);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<AssignmentResponse> response = assignmentService.searchAssignments(keyword, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get assignment statistics", description = "Get comprehensive statistics about assignments")
    public ResponseEntity<AssignmentStatistics> getAssignmentStatistics() {
        log.info("Getting assignment statistics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        AssignmentStatistics response = assignmentService.getAssignmentStatistics(ownerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete assignment", description = "Soft delete an assignment")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteAssignment(
            @Parameter(description = "Assignment ID") @PathVariable Long id) {
        log.info("Deleting assignment: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        assignmentService.deleteAssignment(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore assignment", description = "Restore a soft-deleted assignment")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> restoreAssignment(
            @Parameter(description = "Assignment ID") @PathVariable Long id) {
        log.info("Restoring assignment: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        assignmentService.restoreAssignment(id, ownerId);
        return ResponseEntity.ok().build();
    }
}
