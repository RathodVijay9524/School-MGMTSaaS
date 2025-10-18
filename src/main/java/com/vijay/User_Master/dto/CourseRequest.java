package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Course;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Course creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Course code is required")
    @Size(min = 2, max = 20, message = "Course code must be between 2 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Course code must contain only uppercase letters and numbers")
    private String courseCode; // e.g., "CS101", "BIO202"

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 200, message = "Course name must be between 3 and 200 characters")
    private String courseName;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Class ID is required")
    private Long classId;

    private Long teacherId; // Optional - can be assigned later

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Semester is required")
    @Size(max = 50, message = "Semester name cannot exceed 50 characters")
    private String semester; // e.g., "Fall 2024", "Spring 2025"

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits cannot exceed 10")
    private Integer credits;

    @Min(value = 1, message = "Maximum students must be at least 1")
    @Max(value = 500, message = "Maximum students cannot exceed 500")
    private Integer maxStudents;

    @Builder.Default
    @Min(value = 0, message = "Enrolled students cannot be negative")
    private Integer enrolledStudents = 0;

    @Builder.Default
    private Course.CourseStatus status = Course.CourseStatus.PLANNED;

    @Size(max = 1000, message = "Syllabus cannot exceed 1000 characters")
    private String syllabus; // URL or text

    @Size(max = 200, message = "Schedule cannot exceed 200 characters")
    private String schedule; // e.g., "Mon, Wed, Fri 10:00-11:00 AM"

    @Size(max = 100, message = "Classroom cannot exceed 100 characters")
    private String classroom;
}
