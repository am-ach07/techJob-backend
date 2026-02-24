package com.techJob.DTOs.artisan;

import java.util.List;

public class ArtisanDTO  {

	private String description;
	private Integer ratingCount;
	private Double averageRating;
	private Integer completedOrder;
	private Boolean verified;
	private List<ArtisanSkillDTO> skills;
	
	
	
	
	// Getters and Setters
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public Integer getRatingCount() {
		return ratingCount;
	}
	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}
	public Double getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}
	public Integer getCompletedOrder() {
		return completedOrder;
	}
	public void setCompletedOrder(Integer completedOrder) {
		this.completedOrder = completedOrder;
	}
	public Boolean getVerified() {
		return verified;
	}
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
	public List<ArtisanSkillDTO> getSkills() {
		return skills;
	}
	public void setSkills(List<ArtisanSkillDTO> skills) {
		this.skills = skills;
	}
	
	
	

}
