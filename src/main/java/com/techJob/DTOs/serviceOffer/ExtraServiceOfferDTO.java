package com.techJob.DTOs.serviceOffer;

import java.math.BigDecimal;
import java.util.Set;

public class ExtraServiceOfferDTO {
	
	private String title;
	private BigDecimal price;
	private String extraOfferPublicID;
	
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

	public String getExtraOfferPublicID() {
		return extraOfferPublicID;
	}

	public void setExtraOfferPublicID(String extraOfferPublicID) {
		this.extraOfferPublicID = extraOfferPublicID;
	}
	

}
