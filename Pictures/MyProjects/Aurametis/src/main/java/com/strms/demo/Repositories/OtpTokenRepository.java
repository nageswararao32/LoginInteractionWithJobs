package com.strms.demo.Repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.strms.demo.Entites.OtpToken;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByEmailAndOtpAndOtpTypeAndIsUsedFalse(
        String email, 
        String otp, 
        OtpToken.OtpType otpType
    );
    
    @Modifying
    @Query("UPDATE OtpToken o SET o.isUsed = true, o.usedAt = :usedAt WHERE o.id = :id")
    void markAsUsed(Long id, LocalDateTime usedAt);
    
    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    @Modifying
    @Query("UPDATE OtpToken o SET o.isUsed = true WHERE o.email = :email AND o.otpType = :type AND o.isUsed = false")
    void invalidateAllByEmailAndType(String email, OtpToken.OtpType type);
}
