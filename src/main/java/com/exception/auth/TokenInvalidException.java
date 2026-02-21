package com.exception.auth;

import com.exception.BusinessException;

public class TokenInvalidException extends BusinessException {
	public TokenInvalidException(String message) {
		super(message);
	}

}
