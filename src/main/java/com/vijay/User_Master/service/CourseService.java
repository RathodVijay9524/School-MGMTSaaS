package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.CourseRequest;
import com.vijay.User_Master.dto.CourseResponse;
import com.vijay.User_Master.dto.CourseStatistics;
import com.vijay.User_Master.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Course management
 */
public interface CourseService {

    /**
     * Create a new course
     */
    CourseResponse createCourse(CourseRequest request, Long ownerId);

    /**
     * Update an existing course
     */
    CourseResponse updateCourse(Long id, CourseRequest request, Long ownerId);

    /**
     * Get course by ID
     */
    CourseResponse getCourseById(Long id, Long ownerId);

    /**
     * Get course by course code
     */
    CourseResponse getCourseByCourseCode(String courseCode, Long ownerId);

    /**
     * Get all courses for owner with pagination
     */
    Page<CourseResponse> getAllCourses(Long ownerId, Pageable pageable);

    /**
     * Get courses by class
     */
    List<CourseResponse> getCoursesByClass(Long classId, Long ownerId);

    /**
     * Get courses by teacher
     */
    List<CourseResponse> getCoursesByTeacher(Long teacherId, Long ownerId);

    /**
     * Get courses by subject
     */
    List<CourseResponse> getCoursesBySubject(Long subjectId, Long ownerId);

    /**
     * Get courses by status
     */
    Page<CourseResponse> getCoursesByStatus(Course.CourseStatus status, Long ownerId, Pageable pageable);

    /**
     * Get courses by semester
     */
    List<CourseResponse> getCoursesBySemester(String semester, Long ownerId);

    /**
     * Get ongoing courses
     */
    List<CourseResponse> getOngoingCourses(Long ownerId);

    /**
     * Search courses by keyword
     */
    Page<CourseResponse> searchCourses(String keyword, Long ownerId, Pageable pageable);

    /**
     * Enroll student in course
     */
    CourseResponse enrollStudent(Long courseId, Long studentId, Long ownerId);

    /**
     * Unenroll student from course
     */
    CourseResponse unenrollStudent(Long courseId, Long studentId, Long ownerId);

    /**
     * Get enrolled students for a course
     */
    List<Long> getEnrolledStudents(Long courseId, Long ownerId);

    /**
     * Assign teacher to course
     */
    CourseResponse assignTeacher(Long courseId, Long teacherId, Long ownerId);

    /**
     * Remove teacher from course
     */
    CourseResponse removeTeacher(Long courseId, Long ownerId);

    /**
     * Change course status
     */
    CourseResponse changeCourseStatus(Long courseId, Course.CourseStatus newStatus, Long ownerId);

    /**
     * Delete course (soft delete)
     */
    void deleteCourse(Long id, Long ownerId);

    /**
     * Restore deleted course
     */
    void restoreCourse(Long id, Long ownerId);

    /**
     * Get course statistics
     */
    CourseStatistics getCourseStatistics(Long ownerId);

    /**
     * Check if course code exists
     */
    boolean isCourseCodeExists(String courseCode, Long ownerId);

    /**
     * Check if course is full
     */
    boolean isCourseFull(Long courseId, Long ownerId);

    /**
     * Get available seats in course
     */
    Integer getAvailableSeats(Long courseId, Long ownerId);
}
