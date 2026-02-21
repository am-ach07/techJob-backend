package com.response;

import com.DTOs.artisan.ArtisanDTO;
import com.DTOs.user.UserDTO;

public class ProfileResponse {
	
	private UserDTO user;
	private ArtisanDTO artisan;
	
	// getter and setter
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
	public ArtisanDTO getArtisan() {
		return artisan;
	}
	public void setArtisan(ArtisanDTO artisan) {
		this.artisan = artisan;
	}
	
	
	
}
