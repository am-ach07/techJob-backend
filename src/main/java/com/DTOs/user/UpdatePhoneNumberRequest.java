package com.DTOs.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdatePhoneNumberRequest {

	
	@NotBlank
	@Pattern(regexp = "^(?:\\+213|0)(5|6|7)\\d{8}$",message = "Invalid Algerian phone number") 
	private String phoneNumber;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
	
}
