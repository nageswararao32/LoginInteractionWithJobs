package com.strms.demo.Controllers;

import java.time.LocalDateTime;
import java.util.Map;

import javax.security.auth.login.AccountLockedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.strms.demo.DTOs.ApiResponse;
import com.strms.demo.DTOs.ForgotPasswordRequest;
import com.strms.demo.DTOs.LoginRequest;
import com.strms.demo.DTOs.LoginResponse;
import com.strms.demo.DTOs.OtpVerificationRequest;
import com.strms.demo.DTOs.PasswordResetRequest;
import com.strms.demo.Entites.AdminUser;
import com.strms.demo.ExceptionHandling.InvalidRefreshTokenException;
import com.strms.demo.Repositories.AuditLogRepository;
import com.strms.demo.Services.AuthService;
import com.strms.demo.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }
    
    // ✅ LOGIN: Send email and password, receive OTP
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) throws AccountLockedException {
        log.info("Login attempt for email: {}", request.getEmail());
        LoginResponse response = authService.login(request, httpRequest);
        System.out.println(response);
        log.info("✅ Login response for {}: step={}", request.getEmail(), response.getStep());
        
        return ResponseEntity.ok(ApiResponse.success(response, "OTP sent successfully"));
    }
    
    // ✅ VERIFY OTP: Send OTP, receive tokens
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        log.info("OTP verification for email: {}", request.getEmail());
        
        try {
            LoginResponse response = authService.verifyOtp(request, httpRequest, httpResponse);
            
            setRefreshTokenCookie(httpResponse, response.getRefreshToken());
            
            log.info("✅ OTP verified for {}", request.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
        } catch (Exception e) {
            log.error("OTP verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("OTP verification failed"));
        }
    }
    
    // ✅ FORGOT PASSWORD: Send OTP for password reset
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Password reset requested for email: {}", request.getEmail());
        
        try {
            authService.forgotPassword(request.getEmail(), httpRequest);
            return ResponseEntity.ok(ApiResponse.success(null, "Password reset OTP sent to your email"));
        } catch (Exception e) {
            log.error("Forgot password error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email not found or error occurred"));
        }
    }
    
    // ✅ RESET PASSWORD: Verify OTP and reset password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Password reset for email: {}", request.getEmail());
        
        try {
            authService.resetPassword(request, httpRequest);
            return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
        } catch (Exception e) {
            log.error("Password reset error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Password reset failed"));
        }
    }
    
    // ✅ HEALTH CHECK: Verify service is running
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        log.info("Health check called");
        return ResponseEntity.ok(ApiResponse.success("OK", "Service is running"));
    }
    
    // ✅ REFRESH TOKEN: Accept refresh token and return new access token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Token refresh attempt for email: {}", request.getEmail());
        
        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Refresh token missing"));
        }

        try {
            // ✅ Validate refresh token and get user
            AdminUser user = jwtUtil.validateRefreshToken(request.getRefreshToken());
            
            // ✅ Email validation for security
            if (!user.getEmail().equals(request.getEmail())) {
                log.warn("Email mismatch for token refresh: {} vs {}", 
                        user.getEmail(), request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Email mismatch"));
            }
            
            // ✅ Generate new access token
            String newAccessToken = jwtUtil.generateToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user); // Optional: rotate refresh token
            
            log.info("✅ Token refreshed successfully for user: {}", user.getEmail());
            
            // ✅ Build response
            LoginResponse response = new LoginResponse();
            response.setStep("complete");
            response.setMessage("Token refreshed successfully");
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setRole(user.getRole().name());
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
            
        } catch (InvalidRefreshTokenException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid or expired refresh token"));
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Token refresh failed"));
        }
    }

    @Autowired

    private AuditLogRepository auditLogRepository;
    
    /* 
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) java.util.Map<String, String> body,
            HttpServletResponse response
    ) {
        String email = body != null ? body.get("email") : "unknown";
        
        log.info("🔴 Logout attempt for user: {}", email);
        
        try {
            // ✅ Extract and validate JWT token
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                
                // Optional: Validate token before logout
                if (jwtUtil.validateToken(token)) {
                    log.info("✅ JWT token validated successfully");
                }
            }
            
            // ✅ Clear refresh token cookie
            clearRefreshTokenCookie(response);
            
            log.info("✅ Logout successful for user: {}", email);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
            
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            // Still clear the cookie even if there's an error
            clearRefreshTokenCookie(response);
            return ResponseEntity.ok(ApiResponse.success(null, "Logged out"));
        }
    }
    */

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

   @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String email = body != null ? body.get("email") : "unknown";
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("🔴 Logout attempt initiated for user: {}", email);

        try {
            String token = null;
            boolean tokenValid = false;

            // ✅ Extract and validate JWT token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);

                try {
                    if (jwtUtil.validateToken(token)) {
                        email = jwtUtil.getEmailFromToken(token);
                        tokenValid = true;
                        log.info("✅ JWT token validated successfully for: {}", email);

                        auditLogRepository.save(createAuditLog(
                                email,
                                "LOGOUT_TOKEN_VALIDATED",
                                "JWT token validated successfully",
                                clientIp,
                                userAgent,
                                "SUCCESS"
                        ));
                    } else {
                        log.warn("⚠️ JWT token validation failed");
                        auditLogRepository.save(createAuditLog(
                                email,
                                "LOGOUT_TOKEN_INVALID",
                                "Attempted logout with invalid JWT token",
                                clientIp,
                                userAgent,
                                "WARNING"
                        ));
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Error validating JWT token: {}", e.getMessage());
                    auditLogRepository.save(createAuditLog(
                            email,
                            "LOGOUT_TOKEN_VALIDATION_ERROR",
                            String.format("Error validating JWT token: %s", e.getMessage()),
                            clientIp,
                            userAgent,
                            "WARNING"
                    ));
                }
            } else {
                log.warn("⚠️ No valid Authorization header provided for logout");
                auditLogRepository.save(createAuditLog(
                        email,
                        "LOGOUT_NO_AUTH_HEADER",
                        "Logout attempt without Authorization header",
                        clientIp,
                        userAgent,
                        "WARNING"
                ));
            }

            // ✅ Log pre-cookie-clear state
            if (tokenValid) {
                auditLogRepository.save(createAuditLog(
                        email,
                        "LOGOUT_SESSION_TERMINATION",
                        String.format("Session termination initiated at: %s", LocalDateTime.now()),
                        clientIp,
                        userAgent,
                        "SUCCESS"
                ));
                log.info("📋 Session termination logged for: {}", email);
            }

            // ✅ Clear refresh token cookie
            clearRefreshTokenCookie(httpResponse);
            log.info("🍪 Refresh token cookie cleared");

            auditLogRepository.save(createAuditLog(
                    email,
                    "LOGOUT_REFRESH_TOKEN_CLEARED",
                    "Refresh token cookie cleared from client",
                    clientIp,
                    userAgent,
                    "SUCCESS"
            ));

            // ✅ Clear access token (client-side)
            log.info("🔓 Access token cleared from client");

            auditLogRepository.save(createAuditLog(
                    email,
                    "LOGOUT_ACCESS_TOKEN_CLEARED",
                    "Access token cleared from client storage",
                    clientIp,
                    userAgent,
                    "SUCCESS"
            ));

            // ✅ Final logout success log
            log.info("✅ Logout completed successfully for user: {}", email);

            auditLogRepository.save(createAuditLog(
                    email,
                    "LOGOUT_SUCCESSFUL",
                    String.format("User logged out successfully at: %s. Session ended.", 
                            LocalDateTime.now()),
                    clientIp,
                    userAgent,
                    "SUCCESS"
            ));

            return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));

        } catch (Exception e) {
            log.error("❌ Logout error for user {}: {}", email, e.getMessage());

            // ✅ Log the error even if it occurs
            try {
                auditLogRepository.save(createAuditLog(
                        email,
                        "LOGOUT_ERROR",
                        String.format("Error during logout process: %s", e.getMessage()),
                        clientIp,
                        userAgent,
                        "FAILURE"
                ));
            } catch (Exception auditError) {
                log.error("Failed to save logout error audit log: {}", auditError.getMessage());
            }

            // Still clear the cookie even if there's an error
            try {
                clearRefreshTokenCookie(httpResponse);
                log.info("🍪 Refresh token cookie cleared despite error");
            } catch (Exception cookieError) {
                log.error("Failed to clear refresh token cookie: {}", cookieError.getMessage());
            }

            return ResponseEntity.ok(ApiResponse.success(null, "Logged out"));
        }
    }
  
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)                 // ✅ JS can't access
                .secure(true)                   // ✅ HTTPS only
                .path("/api/auth")              // ✅ auth endpoints only
                .maxAge(7 * 24 * 60 * 60)        // ✅ 7 days
                .sameSite("Strict")             // ✅ CSRF protection
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.debug("Refresh token cookie set");
    }
    
    // ✅ HELPER: Clear refresh token cookie
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);  // Delete cookie immediately
        response.addCookie(cookie);
        log.debug("Refresh token cookie cleared");
    }


    private com.strms.demo.Entites.AuditLog createAuditLog(
            String email,
            String actionType,
            String description,
            String ipAddress,
            String userAgent,
            String status
    ) {
        com.strms.demo.Entites.AuditLog auditLog = new com.strms.demo.Entites.AuditLog();
        auditLog.setUserEmail(email);
        auditLog.setAction(actionType);
        auditLog.setDetails(description);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setStatus(status);
        auditLog.setTimestamp(LocalDateTime.now());
        return auditLog;
    }
}

// ✅ DTO for refresh token request
class RefreshTokenRequest {
    private String email;
    private String refreshToken;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
