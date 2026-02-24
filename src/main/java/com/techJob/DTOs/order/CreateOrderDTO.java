package com.techJob.DTOs.order;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderDTO {

	
	private List<String> extraOfferPublicID = new ArrayList<>();

	public List<String> getExtraOfferPublicID() {
		return extraOfferPublicID;
	}

	public void setExtraOfferPublicID(List<String> extraOfferPublicID) {
		this.extraOfferPublicID = extraOfferPublicID;
	}

	

	
	
}
