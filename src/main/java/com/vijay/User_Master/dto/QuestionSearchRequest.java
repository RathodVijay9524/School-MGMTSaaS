package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.BloomsLevel;
import com.vijay.User_Master.entity.DifficultyLevel;
import com.vijay.User_Master.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for advanced question search
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSearchRequest {

    private String keyword;
    private Long subjectId;
    private Long classId;
    private QuestionType questionType;
    private DifficultyLevel difficulty;
    private BloomsLevel bloomsLevel;
    private String chapter;
    private String topic;
    private List<Long> tagIds;
    private Boolean autoGradableOnly;
    private Boolean activeOnly;
    
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
