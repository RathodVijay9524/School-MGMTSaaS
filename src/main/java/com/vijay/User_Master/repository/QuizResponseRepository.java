package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for QuizResponse entity
 */
@Repository
public interface QuizResponseRepository extends JpaRepository<QuizResponse, Long> {

    // Find by attempt
    List<QuizResponse> findByAttemptIdAndIsDeletedFalse(Long attemptId);

    // Find by question
    List<QuizResponse> findByQuestionIdAndIsDeletedFalse(Long questionId);

    // Find by attempt and question
    Optional<QuizResponse> findByAttemptIdAndQuestionIdAndIsDeletedFalse(Long attemptId, Long questionId);

    // Find correct responses
    List<QuizResponse> findByAttemptIdAndIsCorrectTrueAndIsDeletedFalse(Long attemptId);

    // Find incorrect responses
    List<QuizResponse> findByAttemptIdAndIsCorrectFalseAndIsDeletedFalse(Long attemptId);

    // Find flagged responses
    List<QuizResponse> findByAttemptIdAndIsFlaggedTrueAndIsDeletedFalse(Long attemptId);

    // Find responses needing manual grading
    @Query("SELECT qr FROM QuizResponse qr WHERE qr.attempt.id = :attemptId " +
           "AND qr.autoGraded = false AND qr.gradedBy IS NULL AND qr.isDeleted = false")
    List<QuizResponse> findPendingManualGrading(@Param("attemptId") Long attemptId);

    // Statistics for a question
    @Query("SELECT COUNT(qr) FROM QuizResponse qr WHERE qr.question.id = :questionId " +
           "AND qr.isCorrect = true AND qr.isDeleted = false")
    Long countCorrectResponses(@Param("questionId") Long questionId);

    @Query("SELECT AVG(qr.pointsEarned) FROM QuizResponse qr WHERE qr.question.id = :questionId " +
           "AND qr.isDeleted = false")
    Double getAverageScore(@Param("questionId") Long questionId);

    // Count responses by attempt
    @Query("SELECT COUNT(qr) FROM QuizResponse qr WHERE qr.attempt.id = :attemptId AND qr.isDeleted = false")
    Long countByAttemptId(@Param("attemptId") Long attemptId);
}
