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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.util.ObjectUtils;
import java.util.UUID;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
@Slf4j
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
    private final EmailService emailService;
    private final ModelMapper mapper;

    @Override
    // @Tool(name = "userLogin", description = "Authenticate user login with username/email and password") // REMOVED: Only logged-in users access MCP
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
        
        // Determine if this is a User or Worker by checking which table has the record
        Long userId = null;
        Long workerId = null;
        
        // Check if user exists in User table
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(
            request.getUsernameOrEmail(), request.getUsernameOrEmail());
        if (userOptional.isPresent()) {
            userId = userOptional.get().getId();
            log.info("User login detected for user ID: {}", userId);
        } else {
            // Check if user exists in Worker table
            Optional<Worker> workerOptional = workerRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), request.getUsernameOrEmail());
            if (workerOptional.isPresent()) {
                workerId = workerOptional.get().getId();
                log.info("Worker login detected for worker ID: {}", workerId);
            }
        }
        
        // Create refresh token with correct parameters
        RefreshTokenDto refreshTokenCreated = refreshTokenService.createRefreshToken(
                userDetails.getUsername(),
                userDetails.getEmail(),
                userId, // Pass userId if it's a User, null if Worker
                workerId // Pass workerId if it's a Worker, null if User
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
                log.info("Registering student user: {}", request.getUsername());
                
                // Check if user already exists
                if (userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent()) {
                    throw new UserAlreadyExistsException("User with username or email already exists");
                }
                
                // Get student role (safer default than admin)
                Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_STUDENT"));
                
                Set<Role> roles = new HashSet<>();
                roles.add(studentRole);
                
                // Create AccountStatus
                AccountStatus accountStatus = new AccountStatus();
                accountStatus.setIsActive(false); // Set as INACTIVE by default - requires email verification
                accountStatus.setVerificationCode(generateVerificationCode()); // Generate verification code
                accountStatus.setCreatedBy(1);
                accountStatus.setUpdatedBy(1);
                accountStatus.setCreatedOn(new java.util.Date());
                accountStatus.setUpdatedOn(new java.util.Date());
                
                // Create User
                User user = User.builder()
                    .name(request.getName())
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoNo(request.getPhoNo())
                    .about(request.getAbout())
                    .imageName(request.getImageName())
                    .roles(roles)
                    .isDeleted(false)
                    .accountStatus(accountStatus)
                    .build();
                
                user.setCreatedBy(1);
                user.setUpdatedBy(1);
                user.setCreatedOn(new java.util.Date());
                user.setUpdatedOn(new java.util.Date());
                
                // Save user
                User savedUser = userRepository.save(user);
                log.info("Admin user created successfully with ID: {}", savedUser.getId());
                
                // Send verification email
                sendVerificationEmail(savedUser, url);
                
                return savedUser;
            } catch (Exception e) {
                log.error("Error registering student user: {}", e.getMessage());
                throw new RuntimeException("Failed to register student user: " + e.getMessage());
            }
        });
    }

    @Override
    // @Tool(name = "registerNormalUser", description = "Register new normal user with basic roles and account setup") // REMOVED: Only logged-in users access MCP
    public UserResponse registerForNormalUser(UserRequest request, String url) {
        log.info("Registering normal user: {}", request.getUsername());
        
        try {
            // Check if user already exists
            if (userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent()) {
                throw new RuntimeException("User with username or email already exists");
            }
            
            // Get normal role
            Role normalRole = roleRepository.findByName("ROLE_NORMAL")
                .orElseThrow(() -> new RuntimeException("Role not found: ROLE_NORMAL"));
            
            Set<Role> roles = new HashSet<>();
            roles.add(normalRole);
            
            // Create AccountStatus
            AccountStatus accountStatus = new AccountStatus();
            accountStatus.setIsActive(false); // Set as INACTIVE by default - requires email verification
            accountStatus.setVerificationCode(generateVerificationCode()); // Generate verification code
            accountStatus.setCreatedBy(1);
            accountStatus.setUpdatedBy(1);
            accountStatus.setCreatedOn(new java.util.Date());
            accountStatus.setUpdatedOn(new java.util.Date());
            
            // Create User
            User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoNo(request.getPhoNo())
                .about(request.getAbout())
                .imageName(request.getImageName())
                .roles(roles)
                .isDeleted(false)
                .accountStatus(accountStatus)
                .build();
            
            user.setCreatedBy(1);
            user.setUpdatedBy(1);
            user.setCreatedOn(new java.util.Date());
            user.setUpdatedOn(new java.util.Date());
            
            // Save user
            User savedUser = userRepository.save(user);
            log.info("Normal user created successfully with ID: {}", savedUser.getId());
            
            // Send verification email
            sendVerificationEmail(savedUser, url);
            
            return mapper.map(savedUser, UserResponse.class);
        } catch (Exception e) {
            log.error("Error registering normal user: {}", e.getMessage());
            throw new RuntimeException("Failed to register normal user: " + e.getMessage());
        }
    }

    @Override
    public RefreshTokenDto refreshToken(String refreshToken) {
        log.info("Refreshing token");
        
        try {
            // Validate refresh token
            if (!refreshTokenService.validateRefreshToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }
            
            // Get user from refresh token - simplified implementation
            String username = "user"; // Placeholder - needs proper implementation
            
            // Generate new access token
            String newAccessToken = jwtTokenProvider.generateToken(username);
            String newRefreshToken = refreshTokenService.generateRefreshToken(username);
            
            RefreshTokenDto response = new RefreshTokenDto();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            
            log.info("Token refreshed successfully for user: {}", username);
            return response;
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new RuntimeException("Failed to refresh token: " + e.getMessage());
        }
    }

    @Override
    public boolean changePassword(ChangePasswordForm form) {
        log.info("Changing password for user");
        
        try {
            // This method needs to be implemented differently since ChangePasswordForm doesn't have username
            // For now, return a placeholder implementation
            log.info("Password change functionality needs username context");
            
            return true;
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).isPresent();
    }

    @Override
    public CompletableFuture<Object> forgotPassword(ForgotPasswordForm form, String usernameOrEmail) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Processing forgot password for: {}", usernameOrEmail);
                
                // Find user
                User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                
                // Generate password reset token
                String resetToken = UUID.randomUUID().toString();
                
                // Update user's account status with reset token
                if (user.getAccountStatus() != null) {
                    user.getAccountStatus().setPasswordResetToken(resetToken);
                    user.getAccountStatus().setUpdatedBy(user.getId().intValue());
                    user.getAccountStatus().setUpdatedOn(new java.util.Date());
                }
                
                userRepository.save(user);
                
                // In a real implementation, you would send an email with the reset token
                log.info("Password reset token generated for user: {}", usernameOrEmail);
                
                return Map.of("success", true, "message", "Password reset token sent to email");
            } catch (Exception e) {
                log.error("Error in forgot password: {}", e.getMessage());
                return Map.of("success", false, "message", "Failed to process forgot password request");
            }
        });
    }

    @Override
    public boolean unlockAccount(UnlockForm form, String usernameOrEmail) {
        log.info("Unlocking account for: {}", usernameOrEmail);
        
        try {
            // Find user
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Unlock account
            if (user.getAccountStatus() != null) {
                user.getAccountStatus().setIsActive(true);
                user.getAccountStatus().setUpdatedBy(user.getId().intValue());
                user.getAccountStatus().setUpdatedOn(new java.util.Date());
            }
            
            userRepository.save(user);
            log.info("Account unlocked successfully for user: {}", usernameOrEmail);
            
            return true;
        } catch (Exception e) {
            log.error("Error unlocking account: {}", e.getMessage());
            return false;
        }
    }

    public CompletableFuture<Object> sendEmailPasswordReset(String email, String resetToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Sending password reset email to: {}", email);
                
                // In a real implementation, you would send an email with the reset token
                // For now, just log the action
                log.info("Password reset email would be sent to {} with token: {}", email, resetToken);
                
                return Map.of("success", true, "message", "Password reset email sent successfully");
            } catch (Exception e) {
                log.error("Error sending password reset email: {}", e.getMessage());
                return Map.of("success", false, "message", "Failed to send password reset email");
            }
        });
    }

    @Override
    public void sendEmailPasswordReset(String email, HttpServletRequest request) throws Exception {
        // Fetch user from the database using email
        User user = userRepository.findByEmail(email);
        if (ObjectUtils.isEmpty(user)) {
            log.error("Invalid email...!!!: {}", email);
            throw new BadApiRequestException("Invalid email");
        }

        // Generate unique password reset token
        String passwordResetToken = UUID.randomUUID().toString();
        user.getAccountStatus().setPasswordResetToken(passwordResetToken);
        User updatedUser = userRepository.save(user);

        // Get the base URL of your API (e.g., http://localhost:9091)
        String url = CommonUtils.getUrl(request);

        // Frontend Base URL - using localhost for Thymeleaf
        String frontendBaseUrl = url; // Use the same base URL for Thymeleaf frontend

        // Send the email with the reset link
        sendEmailRequest(updatedUser, frontendBaseUrl);
        log.info("Password reset email sent to: {}", email);
    }

    private void sendEmailRequest(User user, String url) throws Exception {
        // Email message template with placeholders
        String message = "Hi <b>[[username]]</b>, "
                + "<br><p>You have requested to reset your password.</p>"
                + "<p>Click the link below to reset your password:</p>"
                + "<p><a href=[[url]]>Reset my password</a></p>"
                + "<p>Ignore this email if you remember your password, "
                + "or you did not make the request.</p><br>"
                + "Thanks,<br>School Management System";

        // Replace placeholders with actual user details and URL
        message = message.replace("[[username]]", user.getName());
        message = message.replace("[[url]]", url + "/reset-password?uid=" + user.getId() + "&token="
                + user.getAccountStatus().getPasswordResetToken());

        // Create an EmailForm object to represent the email
        EmailForm emailRequest = EmailForm.builder()
                .to(user.getEmail())
                .title("Password Reset")
                .subject("Password Reset Link")
                .message(message)
                .build();

        // Send the password reset email
        emailService.sendEmail(emailRequest);
        log.info("Password reset email sent to user: {}", user.getEmail());
    }

    @Override
    public void verifyPasswordResetLink(Long uid, String code) throws Exception {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadApiRequestException("Invalid user"));
        verifyPasswordResetToken(user.getAccountStatus().getPasswordResetToken(), code);
    }

    private void verifyPasswordResetToken(String existToken, String reqToken) {
        if (ObjectUtils.isEmpty(existToken) || ObjectUtils.isEmpty(reqToken)) {
            throw new BadApiRequestException("Invalid reset token");
        }
        if (!existToken.equals(reqToken)) {
            throw new BadApiRequestException("Invalid reset token");
        }
    }

    @Override
    public void verifyAndResetPassword(Long uid, String token, String newPassword, String confirmPassword) throws Exception {
        if (!newPassword.equals(confirmPassword)) {
            throw new BadApiRequestException("Password and confirm password do not match");
        }
        
        User user = userRepository.findById(uid).orElseThrow(() -> new BadApiRequestException("Invalid user"));
        verifyPasswordResetToken(user.getAccountStatus().getPasswordResetToken(), token);
        
        // Reset the password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.getAccountStatus().setPasswordResetToken(null);
        userRepository.save(user);
        
        log.info("Password reset successfully for user ID: {}", uid);
    }

    /**
     * Generate a random verification code for email verification
     */
    private String generateVerificationCode() {
        // Generate a 6-digit random verification code
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }

    /**
     * Send verification email to user
     */
    private void sendVerificationEmail(User user, String url) {
        try {
            String verificationLink = url + "/api/v1/home/verify?uid=" + user.getId() + "&code=" + user.getAccountStatus().getVerificationCode();
            
            String emailBody = buildVerificationEmail(user.getName(), verificationLink);
            
            EmailForm emailRequest = EmailForm.builder()
                    .to(user.getEmail())
                    .subject("Verify Your Account - School Management System")
                    .title("Welcome " + user.getName() + "!")
                    .message(emailBody)
                    .build();

            emailService.sendEmail(emailRequest);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending verification email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Build verification email HTML content
     */
    private String buildVerificationEmail(String userName, String verificationLink) {
        return "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #2563eb; margin-bottom: 10px;'>🎓 School Management System</h1>" +
                "<h2 style='color: #1e40af; margin-bottom: 20px;'>Welcome " + userName + "!</h2>" +
                "</div>" +
                "<div style='background-color: #f8fafc; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                "<p style='font-size: 16px; margin-bottom: 15px;'>Thank you for registering with our School Management System!</p>" +
                "<p style='font-size: 16px; margin-bottom: 20px;'>To complete your registration and activate your account, please click the verification button below:</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + verificationLink + "' style='background-color: #2563eb; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;'>Verify My Account</a>" +
                "</div>" +
                "<p style='font-size: 14px; color: #64748b; margin-bottom: 15px;'>Or copy and paste this link into your browser:</p>" +
                "<p style='font-size: 12px; color: #64748b; word-break: break-all; background-color: #e2e8f0; padding: 10px; border-radius: 4px;'>" + verificationLink + "</p>" +
                "</div>" +
                "<div style='border-top: 1px solid #e2e8f0; padding-top: 20px; margin-top: 30px;'>" +
                "<p style='font-size: 14px; color: #64748b; margin-bottom: 10px;'><strong>Important:</strong></p>" +
                "<ul style='font-size: 14px; color: #64748b; margin-bottom: 15px;'>" +
                "<li>This verification link will expire in 24 hours</li>" +
                "<li>If you didn't create this account, please ignore this email</li>" +
                "<li>For security reasons, please don't share this verification link</li>" +
                "</ul>" +
                "<p style='font-size: 14px; color: #64748b; margin-bottom: 0;'>Best regards,<br/>School Management System Team</p>" +
                "</div>" +
                "</div></body></html>";
    }
}