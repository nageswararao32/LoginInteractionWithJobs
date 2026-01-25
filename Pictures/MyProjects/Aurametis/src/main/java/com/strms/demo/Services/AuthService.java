/* 
package com.strms.demo.Services;


import java.security.SecureRandom;
import java.time.LocalDateTime;

import javax.security.auth.login.AccountLockedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.strms.demo.DTOs.LoginRequest;
import com.strms.demo.DTOs.LoginResponse;
import com.strms.demo.DTOs.OtpVerificationRequest;
import com.strms.demo.DTOs.PasswordResetRequest;
import com.strms.demo.Entites.AdminUser;
import com.strms.demo.Entites.OtpToken;
import com.strms.demo.ExceptionHandling.InvalidCredentialsException;
import com.strms.demo.ExceptionHandling.InvalidOtpException;
import com.strms.demo.Repositories.AdminUserRepository;
import com.strms.demo.Repositories.OtpTokenRepository;
import com.strms.demo.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final AdminUserRepository adminUserRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    private final JwtUtil jwtUtil;
    
    @Value("${otp.expiration}")
    private Long otpExpiration;
    
    @Value("${otp.length}")
    private Integer otpLength;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    
    public AuthService(AdminUserRepository adminUserRepository, 
                      OtpTokenRepository otpTokenRepository,
                      PasswordEncoder passwordEncoder, 
                      EmailService emailService,
                      AuditLogService auditLogService, 
                      JwtUtil jwtUtil) {
        this.adminUserRepository = adminUserRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.auditLogService = auditLogService;
        this.jwtUtil = jwtUtil;
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) throws AccountLockedException {
        AdminUser user = adminUserRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        
        // Check if account is locked
        if (user.getAccountLockedUntil() != null && 
            user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            auditLogService.logAction(
                request.getEmail(),
                "LOGIN_ATTEMPT_LOCKED",
                "Login attempt on locked account",
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"),
                "FAILURE"
            );
            throw new AccountLockedException(
                "Account is locked until " + user.getAccountLockedUntil() + ". Please try again later."
            );
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user, httpRequest);
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Reset failed attempts on successful password verification
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        adminUserRepository.save(user);
        
        // Generate and send OTP
        String otp = generateOtp();
        saveOtpToken(user.getEmail(), otp, OtpToken.OtpType.LOGIN, getClientIp(httpRequest));
        
        // Send OTP email
        emailService.sendOtpEmail(user.getEmail(), otp, user.getName());
        
        auditLogService.logAction(
            user.getEmail(),
            "LOGIN_OTP_SENT",
            "OTP sent for login verification",
            getClientIp(httpRequest),
            httpRequest.getHeader("User-Agent"),
            "SUCCESS"
        );
        
        log.info("Login OTP sent to {}", user.getEmail());
        
        LoginResponse response = new LoginResponse();
        response.setStep("otp");
        response.setMessage("OTP sent to your email");
        response.setEmail(user.getEmail());
        return response;
    }
    
    @Transactional
    public LoginResponse verifyOtp(OtpVerificationRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        OtpToken otpToken = otpTokenRepository
            .findByEmailAndOtpAndOtpTypeAndIsUsedFalse(
                request.getEmail(),
                request.getOtp(),
                OtpToken.OtpType.LOGIN
            )
            .orElseThrow(() -> new InvalidOtpException("Invalid or expired OTP"));

        if (!otpToken.isValid()) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        AdminUser user = adminUserRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        // Mark OTP as used
        otpToken.setIsUsed(true);
        otpToken.setUsedAt(LocalDateTime.now());
        otpTokenRepository.save(otpToken);

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        adminUserRepository.save(user);

        // Generate JWT tokens
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Set refresh token in httpOnly cookie
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // use HTTPS in production
        refreshCookie.setPath("/api/auth/refresh"); // only sent to refresh endpoint
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        httpResponse.addCookie(refreshCookie);

        LoginResponse response = new LoginResponse();
        response.setStep("complete");
        response.setMessage("Login successful");
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().name());
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    
    @Transactional
    public void forgotPassword(String email, HttpServletRequest httpRequest) {
        AdminUser user = adminUserRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        
        // Invalidate all existing password reset OTPs
        otpTokenRepository.invalidateAllByEmailAndType(email, OtpToken.OtpType.FORGOT_PASSWORD);
        
        // Generate and send OTP
        String otp = generateOtp();
        saveOtpToken(email, otp, OtpToken.OtpType.FORGOT_PASSWORD, getClientIp(httpRequest));
        
        emailService.sendPasswordResetEmail(email, otp, user.getName());
        
        auditLogService.logAction(
            email,
            "PASSWORD_RESET_REQUESTED",
            "Password reset OTP sent",
            getClientIp(httpRequest),
            httpRequest.getHeader("User-Agent"),
            "SUCCESS"
        );
        
        log.info("Password reset OTP sent to {}", email);
    }
    
    @Transactional
    public void resetPassword(PasswordResetRequest request, HttpServletRequest httpRequest) {
        OtpToken otpToken = otpTokenRepository
            .findByEmailAndOtpAndOtpTypeAndIsUsedFalse(
                request.getEmail(),
                request.getOtp(),
                OtpToken.OtpType.FORGOT_PASSWORD
            )
            .orElseThrow(() -> new InvalidOtpException("Invalid or expired OTP"));
        
        if (!otpToken.isValid()) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
        
        AdminUser user = adminUserRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        adminUserRepository.save(user);
        
        // Mark OTP as used
        otpToken.setIsUsed(true);
        otpToken.setUsedAt(LocalDateTime.now());
        otpTokenRepository.save(otpToken);
        
        auditLogService.logAction(
            user.getEmail(),
            "PASSWORD_RESET",
            "Password reset successfully",
            getClientIp(httpRequest),
            httpRequest.getHeader("User-Agent"),
            "SUCCESS"
        );
        
        log.info("Password reset successfully for {}", user.getEmail());
    }
    
    private void handleFailedLogin(AdminUser user, HttpServletRequest httpRequest) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
            user.setAccountLockedUntil(lockUntil);
            adminUserRepository.save(user);
            
            auditLogService.logAction(
                user.getEmail(),
                "ACCOUNT_LOCKED",
                String.format("Account locked due to %d failed login attempts", attempts),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"),
                "WARNING"
            );
            
            log.warn("Account {} locked until {} due to failed login attempts", 
                user.getEmail(), lockUntil);
        } else {
            adminUserRepository.save(user);
            auditLogService.logAction(
                user.getEmail(),
                "LOGIN_FAILED",
                String.format("Failed login attempt %d/%d", attempts, MAX_LOGIN_ATTEMPTS),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"),
                "FAILURE"
            );
        }
    }
    
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    private void saveOtpToken(String email, String otp, OtpToken.OtpType type, String ipAddress) {
    	OtpToken token = new OtpToken();
    	token.setEmail(email);
    	token.setOtp(otp);
    	token.setOtpType(type);
    	token.setExpiresAt(LocalDateTime.now().plusSeconds(otpExpiration / 1000));
    	token.setIsUsed(false);
    	token.setIpAddress(ipAddress);
        otpTokenRepository.save(token);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
}
*/

