package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * DTO for StudySession responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionResponse {

    private Long id;
    private String sessionTitle;
    private String description;
    private String topic;
    private String sessionType;
    private String typeIcon;
    private Date scheduledDate;
    private String formattedScheduledDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Integer durationMinutes;
    private String formattedDuration;
    private String location;
    private String meetingLink;
    private String agenda;
    private String materials;
    private Integer participantsCount;
    private String sessionStatus;
    private String statusColor;
    private String sessionNotes;
    private String feedback;
    private Boolean isRecorded;
    private String recordingLink;
    private Boolean isActive;
    private Boolean isCompleted;
    private Boolean isOngoing;
    private Boolean isUpcoming;
    private Boolean isPast;
    private Long studyGroupId;
    private String studyGroupName;
    private Long facilitatorId;
    private String facilitatorName;
    private Date createdOn;
    private Date updatedOn;
}
