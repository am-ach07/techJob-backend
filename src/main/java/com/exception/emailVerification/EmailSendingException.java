package com.exception.emailVerification;

import com.exception.BusinessException;

public class EmailSendingException extends BusinessException {
	public EmailSendingException(String message) {
		super(message);
	}

}
