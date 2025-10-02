package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Exam statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStatistics {

    private long totalExams;
    private long scheduledExams;
    private long ongoingExams;
    private long completedExams;
    private long cancelledExams;
    private long postponedExams;
    private long rescheduledExams;
    private long midtermExams;
    private long finalExams;
    private long unitTests;
    private long quarterlyExams;
    private long halfYearlyExams;
    private long annualExams;
    private long surpriseTests;
    private long practicalExams;
    private long examsWithResultsPublished;
    private long examsWithoutResultsPublished;
    private long examsBySubject; // Example: count of exams for a specific subject
    private long examsByClass;   // Example: count of exams for a specific class
    private long examsBySupervisor; // Example: count of exams supervised by a specific teacher
    private long upcomingExamsCount;
    private long overdueExamsCount;
    private double averageExamDuration;
    private double averageTotalMarks;
}
