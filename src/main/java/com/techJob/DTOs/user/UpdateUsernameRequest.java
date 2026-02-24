package com.techJob.DTOs.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateUsernameRequest {

	@NotBlank
	@Pattern(regexp = "^[a-z][a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
}
