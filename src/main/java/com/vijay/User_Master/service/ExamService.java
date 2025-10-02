package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.ExamRequest;
import com.vijay.User_Master.dto.ExamResponse;
import com.vijay.User_Master.dto.ExamStatistics;
import com.vijay.User_Master.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Exam management
 */
public interface ExamService {

    /**
     * Create a new exam
     */
    ExamResponse createExam(ExamRequest request, Long ownerId);

    /**
     * Update an existing exam
     */
    ExamResponse updateExam(Long id, ExamRequest request, Long ownerId);

    /**
     * Get exam by ID
     */
    ExamResponse getExamById(Long id, Long ownerId);

    /**
     * Get all exams for owner with pagination
     */
    Page<ExamResponse> getAllExams(Long ownerId, Pageable pageable);

    /**
     * Get exams by class
     */
    Page<ExamResponse> getExamsByClass(Long classId, Long ownerId, Pageable pageable);

    /**
     * Get exams by subject
     */
    List<ExamResponse> getExamsBySubject(Long subjectId, Long ownerId);

    /**
     * Get exams by exam type
     */
    List<ExamResponse> getExamsByType(Exam.ExamType examType, Long ownerId);

    /**
     * Get exams by status
     */
    Page<ExamResponse> getExamsByStatus(Exam.ExamStatus status, Long ownerId, Pageable pageable);

    /**
     * Get exams by date range
     */
    List<ExamResponse> getExamsByDateRange(LocalDate startDate, LocalDate endDate, Long ownerId);

    /**
     * Get upcoming exams
     */
    List<ExamResponse> getUpcomingExams(Long ownerId);

    /**
     * Get overdue exams
     */
    List<ExamResponse> getOverdueExams(Long ownerId);

    /**
     * Get exams by semester
     */
    List<ExamResponse> getExamsBySemester(String semester, Long ownerId);

    /**
     * Get exams by academic year
     */
    List<ExamResponse> getExamsByAcademicYear(String academicYear, Long ownerId);

    /**
     * Get exams by supervisor (teacher)
     */
    List<ExamResponse> getExamsBySupervisor(Long supervisorId, Long ownerId);

    /**
     * Get exams by specific date
     */
    List<ExamResponse> getExamsByDate(LocalDate examDate, Long ownerId);

    /**
     * Search exams by keyword
     */
    Page<ExamResponse> searchExams(String keyword, Long ownerId, Pageable pageable);

    /**
     * Delete exam (soft delete)
     */
    void deleteExam(Long id, Long ownerId);

    /**
     * Restore deleted exam
     */
    void restoreExam(Long id, Long ownerId);

    /**
     * Publish exam results
     */
    ExamResponse publishExamResults(Long id, Long ownerId);

    /**
     * Cancel exam
     */
    ExamResponse cancelExam(Long id, String reason, Long ownerId);

    /**
     * Reschedule exam
     */
    ExamResponse rescheduleExam(Long id, LocalDate newDate, Long ownerId);

    /**
     * Get exam statistics
     */
    ExamStatistics getExamStatistics(Long ownerId);

    /**
     * Get exam calendar for a specific month
     */
    List<ExamResponse> getExamCalendar(LocalDate month, Long ownerId);
}
