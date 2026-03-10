package com.techJob.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.techJob.exception.auth.EmailAlreadyExistsException;
import com.techJob.exception.auth.UsernameAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

	
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	
	
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public String handleUsernameExists(UsernameAlreadyExistsException ex,
                                       BindingResult bindingResult) {
        bindingResult.rejectValue("username", "username.exists", ex.getMessage());
        logger.error("Username already exists: {}", ex.getMessage());
        return "auth/register";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailExists(EmailAlreadyExistsException ex,
                                    BindingResult bindingResult) {
        bindingResult.rejectValue("email", "email.exists", ex.getMessage());
        logger.error("Email already exists: {}", ex.getMessage());
        return "auth/register";
    }
}