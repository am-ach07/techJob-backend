package com.techJob.DTOs.serviceOffer;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;

public class UpdateExtraServiceOfferDTO {
	
	
	
	
	private String title;
	@DecimalMin(value = "0.1", inclusive = false, message = "Price must be greater than 0")
	private BigDecimal price;

	

	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
