package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.CourseRequest;
import com.vijay.User_Master.dto.CourseResponse;
import com.vijay.User_Master.dto.CourseStatistics;
import com.vijay.User_Master.entity.Course;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.CourseService;
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

import java.util.List;

/**
 * REST Controller for Course management
 */
@RestController
@RequestMapping("/api/v1/courses")
@AllArgsConstructor
@Slf4j
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {

    private final CourseService courseService;
    private final WorkerRepository workerRepository;

    @PostMapping
    @Operation(summary = "Create a new course", description = "Create a new course for a class and subject")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest request) {
        log.info("Creating course: {}", request.getCourseCode());
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.createCourse(request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course", description = "Update an existing course")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<CourseResponse> updateCourse(
            @Parameter(description = "Course ID") @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        log.info("Updating course: {}", id);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.updateCourse(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieve a specific course by its ID")
    public ResponseEntity<CourseResponse> getCourseById(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        log.info("Getting course: {}", id);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.getCourseById(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{courseCode}")
    @Operation(summary = "Get course by course code", description = "Retrieve a course by its unique course code")
    public ResponseEntity<CourseResponse> getCourseByCourseCode(
            @Parameter(description = "Course code (e.g., CS101)") @PathVariable String courseCode) {
        log.info("Getting course by code: {}", courseCode);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.getCourseByCourseCode(courseCode, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieve all courses with pagination and sorting")
    public ResponseEntity<Page<CourseResponse>> getAllCourses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "courseName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all courses - page: {}, size: {}", page, size);
        Long ownerId = getCorrectOwnerId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CourseResponse> response = courseService.getAllCourses(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get courses by class", description = "Retrieve all courses for a specific class")
    public ResponseEntity<List<CourseResponse>> getCoursesByClass(
            @Parameter(description = "Class ID") @PathVariable Long classId) {
        log.info("Getting courses for class: {}", classId);
        Long ownerId = getCorrectOwnerId();
        List<CourseResponse> response = courseService.getCoursesByClass(classId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get courses by teacher", description = "Retrieve all courses assigned to a specific teacher")
    public ResponseEntity<List<CourseResponse>> getCoursesByTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        log.info("Getting courses for teacher: {}", teacherId);
        Long ownerId = getCorrectOwnerId();
        List<CourseResponse> response = courseService.getCoursesByTeacher(teacherId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get courses by subject", description = "Retrieve all courses for a specific subject")
    public ResponseEntity<List<CourseResponse>> getCoursesBySubject(
            @Parameter(description = "Subject ID") @PathVariable Long subjectId) {
        log.info("Getting courses for subject: {}", subjectId);
        Long ownerId = getCorrectOwnerId();
        List<CourseResponse> response = courseService.getCoursesBySubject(subjectId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get courses by status", description = "Retrieve all courses with a specific status")
    public ResponseEntity<Page<CourseResponse>> getCoursesByStatus(
            @Parameter(description = "Course status (PLANNED, ONGOING, COMPLETED, CANCELLED)") @PathVariable Course.CourseStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Getting courses with status: {}", status);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());
        Page<CourseResponse> response = courseService.getCoursesByStatus(status, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/semester/{semester}")
    @Operation(summary = "Get courses by semester", description = "Retrieve all courses for a specific semester")
    public ResponseEntity<List<CourseResponse>> getCoursesBySemester(
            @Parameter(description = "Semester name (e.g., Fall 2024)") @PathVariable String semester) {
        log.info("Getting courses for semester: {}", semester);
        Long ownerId = getCorrectOwnerId();
        List<CourseResponse> response = courseService.getCoursesBySemester(semester, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ongoing")
    @Operation(summary = "Get ongoing courses", description = "Retrieve all courses that are currently ongoing")
    public ResponseEntity<List<CourseResponse>> getOngoingCourses() {
        log.info("Getting ongoing courses");
        Long ownerId = getCorrectOwnerId();
        List<CourseResponse> response = courseService.getOngoingCourses(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses", description = "Search courses by keyword in course name or course code")
    public ResponseEntity<Page<CourseResponse>> searchCourses(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Searching courses with keyword: {}", keyword);
        Long ownerId = getCorrectOwnerId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("courseName").ascending());
        Page<CourseResponse> response = courseService.searchCourses(keyword, ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    @Operation(summary = "Enroll student in course", description = "Enroll a student in a specific course")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<CourseResponse> enrollStudent(
            @Parameter(description = "Course ID") @PathVariable Long courseId,
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        log.info("Enrolling student {} in course {}", studentId, courseId);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.enrollStudent(courseId, studentId, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/unenroll/{studentId}")
    @Operation(summary = "Unenroll student from course", description = "Remove a student from a specific course")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<CourseResponse> unenrollStudent(
            @Parameter(description = "Course ID") @PathVariable Long courseId,
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        log.info("Unenrolling student {} from course {}", studentId, courseId);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.unenrollStudent(courseId, studentId, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}/students")
    @Operation(summary = "Get enrolled students", description = "Get list of students enrolled in a course")
    public ResponseEntity<List<Long>> getEnrolledStudents(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.info("Getting enrolled students for course: {}", courseId);
        Long ownerId = getCorrectOwnerId();
        List<Long> response = courseService.getEnrolledStudents(courseId, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/assign-teacher/{teacherId}")
    @Operation(summary = "Assign teacher to course", description = "Assign a teacher to a specific course")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseResponse> assignTeacher(
            @Parameter(description = "Course ID") @PathVariable Long courseId,
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        log.info("Assigning teacher {} to course {}", teacherId, courseId);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.assignTeacher(courseId, teacherId, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/remove-teacher")
    @Operation(summary = "Remove teacher from course", description = "Remove teacher assignment from a course")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseResponse> removeTeacher(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.info("Removing teacher from course {}", courseId);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.removeTeacher(courseId, ownerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{courseId}/status/{newStatus}")
    @Operation(summary = "Change course status", description = "Update the status of a course")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<CourseResponse> changeCourseStatus(
            @Parameter(description = "Course ID") @PathVariable Long courseId,
            @Parameter(description = "New status") @PathVariable Course.CourseStatus newStatus) {
        log.info("Changing course {} status to {}", courseId, newStatus);
        Long ownerId = getCorrectOwnerId();
        CourseResponse response = courseService.changeCourseStatus(courseId, newStatus, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get course statistics", description = "Get comprehensive statistics about courses")
    public ResponseEntity<CourseStatistics> getCourseStatistics() {
        log.info("Getting course statistics");
        Long ownerId = getCorrectOwnerId();
        CourseStatistics response = courseService.getCourseStatistics(ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-code/{courseCode}")
    @Operation(summary = "Check if course code exists", description = "Check if a course code is already in use")
    public ResponseEntity<Boolean> isCourseCodeExists(
            @Parameter(description = "Course code to check") @PathVariable String courseCode) {
        log.info("Checking if course code exists: {}", courseCode);
        Long ownerId = getCorrectOwnerId();
        boolean exists = courseService.isCourseCodeExists(courseCode, ownerId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{courseId}/is-full")
    @Operation(summary = "Check if course is full", description = "Check if a course has reached maximum enrollment")
    public ResponseEntity<Boolean> isCourseFull(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.info("Checking if course is full: {}", courseId);
        Long ownerId = getCorrectOwnerId();
        boolean isFull = courseService.isCourseFull(courseId, ownerId);
        return ResponseEntity.ok(isFull);
    }

    @GetMapping("/{courseId}/available-seats")
    @Operation(summary = "Get available seats", description = "Get the number of available seats in a course")
    public ResponseEntity<Integer> getAvailableSeats(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.info("Getting available seats for course: {}", courseId);
        Long ownerId = getCorrectOwnerId();
        Integer availableSeats = courseService.getAvailableSeats(courseId, ownerId);
        return ResponseEntity.ok(availableSeats);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course", description = "Soft delete a course")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        log.info("Deleting course: {}", id);
        Long ownerId = getCorrectOwnerId();
        courseService.deleteCourse(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore course", description = "Restore a soft-deleted course")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> restoreCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        log.info("Restoring course: {}", id);
        Long ownerId = getCorrectOwnerId();
        courseService.restoreCourse(id, ownerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get the correct owner ID for the logged-in user.
     * If the logged-in user is a worker (like a teacher), return their owner's ID.
     * If the logged-in user is a direct owner, return their own ID.
     */
    private Long getCorrectOwnerId() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long userId = loggedInUser.getId();
        
        // Check if the logged-in user is a worker
        Worker worker = workerRepository.findById(userId).orElse(null);
        if (worker != null && worker.getOwner() != null) {
            // User is a worker, return their owner's ID
            Long ownerId = worker.getOwner().getId();
            log.info("Logged-in user is worker ID: {}, returning owner ID: {}", userId, ownerId);
            return ownerId;
        }
        
        // User is a direct owner, return their own ID
        log.info("Logged-in user is direct owner ID: {}", userId);
        return userId;
    }
}
