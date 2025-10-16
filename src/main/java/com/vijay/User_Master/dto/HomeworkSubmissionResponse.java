package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vijay.User_Master.entity.HomeworkSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Homework Submission response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkSubmissionResponse {

    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String studentAdmissionNumber;
    private LocalDateTime submittedDate;
    private HomeworkSubmission.SubmissionStatus status;
    private String submissionFileUrl;
    private String submissionText;
    
    @JsonProperty("isLate")
    private boolean isLate;
    
    private Integer daysLate;
    private Double marksObtained;
    private Double totalMarks;
    private Double percentage;
    private String grade;
    private Long gradedByTeacherId;
    private String gradedByTeacherName;
    private LocalDateTime gradedDate;
    private String teacherFeedback;
    private String studentRemarks;
    private boolean requiresResubmission;
    private LocalDateTime resubmissionDeadline;
    private Integer submissionAttempt;
    
    // Computed fields
    private String statusDisplay;
    private String submissionTimeDisplay; // e.g., "2 days early" or "3 days late"
    
    @JsonProperty("isGraded")
    private boolean isGraded;
    
    @JsonProperty("isPassed")
    private boolean isPassed;
    
    private String gradeColor; // "green", "yellow", "red" based on performance
}

