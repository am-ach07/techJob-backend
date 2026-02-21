package com.exception.user;

import com.exception.BusinessException;

public class PasswordAreSameOldOne extends BusinessException {

	public PasswordAreSameOldOne() {
		super("Password Can not same old one!! ");
		// TODO Auto-generated constructor stub
	}

}
