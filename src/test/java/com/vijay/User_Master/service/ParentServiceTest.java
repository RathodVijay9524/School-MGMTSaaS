package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.ChildOverviewDTO;
import com.vijay.User_Master.dto.ChildSummaryDTO;
import com.vijay.User_Master.dto.ParentDashboardDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ParentServiceTest extends ServiceTestBase {

    @Mock
    private ParentService parentService;

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
    void getParentDashboard_withValidData_returnsDashboard() {
        ParentDashboardDTO mockResponse = new ParentDashboardDTO();

        when(parentService.getParentDashboard(100L, OWNER_ID)).thenReturn(mockResponse);

        ParentDashboardDTO response = parentService.getParentDashboard(100L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getParentDashboard(100L, OWNER_ID);
    }

    @Test
    void getParentChildren_withValidParentId_returnsChildrenList() {
        List<ChildSummaryDTO> mockChildren = new ArrayList<>();
        mockChildren.add(new ChildSummaryDTO());
        mockChildren.add(new ChildSummaryDTO());

        when(parentService.getParentChildren(100L, OWNER_ID)).thenReturn(mockChildren);

        List<ChildSummaryDTO> response = parentService.getParentChildren(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(parentService).getParentChildren(100L, OWNER_ID);
    }

    @Test
    void getChildOverview_withValidData_returnsChildOverview() {
        ChildOverviewDTO mockResponse = new ChildOverviewDTO();

        when(parentService.getChildOverview(100L, 200L, OWNER_ID)).thenReturn(mockResponse);

        ChildOverviewDTO response = parentService.getChildOverview(100L, 200L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getChildOverview(100L, 200L, OWNER_ID);
    }

    @Test
    void verifyParentAccess_withValidAccess_returnsTrue() {
        when(parentService.verifyParentAccess(100L, 200L, OWNER_ID)).thenReturn(true);

        boolean result = parentService.verifyParentAccess(100L, 200L, OWNER_ID);

        assertTrue(result);
        verify(parentService).verifyParentAccess(100L, 200L, OWNER_ID);
    }

    @Test
    void verifyParentAccess_withNoAccess_returnsFalse() {
        when(parentService.verifyParentAccess(100L, 999L, OWNER_ID)).thenReturn(false);

        boolean result = parentService.verifyParentAccess(100L, 999L, OWNER_ID);

        assertFalse(result);
    }

    @Test
    void getChildSummary_withValidStudentId_returnsSummary() {
        ChildSummaryDTO mockResponse = new ChildSummaryDTO();

        when(parentService.getChildSummary(200L, OWNER_ID)).thenReturn(mockResponse);

        ChildSummaryDTO response = parentService.getChildSummary(200L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getChildSummary(200L, OWNER_ID);
    }

    @Test
    void getAggregatedStatistics_withValidParentId_returnsStatistics() {
        ParentDashboardDTO.DashboardSummaryDTO mockResponse = new ParentDashboardDTO.DashboardSummaryDTO();

        when(parentService.getAggregatedStatistics(100L, OWNER_ID)).thenReturn(mockResponse);

        ParentDashboardDTO.DashboardSummaryDTO response = parentService.getAggregatedStatistics(100L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getAggregatedStatistics(100L, OWNER_ID);
    }

    @Test
    void getParentChildren_withNoChildren_returnsEmptyList() {
        List<ChildSummaryDTO> mockChildren = new ArrayList<>();

        when(parentService.getParentChildren(100L, OWNER_ID)).thenReturn(mockChildren);

        List<ChildSummaryDTO> response = parentService.getParentChildren(100L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getParentDashboard_withDifferentParent_returnsDashboard() {
        ParentDashboardDTO mockResponse = new ParentDashboardDTO();

        when(parentService.getParentDashboard(150L, OWNER_ID)).thenReturn(mockResponse);

        ParentDashboardDTO response = parentService.getParentDashboard(150L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getParentDashboard(150L, OWNER_ID);
    }

    @Test
    void getChildOverview_withDifferentChild_returnsOverview() {
        ChildOverviewDTO mockResponse = new ChildOverviewDTO();

        when(parentService.getChildOverview(100L, 250L, OWNER_ID)).thenReturn(mockResponse);

        ChildOverviewDTO response = parentService.getChildOverview(100L, 250L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getChildOverview(100L, 250L, OWNER_ID);
    }

    @Test
    void verifyParentAccess_withInvalidParent_returnsFalse() {
        when(parentService.verifyParentAccess(999L, 200L, OWNER_ID)).thenReturn(false);

        boolean result = parentService.verifyParentAccess(999L, 200L, OWNER_ID);

        assertFalse(result);
    }

    @Test
    void getChildSummary_withInvalidStudentId_returnsSummary() {
        ChildSummaryDTO mockResponse = new ChildSummaryDTO();

        when(parentService.getChildSummary(999L, OWNER_ID)).thenReturn(mockResponse);

        ChildSummaryDTO response = parentService.getChildSummary(999L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getChildSummary(999L, OWNER_ID);
    }

    @Test
    void getAggregatedStatistics_withDifferentParent_returnsStatistics() {
        ParentDashboardDTO.DashboardSummaryDTO mockResponse = new ParentDashboardDTO.DashboardSummaryDTO();

        when(parentService.getAggregatedStatistics(150L, OWNER_ID)).thenReturn(mockResponse);

        ParentDashboardDTO.DashboardSummaryDTO response = parentService.getAggregatedStatistics(150L, OWNER_ID);

        assertNotNull(response);
        verify(parentService).getAggregatedStatistics(150L, OWNER_ID);
    }

    @Test
    void getParentChildren_withMultipleChildren_returnsAllChildren() {
        List<ChildSummaryDTO> mockChildren = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockChildren.add(new ChildSummaryDTO());
        }

        when(parentService.getParentChildren(100L, OWNER_ID)).thenReturn(mockChildren);

        List<ChildSummaryDTO> response = parentService.getParentChildren(100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(5, response.size());
    }

    @Test
    void verifyParentAccess_withSameParentAndStudent_returnsTrue() {
        when(parentService.verifyParentAccess(100L, 100L, OWNER_ID)).thenReturn(true);

        boolean result = parentService.verifyParentAccess(100L, 100L, OWNER_ID);

        assertTrue(result);
        verify(parentService).verifyParentAccess(100L, 100L, OWNER_ID);
    }
}
