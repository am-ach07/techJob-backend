package com.techJob.exception.emailVerification;

import com.techJob.exception.BusinessException;

public class EmailSendingException extends BusinessException {
	public EmailSendingException(String message) {
		super(message);
	}

}
