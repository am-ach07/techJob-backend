package com.exception.auth;

import com.exception.BusinessException;

public class RefreshTokenException extends BusinessException{

	public RefreshTokenException(String message) {
		super(message);
	}
	

}
