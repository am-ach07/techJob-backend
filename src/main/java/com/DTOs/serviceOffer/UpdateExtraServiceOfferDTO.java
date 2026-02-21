package com.DTOs.serviceOffer;

import java.math.BigDecimal;

public class UpdateExtraServiceOfferDTO {
	
	private String title;
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
