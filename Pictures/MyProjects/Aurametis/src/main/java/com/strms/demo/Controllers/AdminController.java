package com.strms.demo.Controllers;


import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.strms.demo.DTOs.ApiResponse;
import com.strms.demo.Entites.AuditLog;
import com.strms.demo.Services.AuditLogService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    
    private final AuditLogService auditLogService;
    
    public AdminController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardData>> getDashboard(Authentication authentication) {
        log.info("Dashboard accessed by: {}", authentication.getName());
        
        DashboardData data = new DashboardData();
        data.setUserName(authentication.getName());
        data.setCurrentTime(LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success(data, "Dashboard data retrieved"));
    }
    
    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String action
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        
        Page<AuditLog> logs;
        if (userEmail != null && !userEmail.isEmpty()) {
            logs = auditLogService.getLogsByUser(userEmail, pageRequest);
        } else if (action != null && !action.isEmpty()) {
            logs = auditLogService.getLogsByAction(action, pageRequest);
        } else {
            logs = auditLogService.getAllLogs(pageRequest);
        }
        
        return ResponseEntity.ok(ApiResponse.success(logs, "Audit logs retrieved"));
    }
    
    @GetMapping("/audit-logs/recent")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getRecentLogs() {
        List<AuditLog> logs = auditLogService.getRecentLogs();
        return ResponseEntity.ok(ApiResponse.success(logs, "Recent audit logs retrieved"));
    }
    

    public static class DashboardData {
        private String userName;
        private LocalDateTime currentTime;
        
        public DashboardData(){
        	
        }
        
		public DashboardData(String userName, LocalDateTime currentTime) {
			super();
			this.userName = userName;
			this.currentTime = currentTime;
		}



		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public LocalDateTime getCurrentTime() {
			return currentTime;
		}
		public void setCurrentTime(LocalDateTime currentTime) {
			this.currentTime = currentTime;
		}
        
        
    }
}
