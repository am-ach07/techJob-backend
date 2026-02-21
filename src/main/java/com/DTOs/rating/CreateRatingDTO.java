package com.DTOs.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CreateRatingDTO {

    @Min(1)
    @Max(5)
    private Double stars;

    private String comment;

	

	

	

	public Double getStars() {
		return stars;
	}

	public void setStars(Double stars) {
		this.stars = stars;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
    
    
    
}
