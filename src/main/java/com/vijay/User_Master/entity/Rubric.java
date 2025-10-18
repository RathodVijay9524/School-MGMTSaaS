package com.vijay.User_Master.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Rubric entity for grading assignments
 * Defines criteria and scoring guidelines
 */
@Entity
@Table(name = "rubrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Rubric extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "total_points", nullable = false)
    private Double totalPoints;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "rubric_type")
    @Enumerated(EnumType.STRING)
    private RubricType rubricType; // ESSAY, PROJECT, PRESENTATION, LAB_REPORT, etc.

    @OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference
    private List<RubricCriterion> criteria = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_teacher_id")
    private Worker createdByTeacher;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Enums
    public enum RubricType {
        ESSAY, PROJECT, PRESENTATION, LAB_REPORT, RESEARCH_PAPER, CREATIVE_WRITING, CODE_ASSIGNMENT, GENERAL
    }

    // Helper methods
    public void addCriterion(RubricCriterion criterion) {
        criteria.add(criterion);
        criterion.setRubric(this);
    }

    public void removeCriterion(RubricCriterion criterion) {
        criteria.remove(criterion);
        criterion.setRubric(null);
    }

    public Double calculateTotalPoints() {
        return criteria.stream()
                .mapToDouble(RubricCriterion::getMaxPoints)
                .sum();
    }
}
