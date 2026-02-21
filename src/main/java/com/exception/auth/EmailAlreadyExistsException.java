package com.exception.auth;

import com.exception.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {

	public EmailAlreadyExistsException(String email) {
		super("email Already exists :"+email);
		// TODO Auto-generated constructor stub
	}

}
