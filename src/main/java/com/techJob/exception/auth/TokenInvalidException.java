package com.techJob.exception.auth;

import com.techJob.exception.BusinessException;

public class TokenInvalidException extends BusinessException {
	public TokenInvalidException(String message) {
		super(message);
	}

}
