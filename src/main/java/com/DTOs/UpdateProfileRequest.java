package com.DTOs;

import com.DTOs.artisan.UpdateArtisanDTO;
import com.DTOs.user.UpdateUserDTO;

import jakarta.validation.Valid;

public class UpdateProfileRequest {

	
	  private UpdateUserDTO user;
	
	    private UpdateArtisanDTO artisan;
		public UpdateUserDTO getUser() {
			return user;
		}
		public void setUser(UpdateUserDTO user) {
			this.user = user;
		}
		public UpdateArtisanDTO getArtisan() {
			return artisan;
		}
		public void setArtisan(UpdateArtisanDTO artisan) {
			this.artisan = artisan;
		}
	    
	    
	    
}
