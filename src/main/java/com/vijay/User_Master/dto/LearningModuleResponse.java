package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * DTO for LearningModule responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningModuleResponse {

    private Long id;
    private String moduleName;
    private String description;
    private String content;
    private String learningObjectives;
    private String moduleType;
    private String difficultyLevel;
    private Integer estimatedDurationMinutes;
    private String formattedEstimatedDuration;
    private Integer orderIndex;
    private String prerequisites;
    private String resources;
    private String assessmentQuestions;
    private Boolean isCompleted;
    private Date completionDate;
    private Integer timeSpentMinutes;
    private String formattedTimeSpent;
    private Double scorePercentage;
    private String grade;
    private Integer attemptsCount;
    private Boolean isActive;
    private Double progressPercentage;
    private Boolean isPassed;
    private Long learningPathId;
    private String learningPathName;
    private Long studentId;
    private String studentName;
    private Date createdOn;
    private Date updatedOn;
}
