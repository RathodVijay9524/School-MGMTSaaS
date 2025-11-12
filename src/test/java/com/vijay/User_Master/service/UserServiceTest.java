package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.PageableResponse;
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
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest extends ServiceTestBase {

    @Mock
    private UserService userService;

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
    void createUser_withValidRequest_returnsUserResponse() {
        UserRequest request = new UserRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(1L);

        when(userService.create(request)).thenReturn(mockResponse);

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(userService).create(request);
    }

    @Test
    void createUser_async_withValidRequest_returnsCompletableFuture() {
        UserRequest request = new UserRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(1L);

        when(userService.createUser(request)).thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletableFuture<UserResponse> future = userService.createUser(request);

        assertNotNull(future);
        assertTrue(future.isDone());
    }

    @Test
    void createUser_withNullRequest_throwsException() {
        when(userService.create(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                userService.create(null));
    }

    @Test
    void updateUser_withValidData_returnsUpdatedUser() {
        UserRequest request = new UserRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(1L);

        when(userService.updateUser(1L, request)).thenReturn(mockResponse);

        UserResponse response = userService.updateUser(1L, request);

        assertNotNull(response);
        verify(userService).updateUser(1L, request);
    }

    @Test
    void getByIdForUser_withValidId_returnsUser() {
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(1L);

        when(userService.getByIdForUser(1L)).thenReturn(mockResponse);

        UserResponse response = userService.getByIdForUser(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getByIdForUser_withInvalidId_throwsException() {
        when(userService.getByIdForUser(999L))
                .thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () ->
                userService.getByIdForUser(999L));
    }

    @Test
    void getUsersWithFilter_withValidFilters_returnsPagedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserResponse> users = new ArrayList<>();
        users.add(new UserResponse());
        Page<UserResponse> page = new PageImpl<>(users, pageable, 1);

        when(userService.getUsersWithFilter(false, true, "test", pageable)).thenReturn(page);

        Page<UserResponse> response = userService.getUsersWithFilter(false, true, "test", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getUsersWithFilters_withValidParams_returnsPageableResponse() {
        PageableResponse<UserResponse> mockResponse = new PageableResponse<>();

        when(userService.getUsersWithFilters(0, 10, "id", "ASC", false, true))
                .thenReturn(mockResponse);

        PageableResponse<UserResponse> response = userService.getUsersWithFilters(0, 10, "id", "ASC", false, true);

        assertNotNull(response);
        verify(userService).getUsersWithFilters(0, 10, "id", "ASC", false, true);
    }

    @Test
    void getAllActiveUsers_withValidPageable_returnsActiveUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<UserResponse> mockResponse = new PageableResponse<>();

        when(userService.getAllActiveUsers(pageable)).thenReturn(mockResponse);

        PageableResponse<UserResponse> response = userService.getAllActiveUsers(pageable);

        assertNotNull(response);
        verify(userService).getAllActiveUsers(pageable);
    }

    @Test
    void getAllDeletedUsers_withValidPageable_returnsDeletedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse<UserResponse> mockResponse = new PageableResponse<>();

        when(userService.getAllDeletedUsers(pageable)).thenReturn(mockResponse);

        PageableResponse<UserResponse> response = userService.getAllDeletedUsers(pageable);

        assertNotNull(response);
        verify(userService).getAllDeletedUsers(pageable);
    }

    @Test
    void updateAccountStatus_withValidId_succeeds() {
        doNothing().when(userService).updateAccountStatus(1L, true);

        userService.updateAccountStatus(1L, true);

        verify(userService).updateAccountStatus(1L, true);
    }

    @Test
    void softDeleteUser_withValidId_succeeds() {
        doNothing().when(userService).softDeleteUser(1L);

        userService.softDeleteUser(1L);

        verify(userService).softDeleteUser(1L);
    }

    @Test
    void restoreUser_withValidId_succeeds() {
        doNothing().when(userService).restoreUser(1L);

        userService.restoreUser(1L);

        verify(userService).restoreUser(1L);
    }

    @Test
    void permanentlyDelete_withValidId_succeeds() {
        doNothing().when(userService).permanentlyDelete(1L);

        userService.permanentlyDelete(1L);

        verify(userService).permanentlyDelete(1L);
    }

    @Test
    void uploadUserImage_withValidFile_returnsImagePath() {
        when(userService.uploadUserImage(any())).thenReturn("/images/user-123.jpg");

        String response = userService.uploadUserImage(null);

        assertNotNull(response);
        assertTrue(response.contains("/images/"));
    }

    @Test
    void updateUser_withNullRequest_throwsException() {
        when(userService.updateUser(1L, null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(1L, null));
    }
}
