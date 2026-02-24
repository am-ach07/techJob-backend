package com.techJob.DTOs.image;

import java.time.LocalDateTime;
import java.util.UUID;

public class ImageDTO {

    private String imagePublicID;
    private String url;
    private Integer position;	
    private LocalDateTime createdAt;
    
    
    
    
    
	public ImageDTO( String url, Integer position) {
		super();
		this.imagePublicID =UUID.randomUUID().toString();
		this.url = url;
		this.position = position;
	}
	public String getImagePublicID() {
		return imagePublicID;
	}
	public void setImagePublicID(String imagePublicID) {
		this.imagePublicID = imagePublicID;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
