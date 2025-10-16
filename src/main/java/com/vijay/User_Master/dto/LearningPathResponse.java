package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

/**
 * DTO for LearningPath responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathResponse {

    private Long id;
    private String pathName;
    private String description;
    private String subject;
    private String gradeLevel;
    private String learningObjectives;
    private Integer estimatedDurationHours;
    private String formattedDuration;
    private String difficultyLevel;
    private String prerequisites;
    private String learningOutcomes;
    private String assessmentCriteria;
    private String resourcesNeeded;
    private Boolean isAdaptive;
    private Boolean isActive;
    private Integer pathOrder;
    private Double completionPercentage;
    private Double masteryLevel;
    private String status;
    private Integer totalModules;
    private Integer completedModules;
    private Integer remainingModules;
    private Boolean isCompleted;
    private Long studentId;
    private String studentName;
    private Date createdOn;
    private Date updatedOn;
    private List<LearningModuleResponse> modules;
}
