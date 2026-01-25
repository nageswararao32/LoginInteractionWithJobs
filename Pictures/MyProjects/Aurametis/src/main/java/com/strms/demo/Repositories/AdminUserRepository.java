package com.strms.demo.Repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.strms.demo.Entites.AdminUser;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Modifying
    @Query("UPDATE AdminUser a SET a.failedLoginAttempts = :attempts WHERE a.email = :email")
    void updateFailedLoginAttempts(String email, Integer attempts);
    
    @Modifying
    @Query("UPDATE AdminUser a SET a.accountLockedUntil = :lockUntil WHERE a.email = :email")
    void lockAccount(String email, LocalDateTime lockUntil);
    
    @Modifying
    @Query("UPDATE AdminUser a SET a.lastLogin = :loginTime WHERE a.email = :email")
    void updateLastLogin(String email, LocalDateTime loginTime);
}
