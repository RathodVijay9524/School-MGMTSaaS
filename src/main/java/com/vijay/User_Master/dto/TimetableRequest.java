package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Timetable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for Timetable creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableRequest {

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    @NotNull(message = "Day of week is required")
    private Timetable.DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Min(value = 1, message = "Period number must be at least 1")
    @Max(value = 12, message = "Period number cannot exceed 12")
    private Integer periodNumber;

    @Size(max = 50, message = "Room number cannot exceed 50 characters")
    private String roomNumber;

    @NotNull(message = "Period type is required")
    private Timetable.PeriodType periodType;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year cannot exceed 20 characters")
    private String academicYear;

    @Size(max = 20, message = "Semester cannot exceed 20 characters")
    private String semester;

    @Builder.Default
    private Timetable.TimetableStatus status = Timetable.TimetableStatus.ACTIVE;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Builder.Default
    private boolean isRecurring = true;
}
