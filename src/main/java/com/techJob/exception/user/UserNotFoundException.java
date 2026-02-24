package com.techJob.exception.user;

import com.techJob.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
	
		public UserNotFoundException(String str) {
		super("User :  " + str + " not found.");
	}

}
