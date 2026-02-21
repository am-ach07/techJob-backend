package com.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class InvalidLoginCredentialsException extends AuthenticationException {
    public InvalidLoginCredentialsException(String msg) {
        super(msg);
    }

}
