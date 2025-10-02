package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.EmailUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.config.security.JwtTokenProvider;
import com.vijay.User_Master.config.security.model.LoginJWTResponse;
import com.vijay.User_Master.config.security.model.LoginRequest;
import com.vijay.User_Master.dto.RefreshTokenDto;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.form.*;
import com.vijay.User_Master.entity.AccountStatus;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.exceptions.UserAlreadyExistsException;
import com.vijay.User_Master.repository.RoleRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AuthService;
import com.vijay.User_Master.service.RefreshTokenService;
import com.vijay.User_Master.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final EmailUtils emailUtils;
    private final ModelMapper mapper;

    @Override
    public LoginJWTResponse login(LoginRequest request) {
        log.info("Attempting login for username/email: {}", request.getUsernameOrEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // Create refresh token - we'll use the username from userDetails
        RefreshTokenDto refreshTokenCreated = refreshTokenService.createRefreshToken(
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getId(), // This could be user or worker ID
                null // We'll set this to null for now
        );

        if (refreshTokenCreated == null) {
            log.error("Error creating refresh token.");
            throw new RuntimeException("Error creating refresh token.");
        }

        String token = jwtTokenProvider.generateToken(authentication.getName());

        UserResponse response = mapper.map(userDetails, UserResponse.class);

        LoginJWTResponse jwtResponse = LoginJWTResponse.builder()
                .jwtToken(token)
                .user(response)
                .refreshTokenDto(refreshTokenCreated)
                .build();
        return jwtResponse;
    }

    @Override
    public CompletableFuture<Object> registerForAdminUser(UserRequest request, String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Implementation logic here
                return true;
            } catch (Exception e) {
                log.error("Error registering admin user: {}", e.getMessage());
                return false;
            }
        });
    }

    @Override
    public UserResponse registerForNormalUser(UserRequest request) {
        // Implementation logic here
        return null;
    }

    @Override
    public RefreshTokenDto refreshToken(String refreshToken) {
        // Implementation logic here
        return null;
    }

    @Override
    public boolean changePassword(ChangePasswordForm form) {
        // Implementation logic here
        return true;
    }

    @Override
    public boolean existsByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).isPresent();
    }

    @Override
    public CompletableFuture<Object> forgotPassword(ForgotPasswordForm form, String usernameOrEmail) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Implementation logic here
                return true;
            } catch (Exception e) {
                log.error("Error in forgot password: {}", e.getMessage());
                return false;
            }
        });
    }

    @Override
    public boolean unlockAccount(UnlockForm form, String usernameOrEmail) {
        // Implementation logic here
        return true;
    }

    @Override
    public CompletableFuture<Object> sendEmailPasswordReset(String email, HttpServletRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Implementation logic here
                return true;
            } catch (Exception e) {
                log.error("Error sending email password reset: {}", e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Object> verifyPasswordResetLink(Long uid, String code) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Implementation logic here
                return true;
            } catch (Exception e) {
                log.error("Error verifying password reset link: {}", e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Object> verifyAndResetPassword(Long uid, String token, String newPassword, String confirmPassword) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Implementation logic here
                return true;
            } catch (Exception e) {
                log.error("Error verifying and resetting password: {}", e.getMessage());
                return false;
            }
        });
    }
}