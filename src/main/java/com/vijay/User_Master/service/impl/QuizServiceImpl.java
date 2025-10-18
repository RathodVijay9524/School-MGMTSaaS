package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.AutoGradingRequest;
import com.vijay.User_Master.dto.AutoGradingResponse;
import com.vijay.User_Master.dto.BatchGradingRequest;
import com.vijay.User_Master.dto.BatchGradingResponse;
import com.vijay.User_Master.dto.ManualGradingRequest;
import com.vijay.User_Master.dto.QuizAttemptRequest;
import com.vijay.User_Master.dto.QuizAttemptResponse;
import com.vijay.User_Master.dto.QuizCloneRequest;
import com.vijay.User_Master.dto.QuizPublishRequest;
import com.vijay.User_Master.dto.QuizRequest;
import com.vijay.User_Master.dto.QuizResponse;
import com.vijay.User_Master.dto.QuizReviewResponse;
import com.vijay.User_Master.dto.QuizStatisticsResponse;
import com.vijay.User_Master.dto.StudentQuizSummaryResponse;
import com.vijay.User_Master.dto.TeacherQuizDashboardResponse;
import com.vijay.User_Master.entity.AttemptStatus;
import com.vijay.User_Master.entity.Quiz;
import com.vijay.User_Master.entity.QuizAttempt;
import com.vijay.User_Master.entity.Question;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.AutoGradingService;
import com.vijay.User_Master.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of QuizService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizResponseRepository quizResponseRepository;
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final AutoGradingService autoGradingService;

    @Override
    public QuizResponse createQuiz(QuizRequest request, Long ownerId) {
        log.info("Creating quiz for owner: {}", ownerId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .quizType(request.getQuizType())
                .randomizeQuestions(request.getRandomizeQuestions())
                .questionsToShow(request.getQuestionsToShow())
                .timeLimitMinutes(request.getTimeLimitMinutes())
                .showTimer(request.getShowTimer())
                .maxAttempts(request.getMaxAttempts())
                .allowReview(request.getAllowReview())
                .totalPoints(request.getTotalPoints())
                .passingScore(request.getPassingScore())
                .autoGrade(request.getAutoGrade())
                .availableFrom(request.getAvailableFrom())
                .availableUntil(request.getAvailableUntil())
                .showCorrectAnswers(request.getShowCorrectAnswers())
                .showScoreImmediately(request.getShowScoreImmediately())
                .showFeedback(request.getShowFeedback())
                .showOneQuestionAtTime(request.getShowOneQuestionAtTime())
                .requireProctoring(request.getRequireProctoring())
                .preventCopyPaste(request.getPreventCopyPaste())
                .fullScreenMode(request.getFullScreenMode())
                .shuffleAnswers(request.getShuffleAnswers())
                .lockQuestionsAfterAnswering(request.getLockQuestionsAfterAnswering())
                .instructions(request.getInstructions())
                .isActive(request.getIsActive())
                .isPublished(request.getIsPublished())
                .owner(owner)
                .build();

        if (request.getSubjectId() != null) {
            subjectRepository.findById(request.getSubjectId()).ifPresent(quiz::setSubject);
        }

        if (request.getClassId() != null) {
            schoolClassRepository.findById(request.getClassId()).ifPresent(quiz::setSchoolClass);
        }

        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
            quiz.setQuestions(questions);
            quiz.calculateTotalPoints();
        }

        Quiz saved = quizRepository.save(quiz);
        log.info("Quiz created with ID: {}", saved.getId());
        
        return mapToResponse(saved);
    }

    @Override
    public QuizResponse updateQuiz(Long id, QuizRequest request, Long ownerId) {
        Quiz quiz = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setQuizType(request.getQuizType());
        quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
        quiz.setMaxAttempts(request.getMaxAttempts());
        quiz.setPassingScore(request.getPassingScore());
        quiz.setAvailableFrom(request.getAvailableFrom());
        quiz.setAvailableUntil(request.getAvailableUntil());
        quiz.setInstructions(request.getInstructions());

        Quiz updated = quizRepository.save(quiz);
        return mapToResponse(updated);
    }

    @Override
    public QuizResponse getQuizById(Long id, Long ownerId) {
        Quiz quiz = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return mapToResponse(quiz);
    }

    @Override
    public void deleteQuiz(Long id, Long ownerId) {
        Quiz quiz = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setIsDeleted(true);
        quizRepository.save(quiz);
    }

    @Override
    public List<QuizResponse> getAllQuizzes(Long ownerId) {
        return quizRepository.findByOwnerIdAndIsDeletedFalse(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuizResponse> getQuizzesPaginated(Long ownerId, Pageable pageable) {
        return quizRepository.findByOwnerIdAndIsDeletedFalse(ownerId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public List<QuizResponse> getQuizzesBySubject(Long subjectId, Long ownerId) {
        return quizRepository.findBySubjectIdAndIsDeletedFalse(subjectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizResponse> getQuizzesByClass(Long classId, Long ownerId) {
        return quizRepository.findBySchoolClassIdAndIsDeletedFalse(classId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizResponse> getAvailableQuizzes(Long studentId, Long ownerId) {
        return quizRepository.findAvailableQuizzes(ownerId, LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuizResponse publishQuiz(QuizPublishRequest request, Long ownerId) {
        Quiz quiz = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(request.getQuizId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setIsPublished(request.getPublish());
        if (request.getAvailableFrom() != null) {
            quiz.setAvailableFrom(request.getAvailableFrom());
        }
        if (request.getAvailableUntil() != null) {
            quiz.setAvailableUntil(request.getAvailableUntil());
        }

        Quiz updated = quizRepository.save(quiz);
        return mapToResponse(updated);
    }

    @Override
    public QuizResponse cloneQuiz(QuizCloneRequest request, Long ownerId) {
        Quiz original = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(request.getQuizId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Quiz clone = Quiz.builder()
                .title(request.getNewTitle() != null ? request.getNewTitle() : original.getTitle() + " (Copy)")
                .description(original.getDescription())
                .quizType(original.getQuizType())
                .randomizeQuestions(original.getRandomizeQuestions())
                .questionsToShow(original.getQuestionsToShow())
                .timeLimitMinutes(original.getTimeLimitMinutes())
                .maxAttempts(original.getMaxAttempts())
                .passingScore(original.getPassingScore())
                .instructions(original.getInstructions())
                .isActive(true)
                .isPublished(request.getPublishImmediately() != null ? request.getPublishImmediately() : false)
                .owner(original.getOwner())
                .build();

        if (request.getIncludeQuestions()) {
            clone.setQuestions(new ArrayList<>(original.getQuestions()));
        }

        Quiz saved = quizRepository.save(clone);
        return mapToResponse(saved);
    }

    @Override
    public QuizAttemptResponse startQuizAttempt(Long quizId, Long studentId, Long ownerId) {
        Quiz quiz = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(quizId, ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Worker student = workerRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if quiz is available
        if (!quiz.isAvailable()) {
            throw new RuntimeException("Quiz is not available");
        }

        // Check attempt limit
        Long attemptCount = quizAttemptRepository.countAttemptsByStudentAndQuiz(quizId, studentId);
        if (quiz.getMaxAttempts() != null && attemptCount >= quiz.getMaxAttempts()) {
            throw new RuntimeException("Maximum attempts reached");
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .attemptNumber(attemptCount.intValue() + 1)
                .status(AttemptStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .owner(quiz.getOwner())
                .build();

        QuizAttempt saved = quizAttemptRepository.save(attempt);
        log.info("Quiz attempt started: {}", saved.getId());

        return mapAttemptToResponse(saved);
    }

    @Override
    public QuizAttemptResponse submitQuizAttempt(QuizAttemptRequest request, Long studentId, Long ownerId) {
        Quiz quiz = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(request.getQuizId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizAttempt attempt = quizAttemptRepository.findLatestAttempt(request.getQuizId(), studentId)
                .orElseThrow(() -> new RuntimeException("No active attempt found"));

        if (!attempt.isInProgress()) {
            throw new RuntimeException("Attempt already submitted");
        }

        // Save responses
        for (QuizAttemptRequest.QuizAnswerRequest answer : request.getAnswers()) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            com.vijay.User_Master.entity.QuizResponse response = com.vijay.User_Master.entity.QuizResponse.builder()
                    .attempt(attempt)
                    .question(question)
                    .studentAnswer(answer.getAnswer())
                    .timeSpentSeconds(answer.getTimeSpentSeconds())
                    .isFlagged(answer.getIsFlagged())
                    .maxPoints(question.getPoints())
                    .build();

            // Auto-grade if enabled
            if (quiz.getAutoGrade() && question.getAutoGradable()) {
                AutoGradingResponse grading = autoGradingService.gradeResponse(
                        AutoGradingRequest.builder()
                                .questionId(question.getId())
                                .studentAnswer(answer.getAnswer())
                                .build()
                );

                response.setIsCorrect(grading.getIsCorrect());
                response.setPointsEarned(grading.getPointsEarned());
                response.setFeedback(grading.getFeedback());
                response.setAutoGraded(true);
                response.setGradedAt(LocalDateTime.now());
            }

            attempt.addResponse(response);
        }

        // Update attempt
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setTimeSpentSeconds(request.getTimeSpentSeconds());
        attempt.setIpAddress(request.getIpAddress());
        attempt.setTabSwitchCount(request.getTabSwitchCount());
        attempt.setCopyPasteAttempts(request.getCopyPasteAttempts());

        // Calculate score
        attempt.calculateScore();
        attempt.checkPassed(quiz.getPassingScore());

        QuizAttempt saved = quizAttemptRepository.save(attempt);
        log.info("Quiz attempt submitted: {}", saved.getId());

        return mapAttemptToResponse(saved);
    }

    @Override
    public QuizAttemptResponse getQuizAttempt(Long attemptId, Long ownerId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdAndOwnerIdAndIsDeletedFalse(attemptId, ownerId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        return mapAttemptToResponse(attempt);
    }

    @Override
    public List<QuizAttemptResponse> getQuizAttempts(Long quizId, Long ownerId) {
        return quizAttemptRepository.findByQuizIdAndIsDeletedFalse(quizId).stream()
                .map(this::mapAttemptToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizAttemptResponse> getStudentAttempts(Long quizId, Long studentId, Long ownerId) {
        return quizAttemptRepository.findByQuizIdAndStudentIdAndIsDeletedFalse(quizId, studentId).stream()
                .map(this::mapAttemptToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuizReviewResponse getQuizReview(Long attemptId, Long studentId, Long ownerId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdAndOwnerIdAndIsDeletedFalse(attemptId, ownerId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (!attempt.isCompleted()) {
            throw new RuntimeException("Attempt not completed yet");
        }

        List<QuizReviewResponse.QuestionReview> questionReviews = attempt.getResponses().stream()
                .map(this::mapToQuestionReview)
                .collect(Collectors.toList());

        return QuizReviewResponse.builder()
                .attemptId(attempt.getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .totalScore(attempt.getTotalScore())
                .maxScore(attempt.getMaxScore())
                .percentage(attempt.getPercentage())
                .passed(attempt.getPassed())
                .questions(questionReviews)
                .build();
    }

    @Override
    public QuizStatisticsResponse getQuizStatistics(Long quizId, Long ownerId) {
        Quiz quiz = quizRepository.findByIdAndOwnerIdAndIsDeletedFalse(quizId, ownerId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Long totalAttempts = (long) quizAttemptRepository.findByQuizIdAndIsDeletedFalse(quizId).size();
        Long completedAttempts = quizAttemptRepository.countCompletedAttempts(quizId);
        Long uniqueStudents = quizAttemptRepository.countUniqueStudents(quizId);
        Double averageScore = quizAttemptRepository.getAverageScore(quizId);

        return QuizStatisticsResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .totalAttempts(totalAttempts)
                .completedAttempts(completedAttempts)
                .uniqueStudents(uniqueStudents)
                .averageScore(averageScore)
                .build();
    }

    @Override
    public StudentQuizSummaryResponse getStudentSummary(Long studentId, Long ownerId) {
        // Implementation for student dashboard
        return StudentQuizSummaryResponse.builder()
                .studentId(studentId)
                .build();
    }

    @Override
    public TeacherQuizDashboardResponse getTeacherDashboard(Long teacherId, Long ownerId) {
        // Implementation for teacher dashboard
        return TeacherQuizDashboardResponse.builder()
                .teacherId(teacherId)
                .build();
    }

    @Override
    public void manualGradeResponse(ManualGradingRequest request, Long teacherId, Long ownerId) {
        com.vijay.User_Master.entity.QuizResponse response = quizResponseRepository.findById(request.getResponseId())
                .orElseThrow(() -> new RuntimeException("Response not found"));

        Worker teacher = workerRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        response.setPointsEarned(request.getPointsEarned());
        response.setFeedback(request.getFeedback());
        response.setIsCorrect(request.getIsCorrect());
        response.setGradedBy(teacher);
        response.setGradedAt(LocalDateTime.now());

        quizResponseRepository.save(response);

        // Recalculate attempt score
        QuizAttempt attempt = response.getAttempt();
        attempt.calculateScore();
        quizAttemptRepository.save(attempt);
    }

    @Override
    public BatchGradingResponse batchGradeAttempts(BatchGradingRequest request, Long teacherId, Long ownerId) {
        List<BatchGradingResponse.GradingResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (Long attemptId : request.getAttemptIds()) {
            try {
                QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                        .orElseThrow(() -> new RuntimeException("Attempt not found"));

                attempt.setStatus(AttemptStatus.GRADED);
                quizAttemptRepository.save(attempt);

                results.add(BatchGradingResponse.GradingResult.builder()
                        .attemptId(attemptId)
                        .success(true)
                        .totalScore(attempt.getTotalScore())
                        .percentage(attempt.getPercentage())
                        .passed(attempt.getPassed())
                        .build());
                successCount++;
            } catch (Exception e) {
                results.add(BatchGradingResponse.GradingResult.builder()
                        .attemptId(attemptId)
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build());
                failCount++;
            }
        }

        return BatchGradingResponse.builder()
                .totalAttempts(request.getAttemptIds().size())
                .successfullyGraded(successCount)
                .failedGrading(failCount)
                .results(results)
                .build();
    }

    // Helper methods
    private QuizResponse mapToResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .subjectId(quiz.getSubject() != null ? quiz.getSubject().getId() : null)
                .subjectName(quiz.getSubject() != null ? quiz.getSubject().getSubjectName() : null)
                .classId(quiz.getSchoolClass() != null ? quiz.getSchoolClass().getId() : null)
                .className(quiz.getSchoolClass() != null ? quiz.getSchoolClass().getClassName() : null)
                .quizType(quiz.getQuizType())
                .questionCount(quiz.getQuestionCount())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .maxAttempts(quiz.getMaxAttempts())
                .totalPoints(quiz.getTotalPoints())
                .passingScore(quiz.getPassingScore())
                .isActive(quiz.getIsActive())
                .isPublished(quiz.getIsPublished())
                .isAvailable(quiz.isAvailable())
                .build();
    }

    private QuizAttemptResponse mapAttemptToResponse(QuizAttempt attempt) {
        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .studentId(attempt.getStudent().getId())
                .studentName(attempt.getStudent().getName())
                .attemptNumber(attempt.getAttemptNumber())
                .status(attempt.getStatus())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .timeSpentSeconds(attempt.getTimeSpentSeconds())
                .totalScore(attempt.getTotalScore())
                .maxScore(attempt.getMaxScore())
                .percentage(attempt.getPercentage())
                .passed(attempt.getPassed())
                .build();
    }

    private QuizReviewResponse.QuestionReview mapToQuestionReview(com.vijay.User_Master.entity.QuizResponse response) {
        return QuizReviewResponse.QuestionReview.builder()
                .questionId(response.getQuestion().getId())
                .questionText(response.getQuestion().getQuestionText())
                .studentAnswer(response.getStudentAnswer())
                .correctAnswer(response.getCorrectAnswer())
                .isCorrect(response.getIsCorrect())
                .pointsEarned(response.getPointsEarned())
                .maxPoints(response.getMaxPoints())
                .feedback(response.getFeedback())
                .build();
    }
}
