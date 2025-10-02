package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceStatistics {
    private Long classId;
    private String className;
    private LocalDate date;
    private int totalStudents;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    private int halfDayCount;
    private int excusedCount;
    private double attendancePercentage;
}