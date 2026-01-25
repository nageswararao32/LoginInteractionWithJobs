package com.strms.demo.Entites;


import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "admin_users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
public class AdminUser implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;
    
    // Constructors
    public AdminUser() {
    }
    
    public AdminUser(Long id, String email, String name, String password, Role role, 
                    Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, 
                    LocalDateTime lastLogin, Integer failedLoginAttempts, 
                    LocalDateTime accountLockedUntil) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
        this.failedLoginAttempts = failedLoginAttempts;
        this.accountLockedUntil = accountLockedUntil;
    }
    
    // Builder pattern
    public static AdminUserBuilder builder() {
        return new AdminUserBuilder();
    }
    
    public static class AdminUserBuilder {
        private Long id;
        private String email;
        private String name;
        private String password;
        private Role role;
        private Boolean isActive = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLogin;
        private Integer failedLoginAttempts = 0;
        private LocalDateTime accountLockedUntil;
        
        public AdminUserBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public AdminUserBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public AdminUserBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public AdminUserBuilder password(String password) {
            this.password = password;
            return this;
        }
        
        public AdminUserBuilder role(Role role) {
            this.role = role;
            return this;
        }
        
        public AdminUserBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }
        
        public AdminUserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public AdminUserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public AdminUserBuilder lastLogin(LocalDateTime lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }
        
        public AdminUserBuilder failedLoginAttempts(Integer failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
            return this;
        }
        
        public AdminUserBuilder accountLockedUntil(LocalDateTime accountLockedUntil) {
            this.accountLockedUntil = accountLockedUntil;
            return this;
        }
        
        public AdminUser build() {
            return new AdminUser(id, email, name, password, role, isActive, createdAt, 
                               updatedAt, lastLogin, failedLoginAttempts, accountLockedUntil);
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    // UserDetails method - implements interface
    @Override
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getAccountLockedUntil() {
        return accountLockedUntil;
    }
    
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountLockedUntil == null || accountLockedUntil.isBefore(LocalDateTime.now());
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
    
    public enum Role {
        ADMIN,
        USER // For future implementation
    }
}