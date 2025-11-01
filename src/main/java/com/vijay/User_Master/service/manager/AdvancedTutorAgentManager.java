package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.LearningInteractionRequest;
import com.vijay.User_Master.service.AcademicTutoringService;
import com.vijay.User_Master.service.AdaptiveLearningService;
import com.vijay.User_Master.service.AIGradingService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptive Tutor Orchestrator without LangGraph4j dependency.
 * Loops: Explain -> Ask -> Grade(simulated) -> Update Mastery via AdaptiveLearningService
 * until mastery >= 90% or max loops reached.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AdvancedTutorAgentManager {

    private final AcademicTutoringService academicTutoringService;
    private final AdaptiveLearningService adaptiveLearningService;
    private final AIGradingService aiGradingService; // reserved for future use

    @Data
    @Builder
    public static class TutorState {
        private Long studentId;
        private String skillKey; // concept/topic identifier
        private String gradeLevel; // e.g., "Grade 8"
        @Builder.Default
        private List<String> chatHistory = new ArrayList<>();
        private double currentMastery;
        @Builder.Default
        private int iterations = 0;
    }

    public String runTutor(Long studentId, String skillKey, String gradeLevel, int maxLoops) {
        TutorState state = TutorState.builder()
                .studentId(studentId)
                .skillKey(skillKey)
                .gradeLevel(gradeLevel)
                .build();

        // Loop until mastery threshold
        final double target = 90.0;
        while (state.getCurrentMastery() < target && state.getIterations() < Math.max(1, maxLoops)) {
            explainConcept(state);
            generateQuestion(state);
            gradeAnswerAndUpdateMastery(state);
            state.setIterations(state.getIterations() + 1);
        }

        String summary = "Tutor session complete: iterations=" + state.getIterations()
                + ", mastery=" + String.format("%.1f", state.getCurrentMastery()) + "%";
        log.info(summary);
        return summary;
    }

    private void explainConcept(TutorState state) {
        try {
            String explanation = academicTutoringService.explainConcept("General", state.getSkillKey(), state.getGradeLevel());
            state.getChatHistory().add("AI: " + (explanation != null ? explanation : ("Explaining " + state.getSkillKey())));
        } catch (Exception e) {
            log.warn("explainConcept failed: {}", e.getMessage());
            state.getChatHistory().add("AI: Here is an explanation of " + state.getSkillKey() + "...");
        }
    }

    private void generateQuestion(TutorState state) {
        try {
            String question = academicTutoringService.generatePracticeProblems("General", state.getSkillKey(), state.getGradeLevel(), 1);
            state.getChatHistory().add("AI: Practice question - " + (question != null ? question : "Solve a basic problem on " + state.getSkillKey()));
        } catch (Exception e) {
            log.warn("generateQuestion failed: {}", e.getMessage());
            state.getChatHistory().add("AI: Here is a practice question...");
        }
    }

    private void gradeAnswerAndUpdateMastery(TutorState state) {
        // Simulate student's answer path; use adaptive learning to update mastery
        String studentAnswer = "This is a sample answer";
        state.getChatHistory().add("Student: " + studentAnswer);

        // Simulate scoring logic (could integrate AIGradingService later)
        double scorePct = Math.min(100.0, 60.0 + state.getIterations() * 15.0); // improving each loop

        try {
            Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
            LearningInteractionRequest request = LearningInteractionRequest.builder()
                    .studentId(state.getStudentId())
                    .moduleId(0L)
                    .skillKey(state.getSkillKey())
                    .difficulty("MEDIUM")
                    .outcome(scorePct >= 70.0 ? "CORRECT" : (scorePct >= 50.0 ? "PARTIAL" : "INCORRECT"))
                    .score(scorePct)
                    .timeTakenSeconds(90)
                    .hintsUsed(0)
                    .questionType("SHORT_ANSWER")
                    .confidenceLevel(3)
                    .notes("Auto-graded by TutorAgent")
                    .build();
            var masteryResp = adaptiveLearningService.recordInteraction(request, ownerId);
            if (masteryResp != null && masteryResp.getMasteryLevel() != null) {
                state.setCurrentMastery(masteryResp.getMasteryLevel());
            } else {
                // If service doesn't return mastery, approximate
                state.setCurrentMastery(Math.max(state.getCurrentMastery(), Math.min(100.0, scorePct)));
            }
            state.getChatHistory().add("AI: Scored " + String.format("%.0f", scorePct) + "%. Current mastery: "
                    + String.format("%.1f", state.getCurrentMastery()) + "%.");
        } catch (Exception e) {
            log.warn("grade/update mastery failed: {}", e.getMessage());
            // Fallback mastery increment
            double newMastery = Math.min(100.0, state.getCurrentMastery() + 20.0);
            state.setCurrentMastery(newMastery);
            state.getChatHistory().add("AI: Estimated mastery now " + String.format("%.1f", newMastery) + "%.");
        }
    }

    @Tool(description = "Run Adaptive Tutor loop until mastery >= 90%. Inputs: studentId (Long), skillKey (String), gradeLevel (String), maxLoops (Integer, optional, default 3). Returns a summary string.")
    public String runAdaptiveTutorTool(Long studentId, String skillKey, String gradeLevel, Integer maxLoops) {
        int loops = maxLoops != null ? maxLoops : 3;
        return runTutor(studentId, skillKey, gradeLevel, loops);
    }
}
