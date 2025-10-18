package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * SkillPrerequisite entity for Adaptive Learning System
 * Defines prerequisite relationships between skills
 */
@Entity
@Table(name = "skill_prerequisites", indexes = {
    @Index(name = "idx_subject_skill", columnList = "subject_id,skill_key"),
    @Index(name = "idx_prerequisite", columnList = "prerequisite_skill_key")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SkillPrerequisite extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "skill_key", nullable = false, length = 100)
    private String skillKey; // The skill that has prerequisites

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "prerequisite_skill_key", nullable = false, length = 100)
    private String prerequisiteSkillKey; // The required prerequisite skill

    @Column(name = "prerequisite_skill_name", nullable = false)
    private String prerequisiteSkillName;

    @Column(name = "minimum_mastery_required")
    @Builder.Default
    private Double minimumMasteryRequired = 60.0; // Default 60%

    @Column(name = "weight")
    @Builder.Default
    private Double weight = 1.0; // Importance (0-1)

    @Column(name = "is_strict")
    @Builder.Default
    private Boolean isStrict = true; // If true, must meet requirement

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public boolean isMasteryMet(Double currentMastery) {
        return currentMastery != null && currentMastery >= minimumMasteryRequired;
    }

    public boolean isHighPriority() {
        return weight >= 0.8;
    }

    public boolean isMediumPriority() {
        return weight >= 0.5 && weight < 0.8;
    }

    public boolean isLowPriority() {
        return weight < 0.5;
    }

    public String getPriorityLevel() {
        if (isHighPriority()) return "High";
        if (isMediumPriority()) return "Medium";
        return "Low";
    }

    public String getFormattedRequirement() {
        return String.format("Requires %.0f%% mastery in %s", minimumMasteryRequired, prerequisiteSkillName);
    }
}
