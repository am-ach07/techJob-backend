package com.techJob.DTOs.artisan;

import java.util.List;

public class UpdateArtisanDTO  {
	

	private String description;
    private List <ArtisanSkillDTO> skills;
	
	// Getters and Setters
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ArtisanSkillDTO> getSkills() {
		return skills;
	}
	public void setSkills(List<ArtisanSkillDTO> skills) {
		this.skills = skills;
	}
	
	
	
	

}
