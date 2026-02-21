package com.exception.order;

import com.exception.BusinessException;

public class OrderNotFoundException extends BusinessException {

	public OrderNotFoundException(String publicID) {
		super("Order Not Found :"+publicID);
		// TODO Auto-generated constructor stub
	}

}
