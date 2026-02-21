package com.exception.auth;

import com.exception.BusinessException;

public class TokenExpiredException extends BusinessException {
	public TokenExpiredException(String message) {
		super(message);
	}

}
