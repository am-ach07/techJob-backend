package com.exception.auth;

import com.exception.BusinessException;

public class UsernameAlreadyExistsException extends BusinessException {

	public UsernameAlreadyExistsException(String username) {
		super("username Aready exists :"+username);
		// TODO Auto-generated constructor stub
	}

}
