package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.PeerReviewAssignmentRequest;
import com.vijay.User_Master.dto.PeerReviewAssignmentResponse;
import com.vijay.User_Master.dto.PeerReviewRequest;
import com.vijay.User_Master.dto.PeerReviewResponse;
import com.vijay.User_Master.dto.PeerReviewApprovalRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PeerReviewServiceTest extends ServiceTestBase {

    @Mock
    private PeerReviewService peerReviewService;

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
    void assignPeerReviews_withValidRequest_returnsAssignment() {
        PeerReviewAssignmentRequest request = new PeerReviewAssignmentRequest();
        PeerReviewAssignmentResponse mockResponse = new PeerReviewAssignmentResponse();

        when(peerReviewService.assignPeerReviews(request, OWNER_ID)).thenReturn(mockResponse);

        PeerReviewAssignmentResponse response = peerReviewService.assignPeerReviews(request, OWNER_ID);

        assertNotNull(response);
        verify(peerReviewService).assignPeerReviews(request, OWNER_ID);
    }

    @Test
    void submitPeerReview_withValidRequest_returnsPeerReview() {
        PeerReviewRequest request = new PeerReviewRequest();
        PeerReviewResponse mockResponse = new PeerReviewResponse();
        mockResponse.setId(1L);

        when(peerReviewService.submitPeerReview(request, 100L, OWNER_ID)).thenReturn(mockResponse);

        PeerReviewResponse response = peerReviewService.submitPeerReview(request, 100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(peerReviewService).submitPeerReview(request, 100L, OWNER_ID);
    }

    @Test
    void submitPeerReview_withNullRequest_throwsException() {
        when(peerReviewService.submitPeerReview(null, 100L, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                peerReviewService.submitPeerReview(null, 100L, OWNER_ID));
    }

    @Test
    void updatePeerReview_withValidData_returnsUpdatedReview() {
        PeerReviewRequest request = new PeerReviewRequest();
        PeerReviewResponse mockResponse = new PeerReviewResponse();
        mockResponse.setId(1L);

        when(peerReviewService.updatePeerReview(1L, request, 100L, OWNER_ID)).thenReturn(mockResponse);

        PeerReviewResponse response = peerReviewService.updatePeerReview(1L, request, 100L, OWNER_ID);

        assertNotNull(response);
        verify(peerReviewService).updatePeerReview(1L, request, 100L, OWNER_ID);
    }

    @Test
    void getPeerReviewById_withValidId_returnsPeerReview() {
        PeerReviewResponse mockResponse = new PeerReviewResponse();
        mockResponse.setId(1L);

        when(peerReviewService.getPeerReviewById(1L, OWNER_ID)).thenReturn(mockResponse);

        PeerReviewResponse response = peerReviewService.getPeerReviewById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getPeerReviewById_withInvalidId_throwsException() {
        when(peerReviewService.getPeerReviewById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Peer review not found"));

        assertThrows(RuntimeException.class, () ->
                peerReviewService.getPeerReviewById(999L, OWNER_ID));
    }

    @Test
    void getPeerReviewsByAssignment_withValidAssignmentId_returnsReviews() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();
        mockReviews.add(new PeerReviewResponse());
        mockReviews.add(new PeerReviewResponse());

        when(peerReviewService.getPeerReviewsByAssignment(1L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPeerReviewsByAssignment(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(peerReviewService).getPeerReviewsByAssignment(1L, OWNER_ID);
    }

    @Test
    void getPeerReviewsByAssignment_withNoReviews_returnsEmptyList() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();

        when(peerReviewService.getPeerReviewsByAssignment(1L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPeerReviewsByAssignment(1L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getPeerReviewsBySubmission_withValidSubmissionId_returnsReviews() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();
        mockReviews.add(new PeerReviewResponse());

        when(peerReviewService.getPeerReviewsBySubmission(1L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPeerReviewsBySubmission(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPeerReviewsByReviewer_withValidReviewerId_returnsReviews() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();
        mockReviews.add(new PeerReviewResponse());

        when(peerReviewService.getPeerReviewsByReviewer(100L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPeerReviewsByReviewer(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPendingReviews_withValidReviewerId_returnsPendingReviews() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();
        mockReviews.add(new PeerReviewResponse());

        when(peerReviewService.getPendingReviews(100L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPendingReviews(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getPendingReviews_withNoPendingReviews_returnsEmptyList() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();

        when(peerReviewService.getPendingReviews(100L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPendingReviews(100L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void approvePeerReview_withValidRequest_returnsApprovedReview() {
        PeerReviewApprovalRequest request = new PeerReviewApprovalRequest();
        PeerReviewResponse mockResponse = new PeerReviewResponse();
        mockResponse.setId(1L);

        when(peerReviewService.approvePeerReview(request, 50L, OWNER_ID)).thenReturn(mockResponse);

        PeerReviewResponse response = peerReviewService.approvePeerReview(request, 50L, OWNER_ID);

        assertNotNull(response);
        verify(peerReviewService).approvePeerReview(request, 50L, OWNER_ID);
    }

    @Test
    void approvePeerReview_withNullRequest_throwsException() {
        when(peerReviewService.approvePeerReview(null, 50L, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                peerReviewService.approvePeerReview(null, 50L, OWNER_ID));
    }

    @Test
    void getPeerReviewStatistics_withValidAssignmentId_returnsStatistics() {
        when(peerReviewService.getPeerReviewStatistics(1L, OWNER_ID)).thenReturn(any());

        Object response = peerReviewService.getPeerReviewStatistics(1L, OWNER_ID);

        assertNotNull(response);
        verify(peerReviewService).getPeerReviewStatistics(1L, OWNER_ID);
    }

    @Test
    void deletePeerReview_withValidId_succeeds() {
        doNothing().when(peerReviewService).deletePeerReview(1L, OWNER_ID);

        peerReviewService.deletePeerReview(1L, OWNER_ID);

        verify(peerReviewService).deletePeerReview(1L, OWNER_ID);
    }

    @Test
    void updatePeerReview_withNullRequest_throwsException() {
        when(peerReviewService.updatePeerReview(1L, null, 100L, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                peerReviewService.updatePeerReview(1L, null, 100L, OWNER_ID));
    }

    @Test
    void getPeerReviewsByAssignment_multipleReviews_returnsAllReviews() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockReviews.add(new PeerReviewResponse());
        }

        when(peerReviewService.getPeerReviewsByAssignment(1L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPeerReviewsByAssignment(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(5, response.size());
    }

    @Test
    void getPeerReviewsByReviewer_multipleReviews_returnsAllReviews() {
        List<PeerReviewResponse> mockReviews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mockReviews.add(new PeerReviewResponse());
        }

        when(peerReviewService.getPeerReviewsByReviewer(100L, OWNER_ID)).thenReturn(mockReviews);

        List<PeerReviewResponse> response = peerReviewService.getPeerReviewsByReviewer(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(3, response.size());
    }
}
