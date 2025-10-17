package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for feature usage response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureUsageResponse {
    private Integer aiChatbotQueriesUsed;
    private Integer aiChatbotQueriesRemaining;
    private Integer smsSent;
    private Integer whatsappMessagesSent;
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer maxTeachers;
    private Integer currentTeachers;
    private String usageSummary;
}
