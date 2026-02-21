package com.exception.offer;

import com.exception.BusinessException;

public class OfferNotFoundException extends BusinessException {

	public OfferNotFoundException(String publicID) {
		super("Offer Not found:"+publicID);
		// TODO Auto-generated constructor stub
	}

}
