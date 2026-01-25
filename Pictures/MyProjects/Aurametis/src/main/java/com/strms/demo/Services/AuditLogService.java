package com.strms.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.strms.demo.Entites.AuditLog;
import com.strms.demo.Repositories.AuditLogRepository;

@Service
public class AuditLogService {
    
    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);
    
    private final AuditLogRepository auditLogRepository;
    
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    @Async
    public void logAction(String userEmail, String action, String details, 
                         String ipAddress, String userAgent, String status) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserEmail(userEmail);
            auditLog.setAction(action);
            auditLog.setDetails(details);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setStatus(status);
            auditLog.setTimestamp(LocalDateTime.now());
            
            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {} - {}", userEmail, action, status);
            
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage());
        }
    }
    
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    public Page<AuditLog> getLogsByUser(String userEmail, Pageable pageable) {
        return auditLogRepository.findByUserEmail(userEmail, pageable);
    }
    
    public Page<AuditLog> getLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }
    
    public Page<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(start, end, pageable);
    }
    
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }
}
