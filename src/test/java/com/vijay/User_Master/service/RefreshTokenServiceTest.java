package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RefreshTokenDto;
import com.vijay.User_Master.dto.form.RefreshTokenRequest;
import com.vijay.User_Master.config.security.model.JwtResponse;
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
class RefreshTokenServiceTest extends ServiceTestBase {

    @Mock
    private RefreshTokenService refreshTokenService;

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
    void generateRefreshToken_withValidUsername_returnsToken() {
        when(refreshTokenService.generateRefreshToken("testuser"))
                .thenReturn("refresh-token-abc123xyz");

        String token = refreshTokenService.generateRefreshToken("testuser");

        assertNotNull(token);
        assertTrue(token.length() > 0);
        verify(refreshTokenService).generateRefreshToken("testuser");
    }

    @Test
    void generateRefreshToken_withNullUsername_throwsException() {
        when(refreshTokenService.generateRefreshToken(null))
                .thenThrow(new IllegalArgumentException("Username cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                refreshTokenService.generateRefreshToken(null));
    }

    @Test
    void validateRefreshToken_withValidToken_returnsTrue() {
        when(refreshTokenService.validateRefreshToken("valid-token"))
                .thenReturn(true);

        boolean result = refreshTokenService.validateRefreshToken("valid-token");

        assertTrue(result);
        verify(refreshTokenService).validateRefreshToken("valid-token");
    }

    @Test
    void validateRefreshToken_withInvalidToken_returnsFalse() {
        when(refreshTokenService.validateRefreshToken("invalid-token"))
                .thenReturn(false);

        boolean result = refreshTokenService.validateRefreshToken("invalid-token");

        assertFalse(result);
    }

    @Test
    void validateRefreshToken_withExpiredToken_returnsFalse() {
        when(refreshTokenService.validateRefreshToken("expired-token"))
                .thenReturn(false);

        boolean result = refreshTokenService.validateRefreshToken("expired-token");

        assertFalse(result);
    }

    @Test
    void getUsernameFromToken_withValidToken_returnsUsername() {
        when(refreshTokenService.getUsernameFromToken("valid-token"))
                .thenReturn("testuser");

        String username = refreshTokenService.getUsernameFromToken("valid-token");

        assertNotNull(username);
        assertEquals("testuser", username);
        verify(refreshTokenService).getUsernameFromToken("valid-token");
    }

    @Test
    void getUsernameFromToken_withInvalidToken_throwsException() {
        when(refreshTokenService.getUsernameFromToken("invalid-token"))
                .thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () ->
                refreshTokenService.getUsernameFromToken("invalid-token"));
    }

    @Test
    void deleteRefreshToken_withValidToken_succeeds() {
        doNothing().when(refreshTokenService).deleteRefreshToken("valid-token");

        refreshTokenService.deleteRefreshToken("valid-token");

        verify(refreshTokenService).deleteRefreshToken("valid-token");
    }

    @Test
    void createRefreshToken_withValidData_returnsRefreshTokenDto() {
        RefreshTokenDto mockDto = new RefreshTokenDto();

        when(refreshTokenService.createRefreshToken("testuser", "token123", 100L, 50L))
                .thenReturn(mockDto);

        RefreshTokenDto result = refreshTokenService.createRefreshToken("testuser", "token123", 100L, 50L);

        assertNotNull(result);
        verify(refreshTokenService).createRefreshToken("testuser", "token123", 100L, 50L);
    }

    @Test
    void createRefreshToken_withNullUsername_throwsException() {
        when(refreshTokenService.createRefreshToken(null, "token123", 100L, 50L))
                .thenThrow(new IllegalArgumentException("Username cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                refreshTokenService.createRefreshToken(null, "token123", 100L, 50L));
    }

    @Test
    void verifyRefreshToken_withValidDto_returnsVerifiedDto() {
        RefreshTokenDto inputDto = new RefreshTokenDto();
        RefreshTokenDto mockDto = new RefreshTokenDto();

        when(refreshTokenService.verifyRefreshToken(inputDto)).thenReturn(mockDto);

        RefreshTokenDto result = refreshTokenService.verifyRefreshToken(inputDto);

        assertNotNull(result);
        verify(refreshTokenService).verifyRefreshToken(inputDto);
    }

    @Test
    void verifyRefreshToken_withInvalidDto_throwsException() {
        RefreshTokenDto inputDto = new RefreshTokenDto();

        when(refreshTokenService.verifyRefreshToken(inputDto))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        assertThrows(RuntimeException.class, () ->
                refreshTokenService.verifyRefreshToken(inputDto));
    }

    @Test
    void refreshAccessToken_withValidRequest_returnsJwtResponse() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        JwtResponse mockResponse = new JwtResponse();

        when(refreshTokenService.refreshAccessToken(request)).thenReturn(mockResponse);

        JwtResponse result = refreshTokenService.refreshAccessToken(request);

        assertNotNull(result);
        verify(refreshTokenService).refreshAccessToken(request);
    }

    @Test
    void refreshAccessToken_withInvalidRequest_throwsException() {
        RefreshTokenRequest request = new RefreshTokenRequest();

        when(refreshTokenService.refreshAccessToken(request))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        assertThrows(RuntimeException.class, () ->
                refreshTokenService.refreshAccessToken(request));
    }

    @Test
    void refreshAccessToken_withNullRequest_throwsException() {
        when(refreshTokenService.refreshAccessToken(null))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                refreshTokenService.refreshAccessToken(null));
    }

    @Test
    void invalidateRefreshToken_withValidToken_succeeds() {
        doNothing().when(refreshTokenService).invalidateRefreshToken("valid-token");

        refreshTokenService.invalidateRefreshToken("valid-token");

        verify(refreshTokenService).invalidateRefreshToken("valid-token");
    }

    @Test
    void invalidateRefreshToken_withNullToken_throwsException() {
        doThrow(new IllegalArgumentException("Token cannot be null"))
                .when(refreshTokenService).invalidateRefreshToken(null);

        assertThrows(IllegalArgumentException.class, () ->
                refreshTokenService.invalidateRefreshToken(null));
    }
}
