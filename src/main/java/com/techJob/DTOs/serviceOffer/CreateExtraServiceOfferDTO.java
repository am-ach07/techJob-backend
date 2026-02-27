package com.techJob.DTOs.serviceOffer;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateExtraServiceOfferDTO {
	@Size(min = 3, message = "Tiltle must be at least 3 characters")
	@NotBlank(message = "you didn't enser a title!!!")
	private String title;
	@NotNull(message = "you didn't enser a price!!!")
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