package com.strms.demo.Services;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import javax.security.auth.login.AccountLockedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.strms.demo.DTOs.LoginRequest;
import com.strms.demo.DTOs.LoginResponse;
import com.strms.demo.DTOs.OtpVerificationRequest;
import com.strms.demo.DTOs.PasswordResetRequest;
import com.strms.demo.Entites.AdminUser;
import com.strms.demo.Entites.OtpToken;
import com.strms.demo.ExceptionHandling.InvalidCredentialsException;
import com.strms.demo.ExceptionHandling.InvalidOtpException;
import com.strms.demo.Repositories.AdminUserRepository;
import com.strms.demo.Repositories.OtpTokenRepository;
import com.strms.demo.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final AdminUserRepository adminUserRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    private final JwtUtil jwtUtil;
    
    @Value("${otp.expiration}")
    private Long otpExpiration;
    
    @Value("${otp.length}")
    private Integer otpLength;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    
    public AuthService(AdminUserRepository adminUserRepository, 
                      OtpTokenRepository otpTokenRepository,
                      PasswordEncoder passwordEncoder, 
                      EmailService emailService,
                      AuditLogService auditLogService, 
                      JwtUtil jwtUtil) {
        this.adminUserRepository = adminUserRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.auditLogService = auditLogService;
        this.jwtUtil = jwtUtil;
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) throws AccountLockedException {
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String email = request.getEmail();
        
        log.info("Login attempt for email: {}", email);
        
        // Check if user exists
        AdminUser user = adminUserRepository.findByEmail(email)
            .orElseThrow(() -> {
                auditLogService.logAction(
                    email,
                    "LOGIN_FAILED_USER_NOT_FOUND",
                    "User does not exist in system",
                    clientIp,
                    userAgent,
                    "FAILURE"
                );
                log.warn("Login attempt for non-existent user: {}", email);
                return new InvalidCredentialsException("Invalid email or password");
            });
        
        // Check if account is locked
        if (user.getAccountLockedUntil() != null && 
            user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            auditLogService.logAction(
                email,
                "LOGIN_ATTEMPT_ON_LOCKED_ACCOUNT",
                String.format("Account locked until %s. Remaining time: %d minutes", 
                    user.getAccountLockedUntil(), 
                    java.time.temporal.ChronoUnit.MINUTES.between(LocalDateTime.now(), user.getAccountLockedUntil())),
                clientIp,
                userAgent,
                "FAILURE"
            );
            log.warn("Login attempt on locked account: {} until {}", email, user.getAccountLockedUntil());
            throw new AccountLockedException(
                "Account is locked until " + user.getAccountLockedUntil() + ". Please try again later."
            );
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user, httpRequest);
            auditLogService.logAction(
                email,
                "LOGIN_INVALID_PASSWORD",
                String.format("Invalid password provided. Attempt %d/%d", 
                    user.getFailedLoginAttempts() + 1, MAX_LOGIN_ATTEMPTS),
                clientIp,
                userAgent,
                "FAILURE"
            );
            log.warn("Invalid password for user: {}", email);
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Reset failed attempts on successful password verification
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        adminUserRepository.save(user);
        
        auditLogService.logAction(
            email,
            "LOGIN_PASSWORD_VERIFIED",
            "Password verification successful",
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("Password verified successfully for user: {}", email);
        
        // Generate and send OTP
        String otp = generateOtp();
        saveOtpToken(user.getEmail(), otp, OtpToken.OtpType.LOGIN, clientIp);
        
        try {
            emailService.sendOtpEmail(user.getEmail(), otp, user.getName());
            auditLogService.logAction(
                email,
                "LOGIN_OTP_SENT",
                String.format("OTP generated and sent to email: %s (OTP expires in %d seconds)", 
                    user.getEmail(), otpExpiration / 1000),
                clientIp,
                userAgent,
                "SUCCESS"
            );
            log.info("Login OTP sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            auditLogService.logAction(
                email,
                "LOGIN_OTP_EMAIL_FAILED",
                String.format("Failed to send OTP email: %s", e.getMessage()),
                clientIp,
                userAgent,
                "FAILURE"
            );
            log.error("Failed to send OTP email for user: {}", email, e);
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }
        
        LoginResponse response = new LoginResponse();
        response.setStep("otp");
        response.setMessage("OTP sent to your email. Please verify to continue.");
        response.setEmail(user.getEmail());
        return response;
    }
    
    @Transactional
    public LoginResponse verifyOtp(OtpVerificationRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String email = request.getEmail();
        String otp = request.getOtp();
        
        log.info("OTP verification attempt for email: {}", email);
        
        OtpToken otpToken = otpTokenRepository
            .findByEmailAndOtpAndOtpTypeAndIsUsedFalse(
                email,
                otp,
                OtpToken.OtpType.LOGIN
            )
            .orElseThrow(() -> {
                auditLogService.logAction(
                    email,
                    "OTP_VERIFICATION_INVALID",
                    String.format("Invalid or non-existent OTP provided: %s", otp),
                    clientIp,
                    userAgent,
                    "FAILURE"
                );
                log.warn("Invalid OTP for user: {}", email);
                return new InvalidOtpException("Invalid or expired OTP");
            });

        if (!otpToken.isValid()) {
            auditLogService.logAction(
                email,
                "OTP_VERIFICATION_EXPIRED",
                String.format("OTP expired. Generated at: %s", otpToken.getCreatedAt()),
                clientIp,
                userAgent,
                "FAILURE"
            );
            log.warn("Expired OTP for user: {}", email);
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        AdminUser user = adminUserRepository.findByEmail(email)
            .orElseThrow(() -> {
                auditLogService.logAction(
                    email,
                    "OTP_VERIFICATION_USER_NOT_FOUND",
                    "User not found during OTP verification",
                    clientIp,
                    userAgent,
                    "FAILURE"
                );
                log.error("User not found during OTP verification: {}", email);
                return new InvalidCredentialsException("User not found");
            });

        // Mark OTP as used
        otpToken.setIsUsed(true);
        otpToken.setUsedAt(LocalDateTime.now());
        otpTokenRepository.save(otpToken);
        
        auditLogService.logAction(
            email,
            "OTP_MARKED_AS_USED",
            String.format("OTP marked as used at: %s", otpToken.getUsedAt()),
            clientIp,
            userAgent,
            "SUCCESS"
        );

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        user.setIsActive(true);
        adminUserRepository.save(user);
        
        auditLogService.logAction(
            email,
            "LAST_LOGIN_UPDATED",
            String.format("Last login updated to: %s", user.getLastLogin()),
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("Last login updated for user: {}", email);

        // Generate JWT tokens
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        
        auditLogService.logAction(
            email,
            "JWT_TOKENS_GENERATED",
            String.format("Access token and refresh token generated. Access token expires in: %d seconds", 
                jwtUtil.getTokenExpiration() / 1000),
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("JWT tokens generated for user: {}", email);

        // Set refresh token in httpOnly cookie
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        httpResponse.addCookie(refreshCookie);
        
        auditLogService.logAction(
            email,
            "LOGIN_SUCCESSFUL",
            String.format("User logged in successfully. Role: %s. Name: %s", 
                user.getRole().name(), user.getName()),
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("User logged in successfully: {} with role: {}", email, user.getRole().name());

        LoginResponse response = new LoginResponse();
        response.setStep("complete");
        response.setMessage("Login successful");
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().name());
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    @Transactional
    public void forgotPassword(String email, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        log.info("Forgot password request for email: {}", email);
        
        AdminUser user = adminUserRepository.findByEmail(email)
            .orElseThrow(() -> {
                auditLogService.logAction(
                    email,
                    "FORGOT_PASSWORD_USER_NOT_FOUND",
                    "Forgot password request for non-existent user",
                    clientIp,
                    userAgent,
                    "FAILURE"
                );
                log.warn("Forgot password request for non-existent user: {}", email);
                return new InvalidCredentialsException("User not found");
            });
        
        // Invalidate all existing password reset OTPs
        otpTokenRepository.invalidateAllByEmailAndType(email, OtpToken.OtpType.FORGOT_PASSWORD);
        
        auditLogService.logAction(
            email,
            "PREVIOUS_PASSWORD_RESET_OTPS_INVALIDATED",
            "All previous password reset OTPs have been invalidated",
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("Previous password reset OTPs invalidated for user: {}", email);
        
        // Generate and send OTP
        String otp = generateOtp();
        saveOtpToken(email, otp, OtpToken.OtpType.FORGOT_PASSWORD, clientIp);
        
        auditLogService.logAction(
            email,
            "PASSWORD_RESET_OTP_GENERATED",
            String.format("Password reset OTP generated. OTP expires in: %d seconds", 
                otpExpiration / 1000),
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("Password reset OTP generated for user: {}", email);
        
        try {
            emailService.sendPasswordResetEmail(email, otp, user.getName());
            auditLogService.logAction(
                email,
                "PASSWORD_RESET_OTP_SENT",
                String.format("Password reset OTP sent to email: %s", email),
                clientIp,
                userAgent,
                "SUCCESS"
            );
            log.info("Password reset OTP sent successfully to: {}", email);
        } catch (Exception e) {
            auditLogService.logAction(
                email,
                "PASSWORD_RESET_OTP_EMAIL_FAILED",
                String.format("Failed to send password reset OTP email: %s", e.getMessage()),
                clientIp,
                userAgent,
                "FAILURE"
            );
            log.error("Failed to send password reset OTP email for user: {}", email, e);
            throw new RuntimeException("Failed to send password reset email. Please try again.");
        }
    }
    
    @Transactional
    public void resetPassword(PasswordResetRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String email = request.getEmail();
        String otp = request.getOtp();
        
        log.info("Password reset attempt for email: {}", email);
        
        OtpToken otpToken = otpTokenRepository
            .findByEmailAndOtpAndOtpTypeAndIsUsedFalse(
                email,
                otp,
                OtpToken.OtpType.FORGOT_PASSWORD
            )
            .orElseThrow(() -> {
                auditLogService.logAction(
                    email,
                    "PASSWORD_RESET_INVALID_OTP",
                    String.format("Invalid or non-existent OTP provided for password reset: %s", otp),
                    clientIp,
                    userAgent,
                    "FAILURE"
                );
                log.warn("Invalid password reset OTP for user: {}", email);
                return new InvalidOtpException("Invalid or expired OTP");
            });
        
        if (!otpToken.isValid()) {
            auditLogService.logAction(
                email,
                "PASSWORD_RESET_EXPIRED_OTP",
                String.format("Password reset OTP expired. Generated at: %s", otpToken.getCreatedAt()),
                clientIp,
                userAgent,
                "FAILURE"
            );
            log.warn("Expired password reset OTP for user: {}", email);
            throw new InvalidOtpException("Invalid or expired OTP");
        }
        
        AdminUser user = adminUserRepository.findByEmail(email)
            .orElseThrow(() -> {
                auditLogService.logAction(
                    email,
                    "PASSWORD_RESET_USER_NOT_FOUND",
                    "User not found during password reset",
                    clientIp,
                    userAgent,
                    "FAILURE"
                );
                log.error("User not found during password reset: {}", email);
                return new InvalidCredentialsException("User not found");
            });
        
        // Update password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        adminUserRepository.save(user);
        
        auditLogService.logAction(
            email,
            "PASSWORD_UPDATED",
            String.format("User password updated successfully at: %s", LocalDateTime.now()),
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("Password updated successfully for user: {}", email);
        
        // Mark OTP as used
        otpToken.setIsUsed(true);
        otpToken.setUsedAt(LocalDateTime.now());
        otpTokenRepository.save(otpToken);
        
        auditLogService.logAction(
            email,
            "PASSWORD_RESET_OTP_MARKED_USED",
            String.format("Password reset OTP marked as used at: %s", otpToken.getUsedAt()),
            clientIp,
            userAgent,
            "SUCCESS"
        );
        
        auditLogService.logAction(
            email,
            "PASSWORD_RESET_COMPLETE",
            String.format("Password reset completed successfully. User can login with new password."),
            clientIp,
            userAgent,
            "SUCCESS"
        );
        log.info("Password reset completed for user: {}", email);
    }
    
    private void handleFailedLogin(AdminUser user, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String email = user.getEmail();
        
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
            user.setAccountLockedUntil(lockUntil);
            adminUserRepository.save(user);
            
            auditLogService.logAction(
                email,
                "ACCOUNT_LOCKED_FAILED_ATTEMPTS",
                String.format("Account locked due to %d failed login attempts. Lock duration: %d minutes. Locked until: %s", 
                    attempts, LOCK_DURATION_MINUTES, lockUntil),
                clientIp,
                userAgent,
                "WARNING"
            );
            log.warn("Account locked for user: {} until: {} due to {} failed login attempts", 
                email, lockUntil, attempts);
        } else {
            adminUserRepository.save(user);
            auditLogService.logAction(
                email,
                "LOGIN_FAILED_INVALID_CREDENTIALS",
                String.format("Failed login attempt %d/%d. Remaining attempts before lock: %d", 
                    attempts, MAX_LOGIN_ATTEMPTS, MAX_LOGIN_ATTEMPTS - attempts),
                clientIp,
                userAgent,
                "FAILURE"
            );
            log.warn("Failed login attempt for user: {} ({}/{})", email, attempts, MAX_LOGIN_ATTEMPTS);
        }
    }
    
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        log.debug("OTP generated with length: {}", otpLength);
        return otp.toString();
    }
    
    private void saveOtpToken(String email, String otp, OtpToken.OtpType type, String ipAddress) {
        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setOtpType(type);
        token.setExpiresAt(LocalDateTime.now().plusSeconds(otpExpiration / 1000));
        token.setIsUsed(false);
        token.setIpAddress(ipAddress);
        otpTokenRepository.save(token);
        log.info("OTP token saved for email: {} with type: {} and IP: {}", email, type, ipAddress);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}