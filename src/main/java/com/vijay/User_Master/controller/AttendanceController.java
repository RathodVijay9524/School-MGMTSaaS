package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.AttendanceRequest;
import com.vijay.User_Master.dto.AttendanceResponse;
import com.vijay.User_Master.dto.AttendanceStatistics;
import com.vijay.User_Master.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Attendance Management REST Controller
 * Handles student attendance tracking and reporting
 */
@RestController
@RequestMapping("/api/v1/attendance")
@AllArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Mark attendance for a single student
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> markAttendance(@Valid @RequestBody AttendanceRequest request) {
        log.info("Marking attendance for student ID: {}", request.getStudentId());
        AttendanceResponse response = attendanceService.markAttendance(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    /**
     * Mark attendance for multiple students (bulk operation)
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> markBulkAttendance(@Valid @RequestBody List<AttendanceRequest> requests) {
        log.info("Marking bulk attendance for {} students", requests.size());
        List<AttendanceResponse> responses = attendanceService.markBulkAttendance(requests);
        return ExceptionUtil.createBuildResponse(responses, HttpStatus.CREATED);
    }

    /**
     * Update attendance record
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request) {
        log.info("Updating attendance with ID: {}", id);
        AttendanceResponse response = attendanceService.updateAttendance(id, request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get attendance by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getAttendanceById(@PathVariable Long id) {
        log.info("Fetching attendance with ID: {}", id);
        AttendanceResponse response = attendanceService.getAttendanceById(id);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get attendance for a student
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getAttendanceByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        log.info("Fetching attendance for student ID: {}", studentId);
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> response = attendanceService.getAttendanceByStudent(studentId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get attendance for a student by date range
     */
    @GetMapping("/student/{studentId}/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getAttendanceByStudentAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching attendance for student ID: {} between {} and {}", 
            studentId, startDate, endDate);
        List<AttendanceResponse> responses = attendanceService.getAttendanceByStudentAndDateRange(
            studentId, startDate, endDate);
        return ExceptionUtil.createBuildResponse(responses, HttpStatus.OK);
    }

    /**
     * Get attendance for a class on a specific date
     */
    @GetMapping("/class/{classId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getAttendanceByClassAndDate(
            @PathVariable Long classId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Fetching attendance for class ID: {} on date: {}", classId, date);
        List<AttendanceResponse> responses = attendanceService.getAttendanceByClassAndDate(classId, date);
        return ExceptionUtil.createBuildResponse(responses, HttpStatus.OK);
    }

    /**
     * Get attendance for a class by date range
     */
    @GetMapping("/class/{classId}/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getAttendanceByClassAndDateRange(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching attendance for class ID: {} between {} and {}", 
            classId, startDate, endDate);
        List<AttendanceResponse> responses = attendanceService.getAttendanceByClassAndDateRange(
            classId, startDate, endDate);
        return ExceptionUtil.createBuildResponse(responses, HttpStatus.OK);
    }

    /**
     * Get absent students for a class on a specific date
     */
    @GetMapping("/class/{classId}/absent/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getAbsentStudents(
            @PathVariable Long classId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Fetching absent students for class ID: {} on date: {}", classId, date);
        List<AttendanceResponse> responses = attendanceService.getAbsentStudents(classId, date);
        return ExceptionUtil.createBuildResponse(responses, HttpStatus.OK);
    }

    /**
     * Get attendance percentage for a student
     */
    @GetMapping("/student/{studentId}/percentage")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getAttendancePercentage(@PathVariable Long studentId) {
        log.info("Calculating attendance percentage for student ID: {}", studentId);
        Double percentage = attendanceService.calculateAttendancePercentage(studentId);
        return ExceptionUtil.createBuildResponse(
            new PercentageResponse(studentId, percentage), HttpStatus.OK);
    }

    /**
     * Get attendance percentage for a student in date range
     */
    @GetMapping("/student/{studentId}/percentage/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getAttendancePercentageInRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Calculating attendance percentage for student ID: {} between {} and {}", 
            studentId, startDate, endDate);
        Double percentage = attendanceService.calculateAttendancePercentageInRange(
            studentId, startDate, endDate);
        return ExceptionUtil.createBuildResponse(
            new PercentageRangeResponse(studentId, startDate, endDate, percentage), HttpStatus.OK);
    }

    /**
     * Get attendance statistics for a class
     */
    @GetMapping("/class/{classId}/statistics/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getClassAttendanceStatistics(
            @PathVariable Long classId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Fetching attendance statistics for class ID: {} on date: {}", classId, date);
        AttendanceStatistics stats = attendanceService.getClassAttendanceStatistics(classId, date);
        return ExceptionUtil.createBuildResponse(stats, HttpStatus.OK);
    }

    /**
     * Check if attendance is already marked
     */
    @GetMapping("/check")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> checkAttendanceMarked(
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String session) {
        log.info("Checking if attendance marked for student ID: {} on date: {}", studentId, date);
        boolean marked = attendanceService.isAttendanceMarked(studentId, date, session);
        return ExceptionUtil.createBuildResponse(
            new CheckResponse(studentId, date, marked), HttpStatus.OK);
    }

    /**
     * Delete attendance record
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id) {
        log.info("Deleting attendance with ID: {}", id);
        attendanceService.deleteAttendance(id);
        return ExceptionUtil.createBuildResponseMessage(
            "Attendance deleted successfully", HttpStatus.OK);
    }

    // Helper response classes
    private record PercentageResponse(Long studentId, Double percentage) {}
    private record PercentageRangeResponse(Long studentId, LocalDate startDate, LocalDate endDate, Double percentage) {}
    private record CheckResponse(Long studentId, LocalDate date, boolean marked) {}
}

