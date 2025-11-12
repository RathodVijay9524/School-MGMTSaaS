package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AnnouncementRequest;
import com.vijay.User_Master.dto.AnnouncementResponse;
import com.vijay.User_Master.dto.AnnouncementStatistics;
import com.vijay.User_Master.entity.Announcement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AnnouncementServiceTest extends ServiceTestBase {

    @Mock
    private AnnouncementService announcementService;

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
    void createAnnouncement_withValidRequest_returnsAnnouncement() {
        AnnouncementRequest request = new AnnouncementRequest();
        AnnouncementResponse mockResponse = new AnnouncementResponse();
        mockResponse.setId(1L);

        when(announcementService.createAnnouncement(request, OWNER_ID)).thenReturn(mockResponse);

        AnnouncementResponse response = announcementService.createAnnouncement(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(announcementService).createAnnouncement(request, OWNER_ID);
    }

    @Test
    void createAnnouncement_withNullRequest_throwsException() {
        when(announcementService.createAnnouncement(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                announcementService.createAnnouncement(null, OWNER_ID));
    }

    @Test
    void updateAnnouncement_withValidData_returnsUpdatedAnnouncement() {
        AnnouncementRequest request = new AnnouncementRequest();
        AnnouncementResponse mockResponse = new AnnouncementResponse();
        mockResponse.setId(1L);

        when(announcementService.updateAnnouncement(1L, request, OWNER_ID)).thenReturn(mockResponse);

        AnnouncementResponse response = announcementService.updateAnnouncement(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(announcementService).updateAnnouncement(1L, request, OWNER_ID);
    }

    @Test
    void getAnnouncementById_withValidId_returnsAnnouncement() {
        AnnouncementResponse mockResponse = new AnnouncementResponse();
        mockResponse.setId(1L);

        when(announcementService.getAnnouncementById(1L, OWNER_ID)).thenReturn(mockResponse);

        AnnouncementResponse response = announcementService.getAnnouncementById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAnnouncementById_withInvalidId_throwsException() {
        when(announcementService.getAnnouncementById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Announcement not found"));

        assertThrows(RuntimeException.class, () ->
                announcementService.getAnnouncementById(999L, OWNER_ID));
    }

    @Test
    void getAllAnnouncements_withValidOwner_returnsPagedAnnouncements() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AnnouncementResponse> announcements = new ArrayList<>();
        announcements.add(new AnnouncementResponse());
        Page<AnnouncementResponse> page = new PageImpl<>(announcements, pageable, 1);

        when(announcementService.getAllAnnouncements(OWNER_ID, pageable)).thenReturn(page);

        Page<AnnouncementResponse> response = announcementService.getAllAnnouncements(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getActiveAnnouncements_withValidOwner_returnsActiveAnnouncements() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AnnouncementResponse> announcements = new ArrayList<>();
        announcements.add(new AnnouncementResponse());
        Page<AnnouncementResponse> page = new PageImpl<>(announcements, pageable, 1);

        when(announcementService.getActiveAnnouncements(OWNER_ID, pageable)).thenReturn(page);

        Page<AnnouncementResponse> response = announcementService.getActiveAnnouncements(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getPinnedAnnouncements_withValidOwner_returnsPinnedAnnouncements() {
        List<AnnouncementResponse> mockAnnouncements = new ArrayList<>();
        mockAnnouncements.add(new AnnouncementResponse());

        when(announcementService.getPinnedAnnouncements(OWNER_ID)).thenReturn(mockAnnouncements);

        List<AnnouncementResponse> response = announcementService.getPinnedAnnouncements(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAnnouncementsByDateRange_withValidDates_returnsAnnouncements() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<AnnouncementResponse> mockAnnouncements = new ArrayList<>();
        mockAnnouncements.add(new AnnouncementResponse());

        when(announcementService.getAnnouncementsByDateRange(startDate, endDate, OWNER_ID)).thenReturn(mockAnnouncements);

        List<AnnouncementResponse> response = announcementService.getAnnouncementsByDateRange(startDate, endDate, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAnnouncementsForClass_withValidClassId_returnsAnnouncements() {
        List<AnnouncementResponse> mockAnnouncements = new ArrayList<>();
        mockAnnouncements.add(new AnnouncementResponse());

        when(announcementService.getAnnouncementsForClass(1L, OWNER_ID)).thenReturn(mockAnnouncements);

        List<AnnouncementResponse> response = announcementService.getAnnouncementsForClass(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchAnnouncements_withValidKeyword_returnsAnnouncements() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AnnouncementResponse> announcements = new ArrayList<>();
        announcements.add(new AnnouncementResponse());
        Page<AnnouncementResponse> page = new PageImpl<>(announcements, pageable, 1);

        when(announcementService.searchAnnouncements("exam", OWNER_ID, pageable)).thenReturn(page);

        Page<AnnouncementResponse> response = announcementService.searchAnnouncements("exam", OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void publishAnnouncement_withValidId_returnsPublishedAnnouncement() {
        AnnouncementResponse mockResponse = new AnnouncementResponse();
        mockResponse.setId(1L);

        when(announcementService.publishAnnouncement(1L, OWNER_ID)).thenReturn(mockResponse);

        AnnouncementResponse response = announcementService.publishAnnouncement(1L, OWNER_ID);

        assertNotNull(response);
        verify(announcementService).publishAnnouncement(1L, OWNER_ID);
    }

    @Test
    void archiveAnnouncement_withValidId_returnsArchivedAnnouncement() {
        AnnouncementResponse mockResponse = new AnnouncementResponse();
        mockResponse.setId(1L);

        when(announcementService.archiveAnnouncement(1L, OWNER_ID)).thenReturn(mockResponse);

        AnnouncementResponse response = announcementService.archiveAnnouncement(1L, OWNER_ID);

        assertNotNull(response);
        verify(announcementService).archiveAnnouncement(1L, OWNER_ID);
    }

    @Test
    void pinAnnouncement_withValidId_returnsPinnedAnnouncement() {
        AnnouncementResponse mockResponse = new AnnouncementResponse();
        mockResponse.setId(1L);

        when(announcementService.pinAnnouncement(1L, OWNER_ID)).thenReturn(mockResponse);

        AnnouncementResponse response = announcementService.pinAnnouncement(1L, OWNER_ID);

        assertNotNull(response);
        verify(announcementService).pinAnnouncement(1L, OWNER_ID);
    }

    @Test
    void unpinAnnouncement_withValidId_returnsUnpinnedAnnouncement() {
        AnnouncementResponse mockResponse = new AnnouncementResponse();
        mockResponse.setId(1L);

        when(announcementService.unpinAnnouncement(1L, OWNER_ID)).thenReturn(mockResponse);

        AnnouncementResponse response = announcementService.unpinAnnouncement(1L, OWNER_ID);

        assertNotNull(response);
        verify(announcementService).unpinAnnouncement(1L, OWNER_ID);
    }

    @Test
    void incrementViewCount_withValidId_succeeds() {
        doNothing().when(announcementService).incrementViewCount(1L, OWNER_ID);

        announcementService.incrementViewCount(1L, OWNER_ID);

        verify(announcementService).incrementViewCount(1L, OWNER_ID);
    }

    @Test
    void sendAnnouncementNotifications_withValidId_succeeds() {
        doNothing().when(announcementService).sendAnnouncementNotifications(1L, OWNER_ID);

        announcementService.sendAnnouncementNotifications(1L, OWNER_ID);

        verify(announcementService).sendAnnouncementNotifications(1L, OWNER_ID);
    }

    @Test
    void deleteAnnouncement_withValidId_succeeds() {
        doNothing().when(announcementService).deleteAnnouncement(1L, OWNER_ID);

        announcementService.deleteAnnouncement(1L, OWNER_ID);

        verify(announcementService).deleteAnnouncement(1L, OWNER_ID);
    }

    @Test
    void restoreAnnouncement_withValidId_succeeds() {
        doNothing().when(announcementService).restoreAnnouncement(1L, OWNER_ID);

        announcementService.restoreAnnouncement(1L, OWNER_ID);

        verify(announcementService).restoreAnnouncement(1L, OWNER_ID);
    }

    @Test
    void getAnnouncementStatistics_withValidOwner_returnsStatistics() {
        AnnouncementStatistics mockStats = new AnnouncementStatistics();

        when(announcementService.getAnnouncementStatistics(OWNER_ID)).thenReturn(mockStats);

        AnnouncementStatistics response = announcementService.getAnnouncementStatistics(OWNER_ID);

        assertNotNull(response);
        verify(announcementService).getAnnouncementStatistics(OWNER_ID);
    }

    @Test
    void checkAndExpireAnnouncements_succeeds() {
        doNothing().when(announcementService).checkAndExpireAnnouncements();

        announcementService.checkAndExpireAnnouncements();

        verify(announcementService).checkAndExpireAnnouncements();
    }

    @Test
    void updateAnnouncement_withNullRequest_throwsException() {
        when(announcementService.updateAnnouncement(1L, null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                announcementService.updateAnnouncement(1L, null, OWNER_ID));
    }
}
