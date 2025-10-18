package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * RubricCriterion entity
 * Individual scoring criteria within a rubric
 */
@Entity
@Table(name = "rubric_criteria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RubricCriterion extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubric_id", nullable = false)
    private Rubric rubric;

    @Column(nullable = false)
    private String name; // e.g., "Content Quality", "Grammar", "Organization"

    @Column(length = 1000)
    private String description;

    @Column(name = "max_points", nullable = false)
    private Double maxPoints;

    @Column(name = "weight_percentage")
    private Double weightPercentage; // Optional: weight in overall grade

    @Column(name = "order_index")
    private Integer orderIndex; // Display order

    // Performance levels
    @Column(name = "excellent_description", length = 500)
    private String excellentDescription; // 90-100%

    @Column(name = "good_description", length = 500)
    private String goodDescription; // 70-89%

    @Column(name = "satisfactory_description", length = 500)
    private String satisfactoryDescription; // 50-69%

    @Column(name = "needs_improvement_description", length = 500)
    private String needsImprovementDescription; // Below 50%

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Helper methods
    public String getPerformanceLevelDescription(Double score) {
        double percentage = (score / maxPoints) * 100;
        
        if (percentage >= 90) return excellentDescription;
        if (percentage >= 70) return goodDescription;
        if (percentage >= 50) return satisfactoryDescription;
        return needsImprovementDescription;
    }

    public String getPerformanceLevel(Double score) {
        double percentage = (score / maxPoints) * 100;
        
        if (percentage >= 90) return "Excellent";
        if (percentage >= 70) return "Good";
        if (percentage >= 50) return "Satisfactory";
        return "Needs Improvement";
    }
}
