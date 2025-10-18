package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of QuestionBankService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;
    private final RubricRepository rubricRepository;

    @Override
    public QuestionResponse createQuestion(QuestionRequest request, Long ownerId) {
        log.info("Creating question for owner: {}", ownerId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Question question = buildQuestionFromRequest(request, owner);
        Question saved = questionRepository.save(question);
        
        log.info("Question created with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public QuestionResponse updateQuestion(Long id, QuestionRequest request, Long ownerId) {
        log.info("Updating question ID: {} for owner: {}", id, ownerId);
        
        Question question = questionRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        updateQuestionFromRequest(question, request);
        Question updated = questionRepository.save(question);
        
        return mapToResponse(updated);
    }

    @Override
    public QuestionResponse getQuestionById(Long id, Long ownerId) {
        Question question = questionRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return mapToResponse(question);
    }

    @Override
    public void deleteQuestion(Long id, Long ownerId) {
        Question question = questionRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsDeleted(true);
        questionRepository.save(question);
        log.info("Question deleted: {}", id);
    }

    @Override
    public List<QuestionResponse> getAllQuestions(Long ownerId) {
        return questionRepository.findByOwnerIdAndIsDeletedFalse(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuestionResponse> getQuestionsPaginated(Long ownerId, Pageable pageable) {
        return questionRepository.findByOwnerIdAndIsDeletedFalse(ownerId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public List<QuestionResponse> getQuestionsBySubject(Long subjectId, Long ownerId) {
        return questionRepository.findBySubjectIdAndIsDeletedFalse(subjectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByClass(Long classId, Long ownerId) {
        return questionRepository.findBySchoolClassIdAndIsDeletedFalse(classId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByType(QuestionType questionType, Long ownerId) {
        return questionRepository.findByQuestionTypeAndOwnerIdAndIsDeletedFalse(questionType, ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByDifficulty(DifficultyLevel difficulty, Long ownerId) {
        return questionRepository.findByDifficultyAndOwnerIdAndIsDeletedFalse(difficulty, ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> searchQuestions(String keyword, Long ownerId) {
        return questionRepository.searchByQuestionText(ownerId, keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuestionResponse> advancedSearch(QuestionSearchRequest request, Long ownerId) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20
        );
        
        return questionRepository.findByCriteria(
                ownerId,
                request.getSubjectId(),
                request.getQuestionType(),
                request.getDifficulty(),
                pageable
        ).map(this::mapToResponse);
    }

    @Override
    public List<QuestionResponse> getQuestionsByTags(List<Long> tagIds, Long ownerId) {
        return questionRepository.findByTagIds(tagIds, ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse duplicateQuestion(QuestionDuplicateRequest request, Long ownerId) {
        Question original = questionRepository.findByIdAndOwnerIdAndIsDeletedFalse(request.getQuestionId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Question duplicate = duplicateQuestionEntity(original, request);
        Question saved = questionRepository.save(duplicate);
        
        return mapToResponse(saved);
    }

    @Override
    public BulkQuestionImportResponse bulkImportQuestions(BulkQuestionImportRequest request, Long ownerId) {
        List<BulkQuestionImportResponse.ImportError> errors = new ArrayList<>();
        List<Long> importedIds = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < request.getQuestions().size(); i++) {
            try {
                QuestionRequest qr = request.getQuestions().get(i);
                QuestionResponse created = createQuestion(qr, ownerId);
                importedIds.add(created.getId());
                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add(BulkQuestionImportResponse.ImportError.builder()
                        .questionIndex(i)
                        .questionText(request.getQuestions().get(i).getQuestionText())
                        .errorMessage(e.getMessage())
                        .build());
            }
        }

        return BulkQuestionImportResponse.builder()
                .totalQuestions(request.getQuestions().size())
                .successfulImports(successCount)
                .failedImports(failCount)
                .skippedDuplicates(0)
                .errors(errors)
                .importedQuestionIds(importedIds)
                .build();
    }

    @Override
    public QuestionBankStatisticsResponse getStatistics(Long ownerId) {
        Long totalQuestions = questionRepository.countByOwnerId(ownerId);
        List<Question> questions = questionRepository.findByOwnerIdAndIsDeletedFalse(ownerId);
        
        return QuestionBankStatisticsResponse.builder()
                .totalQuestions(totalQuestions)
                .activeQuestions((long) questions.stream().filter(Question::getIsActive).count())
                .autoGradableQuestions((long) questions.stream().filter(Question::getAutoGradable).count())
                .questionsByType(buildTypeStatistics(questions))
                .questionsByDifficulty(buildDifficultyStatistics(questions))
                .build();
    }

    @Override
    public QuestionResponse.QuestionTagResponse createTag(QuestionTagRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        QuestionTag tag = QuestionTag.builder()
                .tagName(request.getTagName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        QuestionTag saved = questionTagRepository.save(tag);
        return mapTagToResponse(saved);
    }

    @Override
    public List<QuestionResponse.QuestionTagResponse> getAllTags(Long ownerId) {
        return questionTagRepository.findByOwnerIdAndIsDeletedFalse(ownerId).stream()
                .map(this::mapTagToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTag(Long tagId, Long ownerId) {
        QuestionTag tag = questionTagRepository.findByIdAndOwnerIdAndIsDeletedFalse(tagId, ownerId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setIsDeleted(true);
        questionTagRepository.save(tag);
    }

    // Helper methods
    private Question buildQuestionFromRequest(QuestionRequest request, User owner) {
        Question question;
        
        switch (request.getQuestionType()) {
            case MULTIPLE_CHOICE:
                question = buildMultipleChoiceQuestion(request, owner);
                break;
            case TRUE_FALSE:
                question = buildTrueFalseQuestion(request, owner);
                break;
            case SHORT_ANSWER:
                question = buildShortAnswerQuestion(request, owner);
                break;
            case ESSAY:
                question = buildEssayQuestion(request, owner);
                break;
            case MATCHING:
                question = buildMatchingQuestion(request, owner);
                break;
            case ORDERING:
                question = buildOrderingQuestion(request, owner);
                break;
            case FILL_IN_BLANK:
                question = buildFillInBlankQuestion(request, owner);
                break;
            default:
                throw new RuntimeException("Unsupported question type: " + request.getQuestionType());
        }

        setCommonFields(question, request, owner);
        return question;
    }

    private void setCommonFields(Question question, QuestionRequest request, User owner) {
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setDifficulty(request.getDifficulty());
        question.setChapter(request.getChapter());
        question.setTopic(request.getTopic());
        question.setExplanation(request.getExplanation());
        question.setPoints(request.getPoints());
        question.setHints(request.getHints());
        question.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        question.setAutoGradable(request.getAutoGradable() != null ? request.getAutoGradable() : false);
        question.setAllowPartialCredit(request.getAllowPartialCredit() != null ? request.getAllowPartialCredit() : false);
        question.setTimeLimitSeconds(request.getTimeLimitSeconds());
        question.setBloomsLevel(request.getBloomsLevel());
        question.setImageUrl(request.getImageUrl());
        question.setVideoUrl(request.getVideoUrl());
        question.setAudioUrl(request.getAudioUrl());
        question.setOwner(owner);

        if (request.getSubjectId() != null) {
            subjectRepository.findById(request.getSubjectId())
                    .ifPresent(question::setSubject);
        }

        if (request.getClassId() != null) {
            schoolClassRepository.findById(request.getClassId())
                    .ifPresent(question::setSchoolClass);
        }
    }

    private MultipleChoiceQuestion buildMultipleChoiceQuestion(QuestionRequest request, User owner) {
        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
        mcq.setAllowMultipleAnswers(request.getAllowMultipleAnswers());
        mcq.setRandomizeOptions(request.getRandomizeOptions());
        mcq.setMinSelections(request.getMinSelections());
        mcq.setMaxSelections(request.getMaxSelections());
        
        if (request.getOptions() != null) {
            List<QuestionOption> options = request.getOptions().stream()
                    .map(opt -> QuestionOption.builder()
                            .question(mcq)
                            .optionText(opt.getOptionText())
                            .isCorrect(opt.getIsCorrect())
                            .orderIndex(opt.getOrderIndex())
                            .partialCreditPercentage(opt.getPartialCreditPercentage())
                            .feedback(opt.getFeedback())
                            .optionImageUrl(opt.getOptionImageUrl())
                            .build())
                    .collect(Collectors.toList());
            mcq.setOptions(options);
        }
        
        return mcq;
    }

    private TrueFalseQuestion buildTrueFalseQuestion(QuestionRequest request, User owner) {
        TrueFalseQuestion question = new TrueFalseQuestion();
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setTrueFeedback(request.getTrueFeedback());
        question.setFalseFeedback(request.getFalseFeedback());
        return question;
    }

    private ShortAnswerQuestion buildShortAnswerQuestion(QuestionRequest request, User owner) {
        ShortAnswerQuestion question = new ShortAnswerQuestion();
        question.setAcceptedAnswers(request.getAcceptedAnswers() != null ? request.getAcceptedAnswers() : new ArrayList<>());
        question.setCaseSensitive(request.getCaseSensitive());
        question.setExactMatch(request.getExactMatch());
        question.setMaxLength(request.getMaxLength());
        question.setMinLength(request.getMinLength());
        question.setUseAiGrading(request.getUseAiGrading());
        return question;
    }

    private EssayQuestion buildEssayQuestion(QuestionRequest request, User owner) {
        EssayQuestion essay = new EssayQuestion();
        essay.setMinWords(request.getMinWords());
        essay.setMaxWords(request.getMaxWords());
        essay.setSampleAnswer(request.getSampleAnswer());
        essay.setUseAiGrading(request.getUseAiGrading());
        essay.setRequireManualReview(request.getRequireManualReview());
        
        if (request.getRubricId() != null) {
            rubricRepository.findById(request.getRubricId())
                    .ifPresent(essay::setRubric);
        }
        
        return essay;
    }

    private MatchingQuestion buildMatchingQuestion(QuestionRequest request, User owner) {
        MatchingQuestion matching = new MatchingQuestion();
        matching.setRandomizeLeft(request.getRandomizeLeft());
        matching.setRandomizeRight(request.getRandomizeRight());
        
        if (request.getPairs() != null) {
            List<MatchingPair> pairs = request.getPairs().stream()
                    .map(p -> {
                        MatchingPair pair = new MatchingPair();
                        pair.setQuestion(matching);
                        pair.setLeftItem(p.getLeftItem());
                        pair.setRightItem(p.getRightItem());
                        pair.setOrderIndex(p.getOrderIndex());
                        pair.setLeftImageUrl(p.getLeftImageUrl());
                        pair.setRightImageUrl(p.getRightImageUrl());
                        return pair;
                    })
                    .collect(Collectors.toList());
            matching.setPairs(pairs);
        }
        
        return matching;
    }

    private OrderingQuestion buildOrderingQuestion(QuestionRequest request, User owner) {
        OrderingQuestion ordering = new OrderingQuestion();
        ordering.setAllowPartialOrder(request.getAllowPartialOrder());
        
        if (request.getItems() != null) {
            List<OrderingItem> items = request.getItems().stream()
                    .map(i -> {
                        OrderingItem item = new OrderingItem();
                        item.setQuestion(ordering);
                        item.setItemText(i.getItemText());
                        item.setCorrectOrder(i.getCorrectOrder());
                        item.setItemImageUrl(i.getItemImageUrl());
                        return item;
                    })
                    .collect(Collectors.toList());
            ordering.setItems(items);
        }
        
        return ordering;
    }

    private FillInTheBlankQuestion buildFillInBlankQuestion(QuestionRequest request, User owner) {
        FillInTheBlankQuestion question = new FillInTheBlankQuestion();
        question.setQuestionTemplate(request.getQuestionTemplate());
        question.setAcceptedAnswers(request.getAcceptedAnswers() != null ? request.getAcceptedAnswers() : new ArrayList<>());
        question.setCaseSensitive(request.getCaseSensitive());
        question.setExactMatch(request.getExactMatch());
        question.setBlankCount(request.getBlankCount());
        return question;
    }

    private void updateQuestionFromRequest(Question question, QuestionRequest request) {
        question.setQuestionText(request.getQuestionText());
        question.setDifficulty(request.getDifficulty());
        question.setChapter(request.getChapter());
        question.setTopic(request.getTopic());
        question.setExplanation(request.getExplanation());
        question.setPoints(request.getPoints());
        question.setHints(request.getHints());
        question.setIsActive(request.getIsActive());
        // Update type-specific fields based on question type
    }

    private Question duplicateQuestionEntity(Question original, QuestionDuplicateRequest request) {
        // Create a copy of the question with new ID
        // Implementation depends on question type
        return original; // Simplified
    }

    private Map<String, Long> buildTypeStatistics(List<Question> questions) {
        return questions.stream()
                .collect(Collectors.groupingBy(
                        q -> q.getQuestionType().name(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> buildDifficultyStatistics(List<Question> questions) {
        return questions.stream()
                .collect(Collectors.groupingBy(
                        q -> q.getDifficulty().name(),
                        Collectors.counting()
                ));
    }

    private QuestionResponse mapToResponse(Question question) {
        QuestionResponse.QuestionResponseBuilder builder = QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .difficulty(question.getDifficulty())
                .subjectId(question.getSubject() != null ? question.getSubject().getId() : null)
                .subjectName(question.getSubject() != null ? question.getSubject().getSubjectName() : null)
                .classId(question.getSchoolClass() != null ? question.getSchoolClass().getId() : null)
                .className(question.getSchoolClass() != null ? question.getSchoolClass().getClassName() : null)
                .chapter(question.getChapter())
                .topic(question.getTopic())
                .explanation(question.getExplanation())
                .points(question.getPoints())
                .hints(question.getHints())
                .isActive(question.getIsActive())
                .autoGradable(question.getAutoGradable())
                .allowPartialCredit(question.getAllowPartialCredit())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .bloomsLevel(question.getBloomsLevel())
                .imageUrl(question.getImageUrl())
                .videoUrl(question.getVideoUrl())
                .audioUrl(question.getAudioUrl())
                .timesUsed(question.getTimesUsed())
                .averageScore(question.getAverageScore());

        // Map type-specific fields
        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            builder.allowMultipleAnswers(mcq.getAllowMultipleAnswers())
                   .randomizeOptions(mcq.getRandomizeOptions())
                   .options(mcq.getOptions().stream()
                           .map(this::mapOptionToResponse)
                           .collect(Collectors.toList()));
        }

        return builder.build();
    }

    private QuestionResponse.QuestionOptionResponse mapOptionToResponse(QuestionOption option) {
        return QuestionResponse.QuestionOptionResponse.builder()
                .id(option.getId())
                .optionText(option.getOptionText())
                .isCorrect(option.getIsCorrect())
                .orderIndex(option.getOrderIndex())
                .partialCreditPercentage(option.getPartialCreditPercentage())
                .feedback(option.getFeedback())
                .optionImageUrl(option.getOptionImageUrl())
                .build();
    }

    private QuestionResponse.QuestionTagResponse mapTagToResponse(QuestionTag tag) {
        return QuestionResponse.QuestionTagResponse.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .description(tag.getDescription())
                .build();
    }

    @Override
    public QuestionResponse.QuestionTagResponse getTagById(Long tagId, Long ownerId) {
        QuestionTag tag = questionTagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));
        
        return QuestionResponse.QuestionTagResponse.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .description(tag.getDescription())
                .build();
    }

    @Override
    public void addQuestionsToTag(Long tagId, List<Long> questionIds, Long ownerId) {
        QuestionTag tag = questionTagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));
        
        List<Question> questions = questionRepository.findAllById(questionIds);
        
        for (Question question : questions) {
            if (!question.getTags().contains(tag)) {
                question.getTags().add(tag);
                questionRepository.save(question);
            }
        }
    }
}
