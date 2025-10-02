package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.TimetableRequest;
import com.vijay.User_Master.dto.TimetableResponse;
import com.vijay.User_Master.dto.TimetableStatistics;
import com.vijay.User_Master.entity.Timetable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;

/**
 * Service interface for Timetable management
 */
public interface TimetableService {

    /**
     * Create a new timetable entry
     */
    TimetableResponse createTimetable(TimetableRequest request, Long ownerId);

    /**
     * Update an existing timetable entry
     */
    TimetableResponse updateTimetable(Long id, TimetableRequest request, Long ownerId);

    /**
     * Get timetable by ID
     */
    TimetableResponse getTimetableById(Long id, Long ownerId);

    /**
     * Get all timetable entries for owner with pagination
     */
    Page<TimetableResponse> getAllTimetables(Long ownerId, Pageable pageable);

    /**
     * Get timetable by class
     */
    List<TimetableResponse> getTimetablesByClass(Long classId, Long ownerId);

    /**
     * Get timetable by class and day
     */
    List<TimetableResponse> getTimetablesByClassAndDay(Long classId, Timetable.DayOfWeek dayOfWeek, Long ownerId);

    /**
     * Get timetable by teacher
     */
    List<TimetableResponse> getTimetablesByTeacher(Long teacherId, Long ownerId);

    /**
     * Get timetable by teacher and day
     */
    List<TimetableResponse> getTimetablesByTeacherAndDay(Long teacherId, Timetable.DayOfWeek dayOfWeek, Long ownerId);

    /**
     * Get timetable by subject
     */
    List<TimetableResponse> getTimetablesBySubject(Long subjectId, Long ownerId);

    /**
     * Get timetable by day of week
     */
    List<TimetableResponse> getTimetablesByDay(Timetable.DayOfWeek dayOfWeek, Long ownerId);

    /**
     * Get timetable by status
     */
    Page<TimetableResponse> getTimetablesByStatus(Timetable.TimetableStatus status, Long ownerId, Pageable pageable);

    /**
     * Get timetable by academic year and semester
     */
    List<TimetableResponse> getTimetablesByAcademicYearAndSemester(String academicYear, String semester, Long ownerId);

    /**
     * Check for teacher time conflicts
     */
    List<TimetableResponse> checkTeacherTimeConflicts(Long teacherId, Timetable.DayOfWeek dayOfWeek, 
                                                     LocalTime startTime, LocalTime endTime, Long ownerId);

    /**
     * Get timetable by room number
     */
    List<TimetableResponse> getTimetablesByRoom(String roomNumber, Long ownerId);

    /**
     * Search timetable entries by keyword
     */
    Page<TimetableResponse> searchTimetables(String keyword, Long ownerId, Pageable pageable);

    /**
     * Delete timetable entry (soft delete)
     */
    void deleteTimetable(Long id, Long ownerId);

    /**
     * Restore deleted timetable entry
     */
    void restoreTimetable(Long id, Long ownerId);

    /**
     * Get timetable statistics
     */
    TimetableStatistics getTimetableStatistics(Long ownerId);

    /**
     * Get weekly timetable for a class
     */
    List<TimetableResponse> getWeeklyTimetableForClass(Long classId, Long ownerId);

    /**
     * Get weekly timetable for a teacher
     */
    List<TimetableResponse> getWeeklyTimetableForTeacher(Long teacherId, Long ownerId);
}
