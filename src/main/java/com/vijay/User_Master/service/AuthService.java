package com.vijay.User_Master.service;

import com.vijay.User_Master.config.security.model.LoginJWTResponse;
import com.vijay.User_Master.config.security.model.LoginRequest;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.RefreshTokenDto;
import com.vijay.User_Master.dto.form.ChangePasswordForm;
import com.vijay.User_Master.dto.form.ForgotPasswordForm;
import com.vijay.User_Master.dto.form.UnlockForm;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

public interface AuthService {
    LoginJWTResponse login(LoginRequest request);
    CompletableFuture<Object> registerForAdminUser(UserRequest request, String url);
    UserResponse registerForNormalUser(UserRequest request, String url);
    RefreshTokenDto refreshToken(String refreshToken);
    boolean changePassword(ChangePasswordForm form);
    boolean existsByUsernameOrEmail(String usernameOrEmail);
    CompletableFuture<Object> forgotPassword(ForgotPasswordForm form, String usernameOrEmail);
    boolean unlockAccount(UnlockForm form, String usernameOrEmail);
    CompletableFuture<Object> sendEmailPasswordReset(String email, HttpServletRequest request);
    CompletableFuture<Object> verifyPasswordResetLink(Long uid, String code);
    CompletableFuture<Object> verifyAndResetPassword(Long uid, String token, String newPassword, String confirmPassword);
}