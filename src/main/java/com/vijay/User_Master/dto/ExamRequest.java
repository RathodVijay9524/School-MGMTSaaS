package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Exam;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for Exam creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequest {

    @NotBlank(message = "Exam name cannot be empty")
    @Size(max = 100, message = "Exam name cannot exceed 100 characters")
    private String examName;

    @NotBlank(message = "Exam code cannot be empty")
    @Size(max = 20, message = "Exam code cannot exceed 20 characters")
    private String examCode;

    @NotNull(message = "Exam type is required")
    private Exam.ExamType examType;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Exam date is required")
    @Future(message = "Exam date must be in the future")
    private LocalDate examDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Min(value = 30, message = "Duration must be at least 30 minutes")
    @Max(value = 300, message = "Duration cannot exceed 300 minutes")
    private Integer durationMinutes;

    @Min(value = 0, message = "Total marks cannot be negative")
    private Double totalMarks;

    @Min(value = 0, message = "Passing marks cannot be negative")
    private Double passingMarks;

    @Size(max = 50, message = "Room number cannot exceed 50 characters")
    private String roomNumber;

    private Long supervisorId; // Teacher ID who supervises the exam

    @Builder.Default
    private Exam.ExamStatus status = Exam.ExamStatus.SCHEDULED;

    @Size(max = 20, message = "Semester cannot exceed 20 characters")
    private String semester;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year cannot exceed 20 characters")
    private String academicYear;

    @Size(max = 1000, message = "Instructions cannot exceed 1000 characters")
    private String instructions;

    @Size(max = 1000, message = "Syllabus cannot exceed 1000 characters")
    private String syllabus;

    @Builder.Default
    private boolean resultsPublished = false;

    private LocalDate resultPublishDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
