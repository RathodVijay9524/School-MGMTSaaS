package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * DTO for TutoringSession responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutoringSessionResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String subject;
    private String topic;
    private String gradeLevel;
    private String question;
    private String aiResponse;
    private String explanation;
    private String stepByStepSolution;
    private String keyConcepts;
    private String practiceProblems;
    private String relatedTopics;
    private String difficultyLevel;
    private String learningObjective;
    private Integer timeSpentMinutes;
    private String formattedTimeSpent;
    private Integer studentSatisfactionRating;
    private Double comprehensionScore;
    private Boolean followUpRequired;
    private Boolean teacherReviewRequired;
    private String sessionStatus;
    private String aiProvider;
    private Integer tokensUsed;
    private Double costUsd;
    private Double costPerMinute;
    private Boolean isCompleted;
    private Boolean needsFollowUp;
    private Date createdOn;
    private Date updatedOn;
}
