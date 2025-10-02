package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Assignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for Assignment response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {

    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private Long teacherId;
    private String teacherName;
    private Assignment.AssignmentType assignmentType;
    private LocalDateTime assignedDate;
    private LocalDateTime dueDate;
    private Double totalMarks;
    private Assignment.AssignmentStatus status;
    private String attachmentUrl;
    private String instructions;
    private Integer submissionsCount;
    private Integer totalStudents;
    private boolean allowLateSubmission;
    private Integer latePenaltyPercentage;
    private String notes;
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;

    // Computed fields
    private boolean isOverdue;
    private boolean isUpcoming;
    private long daysUntilDue;
    private long daysOverdue;
    private String statusDisplay;
    private String typeDisplay;
    private Double submissionPercentage;
    private String ownerName;
}
