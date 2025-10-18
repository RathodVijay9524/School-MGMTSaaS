package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.QuestionPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of QuestionPoolService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionPoolServiceImpl implements QuestionPoolService {

    private final QuestionPoolRepository questionPoolRepository;
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionTagRepository questionTagRepository;
    private final UserRepository userRepository;

    @Override
    public QuestionPoolResponse createPool(QuestionPoolRequest request, Long ownerId) {
        log.info("Creating question pool for owner: {}", ownerId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        QuestionPool pool = QuestionPool.builder()
                .poolName(request.getPoolName())
                .description(request.getDescription())
                .questionsToSelect(request.getQuestionsToSelect())
                .targetDifficulty(request.getTargetDifficulty())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .owner(owner)
                .build();

        if (request.getSubjectId() != null) {
            subjectRepository.findById(request.getSubjectId()).ifPresent(pool::setSubject);
        }

        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
            pool.setQuestions(questions);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<QuestionTag> tags = questionTagRepository.findAllById(request.getTagIds());
            pool.setTags(tags);
        }

        QuestionPool saved = questionPoolRepository.save(pool);
        log.info("Question pool created with ID: {}", saved.getId());
        
        return mapToResponse(saved);
    }

    @Override
    public QuestionPoolResponse updatePool(Long id, QuestionPoolRequest request, Long ownerId) {
        QuestionPool pool = questionPoolRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Question pool not found"));

        pool.setPoolName(request.getPoolName());
        pool.setDescription(request.getDescription());
        pool.setQuestionsToSelect(request.getQuestionsToSelect());
        pool.setTargetDifficulty(request.getTargetDifficulty());
        pool.setIsActive(request.getIsActive());

        QuestionPool updated = questionPoolRepository.save(pool);
        return mapToResponse(updated);
    }

    @Override
    public QuestionPoolResponse getPoolById(Long id, Long ownerId) {
        QuestionPool pool = questionPoolRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Question pool not found"));
        return mapToResponse(pool);
    }

    @Override
    public void deletePool(Long id, Long ownerId) {
        QuestionPool pool = questionPoolRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Question pool not found"));
        pool.setIsDeleted(true);
        questionPoolRepository.save(pool);
        log.info("Question pool deleted: {}", id);
    }

    @Override
    public List<QuestionPoolResponse> getAllPools(Long ownerId) {
        return questionPoolRepository.findByOwnerIdAndIsDeletedFalse(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionPoolResponse> getPoolsBySubject(Long subjectId, Long ownerId) {
        return questionPoolRepository.findBySubjectIdAndIsDeletedFalse(subjectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionPoolGenerateResponse generateQuestions(QuestionPoolGenerateRequest request, Long ownerId) {
        QuestionPool pool = questionPoolRepository.findByIdAndOwnerIdAndIsDeletedFalse(request.getPoolId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Question pool not found"));

        List<Question> availableQuestions = new ArrayList<>(pool.getQuestions());
        List<Question> selectedQuestions = new ArrayList<>();

        // Filter by difficulty if specified
        if (request.getTargetDifficulty() != null) {
            availableQuestions = availableQuestions.stream()
                    .filter(q -> q.getDifficulty() == request.getTargetDifficulty())
                    .collect(Collectors.toList());
        }

        // Filter by question types if specified
        if (request.getQuestionTypes() != null && !request.getQuestionTypes().isEmpty()) {
            availableQuestions = availableQuestions.stream()
                    .filter(q -> request.getQuestionTypes().contains(q.getQuestionType()))
                    .collect(Collectors.toList());
        }

        // Generate questions based on distribution
        if (request.getDifficultyDistribution() != null && !request.getDifficultyDistribution().isEmpty()) {
            selectedQuestions.addAll(selectByDifficultyDistribution(availableQuestions, request.getDifficultyDistribution()));
        } else if (request.getTypeDistribution() != null && !request.getTypeDistribution().isEmpty()) {
            selectedQuestions.addAll(selectByTypeDistribution(availableQuestions, request.getTypeDistribution()));
        } else {
            // Random selection
            Collections.shuffle(availableQuestions);
            int count = Math.min(request.getNumberOfQuestions(), availableQuestions.size());
            selectedQuestions.addAll(availableQuestions.subList(0, count));
        }

        // Calculate total points
        Double totalPoints = selectedQuestions.stream()
                .mapToDouble(Question::getPoints)
                .sum();

        // Map to response
        List<QuestionResponse> questionResponses = selectedQuestions.stream()
                .map(this::mapQuestionToResponse)
                .collect(Collectors.toList());

        return QuestionPoolGenerateResponse.builder()
                .poolId(pool.getId())
                .poolName(pool.getPoolName())
                .requestedQuestions(request.getNumberOfQuestions())
                .generatedQuestions(selectedQuestions.size())
                .questions(questionResponses)
                .totalPoints(totalPoints)
                .build();
    }

    @Override
    public QuestionPoolResponse addQuestionsToPool(Long poolId, List<Long> questionIds, Long ownerId) {
        QuestionPool pool = questionPoolRepository.findByIdAndOwnerIdAndIsDeletedFalse(poolId, ownerId)
                .orElseThrow(() -> new RuntimeException("Question pool not found"));

        List<Question> questions = questionRepository.findAllById(questionIds);
        questions.forEach(pool::addQuestion);

        QuestionPool updated = questionPoolRepository.save(pool);
        return mapToResponse(updated);
    }

    @Override
    public QuestionPoolResponse removeQuestionsFromPool(Long poolId, List<Long> questionIds, Long ownerId) {
        QuestionPool pool = questionPoolRepository.findByIdAndOwnerIdAndIsDeletedFalse(poolId, ownerId)
                .orElseThrow(() -> new RuntimeException("Question pool not found"));

        List<Question> questions = questionRepository.findAllById(questionIds);
        questions.forEach(pool::removeQuestion);

        QuestionPool updated = questionPoolRepository.save(pool);
        return mapToResponse(updated);
    }

    // Helper methods
    private List<Question> selectByDifficultyDistribution(List<Question> questions, Map<String, Integer> distribution) {
        List<Question> selected = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            DifficultyLevel difficulty = DifficultyLevel.valueOf(entry.getKey());
            int count = entry.getValue();
            
            List<Question> filtered = questions.stream()
                    .filter(q -> q.getDifficulty() == difficulty)
                    .collect(Collectors.toList());
            
            Collections.shuffle(filtered);
            selected.addAll(filtered.subList(0, Math.min(count, filtered.size())));
        }
        
        return selected;
    }

    private List<Question> selectByTypeDistribution(List<Question> questions, Map<String, Integer> distribution) {
        List<Question> selected = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            QuestionType type = QuestionType.valueOf(entry.getKey());
            int count = entry.getValue();
            
            List<Question> filtered = questions.stream()
                    .filter(q -> q.getQuestionType() == type)
                    .collect(Collectors.toList());
            
            Collections.shuffle(filtered);
            selected.addAll(filtered.subList(0, Math.min(count, filtered.size())));
        }
        
        return selected;
    }

    private QuestionPoolResponse mapToResponse(QuestionPool pool) {
        return QuestionPoolResponse.builder()
                .id(pool.getId())
                .poolName(pool.getPoolName())
                .description(pool.getDescription())
                .subjectId(pool.getSubject() != null ? pool.getSubject().getId() : null)
                .subjectName(pool.getSubject() != null ? pool.getSubject().getSubjectName() : null)
                .questionCount(pool.getQuestionCount())
                .questionsToSelect(pool.getQuestionsToSelect())
                .targetDifficulty(pool.getTargetDifficulty())
                .isActive(pool.getIsActive())
                .build();
    }

    private QuestionResponse mapQuestionToResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .build();
    }
}
