package com.vijay.User_Master.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for batch grading multiple quiz attempts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGradingRequest {

    @NotNull(message = "Attempt IDs are required")
    private List<Long> attemptIds;

    private Boolean autoGradeOnly;
    private Boolean sendNotifications;
}
