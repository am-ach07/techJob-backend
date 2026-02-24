package com.techJob.exception.auth;

import com.techJob.exception.BusinessException;

public class PhoneNumberAlreadyExistsException extends BusinessException {

	public PhoneNumberAlreadyExistsException(String phoneNumber) {
		super("Phone number Already exist :"+phoneNumber);
		// TODO Auto-generated constructor stub
	}

}
