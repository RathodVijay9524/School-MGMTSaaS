package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.TimetableRequest;
import com.vijay.User_Master.dto.TimetableResponse;
import com.vijay.User_Master.dto.TimetableStatistics;
import com.vijay.User_Master.entity.Timetable;
import com.vijay.User_Master.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/**
 * REST Controller for Timetable management
 */
@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Timetable Management", description = "APIs for managing class schedules and timetables")
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @Operation(summary = "Create a new timetable entry", description = "Create a new schedule entry for a class, subject, and teacher")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<TimetableResponse> createTimetable(@Valid @RequestBody TimetableRequest request) {
        log.info("Creating timetable entry for class: {} and teacher: {}", request.getClassId(), request.getTeacherId());
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        TimetableResponse response = timetableService.createTimetable(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a timetable entry", description = "Update an existing timetable entry")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<TimetableResponse> updateTimetable(
            @Parameter(description = "Timetable ID") @PathVariable Long id,
            @Valid @RequestBody TimetableRequest request) {
        log.info("Updating timetable entry: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        TimetableResponse response = timetableService.updateTimetable(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get timetable by ID", description = "Retrieve a specific timetable entry by its ID")
    public ResponseEntity<TimetableResponse> getTimetableById(
            @Parameter(description = "Timetable ID") @PathVariable Long id) {
        log.info("Getting timetable entry: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        TimetableResponse response = timetableService.getTimetableById(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all timetable entries", description = "Retrieve all timetable entries with pagination and sorting")
    public ResponseEntity<Page<TimetableResponse>> getAllTimetables(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "dayOfWeek") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all timetable entries - page: {}, size: {}", page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TimetableResponse> response = timetableService.getAllTimetables(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get timetable by class", description = "Retrieve all timetable entries for a specific class")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByClass(
            @Parameter(description = "Class ID") @PathVariable Long classId) {
        log.info("Getting timetable entries for class: {}", classId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesByClass(classId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}/weekly")
    @Operation(summary = "Get weekly timetable for class", description = "Retrieve complete weekly schedule for a specific class")
    public ResponseEntity<List<TimetableResponse>> getWeeklyTimetableForClass(
            @Parameter(description = "Class ID") @PathVariable Long classId) {
        log.info("Getting weekly timetable for class: {}", classId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getWeeklyTimetableForClass(classId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}/day/{dayOfWeek}")
    @Operation(summary = "Get timetable by class and day", description = "Retrieve timetable entries for a specific class on a specific day")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByClassAndDay(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Day of week") @PathVariable Timetable.DayOfWeek dayOfWeek) {
        log.info("Getting timetable entries for class: {} on day: {}", classId, dayOfWeek);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesByClassAndDay(classId, dayOfWeek, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get timetable by teacher", description = "Retrieve all timetable entries for a specific teacher")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        log.info("Getting timetable entries for teacher: {}", teacherId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesByTeacher(teacherId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}/weekly")
    @Operation(summary = "Get weekly timetable for teacher", description = "Retrieve complete weekly schedule for a specific teacher")
    public ResponseEntity<List<TimetableResponse>> getWeeklyTimetableForTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        log.info("Getting weekly timetable for teacher: {}", teacherId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getWeeklyTimetableForTeacher(teacherId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}/day/{dayOfWeek}")
    @Operation(summary = "Get timetable by teacher and day", description = "Retrieve timetable entries for a specific teacher on a specific day")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByTeacherAndDay(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @Parameter(description = "Day of week") @PathVariable Timetable.DayOfWeek dayOfWeek) {
        log.info("Getting timetable entries for teacher: {} on day: {}", teacherId, dayOfWeek);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesByTeacherAndDay(teacherId, dayOfWeek, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get timetable by subject", description = "Retrieve all timetable entries for a specific subject")
    public ResponseEntity<List<TimetableResponse>> getTimetablesBySubject(
            @Parameter(description = "Subject ID") @PathVariable Long subjectId) {
        log.info("Getting timetable entries for subject: {}", subjectId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesBySubject(subjectId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/day/{dayOfWeek}")
    @Operation(summary = "Get timetable by day", description = "Retrieve all timetable entries for a specific day of the week")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByDay(
            @Parameter(description = "Day of week") @PathVariable Timetable.DayOfWeek dayOfWeek) {
        log.info("Getting timetable entries for day: {}", dayOfWeek);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesByDay(dayOfWeek, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get timetable by status", description = "Retrieve all timetable entries with a specific status")
    public ResponseEntity<Page<TimetableResponse>> getTimetablesByStatus(
            @Parameter(description = "Timetable status") @PathVariable Timetable.TimetableStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting timetable entries with status: {}", status);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dayOfWeek", "startTime").ascending());
        Page<TimetableResponse> response = timetableService.getTimetablesByStatus(status, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/academic-year/{academicYear}/semester/{semester}")
    @Operation(summary = "Get timetable by academic year and semester", description = "Retrieve timetable entries for a specific academic year and semester")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByAcademicYearAndSemester(
            @Parameter(description = "Academic year") @PathVariable String academicYear,
            @Parameter(description = "Semester") @PathVariable String semester) {
        log.info("Getting timetable entries for academic year: {} and semester: {}", academicYear, semester);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesByAcademicYearAndSemester(academicYear, semester, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conflicts/teacher/{teacherId}")
    @Operation(summary = "Check teacher time conflicts", description = "Check for time conflicts for a teacher on a specific day and time")
    public ResponseEntity<List<TimetableResponse>> checkTeacherTimeConflicts(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @Parameter(description = "Day of week") @RequestParam Timetable.DayOfWeek dayOfWeek,
            @Parameter(description = "Start time (HH:mm)") @RequestParam LocalTime startTime,
            @Parameter(description = "End time (HH:mm)") @RequestParam LocalTime endTime) {
        log.info("Checking time conflicts for teacher: {} on {} at {}-{}", teacherId, dayOfWeek, startTime, endTime);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.checkTeacherTimeConflicts(teacherId, dayOfWeek, startTime, endTime, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomNumber}")
    @Operation(summary = "Get timetable by room", description = "Retrieve all timetable entries for a specific room")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByRoom(
            @Parameter(description = "Room number") @PathVariable String roomNumber) {
        log.info("Getting timetable entries for room: {}", roomNumber);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<TimetableResponse> response = timetableService.getTimetablesByRoom(roomNumber, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search timetable entries", description = "Search timetable entries by keyword in class, subject, teacher, or room")
    public ResponseEntity<Page<TimetableResponse>> searchTimetables(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching timetable entries with keyword: {}", keyword);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dayOfWeek", "startTime").ascending());
        Page<TimetableResponse> response = timetableService.searchTimetables(keyword, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get timetable statistics", description = "Get comprehensive statistics about timetable entries")
    public ResponseEntity<TimetableStatistics> getTimetableStatistics() {
        log.info("Getting timetable statistics");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        TimetableStatistics response = timetableService.getTimetableStatistics(ownerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete timetable entry", description = "Soft delete a timetable entry")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteTimetable(
            @Parameter(description = "Timetable ID") @PathVariable Long id) {
        log.info("Deleting timetable entry: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        timetableService.deleteTimetable(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore timetable entry", description = "Restore a soft-deleted timetable entry")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> restoreTimetable(
            @Parameter(description = "Timetable ID") @PathVariable Long id) {
        log.info("Restoring timetable entry: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        timetableService.restoreTimetable(id, ownerId);
        return ResponseEntity.ok().build();
    }
}
