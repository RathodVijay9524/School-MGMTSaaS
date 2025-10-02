package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Timetable statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableStatistics {

    private long totalPeriods;
    private long activePeriods;
    private long inactivePeriods;
    private long temporaryPeriods;
    private long cancelledPeriods;
    private long lecturePeriods;
    private long practicalPeriods;
    private long labPeriods;
    private long breakPeriods;
    private long lunchPeriods;
    private long sportsPeriods;
    private long assemblyPeriods;
    private long libraryPeriods;
    private long studyHallPeriods;
    private long mondayPeriods;
    private long tuesdayPeriods;
    private long wednesdayPeriods;
    private long thursdayPeriods;
    private long fridayPeriods;
    private long saturdayPeriods;
    private long sundayPeriods;
    private long recurringPeriods;
    private long nonRecurringPeriods;
    private long totalTeachersScheduled;
    private long totalClassesScheduled;
    private long totalSubjectsScheduled;
}
