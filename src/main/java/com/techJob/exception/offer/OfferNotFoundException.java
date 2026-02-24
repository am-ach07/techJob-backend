package com.techJob.exception.offer;

import com.techJob.exception.BusinessException;

public class OfferNotFoundException extends BusinessException {

	public OfferNotFoundException(String publicID) {
		super("Offer Not found:"+publicID);
		// TODO Auto-generated constructor stub
	}

}
