package com.strms.demo.Entites;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens", indexes = {
    @Index(name = "idx_email_type", columnList = "email, otp_type"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Data
@Builder
public class OtpToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 10)
    private String otp;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false, length = 20)
    private OtpType otpType;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return !isUsed && !isExpired();
    }
    
    public enum OtpType {
        LOGIN,
        FORGOT_PASSWORD,
        EMAIL_VERIFICATION
    }
    
    public OtpToken() {
		
	}
    
	public OtpToken(Long id, String email, String otp, OtpType otpType, LocalDateTime createdAt,
			LocalDateTime expiresAt, Boolean isUsed, LocalDateTime usedAt, String ipAddress) {
		super();
		this.id = id;
		this.email = email;
		this.otp = otp;
		this.otpType = otpType;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
		this.isUsed = isUsed;
		this.usedAt = usedAt;
		this.ipAddress = ipAddress;
	}

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

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public OtpType getOtpType() {
		return otpType;
	}

	public void setOtpType(OtpType otpType) {
		this.otpType = otpType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public LocalDateTime getUsedAt() {
		return usedAt;
	}

	public void setUsedAt(LocalDateTime usedAt) {
		this.usedAt = usedAt;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
    
    
    
}
