package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RefreshTokenDto;
import com.vijay.User_Master.dto.form.RefreshTokenRequest;
import com.vijay.User_Master.config.security.model.JwtResponse;

public interface RefreshTokenService {
    String generateRefreshToken(String username);
    boolean validateRefreshToken(String token);
    String getUsernameFromToken(String token);
    void deleteRefreshToken(String token);
    RefreshTokenDto createRefreshToken(String username, String token, Long userId, Long workerId);
    RefreshTokenDto verifyRefreshToken(RefreshTokenDto refreshTokenDto);
    JwtResponse refreshAccessToken(RefreshTokenRequest request);
    void invalidateRefreshToken(String refreshToken);
}