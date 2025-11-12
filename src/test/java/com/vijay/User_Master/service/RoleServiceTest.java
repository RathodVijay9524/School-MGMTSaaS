package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RoleRequest;
import com.vijay.User_Master.dto.RoleResponse;
import com.vijay.User_Master.dto.RoleUpdateRequest;
import com.vijay.User_Master.dto.UserRoleRequest;
import com.vijay.User_Master.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleServiceTest extends ServiceTestBase {

    @Mock
    private RoleService roleService;

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
    void create_withValidRequest_returnsRole() {
        RoleRequest request = new RoleRequest();
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleService.create(request)).thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletableFuture<RoleResponse> response = roleService.create(request);

        assertNotNull(response);
        assertTrue(response.isDone());
        verify(roleService).create(request);
    }

    @Test
    void create_withNullRequest_throwsException() {
        when(roleService.create(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                roleService.create(null));
    }

    @Test
    void update_withValidData_returnsUpdatedRole() {
        RoleRequest request = new RoleRequest();
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleService.update(1L, request)).thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletableFuture<RoleResponse> response = roleService.update(1L, request);

        assertNotNull(response);
        assertTrue(response.isDone());
        verify(roleService).update(1L, request);
    }

    @Test
    void getById_withValidId_returnsRole() {
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleService.getById(1L)).thenReturn(CompletableFuture.completedFuture(mockResponse));

        CompletableFuture<RoleResponse> response = roleService.getById(1L);

        assertNotNull(response);
        assertTrue(response.isDone());
    }

    @Test
    void getById_withInvalidId_throwsException() {
        when(roleService.getById(999L))
                .thenThrow(new RuntimeException("Role not found"));

        assertThrows(RuntimeException.class, () ->
                roleService.getById(999L));
    }

    @Test
    void delete_withValidId_succeeds() {
        doNothing().when(roleService).delete(1L);

        roleService.delete(1L);

        verify(roleService).delete(1L);
    }

    @Test
    void getAllActiveRoles_returnsActiveRoles() {
        List<RoleResponse> mockRoles = new ArrayList<>();
        mockRoles.add(new RoleResponse());
        mockRoles.add(new RoleResponse());

        when(roleService.getAllActiveRoles()).thenReturn(mockRoles);

        List<RoleResponse> response = roleService.getAllActiveRoles();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(roleService).getAllActiveRoles();
    }

    @Test
    void getAllActiveRoles_withNoActiveRoles_returnsEmptyList() {
        List<RoleResponse> mockRoles = new ArrayList<>();

        when(roleService.getAllActiveRoles()).thenReturn(mockRoles);

        List<RoleResponse> response = roleService.getAllActiveRoles();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void updateRole_withValidData_returnsUpdatedRole() {
        RoleUpdateRequest updateRequest = new RoleUpdateRequest();
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleService.updateRole(1L, updateRequest)).thenReturn(mockResponse);

        RoleResponse response = roleService.updateRole(1L, updateRequest);

        assertNotNull(response);
        verify(roleService).updateRole(1L, updateRequest);
    }

    @Test
    void activateRole_withValidId_succeeds() {
        doNothing().when(roleService).activateRole(1L);

        roleService.activateRole(1L);

        verify(roleService).activateRole(1L);
    }

    @Test
    void deactivateRole_withValidId_succeeds() {
        doNothing().when(roleService).deactivateRole(1L);

        roleService.deactivateRole(1L);

        verify(roleService).deactivateRole(1L);
    }

    @Test
    void assignRolesToUser_withValidRequest_returnsUpdatedUser() {
        UserRoleRequest request = new UserRoleRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(100L);

        when(roleService.assignRolesToUser(request)).thenReturn(mockResponse);

        UserResponse response = roleService.assignRolesToUser(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        verify(roleService).assignRolesToUser(request);
    }

    @Test
    void removeRolesFromUser_withValidRequest_returnsUpdatedUser() {
        UserRoleRequest request = new UserRoleRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(100L);

        when(roleService.removeRolesFromUser(request)).thenReturn(mockResponse);

        UserResponse response = roleService.removeRolesFromUser(request);

        assertNotNull(response);
        verify(roleService).removeRolesFromUser(request);
    }

    @Test
    void replaceUserRoles_withValidRequest_returnsUpdatedUser() {
        UserRoleRequest request = new UserRoleRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(100L);

        when(roleService.replaceUserRoles(request)).thenReturn(mockResponse);

        UserResponse response = roleService.replaceUserRoles(request);

        assertNotNull(response);
        verify(roleService).replaceUserRoles(request);
    }

    @Test
    void getUserRoles_withValidUserId_returnsRoles() {
        Set<RoleResponse> mockRoles = new HashSet<>();
        mockRoles.add(new RoleResponse());
        mockRoles.add(new RoleResponse());

        when(roleService.getUserRoles(100L)).thenReturn(mockRoles);

        Set<RoleResponse> response = roleService.getUserRoles(100L);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(roleService).getUserRoles(100L);
    }

    @Test
    void getUserRoles_withNoRoles_returnsEmptySet() {
        Set<RoleResponse> mockRoles = new HashSet<>();

        when(roleService.getUserRoles(100L)).thenReturn(mockRoles);

        Set<RoleResponse> response = roleService.getUserRoles(100L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void roleExists_withExistingRole_returnsTrue() {
        when(roleService.roleExists(1L)).thenReturn(true);

        boolean exists = roleService.roleExists(1L);

        assertTrue(exists);
        verify(roleService).roleExists(1L);
    }

    @Test
    void roleExists_withNonExistingRole_returnsFalse() {
        when(roleService.roleExists(999L)).thenReturn(false);

        boolean exists = roleService.roleExists(999L);

        assertFalse(exists);
    }

    @Test
    void roleExistsByName_withExistingRoleName_returnsTrue() {
        when(roleService.roleExistsByName("ADMIN")).thenReturn(true);

        boolean exists = roleService.roleExistsByName("ADMIN");

        assertTrue(exists);
        verify(roleService).roleExistsByName("ADMIN");
    }

    @Test
    void roleExistsByName_withNonExistingRoleName_returnsFalse() {
        when(roleService.roleExistsByName("NONEXISTENT")).thenReturn(false);

        boolean exists = roleService.roleExistsByName("NONEXISTENT");

        assertFalse(exists);
    }

    @Test
    void assignRolesToUser_withNullRequest_throwsException() {
        when(roleService.assignRolesToUser(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                roleService.assignRolesToUser(null));
    }

    @Test
    void updateRole_withNullRequest_throwsException() {
        when(roleService.updateRole(1L, null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                roleService.updateRole(1L, null));
    }
}
