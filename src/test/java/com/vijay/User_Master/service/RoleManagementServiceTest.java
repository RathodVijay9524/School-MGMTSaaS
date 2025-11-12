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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleManagementServiceTest extends ServiceTestBase {

    @Mock
    private RoleManagementService roleManagementService;

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
    void createRole_withValidRequest_returnsRole() {
        RoleRequest request = new RoleRequest();
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleManagementService.createRole(request)).thenReturn(mockResponse);

        RoleResponse response = roleManagementService.createRole(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(roleManagementService).createRole(request);
    }

    @Test
    void createRole_withNullRequest_throwsException() {
        when(roleManagementService.createRole(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                roleManagementService.createRole(null));
    }

    @Test
    void getRoleById_withValidId_returnsRole() {
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleManagementService.getRoleById(1L)).thenReturn(mockResponse);

        RoleResponse response = roleManagementService.getRoleById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getRoleById_withInvalidId_throwsException() {
        when(roleManagementService.getRoleById(999L))
                .thenThrow(new RuntimeException("Role not found"));

        assertThrows(RuntimeException.class, () ->
                roleManagementService.getRoleById(999L));
    }

    @Test
    void getAllRoles_returnsAllRoles() {
        List<RoleResponse> mockRoles = new ArrayList<>();
        mockRoles.add(new RoleResponse());
        mockRoles.add(new RoleResponse());

        when(roleManagementService.getAllRoles()).thenReturn(mockRoles);

        List<RoleResponse> response = roleManagementService.getAllRoles();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(roleManagementService).getAllRoles();
    }

    @Test
    void updateRole_withValidData_returnsUpdatedRole() {
        RoleRequest request = new RoleRequest();
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleManagementService.updateRole(1L, request)).thenReturn(mockResponse);

        RoleResponse response = roleManagementService.updateRole(1L, request);

        assertNotNull(response);
        verify(roleManagementService).updateRole(1L, request);
    }

    @Test
    void deleteRole_withValidId_returnsTrue() {
        when(roleManagementService.deleteRole(1L)).thenReturn(true);

        boolean result = roleManagementService.deleteRole(1L);

        assertTrue(result);
        verify(roleManagementService).deleteRole(1L);
    }

    @Test
    void deleteRole_withInvalidId_returnsFalse() {
        when(roleManagementService.deleteRole(999L)).thenReturn(false);

        boolean result = roleManagementService.deleteRole(999L);

        assertFalse(result);
    }

    @Test
    void getAllActiveRoles_returnsActiveRoles() {
        List<RoleResponse> mockRoles = new ArrayList<>();
        mockRoles.add(new RoleResponse());
        mockRoles.add(new RoleResponse());

        when(roleManagementService.getAllActiveRoles()).thenReturn(mockRoles);

        List<RoleResponse> response = roleManagementService.getAllActiveRoles();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(roleManagementService).getAllActiveRoles();
    }

    @Test
    void updateRoleDetails_withValidData_returnsUpdatedRole() {
        RoleUpdateRequest updateRequest = new RoleUpdateRequest();
        RoleResponse mockResponse = new RoleResponse();
        mockResponse.setId(1L);

        when(roleManagementService.updateRoleDetails(1L, updateRequest)).thenReturn(mockResponse);

        RoleResponse response = roleManagementService.updateRoleDetails(1L, updateRequest);

        assertNotNull(response);
        verify(roleManagementService).updateRoleDetails(1L, updateRequest);
    }

    @Test
    void activateRole_withValidId_succeeds() {
        doNothing().when(roleManagementService).activateRole(1L);

        roleManagementService.activateRole(1L);

        verify(roleManagementService).activateRole(1L);
    }

    @Test
    void deactivateRole_withValidId_succeeds() {
        doNothing().when(roleManagementService).deactivateRole(1L);

        roleManagementService.deactivateRole(1L);

        verify(roleManagementService).deactivateRole(1L);
    }

    @Test
    void assignRolesToUser_withValidRequest_returnsUpdatedUser() {
        UserRoleRequest request = new UserRoleRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(100L);

        when(roleManagementService.assignRolesToUser(request)).thenReturn(mockResponse);

        UserResponse response = roleManagementService.assignRolesToUser(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        verify(roleManagementService).assignRolesToUser(request);
    }

    @Test
    void removeRolesFromUser_withValidRequest_returnsUpdatedUser() {
        UserRoleRequest request = new UserRoleRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(100L);

        when(roleManagementService.removeRolesFromUser(request)).thenReturn(mockResponse);

        UserResponse response = roleManagementService.removeRolesFromUser(request);

        assertNotNull(response);
        verify(roleManagementService).removeRolesFromUser(request);
    }

    @Test
    void replaceUserRoles_withValidRequest_returnsUpdatedUser() {
        UserRoleRequest request = new UserRoleRequest();
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(100L);

        when(roleManagementService.replaceUserRoles(request)).thenReturn(mockResponse);

        UserResponse response = roleManagementService.replaceUserRoles(request);

        assertNotNull(response);
        verify(roleManagementService).replaceUserRoles(request);
    }

    @Test
    void getUserRoles_withValidUserId_returnsRoles() {
        Set<RoleResponse> mockRoles = new HashSet<>();
        mockRoles.add(new RoleResponse());
        mockRoles.add(new RoleResponse());

        when(roleManagementService.getUserRoles(100L)).thenReturn(mockRoles);

        Set<RoleResponse> response = roleManagementService.getUserRoles(100L);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(roleManagementService).getUserRoles(100L);
    }

    @Test
    void roleExists_withExistingRole_returnsTrue() {
        when(roleManagementService.roleExists(1L)).thenReturn(true);

        boolean exists = roleManagementService.roleExists(1L);

        assertTrue(exists);
        verify(roleManagementService).roleExists(1L);
    }

    @Test
    void roleExists_withNonExistingRole_returnsFalse() {
        when(roleManagementService.roleExists(999L)).thenReturn(false);

        boolean exists = roleManagementService.roleExists(999L);

        assertFalse(exists);
    }

    @Test
    void roleExistsByName_withExistingRoleName_returnsTrue() {
        when(roleManagementService.roleExistsByName("ADMIN")).thenReturn(true);

        boolean exists = roleManagementService.roleExistsByName("ADMIN");

        assertTrue(exists);
        verify(roleManagementService).roleExistsByName("ADMIN");
    }

    @Test
    void roleExistsByName_withNonExistingRoleName_returnsFalse() {
        when(roleManagementService.roleExistsByName("NONEXISTENT")).thenReturn(false);

        boolean exists = roleManagementService.roleExistsByName("NONEXISTENT");

        assertFalse(exists);
    }

    @Test
    void getActiveRolesByIds_withValidIds_returnsRoles() {
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(1L);
        roleIds.add(2L);
        List<RoleResponse> mockRoles = new ArrayList<>();
        mockRoles.add(new RoleResponse());
        mockRoles.add(new RoleResponse());

        when(roleManagementService.getActiveRolesByIds(roleIds)).thenReturn(mockRoles);

        List<RoleResponse> response = roleManagementService.getActiveRolesByIds(roleIds);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(roleManagementService).getActiveRolesByIds(roleIds);
    }

    @Test
    void getRolesByName_withValidName_returnsRoles() {
        List<RoleResponse> mockRoles = new ArrayList<>();
        mockRoles.add(new RoleResponse());

        when(roleManagementService.getRolesByName("ADMIN")).thenReturn(mockRoles);

        List<RoleResponse> response = roleManagementService.getRolesByName("ADMIN");

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(roleManagementService).getRolesByName("ADMIN");
    }

    @Test
    void assignRolesToUser_withNullRequest_throwsException() {
        when(roleManagementService.assignRolesToUser(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                roleManagementService.assignRolesToUser(null));
    }

    @Test
    void getAllRoles_withNoRoles_returnsEmptyList() {
        List<RoleResponse> mockRoles = new ArrayList<>();

        when(roleManagementService.getAllRoles()).thenReturn(mockRoles);

        List<RoleResponse> response = roleManagementService.getAllRoles();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
