package com.techJob.exception.auth;

import com.techJob.exception.BusinessException;

public class TokenExpiredException extends BusinessException {
	public TokenExpiredException(String message) {
		super(message);
	}

}
