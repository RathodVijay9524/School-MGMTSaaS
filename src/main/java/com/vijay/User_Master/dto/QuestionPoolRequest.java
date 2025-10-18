package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating question pools
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPoolRequest {

    @NotBlank(message = "Pool name is required")
    private String poolName;

    private String description;

    private Long subjectId;

    private List<Long> questionIds;

    private List<Long> tagIds;

    private Integer questionsToSelect;

    private DifficultyLevel targetDifficulty;

    private Boolean isActive;
}
