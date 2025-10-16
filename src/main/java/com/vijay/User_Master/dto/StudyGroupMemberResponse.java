package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * DTO for StudyGroupMember responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupMemberResponse {

    private Long id;
    private Long studyGroupId;
    private String studyGroupName;
    private Long studentId;
    private String studentName;
    private String role;
    private String roleIcon;
    private String status;
    private Date joinDate;
    private String formattedJoinDate;
    private Double contributionScore;
    private String contributionLevel;
    private Integer participationCount;
    private Integer helpProvidedCount;
    private Integer helpReceivedCount;
    private Double helpRatio;
    private Date lastActiveDate;
    private Long daysSinceJoined;
    private Long daysSinceLastActive;
    private Boolean isNotificationEnabled;
    private String notes;
    private Boolean isActive;
    private Boolean isPending;
    private Boolean isModerator;
    private Boolean canManageGroup;
    private Date createdOn;
    private Date updatedOn;
}
