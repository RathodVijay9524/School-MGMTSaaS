package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Timetable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

/**
 * DTO for Timetable response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableResponse {

    private Long id;
    private Long classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private Timetable.DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer periodNumber;
    private String roomNumber;
    private Timetable.PeriodType periodType;
    private String academicYear;
    private String semester;
    private Timetable.TimetableStatus status;
    private String notes;
    private boolean isRecurring;
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;

    // Computed fields
    private String dayDisplay;
    private String timeDisplay;
    private String periodTypeDisplay;
    private String statusDisplay;
    private String duration;
    private String ownerName;
}
