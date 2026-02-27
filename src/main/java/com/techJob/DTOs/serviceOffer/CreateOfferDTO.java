package com.techJob.DTOs.serviceOffer;

import java.math.BigDecimal;

import com.techJob.domain.enums.Category;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateOfferDTO {

	@Size(min = 3, message = "Tiltle must be at least 3 characters")
	@NotBlank(message = "you didn't enser a title!!!")
	private String title;
	@NotBlank(message = "you didn't enser a description!!!")
	private String description;
	@NotNull(message = "you didn't enser a category!!!")
	private Category category;
	@NotNull(message = "you didn't enser a price!!!")
	@DecimalMin(value = "0.01", inclusive = true, message = "Price must be positive")
	private BigDecimal price;
	

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
