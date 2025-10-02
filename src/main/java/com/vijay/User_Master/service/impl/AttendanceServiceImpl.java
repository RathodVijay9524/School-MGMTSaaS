package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.AttendanceRequest;
import com.vijay.User_Master.dto.AttendanceResponse;
import com.vijay.User_Master.dto.AttendanceStatistics;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.AttendanceRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.AttendanceService;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final WorkerRepository workerRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAllAttendance(Pageable pageable) {
        log.info("Fetching all attendance records with pagination");
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        // Use owner-based query if available, otherwise fallback to all
        Page<Attendance> attendancePage = attendanceRepository.findByOwner_Id(ownerId, pageable);
        
        return attendancePage.map(this::mapToResponse);
    }

    @Override
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        log.info("Marking attendance for student ID: {} on date: {}", 
            request.getStudentId(), request.getAttendanceDate());
        
        // Check if attendance already marked
        Attendance.AttendanceSession session = request.getSession() != null 
            ? request.getSession() 
            : Attendance.AttendanceSession.FULL_DAY;
            
        boolean exists = attendanceRepository.existsByStudent_IdAndAttendanceDateAndSession(
            request.getStudentId(), request.getAttendanceDate(), session);
            
        if (exists) {
            throw new BadApiRequestException(
                "Attendance already marked for this student on " + request.getAttendanceDate());
        }
        
        // Get entities
        Worker student = workerRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
            
        SchoolClass schoolClass = schoolClassRepository.findById(request.getClassId())
            .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", request.getClassId()));
        
        // Subject is optional for attendance
        Subject subject = null;
        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        }
        
        Worker markedBy = null;
        if (request.getMarkedByTeacherId() != null) {
            markedBy = workerRepository.findById(request.getMarkedByTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getMarkedByTeacherId()));
        }
        
        // Create attendance record
        // Get the current logged-in user as owner
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
            .orElseThrow(() -> new RuntimeException("Owner user not found"));
        
        Attendance attendance = Attendance.builder()
            .student(student)
            .schoolClass(schoolClass)
            .subject(subject)
            .markedBy(markedBy)
            .attendanceDate(request.getAttendanceDate())
            .status(request.getStatus())
            .session(request.getSession() != null ? request.getSession() : Attendance.AttendanceSession.FULL_DAY)
            .remarks(request.getRemarks())
            .owner(owner) // Set the owner for multi-tenancy
            .build();
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        log.info("Attendance marked successfully with ID: {}", savedAttendance.getId());
        
        return mapToResponse(savedAttendance);
    }

    @Override
    public List<AttendanceResponse> markBulkAttendance(List<AttendanceRequest> requests) {
        log.info("Marking bulk attendance for {} students", requests.size());
        
        return requests.stream()
            .map(this::markAttendance)
            .collect(Collectors.toList());
    }

    @Override
    public AttendanceResponse updateAttendance(Long id, AttendanceRequest request) {
        log.info("Updating attendance with ID: {}", id);
        
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));
        
        attendance.setStatus(request.getStatus());
        attendance.setCheckInTime(request.getCheckInTime());
        attendance.setCheckOutTime(request.getCheckOutTime());
        attendance.setRemarks(request.getRemarks());
        attendance.setVerified(request.isVerified());
        attendance.setParentNote(request.getParentNote());
        
        Attendance updated = attendanceRepository.save(attendance);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));
        return mapToResponse(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAttendanceByStudent(Long studentId, Pageable pageable) {
        log.info("Fetching attendance for student ID: {}", studentId);
        return attendanceRepository.findByStudent_Id(studentId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByStudentAndDateRange(
            Long studentId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching attendance for student ID: {} between {} and {}", 
            studentId, startDate, endDate);
        return attendanceRepository.findByStudent_IdAndAttendanceDateBetween(
            studentId, startDate, endDate).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByClassAndDate(Long classId, LocalDate date) {
        log.info("Fetching attendance for class ID: {} on date: {}", classId, date);
        return attendanceRepository.findBySchoolClass_IdAndAttendanceDate(classId, date).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByClassAndDateRange(
            Long classId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching attendance for class ID: {} between {} and {}", 
            classId, startDate, endDate);
        return attendanceRepository.findBySchoolClass_IdAndAttendanceDateBetween(
            classId, startDate, endDate).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAbsentStudents(Long classId, LocalDate date) {
        log.info("Fetching absent students for class ID: {} on date: {}", classId, date);
        return attendanceRepository.findAbsentStudents(classId, date).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAttendancePercentage(Long studentId) {
        log.info("Calculating attendance percentage for student ID: {}", studentId);
        Double percentage = attendanceRepository.calculateAttendancePercentage(studentId);
        return percentage != null ? percentage : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAttendancePercentageInRange(
            Long studentId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating attendance percentage for student ID: {} between {} and {}", 
            studentId, startDate, endDate);
        
        List<Attendance> attendances = attendanceRepository.findByStudent_IdAndAttendanceDateBetween(
            studentId, startDate, endDate);
        
        if (attendances.isEmpty()) return 0.0;
        
        long presentCount = attendances.stream()
            .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
            .count();
        
        return (presentCount * 100.0) / attendances.size();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceStatistics getClassAttendanceStatistics(Long classId, LocalDate date) {
        log.info("Calculating attendance statistics for class ID: {} on date: {}", classId, date);
        
        List<Attendance> attendances = attendanceRepository.findBySchoolClass_IdAndAttendanceDate(
            classId, date);
        
        if (attendances.isEmpty()) {
            return AttendanceStatistics.builder()
                .classId(classId)
                .date(date)
                .totalStudents(0)
                .presentCount(0)
                .absentCount(0)
                .attendancePercentage(0.0)
                .build();
        }
        
        int totalStudents = attendances.size();
        int presentCount = (int) attendances.stream()
            .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count();
        int absentCount = (int) attendances.stream()
            .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count();
        int lateCount = (int) attendances.stream()
            .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count();
        int halfDayCount = (int) attendances.stream()
            .filter(a -> a.getStatus() == Attendance.AttendanceStatus.HALF_DAY).count();
        int excusedCount = (int) attendances.stream()
            .filter(a -> a.getStatus() == Attendance.AttendanceStatus.EXCUSED).count();
        
        double percentage = (presentCount * 100.0) / totalStudents;
        
        SchoolClass schoolClass = attendances.get(0).getSchoolClass();
        
        return AttendanceStatistics.builder()
            .classId(classId)
            .className(schoolClass.getClassName() + " - " + schoolClass.getSection())
            .date(date)
            .totalStudents(totalStudents)
            .presentCount(presentCount)
            .absentCount(absentCount)
            .lateCount(lateCount)
            .halfDayCount(halfDayCount)
            .excusedCount(excusedCount)
            .attendancePercentage(percentage)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAttendanceMarked(Long studentId, LocalDate date, String session) {
        Attendance.AttendanceSession attendanceSession = session != null 
            ? Attendance.AttendanceSession.valueOf(session.toUpperCase())
            : Attendance.AttendanceSession.FULL_DAY;
        return attendanceRepository.existsByStudent_IdAndAttendanceDateAndSession(
            studentId, date, attendanceSession);
    }

    @Override
    public void deleteAttendance(Long id) {
        log.info("Deleting attendance with ID: {}", id);
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));
        attendanceRepository.delete(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceStatistics> getMonthlyAttendanceStatistics(Long classId, int year, int month) {
        log.info("Fetching monthly attendance statistics for class ID: {} for {}/{}", classId, month, year);
        // This would require more complex date range queries
        // For now, return empty list - can be implemented later
        return List.of();
    }

    // Helper Methods
    
    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
            .id(attendance.getId())
            .studentId(attendance.getStudent().getId())
            .studentName(attendance.getStudent().getName())
            .admissionNumber(attendance.getStudent().getUsername())
            .classId(attendance.getSchoolClass().getId())
            .className(attendance.getSchoolClass().getClassName())
            .subjectId(attendance.getSubject() != null ? attendance.getSubject().getId() : null)
            .subjectName(attendance.getSubject() != null ? attendance.getSubject().getSubjectName() : null)
            .attendanceDate(attendance.getAttendanceDate())
            .status(attendance.getStatus())
            .session(attendance.getSession())
            .checkInTime(attendance.getCheckInTime())
            .checkOutTime(attendance.getCheckOutTime())
            .remarks(attendance.getRemarks())
            .markedByTeacherId(attendance.getMarkedBy() != null ? attendance.getMarkedBy().getId() : null)
            .markedByTeacherName(attendance.getMarkedBy() != null ? attendance.getMarkedBy().getName() : null)
            .isVerified(attendance.isVerified())
            .parentNote(attendance.getParentNote())
            .statusDisplay(attendance.getStatus() != null ? attendance.getStatus().toString() : null)
            .sessionDisplay(attendance.getSession() != null ? attendance.getSession().toString() : null)
            .build();
    }
    
    private Long getCurrentOwnerId() {
        // Get the logged-in user ID for multi-tenancy
        return 1L; // For now, using karina's ID. In real implementation, get from security context
    }
}

