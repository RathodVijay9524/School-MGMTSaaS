package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AttendanceStatistics;
import com.vijay.User_Master.dto.AttendanceRequest;
import com.vijay.User_Master.dto.AttendanceResponse;
import com.vijay.User_Master.dto.PageableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Page<AttendanceResponse> getAllAttendance(Pageable pageable);
    AttendanceResponse markAttendance(AttendanceRequest request);
    List<AttendanceResponse> markBulkAttendance(List<AttendanceRequest> requests);
    AttendanceResponse updateAttendance(Long id, AttendanceRequest request);
    AttendanceResponse getAttendanceById(Long id);
    Page<AttendanceResponse> getAttendanceByStudent(Long studentId, Pageable pageable);
    List<AttendanceResponse> getAttendanceByStudentAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate);
    List<AttendanceResponse> getAttendanceByClassAndDate(Long classId, LocalDate date);
    List<AttendanceResponse> getAttendanceByClassAndDateRange(Long classId, LocalDate startDate, LocalDate endDate);
    List<AttendanceResponse> getAbsentStudents(Long classId, LocalDate date);
    Double calculateAttendancePercentage(Long studentId);
    Double calculateAttendancePercentageInRange(Long studentId, LocalDate startDate, LocalDate endDate);
    AttendanceStatistics getClassAttendanceStatistics(Long classId, LocalDate date);
    boolean isAttendanceMarked(Long studentId, LocalDate date, String session);
    void deleteAttendance(Long id);
    List<AttendanceStatistics> getMonthlyAttendanceStatistics(Long classId, int year, int month);
}