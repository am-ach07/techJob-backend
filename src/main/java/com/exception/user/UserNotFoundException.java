package com.exception.user;

import com.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
	
		public UserNotFoundException(String str) {
		super("User :  " + str + " not found.");
	}

}
