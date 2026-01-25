package com.strms.demo.DTOs;

public class LoginResponse {
	private String step;
    private String message;
    private String email;
    private String name;
    private String role;
    private String accessToken;
    private String refreshToken;
    
    public LoginResponse() {
		// TODO Auto-generated constructor stub
	}

	public LoginResponse(String step, String message, String email, String name, String role, String accessToken,
			String refreshToken) {
		super();
		this.step = step;
		this.message = message;
		this.email = email;
		this.name = name;
		this.role = role;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
    
    
}
