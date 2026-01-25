package com.strms.demo.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
	
	public ForgotPasswordRequest() {
		// TODO Auto-generated constructor stub
	}

	public ForgotPasswordRequest(
			@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
