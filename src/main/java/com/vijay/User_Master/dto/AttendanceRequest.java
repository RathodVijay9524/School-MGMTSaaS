package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Attendance;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AttendanceRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Class ID is required")
    private Long classId;
    
    private Long subjectId;
    
    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;
    
    @NotNull(message = "Attendance status is required")
    private Attendance.AttendanceStatus status;
    
    private Attendance.AttendanceSession session;
    
    private LocalTime checkInTime;
    
    private LocalTime checkOutTime;
    
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
    
    private Long markedByTeacherId;
    
    private boolean isVerified;
    
    @Size(max = 500, message = "Parent note must not exceed 500 characters")
    private String parentNote;
}

