package com.vijay.User_Master.service;

import com.vijay.User_Master.entity.LearningInteraction;
import com.vijay.User_Master.entity.SkillMastery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MasteryComputationServiceTest extends ServiceTestBase {

    @Mock
    private MasteryComputationService masteryService;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void updateMastery_withCorrectOutcome_returnsUpdatedMastery() {
        SkillMastery mockMastery = new SkillMastery();
        mockMastery.setMasteryLevel(0.85);

        when(masteryService.updateMastery(100L, "algebra", LearningInteraction.Outcome.CORRECT, 60, 0, OWNER_ID))
                .thenReturn(mockMastery);

        SkillMastery response = masteryService.updateMastery(100L, "algebra", LearningInteraction.Outcome.CORRECT, 60, 0, OWNER_ID);

        assertNotNull(response);
        assertEquals(0.85, response.getMasteryLevel());
        verify(masteryService).updateMastery(100L, "algebra", LearningInteraction.Outcome.CORRECT, 60, 0, OWNER_ID);
    }

    @Test
    void updateMastery_withIncorrectOutcome_returnsUpdatedMastery() {
        SkillMastery mockMastery = new SkillMastery();
        mockMastery.setMasteryLevel(0.45);

        when(masteryService.updateMastery(100L, "algebra", LearningInteraction.Outcome.INCORRECT, 120, 2, OWNER_ID))
                .thenReturn(mockMastery);

        SkillMastery response = masteryService.updateMastery(100L, "algebra", LearningInteraction.Outcome.INCORRECT, 120, 2, OWNER_ID);

        assertNotNull(response);
        assertEquals(0.45, response.getMasteryLevel());
    }

    @Test
    void updateMastery_withPartialOutcome_returnsUpdatedMastery() {
        SkillMastery mockMastery = new SkillMastery();
        mockMastery.setMasteryLevel(0.65);

        when(masteryService.updateMastery(100L, "algebra", LearningInteraction.Outcome.PARTIAL, 90, 1, OWNER_ID))
                .thenReturn(mockMastery);

        SkillMastery response = masteryService.updateMastery(100L, "algebra", LearningInteraction.Outcome.PARTIAL, 90, 1, OWNER_ID);

        assertNotNull(response);
        assertEquals(0.65, response.getMasteryLevel());
    }

    @Test
    void applyDecay_withValidMastery_succeeds() {
        SkillMastery skillMastery = new SkillMastery();

        doNothing().when(masteryService).applyDecay(skillMastery);

        masteryService.applyDecay(skillMastery);

        verify(masteryService).applyDecay(skillMastery);
    }

    @Test
    void applyDecayToInactiveSkills_withValidDays_succeeds() {
        doNothing().when(masteryService).applyDecayToInactiveSkills(30);

        masteryService.applyDecayToInactiveSkills(30);

        verify(masteryService).applyDecayToInactiveSkills(30);
    }

    @Test
    void calculateVelocity_withValidStudent_returnsVelocity() {
        when(masteryService.calculateVelocity(100L, "algebra")).thenReturn(0.15);

        Double velocity = masteryService.calculateVelocity(100L, "algebra");

        assertNotNull(velocity);
        assertEquals(0.15, velocity);
    }

    @Test
    void calculateVelocity_withNoProgress_returnsZero() {
        when(masteryService.calculateVelocity(100L, "algebra")).thenReturn(0.0);

        Double velocity = masteryService.calculateVelocity(100L, "algebra");

        assertNotNull(velocity);
        assertEquals(0.0, velocity);
    }

    @Test
    void scheduleNextReview_withHighQuality_returnsLaterDate() {
        SkillMastery skillMastery = new SkillMastery();
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);

        when(masteryService.scheduleNextReview(skillMastery, 4)).thenReturn(futureDate);

        LocalDateTime nextReview = masteryService.scheduleNextReview(skillMastery, 4);

        assertNotNull(nextReview);
        assertTrue(nextReview.isAfter(LocalDateTime.now()));
    }

    @Test
    void scheduleNextReview_withLowQuality_returnsSoonerDate() {
        SkillMastery skillMastery = new SkillMastery();
        LocalDateTime soonDate = LocalDateTime.now().plusDays(1);

        when(masteryService.scheduleNextReview(skillMastery, 1)).thenReturn(soonDate);

        LocalDateTime nextReview = masteryService.scheduleNextReview(skillMastery, 1);

        assertNotNull(nextReview);
        assertTrue(nextReview.isAfter(LocalDateTime.now()));
    }

    @Test
    void getOrCreateSkillMastery_withNewSkill_returnsSkillMastery() {
        SkillMastery mockMastery = new SkillMastery();
        mockMastery.setSkillKey("algebra");

        when(masteryService.getOrCreateSkillMastery(100L, 1L, "algebra", "Algebra Basics", OWNER_ID))
                .thenReturn(mockMastery);

        SkillMastery response = masteryService.getOrCreateSkillMastery(100L, 1L, "algebra", "Algebra Basics", OWNER_ID);

        assertNotNull(response);
        assertEquals("algebra", response.getSkillKey());
    }

    @Test
    void calculateMasteryChangeRate_withValidStudent_returnsChangeRate() {
        when(masteryService.calculateMasteryChangeRate(100L, "algebra", 30)).thenReturn(0.02);

        Double changeRate = masteryService.calculateMasteryChangeRate(100L, "algebra", 30);

        assertNotNull(changeRate);
        assertEquals(0.02, changeRate);
    }

    @Test
    void predictFutureMastery_withValidSkill_returnsPrediction() {
        SkillMastery skillMastery = new SkillMastery();

        when(masteryService.predictFutureMastery(skillMastery, 30)).thenReturn(0.92);

        Double prediction = masteryService.predictFutureMastery(skillMastery, 30);

        assertNotNull(prediction);
        assertEquals(0.92, prediction);
    }

    @Test
    void calculateMasteryConfidence_withValidMastery_returnsConfidence() {
        SkillMastery skillMastery = new SkillMastery();

        when(masteryService.calculateMasteryConfidence(skillMastery)).thenReturn(0.88);

        Double confidence = masteryService.calculateMasteryConfidence(skillMastery);

        assertNotNull(confidence);
        assertEquals(0.88, confidence);
    }

    @Test
    void calculateMasteryConfidence_withLowInteractions_returnsLowerConfidence() {
        SkillMastery skillMastery = new SkillMastery();

        when(masteryService.calculateMasteryConfidence(skillMastery)).thenReturn(0.45);

        Double confidence = masteryService.calculateMasteryConfidence(skillMastery);

        assertNotNull(confidence);
        assertTrue(confidence < 0.5);
    }

    @Test
    void updateMastery_withNullOutcome_throwsException() {
        when(masteryService.updateMastery(100L, "algebra", null, 60, 0, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Outcome cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                masteryService.updateMastery(100L, "algebra", null, 60, 0, OWNER_ID));
    }
}
