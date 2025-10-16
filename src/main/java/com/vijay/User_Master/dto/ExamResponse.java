package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * DTO for Exam response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {

    private Long id;
    private String examName;
    private String examCode;
    private Exam.ExamType examType;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Double totalMarks;
    private Double passingMarks;
    private String roomNumber;
    private Long supervisorId;
    private String supervisorName;
    private Exam.ExamStatus status;
    private String semester;
    private String academicYear;
    private String instructions;
    private String syllabus;
    private boolean resultsPublished;
    private LocalDate resultPublishDate;
    private String notes;
    private boolean isDeleted;
    private Date createdOn;
    private Date updatedOn;
    
    // ========== NEW FIELDS ==========
    private String questionPaperUrl;
    private Integer totalQuestions;
    private String questionPattern;
    private boolean hasNegativeMarking;
    private Double negativeMarkingPercentage;
    private boolean isBlindGraded;

    // Computed fields
    private boolean isUpcoming;
    private boolean isOngoing;
    private boolean isCompleted;
    private boolean isOverdue;
    private long daysUntilExam;
    private long daysOverdue;
    private String statusDisplay;
    private String typeDisplay;
    private String durationDisplay;
    private String supervisorDisplay;
}
