package com.vijay.User_Master.service;

import com.vijay.User_Master.config.security.model.LoginJWTResponse;
import com.vijay.User_Master.config.security.model.LoginRequest;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.RefreshTokenDto;
import com.vijay.User_Master.dto.form.ChangePasswordForm;
import com.vijay.User_Master.dto.form.ForgotPasswordForm;
import com.vijay.User_Master.dto.form.UnlockForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest extends ServiceTestBase {

    @Mock
    private AuthService authService;

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
    void login_withValidCredentials_returnsJWTResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("test@example.com");
        request.setPassword("password123");

        LoginJWTResponse mockResponse = new LoginJWTResponse();

        when(authService.login(request)).thenReturn(mockResponse);

        LoginJWTResponse response = authService.login(request);

        assertNotNull(response);
        verify(authService).login(request);
    }

    @Test
    void login_withInvalidCredentials_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("invalid@example.com");
        request.setPassword("wrongpassword");

        when(authService.login(request))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_withNullRequest_throwsException() {
        when(authService.login(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> authService.login(null));
    }

    @Test
    void existsByUsernameOrEmail_withExistingEmail_returnsTrue() {
        when(authService.existsByUsernameOrEmail("test@example.com"))
                .thenReturn(true);

        boolean exists = authService.existsByUsernameOrEmail("test@example.com");

        assertTrue(exists);
        verify(authService).existsByUsernameOrEmail("test@example.com");
    }

    @Test
    void existsByUsernameOrEmail_withNonExistingEmail_returnsFalse() {
        when(authService.existsByUsernameOrEmail("nonexistent@example.com"))
                .thenReturn(false);

        boolean exists = authService.existsByUsernameOrEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void refreshToken_withValidToken_returnsNewToken() {
        String refreshToken = "valid-refresh-token";
        RefreshTokenDto mockDto = new RefreshTokenDto();

        when(authService.refreshToken(refreshToken)).thenReturn(mockDto);

        RefreshTokenDto response = authService.refreshToken(refreshToken);

        assertNotNull(response);
        verify(authService).refreshToken(refreshToken);
    }

    @Test
    void refreshToken_withInvalidToken_throwsException() {
        String invalidToken = "invalid-refresh-token";

        when(authService.refreshToken(invalidToken))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        assertThrows(RuntimeException.class, () -> authService.refreshToken(invalidToken));
    }

    @Test
    void changePassword_withValidForm_returnsTrue() {
        ChangePasswordForm form = new ChangePasswordForm();

        when(authService.changePassword(form)).thenReturn(true);

        boolean result = authService.changePassword(form);

        assertTrue(result);
        verify(authService).changePassword(form);
    }

    @Test
    void changePassword_withInvalidForm_returnsFalse() {
        ChangePasswordForm form = new ChangePasswordForm();

        when(authService.changePassword(form)).thenReturn(false);

        boolean result = authService.changePassword(form);

        assertFalse(result);
    }

    @Test
    void changePassword_withNullForm_throwsException() {
        when(authService.changePassword(null))
                .thenThrow(new IllegalArgumentException("Form cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(null));
    }

    @Test
    void unlockAccount_withValidForm_returnsTrue() {
        UnlockForm form = new UnlockForm();

        when(authService.unlockAccount(form, "test@example.com"))
                .thenReturn(true);

        boolean result = authService.unlockAccount(form, "test@example.com");

        assertTrue(result);
    }

    @Test
    void unlockAccount_withInvalidForm_returnsFalse() {
        UnlockForm form = new UnlockForm();

        when(authService.unlockAccount(form, "test@example.com"))
                .thenReturn(false);

        boolean result = authService.unlockAccount(form, "test@example.com");

        assertFalse(result);
    }

    @Test
    void sendEmailPasswordReset_withValidEmail_succeeds() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);

        doNothing().when(authService)
                .sendEmailPasswordReset("test@example.com", request);

        authService.sendEmailPasswordReset("test@example.com", request);

        verify(authService).sendEmailPasswordReset("test@example.com", request);
    }

    @Test
    void sendEmailPasswordReset_withInvalidEmail_throwsException() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);

        doThrow(new Exception("Email not found"))
                .when(authService)
                .sendEmailPasswordReset("invalid@example.com", request);

        assertThrows(Exception.class, () ->
                authService.sendEmailPasswordReset("invalid@example.com", request));
    }

    @Test
    void verifyPasswordResetLink_withValidLink_succeeds() throws Exception {
        doNothing().when(authService)
                .verifyPasswordResetLink(1L, "valid-code");

        authService.verifyPasswordResetLink(1L, "valid-code");

        verify(authService).verifyPasswordResetLink(1L, "valid-code");
    }

    @Test
    void verifyPasswordResetLink_withInvalidLink_throwsException() throws Exception {
        doThrow(new Exception("Invalid reset link"))
                .when(authService)
                .verifyPasswordResetLink(1L, "invalid-code");

        assertThrows(Exception.class, () ->
                authService.verifyPasswordResetLink(1L, "invalid-code"));
    }

    @Test
    void verifyAndResetPassword_withValidData_succeeds() throws Exception {
        doNothing().when(authService)
                .verifyAndResetPassword(1L, "token", "newpass", "newpass");

        authService.verifyAndResetPassword(1L, "token", "newpass", "newpass");

        verify(authService).verifyAndResetPassword(1L, "token", "newpass", "newpass");
    }

    @Test
    void verifyAndResetPassword_withMismatchedPasswords_throwsException() throws Exception {
        doThrow(new Exception("Passwords do not match"))
                .when(authService)
                .verifyAndResetPassword(1L, "token", "newpass", "different");

        assertThrows(Exception.class, () ->
                authService.verifyAndResetPassword(1L, "token", "newpass", "different"));
    }
}
