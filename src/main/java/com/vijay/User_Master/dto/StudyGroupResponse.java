package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

/**
 * DTO for StudyGroup responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupResponse {

    private Long id;
    private String groupName;
    private String description;
    private String subject;
    private String gradeLevel;
    private String topic;
    private String learningObjectives;
    private String groupType;
    private Integer maxMembers;
    private Integer currentMembers;
    private String groupStatus;
    private String studySchedule;
    private String meetingLink;
    private String chatRoomId;
    private Boolean isPublic;
    private Boolean requiresApproval;
    private Boolean isActive;
    private String statusText;
    private String formattedDuration;
    private Boolean isFull;
    private Boolean hasSpace;
    private Boolean isExpired;
    private Boolean canJoin;
    private Integer availableSpots;
    private Long creatorId;
    private String creatorName;
    private Date startDate;
    private Date endDate;
    private Date lastActivityDate;
    private Date createdOn;
    private Date updatedOn;
    private List<StudyGroupMemberResponse> members;
    private List<StudySessionResponse> studySessions;
}
