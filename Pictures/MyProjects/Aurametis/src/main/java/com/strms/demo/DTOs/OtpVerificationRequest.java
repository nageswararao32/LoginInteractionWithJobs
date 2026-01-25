package com.strms.demo.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OtpVerificationRequest {
	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 6, message = "OTP must be 4-6 digits")
    @Pattern(regexp = "^[0-9]+$", message = "OTP must contain only numbers")
    private String otp;
    
    public OtpVerificationRequest() {
		// TODO Auto-generated constructor stub
	}

	public OtpVerificationRequest(
			@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
			@NotBlank(message = "OTP is required") @Size(min = 4, max = 6, message = "OTP must be 4-6 digits") @Pattern(regexp = "^[0-9]+$", message = "OTP must contain only numbers") String otp) {
		super();
		this.email = email;
		this.otp = otp;
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
    
}
