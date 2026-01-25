package com.strms.demo.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetRequest {
	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 6, message = "OTP must be 4-6 digits")
    @Pattern(regexp = "^[0-9]+$", message = "OTP must contain only numbers")
    private String otp;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
    )
    private String newPassword;
    
    public PasswordResetRequest() {
		// TODO Auto-generated constructor stub
	}

	public PasswordResetRequest(
			@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
			@NotBlank(message = "OTP is required") @Size(min = 4, max = 6, message = "OTP must be 4-6 digits") @Pattern(regexp = "^[0-9]+$", message = "OTP must contain only numbers") String otp,
			@NotBlank(message = "New password is required") @Size(min = 8, message = "Password must be at least 8 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character") String newPassword) {
		super();
		this.email = email;
		this.otp = otp;
		this.newPassword = newPassword;
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

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
    
    
}
