package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.AIGradingRequest;
import com.vijay.User_Master.dto.AIGradingResponse;
import com.vijay.User_Master.dto.PeerReviewAssignmentRequest;
import com.vijay.User_Master.dto.PeerReviewAssignmentResponse;
import com.vijay.User_Master.dto.PeerReviewResponse;
import com.vijay.User_Master.service.AIGradingService;
import com.vijay.User_Master.service.PeerReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeerReviewAgentManagerTest {

    @Mock
    private PeerReviewService peerReviewService;

    @Mock
    private AIGradingService aiGradingService;

    @InjectMocks
    private PeerReviewAgentManager manager;

    @Test
    void runPeerReviewWorkflow_returnsErrorWhenAssignmentIdMissing() {
        String result = manager.runPeerReviewWorkflow(null, 3, 2, 0.2, true, 10L);

        assertEquals("assignmentId is required", result);
        verifyNoInteractions(peerReviewService, aiGradingService);
    }

    @Test
    void runPeerReviewWorkflow_runsHappyPathWithAutoGrading() {
        long ownerId = 321L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);

            PeerReviewAssignmentResponse assignmentResp = new PeerReviewAssignmentResponse();
            assignmentResp.setTotalSubmissions(4);
            when(peerReviewService.assignPeerReviews(any(PeerReviewAssignmentRequest.class), eq(ownerId))).thenReturn(assignmentResp);

            PeerReviewResponse review1 = PeerReviewResponse.builder()
                    .submissionId(11L)
                    .reviewerId(100L)
                    .reviewComments("Too short")
                    .build();
            PeerReviewResponse review2 = PeerReviewResponse.builder()
                    .submissionId(12L)
                    .reviewerId(100L)
                    .reviewComments("ok")
                    .build();
            PeerReviewResponse review3 = PeerReviewResponse.builder()
                    .submissionId(13L)
                    .reviewerId(200L)
                    .reviewComments("Detailed feedback with sufficient words")
                    .build();
            when(peerReviewService.getPeerReviewsByAssignment(55L, ownerId))
                    .thenReturn(List.of(review1, review2, review3));

            when(aiGradingService.batchGradeSubmissions(anyList(), eq(99L), eq(ownerId)))
                    .thenReturn(List.of(new AIGradingResponse(), new AIGradingResponse()))
                    .thenReturn(null);

            String report = manager.runPeerReviewWorkflow(55L, 3, 2, 0.5, true, 99L);

            assertTrue(report.contains("Assignment ID: 55"));
            assertTrue(report.contains("Reviews collected: 3"));
            assertTrue(report.contains("Lazy reviewers flagged: 1"));
            assertTrue(report.contains("AI auto-graded essays: 2"));

            ArgumentCaptor<PeerReviewAssignmentRequest> requestCaptor = ArgumentCaptor.forClass(PeerReviewAssignmentRequest.class);
            verify(peerReviewService).assignPeerReviews(requestCaptor.capture(), eq(ownerId));
            PeerReviewAssignmentRequest captured = requestCaptor.getValue();
            assertEquals(3, captured.getReviewsPerSubmission());
            assertTrue(Boolean.TRUE.equals(captured.getRandomAssignment()));
            assertTrue(Boolean.TRUE.equals(captured.getAnonymousReview()));
            assertTrue(Boolean.FALSE.equals(captured.getAllowSelfReview()));

            verify(aiGradingService).batchGradeSubmissions(List.of(11L, 12L, 13L), 99L, ownerId);
            verify(aiGradingService, never()).gradeSubmission(any(AIGradingRequest.class), any());
        }
    }

    @Test
    void runPeerReviewWorkflow_skipsAutoGradeWhenDisabled() {
        long ownerId = 789L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(ownerId);

        try (MockedStatic<CommonUtils> utilities = Mockito.mockStatic(CommonUtils.class)) {
            utilities.when(CommonUtils::getLoggedInUser).thenReturn(userDetails);

            when(peerReviewService.assignPeerReviews(any(PeerReviewAssignmentRequest.class), eq(ownerId)))
                    .thenReturn(new PeerReviewAssignmentResponse());
            when(peerReviewService.getPeerReviewsByAssignment(77L, ownerId))
                    .thenReturn(List.of(PeerReviewResponse.builder()
                            .submissionId(900L)
                            .reviewerId(1L)
                            .reviewComments("Adequate feedback")
                            .build()));

            String report = manager.runPeerReviewWorkflow(77L, 2, 2, 0.5, false, 88L);

            assertTrue(report.contains("AI auto-graded essays: 0"));
            verify(aiGradingService, never()).batchGradeSubmissions(anyList(), any(), any());
            verify(aiGradingService, never()).gradeSubmission(any(AIGradingRequest.class), any());
        }
    }
}

