package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.RubricRequest;
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
    public Rubric createRubric(RubricRequest request, Long ownerId) {
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
        return saved;
    }

    @Override
    public Rubric updateRubric(Long id, RubricRequest request, Long ownerId) {
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

        return rubricRepository.save(rubric);
    }

    @Override
    public Rubric getRubricById(Long id, Long ownerId) {
        Rubric rubric = rubricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubric", "id", id));

        if (!rubric.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized access to rubric");
        }

        return rubric;
    }

    @Override
    public List<Rubric> getAllRubrics(Long ownerId) {
        return rubricRepository.findByOwnerIdAndIsDeletedFalse(ownerId);
    }

    @Override
    public List<Rubric> getRubricsBySubject(Long subjectId, Long ownerId) {
        return rubricRepository.findBySubjectIdAndIsDeletedFalse(subjectId);
    }

    @Override
    public List<Rubric> getRubricsByType(String rubricType, Long ownerId) {
        return rubricRepository.findByRubricTypeAndOwnerIdAndIsDeletedFalse(
                Rubric.RubricType.valueOf(rubricType), ownerId);
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
    public List<Rubric> searchRubrics(String keyword, Long ownerId) {
        return rubricRepository.searchByName(ownerId, keyword);
    }
}
