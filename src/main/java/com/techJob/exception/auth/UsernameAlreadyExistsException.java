package com.techJob.exception.auth;

import com.techJob.exception.BusinessException;

public class UsernameAlreadyExistsException extends BusinessException {

	public UsernameAlreadyExistsException(String username) {
		super("username Aready exists :"+username);
		// TODO Auto-generated constructor stub
	}

}
