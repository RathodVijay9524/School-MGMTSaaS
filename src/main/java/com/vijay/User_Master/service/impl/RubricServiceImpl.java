package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.RubricRequest;
import com.vijay.User_Master.dto.RubricResponse;
import com.vijay.User_Master.entity.Rubric;
import com.vijay.User_Master.entity.RubricCriterion;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.RubricRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.RubricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Rubric Service
 */
@Service
@Slf4j
@Transactional
public class RubricServiceImpl implements RubricService {

    @Autowired
    private RubricRepository rubricRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public RubricResponse createRubric(RubricRequest request, Long ownerId) {
        log.info("Creating rubric: {} for owner: {}", request.getName(), ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Subject subject = null;
        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        }

        Rubric rubric = Rubric.builder()
                .name(request.getName())
                .description(request.getDescription())
                .subject(subject)
                .totalPoints(request.getTotalPoints())
                .rubricType(Rubric.RubricType.valueOf(request.getRubricType()))
                .isActive(true)
                .owner(owner)
                .build();

        // Add criteria
        if (request.getCriteria() != null && !request.getCriteria().isEmpty()) {
            for (RubricRequest.RubricCriterionRequest criterionReq : request.getCriteria()) {
                RubricCriterion criterion = RubricCriterion.builder()
                        .name(criterionReq.getName())
                        .description(criterionReq.getDescription())
                        .maxPoints(criterionReq.getMaxPoints())
                        .weightPercentage(criterionReq.getWeightPercentage())
                        .orderIndex(criterionReq.getOrderIndex())
                        .excellentDescription(criterionReq.getExcellentDescription())
                        .goodDescription(criterionReq.getGoodDescription())
                        .satisfactoryDescription(criterionReq.getSatisfactoryDescription())
                        .needsImprovementDescription(criterionReq.getNeedsImprovementDescription())
                        .build();

                rubric.addCriterion(criterion);
            }
        }

        Rubric saved = rubricRepository.save(rubric);
        log.info("Rubric created successfully with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public RubricResponse updateRubric(Long id, RubricRequest request, Long ownerId) {
        log.info("Updating rubric: {}", id);

        Rubric rubric = rubricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubric", "id", id));

        if (!rubric.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized access to rubric");
        }

        rubric.setName(request.getName());
        rubric.setDescription(request.getDescription());
        rubric.setTotalPoints(request.getTotalPoints());
        rubric.setRubricType(Rubric.RubricType.valueOf(request.getRubricType()));

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
            rubric.setSubject(subject);
        }

        // Update criteria (simple approach: clear and re-add)
        rubric.getCriteria().clear();
        
        if (request.getCriteria() != null && !request.getCriteria().isEmpty()) {
            for (RubricRequest.RubricCriterionRequest criterionReq : request.getCriteria()) {
                RubricCriterion criterion = RubricCriterion.builder()
                        .name(criterionReq.getName())
                        .description(criterionReq.getDescription())
                        .maxPoints(criterionReq.getMaxPoints())
                        .weightPercentage(criterionReq.getWeightPercentage())
                        .orderIndex(criterionReq.getOrderIndex())
                        .excellentDescription(criterionReq.getExcellentDescription())
                        .goodDescription(criterionReq.getGoodDescription())
                        .satisfactoryDescription(criterionReq.getSatisfactoryDescription())
                        .needsImprovementDescription(criterionReq.getNeedsImprovementDescription())
                        .build();

                rubric.addCriterion(criterion);
            }
        }

        Rubric updated = rubricRepository.save(rubric);
        return mapToResponse(updated);
    }

    @Override
    public RubricResponse getRubricById(Long id, Long ownerId) {
        Rubric rubric = rubricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubric", "id", id));

        if (!rubric.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized access to rubric");
        }

        return mapToResponse(rubric);
    }

    @Override
    public List<RubricResponse> getAllRubrics(Long ownerId) {
        return rubricRepository.findByOwnerIdAndIsDeletedFalse(ownerId).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<RubricResponse> getRubricsBySubject(Long subjectId, Long ownerId) {
        return rubricRepository.findBySubjectIdAndIsDeletedFalse(subjectId).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<RubricResponse> getRubricsByType(String rubricType, Long ownerId) {
        return rubricRepository.findByRubricTypeAndOwnerIdAndIsDeletedFalse(
                Rubric.RubricType.valueOf(rubricType), ownerId).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void deleteRubric(Long id, Long ownerId) {
        log.info("Deleting rubric: {}", id);

        Rubric rubric = rubricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubric", "id", id));

        if (!rubric.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized access to rubric");
        }

        rubric.setIsDeleted(true);
        rubricRepository.save(rubric);
    }

    @Override
    public List<RubricResponse> searchRubrics(String keyword, Long ownerId) {
        return rubricRepository.searchByName(ownerId, keyword).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private RubricResponse mapToResponse(Rubric rubric) {
        return RubricResponse.builder()
                .id(rubric.getId())
                .name(rubric.getName())
                .description(rubric.getDescription())
                .subjectId(rubric.getSubject() != null ? rubric.getSubject().getId() : null)
                .subjectName(rubric.getSubject() != null ? rubric.getSubject().getSubjectName() : null)
                .totalPoints(rubric.getTotalPoints())
                .isActive(rubric.getIsActive())
                .rubricType(rubric.getRubricType() != null ? rubric.getRubricType().name() : null)
                .criteria(rubric.getCriteria().stream()
                        .map(c -> RubricResponse.RubricCriterionResponse.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .description(c.getDescription())
                                .maxPoints(c.getMaxPoints())
                                .weightPercentage(c.getWeightPercentage())
                                .orderIndex(c.getOrderIndex())
                                .excellentDescription(c.getExcellentDescription())
                                .goodDescription(c.getGoodDescription())
                                .satisfactoryDescription(c.getSatisfactoryDescription())
                                .needsImprovementDescription(c.getNeedsImprovementDescription())
                                .build())
                        .collect(java.util.stream.Collectors.toList()))
                .createdByTeacherId(rubric.getCreatedByTeacher() != null ? rubric.getCreatedByTeacher().getId() : null)
                .createdByTeacherName(rubric.getCreatedByTeacher() != null ? rubric.getCreatedByTeacher().getName() : null)
                .build();
    }
}
