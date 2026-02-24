package com.techJob.DTOs.order;

import java.math.BigDecimal;

import com.techJob.DTOs.serviceOffer.ExtraServiceOfferDTO;

public class OrderExtraDTO {

	
	
	private BigDecimal extraPriceSnapshot;
	private String extraTitleSnapshot;
    private String orderExtraPublicID;
	private ExtraServiceOfferDTO extra;

	
	//==========getter and setter============
	public BigDecimal getExtraPriceSnapshot() {
		return extraPriceSnapshot;
	}
	public void setExtraPriceSnapshot(BigDecimal extraPriceSnapshot) {
		this.extraPriceSnapshot = extraPriceSnapshot;
	}
	public String getExtraTitleSnapshot() {
		return extraTitleSnapshot;
	}
	public void setExtraTitleSnapshot(String extraTitleSnapshot) {
		this.extraTitleSnapshot = extraTitleSnapshot;
	}
	public String getOrderExtraPublicID() {
		return orderExtraPublicID;
	}
	public void setOrderExtraPublicID(String orderExtraPublicID) {
		this.orderExtraPublicID = orderExtraPublicID;
	}
	public ExtraServiceOfferDTO getExtra() {
		return extra;
	}
	public void setExtra(ExtraServiceOfferDTO extra) {
		this.extra = extra;
	}
	
	
	
	
	
	
	
}
