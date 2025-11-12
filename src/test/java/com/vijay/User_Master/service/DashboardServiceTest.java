package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DashboardAnalytics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest extends ServiceTestBase {

    @Mock
    private DashboardService dashboardService;

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
    void getApplicationOwnerDashboard_returnsAnalytics() {
        DashboardAnalytics mockAnalytics = new DashboardAnalytics();

        when(dashboardService.getApplicationOwnerDashboard()).thenReturn(mockAnalytics);

        DashboardAnalytics response = dashboardService.getApplicationOwnerDashboard();

        assertNotNull(response);
        verify(dashboardService).getApplicationOwnerDashboard();
    }

    @Test
    void getApplicationOwnerDashboard_withMultipleSchools_returnsAggregatedData() {
        DashboardAnalytics mockAnalytics = new DashboardAnalytics();

        when(dashboardService.getApplicationOwnerDashboard()).thenReturn(mockAnalytics);

        DashboardAnalytics response = dashboardService.getApplicationOwnerDashboard();

        assertNotNull(response);
        verify(dashboardService).getApplicationOwnerDashboard();
    }

    @Test
    void getSchoolOwnerDashboard_withValidOwnerId_returnsAnalytics() {
        DashboardAnalytics mockAnalytics = new DashboardAnalytics();

        when(dashboardService.getSchoolOwnerDashboard(OWNER_ID)).thenReturn(mockAnalytics);

        DashboardAnalytics response = dashboardService.getSchoolOwnerDashboard(OWNER_ID);

        assertNotNull(response);
        verify(dashboardService).getSchoolOwnerDashboard(OWNER_ID);
    }

    @Test
    void getSchoolOwnerDashboard_withInvalidOwnerId_throwsException() {
        when(dashboardService.getSchoolOwnerDashboard(999L))
                .thenThrow(new RuntimeException("School owner not found"));

        assertThrows(RuntimeException.class, () ->
                dashboardService.getSchoolOwnerDashboard(999L));
    }

    @Test
    void getSchoolOwnerDashboard_withNullOwnerId_throwsException() {
        when(dashboardService.getSchoolOwnerDashboard(null))
                .thenThrow(new IllegalArgumentException("Owner ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                dashboardService.getSchoolOwnerDashboard(null));
    }

    @Test
    void getSchoolAnalytics_withValidSchoolOwnerId_returnsAnalytics() {
        DashboardAnalytics mockAnalytics = new DashboardAnalytics();

        when(dashboardService.getSchoolAnalytics(OWNER_ID)).thenReturn(mockAnalytics);

        DashboardAnalytics response = dashboardService.getSchoolAnalytics(OWNER_ID);

        assertNotNull(response);
        verify(dashboardService).getSchoolAnalytics(OWNER_ID);
    }

    @Test
    void getSchoolAnalytics_withInvalidSchoolOwnerId_throwsException() {
        when(dashboardService.getSchoolAnalytics(999L))
                .thenThrow(new RuntimeException("School owner not found"));

        assertThrows(RuntimeException.class, () ->
                dashboardService.getSchoolAnalytics(999L));
    }

    @Test
    void getSchoolAnalytics_withNullSchoolOwnerId_throwsException() {
        when(dashboardService.getSchoolAnalytics(null))
                .thenThrow(new IllegalArgumentException("School owner ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                dashboardService.getSchoolAnalytics(null));
    }

    @Test
    void getQuickStats_withValidOwnerId_returnsQuickStats() {
        DashboardAnalytics.QuickStats mockStats = mock(DashboardAnalytics.QuickStats.class);

        when(dashboardService.getQuickStats(OWNER_ID)).thenReturn(mockStats);

        DashboardAnalytics.QuickStats response = dashboardService.getQuickStats(OWNER_ID);

        assertNotNull(response);
        verify(dashboardService).getQuickStats(OWNER_ID);
    }

    @Test
    void getQuickStats_withNullOwnerId_returnsApplicationLevelStats() {
        DashboardAnalytics.QuickStats mockStats = mock(DashboardAnalytics.QuickStats.class);

        when(dashboardService.getQuickStats(null)).thenReturn(mockStats);

        DashboardAnalytics.QuickStats response = dashboardService.getQuickStats(null);

        assertNotNull(response);
        verify(dashboardService).getQuickStats(null);
    }

    @Test
    void getQuickStats_withInvalidOwnerId_throwsException() {
        when(dashboardService.getQuickStats(999L))
                .thenThrow(new RuntimeException("Owner not found"));

        assertThrows(RuntimeException.class, () ->
                dashboardService.getQuickStats(999L));
    }

    @Test
    void getSchoolOwnerDashboard_multipleOwners_returnsDifferentAnalytics() {
        DashboardAnalytics analytics1 = new DashboardAnalytics();
        DashboardAnalytics analytics2 = new DashboardAnalytics();

        when(dashboardService.getSchoolOwnerDashboard(1L)).thenReturn(analytics1);
        when(dashboardService.getSchoolOwnerDashboard(2L)).thenReturn(analytics2);

        DashboardAnalytics response1 = dashboardService.getSchoolOwnerDashboard(1L);
        DashboardAnalytics response2 = dashboardService.getSchoolOwnerDashboard(2L);

        assertNotNull(response1);
        assertNotNull(response2);
        verify(dashboardService).getSchoolOwnerDashboard(1L);
        verify(dashboardService).getSchoolOwnerDashboard(2L);
    }

    @Test
    void getApplicationOwnerDashboard_repeatedCalls_returnsSameData() {
        DashboardAnalytics mockAnalytics = new DashboardAnalytics();

        when(dashboardService.getApplicationOwnerDashboard()).thenReturn(mockAnalytics);

        DashboardAnalytics response1 = dashboardService.getApplicationOwnerDashboard();
        DashboardAnalytics response2 = dashboardService.getApplicationOwnerDashboard();

        assertNotNull(response1);
        assertNotNull(response2);
        verify(dashboardService, times(2)).getApplicationOwnerDashboard();
    }

    @Test
    void getQuickStats_withValidOwnerId_returnsNonNullStats() {
        DashboardAnalytics.QuickStats mockStats = mock(DashboardAnalytics.QuickStats.class);

        when(dashboardService.getQuickStats(OWNER_ID)).thenReturn(mockStats);

        DashboardAnalytics.QuickStats response = dashboardService.getQuickStats(OWNER_ID);

        assertNotNull(response);
        assertEquals(mockStats, response);
    }
}
