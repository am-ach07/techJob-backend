package com.DTOs.serviceOffer;

import java.math.BigDecimal;

import com.domain.enums.Category;


public class UpdateServiceOfferDTO {

	
	
	private String title;
	private String description;
	private BigDecimal price;
	private Category category;
	
	
	

	//getter and setter
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

	
	
}
