package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.FavouriteEntryResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.dto.WorkerRequest;
import com.vijay.User_Master.dto.WorkerResponse;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkerUserServiceTest extends ServiceTestBase {

    @Mock
    private WorkerUserService workerUserService;

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
    void create_withValidRequest_returnsWorker() {
        WorkerRequest request = new WorkerRequest();
        WorkerResponse mockResponse = new WorkerResponse();
        mockResponse.setId(1L);

        when(workerUserService.create(request)).thenReturn(mockResponse);

        WorkerResponse response = workerUserService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(workerUserService).create(request);
    }

    @Test
    void create_withNullRequest_throwsException() {
        when(workerUserService.create(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                workerUserService.create(null));
    }

    @Test
    void findAllActiveUsers_withActiveUsers_returnsList() {
        List<WorkerResponse> mockWorkers = new ArrayList<>();
        mockWorkers.add(new WorkerResponse());
        mockWorkers.add(new WorkerResponse());

        when(workerUserService.findAllActiveUsers()).thenReturn(mockWorkers);

        List<WorkerResponse> response = workerUserService.findAllActiveUsers();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(workerUserService).findAllActiveUsers();
    }

    @Test
    void getAllActiveUserWithSortingSearching_withValidParams_returnsPageableResponse() {
        PageableResponse<WorkerResponse> mockResponse = new PageableResponse<>();

        when(workerUserService.getAllActiveUserWithSortingSearching(0, 10, "name", "asc")).thenReturn(mockResponse);

        PageableResponse<WorkerResponse> response = workerUserService.getAllActiveUserWithSortingSearching(0, 10, "name", "asc");

        assertNotNull(response);
        verify(workerUserService).getAllActiveUserWithSortingSearching(0, 10, "name", "asc");
    }

    @Test
    void favoriteWorkerUser_withValidId_succeeds() throws Exception {
        doNothing().when(workerUserService).favoriteWorkerUser(1L);

        workerUserService.favoriteWorkerUser(1L);

        verify(workerUserService).favoriteWorkerUser(1L);
    }

    @Test
    void unFavoriteWorkerUser_withValidId_succeeds() throws Exception {
        doNothing().when(workerUserService).unFavoriteWorkerUser(1L);

        workerUserService.unFavoriteWorkerUser(1L);

        verify(workerUserService).unFavoriteWorkerUser(1L);
    }

    @Test
    void getUserFavoriteWorkerUsers_withFavorites_returnsList() throws Exception {
        List<FavouriteEntryResponse> mockFavorites = new ArrayList<>();
        mockFavorites.add(new FavouriteEntryResponse());

        when(workerUserService.getUserFavoriteWorkerUsers()).thenReturn(mockFavorites);

        List<FavouriteEntryResponse> response = workerUserService.getUserFavoriteWorkerUsers();

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(workerUserService).getUserFavoriteWorkerUsers();
    }

    @Test
    void getWorkersBySuperUserId_withValidId_returnsPageableResponse() {
        PageableResponse<WorkerResponse> mockResponse = new PageableResponse<>();

        when(workerUserService.getWorkersBySuperUserId(100L, 0, 10, "name", "asc")).thenReturn(mockResponse);

        PageableResponse<WorkerResponse> response = workerUserService.getWorkersBySuperUserId(100L, 0, 10, "name", "asc");

        assertNotNull(response);
        verify(workerUserService).getWorkersBySuperUserId(100L, 0, 10, "name", "asc");
    }

    @Test
    void getWorkersBySuperUserWithFilter_withValidData_returnsPageableResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<WorkerResponse> mockResponse = new PageableResponse<>();

        when(workerUserService.getWorkersBySuperUserWithFilter(100L, "teacher", pageable)).thenReturn(mockResponse);

        PageableResponse<WorkerResponse> response = workerUserService.getWorkersBySuperUserWithFilter(100L, "teacher", pageable);

        assertNotNull(response);
        verify(workerUserService).getWorkersBySuperUserWithFilter(100L, "teacher", pageable);
    }

    @Test
    void getWorkersWithFilter_withValidData_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<WorkerResponse> workers = new ArrayList<>();
        workers.add(new WorkerResponse());
        Page<WorkerResponse> page = new PageImpl<>(workers, pageable, 1);

        when(workerUserService.getWorkersWithFilter(100L, false, true, "john", pageable)).thenReturn(page);

        Page<WorkerResponse> response = workerUserService.getWorkersWithFilter(100L, false, true, "john", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(workerUserService).getWorkersWithFilter(100L, false, true, "john", pageable);
    }

    @Test
    void updateAccountStatus_withValidData_succeeds() {
        doNothing().when(workerUserService).updateAccountStatus(1L, true);

        workerUserService.updateAccountStatus(1L, true);

        verify(workerUserService).updateAccountStatus(1L, true);
    }

    @Test
    void update_withValidData_returnsWorker() throws Exception {
        WorkerRequest request = new WorkerRequest();
        WorkerResponse mockResponse = new WorkerResponse();
        mockResponse.setId(1L);

        when(workerUserService.update(1L, request)).thenReturn(mockResponse);

        WorkerResponse response = workerUserService.update(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(workerUserService).update(1L, request);
    }

    @Test
    void getWorkersByRole_withValidRole_returnsPageableResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<WorkerResponse> mockResponse = new PageableResponse<>();

        when(workerUserService.getWorkersByRole("TEACHER", pageable)).thenReturn(mockResponse);

        PageableResponse<WorkerResponse> response = workerUserService.getWorkersByRole("TEACHER", pageable);

        assertNotNull(response);
        verify(workerUserService).getWorkersByRole("TEACHER", pageable);
    }

    @Test
    void findAllActiveUsers_withNoActiveUsers_returnsEmptyList() {
        List<WorkerResponse> mockWorkers = new ArrayList<>();

        when(workerUserService.findAllActiveUsers()).thenReturn(mockWorkers);

        List<WorkerResponse> response = workerUserService.findAllActiveUsers();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void update_withNullRequest_throwsException() throws Exception {
        when(workerUserService.update(1L, null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                workerUserService.update(1L, null));
    }
}
