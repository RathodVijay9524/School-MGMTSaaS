package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Long classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private LocalDate attendanceDate;
    private Attendance.AttendanceStatus status;
    private Attendance.AttendanceSession session;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String remarks;
    private Long markedByTeacherId;
    private String markedByTeacherName;
    private boolean isVerified;
    private String parentNote;
    
    // Computed fields
    private String statusDisplay;
    private String sessionDisplay;
    private boolean isLate; // If check-in time is after expected time
    private String durationInSchool; // Time between check-in and check-out
    
    // Audit Information
    private LocalDate createdOn;
    private String createdBy;
}

