package com.DTOs.rating;

import java.time.LocalDateTime;

public class RatingDTO {

	
	private Double stars; // 1..5

    private String comment;

    private LocalDateTime createdAt;

    
    //getter and setter


	public String getComment() {
		return comment;
	}

	public Double getStars() {
		return stars;
	}

	public void setStars(Double stars) {
		this.stars = stars;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
    
    
	
}
