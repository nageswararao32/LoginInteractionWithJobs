package com.strms.demo.Controllers;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.strms.demo.Repositories.OtpTokenRepository;

@Component
public class ScheduledTasks {
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    
    private final OtpTokenRepository otpTokenRepository;
    
    public ScheduledTasks(OtpTokenRepository otpTokenRepository) {
        this.otpTokenRepository = otpTokenRepository;
    }
    
    // Run every hour to clean up expired OTP tokens
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredOtpTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            otpTokenRepository.deleteExpiredTokens(now);
            log.info("Cleaned up expired OTP tokens at {}", now);
        } catch (Exception e) {
            log.error("Error cleaning up expired OTP tokens: {}", e.getMessage());
        }
    }
}
