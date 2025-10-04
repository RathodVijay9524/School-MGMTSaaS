package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.ExamRequest;
import com.vijay.User_Master.dto.ExamResponse;
import com.vijay.User_Master.dto.ExamStatistics;
import com.vijay.User_Master.entity.Exam;
import com.vijay.User_Master.service.ExamService;
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

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Exam management
 */
@RestController
@RequestMapping("/api/v1/exams")
@AllArgsConstructor
@Slf4j
@Tag(name = "Exam Management", description = "APIs for managing examinations and assessments")
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @Operation(summary = "Create a new exam", description = "Create a new examination with scheduling and details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> createExam(@Valid @RequestBody ExamRequest request) {
        log.info("Creating exam: {} for subject: {}", request.getExamName(), request.getSubjectId());
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        ExamResponse response = examService.createExam(request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an exam", description = "Update an existing examination")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> updateExam(
            @Parameter(description = "Exam ID") @PathVariable Long id,
            @Valid @RequestBody ExamRequest request) {
        log.info("Updating exam: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        ExamResponse response = examService.updateExam(id, request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exam by ID", description = "Retrieve a specific examination by its ID")
    public ResponseEntity<?> getExamById(
            @Parameter(description = "Exam ID") @PathVariable Long id) {
        log.info("Getting exam: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        ExamResponse response = examService.getExamById(id, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all exams", description = "Retrieve all examinations with pagination and sorting")
    public ResponseEntity<?> getAllExams(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "examDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all exams - page: {}, size: {}", page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ExamResponse> response = examService.getAllExams(ownerId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get exams by class", description = "Retrieve all examinations for a specific class")
    public ResponseEntity<?> getExamsByClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting exams for class: {}", classId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("examDate").ascending());
        Page<ExamResponse> response = examService.getExamsByClass(classId, ownerId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get exams by subject", description = "Retrieve all examinations for a specific subject")
    public ResponseEntity<?> getExamsBySubject(
            @Parameter(description = "Subject ID") @PathVariable Long subjectId) {
        log.info("Getting exams for subject: {}", subjectId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamsBySubject(subjectId, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/type/{examType}")
    @Operation(summary = "Get exams by type", description = "Retrieve all examinations of a specific type")
    public ResponseEntity<?> getExamsByType(
            @Parameter(description = "Exam type") @PathVariable Exam.ExamType examType) {
        log.info("Getting exams for type: {}", examType);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamsByType(examType, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get exams by status", description = "Retrieve all examinations with a specific status")
    public ResponseEntity<?> getExamsByStatus(
            @Parameter(description = "Exam status") @PathVariable Exam.ExamStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting exams for status: {}", status);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("examDate").ascending());
        Page<ExamResponse> response = examService.getExamsByStatus(status, ownerId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get exams by date range", description = "Retrieve examinations within a specific date range")
    public ResponseEntity<?> getExamsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam LocalDate endDate) {
        log.info("Getting exams for date range: {} to {}", startDate, endDate);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamsByDateRange(startDate, endDate, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming exams", description = "Retrieve all upcoming scheduled examinations")
    public ResponseEntity<?> getUpcomingExams() {
        log.info("Getting upcoming exams");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getUpcomingExams(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue exams", description = "Retrieve all overdue scheduled examinations")
    public ResponseEntity<?> getOverdueExams() {
        log.info("Getting overdue exams");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getOverdueExams(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/semester/{semester}")
    @Operation(summary = "Get exams by semester", description = "Retrieve all examinations for a specific semester")
    public ResponseEntity<?> getExamsBySemester(
            @Parameter(description = "Semester") @PathVariable String semester) {
        log.info("Getting exams for semester: {}", semester);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamsBySemester(semester, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/academic-year/{academicYear}")
    @Operation(summary = "Get exams by academic year", description = "Retrieve all examinations for a specific academic year")
    public ResponseEntity<?> getExamsByAcademicYear(
            @Parameter(description = "Academic year") @PathVariable String academicYear) {
        log.info("Getting exams for academic year: {}", academicYear);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamsByAcademicYear(academicYear, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/supervisor/{supervisorId}")
    @Operation(summary = "Get exams by supervisor", description = "Retrieve all examinations supervised by a specific teacher")
    public ResponseEntity<?> getExamsBySupervisor(
            @Parameter(description = "Supervisor ID") @PathVariable Long supervisorId) {
        log.info("Getting exams for supervisor: {}", supervisorId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamsBySupervisor(supervisorId, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/date/{examDate}")
    @Operation(summary = "Get exams by date", description = "Retrieve all examinations on a specific date")
    public ResponseEntity<?> getExamsByDate(
            @Parameter(description = "Exam date (yyyy-MM-dd)") @PathVariable LocalDate examDate) {
        log.info("Getting exams for date: {}", examDate);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamsByDate(examDate, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Search exams", description = "Search examinations by keyword in name, code, subject, or class")
    public ResponseEntity<?> searchExams(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching exams with keyword: {}", keyword);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("examDate").ascending());
        Page<ExamResponse> response = examService.searchExams(keyword, ownerId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/calendar/{month}")
    @Operation(summary = "Get exam calendar", description = "Retrieve examination calendar for a specific month")
    public ResponseEntity<?> getExamCalendar(
            @Parameter(description = "Month (yyyy-MM-dd)") @PathVariable LocalDate month) {
        log.info("Getting exam calendar for month: {}", month);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<ExamResponse> response = examService.getExamCalendar(month, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get exam statistics", description = "Get comprehensive statistics about examinations")
    public ResponseEntity<?> getExamStatistics() {
        log.info("Getting exam statistics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        ExamStatistics response = examService.getExamStatistics(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/publish-results")
    @Operation(summary = "Publish exam results", description = "Publish results for a completed examination")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> publishExamResults(
            @Parameter(description = "Exam ID") @PathVariable Long id) {
        log.info("Publishing results for exam: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        ExamResponse response = examService.publishExamResults(id, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel exam", description = "Cancel a scheduled examination")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> cancelExam(
            @Parameter(description = "Exam ID") @PathVariable Long id,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        log.info("Cancelling exam: {} with reason: {}", id, reason);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        ExamResponse response = examService.cancelExam(id, reason, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule exam", description = "Reschedule a scheduled examination to a new date")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> rescheduleExam(
            @Parameter(description = "Exam ID") @PathVariable Long id,
            @Parameter(description = "New exam date (yyyy-MM-dd)") @RequestParam LocalDate newDate) {
        log.info("Rescheduling exam: {} to date: {}", id, newDate);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        ExamResponse response = examService.rescheduleExam(id, newDate, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exam", description = "Soft delete an examination")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> deleteExam(
            @Parameter(description = "Exam ID") @PathVariable Long id) {
        log.info("Deleting exam: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        examService.deleteExam(id, ownerId);
        return ExceptionUtil.createBuildResponse("Exam deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore exam", description = "Restore a soft-deleted examination")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> restoreExam(
            @Parameter(description = "Exam ID") @PathVariable Long id) {
        log.info("Restoring exam: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        examService.restoreExam(id, ownerId);
        return ExceptionUtil.createBuildResponse("Exam restored successfully", HttpStatus.OK);
    }
}
