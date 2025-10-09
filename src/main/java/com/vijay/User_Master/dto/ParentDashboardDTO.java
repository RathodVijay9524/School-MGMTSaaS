package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Parent Dashboard main page with aggregated data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentDashboardDTO {

    // Parent Information
    private Long parentId;
    private String parentName;
    private String email;
    private String phone;
    
    // Children Summary
    private List<ChildSummaryDTO> children;
    private Integer totalChildren;
    
    // Aggregated Statistics
    private DashboardSummaryDTO summary;
    
    // Recent Notifications/Alerts
    private List<ParentNotificationDTO> notifications;
    
    // Upcoming Events
    private List<EventSummaryDTO> upcomingEvents;
    
    // Pending Items (require action)
    private PendingItemsDTO pendingItems;
    
    // Recent Activities across all children
    private List<ActivityDTO> recentActivities;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardSummaryDTO {
        private Integer totalChildren;
        private Double totalPendingFees;
        private Double totalOverdueFees;
        private Double averageAttendance;
        private Integer totalPendingAssignments;
        private Integer upcomingExamsCount;
        private Integer unreadNotifications;
        private Integer urgentAlerts;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentNotificationDTO {
        private Long id;
        private String childName;
        private Long childId;
        private String type; // FEE_DUE, ATTENDANCE_LOW, EXAM_REMINDER, ASSIGNMENT_DUE, GRADE_PUBLISHED, GENERAL
        private String title;
        private String message;
        private String date;
        private String priority; // HIGH, MEDIUM, LOW
        private boolean isRead;
        private String actionRequired; // YES, NO
        private String actionUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventSummaryDTO {
        private Long id;
        private String title;
        private String description;
        private String eventType;
        private String date;
        private String time;
        private String location;
        private String status;
        private boolean requiresParentAttendance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingItemsDTO {
        private Integer pendingFees;
        private Integer pendingAssignments;
        private Integer upcomingExams;
        private Integer unreadMessages;
        private Integer formsToFill;
        private Integer consentRequired;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDTO {
        private Long id;
        private String childName;
        private Long childId;
        private String activityType; // ATTENDANCE, GRADE, ASSIGNMENT, FEE, EXAM
        private String description;
        private String date;
        private String time;
        private String icon;
        private String color; // For UI styling
    }
}

